package org.freebus.fts.products.services.vdx;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxEntityManager;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ManufacturerService;

/**
 * Data access object for manufacturers stored in a VD_ file.
 */
public final class VdxManufacturerService implements ManufacturerService
{
   private final VdxEntityManager manager;
   private final FunctionalEntityService functionalEntityService;
   private List<Manufacturer> activeManufacturers;

   VdxManufacturerService(VdxEntityManager manager, FunctionalEntityService functionalEntityService)
   {
      this.manager = manager;
      this.functionalEntityService = functionalEntityService;
   }

   @Override
   public Manufacturer getManufacturer(int id) throws PersistenceException
   {
      return manager.fetch(Manufacturer.class, id);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Manufacturer> getManufacturers() throws PersistenceException
   {
      return (List<Manufacturer>) manager.fetchAll(Manufacturer.class);
   }

   @SuppressWarnings("unchecked")
   @Override
   public synchronized List<Manufacturer> getActiveManufacturers() throws PersistenceException
   {
      if (activeManufacturers == null)
      {
         activeManufacturers = new LinkedList<Manufacturer>();

         for (Manufacturer m : (List<Manufacturer>) manager.fetchAll(Manufacturer.class))
         {
            if (!functionalEntityService.getFunctionalEntities(m).isEmpty())
               activeManufacturers.add(m);
         }
      }

      return activeManufacturers;
   }

   @Override
   public void remove(Manufacturer manufacturer) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public void saveIfMissing(Manufacturer manufacturer) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public Manufacturer merge(Manufacturer manufacturer) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public void persist(Manufacturer manufacturer) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public void save(Collection<Manufacturer> manufacturers) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
