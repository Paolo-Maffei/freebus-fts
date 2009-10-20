package org.freebus.fts.vdx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.freebus.fts.Config;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Product;
import org.freebus.fts.products.ProductDb;
import org.freebus.fts.products.VirtualDevice;

/**
 * Helper class that loads a .vdx file into a {@link ProductDb}
 * product-database object.
 */
public class VdxLoader
{
   private ProductDb db;
   private BufferedReader in;
   private String fileName, currentLine, nextLine;
   private String preferedLanguage = "German";
   private int lineNo, languageId, loadUnusedData = 0;
   private VdxSectionType endLoadAfter = VdxSectionType.UNKNOWN;
   private long filePos;
   private final Map<Integer,VdxSection> sections = new TreeMap<Integer,VdxSection>();
   private final Map<Integer,Integer> productIdToDb = new HashMap<Integer,Integer>();
   private final Map<Integer,Integer> catalogEntryIdToDb = new HashMap<Integer,Integer>();
   private final Map<Integer,String> symbolNames = new HashMap<Integer,String>();
   private WeakReference<VdxProgress> progress = null;

   /**
    * Returns the product-database.
    */
   public ProductDb getProductDb()
   {
      return db;
   }

   /**
    * @return the sections that {@link #load} loaded.
    */
   public Map<Integer,VdxSection> getSections()
   {
      return sections;
   }

   /**
    * @param progress the progress to set
    */
   public void setProgress(VdxProgress progress)
   {
      this.progress = new WeakReference<VdxProgress>(progress);
   }

   /**
    * @return the number of data-blocks that shall be loaded from unused sections.
    */
   public int getLoadUnusedData()
   {
      return loadUnusedData;
   }

   /**
    * Set the number of data-blocks that shall be loaded from unused sections.
    */
   public void setLoadUnusedData(int loadUnusedData)
   {
      this.loadUnusedData = loadUnusedData;
   }

   /**
    * @return the section after which loading stops.
    */
   public VdxSectionType getEndLoadAfter()
   {
      return endLoadAfter;
   }

   /**
    * Set the section after which loading stops.
    */
   public void setEndLoadAfter(VdxSectionType endLoadAfter)
   {
      this.endLoadAfter = endLoadAfter;
   }

   /**
    * Load the .vdx file fileName.
    *
    * A new internal product-database is created. 
    * @throws Exception
    */
   public void load(String fileName) throws Exception
   {
      this.fileName = fileName;
      db = new ProductDb();

      System.out.println("Reading file " + fileName);

      final File file = new File(fileName);
      final FileInputStream fileIn = new FileInputStream(file);

      try
      {
         final InputStreamReader streamIn = new  InputStreamReader(fileIn, "ISO8859-15");
         in = new BufferedReader(streamIn);
   
         filePos = 0;
         lineNo = 0;
         languageId = 0;
         currentLine = null;
         nextLine = null;
   
         sections.clear();
         productIdToDb.clear();
         catalogEntryIdToDb.clear();
         symbolNames.clear();
   
         String ident = readLine();
         if (!ident.equals("EX-IM")) throw new Exception("File format is unknown: " + ident);
         
         if (!readHeader())
         {
            fileIn.close();
            return;
         }
   
         if (progress!=null)
         {
            progress.get().setTotal(file.length());
            progress.get().setProgress(0);
         }
         
         for (int i=0; i<1000 && in.ready(); ++i)
         {
            VdxSection section = readSection();
            if (section==null) break;
   
            final VdxSectionType type = section.getType();
            if (type==VdxSectionType.LANGUAGE) section = selectLanguage(section);
            else if (type==VdxSectionType.SYMBOL) section = processSymbols(section);
            else if (type==VdxSectionType.HW_PRODUCT) section = processProducts(section);
            else if (type==VdxSectionType.CATALOG_ENTRY) section = processCatalogEntries(section);
            else if (type==VdxSectionType.FUNCTIONAL_ENTITY) section = processFunctionalEntities(section);
            else if (type==VdxSectionType.VIRTUAL_DEVICE) section = processVirtualDevice(section);
   
            if (section!=null) sections.put(section.getId(), section);
            if (progress!=null) progress.get().setProgress(filePos);
            if (endLoadAfter!=VdxSectionType.UNKNOWN && section.getType()==endLoadAfter) break;
         }
   
         System.out.println("Done reading file " + fileName);
         if (progress!=null) progress.get().setProgress(file.length());
      }
      catch (Exception e)
      {
         throw new Exception(""+fileName+" line "+Integer.toString(lineNo)+": "+e.getMessage(), e);
      }
      finally
      {
         fileIn.close();
      }
   }

