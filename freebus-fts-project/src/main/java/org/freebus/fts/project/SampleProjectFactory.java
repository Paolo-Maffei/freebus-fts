package org.freebus.fts.project;

import org.freebus.fts.project.internal.I18n;

/**
 * Factory that creates a project that gets initialized with example values.
 */
public final class SampleProjectFactory
{
   /**
    * Creates a project that gets initialized with example values.
    */
   public static Project newProject()
   {
      final Project project = new Project();
      project.setName(I18n.getMessage("SampleProjectFactory.ProjectName"));
   
      final Area area = new Area();
      area.setName(I18n.getMessage("SampleProjectFactory.Area1"));
      area.setAddress(1);
      project.add(area);
      
      final Line line1 = new Line();
      line1.setName(I18n.getMessage("SampleProjectFactory.Line1"));
      line1.setAddress(1);
      area.add(line1);

      final Line line2 = new Line();
      line2.setName(I18n.getMessage("SampleProjectFactory.Line2"));
      line2.setAddress(2);
      area.add(line2);


      final Building building = new Building();
      building.setName(I18n.getMessage("SampleProjectFactory.Building1"));
      project.add(building);

      final Room room1 = new Room();
      room1.setName(I18n.getMessage("SampleProjectFactory.Room1"));
      building.add(room1);

      final Room room2 = new Room();
      room2.setName(I18n.getMessage("SampleProjectFactory.Room2"));
      building.add(room2);


      final Device device1 = new Device();
      device1.setAddress(31);
      line1.add(device1);
      room1.add(device1);

      final Device device2 = new Device();
      device2.setAddress(32);
      line1.add(device2);
      room2.add(device2);

      final Device device3 = new Device();
      device3.setAddress(33);
      line2.add(device3);
      room2.add(device3);

      
      final MainGroup mainGroup1 = new MainGroup();
      mainGroup1.setAddress(4);
      project.add(mainGroup1);

      final MidGroup midGroup1 = new MidGroup();
      midGroup1.setAddress(5);
      mainGroup1.add(midGroup1);

      final MidGroup midGroup2 = new MidGroup();
      midGroup1.setAddress(6);
      mainGroup1.add(midGroup2);

      final SubGroup group1 = new SubGroup();
      group1.setAddress(101);
      midGroup1.add(group1);

      final SubGroup group2 = new SubGroup();
      group2.setAddress(102);
      midGroup1.add(group2);

      return project;
   }

   //
   // It is not allowed to create objects of this class
   //
   private SampleProjectFactory()
   {     
   }
}
