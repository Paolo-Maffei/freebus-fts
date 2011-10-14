package org.freebus.fts.service.job.device;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Mask;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.ProgramService;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceObject;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.service.ProjectController;
import org.freebus.fts.service.devicecontroller.AssociationTableEntry;
import org.freebus.fts.service.exception.JobFailedException;
import org.freebus.fts.service.internal.I18n;
import org.freebus.fts.service.job.ListenableJob;
import org.freebus.fts.service.job.entity.DeviceInfo;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.MemoryConnection;
import org.freebus.knxcomm.application.MemoryRead;
import org.freebus.knxcomm.application.MemoryResponse;
import org.freebus.knxcomm.telegram.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A job that downloads details about a specific physical device and populates a
 * device object with the data.
 */
public class DeviceDownloadJob extends ListenableJob
{
   private static final Logger LOGGER = LoggerFactory.getLogger(DeviceDownloadJob.class);

//   private boolean createMissingGroups = true;

   private final String label;
   private final DeviceInfo info;
   private Device device;
   private Program program;
   private Mask mask;

   /**
    * Create a device download job.
    * 
    * @param addr - the physical address of the device to download.
    */
   public DeviceDownloadJob(PhysicalAddress addr)
   {
      this(new DeviceInfo(addr));
   }

   /**
    * Create a device download job.
    * 
    * @param info - the device info with at least the physical address set.
    */
   public DeviceDownloadJob(DeviceInfo info)
   {
      this.info = info;
      label = I18n.formatMessage("DeviceDownloadJob.Label", info.getAddress().toString());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getLabel()
   {
      return label;
   }

   /**
    * @return The device-information.
    */
   public DeviceInfo getDeviceInfo()
   {
      return info;
   }

   /**
    * @return The physical address of the device to download.
    */
   public PhysicalAddress getAddress()
   {
      return info.getAddress();
   }

   /**
    * Create a {@link Device device} from the previously read {@link DeviceInfo
    * device-info}.
    * 
    * @throws JobFailedException if the device could not be created.
    */
   private void createDevice() throws JobFailedException
   {
      final ProductsFactory prodFactory = ProductsManager.getFactory();

      final int manufacturerId = info.getManufacturerId();
      final Manufacturer manufacturer = prodFactory.getManufacturerService().getManufacturer(manufacturerId);
      if (manufacturer == null)
      {
         throw new JobFailedException("Manufacturer with id #" + manufacturerId + " not found in database");
      }

      final ProgramService programService = prodFactory.getProgramService();
      final List<Program> programs = programService.findProgram(manufacturer, info.getDeviceType());
      if (programs.size() < 1)
      {
         throw new JobFailedException("No application program found in database with id $" + info.getDeviceType()
               + " from manufacturer " + manufacturer);
      }
      if (programs.size() > 1)
      {
         throw new JobFailedException("Multiple application programs found in database with id $"
               + info.getDeviceType() + " from manufacturer " + manufacturer);
      }

      program = programs.get(0);
      mask = program.getMask();

      final List<VirtualDevice> virtDevices = prodFactory.getVirtualDeviceService().findVirtualDevices(program);
      if (virtDevices.size() > 1)
      {
         LOGGER.warn("Virtual device for application program is not unique, using first matching");
      }
      final VirtualDevice virtDevice = virtDevices.get(0);

      device = new Device(virtDevice);
   }

   /**
    * Read the {@link DeviceInfo device-info} data from the device.
    * 
    * @param con - the data connection to use for download.
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   private void readDeviceInfo(final DataConnection con) throws IOException, TimeoutException
   {
      if (info.getDescriptor() == null)
      {
         info.setDescriptor(con.getDeviceDescriptor0());
      }

      if (info.getManufacturerId() == -1 || info.getDeviceType() == -1)
      {
         final MemoryResponse reply = (MemoryResponse) con.query(new MemoryRead(0x104, 4));
         final byte[] data = reply.getData();

         info.setManufacturerId(data[0] & 255);
         info.setDeviceType(((data[1] & 255) << 8) | (data[2] & 255));

         msleep(100);
      }
   }

   /**
    * Download the group addresses.
    * 
    * @param memCon - the memory connection to use for download.
    * 
    * @return The group addresses of the device.
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   GroupAddress[] downloadAddrTab(final MemoryConnection memCon) throws IOException, TimeoutException
   {
      final int addr = mask.getAddressTabAddress();
      byte[] data;

      data = memCon.read(addr, 1);
      final int num = (data[0] & 255) - 1; // skip first address, it's the device's physical address
      final GroupAddress[] result = new GroupAddress[num];
      LOGGER.debug("%%% Device has " + num + " group addresses:");

      if (num > 0)
      {
         data = memCon.read(addr + 3, num << 1);
         
         for (int i = 0, offs = 0; i < num; ++i, offs += 2)
         {
            final GroupAddress groupAddr = new GroupAddress((data[offs] << 8) | data[offs + 1]);
            result[i] = groupAddr;
            LOGGER.debug("%%% Group address: " + groupAddr);
         }
      }

      return result;
   }

   /**
    * Download the communication objects.
    * 
    * @param memCon - the memory connection to use for download.
    * 
    * @return The communication objects of the device.
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   ObjectDescriptor[] downloadCommObjsTab(final MemoryConnection memCon) throws IOException, TimeoutException
   {
      final int addr = device.getProgram().getCommsTabAddr();

      byte[] data = memCon.read(addr, 1);
      final int num = data[0] & 255;
      final ObjectDescriptor[] result = new ObjectDescriptor[num];
      LOGGER.debug("%%% Device has " + num + " communication objects:");

      if (num > 0)
      {
         data = memCon.read(addr + 1, num * 3);
         for (int i = 0, offs = 0; i < num; ++i, offs += 3)
         {
            final ObjectDescriptor objDesc = new ObjectDescriptor();
            objDesc.fromByteArray(data, offs);
            result[i] = objDesc;
   
            LOGGER.debug("%%% Communication object: " + objDesc);
         }
      }

      return result;
   }
   
   /**
    * Download the association table.
    * 
    * @param memCon - the memory connection to use for download.
    * 
    * @return The association table of the device.
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   AssociationTableEntry[] downloadAssocTab(final MemoryConnection memCon) throws IOException, TimeoutException
   {
      final int ptrAddr = mask.getAssocTabPtrAddress();
      byte[] data = memCon.read(ptrAddr, 1);
      final int addr = (data[0] & 255) + 0x100;

      data = memCon.read(addr, 1);
      int num = data[0] & 255;
      final AssociationTableEntry[] result = new AssociationTableEntry[num];
      LOGGER.debug("%%% Device has " + num + " association table entries:");

      if (num > 0)
      {
         data = memCon.read(addr + 1, num << 1);
         for (int i = 0, offs = 0; i < num; ++i, offs += 2)
         {
            final AssociationTableEntry assoc = new AssociationTableEntry(data[offs] & 255, data[offs + 1] & 255);
            result[i] = assoc;
   
            LOGGER.debug("%%% Association: " + assoc);
         }
      }

      return result;
   }

   /**
    * Add the communication tables to the device. Creates missing groups in the current
    * {@link Project project}.
    * 
    * @param addrs - the group addresses
    * @param objDescs - the object descriptors 
    * @param assocs - the associations to set
    */
   void addComTabsToDevice(final GroupAddress[] addrs, ObjectDescriptor[] objDescs, AssociationTableEntry[] assocs)
   {
      final ProjectController controller = ProjectManager.getController();

      for (final GroupAddress addr : addrs)
      {
         final SubGroup subGroup = controller.findOrCreateSubGroup(addr);
      }

      final List<DeviceObject> devObjs = device.getDeviceObjects();
      devObjs.clear();

      for (final ObjectDescriptor objDesc: objDescs)
      {
         final DeviceObject devObj = new DeviceObject();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void main(BusInterface bus) throws IOException, TimeoutException, JobFailedException
   {
      final DataConnection con = bus.connect(info.getAddress(), Priority.SYSTEM);
      final MemoryConnection memCon = new MemoryConnection(con);
      msleep(50);

      readDeviceInfo(con);
      createDevice();

      final GroupAddress[] groupAddrs = downloadAddrTab(memCon);
      final ObjectDescriptor[] objDescs = downloadCommObjsTab(memCon);
      final AssociationTableEntry[] assocs = downloadAssocTab(memCon);

      addComTabsToDevice(groupAddrs, objDescs, assocs);

      con.close();
   }
}