   /**
    * Read the next line from the input file.
    * @throws IOException 
    */
   protected String readLine() throws IOException
   {
      if (nextLine==null)
      {
         currentLine = in.readLine();
      }
      else
      {
         currentLine = nextLine;
         nextLine = null;
      }

      ++lineNo;
      filePos += currentLine.length()+2;
      return currentLine;
   }

   /**
    * Look at the next line of the input file.
    * @throws IOException 
    */
   protected String peekLine() throws IOException
   {
      if (nextLine==null) nextLine = in.readLine();
      return nextLine;
   }

   /**
    * An error occurred.
    */
   protected void parseError(String message)
   {
      throw new RuntimeException(fileName+" line "+lineNo+": "+message+". Line: "+currentLine);
   }
   
   /**
    * Ensure a specific line type.
    */
   protected boolean testLineType(String value, String expect)
   {
      if (!expect.equals(value))
      {
         parseError("Invalid line type. Expected '"+expect+"', found '"+value+"'");
         return false;
      }
      return true;
   }

   /**
    * Read the header section
    * @throws IOException 
    */
   protected boolean readHeader() throws IOException
   {
      char recType;
      String line;

      while (in.ready())
      {
         line = readLine();
         recType = Character.toUpperCase(line.charAt(0));

         switch (recType)
         {
            case '-': // End of section
               return in.ready();
            case 'N': // Filename
               break;
            case 'K': // Comment
               break;
            case 'D': // Creation timestamp
               db.setCreationDate(line.substring(2));
               break;
            case 'V': // Version
               db.setVersion(line.substring(2));
               break;
            case 'H': // "virtual_device" ... whatever
               break;
            default:
               parseError("Invalid record type: " + recType);
               return false;
         }
      }

      return in.ready();
   }

