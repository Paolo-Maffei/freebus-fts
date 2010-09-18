package org.freebus.knxcomm.jobs.steps;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.application.attic.DeviceDescriptorProperties;
import org.freebus.knxcomm.internal.DataConnectionImpl;
import org.freebus.knxcomm.jobs.SingleDeviceJob;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;

public class JobStepsQueue extends SingleDeviceJob
{
   final Telegrams telegrams = new Telegrams();

   DeviceDescriptorProperties deviceDescriptorProperties;

   class recvieTelegram extends Telegram
   {

   }

   JobSteps jobSteps;

   public JobStepsQueue(JobSteps jobSteps)
   {
      super(jobSteps.getDest());
      this.jobSteps = jobSteps;
      // TODO Auto-generated constructor stub
   }

   @Override
   public void main(BusInterface bus) throws IOException
   {
      // TODO: not finished
      Logger.getLogger(getClass()).debug("Connect to Device "+jobSteps.getDest().toString());
      DataConnectionImpl dataConnection  =(DataConnectionImpl) bus.connect((PhysicalAddress)  jobSteps.getDest(), Priority.SYSTEM);
      for (JobStep jobStep : jobSteps)
      {
         Logger.getLogger(getClass()).debug("Send Telegram: "+jobStep.toString());
         try
         {
            jobStep.setResivedApplication(dataConnection.query(jobStep.getApplication()));
            jobStep.setJobStepStatus(JobStepStatus.finished);
         }
         catch (Exception e)
         {
            jobStep.setJobStepStatus(JobStepStatus.Error);
         }
      }
      dataConnection.close();
      jobSteps.notifyJobStepStatus();
   }





   @Override
   public String getLabel()
   {
      // TODO Auto-generated method stub
      return null;
   }






   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
    }

   /**
    * @return the deviceDescriptorProperties
    */
   public DeviceDescriptorProperties getDeviceDescriptorProperties()
   {
      return deviceDescriptorProperties;
   }

   /**
    * @param deviceDescriptorProperties the deviceDescriptorProperties to set
    */
   public void setDeviceDescriptorProperties(DeviceDescriptorProperties deviceDescriptorProperties)
   {
      this.deviceDescriptorProperties = deviceDescriptorProperties;
   }
}
