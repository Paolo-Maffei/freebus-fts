package org.freebus.fts.products.services.vdx;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.ProductDescription;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProductDescriptionService;

/**
 * Data access class that provides access to the product descriptions section of a VD_ file.
 */
public final class VdxProductDescriptionService implements ProductDescriptionService
{
   private final VdxFileReader reader;
   private Map<Integer, List<String>> descriptions;

   VdxProductDescriptionService(VdxFileReader reader)
   {
      this.reader = reader;
   }

   private synchronized void fetchData() throws DAOException
   {
      if (descriptions != null) return;

      descriptions = new HashMap<Integer, List<String>>();
      if (!reader.hasSection("product_description")) return;

      try
      {
         Map<Integer, Map<Integer, String>> tmpDescs = new HashMap<Integer, Map<Integer, String>>();

         final Object[] arr = reader.getSectionEntries("product_description", ProductDescription.class);
         for (Object obj : arr)
         {
            final ProductDescription desc = (ProductDescription) obj;
            final Integer entryId = desc.getCatalogEntryId();
            Map<Integer, String> lines;

            if (tmpDescs.containsKey(entryId))
            {
               lines = tmpDescs.get(entryId);
            }
            else
            {
               lines = new TreeMap<Integer, String>();
               tmpDescs.put(desc.getCatalogEntryId(), lines);
            }
            lines.put(desc.getDisplayOrder(), desc.getDescription());
         }

         for (Integer key: tmpDescs.keySet())
         {
            descriptions.put(key, new LinkedList<String>(tmpDescs.get(key).values()));
         }
      }
      catch (IOException e)
      {
         throw new DAOException(e);
      }
   }

   @Override
   public synchronized List<String> getProductDescription(CatalogEntry entry) throws DAOException
   {
      if (descriptions == null) fetchData();
      return descriptions.get(entry.getId());
   }
}
