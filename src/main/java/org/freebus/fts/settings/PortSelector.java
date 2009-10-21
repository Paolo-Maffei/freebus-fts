package org.freebus.fts.settings;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.freebus.fts.Config;
import org.freebus.fts.utils.I18n;

/**
 * A configuration widget for the selection of the comm-port.
 */
public class PortSelector extends Composite
{
   private final Combo cbxPort;

   /**
    * Create a comm-port selection widget.
    */
   public PortSelector(Composite parent, int style)
   {
      super(parent, style);

      GridLayout gridLayout = new GridLayout(2, false);
      gridLayout.horizontalSpacing = 8;
      gridLayout.verticalSpacing = 4;
      setLayout(gridLayout);

      Label lbl;

      lbl = new Label(this, SWT.CENTER);
      lbl.setText(I18n.getMessage("PortSelector_Port"));

      cbxPort = new Combo(this, SWT.READ_ONLY);
      setupPortsCombo();
   }

   /**
    * Fill the ports combo-box with all available ports.
    */
   protected void setupPortsCombo()
   {
      cbxPort.removeAll();

      final String commPort = Config.getInstance().getCommPort();

      final Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
      while (portList.hasMoreElements())
      {
         CommPortIdentifier portIdent = (CommPortIdentifier) portList.nextElement();
         
         if (portIdent.getPortType() == CommPortIdentifier.PORT_SERIAL)
         {
            cbxPort.add(portIdent.getName());
            if (portIdent.getName().equals(commPort)) cbxPort.select(cbxPort.getItemCount()-1);
         }
      }
   }

   /**
    * Apply the widget's contents to the config object.
    */
   public void apply()
   {
      final Config cfg = Config.getInstance();

      int idx = cbxPort.getSelectionIndex();
      cfg.setCommPort(idx>=0 ? cbxPort.getItem(idx) : null);
   }
}
