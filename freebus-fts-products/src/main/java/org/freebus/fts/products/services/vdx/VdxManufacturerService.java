package org.freebus.fts.products.services.vdx;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.common.vdx.VdxEntityManager;
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

   @Override
   public List<Manufacturer> getManufacturers() throws PersistenceException
   {
      @SuppressWarnings("unchecked")
      final List<Manufacturer> result = (List<Manufacturer>) manager.fetchAll(Manufacturer.class);
      return result; 
   }

   @Override
   public List<Manufacturer> getActiveManufacturers() throws PersistenceException
   {
      if (activeManufacturers == null)
      {
         synchronized (manager)
         {
            if (activeManufacturers == null)
            {
               activeManufacturers = new LinkedList<Manufacturer>();
               
               @SuppressWarnings("unchecked")
               final List<Manufacturer> manufacturers = (List<Manufacturer>) manager.fetchAll(Manufacturer.class);

               for (Manufacturer m : manufacturers)
               {
                  if (!functionalEntityService.getFunctionalEntities(m).isEmpty()) activeManufacturers.add(m);
               }
            }
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
   public void save(Manufacturer manufacturer) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public void save(List<Manufacturer> manufacturers) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
