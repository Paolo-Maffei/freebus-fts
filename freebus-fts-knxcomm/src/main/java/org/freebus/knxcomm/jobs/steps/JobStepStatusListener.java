package org.freebus.knxcomm.jobs.steps;

import java.util.EventListener;

public interface JobStepStatusListener extends EventListener
{
   public void JobStepStatus(JobStepStatusEvent e);
}
