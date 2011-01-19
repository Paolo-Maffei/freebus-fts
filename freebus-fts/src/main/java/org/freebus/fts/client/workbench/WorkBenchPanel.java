package org.freebus.fts.client.workbench;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.freebus.fts.client.FTS;
import org.freebus.fts.client.components.PagePosition;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.elements.components.Closeable;

/**
 * A panel that is displayed in the {@link WorkBench} and contains some content.
 * <p>
 * You most probably do not want to inherit from this class directly, but rather
 * inherit from {@link WorkBenchView} or {@link WorkBenchEditor}.
 * 
 * @see WorkBenchView
 * @see WorkBenchEditor
 */
public abstract class WorkBenchPanel extends JPanel implements Closeable
{
   private static final long serialVersionUID = 4021452407687663969L;

   private boolean modified;

   /**
    * Create a page-frame. Sets the name of the page to the name of the class.
    */
   public WorkBenchPanel()
   {
      setName(getClass().getSimpleName());
   }

   /**
    * @return the preferred position of the page. Default is
    *         {@link PagePosition#CENTER}.
    */
   public PagePosition getPagePosition()
   {
      return PagePosition.CENTER;
   }

   /**
    * Called when the page is closed.
    */
   protected void closeEvent()
   {
   }

   /**
    * Update the page's contents. This default implementation does nothing.
    */
   public void updateContents()
   {
   }

   /**
    * @return The work bench.
    */
   protected WorkBench getWorkBench()
   {
      return ((WorkBench) WorkBench.getInstance());
   }
   
   /**
    * Show the panel on the {@link WorkBench}.
    */
   public void open()
   {
      getWorkBench().addPanel(this);
      getWorkBench().showPanel(this);
   }

   /**
    * Close the page.
    */
   @Override
   public void close()
   {
      setVisible(false);

      if (!(getParent() instanceof JTabbedPane))
         return;

      final JTabbedPane parent = (JTabbedPane) getParent();
      final int tabIndex = parent.indexOfComponent(this);
      if (tabIndex >= 0)
      {
         parent.remove(tabIndex);
      }
   }

   /**
    * Set the name of the page.
    */
   @Override
   public void setName(String name)
   {
      super.setName(name);

      if (!(getParent() instanceof JTabbedPane))
         return;

      if (isModified())
         name = '*' + name;

      final JTabbedPane parent = (JTabbedPane) getParent();
      final int tabIndex = parent.indexOfComponent(this);
      if (tabIndex >= 0)
      {
         parent.setTitleAt(tabIndex, name);
         parent.getTabComponentAt(tabIndex).setName(name);
      }
   }

   /**
    * Set the modified flag.
    */
   public void setModified(boolean modified)
   {
      if (this.modified == modified)
         return;

      this.modified = modified;
      setName(getName());
   }

   /**
    * @return true if the page's contents is modified.
    */
   public boolean isModified()
   {
      return modified;
   }

   /**
    * Show a confirmation dialog that asks the user if the changes in the page
    * shall be applied before closing.
    * 
    * @return {@link JOptionPane#YES_OPTION}, {@link JOptionPane#NO_OPTION}, or
    *         {@link JOptionPane#CANCEL_OPTION}.
    */
   protected int confirmClose()
   {
      final String title = I18n.getMessage("AbstractPage.ConfirmClose_Title") + " - " + FTS.getInstance().getName();
      final String msg = "<html><body width=\"300\">"
            + I18n.getMessage("AbstractPage.ConfirmClose_Text").replace("\n", "<br />") + "</body></html>";

      return JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_CANCEL_OPTION);
   }
}
