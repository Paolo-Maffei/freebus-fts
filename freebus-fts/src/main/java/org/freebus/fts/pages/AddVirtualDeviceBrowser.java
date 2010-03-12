package org.freebus.fts.pages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;

import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ProjectController;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.VirtualDevice;


/**
 * A virtual-device browser that has a button which imports the current
 * virtual-device into the project.
 * 
 * Requires that a project controller is set with {@link #setProjectController}.
 */
public class AddVirtualDeviceBrowser extends ProductsBrowser
{
   private static final long serialVersionUID = 5704775166874780673L;

   private JButton btnAdd = new JButton(I18n.getMessage("ProductsToProjectBrowser.AddButton"));
   private ProjectController controller;

   /**
    * Create a org.freebus.fts.products browser. 
    */
   public AddVirtualDeviceBrowser()
   {
      super();
      setName(I18n.getMessage("ProductsToProjectBrowser.Title"));

      final Box boxBottom = getBottomBox();
      boxBottom.add(Box.createHorizontalGlue());
      boxBottom.add(btnAdd);

      btnAdd.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            addSelectedDevice();
         }
      });
   }

   /**
    * Add the currently selected device to the project, using the project
    * controller. Does nothing if no project controller is set.
    */
   public void addSelectedDevice()
   {
      final VirtualDevice dev = getSelectedVirtualDevice();

      if (dev != null && controller != null)
         controller.addDevice(dev);
   }

   /**
    * Set the project controller that is used to manipulate the current
    * project.
    */
   public void setProjectController(final ProjectController controller)
   {
      this.controller = controller;
   }

   /**
    * Update the selected catalog entry.
    */
   @Override
   public void updateCatalogEntry()
   {
      super.updateCatalogEntry();

      final VirtualDevice dev = getSelectedVirtualDevice();
      final CatalogEntry entry = (dev == null ? null : dev.getCatalogEntry());
      final boolean valid = dev != null && entry != null;

      getBottomBox().setVisible(valid);
   }
}
