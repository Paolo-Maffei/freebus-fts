package org.freebus.fts.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.utils.I18n;

/**
 * A dialog for programming a physical address.
 */
public class PhysicalAddressProgrammer
{
   private final Shell shell = new Shell(MainWindow.getInstance().getShell(), SWT.DIALOG_TRIM|SWT.SYSTEM_MODAL);
   private final Group contents = new Group(shell, SWT.SHADOW_NONE);
   private final Combo cboZone = new Combo(contents, SWT.SIMPLE);
   private final Combo cboLine = new Combo(contents, SWT.SIMPLE);
   private final Combo cboNode = new Combo(contents, SWT.SIMPLE);

   /**
    * Create a physical address programmer dialog.
    */
   public PhysicalAddressProgrammer()
   {
      shell.setText(I18n.getMessage("PhysicalAddressProgrammer_Title"));
      shell.setLayout(new FormLayout());
      shell.setSize(500, 400);

      contents.setLayout(new GridLayout(2, false));

      cboZone.setLayoutData(new GridData(200, SWT.DEFAULT));
      cboLine.setLayoutData(new GridData(200, SWT.DEFAULT));
      cboNode.setLayoutData(new GridData(200, SWT.DEFAULT));
   }

   /**
    * Show the dialog.
    */
   public void open()
   {
      shell.open();
   }
}
