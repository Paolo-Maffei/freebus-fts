package org.freebus.fts.products.dao.vdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.dao.DAOException;
import org.freebus.fts.products.dao.DAONotFoundException;
import org.freebus.fts.products.dao.FunctionalEntityDAO;
import org.freebus.fts.products.dao.ManufacturerDAO;
import org.freebus.fts.vdx.VdxFileReader;

/**
 * Data access object for manufacturers stored in a VDX file.
 */
public final class VdxManufacturerDAO implements ManufacturerDAO
{
   private final VdxFileReader reader;
   private FunctionalEntityDAO functionalEntityDAO;
   private List<Manufacturer> manufacturers, activeManufacturers;

   VdxManufacturerDAO(VdxFileReader reader, FunctionalEntityDAO functionalEntityDAO)
   {
      this.reader = reader;
      this.functionalEntityDAO = functionalEntityDAO;
   }

   private synchronized void fetchData() throws DAOException
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
         throw new DAOException(e);
      }
   }

   @Override
   public Manufacturer getManufacturer(int id) throws DAOException
   {
      if (manufacturers == null) fetchData();

      for (Manufacturer manufacturer : manufacturers)
         if (manufacturer.getId() == id) return manufacturer;

      throw new DAONotFoundException("Object not found, id=" + Integer.toString(id));
   }

   @Override
   public List<Manufacturer> getManufacturers() throws DAOException
   {
      if (manufacturers == null) fetchData();
      return manufacturers;
   }

   @Override
   public List<Manufacturer> getActiveManufacturers() throws DAOException
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
                  if (!functionalEntityDAO.getFunctionalEntities(m).isEmpty()) activeManufacturers.add(m);
               }
            }
         }
      }

      return activeManufacturers;
   }
}
