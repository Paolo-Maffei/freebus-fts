package org.freebus.fts.products.services.vdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.products.BcuType;
import org.freebus.fts.products.services.BcuTypeService;

/**
 * Data access object for manufacturers stored in a VD_ file.
 */
public final class VdxBcuTypeService implements BcuTypeService
{
   private final VdxFileReader reader;
   private List<BcuType> bcuTypes;

   VdxBcuTypeService(VdxFileReader reader)
   {
      this.reader = reader;
   }

   private synchronized void fetchData() throws PersistenceException
   {
      if (bcuTypes != null) return;

      try
      {
         final Object[] arr = reader.getSectionEntries("bcu_type", BcuType.class);
         Arrays.sort(arr, new Comparator<Object>()
         {
            @Override
            public int compare(Object a, Object b)
            {
               return ((BcuType) a).getName().compareTo(((BcuType) b).getName());
            }
         });
         bcuTypes = new ArrayList<BcuType>(arr.length);
         for (Object obj : arr)
            bcuTypes.add((BcuType) obj);
      }
      catch (IOException e)
      {
         throw new PersistenceException(e);
      }
   }

   @Override
   public BcuType getBcuType(int id) throws PersistenceException
   {
      if (bcuTypes == null) fetchData();

      for (BcuType bcuType: bcuTypes)
         if (bcuType.getId() == id) return bcuType;

      return null;
   }

   @Override
   public List<BcuType> getBcuTypes() throws PersistenceException
   {
      if (bcuTypes == null) fetchData();
      return bcuTypes;
   }

   @Override
   public void persist(BcuType bcuType)
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public BcuType merge(BcuType bcuType)
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
