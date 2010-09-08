package org.freebus.fts.pages;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityTransaction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.freebus.fts.core.I18n;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.ProductsImporter;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.ProductsFactory;

/**
 * A org.freebus.fts.products browser that allows to mark org.freebus.fts.products for import.
 */
public class ProductsImportBrowser extends ProductsBrowser
{
   private static final long serialVersionUID = 5704775166874780673L;

   private final JCheckBox cbxImport = new JCheckBox(I18n.getMessage("ProductsBrowser.ImportOption"));
   private final JButton btnImport = new JButton(I18n.getMessage("ProductsBrowser.ImportButton"));
   private final Set<VirtualDevice> importDevices = new HashSet<VirtualDevice>();

   /**
    * Create a import-org.freebus.fts.products browser page.
    */
   public ProductsImportBrowser()
   {
      super();

      final Box boxBottom = getBottomBox();
      boxBottom.setVisible(false);
      boxBottom.add(Box.createHorizontalGlue());
      boxBottom.add(cbxImport);
      boxBottom.add(Box.createHorizontalStrut(20));
      boxBottom.add(btnImport);

      cbxImport.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            final VirtualDevice currentDevice = getSelectedVirtualDevice();
            if (cbxImport.isSelected()) importDevices.add(currentDevice);
            else importDevices.remove(currentDevice);
            btnImport.setEnabled(!importDevices.isEmpty());

         }
      });

      btnImport.setEnabled(false);
      btnImport.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            if (!importDevices.isEmpty())
               importProducts(importDevices);
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setObject(Object obj)
   {
      importDevices.clear();
      super.setObject(obj);
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
      if (valid) cbxImport.setSelected(importDevices.contains(dev));
   }

   /**
    * Import the marked virtual devices.
    */
   protected void importProducts(Set<VirtualDevice> virtualDevices)
   {
      final ProductsFactory ftsProductsFactory = ProductsManager.getFactory();
      final EntityTransaction transaction = ftsProductsFactory.getTransaction();

      try
      {
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

         final List<VirtualDevice> virtDevsList = new Vector<VirtualDevice>(virtualDevices.size());

         final Iterator<VirtualDevice> it = virtualDevices.iterator();
         while (it.hasNext())
            virtDevsList.add(it.next());

         transaction.begin();

         final ProductsImporter importer = ProductsManager.getProductsImporter(getProductsFactory(), ftsProductsFactory);
         importer.copy(virtDevsList);

         transaction.commit();

         importDevices.clear();
         updateCatalogEntry();

         JOptionPane.showMessageDialog(this, I18n.formatMessage("ProductsImportBrowser.DoneMessage",
               new Object[] { virtDevsList.size() }), I18n.getMessage("ProductsImportBrowser.DoneTitle"),
               JOptionPane.INFORMATION_MESSAGE);
      }
      catch (Exception e)
      {
         if (transaction.isActive())
            transaction.rollback();

         Dialogs.showExceptionDialog(e, I18n.getMessage("ProductsImportBrowser.ErrImport"));
      }
      finally
      {
         setCursor(Cursor.getDefaultCursor());
      }
   }
}