   /**
    * Read the next section
    * @throws IOException 
    */
   protected VdxSection readSection() throws IOException
   {
      String line, sectionName;
      final Vector<String> fieldNames = new Vector<String>();
      String[] arr, values;
      int sectionId, idx, id, numFields;
      int numDataBlocks = -1;

      line = readLine();
      arr = line.split(" ", 3);
      if (!testLineType(arr[0], "T")) return null;

      sectionId = Integer.parseInt(arr[1]);
      sectionName = arr[2];
//      System.out.println("=====\nSection: "+sectionName);
      System.out.println("Section: "+sectionName);

      VdxSectionType type;
      if (!VdxSectionType.isValid(sectionId))
      {
         System.out.println("Info: unknown vdx section #"+Integer.toString(sectionId)+" encountered: " + sectionName);
         type = VdxSectionType.UNKNOWN;
      }
      else type = VdxSectionType.valueOf(sectionId);

      final VdxSection section = new VdxSection(sectionName, sectionId, type);

      //
      // Read field definitions
      //
      for (numFields=0; in.ready(); ++numFields)
      {
         line = readLine();
         if (!line.startsWith("C")) break;
         arr = line.split(" ", 6);
         fieldNames.add(arr[5]);
//         System.out.println("Field: "+arr[5]);
         section.addField(arr[5]);
      }

      if (type.keyField!=null) section.setKeyFieldIdx(section.getFieldIndex(type.keyField, true));
      if (type.nameField!=null) section.setNameFieldIdx(section.getFieldIndex(type.nameField, true));
      final int keyFieldIdx = section.getKeyFieldIdx();
      
      // See if there is a language-id field. If so, only those
      // data block will be used that have the same language-id
      // as we prefer.
      final int langIdIdx = languageId>0 ? section.getFieldIndex("LANGUAGE_ID", false) : -1;

      //
      // Read data blocks
      //
      while (in.ready())
      {
         if (line.startsWith("---------------------")) break;
         arr = line.split(" ", 5);
         if (!testLineType(arr[0], "R")) return null;

         idx = Integer.parseInt(arr[1]);
         id = Integer.parseInt(arr[3]);
         if (id!=sectionId)
         {
            parseError("Invalid section-id, expected "+Integer.toString(sectionId)+", found "+Integer.toString(id));
            return null;
         }

//         System.out.println("-----");
         values = new String[numFields];
         for (int i=0; i<numFields && in.ready(); ++i)
         {
            line = readLine();
            if (in.ready() && peekLine().startsWith("\\\\"))
            {
               final StringBuilder lines = new StringBuilder();
               while (in.ready() && peekLine().startsWith("\\\\"))
                  lines.append(readLine().substring(2));
               line += lines.toString();
            }
            if (line.length()>1) line = line.replaceAll("\\\\\\\\", "\\\\");
            values[i] = line;
         }

         if (langIdIdx<0 || Integer.parseInt(values[langIdIdx])==languageId)
         {
            final int key = keyFieldIdx<0 ? idx : Integer.parseInt(values[keyFieldIdx]);
            if (section.hasElement(key))
            {
               parseError("Section "+section.getName()+" already contains an element with id "+Integer.toString(key));
               return null;
            }

            if (type.load || ++numDataBlocks < loadUnusedData)
            {
               section.addElement(key, values);
            }
            else
            {
               // Skip to end of section
               while (in.ready())
               {
                  if (readLine().startsWith("---------------------")) break;
                  if ((lineNo & 1023)==0 && progress!=null) progress.get().setProgress(filePos);
               }
               break;
            }
         }

         if (progress!=null) progress.get().setProgress(filePos);

         line = readLine();
      }

      return section;
   }

   /**
    * Returns the manufacturer with the given id.
    *
    * The manufacturer is searched in the manufacturer vdx-section and added to the
    * product-database, if it does not exist in the product-database.
    */
   protected Manufacturer getManufacturer(int id)
   {
      Manufacturer manufacturer = db.getManufacturer(id);
      if (manufacturer==null)
      {
         final String name = sections.get(VdxSectionType.MANUFACTURER.id).getValue(id, "MANUFACTURER_NAME");
         manufacturer = new Manufacturer(id, name);
         db.addManufacturer(manufacturer);
      }
      return manufacturer;
   }
   
   /**
    * Get the language-id from the ete_language section
    */
   protected VdxSection selectLanguage(VdxSection section)
   {
      final Set<Integer> ids = section.getElementIds();
      final Iterator<Integer> it = ids.iterator();

      final int langIdIdx = section.getFieldIndex("LANGUAGE_ID", true);
      final int langNameIdx = section.getFieldIndex("LANGUAGE_NAME", true);
      String[] values;

      languageId = 0;

      while (it.hasNext())
      {
         values = section.getElement(it.next());
         if (values[langNameIdx].equals(preferedLanguage))
         {
            languageId = Integer.parseInt(values[langIdIdx]);
            System.out.println("Using language "+preferedLanguage+" (id "+Integer.toString(languageId)+")");
            break;
         }
      }

      return null;
   }

   /**
    * Store the icons and remove the icon data from the section
    * @throws IOException 
    */
   protected VdxSection processSymbols(VdxSection section) throws IOException
   {
      final Set<Integer> ids = section.getElementIds();
      final Iterator<Integer> it = ids.iterator();
      final int fileNameIdx = section.getFieldIndex("SYMBOL_FILENAME", true);
      final int dataIdx = section.getFieldIndex("SYMBOL_DATA", true);
      final int nameIdx = section.getNameFieldIdx();
      final String tempDir = Config.getInstance().getTempDir()+'/';
      String[] values;
      
      while (it.hasNext())
      {
         final int id = it.next();

         values = section.getElement(id);
         String data = values[dataIdx];
         values[dataIdx] = "(removed)";

         String name = values[fileNameIdx].toLowerCase();
         symbolNames.put(id, name);
         
         final File file = new File(tempDir+name+".bmp");
         final FileOutputStream out = new FileOutputStream(file);

         final int dataLen = data.length();
         final byte[] bytes = new byte[dataLen>>1];

         for (int i=0,j=0; i<dataLen; i+=2,++j)
            bytes[j] = (byte)Integer.parseInt(data.substring(i, i+2), 16);

         out.write(bytes);
         out.close();

         System.out.println("Saving symbol \""+values[nameIdx]+"\": "+file.getName());
      }

      return section;
   }

