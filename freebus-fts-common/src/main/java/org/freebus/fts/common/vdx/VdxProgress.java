package org.freebus.fts.common.vdx;

public interface VdxProgress
{
   /**
    * Set the total number for 100% progress.
    */
   public void setTotal(long total);
   
   /**
    * Set the current progress (0..total)
    */
   public void setProgress(long progress);
}
