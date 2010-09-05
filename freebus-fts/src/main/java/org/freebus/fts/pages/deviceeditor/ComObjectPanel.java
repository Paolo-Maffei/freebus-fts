package org.freebus.fts.pages.deviceeditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freebus.fts.core.ImageCache;
import org.freebus.fts.products.CommunicationObject;

/**
 * Displays / edits details about a {@link CommunicationObject}.
 */
public class ComObjectPanel extends JPanel
{
   private static final long serialVersionUID = -4579589068706937561L;

   private final JLabel lblName = new JLabel();
   private final JLabel lblType = new JLabel();

   private final CommunicationObject comObject;

   /**
    * Create a communication-objects panel.
    * 
    * @param comObject - the communication object to display.
    */
   public ComObjectPanel(CommunicationObject comObject)
   {
      this.comObject = comObject;

      setLayout(new GridBagLayout());
      setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));

      final Insets noInsets = new Insets(0, 0, 0, 0);
      int gridy = -1;

      add(new JLabel(ImageCache.getIcon("icons/com-object")), new GridBagConstraints(0, ++gridy, 1, 2, 0, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.VERTICAL, new Insets(0, 2, 0, 2), 0, 0));
      add(lblName, new GridBagConstraints(1, gridy, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, noInsets, 0, 0));
      add(lblType, new GridBagConstraints(1, ++gridy, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, noInsets, 0, 0));

      updateContents();
   }

   /**
    * @return The communication object that is displayed.
    */
   public CommunicationObject getComObject()
   {
      return comObject;
   }

   /**
    * Update the contents of the panel.
    */
   public void updateContents()
   {
      lblName.setText(comObject.getName() + " - " + comObject.getFunction());
      lblType.setText(comObject.getObjectType().getName());
   }
}
