package org.freebus.fts.products.services.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.services.ProgramService;

/**
 * JPA {@link Program} service.
 */
public final class JpaProgramService implements ProgramService
{
   private final EntityManager entityManager;

   JpaProgramService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public Program getProgram(int id) throws PersistenceException
   {
      return entityManager.find(Program.class, id);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Program> getPrograms() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select p from Program p order by p.name");

      return query.getResultList();
   }

   @Override
   public void persist(Program program)
   {
      entityManager.persist(program);
   }

   @Override
   public Program merge(Program program)
   {
      return entityManager.merge(program);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Program> findProgram(Manufacturer manufacturer, int deviceType)
   {
      final Query query = entityManager.createQuery("select p from Program p where p.manufacturer=?1 and p.deviceType=?2");
      query.setParameter(1, manufacturer);
      query.setParameter(2, deviceType);

      return query.getResultList();
   }
}