   /**
    * Process the hardware products 
    */
   protected VdxSection processProducts(VdxSection section)
   {
      final Set<Integer> ids = section.getElementIds();
      final Iterator<Integer> it = ids.iterator();
      final int manufacturerIdx = section.getFieldIndex("MANUFACTURER_ID", true);
      final int origManufacturerIdx = section.getFieldIndex("ORIGINAL_MANUFACTURER_ID", false);
      final int productNameIdx = section.getFieldIndex("PRODUCT_NAME", true);
      final int productIdIdx = section.getFieldIndex("PRODUCT_ID", true);
      final int productSerialIdx = section.getFieldIndex("PRODUCT_SERIAL_NUMBER", false);
      final int symbolIdx = section.getFieldIndex("SYMBOL_ID", false);

      int manufacturerId, lastManufacturerId = -1;
      Manufacturer manufacturer = null;
      String val, productName;
      String[] values;

      while (it.hasNext())
      {
         values = section.getElement(it.next());

         manufacturerId = Integer.parseInt(values[manufacturerIdx]);
         if (manufacturerId!=lastManufacturerId)
         {
            lastManufacturerId = manufacturerId;
            manufacturer = getManufacturer(manufacturerId);
         }

         productName = values[productNameIdx];
         if (origManufacturerIdx>=0)
         {
            val = values[origManufacturerIdx];
            if (!val.isEmpty())
            {
               final int origManId = Integer.parseInt(val);
               productName += " ("+sections.get(VdxSectionType.MANUFACTURER.id).getValue(origManId, "MANUFACTURER_NAME")+")";
            }
         }
         
         final Product product = new Product(productName, manufacturer);
         if (productSerialIdx>=0) product.setSerial(values[productSerialIdx]);
         if (symbolIdx>=0)
         {
            val = values[symbolIdx];
            if (!val.isEmpty()) product.setSymbolName(symbolNames.get(Integer.parseInt(val)));
         }

         final int dbId = db.addProduct(product);

         final int productId = Integer.parseInt(values[productIdIdx]);
         productIdToDb.put(productId, dbId);
      }

      return section;
   }

   /**
    * Process the catalog-entries.
    */
   protected VdxSection processCatalogEntries(VdxSection section)
   {
      final Set<Integer> ids = section.getElementIds();
      final Iterator<Integer> it = ids.iterator();
      final int catIdIdx = section.getFieldIndex("CATALOG_ENTRY_ID", true);
      final int nameIdx = section.getFieldIndex("ENTRY_NAME", true);
      final int productIdx = section.getFieldIndex("PRODUCT_ID", true);
      final int manufacturerIdx = section.getFieldIndex("MANUFACTURER_ID", true);
      int manufacturerId, lastManufacturerId = -1;
      Manufacturer manufacturer = null;
      Product product;
      String[] values;

      while (it.hasNext())
      {
         values = section.getElement(it.next());

         manufacturerId = Integer.parseInt(values[manufacturerIdx]);
         if (manufacturerId!=lastManufacturerId)
         {
            lastManufacturerId = manufacturerId;
            manufacturer = getManufacturer(manufacturerId);
         }

         product = db.getProduct(productIdToDb.get(Integer.parseInt(values[productIdx])));
         final CatalogEntry cat = new CatalogEntry(values[nameIdx], manufacturer, product);

         final int dbId = db.addCatalogEntry(cat);

         final int catId = Integer.parseInt(values[catIdIdx]);
         catalogEntryIdToDb.put(catId, dbId);
      }

      return section;
   }

