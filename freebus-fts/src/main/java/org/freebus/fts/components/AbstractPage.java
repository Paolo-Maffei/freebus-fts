package org.freebus.fts.components;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;


/**
 * Common class for pages that are usually displayed in the {@link WorkBench} application window.
 */
public abstract class AbstractPage extends JPanel
{
   private static final long serialVersionUID = 4021452407687663969L;

   /**
    * Create a page-frame.
    */
   public AbstractPage()
   {
      setName(getClass().getSimpleName());
   }

   /**
    * @return the preferred position of the page. Default is {@link PagePosition#CENTER}.
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
    * Update the page's contents.
    * This default implementation does nothing.
    */
   public void updateContents()
   {
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

      final JTabbedPane parent = (JTabbedPane) getParent();
      final int tabIndex = parent.indexOfComponent(this);
      if (tabIndex >= 0)
      {
         parent.setTitleAt(tabIndex, name);
         parent.getTabComponentAt(tabIndex).setName(name);
      }
   }
}
