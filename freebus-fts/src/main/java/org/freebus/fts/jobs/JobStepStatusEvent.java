package org.freebus.fts.jobs;

public class JobStepStatusEvent extends java.util.EventObject

{
   private JobSteps jobSteps;

   public JobStepStatusEvent(Object source, JobSteps jobSteps)
   {
      super(source);
      this.jobSteps =jobSteps;
      // TODO Auto-generated constructor stub
   }

   /**
    * 
    */
   private static final long serialVersionUID = -5663477590368694895L;

   public JobSteps getJobSteps()
   {
      return jobSteps;
   }

}
