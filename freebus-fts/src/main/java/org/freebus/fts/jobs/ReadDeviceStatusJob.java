package org.freebus.fts.jobs;

import java.io.IOException;

import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.telegram.Address;

public class ReadDeviceStatusJob extends SingleDeviceJob
{

   ReadDeviceStatusJob(Address targetAddress)
   {
      super(targetAddress);
      // TODO Auto-generated constructor stub
   }

   @Override
   public void main(BusInterface bus) throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public String getLabel()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
