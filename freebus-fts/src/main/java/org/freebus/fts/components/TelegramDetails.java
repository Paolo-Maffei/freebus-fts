package org.freebus.fts.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.freebus.fts.core.I18n;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * A component that shows details about a specific {@link Telegram telegram}.
 */
public class TelegramDetails extends JScrollPane
{
   private static final long serialVersionUID = 4049021192648889738L;

   private byte[] data;
   private int gridy;

   private final JPanel body = new JPanel();
   private final Insets insets = new Insets(2, 2, 2, 2);

   /**
    * Create a telegram details component.
    */
   public TelegramDetails()
   {
      setViewportView(body);

      body.setLayout(new GridBagLayout());

      final int margin = 4;
      body.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
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
      body.removeAll();
      gridy = -1;

      setPreferredSize(getSize());

      if (data == null || data.length < 7)
      {
         body.add(new JLabel(I18n.getMessage("TelegramDetails.ErrInvaild")), new GridBagConstraints(0, ++gridy, 1, 1,
               1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
      }
      else createParts();
   }

   /**
    * Create the GUI parts
    */
   private void createParts()
   {
      int ctrl = data[0];


   }

   /**
    * Create a full width label containing a hex string representation of some
    * bytes of the data.
    */
}
