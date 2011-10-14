package org.freebus.fts.service.job.device;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.freebus.fts.common.ByteUtils;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.products.Mask;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceProgramming;
import org.freebus.fts.service.devicecontroller.AssociationTableEntry;
import org.freebus.fts.service.devicecontroller.DeviceController;
import org.freebus.fts.service.devicecontroller.DeviceProgrammerType;
import org.freebus.fts.service.exception.JobFailedException;
import org.freebus.fts.service.internal.I18n;
import org.freebus.fts.service.job.ListenableJob;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.MemoryConnection;
import org.freebus.knxcomm.MemoryConnectionInterface;
import org.freebus.knxcomm.application.memory.MemoryLocation;
import org.freebus.knxcomm.telegram.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A device programmer job that using BCU1 programming techniques.
 */
public class Bcu1ProgrammerJob extends ListenableJob implements DeviceProgrammerJob
{
   private static final Logger LOGGER = LoggerFactory.getLogger(Bcu1ProgrammerJob.class);

   private final Set<DeviceProgrammerType> types = new HashSet<DeviceProgrammerType>();
   private boolean physicalAddressJobQueued;
   private final DeviceController controller;
   private final Device device;
   private final String label;

   /**
    * Create a BCU-1 device programmer job.
    * 
    * @param controller - the device controller.
    * @param type - the type of programming that shall be done.
    */
   public Bcu1ProgrammerJob(DeviceController controller, DeviceProgrammerType type)
   {
      this(controller);
      types.add(type);
   }

   /**
    * Create a BCU-1 device programmer job.
    * 
    * @param controller - the device controller.
    * @param types - the types of programming that shall be done.
    */
   public Bcu1ProgrammerJob(DeviceController controller, Collection<DeviceProgrammerType> types)
   {
      this(controller);
      this.types.addAll(types);
   }

   /*
    * Internal constructor
    */
   protected Bcu1ProgrammerJob(DeviceController controller)
   {
      this.controller = controller;
      this.device = controller.getDevice();

      label = I18n.formatMessage("Bcu1ProgrammerJob.Label", device.getPhysicalAddress().toString());
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
    * Inform the programmer job that the physical address of the device will be
    * programmed too. This is to avoid an error dialog when programming the
    * physical address fails.
    */
   public void setPhysicalAddressJobQueued()
   {
      this.physicalAddressJobQueued = true;
   }

   /**
    * Ensure that the hardware device is compatible and can be programmed.
    * 
    * @param memCon - the memory connection to use for accessing the device.
    * 
    * @throws JobFailedException if the device is not compatible
    * @throws TimeoutException
    * @throws IOException
    */
   void verifyDevice(final MemoryConnectionInterface memCon) throws IOException, TimeoutException, JobFailedException
   {
      byte[] mem = memCon.read(device.getProgram().getMask().getManufacturerIdAddress(), 1);

      // Verify the manufacturer ID
      final int manufacturerId = ByteUtils.toInteger(mem, 0, 1);
      if (manufacturerId != device.getProgram().getManufacturer().getId())
      {
         throw new JobFailedException(I18n.formatMessage("Bcu1ProgrammerJob.ErrWrongManufacturer", device
               .getPhysicalAddress().toString()));
      }

      // Verify the mask version
      final int maskVersion = memCon.getConnection().getDeviceDescriptor0().getVersion();
      final int maskVersionMajor = maskVersion >> 4;
      final int progMaskVersion = device.getProgram().getMask().getVersion();
      final int progMaskVersionMajor = progMaskVersion >> 4;
      if (progMaskVersionMajor != maskVersionMajor || progMaskVersion > maskVersion)
      {
         throw new JobFailedException(I18n.formatMessage("Bcu1ProgrammerJob.ErrIncompatibleMaskVersion",
                             Integer.toHexString(maskVersion),
                             device.getPhysicalAddress().toString(),
                             Integer.toHexString(progMaskVersion)
                            ));
      }
   }

   /**
    * Prepare device programming.
    * 
    * @param memCon - the memory connection to use for accessing the device.
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   void prepareUpload(final MemoryConnectionInterface memCon) throws IOException, TimeoutException
   {
      // Stop the BCU's application program by setting the runtime-error flags
      memCon.write(MemoryLocation.RunError, new byte[] { 0 });
   }

   /**
    * Finish device programming.
    * 
    * @param memCon - the memory connection to use for accessing the device.
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   void finishUpload(final MemoryConnectionInterface memCon) throws IOException, TimeoutException
   {
      // Clear the BCU's runtime-error flags
      memCon.write(MemoryLocation.RunError, new byte[] { (byte) 255 });
   }

   /**
    * Upload the application program to the device.
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   void uploadProgram(final MemoryConnectionInterface memCon) throws IOException, TimeoutException
   {
      /* final byte[] appInfo = */memCon.read(MemoryLocation.ApplicationID);

      // TODO

      final DeviceProgramming progr = device.getProgramming();
      progr.setProgramValid(true);
      progr.setLastUploadNow();
   }

