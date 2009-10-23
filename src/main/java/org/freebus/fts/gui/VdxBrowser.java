package org.freebus.fts.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.vdx.VdxFileReader;
import org.freebus.fts.vdx.VdxSection;

/**
 * A browser for a .vdx file
 */
public class VdxBrowser extends Composite
{
   private final VdxFileReader reader;
   private final Combo cboSection;
   private final Table tblElems, tblValues;
   private final Button cbxSort;
   private final Group grpOthers;
   private long progressTotal, progressCurrent;
   private boolean progressUpdating = false;
   private String[] sectionNames = null;
   private VdxSection section = null;

   /**
    * Create a new vdx browser.
    * 
    * @throws IOException
    */
   public VdxBrowser(Composite parent, String fileName) throws IOException
   {
      super(parent, SWT.FLAT);

      reader = new VdxFileReader(fileName);
      setLayout(new FormLayout());

      TableColumn tabColumn;
      FormData formData;

      cboSection = new Combo(this, SWT.DEFAULT);

      final Label lblCap = new Label(this, SWT.FLAT);
      lblCap.setText(I18n.getMessage("VdxBrowser.Caption"));
      formData = new FormData();
      formData.left = new FormAttachment(2);
      formData.top = new FormAttachment(1);
      lblCap.setLayoutData(formData);

      cbxSort = new Button(this, SWT.CHECK);
      cbxSort.setText(I18n.getMessage("VdxBrowser_SortSections"));
      cbxSort.pack();
      formData = new FormData();
      formData.right = new FormAttachment(98);
      formData.top = new FormAttachment(1);
      cbxSort.setLayoutData(formData);
      cbxSort.addListener(SWT.Selection, new Listener()
      {
         public void handleEvent(Event e)
         {
            getDisplay().readAndDispatch();
            updateContents();
         }
      });

      formData = new FormData();
      formData.left = new FormAttachment(2);
      formData.right = new FormAttachment(98);
      formData.top = new FormAttachment(lblCap, 8);
      cboSection.setLayoutData(formData);
      cboSection.setVisibleItemCount(20);
      cboSection.addListener(SWT.Selection, new Listener()
      {
         public void handleEvent(Event e)
         {
            onSectionSelected();
         }
      });

      tblElems = new Table(this, SWT.BORDER);
      formData = new FormData();
      formData.left = new FormAttachment(2);
      formData.right = new FormAttachment(98);
      formData.top = new FormAttachment(cboSection, 2);
      formData.bottom = new FormAttachment(40);
      tblElems.setLayoutData(formData);
      tblElems.addListener(SWT.Selection, new Listener()
      {
         public void handleEvent(Event e)
         {
            onElemSelected();
         }
      });

      tabColumn = new TableColumn(tblElems, SWT.RIGHT);
      tabColumn.setText("id");
      tabColumn = new TableColumn(tblElems, SWT.NULL);
      tabColumn.setText("name");

      final Label lblValues = new Label(this, SWT.FLAT);
      lblValues.setText(I18n.getMessage("VdxBrowser.Values_Caption"));
      lblValues.pack();
      formData = new FormData();
      formData.left = new FormAttachment(2);
      formData.top = new FormAttachment(tblElems, 4);
      lblValues.setLayoutData(formData);

      tblValues = new Table(this, SWT.BORDER);
      formData = new FormData();
      formData.left = new FormAttachment(2);
      formData.right = new FormAttachment(98);
      formData.top = new FormAttachment(lblValues, 2);
      formData.bottom = new FormAttachment(90);
      tblValues.setLayoutData(formData);
      tblValues.addListener(SWT.Selection, new Listener()
      {
         public void handleEvent(Event e)
         {
            onValueSelected();
         }
      });

      tabColumn = new TableColumn(tblValues, SWT.NULL);
      tabColumn.setText("key");
      tabColumn = new TableColumn(tblValues, SWT.NULL);
      tabColumn.setText("value");

      grpOthers = new Group(this, SWT.FLAT);
      final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
      rowLayout.marginTop = 8;
      rowLayout.spacing = 2;
      grpOthers.setLayout(rowLayout);
      grpOthers.setText(I18n.getMessage("VdxBrowser.Others_Caption"));
      formData = new FormData();
      formData.left = new FormAttachment(2);
      formData.right = new FormAttachment(98);
      formData.top = new FormAttachment(tblValues, 2);
      formData.height = 80;
      formData.bottom = new FormAttachment(98);
      grpOthers.setLayoutData(formData);

      updateContents();
   }

