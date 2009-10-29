package org.freebus.fts.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.freebus.fts.project.Floor;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.ImageCache;

/**
 * A tab page that shows the plan of a floor.
 */
public final class FloorPlanTab extends TabPage
{
   private final Canvas canvas = new Canvas(this, SWT.FLAT);
   private Floor floor = null;
   private Image floorPlanImage = null;

   /**
    * Create a new widget.
    * 
    * @param parent - the parent widget.
    */
   public FloorPlanTab(Composite parent)
   {
      super(parent);
      setTitle(I18n.getMessage("FloorPlan_Tab"));
      setPlace(SWT.CENTER);

      canvas.setBackground(new Color(getDisplay(), 255, 255, 255));
      canvas.setSize(150, 150);
      canvas.setLocation(20, 20);

      FormData formData = new FormData();
      formData.top = new FormAttachment(1);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      formData.bottom = new FormAttachment(100);
      canvas.setLayoutData(formData);
      
      canvas.addPaintListener(new PaintListener()
      {
         public void paintControl(PaintEvent e)
         {
            if (floor == null) return;

            GC gc = new GC(canvas);

            if (floorPlanImage != null)
               gc.drawImage(floorPlanImage, 0, 0);

//            gc.drawRectangle(10, 10, 40, 45);
//            gc.drawOval(65, 10, 30, 35);
//            gc.drawLine(130, 10, 90, 80);
//            gc.drawPolygon(new int[] { 20, 70, 45, 90, 70, 70 });
//            gc.drawPolyline(new int[] { 10, 120, 70, 100, 100, 130, 130, 75 });

            gc.dispose();            
         }
      });
   }

   /**
    * @return the floor
    */
   public Floor getFloor()
   {
      return floor;
   }

   /**
    * Set the floor that is shown.
    */
   @Override
   public void setObject(Object o)
   {
      floor = (Floor) o;
      setTitle(floor.getName());
      updateContents();
   }

   /**
    * Update the widget's contents.
    */
   @Override
   public void updateContents()
   {
      floorPlanImage = null;

      final String planFileName = floor.getPlanFileName();
      if (planFileName != null) floorPlanImage = ImageCache.getImage(planFileName);

      redraw();
   }
}
