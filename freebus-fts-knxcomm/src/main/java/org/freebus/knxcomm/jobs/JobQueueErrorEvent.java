package org.freebus.knxcomm.jobs;



public class JobQueueErrorEvent  {
	  /**
	    * The job that is currently processed.
	    */
	   public Job job;


	   /**
	    * The error message of the job
	    */
	   public String errormessage;

	   /**
	    * Create an JobQueueErrorEvent object.
	    * With message about the exception.
	    */
	   public JobQueueErrorEvent(Job job, String errormessage)
	   {
	      this.job = job;
	      this.errormessage = errormessage;
	   }



	 
	

}
