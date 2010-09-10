package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.freebus.fts.MainWindow;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.pages.inspectvdxfile.TableContents;
import org.freebus.fts.pages.inspectvdxfile.TableContentsGrid;
import org.freebus.fts.pages.inspectvdxfile.TableContentsPerRecord;
import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.persistence.vdx.VdxSection;
import org.freebus.fts.utils.ButtonUtils;

/**
 * A page that displays the contents of a VDX file.
 */
public class InspectVdxFile extends AbstractPage
{
   private static final long serialVersionUID = 244934403536467189L;

   private transient VdxFileReader reader;

   private final JCheckBox cbxTablesSorted = new JCheckBox();
   private final JComboBox cboTables = new JComboBox();
   private final JComboBox cboMaxRecords = new JComboBox();
   private final CardLayout contentsCards = new CardLayout();
   private final JPanel contents = new JPanel(contentsCards);
   private final TableContents tableContentsPerRecord, tableContentsGrid;

   private String selectedTableName;
   private TableContents currentContents;
   private int maxRecords;

   /**
    * Create a VDX browser page.
    */
   public InspectVdxFile()
   {
      setLayout(new BorderLayout());

      add(contents, BorderLayout.CENTER);

      final TableContentsGrid contentsGrid = new TableContentsGrid();
      contents.add(contentsGrid, contentsGrid.getName());
      tableContentsGrid = contentsGrid;

      final TableContentsPerRecord contentsPerRecord = new TableContentsPerRecord();
      contents.add(contentsPerRecord, contentsPerRecord.getName());
      tableContentsPerRecord = contentsPerRecord;

      createToolBar();
   }

   /**
    * Create the tool-bar.
    */
   private void createToolBar()
   {
      final ToolBar toolBar = new ToolBar();
      add(toolBar, BorderLayout.NORTH);

      toolBar.add(new JLabel(I18n.getMessage("InspectVdxFile.CboTables") + ": "));
      toolBar.add(cboTables);
      cboTables.setMinimumSize(new Dimension(100, 5));
      cboTables.setMaximumRowCount(20);
      cboTables.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            updateContents();
         }
      });

      toolBar.add(cbxTablesSorted);
      ButtonUtils.setToolButtonProperties(cbxTablesSorted);
      cbxTablesSorted.setSelected("1".equals(Config.getInstance().getStringValue("InspectVdxFile.sortTables")));
      cbxTablesSorted.setText(I18n.getMessage("InspectVdxFile.CbxTablesSorted"));
      cbxTablesSorted.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Config.getInstance().put("InspectVdxFile.sortTables", cbxTablesSorted.isSelected() ? "1" : "0");
            updateTables();
         }
      });

      toolBar.addSeparator();

      final ButtonGroup btnGrpLayout = new ButtonGroup();
      JToggleButton toggleButton;

      toggleButton = createSwitchContentsToggleButton(tableContentsPerRecord, "icons/view_choose", I18n
            .getMessage("InspectVdxFile.ViewPerRecordToolTip"));
      toolBar.add(toggleButton);
      btnGrpLayout.add(toggleButton);

      toggleButton = createSwitchContentsToggleButton(tableContentsGrid, "icons/view_grid", I18n
            .getMessage("InspectVdxFile.ViewGridToolTip"));
      toolBar.add(toggleButton);
      btnGrpLayout.add(toggleButton);

      toggleButton.setSelected(true);
      currentContents = tableContentsGrid;

      toolBar.addSeparator();

      toolBar.add(new JLabel(I18n.getMessage("InspectVdxFile.CboMaxRecords") + ": "));
      toolBar.add(cboMaxRecords);
      cboMaxRecords.setMinimumSize(new Dimension(100, 5));
      cboMaxRecords.setMaximumRowCount(20);
      cboMaxRecords.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            final int selMaxRecords = (Integer) cboMaxRecords.getSelectedItem();
            if (selMaxRecords != maxRecords)
            {
               maxRecords = selMaxRecords;
               updateContents();
            }
         }
      });

      for (final int val : new int[] { 1000, 2500, 5000, 10000, 25000, 50000, 100000 })
         cboMaxRecords.addItem(val);

      maxRecords = 10000;
      cboMaxRecords.setSelectedIndex(3);
   }

   /**
    * Create a toggle button for switching the contents type.
    */
   private JToggleButton createSwitchContentsToggleButton(final TableContents tableContents, String iconName,
         String toolTipText)
   {
      final JToggleButton toggleButton = new JToggleButton(ImageCache.getIcon(iconName));
      toggleButton.setToolTipText(toolTipText);
      ButtonUtils.setToolButtonProperties(toggleButton);

      toggleButton.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            currentContents = tableContents;
            updateContents();
            contentsCards.show(contents, tableContents.getName());
         }
      });

      return toggleButton;
   }

   /**
    * Set the object that is displayed. This has to be a {@link File} object.
    */
   @Override
   public void setObject(Object o)
   {
      final MainWindow mainWin = MainWindow.getInstance();
      final File inFile = (File) o;

      try
      {
         mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         reader = new VdxFileReader(inFile);

         setName(inFile.getName());
         updateTables();

         updateContents();
      }
      catch (IOException e)
      {
         Dialogs
               .showExceptionDialog(e, I18n.formatMessage("VdxBrowser.ErrOpenFile", new Object[] { inFile.getPath() }));
         return;
      }
      finally
      {
         mainWin.setCursor(Cursor.getDefaultCursor());
      }
   }

   /**
    * Update the list of tables
    */
   public void updateTables()
   {
      final String selectedTableName = (String) cboTables.getSelectedItem();

      cboTables.removeAllItems();

      final Set<String> tableNamesSet = reader.getSectionNames();
      final String[] tableNames = new String[tableNamesSet.size()];
      tableNamesSet.toArray(tableNames);

      if (cbxTablesSorted.isSelected())
         Arrays.sort(tableNames);

      for (final String tableName : tableNames)
      {
         cboTables.addItem(tableName);

         if (tableName.equals(selectedTableName))
            cboTables.setSelectedIndex(cboTables.getItemCount() - 1);
      }
   }

   /**
    * Update the contents of the page.
    */
   @Override
   public void updateContents()
   {
      final Logger logger = Logger.getLogger(getClass());

      if (currentContents == null)
         return;

      final String newSelectedTableName = (String) cboTables.getSelectedItem();
      if (newSelectedTableName == null)
         return;

      if (selectedTableName != null && !selectedTableName.equals(newSelectedTableName))
         reader.removeSectionContents(selectedTableName);

      selectedTableName = newSelectedTableName;

      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

               final VdxSection selectedTable = reader.getSection(newSelectedTableName);
               currentContents.setTable(selectedTable, maxRecords);

               final int numRecords = selectedTable.getNumElements();
               if (numRecords >= maxRecords)
               {
                  logger.warn(I18n.formatMessage("InspectVdxFile.WarnListTruncated",
                        new Object[] { maxRecords, numRecords }));
               }
               else
               {
                  logger.info(I18n.formatMessage("InspectVdxFile.RecordsLoaded",
                        new Object[] { numRecords }));
               }
            }
            catch (IOException e)
            {
               Dialogs.showExceptionDialog(e, "Failed to show VDX section " + newSelectedTableName);
            }
            finally
            {
               setCursor(Cursor.getDefaultCursor());
            }
         }
      });
   }
}
