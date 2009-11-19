package org.freebus.fts.gui.actions;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.freebus.fts.utils.ImageCache;

/**
 * Base class for actions. An action can be attached to GUI elements like
 * buttons or menu items.
 */
public abstract class GenericAction implements SelectionListener
{
   private final String label, toolTip;
   private final Image image;
   private final CopyOnWriteArrayList<WeakReference<Widget>> widgets = new CopyOnWriteArrayList<WeakReference<Widget>>();

   /**
    * Create an action object.
    */
   public GenericAction(String label, String toolTip, Image image)
   {
      this.label = label;
      this.toolTip = toolTip;
      this.image = image;
   }

   /**
    * Create an action object. The image, if given, is fetched from the image
    * cache with {@link ImageCache#getImage}.
    */
   public GenericAction(String label, String toolTip, String imageName)
   {
      this.label = label;
      this.toolTip = toolTip;

      if (imageName == null) image = null;
      else image = ImageCache.getImage(imageName);
   }

   /**
    * Attach the action to the given button. The button's text, tool-tip and
    * image are set to the application's contents, if the application has
    * non-null values.
    */
   public final void attach(Button button)
   {
      widgets.add(new WeakReference<Widget>(button));

      button.addSelectionListener(this);
      if (label != null) button.setText(label);
      if (toolTip != null) button.setToolTipText(toolTip);
      if (image != null) button.setImage(image);
   }

   /**
    * Attach the action to the given menu item. The menu-item's text and image
    * are set to the application's contents, if the application has non-null
    * values.
    */
   public final void attach(MenuItem menuItem)
   {
      widgets.add(new WeakReference<Widget>(menuItem));

      menuItem.addSelectionListener(this);
      if (label != null) menuItem.setText(label);
      if (image != null) menuItem.setImage(image);
   }

   /**
    * Attach the action to the given tool item. The tool-item's tool-tip text
    * and image are set to the application's contents, if the application has
    * non-null values.
    */
   public final void attach(ToolItem toolItem)
   {
      widgets.add(new WeakReference<Widget>(toolItem));

      toolItem.addSelectionListener(this);
      if (toolTip != null) toolItem.setToolTipText(toolTip);
      if (image != null) toolItem.setImage(image);
   }

   /**
    * Detach the action from the given button.
    */
   public final void detach(Button button)
   {
      widgets.remove(new WeakReference<Widget>(button));
      button.removeSelectionListener(this);
   }

   /**
    * Detach the action from the given menu item.
    */
   public final void detach(MenuItem menuItem)
   {
      widgets.remove(new WeakReference<Widget>(menuItem));
      menuItem.removeSelectionListener(this);
   }

   /**
    * Detach the action from the given tool item.
    */
   public final void detach(ToolItem toolItem)
   {
      widgets.remove(new WeakReference<Widget>(toolItem));
      toolItem.removeSelectionListener(this);
   }

   /**
    * Create a button and attach this action to it.
    * 
    * @return the created button.
    */
   public final Button newButton(Composite parent, int style)
   {
      final Button button = new Button(parent, style);
      attach(button);
      return button;
   }

   /**
    * Create a menu-item and attach this action to it.
    * 
    * @return the created menu-item.
    */
   public final MenuItem newMenuItem(Menu parent, int style)
   {
      final MenuItem item = new MenuItem(parent, style);
      attach(item);
      return item;
   }

   /**
    * Create a tool-item and attach this action to it.
    * 
    * @return the created tool-item.
    */
   public final ToolItem newToolItem(ToolBar parent, int style)
   {
      final ToolItem item = new ToolItem(parent, style);
      attach(item);
      return item;
   }

   /**
    * Enable/disable all widgets to which this action is attached.
    */
   public final void setEnabled(boolean enabled)
   {
      for (final WeakReference<Widget> ref : widgets)
      {
         final Widget widget = ref.get();
         if (widget instanceof Button)
         {
            final Button button = (Button) widget;
            button.setEnabled(enabled);
         }
         else if (widget instanceof MenuItem)
         {
            final MenuItem menuItem = (MenuItem) widget;
            menuItem.setEnabled(enabled);
         }
         else if (widget instanceof ToolItem)
         {
            final ToolItem toolItem = (ToolItem) widget;
            toolItem.setEnabled(enabled);
         }
      }
   }

   /**
    * Called when the action is triggered. To be implemented in subclasses.
    */
   public abstract void triggered(SelectionEvent event);

   /**
    * Callback, which is called when a widget to which the action is attached
    * gets selected.
    */
   @Override
   public final void widgetDefaultSelected(SelectionEvent event)
   {
      triggered(event);
   }

   /**
    * Callback, which is called when a widget to which the action is attached
    * gets selected.
    */
   @Override
   public final void widgetSelected(SelectionEvent event)
   {
      triggered(event);
   }
}
