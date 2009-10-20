package org.freebus.fts.settings;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
      setLayout(new FormLayout());

      FormData formData;
      Label lbl;

      lbl = new Label(this, SWT.FLAT);
      lbl.setText(I18n.getMessage("PortSelector_Port"));
      formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(0);
      lbl.setLayoutData(formData);

      cbxPort = new Combo(this, SWT.DEFAULT);
      setupPortsCombo();
      cbxPort.pack();
      cbxPort.setSize(200, cbxPort.getSize().y+4);
      formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(lbl, 1);
      formData.right = new FormAttachment(100);
      cbxPort.setLayoutData(formData);
   }

   /**
    * Fill the ports combo-box with all available ports.
    */
   protected void setupPortsCombo()
   {
      cbxPort.removeAll();

      final String commPort = Config.getConfig().getCommPort();

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
      final Config cfg = Config.getConfig();

      int idx = cbxPort.getSelectionIndex();
      cfg.setCommPort(idx>=0 ? cbxPort.getItem(idx) : null);
   }
}
