package org.freebus.fts.products.services.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.freebus.fts.products.Program;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProgramService;

public final class JpaProgramService implements ProgramService
{
   private final EntityManager entityManager;

   JpaProgramService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public Program getProgram(int id) throws DAOException
   {
      return entityManager.find(Program.class, id);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Program> getPrograms() throws DAOException
   {
      final Query query = entityManager.createQuery("select p from Program p order by p.name");

      return query.getResultList();
   }
}
