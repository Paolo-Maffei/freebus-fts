package org.freebus.fts.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.freebus.fts.eib.PhysicalAddress;
import org.freebus.fts.eib.jobs.JobQueue;
import org.freebus.fts.eib.jobs.SetPhysicalAddressJob;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.SimpleSelectionListener;
import org.freebus.fts.widgets.AddressInput;

/**
 * A dialog for programming a physical address.
 */
public class PhysicalAddressProgrammer extends Dialog implements Listener
{
   private final AddressInput adiPhysical;
   private final Button btnProgram;

   /**
    * Create a physical address programmer dialog.
    */
   public PhysicalAddressProgrammer()
   {
      super(I18n.getMessage("PhysicalAddressProgrammer_Title"));
      setSize(500, 400);
      center();

      Label lbl;

      contents.setLayout(new GridLayout(2, false));

      lbl = new Label(contents, SWT.FLAT);
      lbl.setText(I18n.getMessage("PhysicalAddressProgrammer_Address"));

      adiPhysical = new AddressInput(contents, SWT.FLAT);
      adiPhysical.setLayoutData(new GridData(200, SWT.DEFAULT));
      adiPhysical.addValidInputListener(this);

      btnProgram = new Button(buttonBox, SWT.PUSH);
      btnProgram.setText(I18n.getMessage("PhysicalAddressProgrammer_BtnProgram"));

      final Button btnClose = new Button(buttonBox, SWT.PUSH);
      btnClose.setText(I18n.getMessage("PhysicalAddressProgrammer_BtnClose"));

      btnProgram.addSelectionListener(new SimpleSelectionListener()
      {
         @Override
         public void widgetSelected(SelectionEvent e)
         {
            program();
         }
      });

      btnClose.addSelectionListener(new SimpleSelectionListener()
      {
         @Override
         public void widgetSelected(SelectionEvent e)
         {
            dispose();
         }
      });
   }

   /**
    * Open the dialog.
    */
   @Override
   public void open()
   {
      super.open();
      adiPhysical.setAddress(PhysicalAddress.ONE);
   }

   /**
    * Start the programming. Called when the user clicks the program button.
    */
   public void program()
   {
      JobQueue.getDefaultJobQueue().add(new SetPhysicalAddressJob((PhysicalAddress) adiPhysical.getAddress()));
      dispose();
   }

   /**
    * Event callback.
    */
   @Override
   public void handleEvent(Event event)
   {
      if (event.widget == adiPhysical && event.type == SWT.Verify)
         btnProgram.setEnabled(event.doit);
   }
}
