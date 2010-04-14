package org.freebus.knxcomm.jobs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.ApplicationTypeResponse;
import org.freebus.knxcomm.applicationData.DeviceDescriptorProperties;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

public class JobStepsQueue extends SingleDeviceJob
{

   private ApplicationType applicationExpected;

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
   public void main(BusInterface bus) throws Exception
   {
      // TODO: not finished
     // jobSteps.setFrom(bus.getPhysicalAddress());
      Logger.getLogger(getClass()).debug("Send Connect ");
      bus.send(jobSteps.getConecet());


      try
      {

         TelegramSearchConditions tsc = new TelegramSearchConditions();
         tsc.setTransport(Transport.ConnectedAck);
        Telegrams t = waitforTelegram(tsc);
        if (t.size() == 1)  Logger.getLogger(getClass()).debug("Resived ACK");

      }
      catch (JobFailedException e)
      {
         e.getMessage().equals("NoAnwser");
         jobSteps.setConnectStatus(JobStepStatus.Error);
         jobSteps.notifyJobStepStatus();
      }
      telegrams.clear();
      int i = 0;
      for (JobStep jobStep : jobSteps)
      {

         if (jobStep.getApplication().isDeviceDescriptorRequired())
         {
            jobStep.getApplication().setDeviceDescriptorProperties(deviceDescriptorProperties);
         }
         jobStep.setSequence(i);
         Logger.getLogger(getClass()).debug("Send Telegram: "+jobStep.toString());
         bus.send(jobStep);

         ApplicationTypeResponse appres = jobStep.getApplication().getApplicationResponses();
         for (ApplicationType at : appres)
         {
            try
            {

            	TelegramSearchConditions tsc = new TelegramSearchConditions();
            	tsc.setTransport(Transport.Connected);
            	tsc.setApplicationType(at);
               Telegrams t = waitforTelegram(tsc);
               Logger.getLogger(getClass()).debug("Found resived telegram: "+t.get(0).toString());
               jobStep.setResivedApplication(t.get(0).getApplication());
               jobStep.setJobStepStatus(JobStepStatus.Successfull);


            }
            catch (JobFailedException e)
            {
               e.getMessage().equals("Job Step Failed");
               jobSteps.setConnectStatus(JobStepStatus.Error);

               telegrams.clear();
            }
         }
         i++;
         telegrams.clear();
      }
      telegrams.clear();

      msleep(5000);
      bus.send(jobSteps.getDisconecet());
      jobSteps.notifyJobStepStatus();
   }

   public Telegrams waitforTelegram(TelegramSearchConditions telegramSearchConditions)
         throws JobFailedException
   {



      Telegrams ts;
      for (int i = 0; i < 50; i++)
      {
         msleep(10);
        ts = telegrams.searchTelegram(telegramSearchConditions);
        if (ts.size()>0)return ts;

      }
      throw new JobFailedException("NoAnwser");
   }



   @Override
   public String getLabel()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void setDeviceDescriptorProperties(DeviceDescriptorProperties deviceDescriptorProperties)
   {
      this.deviceDescriptorProperties = deviceDescriptorProperties;
   }

   public void read(JobStep jobStep)
   {

   }



   /**
    * Sleep some milliseconds.
    */
   protected void msleep(int milliseconds)
   {
      try
      {
         Thread.sleep(milliseconds);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      telegrams.add(telegram);
      Logger.getLogger(getClass()).debug("Received answer: " + telegram);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSendConfirmed(Telegram telegram)
   {
      telegrams.add(telegram);
      Logger.getLogger(getClass()).debug("Confirm answer: " + telegram);
   }
}
