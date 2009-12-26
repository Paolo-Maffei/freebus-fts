package org.freebus.fts.project.service.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.freebus.fts.project.Area;
import org.freebus.fts.project.service.AreaService;


public final class JpaAreaService implements AreaService
{
   private final EntityManager entityManager;

   JpaAreaService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public Area getArea(int id) throws PersistenceException
   {
      return entityManager.find(Area.class, id);
   }
}
