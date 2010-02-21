package org.freebus.fts.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.freebus.fts.components.Dialog;
import org.freebus.fts.core.I18n;
import org.freebus.knxcomm.telegram.PhysicalAddress;

/**
 * A small dialog that allows to enter a physical address and starts the
 * programming job that programs a device on the bus with the entered address.
 */
public class SetPhysicalAddress extends Dialog
{
   private static final long serialVersionUID = -2174732025882942111L;

   private final JTextArea inpAddr = new JTextArea();
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

      final Container body = getBodyPane();
      body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

      final JLabel lbl = new JLabel("<html><body>" + I18n.getMessage("SetPhysicalAddress.Explain") + "</body></html>");
      lbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      body.add(lbl);

      inpAddr.setMaximumSize(new Dimension(32768, 20));
      inpAddr.setText("1.1.1");
      body.add(inpAddr);

      inpAddr.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent e)
         {
            btnOk.setEnabled(PhysicalAddress.valueOf(inpAddr.getText()) != null);
         }
      });

      btnOk = new JButton(I18n.getMessage("Button.Program"));
      btnOk.setEnabled(false);
      addButton(btnOk, Dialog.ACCEPT);

      addButton(new JButton(I18n.getMessage("Button.Cancel")), Dialog.REJECT);
      
      setSize(400, 200);
   }
}