   /**
    * Upload the parameters to the device.
    */
   void uploadParameters(final MemoryConnectionInterface memCon)
   {
      // TODO

      final DeviceProgramming progr = device.getProgramming();
      progr.setParametersValid(true);
      progr.setLastUploadNow();
   }

   /**
    * Upload the address table to the device.
    * 
    * @param memCon - the memory connection to use for the upload
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   void uploadAddrTab(final MemoryConnectionInterface memCon) throws IOException, TimeoutException
   {
      final GroupAddress[] groupAddrs = controller.getGroupAddresses();

      final int addrTabAddr = device.getProgram().getMask().getAddressTabAddress();
      final int addrTabSize = device.getProgram().getAddrTabSize();

      final int maxEntries = (addrTabSize - 1) >> 1;
      if (groupAddrs.length > maxEntries - 1)
      {
         throw new RuntimeException("The Address table of the device can only hold up to " + maxEntries
               + " addresses, including the device's address");
      }

      final byte[] data = new byte[((groupAddrs.length + 1) << 1) + 1];
      int idx = -1;
      data[++idx] = (byte) (groupAddrs.length + 1); // number of addresses

      int addr = device.getPhysicalAddress().getAddr();
      data[++idx] = (byte) (addr >> 8);
      data[++idx] = (byte) (addr & 255);

      for (int i = 0; i < groupAddrs.length; ++i)
      {
         addr = groupAddrs[i].getAddr();
         data[++idx] = (byte) (addr >> 8);
         data[++idx] = (byte) (addr & 255);
      }

      memCon.write(addrTabAddr, data);
   }

   /**
    * Upload the communication objects to the device.
    * 
    * @param memCon - the memory connection to use for the upload
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   void uploadCommObjsTab(final MemoryConnectionInterface memCon) throws IOException, TimeoutException
   {
      final ObjectDescriptor[] objDescs = controller.getObjectDescriptors();

      final int commsTabAddr = device.getProgram().getCommsTabAddr();
      final int commsTabSize = device.getProgram().getCommsTabSize();

      final int maxEntries = (commsTabSize - 2) / 3;
      if (objDescs.length > maxEntries)
      {
         throw new RuntimeException("The communications table of the device can only hold up to " + maxEntries
               + " communication objects");
      }

      final byte[] data = new byte[objDescs.length * 3 + 2];
      int idx = -1;
      data[++idx] = (byte) objDescs.length; // number of com-objects
      data[++idx] = (byte) getEepromByte(commsTabAddr + 1); // pointer to RAM flag table

      for (final ObjectDescriptor objDesc : objDescs)
      {
         byte[] objDescBytes = objDesc.toByteArray();

         data[++idx] = (byte) getEepromByte(commsTabAddr + idx);
         data[++idx] = objDescBytes[1];
         data[++idx] = objDescBytes[2];
      }

      memCon.write(commsTabAddr, data);
   }

   /**
    * Upload the association table to the device.
    * 
    * @param memCon - the memory connection to use for the upload
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   void uploadAssocTab(final MemoryConnectionInterface memCon) throws IOException, TimeoutException
   {
      final AssociationTableEntry[] associations = controller.getAssociationTable();
      final byte[] data = new byte[associations.length * 2 + 1];
      final byte unused = (byte) 0;

      final int assocTabAddr = device.getProgram().getAssocTabAddr();
      final int assocTabSize = device.getProgram().getAssocTabSize();

      if (data.length > assocTabSize)
      {
         throw new RuntimeException("The association table of the device can only hold up to " + assocTabSize
               + " associations");
      }

      int idx = -1;
      data[++idx] = (byte) associations.length;

      for (int i = 0; i < associations.length; ++i)
      {
         final AssociationTableEntry ae = associations[i];
         if (ae == null)
         {
            data[++idx] = unused;
            data[++idx] = unused;
         }
         else
         {
            data[++idx] = (byte) ae.getConnectionIndex();
            data[++idx] = (byte) ae.getDeviceObjectIndex();
         }
      }

      memCon.write(assocTabAddr, data);
   }
   
   /**
    * Upload the communication tables to the device.
    * 
    * @param memCon - the memory connection to use for the upload
    * 
    * @throws TimeoutException
    * @throws IOException
    */
   void uploadCommunications(final MemoryConnectionInterface memCon) throws IOException, TimeoutException
   {
      uploadAddrTab(memCon);
      uploadCommObjsTab(memCon);
      uploadAssocTab(memCon);

      final DeviceProgramming progr = device.getProgramming();
      progr.setCommunicationValid(true);
      progr.setLastUploadNow();
   }

