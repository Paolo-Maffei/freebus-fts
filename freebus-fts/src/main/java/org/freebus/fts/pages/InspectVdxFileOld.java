package org.freebus.fts.pages;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.freebus.fts.MainWindow;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.SortedListModel;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.persistence.vdx.VdxSection;
import org.freebus.fts.persistence.vdx.VdxSectionHeader;
import org.freebus.fts.utils.TableUtils;

/**
 * A page that displays the contents of a VDX file.
 */
public class InspectVdxFileOld extends AbstractPage
{
   private static final long serialVersionUID = 244934403536467189L;

   private transient VdxFileReader reader;

   private final JCheckBox cbxTablesSorted = new JCheckBox();
   private final JComboBox cboTables = new JComboBox();

   private final DefaultListModel lmRecords = new DefaultListModel();
   private final SortedListModel slmRecords = new SortedListModel(lmRecords);
   private final JList lstRecords = new JList(slmRecords);

   private final DefaultTableModel tbmFields = new DefaultTableModel();
   private final JTable tblFields = new JTable(tbmFields);
   private String selectedTableName;
   private int maxRecords = 8000;

   /**
    * Create a VDX browser page.
    */
   public InspectVdxFileOld()
   {
      setLayout(new GridBagLayout());

      final Insets insets = new Insets(2, 2, 2, 2);
      int row = -1;

      add(new JLabel(I18n.getMessage("InspectVdxFile.CboTables")), new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
      add(cboTables, new GridBagConstraints(1, row, 1, 1, 10, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
      cboTables.setMaximumRowCount(20);
      cboTables.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            updateRecords();
         }
      });

      add(Box.createHorizontalStrut(20), new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
      add(cbxTablesSorted, new GridBagConstraints(3, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
      cbxTablesSorted.setFocusable(false);
      cbxTablesSorted.setSelected("1".equals(Config.getInstance().getStringValue("InspectVdxFile.sortTables")));
      cbxTablesSorted.setText(I18n.getMessage("InspectVdxFile.CbxTablesSorted"));
      cbxTablesSorted.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Config.getInstance().put("InspectVdxFile.sortTables", cbxTablesSorted.isSelected() ? "1" : "0");
            updateContents();
         }
      });

      add(new JSeparator(), new GridBagConstraints(0, ++row, 4, 1, 10, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

      lstRecords.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent ev)
         {
            updateFields();
         }
      });
      final JScrollPane scpEntries = new JScrollPane(lstRecords);

      final String[] columnNames = new String[] { I18n.getMessage("InspectVdxFile.KeyColumn"), I18n.getMessage("InspectVdxFile.ValueColumn") };
      tbmFields.setColumnIdentifiers(columnNames);
      tblFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      final JScrollPane scpFields = new JScrollPane(tblFields);
      scpFields.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      final JSplitPane sppContents = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scpEntries, scpFields);
      add(sppContents, new GridBagConstraints(0, ++row, 4, 1, 1, 10, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, insets, 0, 0));
      sppContents.setResizeWeight(0.25);
      sppContents.setDividerLocation(250);
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
         updateContents();
      }
      catch (IOException e)
      {
         Dialogs.showExceptionDialog(e, I18n.formatMessage("VdxBrowser.ErrOpenFile", new Object[] { inFile.getPath() }));
         return;
      }
      finally
      {
         mainWin.setCursor(Cursor.getDefaultCursor());
      }
   }

   /**
    * Update the contents of the page.
    */
   @Override
   public void updateContents()
   {
      if (reader == null) return;

      cboTables.removeAllItems();

      final Set<String> tableNamesSet = reader.getSectionNames();
      final String[] tableNames = new String[tableNamesSet.size()];
      tableNamesSet.toArray(tableNames);

      if (cbxTablesSorted.isSelected())
         Arrays.sort(tableNames);

      for (final String tableName: tableNames)
         cboTables.addItem(tableName);

      updateRecords();
   }

   /**
    * Update the list of table records.
    */
   public void updateRecords()
   {
      lmRecords.clear();

      if (selectedTableName != null)
         reader.removeSectionContents(selectedTableName);

      selectedTableName = getSelectedTableName();
      if (selectedTableName == null) return;

      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               reader.removeSectionContents(selectedTableName);
               final VdxSectionHeader header = reader.getSectionHeader(selectedTableName);
               final VdxSection section = reader.getSection(selectedTableName);

               // Find a suitable key field
               int keyIdx = header.getIndexOf(selectedTableName + "_id");
               int nameIdx = -1;
               final int numFields = header.fields.length;
               for (int idx =  0; idx < numFields && (keyIdx < 0 || nameIdx < 0); ++idx)
               {
                  final String fieldName = header.fields[idx].toLowerCase();

                  if (nameIdx < 0)
                  {
                     if (fieldName.endsWith("_name")) nameIdx = idx;
                     else if (fieldName.endsWith("text")) nameIdx = idx;
                     else if (fieldName.startsWith("display")) nameIdx = idx;
                  }

                  if (keyIdx < 0 && fieldName.endsWith("_id")) keyIdx = idx;
               }
               if (keyIdx < 0) keyIdx = 0;

               int numRecords = section.getNumElements();
               if (numRecords > maxRecords) numRecords = maxRecords;

               lmRecords.setSize(numRecords);
               for (int i = 0; i < numRecords; ++i)
               {
                  final String keyStr = section.getValue(i, keyIdx);
                  if (nameIdx >= 0) lmRecords.set(i, section.getValue(i, nameIdx) + " [" + keyStr + ']');
                  else lmRecords.set(i, keyStr);
               }
               if (numRecords >= maxRecords)
                  Logger.getLogger(getClass()).warn(I18n.formatMessage("InspectVdxFile.WarnListTruncated", new Object[] { maxRecords }));

               if (numRecords > 0) lstRecords.setSelectedIndex(0);
               updateFields();
            }
            catch (IOException e)
            {
               Dialogs.showExceptionDialog(e, "Failed to update records");
            }
            finally
            {
               setCursor(Cursor.getDefaultCursor());
            }
         }
      });
   }

   /**
    * Update the list of record fields of the currently selected record.
    * @throws IOException if there is an error reading the VD_ file.
    */
   public void updateFields()
   {
      try
      {
         final String tableName = getSelectedTableName();
         final int recordIdx = getSelectedRecordIndex();

         if (tableName == null || recordIdx < 0)
         {
            tbmFields.setNumRows(0);
            return;
         }

         final VdxSectionHeader header = reader.getSectionHeader(tableName);
         final VdxSection section = reader.getSection(tableName);

         final int numFields = header.fields.length;
         tbmFields.setNumRows(numFields);

         for (int fieldIdx = 0; fieldIdx < numFields; ++fieldIdx)
         {
            tbmFields.setValueAt(header.fields[fieldIdx], fieldIdx, 0);
            tbmFields.setValueAt(section.getValue(recordIdx, fieldIdx), fieldIdx, 1);
         }

         TableUtils.pack(tblFields, 2);
      }
      catch (IOException e)
      {
         Dialogs.showExceptionDialog(e, "Failed to update fields");
      }
   }

   /**
    * @return the name of the selected table, or null if no table is selected.
    */
   public String getSelectedTableName()
   {
      return (String) cboTables.getSelectedItem();
   }

   /**
    * @return the index of the selected record, or -1 if no record is selected.
    */
   public int getSelectedRecordIndex()
   {
      final int sortedIdx = lstRecords.getSelectedIndex();
      if (sortedIdx < 0) return -1;
      return slmRecords.toUnsortedModelIndex(sortedIdx);
   }
}
