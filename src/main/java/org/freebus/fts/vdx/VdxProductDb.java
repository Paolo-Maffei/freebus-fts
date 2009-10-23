package org.freebus.fts.vdx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.freebus.fts.products.CatalogGroup;
import org.freebus.fts.products.ProductDb;
import org.freebus.fts.products.ProductFilter;

/**
 * A {@link ProductDb} implementation that works on vd_ files.
 */
public final class VdxProductDb implements ProductDb
{
   private final VdxFileReader reader;

   /**
    * Create a new VdxProductDb object. Loads the given vd_ file.
    * @throws IOException 
    */
   public VdxProductDb(String fileName) throws IOException
   {
      reader = new VdxFileReader(fileName);
   }

   /**
    * @return the file-name of the loaded vd_ file.
    */
   public String getFileName()
   {
      return reader.getFileName();
   }

   @Override
   public Set<CatalogGroup> getCatalogGroups(ProductFilter filter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Map<Integer, String> getManufacturers()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
