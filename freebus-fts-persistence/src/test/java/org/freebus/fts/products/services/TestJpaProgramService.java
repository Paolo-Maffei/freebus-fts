package org.freebus.fts.products.services;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.List;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.exception.DAOException;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Mask;
import org.freebus.fts.products.Program;
import org.freebus.fts.test_utils.ProductsTestCase;
import org.junit.Before;
import org.junit.Test;


public class TestJpaProgramService extends ProductsTestCase
{
   private ProgramService progService;

   @Before
   public void setUp() throws Exception
   {
      progService = getJpaProductsFactory().getProgramService();

      final Manufacturer manu = new Manufacturer(118, "manu-118");
      getJpaProductsFactory().getManufacturerService().persist(manu);

      progService.persist(new Program(111, "Program-11", manu, new Mask()));
      progService.persist(new Program(112, "Program-12", manu, new Mask()));
      progService.persist(new Program(113, "Program-13", manu, new Mask()));

      DatabaseResources.getEntityManager().flush();
      DatabaseResources.getEntityManager().getTransaction().setRollbackOnly();
   }

   @Test
   public final void testGetProgram() throws DAOException
   {
      final Program prog = progService.getProgram(112);
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
      final Manufacturer manu88 = new Manufacturer(88, "manu-88");
      getJpaProductsFactory().getManufacturerService().persist(manu88);

      final Program prog = new Program(0, "Program-88", manu88, new Mask());
      final byte[] data = new byte[] { 0, 1, 2, 45, 0, 127, 4, -128, 0, 7, 1 };
      prog.setEepromData(data);

      progService.persist(prog);
      DatabaseResources.getEntityManager().flush();

      assertNotSame(0, prog.getId());

      final Program progLoaded =  progService.getProgram(prog.getId());
      assertNotNull(progLoaded);

      assertEquals(prog.getId(), progLoaded.getId());
      assertArrayEquals(data, progLoaded.getEepromData());
   }
}
