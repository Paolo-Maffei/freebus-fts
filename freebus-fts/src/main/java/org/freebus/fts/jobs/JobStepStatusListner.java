package org.freebus.fts.jobs;

import java.util.EventListener;

public interface JobStepStatusListner extends EventListener
{
   public void JobStepStatus(JobStepStatusEvent e);
}
