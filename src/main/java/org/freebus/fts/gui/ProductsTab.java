package org.freebus.fts.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.ProductDb;
import org.freebus.fts.products.ProductFilter;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.widgets.CatalogEntryWidget;

/**
 * A browser for a {@link ProductDb} products-database.
 */
public class ProductsTab extends TabPage
{
   private ProductDb productDb = null;
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
      FillLayout fillLayout;
      
      final FontData curFontData = getFont().getFontData()[0];
      fntCaption = new Font(Display.getCurrent(), new FontData(curFontData.getName(), (int)(curFontData.getHeight()*1.2), SWT.BOLD));

      Group grpManufacturer = new Group(this, SWT.BORDER);
      grpManufacturer.setText(I18n.getMessage("ProductsTab.Manufacturer"));
      grpManufacturer.setLayout(new FillLayout());
      formData = new FormData();
      formData.width = 150;
      formData.bottom = new FormAttachment(35);
      formData.left = new FormAttachment(1);
      formData.top = new FormAttachment(1);
      grpManufacturer.setLayoutData(formData);
      lstManufacturers = new List(grpManufacturer, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
      lstManufacturers.addListener(SWT.Selection, new Listener() { public void handleEvent(Event e) { updateCategories(); } });

      Group grpCategories = new Group(this, SWT.BORDER);
      grpCategories.setText(I18n.getMessage("ProductsTab.Categories"));
      grpCategories.setSize(300, 200);
      grpCategories.setLayout(new FillLayout());
      formData = new FormData();
      formData.width = 300;
      formData.bottom = new FormAttachment(35);
      formData.left = new FormAttachment(grpManufacturer, 1);
      formData.top = new FormAttachment(1);
      grpCategories.setLayoutData(formData);
      treCategories = new Tree(grpCategories, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
      treCategories.addListener(SWT.Selection, new Listener() { public void handleEvent(Event e) { updateCatalog(); } });

      Group grpCatalog = new Group(this, SWT.BORDER);
      grpCatalog.setText(I18n.getMessage("ProductsTab.Catalog"));
      grpCatalog.setLayout(new FillLayout());
      formData = new FormData();
      formData.top = new FormAttachment(1);
      formData.bottom = new FormAttachment(35);
      formData.left = new FormAttachment(grpCategories, 1);
      formData.right = new FormAttachment(99);
      grpCatalog.setLayoutData(formData);
      tblCatalog = new Table(grpCatalog, SWT.BORDER|SWT.SINGLE|SWT.V_SCROLL);
      tblCatalog.addListener(SWT.Selection, new Listener() { public void handleEvent(Event e) { updateDetails(); } });

      final Label lblSep = new Label(this, SWT.SEPARATOR|SWT.HORIZONTAL);
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

      grpApplications = new Group(this, SWT.BORDER);
      grpApplications.setText(I18n.getMessage("ProductsTab.Applications"));
      grpApplications.setLayout(new FillLayout());
      formData = new FormData();
      formData.top = new FormAttachment(lblSelProduct, 5);
      formData.bottom = new FormAttachment(99);
      formData.left = new FormAttachment(1);
      formData.width = 400;
      grpApplications.setLayoutData(formData);
      tblApplications = new Table(grpApplications, SWT.BORDER|SWT.SINGLE|SWT.V_SCROLL);
//      tblApplications.addListener(SWT.Selection, new Listener() { public void handleEvent(Event e) { updateDetails(); } });

      
      grpDetails = new Group(this, SWT.BORDER|SWT.SINGLE);
      grpDetails.setSize(600, 300);
      fillLayout = new FillLayout();
      fillLayout.marginHeight = 4;
      fillLayout.marginWidth = 2;
      grpDetails.setLayout(fillLayout);
//      grpDetails.setText(I18n.getMessage("ProductsTab.Product_Caption"));
      formData = new FormData();
      formData.top = new FormAttachment(lblSelProduct, 5);
      formData.bottom = new FormAttachment(99);
      formData.left = new FormAttachment(grpApplications, 1);
      formData.right = new FormAttachment(99);
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
      productDb = (ProductDb) o;
//      setTitle(productDb.getName());
      updateContents();
   }
   
   /**
    * Update the widget's contents. Called when the displayed
    * object is changed. The default implementation is empty.
    */
   @Override
   public void updateContents()
   {
      if (productDb != null) try
      {
         updateManufacturers();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * @return a list with the id's of the selected manufacturers.
    * The returned list is empty if no manufacturer is selected.
    */
   public int[] getSelectedManufacturers()
   {
      final String[] sel = lstManufacturers.getSelection();

      final int[] result = new int[sel.length];
      for (int i=sel.length-1; i>=0; --i)
         result[i] = (Integer) lstManufacturers.getData(sel[i]);

      return result;
   }

   /**
    * Returns a list with the id's of the selected categories.
    */
   public int[] getSelectedCategories()
   {
      final TreeItem[] selTopLevel = treCategories.getSelection();

      final Vector<TreeItem> sel = new Vector<TreeItem>((selTopLevel.length<<2) + 10);
      for (TreeItem item: selTopLevel)
      {
         sel.add(item);
         getChildren(item, sel);
      }

      final int[] result = new int[sel.size()];
      for (int i = sel.size() - 1; i >= 0; --i)
         result[i] = (Integer) sel.get(i).getData();

      return result;
   }

   /**
    * Recursively adds all children of the given tree-item to the vector.
    */
   protected void getChildren(TreeItem item, Vector<TreeItem> children)
   {
      for (TreeItem child: item.getItems())
      {
         children.add(child);
         getChildren(child, children);
      }
   }

   /**
    * Update the list of manufacturers.
    * @throws IOException 
    */
   public void updateManufacturers() throws IOException
   {
      lstManufacturers.removeAll();

      final Map<Integer, String> manufacturers = productDb.getManufacturers();
      final Map<String, Integer> manufacturersSorted = new TreeMap<String, Integer>();

      for (final Integer id: manufacturers.keySet())
         manufacturersSorted.put(manufacturers.get(id), id);

      for (final String name: manufacturersSorted.keySet())
      {
         lstManufacturers.add(name);
         lstManufacturers.setData(name, manufacturersSorted.get(name));
      }

      if (!manufacturersSorted.isEmpty()) lstManufacturers.select(0);
      updateCategories();
   }

   /**
    * Update the list of categories.
    */
   public void updateCategories()
   {
      treCategories.removeAll();
      final HashMap<Integer,TreeItem> treeItems = new HashMap<Integer,TreeItem>();
      FunctionalEntity cat;
      TreeItem item, parentItem;

      final ProductFilter filter = new ProductFilter();
      filter.manufacturers = getSelectedManufacturers();

      java.util.List<FunctionalEntity> cats = null;
      try
      {
         cats = productDb.getFunctionalEntities(filter);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return;
      }

      final Map<String, FunctionalEntity> catSorted = new TreeMap<String, FunctionalEntity>();
      for (final FunctionalEntity functionalEntity: cats)
         catSorted.put(functionalEntity.getName(), functionalEntity);

      // Process all categories, as long as there are categories to be added
      // to the tree. Found categories are removed from the catSorted set.
      for (int tries = 10; tries > 0 && !catSorted.isEmpty(); --tries)
      {
         final String[] names = new String[catSorted.size()];
         catSorted.keySet().toArray(names);

         for (int i = 0; i < names.length; ++i)
         {
            final String name = names[i];
            cat = catSorted.get(name);
            
            final int parentId = cat.getParentId();
            if (parentId > 0)
            {
               parentItem = treeItems.get(parentId);
               if (parentItem == null) continue; // try next time
            }
            else parentItem = null;
   
            if (parentItem == null) item = new TreeItem(treCategories, SWT.FLAT);
            else item = new TreeItem(parentItem, SWT.FLAT);

            item.setText(cat.getName());
            item.setData(cat.getId());

            treeItems.put(cat.getId(), item);
            catSorted.remove(name);
         }
      }
      
      if (treCategories.getItemCount() > 0) treCategories.select(treCategories.getItem(0));
      updateCatalog();
   }

   /**
    * Update the list of catalog entries.
    */
   public void updateCatalog()
   {
      tblCatalog.removeAll();

      final TreeMap<String,VirtualDevice> matches = new TreeMap<String,VirtualDevice>();

      final ProductFilter filter = new ProductFilter();
      filter.manufacturers = getSelectedManufacturers();
      filter.functionalEntities = getSelectedCategories();

      try
      {
         for (VirtualDevice dev: productDb.getVirtualDevices(filter))
            matches.put(dev.getName(), dev);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return;
      }

//      final int numCatEntries = productDb.getNumCatalogEntries();
//      for (int id=0; id<numCatEntries; ++id)
//      {
//         catalogEntry = productDb.getCatalogEntry(id);
//         if (!manufacturers.contains(catalogEntry.getManufacturer())) continue;
//
//         final int numVirtDev = catalogEntry.getVirtualDevicesCount();
//         for (int i=0; i<numVirtDev; ++i)
//         {
//            virtualDevice = catalogEntry.getVirtualDevice(i);
//            if (virtualDevice==null || !cats.contains(virtualDevice.getFunctionalEntity())) continue;
//
//            matches.put(catalogEntry.getName(), catalogEntry);
//            break;
//         }
//      }

      for (String key: matches.keySet())
      {
         VirtualDevice dev = matches.get(key);
         final TableItem item = new TableItem(tblCatalog, SWT.FLAT);
         item.setText(dev.getName());
         item.setData(dev.getId());
      }

      updateDetails();
   }
   
   /**
    * Update the details about the selected catalog entry.
    */
   public void updateDetails()
   {
      final TableItem[] sel = tblCatalog.getSelection();
      final boolean isVisible = sel.length>0;

      grpDetails.setVisible(isVisible);
      grpApplications.setVisible(isVisible);
      lblSelProduct.setVisible(isVisible);

      if (sel.length<=0)
      {
         cewDetails.setCatalogEntry(null);
         lblDescription.setText("");
         return;
      }
      
//      final int devId = (Integer) sel[0].getData();
//      VirtualDevice dev = null;
//      try
//      {
//         dev = productDb.getVirtualDevice(devId);
//      }
//      catch (IOException e1)
//      {
//         e1.printStackTrace();
//         return;
//      }
//      final String devLabel = sel[0].getText();
//
////      final CatalogEntry catalogEntry = (CatalogEntry) sel[0].getData();
//      lblSelProduct.setText(I18n.getMessage("ProductsTab.Selected_Product").replace("%1", devLabel));
////      cewDetails.setCatalogEntry(catalogEntry);
//      try
//      {
//         lblDescription.setText(productDb.getProductDescription(dev.getCatalogEntryId()));
//      }
//      catch (IOException e)
//      {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//      }
//
//      tblApplications.removeAll();
   }
}
