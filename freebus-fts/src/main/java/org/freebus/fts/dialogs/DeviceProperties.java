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

import org.freebus.fts.components.Dialog;
import org.freebus.fts.core.I18n;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;

/**
 * A small dialog that allows to enter a name and physical address.
 *
 */
public class DeviceProperties extends Dialog
{
   private static final long serialVersionUID = 1L;
   private Line parentLine;
   
   private final JTextField inpName = new JTextField();
   private JComboBox inpAddress;
   private JButton btnOk;

   
   /**
    * Create a DeviceProperties dialog
    *
    * @param owner the <code>Window</code> from which the dialog is displayed or
    *           <code>null</code> if this dialog has no owner
    * @param parentLine the line the device will be added. We need this to find out 
    *           the free addresses in the line for devices
    */
   public DeviceProperties(Window owner, Line parentLine)
   {
      super(owner);
      this.parentLine = parentLine;
      createDialog(owner, I18n.getMessage("DeviceProperties.DefaultName"), -1);
   }
   
   /**
    * Create a DeviceProperties dialog
    *
    * @param owner the <code>Window</code> from which the dialog is displayed or
    *           <code>null</code> if this dialog has no owner
    * @param device the device we want to edit
    */
   public DeviceProperties(Window owner, Device device)
   {
      super(owner);
      this.parentLine = device.getLine();
      createDialog(owner, device.getName(), device.getAddress());
   }
   
   /**
    * creates the dialog
    * @param owner
    * @param currentName
    * @param address
    */
   private void createDialog(Window owner, String currentName, int address)
   {
      final Project project = ProjectManager.getProject();
      if (project == null || parentLine == null)
         throw new IllegalArgumentException();
      
      setTitle(I18n.getMessage("DeviceProperties.Title"));
      setModal(true);

      final Container body = getBodyPane();
      body.setLayout(new GridBagLayout());
      
      JLabel lbl = new JLabel("<html><body>" + I18n.formatMessage("DeviceProperties.Explain", new String[] { Integer.toString(Area.MIN_NAME_LENGTH) } ) + "</body></html>");
      body.add(lbl, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(4, 4, 2, 4), 0, 0));

      lbl = new JLabel(I18n.getMessage("DeviceProperties.Name"));
      body.add(lbl, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(4, 2, 2, 4), 0, 0));

      inpName.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
      inpName.setColumns(10);
      inpName.setText(currentName);
      inpName.setEditable(false);
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
      
      final int usedAddr[] = parentLine.getUsedDeviceAddresses();
      
      //we want a list with all free addresses
      String freeAddr[] = new String[(255 - usedAddr.length + 1 + 1)];
      
      int pos = 0;
      if (address == -1)
         freeAddr[pos] = "---";
      else
         freeAddr[pos] = Integer.toString(address); 
      pos++;
      
      for (int cnt = 0 ; cnt <= 255 ; cnt++)
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
      
      lbl = new JLabel(I18n.getMessage("DeviceProperties.Address"));
      body.add(lbl, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(4, 2, 2, 4), 0, 0));
      
      inpAddress = new JComboBox(freeAddr);
      inpAddress.setSelectedIndex(0);   // this is either the already set address or nothing use full
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
    * @param a          array to search
    * @param toFind     value to find
    * @return           true if it found
    */
   private boolean contains(int a[], int toFind)
   {
      for (int i = 0 ; i < a.length ; i++)
      {
         if (a[i] == toFind)
            return true;
      }
      return false;
   }
   
   /**
    * @return the name of the text input field
    *         
    */
   public String getDeviceName()
   {
      return inpName.getText();
   }

   /**
    * Set the contents of the name text input field.
    *
    * @param newName - the new name for the device
    */
   public void setDeviceName(final String newName)
   {
      //TODO will this ever make it though to the device ?
      inpName.setText(newName);
   }

   /**
    * returns the selected address for the area
    * throws NumberFormatException if an invalid address is selected.
    * 
    * @return address
    */
   public int getAddress()
   {
      return Integer.parseInt((String)inpAddress.getSelectedItem());
   }
   
   /**
    * Enable the Program button if the contents of the input field is a valid
    * physical address. Disable the button if not.
    */
   private void validateInput()
   {
      boolean ok = true;
      
      if (!(inpName.getText().length() >= 3))
         ok = false;
      
      // I do not know a nicer way to check if we have a valid address
      try
      {
         Integer.parseInt((String)inpAddress.getSelectedItem());
      }
      catch(NumberFormatException e)
      {
         ok = false;
      }
      
      btnOk.setEnabled(ok);
   }
}
