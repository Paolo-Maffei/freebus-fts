package org.freebus.fts.products.services.vdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.common.vdx.VdxFileReader;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ManufacturerService;

/**
 * Data access object for manufacturers stored in a VD_ file.
 */
public final class VdxManufacturerService implements ManufacturerService
{
   private final VdxFileReader reader;
   private FunctionalEntityService functionalEntityService;
   private List<Manufacturer> manufacturers, activeManufacturers;

   VdxManufacturerService(VdxFileReader reader, FunctionalEntityService functionalEntityService)
   {
      this.reader = reader;
      this.functionalEntityService = functionalEntityService;
   }

   private synchronized void fetchData() throws PersistenceException
   {
      if (manufacturers != null) return;

      try
      {
         final Object[] arr = reader.getSectionEntries("manufacturer", Manufacturer.class);
         Arrays.sort(arr, new Comparator<Object>()
         {
            @Override
            public int compare(Object a, Object b)
            {
               return ((Manufacturer) a).getName().compareTo(((Manufacturer) b).getName());
            }
         });
         manufacturers = new ArrayList<Manufacturer>(arr.length);
         for (Object obj : arr)
            manufacturers.add((Manufacturer) obj);
      }
      catch (IOException e)
      {
         throw new PersistenceException(e);
      }
   }

   @Override
   public Manufacturer getManufacturer(int id) throws PersistenceException
   {
      if (manufacturers == null) fetchData();

      for (Manufacturer manufacturer : manufacturers)
         if (manufacturer.getId() == id) return manufacturer;

      throw new PersistenceException("Object not found, id=" + Integer.toString(id));
   }

   @Override
   public List<Manufacturer> getManufacturers() throws PersistenceException
   {
      if (manufacturers == null) fetchData();
      return manufacturers;
   }

   @Override
   public List<Manufacturer> getActiveManufacturers() throws PersistenceException
   {
      if (activeManufacturers == null)
      {
         synchronized (reader)
         {
            if (activeManufacturers == null)
            {
               activeManufacturers = new LinkedList<Manufacturer>();
               if (manufacturers == null) fetchData();

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
