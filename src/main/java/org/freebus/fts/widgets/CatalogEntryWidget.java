package org.freebus.fts.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.utils.I18n;

public class CatalogEntryWidget extends Composite
{
   private final boolean editable;
   private CatalogEntry catalogEntry = null;
   private final Text entName, entManufacturer, entColor, entWidthModules, entWidthMM;
   private final Text entOrderNumber, entSeries;
   private final Font fntLabels;

   /**
    * Create a widget that displays details about a {@link CatalogEntry}
    */
   public CatalogEntryWidget(Composite parent, int style, boolean editable)
   {
      super(parent, style);
      this.editable = editable;

      setLayout(new GridLayout(2, false));

      final FontData curFontData = getFont().getFontData()[0];
      fntLabels = new Font(Display.getCurrent(), new FontData(curFontData.getName(), curFontData.getHeight(), SWT.BOLD));
      
      entName = createField(I18n.getMessage("Name"));
      entManufacturer = createField(I18n.getMessage("Manufacturer"));
      entOrderNumber = createField(I18n.getMessage("Order_number"));
      entSeries = createField(I18n.getMessage("Series"));
      entColor = createField(I18n.getMessage("Color"));
      entWidthModules = createField(I18n.getMessage("Width_in_modules"));
      entWidthMM = createField(I18n.getMessage("Width_in_mm"));

      Label lbl = new Label(this, SWT.FLAT);
      lbl.setText(I18n.getMessage("For_DIN_rail")+":");
      lbl.setFont(fntLabels);
      lbl.pack();

      
   }

   /**
    * Create a label with a data field. 
    */
   private Text createField(String label)
   {
      final Label lbl = new Label(this, SWT.FLAT);
      lbl.setText(label+":");
      lbl.setFont(fntLabels);
      lbl.pack();

      final Text entry = new Text(this, editable ? SWT.BORDER : SWT.FLAT);
      entry.setEditable(editable);
      entry.setText(" ");
      if (!editable)
      {
         entry.setForeground(getForeground());
         entry.setBackground(getBackground());
      }
      entry.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

      return entry;
   }
   
   /**
    * Set the catalog-entry that is displayed.
    * Calls {@link #updateContents}.
    */
   public void setCatalogEntry(CatalogEntry catalogEntry)
   {
      this.catalogEntry = catalogEntry;
      updateContents();
   }
   
   /**
    * Update the contents of the widget.
    */
   public void updateContents()
   {
      if (catalogEntry==null)
      {
         entName.setText("");
         entColor.setText("");
         return;
      }

      entName.setText(catalogEntry.getName());
//      entManufacturer.setText(catalogEntry.getManufacturer().getName());
      entColor.setText(catalogEntry.getColor());
      entWidthModules.setText(Integer.toString(catalogEntry.getWidthModules()));
      entWidthMM.setText(Integer.toString(catalogEntry.getWidthMM()));
      entOrderNumber.setText(catalogEntry.getOrderNumber());
      entSeries.setText(catalogEntry.getSeries());
   }
}
