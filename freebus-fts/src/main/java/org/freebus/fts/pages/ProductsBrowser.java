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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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
import org.freebus.fts.pages.productsbrowser.FunctionalEntityTreeNode;
import org.freebus.fts.pages.productsbrowser.ManufacturerItem;
import org.freebus.fts.pages.productsbrowser.ProductsListCellRenderer;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.ProductsManager;
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

   private final JList lstEntries;
   private final JScrollPane scpEntries;
   private DefaultListModel lmEntries = new DefaultListModel();

   private final JLabel lblEntryName;
   private final CatalogEntryDetails ceDetails;

   private final Box boxBottom = Box.createHorizontalBox();
   private final JCheckBox cbxImport = new JCheckBox(I18n.getMessage("ProductsBrowser.ImportOption"));
   private final JButton btnImport = new JButton(I18n.getMessage("ProductsBrowser.ImportButton"));
   private final Set<VirtualDevice> importDevices = new HashSet<VirtualDevice>();
   private boolean importMode = false;

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
      final JPanel pnlCenter = new JPanel();

      sppTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlTopLeft, pnlTopRight);
      sppTop.setDividerLocation(300);
      sppTop.setContinuousLayout(true);
      sppTop.setFocusable(false);

      sppCenter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sppTop, pnlCenter);
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
      cboManufacturer.setModel(new DefaultComboBoxModel());
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

      lstEntries = new JList(lmEntries);
      lstEntries.setCellRenderer(new ProductsListCellRenderer());
      lstEntries.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            updateCatalogEntry();
         }
      });
//      tbmEntries.setColumnIdentifiers(new String[] { I18n.getMessage("ProductsBrowser.CatalogEntries.Device"),
//            I18n.getMessage("ProductsBrowser.CatalogEntries.Entry") });
      scpEntries = new JScrollPane(lstEntries);
      pnlTopRight.add(scpEntries, new GridBagConstraints(0, ++row, 1, 1, 1, 10, GridBagConstraints.WEST,
            GridBagConstraints.BOTH, insets, 0, 0));

      //
      // Center area
      //
      pnlCenter.setLayout(new GridBagLayout());
      row = -1;

      lblEntryName = new JLabel();
      lblEntryName.setFont(fntCaption.deriveFont(fntCaption.getSize() * 1.1f));
      pnlCenter.add(lblEntryName, new GridBagConstraints(0, ++row, 1, 1, 1, 0, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, insets, 0, 4));

      ceDetails = new CatalogEntryDetails();
      ceDetails.setVisible(false);
      ceDetails.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), BorderFactory.createLineBorder(getBackground(), 5)));
      pnlCenter.add(ceDetails, new GridBagConstraints(0, ++row, 1, 1, 1, 10, GridBagConstraints.WEST,
            GridBagConstraints.BOTH, insets, 0, 0));

      //
      // Bottom area
      //
      add(boxBottom, BorderLayout.SOUTH);
      boxBottom.setVisible(false);
      boxBottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
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
    * Enable the import mode. In import mode the import buttons on the bottom of the page are
    * enabled, and the {@link import} callback is called when the import button is clicked.
    */
   protected void enableImportMode()
   {
      importMode = true;
      boxBottom.setVisible(ceDetails.isVisible());
   }

   @Override
   public void setObject(Object obj)
   {
      final MainWindow mainWin = MainWindow.getInstance();
      productsFactory = null;

      try
      {
         mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

         if (obj instanceof File) productsFactory = ProductsManager.getFactory(((File) obj).getAbsolutePath());
         else productsFactory = ProductsManager.getFactory();
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
    * @return The products factory that contains the entities of the products that are displayed. 
    */
   public ProductsFactory getProductsFactory()
   {
      return productsFactory;
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
            final List<Manufacturer> manus = dao.getActiveManufacturers();

            final ManufacturerItem items[] = new ManufacturerItem[manus.size()];
            for (int i = manus.size() - 1; i >= 0; --i)
               items[i] = new ManufacturerItem(manus.get(i));
            Arrays.sort(items);

            for (ManufacturerItem item: items)
               cboManufacturer.addItem(item);
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

                  final DefaultMutableTreeNode node = new FunctionalEntityTreeNode(cat, true);
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
    * Update the list of catalog entries / virtual devices. 
    * Called when the user selects a product category.
    */
   public void updateCatalogEntries()
   {
      lmEntries.clear();
      lstEntries.removeAll();

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
         if (ents != null && virtualDeviceDAO != null && catalogEntryDAO != null)
         {
            devs = virtualDeviceDAO.getVirtualDevices(ents);

            lmEntries.setSize(devs.size());

            int row = -1;
            for (VirtualDevice dev : devs)
               lmEntries.set(++row, dev);
         }
      }
      catch (PersistenceException e)
      {
         Dialogs.showExceptionDialog(e, "");
         return;
      }

      if (lmEntries.getSize() > 0) lstEntries.getSelectionModel().setSelectionInterval(0, 0);

      updateCatalogEntry();
   }

   /**
    * Update the selected catalog entry.
    */
   public void updateCatalogEntry()
   {
      final VirtualDevice dev = getSelectedVirtualDevice();
      final CatalogEntry entry = (dev == null ? null : dev.getCatalogEntry());
      final boolean valid = dev != null && entry != null;

      lblEntryName.setVisible(valid);
      ceDetails.setVisible(valid);
      boxBottom.setVisible(importMode && valid);

      if (valid)
      {
         lblEntryName.setText(I18n.formatMessage("ProductsBrowser.DetailsCaption", new Object[] { dev.getName() }));
         ceDetails.setCatalogEntry(entry);

         cbxImport.setSelected(importDevices.contains(dev));
      }
   }

   /**
    * Import the marked virtual devices. Called when the user clicks the import button.
    * To be implemented in subclasses - this implementation is empty.
    */
   protected void importProducts(Set<VirtualDevice> virtualDevices)
   {
   }

   /**
    * @return the currently selected manufacturer.
    */
   protected Manufacturer getSelectedManufacturer()
   {
      return ((ManufacturerItem) cboManufacturer.getSelectedItem()).getManufacturer();
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
//   public CatalogEntry getSelectedCatalogEntry()
//   {
//      return (CatalogEntry) lstEntries.getSelectedValue();
//   }

   /**
    * @return the selected {@link VirtualDevice}.
    */
   public VirtualDevice getSelectedVirtualDevice()
   {
      return (VirtualDevice) lstEntries.getSelectedValue();
   }
}
