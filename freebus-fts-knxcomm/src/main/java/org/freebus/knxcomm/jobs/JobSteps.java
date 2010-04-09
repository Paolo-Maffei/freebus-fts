package org.freebus.knxcomm.jobs;

import java.util.ArrayList;
import java.util.Vector;

import org.freebus.fts.common.address.Address;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

public class JobSteps extends ArrayList<JobStep>
{
   private static final long serialVersionUID = -2571503594228446068L;
   private Address dest = GroupAddress.BROADCAST;
   private JobStepStatus ConnectStatus =JobStepStatus.Waiting ;
   /**
    * @return the connectStatus
    */
   public JobStepStatus getConnectStatus()
   {
      return ConnectStatus;
   }

   private PhysicalAddress from = PhysicalAddress.NULL;

   private Priority priority = Priority.SYSTEM;

   private boolean repeated = false;

   private int routingCounter = 6;

   public Address getDest()
   {
      return dest;
   }

   public PhysicalAddress getFrom()
   {
      return from;
   }

   public Priority getPriority()
   {
      return priority;
   }

   public int getRoutingCounter()
   {
      return routingCounter;
   }

   public boolean isRepeated()
   {
      return repeated;
   }

   public void setDest(Address dest)
   {
      this.dest = dest;
   }

   public void setFrom(PhysicalAddress from)
   {
      this.from = from;
   }

   public void setPriority(Priority priority)
   {
      this.priority = priority;
   }

   public void setRepeated(boolean repeated)
   {
      this.repeated = repeated;
   }

   public void setRoutingCounter(int routingCounter)
   {
      this.routingCounter = routingCounter;
   }

   public void add(Application application)
   {
      JobStep jobStep = new JobStep();
      jobStep.setFrom(this.from);
      jobStep.setDest(this.dest);
      jobStep.setPriority(this.priority);
      jobStep.setRepeated(this.repeated);
      jobStep.setRoutingCounter(this.routingCounter);
      jobStep.setApplication(application);
      jobStep.setTransport(Transport.Connected);
      add(jobStep);
   }

   private java.util.Vector<JobStepStatusListner> listeners;

   public void addMyListener(JobStepStatusListner l)
   {
      if (listeners == null)
      {
         listeners = new Vector<JobStepStatusListner>();
      }
      listeners.add(l);
   }

   public void removeMyListener(JobStepStatusListner l)
   {
      if (listeners != null)
      {
         listeners.remove(l);
      }
   }

   protected void notifyJobStepStatus()
   {

      JobStepStatusEvent e = new JobStepStatusEvent(this, this);

      int size = listeners.size();
      for (int i = 0; i < size; i++)
      {
         JobStepStatusListner l = listeners.elementAt(i);
         l.JobStepStatus(e);
      }
   }



   public Telegram getConecet()
   {

      Telegram telegram = new Telegram();
      telegram.setFrom(this.from);
      telegram.setDest(this.dest);
      telegram.setPriority(this.priority);
      telegram.setRepeated(this.repeated);
      telegram.setTransport(Transport.Connect);
      return telegram;

   }
public void setConnectStatus(JobStepStatus connectStatus){
   this.ConnectStatus = connectStatus;
}
   public Telegram getDisconecet()
   {

      Telegram telegram = new Telegram();
      telegram.setFrom(this.from);
      telegram.setDest(this.dest);
      telegram.setPriority(this.priority);
      telegram.setRepeated(this.repeated);
      telegram.setTransport(Transport.Disconnect);
      return telegram;

   }
}
