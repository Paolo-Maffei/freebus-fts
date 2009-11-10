package org.freebus.fts.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.freebus.fts.Config;
import org.freebus.fts.db.Driver;
import org.freebus.fts.utils.I18n;

/**
 * A configuration widget for the settings of a database connection.
 */
public class DatabaseSettings extends Composite
{
   private final Combo cboDriver;
   private final Text inpLocation, inpUser, inpPass;

   /**
    * Create a database-settings widget.
    */
   public DatabaseSettings(Composite parent, int style)
   {
      super(parent, style);

      GridLayout gridLayout = new GridLayout(2, false);
      gridLayout.horizontalSpacing = 8;
      gridLayout.verticalSpacing = 4;
      setLayout(gridLayout);

      final Config cfg = Config.getInstance();

      Label lbl;
      String val;
      
      lbl = new Label(this, SWT.LEFT);
      lbl.setLayoutData(new GridData(160, SWT.DEFAULT));
      lbl.setText(I18n.getMessage("DatabaseSettings_Driver"));

      cboDriver = new Combo(this, SWT.READ_ONLY);
      cboDriver.setLayoutData(new GridData(200, SWT.DEFAULT));
      setupDriverCombo();

      lbl = new Label(this, SWT.LEFT);
      lbl.setText(I18n.getMessage("DatabaseSettings_Location"));

      inpLocation = new Text(this, SWT.BORDER | SWT.SINGLE);
      inpLocation.setLayoutData(new GridData(200, SWT.DEFAULT));
      val = cfg.getProductsDbLocation();
      if (val == null || val.isEmpty()) val = cfg.getProductsDbDriver().getExampleLocation();
      inpLocation.setText(val);

      lbl = new Label(this, SWT.LEFT);
      lbl.setText(I18n.getMessage("DatabaseSettings_User"));

      inpUser = new Text(this, SWT.BORDER | SWT.SINGLE);
      inpUser.setLayoutData(new GridData(200, SWT.DEFAULT));
      val = cfg.getProductsDbUser();
      if (val == null) val = "";
      inpUser.setText(val);

      lbl = new Label(this, SWT.LEFT);
      lbl.setText(I18n.getMessage("DatabaseSettings_Pass"));

      inpPass = new Text(this, SWT.BORDER | SWT.SINGLE /*| SWT.PASSWORD*/);
      inpPass.setLayoutData(new GridData(200, SWT.DEFAULT));
      val = cfg.getProductsDbPass();
      if (val == null) val = "";
      inpPass.setText(val);
   }

   /**
    * Fill the ports combo-box with all available database drivers.
    */
   protected void setupDriverCombo()
   {
      cboDriver.removeAll();

      final Driver current = Config.getInstance().getProductsDbDriver();

      int i = -1;
      for (final Driver driver: Driver.values())
      {
         cboDriver.add(driver.getLabel());
         cboDriver.setData(Integer.toString(++i), driver);
         if (driver == current) cboDriver.select(i);
      }
   }

   /**
    * Apply the widget's contents to the configuration object.
    * @return true if anything was changed.
    */
   public boolean apply()
   {
      final Config cfg = Config.getInstance();

      int idx = cboDriver.getSelectionIndex();
      final Driver driver = (Driver) cboDriver.getData(Integer.toString(idx));

      if (driver == cfg.getProductsDbDriver() &&
          inpLocation.getText().equals(cfg.getProductsDbLocation()) &&
          inpUser.getText().equals(cfg.getProductsDbUser()) &&
          inpPass.getText().equals(cfg.getProductsDbPass()))
      {
         return false;
      }

      cfg.setProductsDbDriver(driver);
      cfg.setProductsDbLocation(inpLocation.getText());
      cfg.setProductsDbUser(inpUser.getText());
      cfg.setProductsDbPass(inpPass.getText());

      return true;
   }
}
