package org.freebus.fts.products.services;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.BcuType;
import org.freebus.fts.products.Mask;
import org.freebus.fts.test_utils.ProductsTestCase;
import org.junit.Before;
import org.junit.Test;


public class TestJpaMaskService extends ProductsTestCase
{
   private MaskService maskService;

   @Before
   public void setUp() throws Exception
   {
      maskService = getJpaProductsFactory().getMaskService();

      DatabaseResources.getEntityManager().flush();
      DatabaseResources.getEntityManager().getTransaction().setRollbackOnly();
   }

   @Test
   public final void persist() throws DAOException
   {
      final Mask mask = new Mask();
      mask.setBcuType(new BcuType(0, "bcu-1", "cpu-1"));

      maskService.persist(mask);
   }
}
