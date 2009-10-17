package org.freebus.fts.gui;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.vdx.VdxProgress;
import org.freebus.fts.vdx.VdxSection;

/**
 * A browser for a .vdx file
 */
public class VdxBrowser extends Composite implements VdxProgress
{
   private final Map<Integer,VdxSection> sections;
   private final Combo cbxSection;
   private final ProgressBar prbLoad;
   private final Table tblElems, tblValues;
   private final Label lblLoad;
   private long progressTotal, progressCurrent;
   private boolean progressUpdating = false;
   private int currentSectionId = -1;
   
   /**
    * Create a new vdx browser.
    */
   public VdxBrowser(Composite parent, Map<Integer,VdxSection> sections)
   {
      super(parent, SWT.FLAT);
      this.sections = sections;
      setLayout(new FormLayout());

      TableColumn tabColumn;
      FormData formData;
      Label lbl;

      cbxSection = new Combo(this, SWT.DEFAULT);

      lbl = new Label(this, SWT.FLAT);
      lbl.setAlignment(SWT.BOTTOM);
      lbl.setText(I18n.getMessage("vdx_browser_progress")+" ");
      lbl.pack();
      formData = new FormData();
      formData.left = new FormAttachment(0);
      formData.bottom = new FormAttachment(cbxSection, -2);
      lbl.setLayoutData(formData);

      lblLoad = new Label(this, SWT.FLAT);
      lblLoad.setAlignment(SWT.BOTTOM);
      lblLoad.setText("0%");
      lblLoad.pack();
      formData = new FormData();
      formData.left = new FormAttachment(lbl, 0);
      formData.bottom = new FormAttachment(cbxSection, -2);
      lblLoad.setLayoutData(formData);

      prbLoad = new ProgressBar(this, SWT.SMOOTH);
      formData = new FormData();
      formData.left = new FormAttachment(lblLoad, 1);
      formData.right = new FormAttachment(100);
      formData.top = new FormAttachment(1);
      prbLoad.setLayoutData(formData);
      
      formData = new FormData();
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      formData.top = new FormAttachment(prbLoad, 1);
      cbxSection.setLayoutData(formData);
      cbxSection.setVisibleItemCount(20);
      cbxSection.addListener(SWT.Selection, new Listener() { public void handleEvent(Event e) { onSectionSelected(); } });

      tblElems = new Table(this, SWT.BORDER);
      formData = new FormData();
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      formData.top = new FormAttachment(cbxSection, 0);
      formData.bottom = new FormAttachment(40);
      tblElems.setLayoutData(formData);
      tblElems.addListener(SWT.Selection, new Listener() { public void handleEvent(Event e) { onElemSelected(); } });

      tabColumn = new TableColumn(tblElems, SWT.RIGHT);
      tabColumn.setText("id");
      tabColumn = new TableColumn(tblElems, SWT.NULL);
      tabColumn.setText("name");

      tblValues = new Table(this, SWT.BORDER);
      formData = new FormData();
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      formData.top = new FormAttachment(tblElems, 0);
      formData.bottom = new FormAttachment(100);
      tblValues.setLayoutData(formData);

      tabColumn = new TableColumn(tblValues, SWT.NULL);
      tabColumn.setText("key");
      tabColumn = new TableColumn(tblValues, SWT.NULL);
      tabColumn.setText("value");
   }

   /**
    * Update the contents of the browser.
    * Call this if the sections have changed.
    */
   public void updateContents()
   {
      final Set<Integer> keys = sections.keySet();
      String[] items = new String[keys.size()];

      currentSectionId = -1;

      int i = 0;
      for (Integer key: keys)
         items[i++] = key.toString()+": "+sections.get(key).getName();
      cbxSection.setItems(items);

      if (items.length > 0)
      {
         cbxSection.select(0);
         onSectionSelected();
      }
   }

   /**
    * An entry of the section combobox was selected.
    */
   protected void onSectionSelected()
   {
      tblElems.removeAll();

      final String sel = cbxSection.getItem(cbxSection.getSelectionIndex());
      final int selIdx = sel.indexOf(':');
      currentSectionId = Integer.parseInt(sel.substring(0, selIdx));
      final VdxSection section = sections.get(currentSectionId);

      final Set<Integer> ids = section.getElementIds();
      final Iterator<Integer> it = ids.iterator();
      final int nameFieldIdx = section.getNameFieldIdx();
      int id;

      while (it.hasNext())
      {
         id = it.next();
         TableItem item = new TableItem(tblElems, SWT.NULL);
         item.setText(0, Integer.toString(id));
         if (nameFieldIdx>=0) item.setText(1, section.getElement(id)[nameFieldIdx]);
      }
      
      tblElems.getColumn(0).pack();
      tblElems.getColumn(1).pack();

      if (!ids.isEmpty())
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
      final int id = Integer.parseInt(tblElems.getSelection()[0].getText(0));

      final VdxSection section = sections.get(currentSectionId);
      final Vector<String> fields = section.getFields();
      final String[] values = section.getElement(id);

      for (int i=0; i<fields.size(); ++i)
      {
         TableItem item = new TableItem(tblValues, SWT.NULL);
         item.setText(0, fields.get(i));
         item.setText(1, values[i]);
      }
      
      tblValues.getColumn(0).pack();
      tblValues.getColumn(1).pack();
   }

   @Override
   public synchronized void setProgress(final long progress)
   {
      if (progressCurrent==progress) return;
      progressCurrent = progress;

      if (progressUpdating) return;
      progressUpdating = true;

      Display.getDefault().asyncExec(new Runnable()
      {
         public void run()
         {
            if (prbLoad.isDisposed()) return;
            final int progrPerc = (int)(100*(progress+1)/progressTotal);
            prbLoad.setSelection(progrPerc);
            lblLoad.setText(Integer.toString(progrPerc)+"%");
            update();
            progressUpdating = false;
         }
      });
   }

   @Override
   public synchronized void setTotal(long total)
   {
      progressTotal = total;
      progressCurrent = 0;
      progressUpdating = false;

      Display.getDefault().asyncExec(new Runnable()
      {
         public void run()
         {
           if (prbLoad.isDisposed()) return;
           prbLoad.setMinimum(0);
           prbLoad.setMaximum(100);
         }
       });
   }
}