   /**
    * Update the contents of the browser. Call this if the sections have
    * changed.
    */
   public void updateContents()
   {
      final Set<String> namesSet = reader.getSectionNames();

      sectionNames = new String[namesSet.size()];
      namesSet.toArray(sectionNames);
      if (cbxSort.getSelection()) Arrays.sort(sectionNames);

      cboSection.removeAll();
      for (int i = 0; i < sectionNames.length; ++i)
         cboSection.add(sectionNames[i]);

      if (sectionNames.length > 0)
      {
         cboSection.select(0);
         onSectionSelected();
      }
   }

   /**
    * An entry of the section combo box was selected.
    */
   protected void onSectionSelected()
   {
      tblElems.removeAll();

      final int selectedIdx = cboSection.getSelectionIndex();
      if (selectedIdx < 0 || selectedIdx >= sectionNames.length) return;

      final String sectionName = sectionNames[selectedIdx];
      try
      {
         section = reader.getSection(sectionName);
         reader.removeSection(sectionName);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         MessageBox mbox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
         mbox.setMessage(e.getMessage());
         mbox.open();
         return;
      }

      // Find a suitable name field for the list
      final String[] fieldNames = section.getHeader().fields;
      int nameIdx;
      for (nameIdx = 0; nameIdx < fieldNames.length - 1; ++nameIdx)
      {
         if (!fieldNames[nameIdx].endsWith("_id") &&
             !fieldNames[nameIdx].endsWith("_number"))
            break;
      }
      if (nameIdx >= fieldNames.length) nameIdx = 0;

      final int numElems = section.getNumElements();
      for (int i = 0; i < numElems; ++i)
      {
         final TableItem item = new TableItem(tblElems, SWT.NULL);
         item.setText(0, Integer.toString(i));
         item.setText(1, section.getValue(i, nameIdx));
      }

      tblElems.getColumn(0).pack();
      tblElems.getColumn(1).pack();

      if (numElems > 0)
      {
         tblElems.select(0);
         onElemSelected();
      }
   }

   /**
    * An entry of the elements table was selected.
    */
   protected void onElemSelected()
   {
      tblValues.removeAll();
      grpOthers.setVisible(false);

      final int id = tblElems.getSelectionIndex();
      if (section == null || id < 0) return;

      final String[] fieldNames = section.getHeader().fields;
      final String[] values = section.getElementValues(id);

      for (int i = 0; i < fieldNames.length; ++i)
      {
         TableItem item = new TableItem(tblValues, SWT.NULL);
         item.setText(0, fieldNames[i]);
         item.setText(1, values[i]);
      }

      tblValues.getColumn(0).pack();
      tblValues.getColumn(1).pack();
   }

   /**
    * An entry of the values table was selected.
    */
   protected void onValueSelected()
   {
      final int idx = tblValues.getSelectionIndex();
      grpOthers.setVisible(idx >= 0);
      if (idx < 0) return;

      for (Control child: grpOthers.getChildren())
         child.dispose();

      final String fieldName = tblValues.getItem(idx).getText(0);
      final String fieldValue = tblValues.getItem(idx).getText(1);
      final String curSectionName = section.getHeader().name;
      VdxSection section;
      int fieldIdx;

      grpOthers.setText(I18n.getMessage("VdxBrowser.Others_Caption").replace("%1", fieldName));

      for (final String sectionName: reader.getSectionNames())
      {
         if (sectionName.equals(curSectionName)) continue;

         fieldIdx = reader.getSectionHeader(sectionName).getIndexOf(fieldName);
         if (fieldIdx < 0) continue;

         final Combo cbo = new Combo(grpOthers, SWT.READ_ONLY|SWT.DROP_DOWN);
         cbo.setData(sectionName);
         cbo.addListener(SWT.Selection, new Listener()
         {
            public void handleEvent(Event e)
            {
               final String sectionName = (String) e.widget.getData();
               int i;
               for (i = sectionNames.length-1; i >= 0; --i)
                  if (sectionNames[i].equals(sectionName)) break;

               if (i >= 0)
               {
                  cboSection.select(i);
                  onSectionSelected();
               }
            }
         });

         try
         {
            section = reader.getSection(sectionName);
         }
         catch (IOException e)
         {
            e.printStackTrace();
            continue;
         }

         final int numElems = section.getNumElements();
         int matches = 0;
         for (int i = 0; i < numElems; ++i)
         {
            if (!fieldValue.equals(section.getValue(i, fieldIdx))) continue;
            cbo.add("#" + Integer.toString(i));
            ++matches;
         }

         reader.removeSection(sectionName);

         cbo.pack();
         cbo.add("[ " + Integer.toString(matches) + " " + sectionName + " ]", 0);
         cbo.select(0);
      }

      grpOthers.pack();
      grpOthers.layout();
   }
}
