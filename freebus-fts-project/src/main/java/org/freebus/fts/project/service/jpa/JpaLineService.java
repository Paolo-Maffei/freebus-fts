package org.freebus.fts.project.service.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.freebus.fts.project.Line;
import org.freebus.fts.project.service.LineService;


public final class JpaLineService implements LineService
{
   private final EntityManager entityManager;

   JpaLineService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public Line getLine(int id) throws PersistenceException
   {
      return entityManager.find(Line.class, id);
   }
}
