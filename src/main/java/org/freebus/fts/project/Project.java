
package org.freebus.fts.project;

import org.freebus.fts.utils.I18n;

/**
 * Main class for a Freebus/ETS project
 */
public class Project
{
   private String name;
   private final Buildings buildings = new Buildings(); 
   private final Areas areas = new Areas(); 
   private final MainGroups mainGroups = new MainGroups(); 

   /**
    * Create a new project.
    * 
    * @param name is the name of the project.
    */
   public Project(String name)
   {
      this.name = name;
   }
   
   /**
    * Set the name of the project
    * 
    * @param name is the new name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the name of the project
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * @return the buildings.
    */
   public Buildings getBuildings()
   {
      return buildings;
   }
   
   /**
    * @return the areas.
    */
   public Areas getAreas()
   {
      return areas;
   }
   
   /**
    * @return the main-groups.
    */
   public MainGroups getMainGroups()
   {
      return mainGroups;
   }

   /**
    * Create a new sample project.
    * 
    * @return the created project.
    */
   static public Project createSampleProject()
   {
      final Project project = new Project(I18n.getMessage("proj_sample_name"));
      final Building building = project.buildings.createBuilding(I18n.getMessage("proj_sample_house"));

      final Floor floor1 = building.createFloor(I18n.getMessage("proj_sample_floor1"));
      floor1.createRoom(I18n.getMessage("proj_sample_room1_1"));
      floor1.createRoom(I18n.getMessage("proj_sample_room1_2"));
      floor1.createRoom(I18n.getMessage("proj_sample_room1_3"));
      floor1.createRoom(I18n.getMessage("proj_sample_room1_4"));
      floor1.createRoom(I18n.getMessage("proj_sample_room1_5"));

      final Area area = project.areas.createArea(I18n.getMessage("proj_sample_area"));
      final Line line1 = area.createLine(I18n.getMessage("proj_sample_line1"));
      final Device sensor1 = line1.createDevice(I18n.getMessage("proj_sample_sensor1"));      
      final Device actor1 = line1.createDevice(I18n.getMessage("proj_sample_actor1"));

      final MainGroup mainGroup = project.mainGroups.createMainGroup(I18n.getMessage("proj_sample_group1"));
      final MiddleGroup midGroup1 = mainGroup.createMiddleGroup(I18n.getMessage("proj_sample_group1_1"));
      midGroup1.add(sensor1);
      midGroup1.add(actor1);

      project.areas.assignIds();

      return project;
   }
}
