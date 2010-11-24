package org.freebus.fts.components.telegramdetails;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.common.address.Address;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.components.AddressField;

/**
 * {@link DetailsPart Details part} for the header of
 * a telegram.
 */
public class HeaderDetailsPart extends AbstractDetailsPart
{
   private static final long serialVersionUID = -85592691969666649L;

   private final AddressField adfFrom = new AddressField(AddressField.PHYSICAL);
   private final AddressField adfDest = new AddressField(AddressField.ANY);

   private byte[] data;
   private boolean isExtFormat;

   /**
    * Create a details part
    *
    * @param parent - the parent
    */
   public HeaderDetailsPart(DetailsParent parent)
   {
      super(parent);

      final JPanel body = getBody();
      body.setLayout(new BoxLayout(body, BoxLayout.X_AXIS));

      body.add(new JLabel(I18n.getMessage("HeaderDetailsPart.From")));
      adfFrom.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 4));
      body.add(adfFrom);
      adfFrom.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent e)
         {
            if (adfFrom.getAddress() != null)
               changed();
         }
      });

      body.add(new JLabel(I18n.getMessage("HeaderDetailsPart.Dest")));
      adfDest.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 4));
      body.add(adfDest);
      adfDest.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent e)
         {
            if (adfDest.getAddress() != null)
               changed();
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setData(byte[] data)
   {
      this.data = data;
      isExtFormat = (data[0] & 0x80) == 0;

      setLabelData(data);

      if (isExtFormat)
      {
         adfFrom.setAddress(new PhysicalAddress(data[2], data[3]));

         final boolean destIsGroup = (data[1] & 0x80) == 0x80;
         if (destIsGroup)
            adfDest.setAddress(new GroupAddress(data[4], data[5]));
         else adfDest.setAddress(new PhysicalAddress(data[4], data[5]));
      }
      else
      {
         adfFrom.setAddress(new PhysicalAddress(data[1], data[2]));

         final boolean destIsGroup = (data[5] & 0x80) == 0x80;
         if (destIsGroup)
            adfDest.setAddress(new GroupAddress(data[3], data[4]));
         else adfDest.setAddress(new PhysicalAddress(data[3], data[4]));
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] getData()
   {
      return data;
   }

   /**
    * Called when the user changed an element.
    */
   protected void changed()
   {
      final int fromOffs = isExtFormat ? 2 : 1;

      final PhysicalAddress from = (PhysicalAddress) adfFrom.getAddress();
      data[fromOffs] = (byte) (from.getAddr() >> 8);
      data[fromOffs + 1] = (byte) from.getAddr();

      final Address dest = adfDest.getAddress();
      data[fromOffs + 2] = (byte) (dest.getAddr() >> 8);
      data[fromOffs + 3] = (byte) dest.getAddr();

      setLabelData(data);
      notifyChanged();
   }
}
