package org.freebus.fts.components;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.persistence.NoResultException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import org.freebus.fts.I18n;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.ProductDescription;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProductDescriptionService;
import org.freebus.fts.products.services.ProductsFactory;

/**
 * A widget that contains details about a {@link CatalogEntry}.
 */
public final class CatalogEntryDetails extends JPanel
{
   private static final long serialVersionUID = -3649914854489876707L;

   private final Font fntCaption = getFont().deriveFont(Font.BOLD);
   private final Insets insets = new Insets(0, 0, 0, 0);
   private final Insets labelInsets = new Insets(0, 0, 0, 8);
   private final JLabel entName, entManufacturer, entOrderNumber, entSeries, entColor, entWidthModules;
   private final JLabel entWidthMM, entDinRail;
   private final JTextArea entDescription;

   private ProductDescriptionService productDescriptionService;

   /**
    * Create a {@link CatalogEntry} details widget.
    */
   public CatalogEntryDetails()
   {
      setLayout(new GridBagLayout());

      int row = -1;
      int col = 0;

      entName = createLine(I18n.getMessage("CatalogEntryDetails.Name"), col, ++row);
      entManufacturer = createLine(I18n.getMessage("CatalogEntryDetails.Manufacturer"), col, ++row);
      entOrderNumber = createLine(I18n.getMessage("CatalogEntryDetails.Order_number"), col, ++row);
      entSeries = createLine(I18n.getMessage("CatalogEntryDetails.Series"), col, ++row);
      entColor = createLine(I18n.getMessage("CatalogEntryDetails.Color"), col, ++row);
      entWidthModules = createLine(I18n.getMessage("CatalogEntryDetails.Width_in_modules"), col, ++row);
      entWidthMM = createLine(I18n.getMessage("CatalogEntryDetails.Width_in_mm"), col, ++row);
      entDinRail = createLine(I18n.getMessage("CatalogEntryDetails.For_DIN_rail"), col, ++row);

      add(new JLabel(), new GridBagConstraints(col, ++row, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            insets, 0, 8));

      add(new JSeparator(JSeparator.HORIZONTAL), new GridBagConstraints(col, ++row, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            insets, 0, 8));

      entDescription = new JTextArea();
      entDescription.setEditable(false);
      entDescription.setOpaque(false);
      entDescription.setBorder(BorderFactory.createEmptyBorder());
      add(entDescription, new GridBagConstraints(col, ++row, 2, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.BOTH, insets, 0, 0));
   }

   /**
    * Create a label / text line.
    * 
    * @return the created text widget.
    */
   private JLabel createLine(String label, int col, int row)
   {
      JLabel lbl = new JLabel(label);
      lbl.setFont(fntCaption);
      add(lbl, new GridBagConstraints(col, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            labelInsets, 0, 0));

      lbl = new JLabel();
      add(lbl, new GridBagConstraints(col + 1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            insets, 0, 0));

      return lbl;
   }

   /**
    * Set the {@link ProductsFactory} org.freebus.fts.products-factory that is used for accessing details
    * about a catalog entry.
    */
   public void setProductsFactory(ProductsFactory productsFactory)
   {
      if (productsFactory == null) productDescriptionService = null;
      else productDescriptionService = productsFactory.getProductDescriptionService();
   }

   /**
    * Set the {@link CatalogEntry} object that is displayed.
    */
   public void setCatalogEntry(CatalogEntry entry)
   {
      entName.setText(entry.getName());
      entManufacturer.setText(entry.getManufacturer().getName());
      entOrderNumber.setText(entry.getOrderNumber());
      entSeries.setText(entry.getSeries());
      entColor.setText(entry.getColor());
      entWidthModules.setText("" + entry.getWidthModules());
      entWidthMM.setText("" + entry.getWidthMM());
      entDinRail.setText(I18n.getMessage(entry.getDIN() ? "CatalogEntryDetails.DIN_Yes" : "CatalogEntryDetails.DIN_No"));

      ProductDescriptionService prodDescService = productDescriptionService;
      if (prodDescService == null)
         prodDescService = ProductsManager.getFactory().getProductDescriptionService();

      try
      {
         final ProductDescription desc = prodDescService.getProductDescription(entry);
         entDescription.setText(desc == null ? "" : desc.getDescription());
      }
      catch (NoResultException e)
      {
         entDescription.setText("");
      }
      catch (DAOException e)
      {
         e.printStackTrace();
         entDescription.setText("");
      }
   }
}
