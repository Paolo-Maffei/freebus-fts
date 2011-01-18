package org.freebus.fts.client.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.freebus.fts.client.actions.SetPhysicalAddressAction;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.elements.components.Dialog;

/**
 * A small dialog that allows to enter a physical address.
 *
 * The corresponding action {@link SetPhysicalAddressAction} starts the
 * programming job that programs a device on the bus with the entered address
 * when the user clicks the "Program" button of this dialog.
 */
public class SetPhysicalAddress extends Dialog
{
   private static final long serialVersionUID = -2174732025882942111L;

   private final JTextField inpAddr = new JTextField();
   private final JButton btnOk;

   /**
    * Create a set-physical-address dialog
    *
    * @param owner the <code>Window</code> from which the dialog is displayed or
    *           <code>null</code> if this dialog has no owner
    */
   public SetPhysicalAddress(Window owner)
   {
      super(owner);
      setTitle(I18n.getMessage("SetPhysicalAddress.Title"));

      final Container body = getBodyPane();
      body.setLayout(new GridBagLayout());

      JLabel lbl = new JLabel("<html><body>" + I18n.getMessage("SetPhysicalAddress.Explain") + "</body></html>");
      body.add(lbl, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(4, 4, 2, 4), 0, 0));

      lbl = new JLabel(I18n.getMessage("SetPhysicalAddress.Address"));
      body.add(lbl, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(4, 2, 2, 4), 0, 0));

      inpAddr.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
      inpAddr.setColumns(10);
      inpAddr.setText("1.1.1");
      inpAddr.setMinimumSize(inpAddr.getPreferredSize());
      inpAddr.setSelectionStart(0);
      inpAddr.setSelectionEnd(inpAddr.getText().length());
      body.add(inpAddr, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, new Insets(4, 2, 2, 4), 0, 0));

      body.add(new JPanel(), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

      inpAddr.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent e)
         {
            validateInput();
         }
      });

      btnOk = new JButton(I18n.getMessage("Button.Program"));
      addButton(btnOk, Dialog.ACCEPT);

      addButton(new JButton(I18n.getMessage("Button.Cancel")), Dialog.REJECT);

      setMinimumSize(new Dimension(400, 150));
      setSize(getMinimumSize());
      center();

      validateInput();
   }

   /**
    * @return the physical address that the physical-address input field
    *         contains, or null if the input field contains no valid physical
    *         address.
    *
    * @see PhysicalAddress#valueOf(String)
    */
   public PhysicalAddress getPhysicalAddress()
   {
      return PhysicalAddress.valueOf(inpAddr.getText());
   }

   /**
    * Set the contents of the physical-address input field.
    *
    * @param addr - the physical address to set.
    */
   public void setPhysicalAddress(final PhysicalAddress addr)
   {
      inpAddr.setText(addr.toString());
   }

   /**
    * Enable the Program button if the contents of the input field is a valid
    * physical address. Disable the button if not.
    */
   private void validateInput()
   {
      btnOk.setEnabled(PhysicalAddress.valueOf(inpAddr.getText()) != null);
   }
}
