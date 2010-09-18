package org.freebus.knxcomm.application;

import java.io.DataInput;
import java.io.IOException;

import org.freebus.fts.common.address.GroupAddress;

/**
 * Request the value of a {@link GroupAddress}. The response is a
 * {@link GroupValueResponse} telegram.
 */
public final class GroupValueRead extends AbstractApplication
{
   /**
    * Create an empty group-value read object
    */
   public GroupValueRead()
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ApplicationType getType()
   {
      return ApplicationType.GroupValue_Read;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void readData(DataInput in, int length) throws IOException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      return 0;
   }
}
