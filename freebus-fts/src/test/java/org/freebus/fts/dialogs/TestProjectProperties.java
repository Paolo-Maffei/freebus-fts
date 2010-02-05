package org.freebus.fts.dialogs;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.JFCTestHelper;
import junit.extensions.jfcunit.WindowMonitor;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.finder.NamedComponentFinder;

import org.freebus.fts.project.Project;
import org.junit.Test;

public class TestProjectProperties extends JFCTestCase
{
   private JFrame parentFrame;
   private Project project;
   private ProjectProperties projectProperties;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();

      if (parentFrame == null)
      {
         parentFrame = new JFrame();
         parentFrame.setVisible(true);

         WindowMonitor.start();
      }
      
      setHelper(new JFCTestHelper());

      project = new Project();
      project.setName("project-715");
      project.setDescription("description-715");
      project.setId(715);

      projectProperties = new ProjectProperties(parentFrame);
      projectProperties.setVisible(true);
   }

   @Override
   public void tearDown() throws Exception
   {
      super.tearDown();
      projectProperties = null;
   }

   @Test
   public final void testProjectProperties()
   {
      NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, null);
      finder.setWait(30);

      projectProperties.setProject(project);
      assertEquals(project, projectProperties.getProject());

      finder.setName("edtName");
      final JTextField edtName = (JTextField) finder.find();
      assertNotNull(edtName);
      assertEquals("project-715", edtName.getText());

      finder.setName("edtDescription");
      final JTextArea edtDescription = (JTextArea) finder.find();
      assertNotNull(edtDescription);
      assertEquals("description-715", edtDescription.getText());

      edtName.setText("new-name");
      assertEquals("project-715", project.getName());

      edtDescription.setText("new-description");
      assertEquals("description-715", project.getDescription());

      finder.setName("btnOk");
      JButton btnOk = (JButton) finder.find();
      assertNotNull(btnOk);
      getHelper().enterClickAndLeave(new MouseEventData(this, btnOk));
      assertFalse(projectProperties.isVisible());

      assertEquals("new-name", project.getName());
      assertEquals("new-description", project.getDescription());
   }
}
