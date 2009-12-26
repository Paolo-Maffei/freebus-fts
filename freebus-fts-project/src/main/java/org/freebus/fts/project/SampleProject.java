package org.freebus.fts.project;

import org.freebus.fts.project.internal.I18n;

/**
 * A project that gets initialized with example values.
 */
public class SampleProject extends Project
{
   /**
    * A project that gets initialized with example values.
    */
   public SampleProject()
   {
      setName(I18n.getMessage("SampleProject.ProjectName"));
   
      final Area area = new Area();
      area.setName(I18n.getMessage("SampleProject.Area1"));
      area.setAddress(1);
      getAreas().add(area);
      
      final Line line1 = new Line();
      line1.setName(I18n.getMessage("SampleProject.Line1"));
      line1.setAddress(2);
      area.add(line1);

      final Line line2 = new Line();
      line2.setName(I18n.getMessage("SampleProject.Line2"));
      line2.setAddress(2);
      area.add(line2);

      final Device device1 = new Device();
      device1.setAddress(3);
      line1.add(device1);

      final Device device2 = new Device();
      device2.setAddress(3);
      line1.add(device2);

      final Device device3 = new Device();
      device3.setAddress(3);
      line2.add(device3);
   }
}
