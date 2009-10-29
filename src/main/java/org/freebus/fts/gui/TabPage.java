package org.freebus.fts.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.freebus.fts.utils.ImageCache;

/**
 * A tab-page. The tab-page contains a title that shall be used for the
 * tab item.
 * 
 * Toolbar and tool-buttons can be created. The tab-page is a selection
 * listener, and the created tool-buttons call {TabPage#widgetSelected}.
 * The idea is to specify some custom data when creating a tool-button,
 * and to override the {TabPage#widgetSelected} method, and use the
 * button's custom data to decide which action shall be executed.
 */
public abstract class TabPage extends Composite implements SelectionListener
{
   private String title = "Unnamed";
   private ToolBar toolBar = null;
   private int place = SWT.CENTER;

   /**
    * Create a new widget.
    * 
    * @param parent - the parent widget.
    */
   public TabPage(Composite parent)
   {
      super(parent, SWT.FLAT);
      setLayout(new FormLayout());
   }

   /**
    * Set the title of the tab-page.
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @return the title of the tab-page.
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * The place within the main-window where the tab-page is opened by default.
    * Can be {@link SWT#LEFT}, {@link SWT#CENTER}.
    * 
    * @param place the place to set.
    */
   public void setPlace(int place)
   {
      this.place = place;
   }

   /**
    * @return the place within the main-window where the tab-page is opened by default.
    */
   public int getPlace()
   {
      return place;
   }

   /**
    * Set the object that the tab-page shows. E.g. the {@link Project} or a {@link Floor}.
    */
   public abstract void setObject(Object o);

   /**
    * Create a toolbar item and add it to the toolbar.
    * 
    * @param iconName - the name of the icon, without extension, or null for no icon.
    * @param style - the style of the toolbar item, e.g. {@link SWT#PUSH} for a push-button.
    * @param data - the custom {@link Object} data is set as data of the button, if given.
    */
   public ToolItem createToolItem(String iconName, int style, Object data)
   {
      if (toolBar == null) createToolBar();

      final ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
      if (iconName != null) toolItem.setImage(ImageCache.getImage(iconName));
      toolItem.addSelectionListener(this);
      toolItem.setData(data);

      return toolItem;
   }

   /**
    * Create a toolbar and add it to the tab-page. Automatically
    * called by {@link #createToolItem}.
    */
   public ToolBar createToolBar()
   {
      toolBar = new ToolBar(this, SWT.FLAT|SWT.RIGHT_TO_LEFT);

      final FormData formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      toolBar.setLayoutData(formData);

      return toolBar;
   }

   /**
    * @return the toolbar.
    */
   public ToolBar getToolBar()
   {
      return toolBar;
   }

   /**
    * Dispose the widget.
    */
   @Override
   public void dispose()
   {
      MainWindow.getInstance().tabPageDisposed(this);
      super.dispose();
   }
   
   /**
    * Update the widget's contents. Called when the displayed
    * object is changed. The default implementation is empty.
    */
   public void updateContents()
   {
   }
   
   /**
    * Called when a tool-button is clicked.
    * This default implementation does nothing and should
    * be overridden in subclasses. Use the tool-button's custom
    * data, which can be specified in {@link #createToolItem}.
    */
   @Override
   public void widgetSelected(SelectionEvent e)
   {
   }

   /**
    * Calls {@link #widgetSelected}.
    */
   @Override
   public void widgetDefaultSelected(SelectionEvent e)
   {
      widgetSelected(e);
   }   
}
