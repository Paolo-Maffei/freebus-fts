package org.freebus.fts.products.importer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.freebus.fts.products.BcuType;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Mask;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterType;
import org.freebus.fts.products.ParameterValue;
import org.freebus.fts.products.Product;
import org.freebus.fts.products.ProductDescription;
import org.freebus.fts.products.ProductsImporter;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.ProgramDescription;
import org.freebus.fts.products.S19Block;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.BcuTypeService;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ProductDescriptionService;
import org.freebus.fts.products.services.ProductService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.ProgramDescriptionService;
import org.freebus.fts.products.services.ProgramService;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * A products importer that remaps IDs to be unique.
 */
public final class RemappingProductsImporter implements ProductsImporter
{
   private final ProductsImporterContext ctx;
   private final Map<String, CatalogEntry> knownCatEntries = new HashMap<String, CatalogEntry>();
   private final Map<CatalogEntry, ProductDescription> prodDescsToStore = new HashMap<CatalogEntry, ProductDescription>();
   private final List<ProgramDescription> progDescsToStore = new LinkedList<ProgramDescription>();
   private final Logger logger = Logger.getLogger(getClass());

   /**
    * Create a products importer that remaps IDs to be unique during import.
    * 
    * @param sourceFactory
    * @param destFactory
    */
   public RemappingProductsImporter(ProductsFactory sourceFactory, ProductsFactory destFactory)
   {
      ctx = new ProductsImporterContext(sourceFactory, destFactory);
   }

   /**
    * Create a fingerprint for the given functional entity.
    */
   public String getFingerPrint(FunctionalEntity funcEntity)
   {
      final StringBuilder sb = new StringBuilder();

      while (funcEntity != null)
      {
         sb.append(funcEntity.getNumber() == null ? "--" : funcEntity.getNumber());
         sb.append(':').append(funcEntity.getManufacturer().getId());
         funcEntity = funcEntity.getParent();
         
         if (funcEntity != null)
            sb.append('/');
      }

      return sb.toString();
   }

   /**
    * Create an alternative fingerprint for the given functional entity.
    */
   public String getAltFingerPrint(FunctionalEntity funcEntity)
   {
      return funcEntity.getManufacturer().getId() + ":" + funcEntity.getName();
   }

   /**
    * Create a fingerprint for the given catalog entry.
    */
   public String getFingerPrint(CatalogEntry catEntry)
   {
      return catEntry.getManufacturer().getId() + ":" + catEntry.getName();
   }

   /**
    * Create a fingerprint for the given virtual device.
    */
   public String getFingerPrint(VirtualDevice device)
   {
      return device.getCatalogEntry().getManufacturer().getId() + ":" + device.getProductTypeId() + ":"
            + device.getName();
   }

   /**
    * Create a fingerprint for the given product.
    */
   public String getFingerPrint(Product product)
   {
      return product.getManufacturer().getId() + ":" + product.getName();
   }

   /**
    * Create a fingerprint for the given application program.
    */
   public String getFingerPrint(Program program)
   {
      return program.getManufacturer().getId() + ":" + program.getDeviceType() + ":" + program.getName();
   }

   /**
    * Process all manufacturers of the devices and all other objects that are
    * connected to one of the devices. The manufacturers are collected and added
    * to the {@link ProductsImporterContext context} where they are added to the
    * destination persistence context.
    */
   public void copyManufacturers(final List<VirtualDevice> devices)
   {
      for (final VirtualDevice device : devices)
      {
         FunctionalEntity funcEntity = device.getFunctionalEntity();
         while (funcEntity != null)
         {
            ctx.add(funcEntity.getManufacturer());
            funcEntity = funcEntity.getParent();
         }

         final CatalogEntry catEntry = device.getCatalogEntry();
         if (catEntry != null)
         {
            ctx.add(catEntry.getManufacturer());

            final Product product = catEntry.getProduct();
            if (product != null)
               ctx.add(product.getManufacturer());
         }

         final Program program = device.getProgram();
         if (program != null)
            ctx.add(program.getManufacturer());
      }
   }

