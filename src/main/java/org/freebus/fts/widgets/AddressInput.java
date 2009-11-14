package org.freebus.fts.widgets;

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.freebus.fts.eib.Address;
import org.freebus.fts.eib.GroupAddress;
import org.freebus.fts.eib.PhysicalAddress;

/**
 * A widget for entering a physical address.
 */
public class AddressInput extends Composite
{
   private final CopyOnWriteArrayList<Listener> validInputListeners = new CopyOnWriteArrayList<Listener>();
   private final Spinner spiNode;
   private final Text inpZoneLine;
   private boolean isValid = true;
   private boolean isGroupAddr = false;
   private Color bgValid, bgInvalid;

   public AddressInput(Composite parent, int style)
   {
      super(parent, style);

      RowLayout rowLayout = new RowLayout();
      rowLayout.type = SWT.HORIZONTAL;
      rowLayout.fill = true;
      setLayout(rowLayout);

      inpZoneLine = new Text(this, SWT.BORDER);
      inpZoneLine.setTextLimit(6);
      inpZoneLine.setText("00.00.");
      inpZoneLine.setToolTipText("1.1 ... 15.15");
      bgValid = inpZoneLine.getBackground();

      int red = (bgValid.getRed() + 4) << 2;
      if (red < 64) red = 64;
      if (red > 255) red = 255;
      bgInvalid = new Color(bgValid.getDevice(), red, bgValid.getGreen() >> 1, bgValid.getBlue() >> 1);

      spiNode = new Spinner(this, SWT.BORDER);
      spiNode.setToolTipText("1 ... 255");
      spiNode.setMinimum(1);
      spiNode.setMaximum(255);

      inpZoneLine.addModifyListener(new ModifyListener()
      {
         @Override
         public void modifyText(ModifyEvent e)
         {
            isValid = isValidAddrPart(inpZoneLine.getText());
            inpZoneLine.setBackground(isValid ? bgValid : bgInvalid);
            if (!validInputListeners.isEmpty()) notifyListeners();
         }
      });
   }

   /**
    * Returns the address that the widget contains. Depending on which address
    * type was given during the initial {@link #setAddress}, this can either be
    * a {@link GroupAddress} or a {@link PhysicalAddress}.
    * 
    * @return the entered address.
    */
   public Address getAddress()
   {
      if (isGroupAddr) return GroupAddress.valueOf(inpZoneLine.getText() + '/' + Integer.toString(spiNode.getSelection()));
      return PhysicalAddress.valueOf(inpZoneLine.getText() + '.' + Integer.toString(spiNode.getSelection()));
   }

   /**
    * Set the address that the widget contains. This can either be a
    * {@link PhysicalAddress} or a {@link GroupAddress}.
    */
   public void setAddress(Address address)
   {
      inpZoneLine.setTextLimit(5);

      if (address instanceof GroupAddress)
      {
         isGroupAddr = true;
         final GroupAddress groupAddress = (GroupAddress) address;
         inpZoneLine.setText(String.format("%d/%d", groupAddress.getMain(), groupAddress.getMiddle()));
         spiNode.setSelection(groupAddress.getSub());
      }
      else if (address instanceof PhysicalAddress)
      {
         isGroupAddr = false;
         final PhysicalAddress physicalAddress = (PhysicalAddress) address;
         inpZoneLine.setText(String.format("%d.%d", physicalAddress.getZone(), physicalAddress.getLine()));
         spiNode.setSelection(physicalAddress.getNode());
      }
      else throw new IllegalArgumentException("address must be either a GroupAddress or a PhysicalAddress");
   }

   /**
    * @return true if the widget contains a valid address.
    */
   public boolean isValid()
   {
      return isValid;
   }

   /**
    * Add a listener that is informed when the value of the widget gets valid or
    * invalid. The sent {@link Event}s will have event type {@link SWT#Verify},
    * the {@link Event#widget} set to this widget, and {@link Event#doit} to
    * true if the input is valid.
    */
   public void addValidInputListener(Listener listener)
   {
      validInputListeners.add(listener);
   }

   /**
    * Remove a listener that is informed when the value of the widget gets valid
    * or invalid.
    */
   public void removeValidInputListener(Listener listener)
   {
      validInputListeners.remove(listener);
   }

   /**
    * Notify the valid-input listeners.
    */
   protected void notifyListeners()
   {
      final Event event = new Event();
      event.type = SWT.Verify;
      event.widget = this;
      event.doit = isValid;

      for (Listener listener : validInputListeners)
         listener.handleEvent(event);
   }

   /**
    * @return true if the string str is a valid address part (format "x.x" or
    *         "x/y" with 1<=x<=15 and 1<=y<=7)
    */
   protected boolean isValidAddrPart(String str)
   {
      final int sepPos = str.indexOf(isGroupAddr ? '/' : '.');
      if (sepPos <= 0) return false;

      int val = Integer.parseInt(str.substring(0, sepPos));
      if (val < 1 || val > 15) return false;

      final String valStr = str.substring(sepPos + 1);
      if (valStr.isEmpty()) return false;

      val = Integer.parseInt(valStr);
      if (!isGroupAddr && (val < 1 || val > 15)) return false;
      if (isGroupAddr && (val < 1 || val > 7)) return false;

      return true;
   }
}
