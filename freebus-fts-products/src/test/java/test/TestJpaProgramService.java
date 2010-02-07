package test;

import static org.junit.Assert.*;

import java.util.List;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProgramService;
import org.junit.Before;
import org.junit.Test;

import test.internal.PersistenceTestCase;

public class TestJpaProgramService extends PersistenceTestCase
{
   private ProgramService progService;

   @Before
   @Override
   public void setUp() throws Exception
   {
      super.setUp();

      progService = getJpaProductsFactory().getProgramService();

      final Manufacturer manu = new Manufacturer(1, "manu-1");
      
      progService.save(new Program(11, "Program-11", manu));
      progService.save(new Program(12, "Program-12", manu));
      progService.save(new Program(13, "Program-13", manu));

      DatabaseResources.getEntityManager().flush();
   }

   @Test
   public final void testGetProgram() throws DAOException
   {
      final Program prog = progService.getProgram(11);
      assertNotNull(prog);
   }

   @Test
   public final void testGetPrograms()
   {
      final List<Program> progs = progService.getPrograms();
      assertNotNull(progs);
      assertEquals(3, progs.size());
   }

   @Test
   public final void testSave()
   {
      final Program prog = new Program(0, "Program-88", new Manufacturer(88, "manu-88"));
      final byte[] data = new byte[] { 0, 1, 2, 45, 0, 127, 4, -128, 0, 7, 1 };
      prog.setEepromData(data);

      progService.save(prog);
      DatabaseResources.getEntityManager().flush();

      assertNotSame(0, prog.getId());

      final Program progLoaded =  progService.getProgram(prog.getId());
      assertNotNull(progLoaded);

      assertEquals(prog.getId(), progLoaded.getId());
      assertArrayEquals(data, progLoaded.getEepromData());
   }
}
