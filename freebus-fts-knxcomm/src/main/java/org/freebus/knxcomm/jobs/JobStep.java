package org.freebus.knxcomm.jobs;

import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.telegram.Telegram;

public class JobStep extends Telegram
{

   private boolean deviceDescriptorRequired;
   private Application receivedApplication;
   private JobStepStatus jobStepStatus;

   /**
    * @return the deviceDescriptorRequiered
    */
   public boolean isDeviceDescriptorRequired()
   {
      return deviceDescriptorRequired;
   }

   /**
    * @param DeviceDescriptorRequired the deviceDescriptorRequiered to set
    */
   public void setDeviceDescriptorRequired(boolean DeviceDescriptorRequired)
   {
      this.deviceDescriptorRequired = DeviceDescriptorRequired;
   }

   /**
    * @return the received application
    */
   public Application getReceivedApplication()
   {
      return receivedApplication;
   }

   /**
    * @param resivedApplication the resivedApplication to set
    */
   public void setResivedApplication(Application resivedApplication)
   {
      receivedApplication = resivedApplication;
   }

   /**
    * @return the jobStepStatus
    */
   public JobStepStatus getJobStepStatus()
   {
      return jobStepStatus;
   }

   /**
    * @param jobStepStatus the jobStepStatus to set
    */
   public void setJobStepStatus(JobStepStatus jobStepStatus)
   {
      this.jobStepStatus = jobStepStatus;
   }



}
