package org.freebus.fts.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.CatalogGroup;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.ProductDbOld;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.utils.I18n;

/**
 * A browser for a {@link ProductDbOld} products-database.
 */
public class ProductsTab extends TabPage
{
   private ProductDbOld productDb = null;
   private final List lstManufacturers;
   private final Tree treCategories;
   private final Table tblCatalog, tblApplications;
   private final Group grpDetails, grpApplications;
   private final CatalogEntryWidget cewDetails;
   private final Label lblSelProduct;
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
   }

   /**
    * Set the object that the tab-page shows.
    */
   @Override
   public void setObject(Object o)
   {
      productDb = (ProductDbOld) o;
      setTitle(productDb.getCreationDate());
      updateContents();
   }
   
   /**
    * Update the widget's contents. Called when the displayed
    * object is changed. The default implementation is empty.
    */
   @Override
   public void updateContents()
   {
      if (productDb != null)
         updateManufacturers();
   }

   /**
    * Returns a list with the selected manufacturers.
    */
   public Set<Manufacturer> getSelectedManufacturers()
   {
      final String[] sel = lstManufacturers.getSelection();

      final Set<Manufacturer> result = new HashSet<Manufacturer>();
      for (int i=sel.length-1; i>=0; --i)
      {
         final Integer id = (Integer) lstManufacturers.getData(sel[i]);
         result.add(productDb.getManufacturer(id));
      }
      return result;
   }

   /**
    * Returns a list with the selected categories.
    */
   public Set<CatalogGroup> getSelectedCategories()
   {
      final TreeItem[] selTopLevel = treCategories.getSelection();

      final Vector<TreeItem> sel = new Vector<TreeItem>((selTopLevel.length<<2) + 10);
      for (TreeItem item: selTopLevel)
      {
         sel.add(item);
         getChildren(item, sel);
      }

      final Set<CatalogGroup> result = new HashSet<CatalogGroup>();
      for (int i=sel.size()-1; i>=0; --i)
         result.add((CatalogGroup) sel.get(i).getData());

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
    */
   public void updateManufacturers()
   {
      lstManufacturers.removeAll();

      final Set<Integer> ids = productDb.getManufacturerKeys();
      for (final Integer id: ids)
      {
         final Manufacturer manufacturer = productDb.getManufacturer(id);
         lstManufacturers.add(manufacturer.getName());
         lstManufacturers.setData(manufacturer.getName(), id);
      }

      if (!ids.isEmpty()) lstManufacturers.select(0);
      updateCategories();
   }

   /**
    * Update the list of categories.
    */
   public void updateCategories()
   {
      treCategories.removeAll();
      final HashMap<CatalogGroup,TreeItem> treeItems = new HashMap<CatalogGroup,TreeItem>();
      final TreeMap<String,CatalogGroup> catSorted = new TreeMap<String,CatalogGroup>();
      CatalogGroup parentCat, cat;
      TreeItem item;

      for (final Manufacturer manufacturer: getSelectedManufacturers())
      {
         final Set<Integer> ids = manufacturer.getFunctionalEntityKeys();
         for (final Integer id: ids)
         {
            cat = manufacturer.getFunctionalEntity(id);
            catSorted.put(cat.getName()+'#'+id.toString(), cat);
         }
      }

      // Process all top-level categories
      for (String key: catSorted.keySet())
      {
         cat = catSorted.get(key);
         if (cat.getParent()!=null) continue;

         item = new TreeItem(treCategories, SWT.FLAT);
         item.setText(cat.getName());
         item.setData(cat);
         treeItems.put(cat, item);
      }

      // Process all non-top-level categories
      for (String key: catSorted.keySet())
      {
         cat = catSorted.get(key);
         parentCat = cat.getParent();
         if (parentCat==null) continue;
   
         item = new TreeItem(treeItems.get(parentCat), SWT.FLAT);
         item.setText(cat.getName());
         item.setData(cat);
         treeItems.put(cat, item);
      }
      
      if (!catSorted.isEmpty()) treCategories.select(treCategories.getItem(0));
      updateCatalog();
   }

   /**
    * Update the list of catalog entries.
    */
   public void updateCatalog()
   {
      final Set<Manufacturer> manufacturers = getSelectedManufacturers();
      final Set<CatalogGroup> cats = getSelectedCategories();
      final TreeMap<String,CatalogEntry> matches = new TreeMap<String,CatalogEntry>();

      VirtualDevice virtualDevice;
      CatalogEntry catalogEntry;

      tblCatalog.removeAll();

      final int numCatEntries = productDb.getNumCatalogEntries();
      for (int id=0; id<numCatEntries; ++id)
      {
         catalogEntry = productDb.getCatalogEntry(id);
         if (!manufacturers.contains(catalogEntry.getManufacturer())) continue;

         final int numVirtDev = catalogEntry.getVirtualDevicesCount();
         for (int i=0; i<numVirtDev; ++i)
         {
            virtualDevice = catalogEntry.getVirtualDevice(i);
            if (virtualDevice==null || !cats.contains(virtualDevice.getFunctionalEntity())) continue;

            matches.put(catalogEntry.getName(), catalogEntry);
            break;
         }
      }
      
      for (String key: matches.keySet())
      {
         catalogEntry = matches.get(key);
         final TableItem item = new TableItem(tblCatalog, SWT.FLAT);
         item.setText(catalogEntry.getName());
         item.setData(catalogEntry);
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
         return;
      }

      final CatalogEntry catalogEntry = (CatalogEntry) sel[0].getData();
      lblSelProduct.setText(I18n.getMessage("ProductsTab.Selected_Product").replace("%1", catalogEntry.getName()));
      cewDetails.setCatalogEntry(catalogEntry);

      tblApplications.removeAll();
      
   }
}
