package org.freebus.fts.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.text.MessageFormat;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.freebus.fts.FTS;
import org.freebus.fts.core.I18n;
import org.freebus.fts.elements.components.Dialog;
import org.freebus.fts.elements.components.Dialogs;

/**
 * An "about" dialog.
 */
public class About extends Dialog
{
   private static final long serialVersionUID = -2792850060628403138L;

   /**
    * Create the "about" dialog.
    */
   public About(Window owner)
   {
      super(owner);

      setTitle(I18n.getMessage("About.Title"));
      setSize(500, 400);

      final Container body = getBodyPane();
      body.setLayout(new BorderLayout());

      final String licenseUrl = "http://www.gnu.org/licenses";
      final String projectUrl = "http://bugs.freebus.org/projects/fts";

      final StringBuilder sb = new StringBuilder();

      sb.append("<html>");
      sb.append("<h1>").append(htmlEncode(I18n.getMessage("About.ProductName"))).append("</h1>");

      final Properties props = FTS.getInstance().getManifestProperties();
      final String version = props.getProperty("Version");
      final String revision = props.getProperty("SCM-Revision");
      if (version != null || revision != null)
      {
         if (version != null)
         {
            sb.append(MessageFormat.format(htmlEncode(I18n.getMessage("About.Version")), version, null));
         }
         if (revision != null)
         {
            if (version != null)
               sb.append(", ");
            sb.append(MessageFormat.format(htmlEncode(I18n.getMessage("About.Revision")), revision, null));
         }
         sb.append("<br /><br />");
      }

      sb.append("<i>").append(htmlEncode(I18n.getMessage("About.Copyright"))).append("</i><br /><br />");

      sb.append(MessageFormat.format(htmlEncode(I18n.getMessage("About.Website")), "<a href=\"" + projectUrl + "\">"
            + projectUrl + "</a>", null));
      sb.append("<br /><br />");

      sb.append(htmlEncode(I18n.getMessage("About.License"))).append("<br /><br />");
      sb.append(htmlEncode(I18n.getMessage("About.Warranty"))).append("<br /><br />");

      sb.append(MessageFormat.format(htmlEncode(I18n.getMessage("About.ObtainLicense")), "<a href=\"" + licenseUrl
            + "\">" + licenseUrl + "</a>", null));

      sb.append("</html>");

      final JEditorPane jep = new JEditorPane("text/html", sb.toString());
      jep.setOpaque(false);
      jep.setEditable(false);
      jep.setBorder(BorderFactory.createEmptyBorder(10, 20, 4, 20));
      body.add(jep, BorderLayout.CENTER);

      addButton(new JButton(I18n.getMessage("Button.Ok")), Dialog.ACCEPT);

      jep.addHyperlinkListener(new HyperlinkListener()
      {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent e)
         {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
               try
               {
                  java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
               }
               catch (Exception ex)
               {
                  Dialogs.showExceptionDialog(ex, I18n.getMessage("About.ErrStartBrowser"));
               }
            }
         }
      });
   }

   /**
    * Encode special characters in str for HTML
    */
   private String htmlEncode(final String str)
   {
      return str.replace("&", "&amp;").replace("'", "&rsquo;").replace("\"", "&quot;").replace("<", "&lt;")
         .replace(">", "&gt;").replace("\n", "<br />");
   }
}
