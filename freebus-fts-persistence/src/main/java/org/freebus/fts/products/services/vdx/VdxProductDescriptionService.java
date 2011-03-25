package org.freebus.fts.products.services.vdx;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.exception.DAOException;
import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.persistence.vdx.VdxSection;
import org.freebus.fts.persistence.vdx.VdxSectionHeader;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.ProductDescription;
import org.freebus.fts.products.services.ProductDescriptionService;

/**
 * Data access class that provides access to the product descriptions section of a VD_ file.
 */
public final class VdxProductDescriptionService implements ProductDescriptionService
{
   private final VdxFileReader reader;
   private Map<Integer, Object> descriptions;

   VdxProductDescriptionService(VdxFileReader reader)
   {
      this.reader = reader;
   }

   private synchronized void fetchData() throws DAOException
   {
      if (descriptions != null) return;

      descriptions = new HashMap<Integer, Object>();
      if (!reader.hasSection("product_description")) return;

      try
      {
         Map<Integer, Map<Integer, String>> tmpDescs = new HashMap<Integer, Map<Integer, String>>();

         final VdxSection table = reader.getSection("product_description");
         final VdxSectionHeader header = table.getHeader();
         final int catEntryIdIdx = header.getIndexOf("catalog_entry_id");
         final int textIdx = header.getIndexOf("product_description_text");
         final int displayOrderIdx = header.getIndexOf("display_order");
         final int languageIdIdx = header.getIndexOf("language_id");
         final int languageId = reader.getLanguageId();

         for (int idx = table.getNumElements() - 1; idx >= 0; --idx)
         {
            if (table.getIntValue(idx, languageIdIdx) != languageId)
               continue;

            final Integer programId = table.getIntValue(idx, catEntryIdIdx);
            Map<Integer, String> lines;

            if (tmpDescs.containsKey(programId))
            {
               lines = tmpDescs.get(programId);
            }
            else
            {
               lines = new TreeMap<Integer, String>();
               tmpDescs.put(programId, lines);
            }

            lines.put(table.getIntValue(idx, displayOrderIdx), table.getValue(idx, textIdx));
         }

         for (Integer key: tmpDescs.keySet())
         {
            final Collection<String> lines = tmpDescs.get(key).values();
            final StringBuilder sb = new StringBuilder();
            if (!lines.isEmpty())
            {
               final Iterator<String> it = lines.iterator();
               sb.append(it.next());

               while (it.hasNext())
                  sb.append("\n").append(it.next());
            }

            descriptions.put(key, sb.toString());
         }
      }
      catch (IOException e)
      {
         throw new DAOException(e);
      }
   }

   @Override
   public synchronized ProductDescription getProductDescription(CatalogEntry entry) throws DAOException
   {
      if (descriptions == null) fetchData();

      Object obj = descriptions.get(entry.getId());
      if (obj instanceof String)
      {
         obj = new ProductDescription(entry, (String) obj);
         descriptions.put(entry.getId(), obj);
      }

      return (ProductDescription) obj;
   }

   @Override
   public void persist(ProductDescription desc)
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public ProductDescription merge(ProductDescription desc)
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
