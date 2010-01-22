package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.freebus.fts.MainWindow;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.CatalogEntryDetails;
import org.freebus.fts.core.I18n;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Products;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.VirtualDeviceService;
import org.freebus.fts.utils.TreeUtils;

/**
 * A browser for a products database.
 */
public class ProductsBrowser extends AbstractPage
{
   private static final long serialVersionUID = -3626465155128774861L;

   private ProductsFactory productsFactory;

   private final JComboBox cboManufacturer;
   private final JSplitPane sppCenter, sppTop;

   private final JTree treCategories;
   private final JScrollPane scpCategories;
   private final DefaultMutableTreeNode rootCategories = new DefaultMutableTreeNode("[CATEGORIES]");
   private DefaultTreeModel trmCategories = new DefaultTreeModel(rootCategories);

   private final JTable tblEntries;
   private final JScrollPane scpEntries;
   private DefaultTableModel tbmEntries = new DefaultTableModel(0, 2);

   private final JLabel lblEntryName;
   private final CatalogEntryDetails ceDetails;

   /**
    * Create a bus monitor widget.
    */
   public ProductsBrowser()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("ProductsBrowser.Title"));
      setBorder(BorderFactory.createLineBorder(getBackground(), 4));

      final Font fntCaption = getFont().deriveFont(Font.BOLD);

      final JPanel pnlTopLeft = new JPanel();
      final JPanel pnlTopRight = new JPanel();
      final JPanel pnlBottom = new JPanel();

      sppTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlTopLeft, pnlTopRight);
      sppTop.setDividerLocation(300);
      sppTop.setContinuousLayout(true);
      sppTop.setFocusable(false);

      sppCenter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sppTop, pnlBottom);
      add(sppCenter, BorderLayout.CENTER);
      sppCenter.setDividerLocation(300);
      sppCenter.setContinuousLayout(true);
      sppCenter.setFocusable(false);

      int row;
      JLabel lbl;
      final Insets insets = new Insets(0, 0, 0, 0);

      //
      // Top-left area
      //
      pnlTopLeft.setLayout(new GridBagLayout());
      row = -1;

      lbl = new JLabel(I18n.getMessage("ProductsBrowser.Manufacturer"));
      lbl.setFont(fntCaption);
      pnlTopLeft.add(lbl, new GridBagConstraints(0, ++row, 1, 1, 1, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, insets, 0, 0));

      cboManufacturer = new JComboBox();
      pnlTopLeft.add(cboManufacturer, new GridBagConstraints(0, ++row, 1, 1, 1, 0, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, insets, 0, 0));
      cboManufacturer.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            updateCategories();
         }
      });

      pnlTopLeft.add(new JLabel(), new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, insets, 0, 5));

      lbl = new JLabel(I18n.getMessage("ProductsBrowser.Categories"));
      lbl.setFont(fntCaption);
      pnlTopLeft.add(lbl, new GridBagConstraints(0, ++row, 1, 1, 1, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, insets, 0, 0));

      treCategories = new JTree(trmCategories);
      treCategories.setRootVisible(false);
      treCategories.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
      treCategories.addTreeSelectionListener(new TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            updateCatalogEntries();
         }
      });
      scpCategories = new JScrollPane(treCategories);
      pnlTopLeft.add(scpCategories, new GridBagConstraints(0, ++row, 1, 1, 1, 10, GridBagConstraints.WEST,
            GridBagConstraints.BOTH, insets, 0, 0));

      //
      // Top-right area
      //
      pnlTopRight.setLayout(new GridBagLayout());
      row = -1;

      lbl = new JLabel(I18n.getMessage("ProductsBrowser.CatalogEntries"));
      lbl.setFont(fntCaption);
      pnlTopRight.add(lbl, new GridBagConstraints(0, ++row, 1, 1, 1, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, insets, 0, 0));

      tblEntries = new JTable(0, 2);
      tblEntries.setModel(tbmEntries);
      tblEntries.setColumnSelectionAllowed(false);
      tblEntries.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            updateCatalogEntry();
         }
      });
      tbmEntries.setColumnIdentifiers(new String[] { I18n.getMessage("ProductsBrowser.CatalogEntries.Device"),
            I18n.getMessage("ProductsBrowser.CatalogEntries.Entry") });
      scpEntries = new JScrollPane(tblEntries);
      pnlTopRight.add(scpEntries, new GridBagConstraints(0, ++row, 1, 1, 1, 10, GridBagConstraints.WEST,
            GridBagConstraints.BOTH, insets, 0, 0));

      //
      // Bottom area
      //
      pnlBottom.setLayout(new GridBagLayout());
      row = -1;

      lblEntryName = new JLabel();
      lblEntryName.setFont(fntCaption.deriveFont(fntCaption.getSize() * 1.1f));
      pnlBottom.add(lblEntryName, new GridBagConstraints(0, ++row, 1, 1, 1, 0, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, insets, 0, 4));

      ceDetails = new CatalogEntryDetails();
      ceDetails.setVisible(false);
      ceDetails.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), BorderFactory.createLineBorder(getBackground(), 5)));
      pnlBottom.add(ceDetails, new GridBagConstraints(0, ++row, 1, 1, 1, 10, GridBagConstraints.WEST,
            GridBagConstraints.BOTH, insets, 0, 0));
   }

   @Override
   public void setObject(Object obj)
   {
      final MainWindow mainWin = MainWindow.getInstance();
      productsFactory = null;

      try
      {
         mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

         if (obj instanceof File) productsFactory = Products.getFactory(((File) obj).getAbsolutePath());
         else productsFactory = Products.getFactory();
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, "Failed to open products database");
      }
      finally
      {
         mainWin.setCursor(Cursor.getDefaultCursor());
      }

      ceDetails.setProductsFactory(productsFactory);
      updateContents();
   }

   /**
    * Update the contents of the page.
    */
   @Override
   public void updateContents()
   {
      cboManufacturer.removeAllItems();

      try
      {
         final ManufacturerService dao = productsFactory.getManufacturerService();
         if (dao != null)
         {
            for (Manufacturer manufacturer : dao.getActiveManufacturers())
               cboManufacturer.addItem(manufacturer);
         }

         if (cboManufacturer.getItemCount() > 0) cboManufacturer.setSelectedIndex(0);
      }
      catch (PersistenceException e)
      {
         Dialogs.showExceptionDialog(e, "");
         return;
      }
   }

   /**
    * Update the product categories.
    */
   protected void updateCategories()
   {
      rootCategories.removeAllChildren();

      try
      {
         final FunctionalEntityService dao = productsFactory.getFunctionalEntityService();
         if (dao != null)
         {
            final List<FunctionalEntity> cats = dao.getFunctionalEntities(getSelectedManufacturer());
            final Map<FunctionalEntity, DefaultMutableTreeNode> parentNodes = new HashMap<FunctionalEntity, DefaultMutableTreeNode>();

            for (int tries = 20; tries > 0 && !cats.isEmpty(); --tries)
            {
               for (FunctionalEntity cat : new LinkedList<FunctionalEntity>(cats))
               {
                  DefaultMutableTreeNode parentNode;

                  final FunctionalEntity parent = cat.getParent();
                  if (parent != null)
                  {
                     parentNode = parentNodes.get(parent);
                     if (parentNode == null) continue;
                  }
                  else
                  {
                     parentNode = rootCategories;
                  }

                  final DefaultMutableTreeNode node = new DefaultMutableTreeNode(cat, true);
                  parentNode.add(node);

                  parentNodes.put(cat, node);
                  cats.remove(cat);
               }
            }
         }
      }
      catch (PersistenceException e)
      {
         Dialogs.showExceptionDialog(e, "");
         return;
      }
      finally
      {
         TreeUtils.expandAll(treCategories);
      }
   }

   /**
    * Update the list of catalog entries. Called when the user selects a product category.
    */
   public void updateCatalogEntries()
   {
      tblEntries.removeAll();

      // ETS uses the following fields for its catalog-entries table:
      //
      // virtual_device.name | virtual_device.description | catalog_entry.name
      // | catalog_entry.order_number | application_program.name
      // | application_program.version | catalog_entry.din_flag

      java.util.List<VirtualDevice> devs;

      try
      {
         final VirtualDeviceService virtualDeviceDAO = productsFactory.getVirtualDeviceService();
         final CatalogEntryService catalogEntryDAO = productsFactory.getCatalogEntryService();

         final FunctionalEntity[] ents = getSelectedCategories();
         if (ents == null || virtualDeviceDAO == null || catalogEntryDAO == null)
         {
            tbmEntries.setRowCount(0);
         }
         else
         {
            devs = virtualDeviceDAO.getVirtualDevices(ents);

            tbmEntries.setRowCount(devs.size());

            int row = 0;
            for (VirtualDevice dev : devs)
            {
               tbmEntries.setValueAt(dev, row, 0);
               tbmEntries.setValueAt(dev.getCatalogEntry(), row, 1);
               ++row;
            }
         }
      }
      catch (PersistenceException e)
      {
         Dialogs.showExceptionDialog(e, "");
         return;
      }

      if (tbmEntries.getRowCount() > 0) tblEntries.getSelectionModel().setSelectionInterval(0, 0);

      updateCatalogEntry();
   }

   /**
    * Update the selected catalog entry.
    */
   public void updateCatalogEntry()
   {
      final CatalogEntry entry = getSelectedCatalogEntry();
      final VirtualDevice dev = getSelectedVirtualDevice();

      lblEntryName.setVisible(entry != null);
      ceDetails.setVisible(entry != null);

      if (entry != null && dev != null)
      {
         lblEntryName.setText(I18n.formatMessage("ProductsBrowser.DetailsCaption", new Object[] { dev.getName() }));
         ceDetails.setCatalogEntry(entry);
      }
   }

   /**
    * @return the currently selected manufacturer.
    */
   protected Manufacturer getSelectedManufacturer()
   {
      return (Manufacturer) cboManufacturer.getSelectedItem();
   }

   /**
    * @return a list with the id's of the selected categories.
    */
   public FunctionalEntity[] getSelectedCategories()
   {
      final TreePath[] paths = treCategories.getSelectionModel().getSelectionPaths();
      if (paths == null) return null;

      final List<FunctionalEntity> entities = new LinkedList<FunctionalEntity>();

      for (TreePath path : paths)
      {
         final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
         entities.add((FunctionalEntity) node.getUserObject());

         for (TreeNode child : TreeUtils.getChildTreeNodes(node))
            entities.add((FunctionalEntity) ((DefaultMutableTreeNode) child).getUserObject());
      }

      final FunctionalEntity[] result = new FunctionalEntity[entities.size()];
      entities.toArray(result);
      return result;
   }

   /**
    * @return the selected catalog entry.
    */
   public CatalogEntry getSelectedCatalogEntry()
   {
      final int row = tblEntries.getSelectedRow();
      if (row < 0) return null;
      return (CatalogEntry) tbmEntries.getValueAt(row, 1);
   }

   /**
    * @return the selected {@link VirtualDevice}.
    */
   public VirtualDevice getSelectedVirtualDevice()
   {
      final int row = tblEntries.getSelectedRow();
      if (row < 0) return null;
      return (VirtualDevice) tbmEntries.getValueAt(row, 0);
   }
}
