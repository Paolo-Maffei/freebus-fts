package org.freebus.fts.gui_swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.eclipse.swt.widgets.Display;
import org.freebus.fts.project.Project;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.IconCache;

/**
 * The main application window.
 */
public final class MainWindow extends JFrame implements ActionListener
{
   private static final long serialVersionUID = -8804780918316053246L;
   private final Display display = new Display();
   private final JMenuBar menuBar;
   private final JToolBar toolBar;
   private final JTabbedPane tabPaneLeft, tabPaneCenter;
   private final Project project = Project.createSampleProject();

   /**
    * Create a new main-window.
    */
   public MainWindow()
   {
      super(I18n.getMessage("app_title"));
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(new BorderLayout());

      final double f = 0.9;
      final int w = (int) (display.getBounds().width * f);
      final int h = (int) (display.getBounds().height * f);
      setSize(w, h);

      menuBar = new JMenuBar();
      initMenuBar();
      setJMenuBar(menuBar);

      toolBar = new JToolBar();
      initToolBar();
      add(toolBar, BorderLayout.PAGE_START);

      tabPaneLeft = new JTabbedPane();
      tabPaneCenter = new JTabbedPane();
      
      final JSplitPane sptBody = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabPaneLeft, tabPaneCenter);
      sptBody.setOneTouchExpandable(true);
      sptBody.setDividerLocation(250);
      add(sptBody);

      addTabPage(tabPaneLeft, new PhysicalTab(project));
   }

   /**
    * Add the tab-page page to the tabbed-pane tabPane.
    */
   public void addTabPage(JTabbedPane tabPane, TabPage page)
   {
      tabPane.add(page.getTitle(), page);
   }
   
   /**
    * Initialize the menu-bar
    */
   private void initMenuBar()
   {
      JMenuItem menuItem;
      JMenu menu;
      
      menu = new JMenu(I18n.getMessage("File_Menu"));
      menu.setMnemonic(I18n.getMessage("File_Menu_Mnemonic").charAt(0));
      menuBar.add(menu);

      menuItem = new JMenuItem(I18n.getMessage("New_Project"));
      menuItem.setMnemonic(I18n.getMessage("New_Project_Mnemonic").charAt(0));
      menuItem.setActionCommand("new-project");
      menuItem.addActionListener(this);
      menu.add(menuItem);

      menuItem = new JMenuItem(I18n.getMessage("Open_Project"));
      menuItem.setMnemonic(I18n.getMessage("Open_Project_Mnemonic").charAt(0));
      menuItem.setActionCommand("open-project");
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
      menuItem.addActionListener(this);
      menu.add(menuItem);

      menu.addSeparator();
      
      menuItem = new JMenuItem(I18n.getMessage("Exit"));
      menuItem.setMnemonic(I18n.getMessage("Exit_Mnemonic").charAt(0));
      menuItem.setActionCommand("exit");
      menuItem.addActionListener(this);
      menu.add(menuItem);
   }

   /**
    * Initialize the tool-bar
    */
   private void initToolBar()
   {
      JButton button;

      button = new JButton();
      button.setIcon(IconCache.get("exit"));
      button.setActionCommand("exit");
      button.addActionListener(this);
      toolBar.add(button);
   }

   /**
    * An action was triggered
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      final String cmd = e.getActionCommand();
      if (cmd.equals("exit")) System.exit(0);
   }

}
