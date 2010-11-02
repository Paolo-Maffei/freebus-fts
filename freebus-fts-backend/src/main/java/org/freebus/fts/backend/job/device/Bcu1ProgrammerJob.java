package org.freebus.fts.backend.job.device;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.freebus.fts.backend.devicecontroller.DeviceController;
import org.freebus.fts.backend.devicecontroller.DeviceProgrammerType;
import org.freebus.fts.backend.exception.JobFailedException;
import org.freebus.fts.backend.internal.I18n;
import org.freebus.fts.backend.job.ListenableJob;
import org.freebus.fts.common.ByteUtils;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceProgramming;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.MemoryConnectionAdapter;
import org.freebus.knxcomm.application.memory.MemoryLocation;
import org.freebus.knxcomm.telegram.Priority;

/**
 * A device programmer job that using BCU1 programming techniques.
 */
public class Bcu1ProgrammerJob extends ListenableJob implements DeviceProgrammerJob
{
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
    * Inform the programmer job that the physical address of the device will
    * be programmed too. This is to avoid an error dialog when programming
    * the physical address fails.
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
   void verifyDevice(final MemoryConnectionAdapter memCon) throws IOException, TimeoutException, JobFailedException
   {
      final int manufacturerIdAddr = device.getProgram().getMask().getManufacturerIdAddress();
      final int manufacturerIdSize = 1;
      byte[] mem = memCon.read(manufacturerIdAddr, manufacturerIdSize);

      // Verify the manufacturer ID
      final int manufacturerId = ByteUtils.toInteger(mem, 0, manufacturerIdSize);
      if (manufacturerId != device.getProgram().getManufacturer().getId())
         throw new JobFailedException(I18n.formatMessage("Bcu1ProgrammerJob.ErrWrongManufacturer", device
               .getPhysicalAddress().toString()));
   }

   /**
    * Prepare device programming.
    *
    * @param memCon - the memory connection to use for accessing the device.
    *
    * @throws TimeoutException
    * @throws IOException
    */
   void prepareUpload(final MemoryConnectionAdapter memCon) throws IOException, TimeoutException
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
   void finishUpload(final MemoryConnectionAdapter memCon) throws IOException, TimeoutException
   {
      // Clear the BCU's runtime-error flags
      memCon.write(MemoryLocation.RunError, new byte[] { (byte) 255 });
   }

   /**
    * Upload the application program to the device.
    */
   void uploadProgram(final MemoryConnectionAdapter memCon)
   {

      final DeviceProgramming progr = device.getProgramming();
      progr.setProgramValid(true);
      progr.setLastUploadNow();
   }

   /**
    * Upload the parameters to the device.
    */
   void uploadParameters(final MemoryConnectionAdapter memCon)
   {

      final DeviceProgramming progr = device.getProgramming();
      progr.setParametersValid(true);
      progr.setLastUploadNow();

   }

   /**
    * Upload the communication tables to the device.
    */
   void uploadCommunications(final MemoryConnectionAdapter memCon)
   {

      final DeviceProgramming progr = device.getProgramming();
      progr.setCommunicationValid(true);
      progr.setLastUploadNow();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void main(BusInterface bus) throws JobFailedException, IOException, TimeoutException
   {
      if (!device.getProgramming().isPhysicalAddressValid())
      {
         if (physicalAddressJobQueued)
         {
            // If we come here, programming the physical address failed, and the user
            // already got an error message. No need to report another error.
            return;
         }

         throw new JobFailedException(I18n.formatMessage("Bcu1ProgrammerJob.ErrPhysicalAddressNotValid", device
               .getPhysicalAddress().toString()));
      }

      final DataConnection con = bus.connect(device.getPhysicalAddress(), Priority.SYSTEM);
      con.installMemoryAddressMapper();

      final MemoryConnectionAdapter memCon = new MemoryConnectionAdapter(con);

      verifyDevice(memCon);
      prepareUpload(memCon);

      if (types.contains(DeviceProgrammerType.PROGRAM))
         uploadProgram(memCon);
      if (types.contains(DeviceProgrammerType.PARAMETERS))
         uploadParameters(memCon);
      if (types.contains(DeviceProgrammerType.COMMUNICATIONS))
         uploadCommunications(memCon);

      finishUpload(memCon);
   }
}
