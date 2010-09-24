package org.freebus.fts.components;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.freebus.fts.FTS;
import org.freebus.fts.core.I18n;
import org.freebus.fts.elements.components.Closeable;

/**
 * Common class for pages that are usually displayed in the {@link WorkBench}
 * application window.
 */
public abstract class AbstractPage extends JPanel implements Closeable
{
   private static final long serialVersionUID = 4021452407687663969L;

   private boolean modified;

   /**
    * Create a page-frame.
    */
   public AbstractPage()
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
    * Set the object that the page shall display.
    */
   public abstract void setObject(Object o);

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
      final String msg = "<html><body width=\"300\">" +  I18n.getMessage("AbstractPage.ConfirmClose_Text").replace("\n", "<br />") + "</body></html>";

      return JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_CANCEL_OPTION);
   }
}
