package org.freebus.fts.client.workbench;

import javax.swing.SwingUtilities;

/**
 * Base class for workbench editors. An editor is displayed in the center of the
 * workbench. Editors are not usually not unique.
 * 
 * @see WorkBenchView
 */
public abstract class WorkBenchEditor extends WorkBenchPanel
{
   private static final long serialVersionUID = 1792996292582082779L;
   private Object obj;
   
   /**
    * Create a workbench editor.
    */
   public WorkBenchEditor()
   {
      super();
   }

   /**
    * Set the object that is edited / viewed.
    * <p>
    * Ensures that the editor is shown in the workbench and brings it to the
    * front to make it visible.
    * <p>
    * Calls {@link #objectChanged(Object)} to inform the editor of the change.
    * 
    * @param obj - the object to set.
    */
   public final void setObject(final Object obj)
   {
      this.obj = obj;

      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            objectChanged(obj);

            getWorkBench().addPanel(WorkBenchEditor.this);
            getWorkBench().showPanel(WorkBenchEditor.this);
         }
      });
   }

   /**
    * @return The object that is edited / viewed.
    */
   public Object getObject()
   {
      return obj;
   }

   /**
    * The viewed / edited object was changed.
    * 
    * @param obj - the object.
    */
   public abstract void objectChanged(Object obj);
}
