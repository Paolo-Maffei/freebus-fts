package org.freebus.fts.components.parametereditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.freebus.fts.common.Environment;
import org.freebus.fts.persistence.db.ConnectionDetails;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SampleProjectFactory;
import org.freebus.fts.project.service.ProjectService;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestDeviceParamData
{
   private static final String persistenceUnitName = "test-full";
   private static final int projectId = 17;

   protected static final ConnectionDetails conDetails;

   static
   {
      Environment.init();

      conDetails = new ConnectionDetails(DriverType.HSQL_MEM, "db.TestDeviceParamData");

      final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(persistenceUnitName, conDetails);
      DatabaseResources.setEntityManagerFactory(emf);
   }

   @Test
   public final void stepCreateProject()
   {
      final EntityTransaction trans = DatabaseResources.getEntityManager().getTransaction();
      trans.begin();

      final Project project = SampleProjectFactory.newProject(persistenceUnitName);
      assertNotNull(project);

      project.setId(projectId);
      ProjectManager.getProjectFactory().getProjectService().save(project);

      trans.commit();
   }

   @Test
   public final void stepModifyParamData()
   {
      final ProjectService projectService = ProjectManager.getProjectFactory().getProjectService();

      final Project project = projectService.getProject(projectId);
      assertNotNull(project);

      final Device device = project.getAreas().get(0).getLines().get(0).getDevices().get(0);
      assertNotNull(device);

      final Program program = device.getProgram();
      assertNotNull(program);

      Map<Parameter,ParamData> paramDatas = DeviceParamData.createParamData(device);
      assertNotNull(paramDatas);

      // Get parameter from page "Test", parameter "Nach Reset sofort senden"
      Parameter param = program.getParameter(42856);
      ParamData data = paramDatas.get(param);
      assertNotNull(data);
      assertEquals(0, data.getValue());

      // Test the value also direct from the device
      assertEquals(Integer.valueOf(0), device.getDeviceParameter(param).getIntValue());

      // Modify the value
      data.setValue(1);

      // Apply all values to the device
      DeviceParamData.applyParamData(device, paramDatas);

      // Test if the value was properly applied
      assertEquals(Integer.valueOf(1), device.getDeviceParameter(param).getIntValue());
      assertEquals("1", device.getDeviceParameter(param).getValue());

      // Get again and see if the values are still Ok
      paramDatas = DeviceParamData.createParamData(device);
      data = paramDatas.get(param);
      assertNotNull(data);
      assertEquals(1, data.getValue());
   }
}