   /**
    * Process a specific functional entity, using the map of functional entities
    * that are already in the destination database.
    */
   private FunctionalEntity processFunctionalEntity(FunctionalEntity funcEntity,
         final Map<String, FunctionalEntity> knownFuncEntities)
   {
      final String fingerPrint = getFingerPrint(funcEntity);
      FunctionalEntity knownFuncEntity = knownFuncEntities.get(fingerPrint);
      if (knownFuncEntity != null)
         return knownFuncEntity;

      final String altFingerPrint = getAltFingerPrint(funcEntity);
      knownFuncEntity = knownFuncEntities.get(altFingerPrint);
      if (knownFuncEntity != null)
         return knownFuncEntity;

      logger.info("New functional entity: " + funcEntity);
      funcEntity.setId(0);
      funcEntity.setManufacturer(ctx.getManufacturer(funcEntity.getManufacturer().getId()));

      FunctionalEntity parentFuncEntity = funcEntity.getParent();
      if (parentFuncEntity != null)
      {
         parentFuncEntity = processFunctionalEntity(parentFuncEntity, knownFuncEntities);
         funcEntity.setParent(parentFuncEntity);
      }

      ctx.destFactory.getFunctionalEntityService().persist(funcEntity);
      knownFuncEntities.put(fingerPrint, funcEntity);
      knownFuncEntities.put(altFingerPrint, funcEntity);

      return funcEntity;
   }

   /**
    * Process all functional entities.
    */
   public void copyFunctionalEntities(final List<VirtualDevice> devices)
   {
      final FunctionalEntityService funcEntService = ctx.destFactory.getFunctionalEntityService();
      final Map<String, FunctionalEntity> knownFuncEntities = new HashMap<String, FunctionalEntity>();

      // Load all functional entities from the destination factory, remember
      // those of a manufacturer that is used, and store them in
      // knownFuncEntities with a fingerprint as key and once with the
      // manufacturer+name as key.
      for (final FunctionalEntity funcEntity : funcEntService.getFunctionalEntities())
      {
         if (ctx.getManufacturer(funcEntity.getManufacturer().getId()) != null)
         {
            knownFuncEntities.put(getFingerPrint(funcEntity), funcEntity);
            knownFuncEntities.put(getAltFingerPrint(funcEntity), funcEntity);
         }
      }

      // Lookup the functional entities of the virtual devices and replace
      // them with functional entities of the destination database.
      for (final VirtualDevice device : devices)
      {
         FunctionalEntity funcEntity = processFunctionalEntity(device.getFunctionalEntity(), knownFuncEntities);
         device.setFunctionalEntity(funcEntity);
      }
   }

   /**
    * Process all catalog entries.
    */
   public void copyCatalogEntries(final List<VirtualDevice> devices)
   {
      final CatalogEntryService catEntryService = ctx.destFactory.getCatalogEntryService();
      final ProductDescriptionService srcProdDescService = ctx.sourceFactory.getProductDescriptionService();
      knownCatEntries.clear();

      // Load all catalog entries from the destination factory, remember
      // those of a manufacturer that is used, and store them in
      // knownCatEntries.
      for (final CatalogEntry catEntry : catEntryService.getCatalogEntries())
      {
         final int manuId = catEntry.getManufacturer().getId();
         if (ctx.getManufacturer(manuId) != null)
         {
            knownCatEntries.put(getFingerPrint(catEntry), catEntry);
         }
      }

      // Lookup the catalog entries of the virtual devices and replace
      // them with catalog entries of the destination database. Also update the
      // catalog entry objects of the virtual devices to instances of the
      // destination database.
      for (final VirtualDevice device : devices)
      {
         final CatalogEntry catEntry = device.getCatalogEntry();
         if (catEntry == null)
            continue;

         final String fingerPrint = getFingerPrint(catEntry);
         CatalogEntry knownCatEntry = knownCatEntries.get(fingerPrint);
         if (knownCatEntry == null)
         {
            logger.info("New catalog entry: " + catEntry);

            try
            {
               // Product description lines can only be stored when the catalog
               // entry has it's new id.
               prodDescsToStore.put(catEntry, srcProdDescService.getProductDescription(catEntry));
            }
            catch (DAOException e)
            {
               e.printStackTrace();
            }

            catEntry.setId(0);
            catEntry.setManufacturer(ctx.getManufacturer(catEntry.getManufacturer().getId()));
            catEntryService.persist(catEntry);
            knownCatEntries.put(fingerPrint, catEntry);
         }
         else
         {
            device.setCatalogEntry(knownCatEntry);
         }
      }
   }

