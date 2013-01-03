package org.freebus.fts.project.integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.Validate;
import org.freebus.fts.common.internal.I18n;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.integration.generated.AreaType;
import org.freebus.fts.project.integration.generated.DeviceType;
import org.freebus.fts.project.integration.generated.LineType;
import org.freebus.fts.project.integration.generated.ProjectType;
import org.freebus.fts.project.integration.generated.VirtualDeviceRef;
import org.xml.sax.SAXException;

/**
 * An importer that creates a project from an XML project export file or stream.
 */
public class ProjectImporter
{
   private final ProductsFactory productsFactory;

   /**
    * Create a project importer that uses the {@link ProductsManager#getFactory()
    * default products factory}.
    */
   public ProjectImporter()
   {
      productsFactory = ProductsManager.getFactory();
   }

   /**
    * Create a project importer that uses a specific products factory.
    * 
    * @param productsFactory - the products factory to use for imports.
    */
   public ProjectImporter(ProductsFactory productsFactory)
   {
      Validate.notNull(productsFactory);
      this.productsFactory = productsFactory;
   }

   /**
    * @return The products factory that the importer uses.
    */
   public ProductsFactory getProductsFactory()
   {
      return productsFactory;
   }

   /**
    * Read the file and create the project it contains.
    * 
    * @param file - the file to read.
    * 
    * @return The created project.
    * @throws FileNotFoundException if the file does not exist
    */
   public Project readProject(File file) throws FileNotFoundException
   {
      return readProject(new FileInputStream(file));
   }

   /**
    * Read the input stream and create the project it contains.
    * 
    * @param stream - the stream to read.
    * 
    * @return The created project.
    */
   public Project readProject(InputStream stream)
   {
      try
      {
         final JAXBContext context = JAXBContext.newInstance("org.freebus.fts.project.integration.generated");

         final URL schemaUrl = getClass().getClassLoader().getResource("project.xsd");
         if (schemaUrl == null)
            throw new RuntimeException("Schema file not found in class path: " + "project.xsd");

         final Unmarshaller unmarshaller = context.createUnmarshaller();
         unmarshaller.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaUrl));

         return createProject((ProjectType) ((JAXBElement<?>) unmarshaller.unmarshal(stream)).getValue());
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
      catch (SAXException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Create a {@link Project project} from the XML project.
    * 
    * @param xmlProject - the XML project object to process
    * @return The created project.
    */
   private Project createProject(ProjectType xmlProject)
   {
      final Project project = new Project();
      project.setName(xmlProject.getName());
      project.setDescription(xmlProject.getDescription());

      if (xmlProject.getAreas() != null)
      {
         for (final AreaType xmlArea : xmlProject.getAreas().getArea())
         {
            project.add(createArea(xmlArea));
         }
      }

      return project;
   }

   /**
    * Create an {@link Area area} from the XML area.
    * 
    * @param xmlArea - the XML area object to process
    * @return The created area.
    */
   private Area createArea(AreaType xmlArea)
   {
      final Area area = new Area();
      area.setName(xmlArea.getName());
      area.setAddress(xmlArea.getAddress());

      if (xmlArea.getLine() != null)
      {
         for (final LineType xmlLine : xmlArea.getLine())
         {
            area.add(createLine(xmlLine));
         }
      }

      return area;
   }

   /**
    * Create a {@link Line line} from the XML line.
    * 
    * @param xmlLine - the XML line object to process
    * @return The created line.
    */
   private Line createLine(LineType xmlLine)
   {
      final Line line = new Line();
      line.setName(xmlLine.getName());
      line.setAddress(xmlLine.getAddress());

      if (xmlLine.getDevice() != null)
      {
         for (final DeviceType xmlDevice : xmlLine.getDevice())
         {
            line.add(createDevice(xmlDevice));
         }
      }

      return line;
   }

   /**
    * Create a {@link Device device} from the XML device.
    * 
    * @param xmlDevice - the XML device object to process
    * @return The created device.
    */
   private Device createDevice(DeviceType xmlDevice)
   {
      final ManufacturerService manuService = productsFactory.getManufacturerService();

      final VirtualDeviceRef xmlVirtDevice = xmlDevice.getVirtualDevice();
      final Manufacturer virtDeviceManu = manuService.getManufacturer(xmlVirtDevice.getManufacturer());
      Validate.notNull(virtDeviceManu, I18n.formatMessage("ProjectImporter.ErrNoManufacturer", xmlVirtDevice.getName()));

      final Device device = new Device();
      device.setName(xmlDevice.getName());
      device.setAddress(xmlDevice.getAddress());

      return device;
   }
}
