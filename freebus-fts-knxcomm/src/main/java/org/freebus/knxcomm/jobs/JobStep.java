package org.freebus.knxcomm.jobs;

import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.telegram.Telegram;

public class JobStep extends Telegram
{

   private boolean DeviceDescriptorRequired;
   private Application ResivedApplication;
   private JobStepStatus jobStepStatus;

   /**
    * @return the deviceDescriptorRequiered
    */
   public boolean isDeviceDescriptorRequiered()
   {
      return DeviceDescriptorRequired;
   }

   /**
    * @param deviceDescriptorRequiered the deviceDescriptorRequiered to set
    */
   public void setDeviceDescriptorRequired(boolean DeviceDescriptorRequired)
   {
      this.DeviceDescriptorRequired = DeviceDescriptorRequired;
   }

   /**
    * @return the resivedApplication
    */
   public Application getResivedApplication()
   {
      return ResivedApplication;
   }

   /**
    * @param resivedApplication the resivedApplication to set
    */
   public void setResivedApplication(Application resivedApplication)
   {
      ResivedApplication = resivedApplication;
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