   /**
    * Get a byte from the {@link Program#getEepromData() EEPROM data of the program} of
    * the device. If the program contains no EEPROM data, the byte is read from the
    * {@link Mask#getMaskEepromData() mask's EEPROM data}.
    * 
    * @param address - the address to read
    */
   byte getEepromByte(int address)
   {
      final Program program = device.getProgram();
      final Mask mask = program.getMask();

      byte[] data = program.getEepromData();
      if (data == null)
      {
         data = mask.getMaskEepromData();
         if (data == null) throw new RuntimeException("Program / mask contain no EEPROM data"); 
      }

      return data[address - mask.getUserEepromStart()];
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void main(BusInterface bus) throws JobFailedException, IOException, TimeoutException
   {
      // Wait a little for the device to be available after programming the physical address
      if (physicalAddressJobQueued)
         msleep(500);

//      if (!device.getProgramming().isPhysicalAddressValid())
//      {
//         if (physicalAddressJobQueued)
//         {
//            // If we come here, programming the physical address failed, and the
//            // user already got an error message. No need to report another
//            // error.
//            LOGGER.debug("Physical address not programmed, skipping job");
//            return;
//         }
//
//         throw new JobFailedException(I18n.formatMessage("Bcu1ProgrammerJob.ErrPhysicalAddressNotValid", device
//               .getPhysicalAddress().toString()));
//      }

      final DataConnection con = bus.connect(device.getPhysicalAddress(), Priority.SYSTEM);
      con.installMemoryAddressMapper();

      final MemoryConnectionInterface memCon = new MemoryConnection(con);

      verifyDevice(memCon);
      prepareUpload(memCon);

      if (types.contains(DeviceProgrammerType.PROGRAM))
      {
         LOGGER.info("uploadProgram");
         uploadProgram(memCon);
      }
      if (types.contains(DeviceProgrammerType.PARAMETERS))
         uploadParameters(memCon);
      if (types.contains(DeviceProgrammerType.COMMUNICATIONS))
         uploadCommunications(memCon);

      finishUpload(memCon);
   }
}
