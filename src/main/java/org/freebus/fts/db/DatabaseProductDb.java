package org.freebus.fts.db;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.freebus.fts.dialogs.ExceptionDialog;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.ProductDb;
import org.freebus.fts.products.ProductFilter;
import org.freebus.fts.products.VirtualDevice;

/**
 * An implementation of the {@link ProductDb} interface that works on the
 * products SQL database.
 */
public class DatabaseProductDb implements ProductDb
{
   protected final EntityManager entityManager;

   /**
    * Create a product-DB object that uses the default entity manager
    * {@link DatabaseResources#getEntityManager()}.
    */
   public DatabaseProductDb()
   {
      this.entityManager = DatabaseResources.getEntityManager();
   }

   /**
    * Create a product-DB object that uses the supplied entity manager.
    */
   public DatabaseProductDb(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   /**
    * @return the entity manager that is used.
    */
   public EntityManager getEntityManager()
   {
      return entityManager;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<FunctionalEntity> getFunctionalEntities(ProductFilter filter) throws IOException
   {
      String stmt = "SELECT fe FROM FunctionalEntity fe";
      if (filter.manufacturer != 0) stmt += " WHERE fe.manufacturerId = " + Integer.toString(filter.manufacturer);

      final Query query = entityManager.createQuery(stmt);
      final List<?> objs = query.getResultList();

      return (List<FunctionalEntity>) objs;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<Integer, String> getManufacturers() throws IOException
   {
      final Query query = entityManager.createQuery("SELECT m FROM Manufacturer m ORDER BY m.name");
      final List<?> objs = query.getResultList();

      final Map<Integer, String> result = new TreeMap<Integer, String>();
      for (Object obj : objs)
      {
         final Manufacturer manu = (Manufacturer) obj;
         result.put(manu.getId(), manu.getName());
      }

      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getProductDescription(int catalogEntryId) throws IOException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VirtualDevice getVirtualDevice(int id) throws IOException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<VirtualDevice> getVirtualDevices(ProductFilter filter) throws IOException
   {
      final StringBuilder matchStr = new StringBuilder();

      String funcEnts = filter.getFunctionalEntitiesString();
      if (funcEnts != null)
      {
         if (matchStr.length() <= 0) matchStr.append(" WHERE ");
         else matchStr.append(" AND ");
         matchStr.append("dev.functionalEntityId IN (");
         matchStr.append(funcEnts);
         matchStr.append(")");
      }

      final Query query = entityManager.createQuery("SELECT dev FROM VirtualDevice dev" + matchStr.toString());
      final List<?> objs = query.getResultList();

      return (List<VirtualDevice>) objs;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CatalogEntry getCatalogEntry(int id) throws IOException
   {
      final Query query = entityManager.createQuery("SELECT ent FROM CatalogEntry ent WHERE ent.id="
            + Integer.toString(id));

      try
      {
         return (CatalogEntry) query.getSingleResult();
      }
      catch (NoResultException e)
      {
         new ExceptionDialog(e);
         return null;
      }
   }
}
