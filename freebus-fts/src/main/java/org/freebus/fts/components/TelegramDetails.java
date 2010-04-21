package org.freebus.fts.components;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.freebus.fts.components.telegramdetails.DetailsParent;
import org.freebus.fts.components.telegramdetails.DetailsPart;
import org.freebus.fts.components.telegramdetails.HeaderDetailsPart;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * A component that shows details about a specific {@link Telegram telegram}.
 */
public class TelegramDetails extends JPanel implements DetailsParent
{
   private static final long serialVersionUID = 4049021192648889738L;

   private byte[] data;
   private int gridy = -1;

   private final JPanel body = new JPanel();
   private final Insets partInsets = new Insets(0, 0, 0, 4);

   private final HeaderDetailsPart headerPart = new HeaderDetailsPart(this);

   /**
    * Create a telegram details component.
    */
   public TelegramDetails()
   {
      add(body);
//      setViewportView(body);

      final int margin = 4;
      body.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
      body.setLayout(new GridBagLayout());
      body.setOpaque(false);

      addPart(headerPart);

      body.add(new JPanel(), new GridBagConstraints(0, ++gridy, 1, 1, 1, 100, GridBagConstraints.NORTH,
            GridBagConstraints.BOTH, partInsets, 0, 0));
   }

   /**
    * Set the telegram that is displayed.
    *
    * @param telegram - the telegram to display
    */
   public void setTelegram(Telegram telegram)
   {
      setTelegram(telegram.toByteArray());
   }

   /**
    * Set the telegram that is displayed by its raw data.
    *
    * @param data - the data of the telegram to display
    */
   public void setTelegram(byte[] data)
   {
      this.data = data;
      updateContents();
   }

   /**
    * Update the component's contents.
    */
   protected void updateContents()
   {
      final int ctrl = data[0];
      final boolean isExtFormat = (ctrl & 0x80) == 0;

      headerPart.setData(Arrays.copyOfRange(data, 0, isExtFormat ? 7 : 6));
   }

   /**
    * Add a component to the body.
    */
   private void addPart(Component comp)
   {
      body.add(comp, new GridBagConstraints(0, ++gridy, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
            GridBagConstraints.HORIZONTAL, partInsets, 0, 0));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void partChanged(DetailsPart part)
   {
   }
}
