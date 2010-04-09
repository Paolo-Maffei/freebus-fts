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
   private final Semaphore semaphore = new Semaphore(0);
   final List<Telegram> telegrams = new LinkedList<Telegram>();
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
      bus.send(jobSteps.getConecet());

      try
      {
         waitforTelegram(jobSteps.getConecet(), Transport.ConnectedAck);
         jobSteps.setConnectStatus(JobStepStatus.Successfull);
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

         if (jobStep.getApplication().isDeviceDescriptorRequiered())
         {
            jobStep.getApplication().setDeviceDescriptorProperties(deviceDescriptorProperties);
         }
         jobStep.setSequence(i);
         
         bus.send(jobStep);

         ApplicationTypeResponse appres = jobStep.getApplication().getApplicationResponses();
         for (ApplicationType at : appres)
         {
            try
            {
               Telegram t = waitforTelegram(jobSteps.getConecet(), Transport.Connected, i, at);
               jobStep.setResivedApplication(t.getApplication());
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

   public Telegram waitforTelegram(Telegram send, Transport transport, int sequnece, ApplicationType app)
         throws JobFailedException
   {
      Telegram t;
      for (int i = 0; i < 50; i++)
      {
         msleep(10);
         t = searchReceivedByTransport(send, transport);
         if (t != null){
         if (t.getApplicationType() == app && t.getSequence() == sequnece)
            return t;
      }}
      throw new JobFailedException("NoAnwser");
   }

   public Telegram waitforTelegram(Telegram send, Transport transport) throws JobFailedException
   {
      Telegram t;
      for (int i = 0; i < 50; i++)
      {
         msleep(10);
         t = searchReceivedByTransport(send, transport);
         if (t != null)
            return t;
      }
      throw new JobFailedException("NoAnwser");
   }

   /**
    * Search in the received Telegram list to accord with send Telegram
    * 
    * @param send
    * @param transport
    * @return
    */
   public Telegram searchReceivedByTransport(Telegram send, Transport transport)
   {
      boolean transportfild;

      boolean fromAddr;
      boolean destaddr;
      for (Telegram t : telegrams)
      {
         transportfild = false;

         fromAddr = false;
         destaddr = false;

         if (t.getTransport() == transport)
            transportfild = true;
         if (t.getDest().getAddr() == send.getFrom().getAddr())
            destaddr = true;
         if (t.getFrom().getAddr() == send.getDest().getAddr())
            fromAddr = true;
         if (transportfild == true && fromAddr == true && destaddr == true)
            return t;
      }
      return null;

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
      semaphore.release();

   }
}