   /**
    * Process the functional entities.
    */
   protected VdxSection processFunctionalEntities(VdxSection section)
   {
      final Set<Integer> ids = section.getElementIds();
      final int entityIdIdx = section.getFieldIndex("FUNCTIONAL_ENTITY_ID", true);
      final int nameIdx = section.getFieldIndex("FUNCTIONAL_ENTITY_NAME", true);
      final int descriptionIdx = section.getFieldIndex("FUNCTIONAL_ENTITY_DESCRIPTION", false);
      final int parentIdIdx = section.getFieldIndex("FUN_FUNCTIONAL_ENTITY_ID", false);
      final int manufacturerIdx = section.getFieldIndex("MANUFACTURER_ID", true);
      int manufacturerId, lastManufacturerId = -1;
      Manufacturer manufacturer = null;
      FunctionalEntity parentEnt;
      String[] values;
      String val, description;

      for (final Iterator<Integer> it = ids.iterator(); it.hasNext(); )
      {
         values = section.getElement(it.next());

         manufacturerId = Integer.parseInt(values[manufacturerIdx]);
         if (manufacturer==null || manufacturerId!=lastManufacturerId)
         {
            manufacturer = getManufacturer(manufacturerId);
            lastManufacturerId = manufacturerId;
         }

         if (descriptionIdx>=0) description = values[descriptionIdx];
         else description = "";

         FunctionalEntity funcEnt = new FunctionalEntity(Integer.parseInt(values[entityIdIdx]), values[nameIdx], description);
         manufacturer.addFunctionalEntity(funcEnt);
      }

      if (parentIdIdx>=0)
      {
         // Set parent functional-entities

         for (final Iterator<Integer> it = ids.iterator(); it.hasNext(); )
         {
            values = section.getElement(it.next());
            final int id = Integer.parseInt(values[entityIdIdx]);
   
            manufacturerId = Integer.parseInt(values[manufacturerIdx]);
            if (manufacturer==null || manufacturerId!=lastManufacturerId)
            {
               manufacturer = getManufacturer(manufacturerId);
               lastManufacturerId = manufacturerId;
            }

            val = values[parentIdIdx];
            if (val.isEmpty()) continue;

            final int parentId = Integer.parseInt(val);
            parentEnt = manufacturer.getFunctionalEntity(parentId);
            if (parentEnt==null) parseError("In functional-entity #"+Integer.toString(id)+": parent #"+parentId+" not found");

            manufacturer.getFunctionalEntity(id).setParent(parentEnt);
         }
      }

      return section;
   }

   /**
    * Process the virtual devices.
    */
   protected VdxSection processVirtualDevice(VdxSection section)
   {
      final int deviceIdIdx = section.getFieldIndex("VIRTUAL_DEVICE_ID", true);
      final int nameIdx = section.getFieldIndex("VIRTUAL_DEVICE_NAME", true);
      final int descriptionIdx = section.getFieldIndex("VIRTUAL_DEVICE_DESCRIPTION", false);
      final int catalogIdx = section.getFieldIndex("CATALOG_ENTRY_ID", true);
      final int functionalIdx = section.getFieldIndex("FUNCTIONAL_ENTITY_ID", true);

      VirtualDevice virtDev;
      FunctionalEntity funcEnt;
      CatalogEntry catEnt;
      String[] values;
      int id;

      final Set<Integer> ids = section.getElementIds();
      for (final Iterator<Integer> it = ids.iterator(); it.hasNext(); )
      {
         values = section.getElement(it.next());

         id = Integer.parseInt(values[catalogIdx]);
         catEnt = db.getCatalogEntry(catalogEntryIdToDb.get(id));
         if (catEnt==null) parseError("Unknown catalog-entry #"+id);

         id = Integer.parseInt(values[functionalIdx]);
         funcEnt = catEnt.getManufacturer().getFunctionalEntity(id);
         if (funcEnt==null) parseError("Unknown functional-entity #"+id);

         id = Integer.parseInt(values[deviceIdIdx]);
         virtDev = new VirtualDevice(id, values[nameIdx], values[descriptionIdx], funcEnt);
         catEnt.addVirtualDevice(virtDev);
      }

      return section;
   }
}