   /**
    * Process all hardware products.
    */
   public void copyProducts(final List<VirtualDevice> devices)
   {
      final ProductService productService = ctx.destFactory.getProductService();
      final Map<String, Product> knownProducts = new HashMap<String, Product>();

      // Load all products from the destination factory, remember
      // those of a manufacturer that is used, and store them in
      // knownCatEntries.
      for (final Product product : productService.getProducts())
      {
         final int manuId = product.getManufacturer().getId();
         if (ctx.getManufacturer(manuId) != null)
         {
            knownProducts.put(getFingerPrint(product), product);
         }
      }

      // Lookup the products of the virtual devices' catalog entries and replace
      // them with products of the destination database. Also update the
      // products of the virtual devices to instances of the destination
      // database.
      for (final VirtualDevice device : devices)
      {
         final CatalogEntry catEntry = device.getCatalogEntry();
         if (catEntry == null)
            continue;

         Product product = catEntry.getProduct();
         if (product == null)
            continue;

         final String fingerPrint = getFingerPrint(product);
         Product knownProduct = knownProducts.get(fingerPrint);
         if (knownProduct == null)
         {
            logger.info("New hardware product: " + product);

            product.setId(0);
            product.setManufacturer(ctx.getManufacturer(catEntry.getManufacturer().getId()));
            productService.persist(product);
            knownProducts.put(fingerPrint, product);
         }
         else if (product.getVersion() > knownProduct.getVersion())
         {
            logger.info("Updated hardware product: " + product);

            product.setId(knownProduct.getId());
            product.setManufacturer(ctx.getManufacturer(catEntry.getManufacturer().getId()));
            product = productService.merge(product);
            catEntry.setProduct(product);
            knownProducts.put(fingerPrint, product);
         }
         else
         {
            catEntry.setProduct(knownProduct);
         }
      }
   }

   /**
    * Persist a application program.
    */
   public void persist(Program prog)
   {
      try
      {
         final ProgramDescriptionService srcProgDescService = ctx.sourceFactory.getProgramDescriptionService();
         final ProgramDescription desc = srcProgDescService.getProgramDescription(prog);
         
         if (desc != null)
         {
            desc.setProgram(prog);
            progDescsToStore.add(desc);
            
         }
      }
      catch (DAOException e)
      {
         e.printStackTrace();
      }

      prog.setId(0);
      prog.setDescription(null);
      prog.setManufacturer(ctx.getManufacturer(prog.getManufacturer().getId()));
      final Set<ParameterType> paramTypes = prog.getParameterTypes();
      paramTypes.clear();

      for (final Parameter param : prog.getParameters())
      {
         param.setId(0);

         final ParameterType paramType = param.getParameterType();
         paramTypes.add(paramType);
         paramType.setId(0);

         final Set<ParameterValue> values = paramType.getValues();
         if (values != null)
         {
            for (ParameterValue value : values)
               value.setId(0);
         }
      }

      for (final CommunicationObject comObject : prog.getCommunicationObjects())
         comObject.setId(0);

      ctx.destFactory.getProgramService().persist(prog);
   }

   /**
    * Process the application programs.
    */
   public void copyPrograms(List<VirtualDevice> devices)
   {
      final ProgramService programService = ctx.destFactory.getProgramService();
      final Map<String, Program> knownPrograms = new HashMap<String, Program>();

      // Load all application programs from the destination factory, remember
      // those of the manufacturers that are used, and store them in
      // knownPrograms.
      for (final Program prog : programService.getPrograms())
      {
         final int manuId = prog.getManufacturer().getId();
         if (ctx.getManufacturer(manuId) != null)
            knownPrograms.put(getFingerPrint(prog), prog);
      }

      for (final VirtualDevice device : devices)
      {
         final Program prog = device.getProgram();
         if (prog == null)
            continue;

         prog.getMask().setId(0);

         final String fingerPrint = getFingerPrint(prog);
         final Program knownProg = knownPrograms.get(fingerPrint);

         if (knownProg == null)
         {
            logger.info("New program: " + prog);

            for (final S19Block block : prog.getS19Blocks())
               block.setId(0);

            persist(prog);
         }
         else if (prog.getVersion().compareToIgnoreCase(knownProg.getVersion()) > 0)
         {
            logger.info("Newer version of program: " + prog);
            persist(prog);
         }
         else
         {
            device.setProgram(knownProg);
         }
      }
   }

