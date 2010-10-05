package org.freebus.fts.products.services.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.products.Program;
import org.freebus.fts.products.ProgramDescription;
import org.freebus.fts.products.services.ProgramDescriptionService;

public final class JpaProgramDescriptionService implements ProgramDescriptionService
{
   private final EntityManager entityManager;

   JpaProgramDescriptionService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public ProgramDescription getProgramDescription(Program prog)
   {
      final Query query = entityManager.createQuery("select pd from ProgramDescription pd where pd.programId=?1");
      query.setParameter(1, prog.getId());
      return (ProgramDescription) query.getSingleResult();
   }

   @Override
   public void persist(ProgramDescription desc) throws PersistenceException
   {
      entityManager.persist(desc);
   }

   @Override
   public ProgramDescription merge(ProgramDescription desc) throws PersistenceException
   {
      return entityManager.merge(desc);
   }
}
