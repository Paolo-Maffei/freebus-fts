package examples;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.freebus.fts.client.core.Config;
import org.freebus.fts.common.Environment;
import org.freebus.fts.common.HexString;
import org.freebus.fts.components.TelegramDetails;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.elements.services.LookAndFeelManager;

/**
 * A test program that opens a telegram details viewer and displays a test
 * telegram in it.
 */
public class TelegramDetailsViewer extends JFrame
{
   private static final long serialVersionUID = 1L;
   private static final String appTitle = "FTS Telegram Details";

   private final TelegramDetails detailsView = new TelegramDetails();
   private final JTextField txtTelegramData = new JTextField();

   /**
    * Create a new telegram details test-window.
    */
   public TelegramDetailsViewer()
   {
      super(appTitle);

      setLayout(new GridBagLayout());
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setIconImage(ImageCache.getIcon("app-icon").getImage());
      setSize(800, 800);

      final Insets insets = new Insets(2, 2, 2, 2);
      final JButton btnUpdate = new JButton("Update");

      add(new JLabel("Telegram Data:"), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.NONE, insets, 0, 0));
      add(txtTelegramData, new GridBagConstraints(1, 0, 1, 1, 100, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, insets, 0, 0));
      add(btnUpdate, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, insets,
            0, 0));

      detailsView.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
      add(detailsView, new GridBagConstraints(0, 1, 3, 1, 1, 100, GridBagConstraints.NORTHWEST,
            GridBagConstraints.BOTH, insets, 0, 0));

      btnUpdate.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            updateContents();
         }
      });

      txtTelegramData.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyTyped(KeyEvent ev)
         {
            if (ev.getKeyCode() == KeyEvent.VK_ENTER)
               updateContents();
         }
      });
   }

   /**
    * Update the telegram details from the telegram data string.
    */
   private void updateContents()
   {
      detailsView.setTelegram(HexString.valueOf(txtTelegramData.getText()));
   }

   /**
    * Set the telegram to display, as string containing hex bytes.
    *
    * @param str - the telegram as bytes string
    */
   public void setTelegram(final String str)
   {
      txtTelegramData.setText(str);
      updateContents();
   }

   /**
    * The main.
    */
   public static void main(String[] args)
   {
      Environment.init();
      @SuppressWarnings("unused")
      final Config globalConfig = new Config();
      LookAndFeelManager.setDefaultLookAndFeel();

      final TelegramDetailsViewer viewer = new TelegramDetailsViewer();
      viewer.setTelegram("90 33 07 00 00 63 43 40 00 12");

      viewer.setVisible(true);
   }
}