   /**
    * Import the virtual devices.
    * 
    * @param devices
    */
   public void copyVirtualDevices(List<VirtualDevice> devices)
   {
      final Map<String, VirtualDevice> knownDevices = new HashMap<String, VirtualDevice>();
      final VirtualDeviceService virtualDeviceService = ctx.destFactory.getVirtualDeviceService();

      for (final VirtualDevice device : virtualDeviceService.getVirtualDevices())
      {
         if (knownCatEntries.containsKey(getFingerPrint(device.getCatalogEntry())))
            knownDevices.put(getFingerPrint(device), device);
      }

      for (VirtualDevice device : devices)
      {
         final String fingerPrint = getFingerPrint(device);
         VirtualDevice knownDevice = knownDevices.get(fingerPrint);

         if (knownDevice == null)
         {
            logger.info("New device: " + device);

            device.setId(0);
            virtualDeviceService.persist(device);
            knownDevices.put(fingerPrint, device);
         }
         else if (device.getNumber() > knownDevice.getNumber())
         {
            logger.info("Updated device: " + device);

            device.setId(knownDevice.getId());
            device = virtualDeviceService.merge(device);
            knownDevices.put(fingerPrint, device);
         }
         else
         {
            logger.info("Have same/newer device: " + device);
            continue;

         }
      }
   }

   /**
    * Import all BCU types of the {@link Product hardware products} and
    * {@link Program application programs} of the virtual devices.
    * 
    * @param devices - the virtual devices to process.
    */
   public void copyBcuTypes(List<VirtualDevice> devices)
   {
      final BcuTypeService bcuTypeService = ctx.destFactory.getBcuTypeService();

      for (final VirtualDevice device : devices)
      {
         final Product product = device.getCatalogEntry().getProduct();
         BcuType bcuType = product.getBcuType();
         if (bcuType != null)
         {
            final BcuType bt = bcuTypeService.getBcuType(bcuType.getId());
            if (bt == null) bcuTypeService.persist(bcuType);
            else product.setBcuType(bt);
         }

         final Program program = device.getProgram();
         final Mask mask = program == null ? null : program.getMask();
         bcuType = mask == null ? null : mask.getBcuType();
         if (bcuType != null)
         {
            final BcuType bt = bcuTypeService.getBcuType(bcuType.getId());
            if (bt == null) bcuTypeService.persist(bcuType);
            else mask.setBcuType(bt);
         }
      }
   }

   /**
    * Persist the collected product descriptions.
    */
   public void persistProductDescriptions()
   {
      final ProductDescriptionService prodDescService = ctx.destFactory.getProductDescriptionService();

      for (Entry<CatalogEntry, ProductDescription> e : prodDescsToStore.entrySet())
      {
         final CatalogEntry catEntry = e.getKey();
         final ProductDescription desc = e.getValue();

         catEntry.setDescription(desc);
         prodDescService.persist(desc);
      }
   }

   /**
    * Persist the collected program descriptions.
    */
   public void persistProgramDescriptions()
   {
      final ProgramDescriptionService progDescService = ctx.destFactory.getProgramDescriptionService();

      for (final ProgramDescription desc : progDescsToStore)
      {
         desc.getProgram().setDescription(desc);
         progDescService.persist(desc);
      }
   }

   /**
    * Delete orphaned objects. Delete all devices that were replaced with newer
    * versions and are not used by any project.
    */
   public void cleanup()
   {
      int num;

      logger.info("Cleaning up database");

      // TODO
      //
      // Things to cleanup here:
      //
      // - (Application) programs that no virtual device or device references
      // - Orphaned ProductDescription and ProgramDescription entries

      // Cleanup (Hardware) products that no catalog entry references 
      num = ctx.destFactory.getProductService().removeOrphanedProducts();
      logger.info("Cleanup: " + num + " orphaned hardware products deleted");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void copy(List<VirtualDevice> devices)
   {
      final EntityTransaction transaction = ctx.destFactory.getTransaction();
      final boolean ownTransaction = !transaction.isActive();

      ctx.clear();
      prodDescsToStore.clear();
      progDescsToStore.clear();

      try
      {
         if (ownTransaction)
            transaction.begin();

         copyBcuTypes(devices);
         copyManufacturers(devices);
         copyFunctionalEntities(devices);
         copyProducts(devices);
         copyCatalogEntries(devices);
         copyPrograms(devices);
         copyVirtualDevices(devices);

         ctx.destFactory.flushEntityManager();
         if (ownTransaction)
         {
            transaction.commit();
            transaction.begin();
         }

         persistProductDescriptions();
         persistProgramDescriptions();
         cleanup();

         if (ownTransaction)
            transaction.commit();
      }
      finally
      {
         if (ownTransaction && transaction.isActive())
            transaction.rollback();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProductsFactory getSourceFactory()
   {
      return ctx.sourceFactory;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProductsFactory getDestFactory()
   {
      return ctx.destFactory;
   }
}
