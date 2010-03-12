package org.freebus.fts.products.services.vdx;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxEntityManager;
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

   @SuppressWarnings("unchecked")
   @Override
   public List<FunctionalEntity> getFunctionalEntities() throws PersistenceException
   {
      return (List<FunctionalEntity>) manager.fetchAll(FunctionalEntity.class);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<FunctionalEntity> getFunctionalEntities(Manufacturer m) throws PersistenceException
   {
      final List<FunctionalEntity> result = new LinkedList<FunctionalEntity>();

      for (FunctionalEntity entity : (List<FunctionalEntity>) manager.fetchAll(FunctionalEntity.class))
         if (entity.getManufacturer() == m) result.add(entity);

      return result;
   }

   @Override
   public FunctionalEntity getFunctionalEntity(int id) throws PersistenceException
   {
      return manager.fetch(FunctionalEntity.class, id);
   }

   @Override
   public void save(FunctionalEntity funcEnt) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
