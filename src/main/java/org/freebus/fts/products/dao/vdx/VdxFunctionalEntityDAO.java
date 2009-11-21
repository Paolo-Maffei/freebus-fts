package org.freebus.fts.products.dao.vdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.dao.DAOException;
import org.freebus.fts.products.dao.FunctionalEntityDAO;
import org.freebus.fts.vdx.VdxFileReader;

/**
 * Data access object for functional entities stored in a VDX file.
 */
public final class VdxFunctionalEntityDAO implements FunctionalEntityDAO
{
   private final VdxFileReader reader;
   private List<FunctionalEntity> entities;

   VdxFunctionalEntityDAO(VdxFileReader reader)
   {
      this.reader = reader;
   }

   private synchronized void fetchData() throws DAOException
   {
      if (entities != null) return;

      try
      {
         final Object[] arr = reader.getSectionEntries("functional_entity", FunctionalEntity.class);
         Arrays.sort(arr, new Comparator<Object>()
         {
            @Override
            public int compare(Object a, Object b)
            {
               return ((FunctionalEntity) a).getName().compareTo(((FunctionalEntity) b).getName());
            }
         });
         entities = new ArrayList<FunctionalEntity>(arr.length);
         for (Object obj : arr)
            entities.add((FunctionalEntity) obj);
      }
      catch (IOException e)
      {
         throw new DAOException(e);
      }
   }

   @Override
   public List<FunctionalEntity> getFunctionalEntities() throws DAOException
   {
      if (entities == null) fetchData();
      return entities;
   }

   @Override
   public List<FunctionalEntity> getFunctionalEntities(Manufacturer m) throws DAOException
   {
      if (entities == null) fetchData();

      final List<FunctionalEntity> result = new LinkedList<FunctionalEntity>();
      final int manufacturerId = m.getId();

      for (FunctionalEntity entity : entities)
         if (entity.getManufacturerId() == manufacturerId) result.add(entity);

      return result;
   }

   @Override
   public FunctionalEntity getFunctionalEntity(int id) throws DAOException
   {
      if (entities == null) fetchData();

      for (FunctionalEntity entity : entities)
         if (entity.getId() == id) return entity;

      throw new DAOException("Object not found: FunctionalEntity with id " + Integer.toString(id));
   }
}
