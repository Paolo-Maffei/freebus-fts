package org.freebus.fts.products.services.vdx;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.common.vdx.VdxEntityManager;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.FunctionalEntityService;

/**
 * Data access object for functional entities stored in a VD_ file.
 */
public final class VdxFunctionalEntityService implements FunctionalEntityService
{
   private final VdxEntityManager manager;

   VdxFunctionalEntityService(VdxEntityManager manager)
   {
      this.manager = manager;
   }

   @Override
   public List<FunctionalEntity> getFunctionalEntities() throws PersistenceException
   {
      @SuppressWarnings("unchecked")
      final List<FunctionalEntity> result = (List<FunctionalEntity>) manager.fetchAll(FunctionalEntity.class);
      return result; 
   }

   @Override
   public List<FunctionalEntity> getFunctionalEntities(Manufacturer m) throws PersistenceException
   {
      @SuppressWarnings("unchecked")
      final List<FunctionalEntity> entities = (List<FunctionalEntity>) manager.fetchAll(FunctionalEntity.class);

      final List<FunctionalEntity> result = new LinkedList<FunctionalEntity>();
      for (FunctionalEntity entity : entities)
         if (entity.getManufacturer() == m) result.add(entity);

      return result;
   }

   @Override
   public FunctionalEntity getFunctionalEntity(int id) throws PersistenceException
   {
      return manager.fetch(FunctionalEntity.class, id);
   }
}
