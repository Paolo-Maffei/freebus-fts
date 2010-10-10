package org.freebus.fts.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.freebus.fts.I18n;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.elements.components.Dialog;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;

/**
 * A small dialog that allows to enter a name and physical address.
 *
 */
public class AreaProperties extends Dialog
{
   private static final long serialVersionUID = 1L;

   private final JTextField inpName = new JTextField();
   private JComboBox inpAddress;
   private JButton btnOk;

   /**
    * Create a AreaProperties dialog
    *
    * @param owner the <code>Window</code> from which the dialog is displayed or
    *           <code>null</code> if this dialog has no owner
    */
   public AreaProperties(Window owner)
   {
      super(owner);
      createDialog(owner, I18n.getMessage("AreaProperties.DefaultName"), -1);
   }

   /**
    * Create a AreaProperties dialog
    *
    * @param owner the <code>Window</code> from which the dialog is displayed or
    *           <code>null</code> if this dialog has no owner
    * @param currentName the name to provide for Area as a start
    */
   public AreaProperties(Window owner, String currentName, int address)
   {
      super(owner);
      createDialog(owner, currentName, address);
   }

   /**
    * creates the dialog
    *
    * @param owner
    * @param currentName
    * @param address
    */
   private void createDialog(Window owner, String currentName, int address)
   {
      final Project project = ProjectManager.getProject();
      if (project == null)
         throw new IllegalArgumentException();

      setTitle(I18n.getMessage("AreaProperties.Title"));
      setModal(true);

      final Container body = getBodyPane();
      body.setLayout(new GridBagLayout());

      JLabel lbl = new JLabel("<html><body>"
            + I18n.formatMessage("AreaProperties.Explain", new String[] { Integer.toString(Area.MIN_NAME_LENGTH) })
            + "</body></html>");
      body.add(lbl, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(4, 4, 2, 4), 0, 0));

      lbl = new JLabel(I18n.getMessage("AreaProperties.Name"));
      body.add(lbl, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(4, 2, 2, 4), 0, 0));

      inpName.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
      inpName.setColumns(10);
      inpName.setText(currentName);
      inpName.setMinimumSize(inpName.getPreferredSize());
      inpName.setSelectionStart(0);
      inpName.setSelectionEnd(inpName.getText().length());
      body.add(inpName, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, new Insets(4, 2, 2, 4), 0, 0));

      body.add(new JPanel(), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

      inpName.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent e)
         {
            validateInput();
         }
      });

      // TODO
      final int usedAddr[] = new int[0];

      // we want a list with all free addresses
      String freeAddr[] = new String[(Area.MAX_ADDR - usedAddr.length + 1 + 1)];

      int pos = 0;
      if (address == -1)
         freeAddr[pos] = "---";
      else freeAddr[pos] = Integer.toString(address);
      pos++;

      for (int cnt = 0; cnt <= Area.MAX_ADDR; cnt++)
      {
         // search for next free number
         while (contains(usedAddr, cnt))
         {
            cnt++;
         }
         // store it
         if (pos < freeAddr.length)
         {
            freeAddr[pos] = Integer.toString(cnt);
            pos++;
         }
      }

      lbl = new JLabel(I18n.getMessage("AreaProperties.Address"));
      body.add(lbl, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(4, 2, 2, 4), 0, 0));

      inpAddress = new JComboBox(freeAddr);
      inpAddress.setSelectedIndex(0); // this is either the already set address
                                      // or nothing use full
      body.add(inpAddress, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, new Insets(4, 2, 2, 4), 0, 0));

      inpAddress.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            validateInput();
         }
      });

      body.add(new JPanel(), new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

      btnOk = new JButton(I18n.getMessage("Button.Ok"));
      addButton(btnOk, Dialog.ACCEPT);

      addButton(new JButton(I18n.getMessage("Button.Cancel")), Dialog.REJECT);

      setMinimumSize(new Dimension(440, 180));
      setSize(getMinimumSize());

      validateInput();
   }

   /**
    * returns true if the array contains the given value
    *
    * @param a array to search
    * @param toFind value to find
    * @return true if it found
    */
   private boolean contains(int a[], int toFind)
   {
      for (int i = 0; i < a.length; i++)
      {
         if (a[i] == toFind)
            return true;
      }
      return false;
   }

   /**
    * @return the physical address that the physical-address input field
    *         contains, or null if the input field contains no valid physical
    *         address.
    *
    * @see PhysicalAddress#valueOf(String)
    */
   public String getAreaName()
   {
      return inpName.getText();
   }

   /**
    * Set the name of the area.
    *
    * @param newName - the name to set.
    */
   public void setAreaName(final String newName)
   {
      inpName.setText(newName);
   }

   /**
    * Returns the selected address for the area throws NumberFormatException if
    * an invalid address is selected.
    *
    * @return address
    */
   public int getAddress()
   {
      return Integer.parseInt((String) inpAddress.getSelectedItem());
   }

   /**
    * Enable the Program button if the contents of the input field is a valid
    * physical address. Disable the button if not.
    */
   private void validateInput()
   {
      boolean ok = true;

      if (!(inpName.getText().length() >= Area.MIN_NAME_LENGTH))
         ok = false;

      // I do not know a nicer way to check if we have a valid address
      try
      {
         Integer.parseInt((String) inpAddress.getSelectedItem());
      }
      catch (NumberFormatException e)
      {
         ok = false;
      }

      btnOk.setEnabled(ok);
   }
}
