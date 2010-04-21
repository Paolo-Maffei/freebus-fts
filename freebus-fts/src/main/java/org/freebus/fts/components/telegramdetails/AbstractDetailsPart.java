package org.freebus.fts.components.telegramdetails;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freebus.fts.common.HexString;

/**
 * An abstract base implementation of a details part.
 */
public abstract class AbstractDetailsPart extends JPanel implements DetailsPart
{
   private static final long serialVersionUID = 3125178812213451030L;

   private final DetailsParent parent;
   private final JLabel lblData = new JLabel();
   private final JPanel body = new JPanel();

   /**
    * Create a details part.
    *
    * @param parent - the parent that owns the details part.
    */
   protected AbstractDetailsPart(DetailsParent parent)
   {
      this.parent = parent;

      setLayout(new BorderLayout(0, 0));

      lblData.setOpaque(false);
      lblData.setForeground(Color.blue);
      lblData.setText(" ");
      add(lblData, BorderLayout.NORTH);

      body.setOpaque(false);
      body.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
      add(body, BorderLayout.CENTER);
   }

   /**
    * @return the body panel.
    */
   protected JPanel getBody()
   {
      return body;
   }

   /**
    * Set the contents of the data, for the data bytes label.
    */
   protected void setLabelData(final byte[] data)
   {
      lblData.setText(HexString.toString(data));
   }

   /**
    * Notify the parent that the part's contents has changed.
    */
   protected void notifyChanged()
   {
      parent.partChanged(this);
   }
}
