package org.freebus.fts.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.freebus.fts.dialogs.ExceptionDialog;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.dao.CatalogEntryDAO;
import org.freebus.fts.products.dao.DAOException;
import org.freebus.fts.products.dao.FunctionalEntityDAO;
import org.freebus.fts.products.dao.ProductDescriptionDAO;
import org.freebus.fts.products.dao.ProductsDAOFactory;
import org.freebus.fts.products.dao.VirtualDeviceDAO;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.widgets.CatalogEntryWidget;

/**
 * A browser for the {@link ProductDb} products-database.
 */
public class ProductsTab extends TabPage
{
   private ProductsDAOFactory productsFactory;
   private final List lstManufacturers;
   private final Tree treCategories;
   private final Table tblCatalog, tblApplications;
   private final Group grpDetails, grpApplications;
   private final CatalogEntryWidget cewDetails;
   private final Label lblSelProduct, lblDescription;
   private final Font fntCaption;

   /**
    * Create a new products-tab.
    */
   public ProductsTab(Composite parent)
   {
      super(parent);
      setTitle(I18n.getMessage("Products_Tab"));
      setPlace(SWT.CENTER);

      FormData formData;

      final FontData curFontData = getFont().getFontData()[0];
      fntCaption = new Font(Display.getCurrent(), new FontData(curFontData.getName(),
            (int) (curFontData.getHeight() * 1.2), SWT.BOLD));

      Group grpManufacturer = new Group(this, SWT.FLAT);
      grpManufacturer.setText(I18n.getMessage("ProductsTab.Manufacturer"));
      grpManufacturer.setLayout(new FillLayout());
      formData = new FormData();
      formData.width = 150;
      formData.bottom = new FormAttachment(35);
      formData.left = new FormAttachment(1);
      formData.top = new FormAttachment(1);
      grpManufacturer.setLayoutData(formData);
      lstManufacturers = new List(grpManufacturer, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
      lstManufacturers.addListener(SWT.Selection, new Listener()
      {
         public void handleEvent(Event e)
         {
            updateCategories();
         }
      });

      Group grpCategories = new Group(this, SWT.FLAT);
      grpCategories.setText(I18n.getMessage("ProductsTab.Categories"));
      grpCategories.setSize(300, 200);
      grpCategories.setLayout(new FillLayout());
      formData = new FormData();
      formData.width = 250;
      formData.bottom = new FormAttachment(35);
      formData.left = new FormAttachment(grpManufacturer, 1);
      formData.top = new FormAttachment(1);
      grpCategories.setLayoutData(formData);
      treCategories = new Tree(grpCategories, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
      treCategories.addListener(SWT.Selection, new Listener()
      {
         public void handleEvent(Event e)
         {
            updateCatalogEntries();
         }
      });

      Group grpCatalog = new Group(this, SWT.FLAT);
      grpCatalog.setText(I18n.getMessage("ProductsTab.Catalog"));
      grpCatalog.setLayout(new FillLayout());
      formData = new FormData();
      formData.top = new FormAttachment(1);
      formData.bottom = new FormAttachment(35);
      formData.left = new FormAttachment(grpCategories, 1);
      formData.right = new FormAttachment(99);
      grpCatalog.setLayoutData(formData);
      tblCatalog = new Table(grpCatalog, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);
      new TableColumn(tblCatalog, SWT.LEFT);
      new TableColumn(tblCatalog, SWT.LEFT);
      tblCatalog.addListener(SWT.Selection, new Listener()
      {
         public void handleEvent(Event e)
         {
            updateDetails();
         }
      });

      final Label lblSep = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
      formData = new FormData();
      formData.top = new FormAttachment(grpManufacturer, 5);
      formData.height = 5;
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      lblSep.setLayoutData(formData);

      lblSelProduct = new Label(this, SWT.LEFT);
      lblSelProduct.setFont(fntCaption);
      formData = new FormData();
      formData.top = new FormAttachment(lblSep, 5);
      formData.left = new FormAttachment(1);
      formData.right = new FormAttachment(99);
      lblSelProduct.setLayoutData(formData);

      grpApplications = new Group(this, SWT.FLAT);
      grpApplications.setText(I18n.getMessage("ProductsTab.Applications"));
      grpApplications.setLayout(new FillLayout());
      formData = new FormData();
      formData.top = new FormAttachment(lblSelProduct, 5);
      formData.bottom = new FormAttachment(99);
      formData.left = new FormAttachment(1);
      formData.width = 400;
      grpApplications.setLayoutData(formData);
      tblApplications = new Table(grpApplications, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);
      // tblApplications.addListener(SWT.Selection, new Listener() { public void
      // handleEvent(Event e) { updateDetails(); } });

      grpDetails = new Group(this, SWT.FLAT | SWT.SINGLE);
      RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
      rowLayout.fill = true;
      rowLayout.justify = true;
      rowLayout.spacing = 4;
      grpDetails.setLayout(rowLayout);
      // grpDetails.setText(I18n.getMessage("ProductsTab.Product_Caption"));
      formData = new FormData();
      formData.top = new FormAttachment(lblSelProduct, 5);
      formData.bottom = new FormAttachment(99);
      formData.left = new FormAttachment(grpApplications, 1);
      formData.width = 400;
      grpDetails.setLayoutData(formData);
      grpDetails.setVisible(false);

      cewDetails = new CatalogEntryWidget(grpDetails, SWT.BORDER, false);

      lblDescription = new Label(grpDetails, SWT.BORDER);
   }

   /**
    * Set the {@link ProductDb} product-database that the tab-page shows.
    */
   @Override
   public void setObject(Object o)
   {
      productsFactory = (ProductsDAOFactory) o;
      cewDetails.setDAOFactory(productsFactory);
      updateContents();
   }

   /**
    * Update the widget's contents. Called when the displayed object is changed.
    * The default implementation is empty.
    */
   @Override
   public void updateContents()
   {
      if (productsFactory != null) updateManufacturers();
   }

   /**
    * @return the selected manufacturer, or null if none is selected.
    */
   public Manufacturer getSelectedManufacturer()
   {
      final String[] sel = lstManufacturers.getSelection();
      if (sel == null) return null;

      return (Manufacturer) lstManufacturers.getData(sel[0]);
   }

   /**
    * Returns a list with the id's of the selected categories.
    */
   public FunctionalEntity[] getSelectedCategories()
   {
      final TreeItem[] selTopLevel = treCategories.getSelection();

      final Vector<TreeItem> sel = new Vector<TreeItem>((selTopLevel.length << 2) + 10);
      for (TreeItem item : selTopLevel)
      {
         sel.add(item);
         getChildren(item, sel);
      }

      final FunctionalEntity[] result = new FunctionalEntity[sel.size()];
      for (int i = sel.size() - 1; i >= 0; --i)
         result[i] = (FunctionalEntity) sel.get(i).getData();

      return result;
   }

   /**
    * Recursively adds all children of the given tree-item to the vector.
    */
   protected void getChildren(TreeItem item, Vector<TreeItem> children)
   {
      for (TreeItem child : item.getItems())
      {
         children.add(child);
         getChildren(child, children);
      }
   }

   /**
    * Update the list of manufacturer.
    * 
    * @throws IOException
    */
   public void updateManufacturers()
   {
      lstManufacturers.removeAll();

      java.util.List<Manufacturer> manufacturers;
      try
      {
         manufacturers = productsFactory.getManufacturerDAO().getActiveManufacturers();
      }
      catch (DAOException e)
      {
         new ExceptionDialog(e);
         return;
      }

      for (final Manufacturer m : manufacturers)
      {
         lstManufacturers.add(m.getName());
         lstManufacturers.setData(m.getName(), m);
      }

      if (!manufacturers.isEmpty()) lstManufacturers.select(0);
      updateCategories();
   }

   /**
    * Update the list of categories. Called when the user clicks a manufacturer.
    */
   public void updateCategories()
   {
      treCategories.removeAll();
      final HashMap<Integer, TreeItem> treeItems = new HashMap<Integer, TreeItem>();
      TreeItem item, parentItem;

      java.util.List<FunctionalEntity> cats;
      try
      {
         final FunctionalEntityDAO dao = productsFactory.getFunctionalEntityDAO();
         if (dao == null) return;
         cats = dao.getFunctionalEntities(getSelectedManufacturer());
      }
      catch (DAOException e)
      {
         new ExceptionDialog(e);
         return;
      }

      final Set<Integer> ids = new HashSet<Integer>();
      for (final FunctionalEntity cat : cats)
         ids.add(cat.getId());

      // Process all categories, as long as there are categories to be added
      // to the tree. Found categories are removed from the catSorted set.
      for (int tries = 10; tries > 0 && !cats.isEmpty(); --tries)
      {
         for (final FunctionalEntity cat : new LinkedList<FunctionalEntity>(cats))
         {
            final int parentId = cat.getParentId();
            if (parentId > 0 && ids.contains(parentId))
            {
               parentItem = treeItems.get(parentId);
               if (parentItem == null) continue; // try next time
            }
            else parentItem = null;

            if (parentItem == null) item = new TreeItem(treCategories, SWT.FLAT);
            else item = new TreeItem(parentItem, SWT.FLAT);

            item.setText(cat.getName());
            item.setData(cat);

            treeItems.put(cat.getId(), item);
            cats.remove(cat);
         }
      }

      if (treCategories.getItemCount() > 0) treCategories.select(treCategories.getItem(0));
      updateCatalogEntries();
   }

   /**
    * Update the list of catalog entries. Called when the user selects a product
    * category.
    */
   public void updateCatalogEntries()
   {
      tblCatalog.removeAll();

      // ETS uses the following fields for its entries table:
      //
      // virtual_device.name | virtual_device.description | catalog_entry.name
      // | catalog_entry.order_number | application_program.name
      // | application_program.version | catalog_entry.din_flag

      java.util.List<VirtualDevice> devs;

      try
      {
         final VirtualDeviceDAO virtualDeviceDAO = productsFactory.getVirtualDeviceDAO();
         final CatalogEntryDAO catalogEntryDAO = productsFactory.getCatalogEntryDAO();

         if (virtualDeviceDAO == null || catalogEntryDAO == null) return;
         devs = virtualDeviceDAO.getVirtualDevices(getSelectedCategories());

         for (VirtualDevice dev : devs)
         {
            final TableItem item = new TableItem(tblCatalog, SWT.FLAT);
            item.setText(0, dev.getName());
            item.setData(dev);

            CatalogEntry entry = catalogEntryDAO.getCatalogEntry(dev.getCatalogEntryId());
            item.setText(1, entry.getName());
         }
      }
      catch (DAOException e)
      {
         new ExceptionDialog(e);
         return;
      }

      for (TableColumn tableColumn : tblCatalog.getColumns())
         tableColumn.pack();

      if (!devs.isEmpty()) tblCatalog.select(0);

      updateDetails();
   }

   /**
    * Update the details about the selected catalog entry.
    */
   public void updateDetails()
   {
      final TableItem[] sel = tblCatalog.getSelection();
      final boolean isVisible = sel.length > 0;

      grpDetails.setVisible(isVisible);
      grpApplications.setVisible(isVisible);
      lblSelProduct.setVisible(isVisible);

      tblApplications.removeAll();

      if (sel.length <= 0)
      {
         cewDetails.setCatalogEntry(null);
         lblDescription.setText("");
         return;
      }

      final VirtualDevice dev = (VirtualDevice) sel[0].getData();

      try
      {
         final CatalogEntry catalogEntry = productsFactory.getCatalogEntryDAO()
               .getCatalogEntry(dev.getCatalogEntryId());
         lblSelProduct.setText(I18n.getMessage("ProductsTab.Selected_Product").replace("%1", dev.getName()));
         cewDetails.setCatalogEntry(catalogEntry);

         final ProductDescriptionDAO productDescriptionDAO = productsFactory.getProductDescriptionDAO();
         final StringBuilder sb = new StringBuilder();

         if (productDescriptionDAO != null)
         {
            for (String line : productDescriptionDAO.getProductDescription(catalogEntry))
            {
               sb.append(line);
               sb.append("\n");
            }
         }
         lblDescription.setText(sb.toString());
      }
      catch (DAOException e)
      {
         new ExceptionDialog(e);
         return;
      }
   }
}
