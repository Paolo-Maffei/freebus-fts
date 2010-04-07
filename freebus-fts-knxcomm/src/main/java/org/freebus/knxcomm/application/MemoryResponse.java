package org.freebus.knxcomm.application;

/**
 * Response to a {@link MemoryRead memory read} request.
 */
public class MemoryResponse extends MemoryData
{
   /**
    * Create a memory response object with address 0 and no data.
    */
   public MemoryResponse()
   {
      this(0, null);
   }

   /**
    * Create a memory response object.
    * 
    * @param address - the 16 bit memory address.
    * @param data - the data. Up to 63 bytes.
    * 
    * @throws IllegalArgumentException if the supplied memory data has more than
    *            63 bytes.
    */
   protected MemoryResponse(int address, int[] data)
   {
      super(address, data);
   }

   /**
    * @return The type of the application:
    *         {@link ApplicationType#Memory_Response}.
    */
   @Override
   public ApplicationType getType()
   {
      return ApplicationType.Memory_Response;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ApplicationTypeResponse getApplicationResponses()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
