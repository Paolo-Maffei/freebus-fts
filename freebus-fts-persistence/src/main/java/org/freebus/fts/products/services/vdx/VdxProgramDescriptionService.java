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
import org.freebus.fts.products.Program;
import org.freebus.fts.products.ProgramDescription;
import org.freebus.fts.products.services.ProgramDescriptionService;

/**
 * Data access class that provides access to the program descriptions section of a VD_ file.
 */
public final class VdxProgramDescriptionService implements ProgramDescriptionService
{
   private final VdxFileReader reader;
   private Map<Integer, Object> descriptions;

   VdxProgramDescriptionService(VdxFileReader reader)
   {
      this.reader = reader;
   }

   private synchronized void fetchData() throws DAOException
   {
      if (descriptions != null) return;

      descriptions = new HashMap<Integer, Object>();
      if (!reader.hasSection("program_description")) return;

      try
      {
         Map<Integer, Map<Integer, String>> tmpDescs = new HashMap<Integer, Map<Integer, String>>();

         final VdxSection table = reader.getSection("program_description");
         final VdxSectionHeader header = table.getHeader();
         final int programIdIdx = header.getIndexOf("program_id");
         final int textIdx = header.getIndexOf("text");
         final int displayOrderIdx = header.getIndexOf("display_order");
         final int languageIdIdx = header.getIndexOf("language_id");
         final int languageId = reader.getLanguageId();

         for (int idx = table.getNumElements() - 1; idx >= 0; --idx)
         {
            if (table.getIntValue(idx, languageIdIdx) != languageId)
               continue;

            final Integer programId = table.getIntValue(idx, programIdIdx);
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
   public synchronized ProgramDescription getProgramDescription(Program prog) throws DAOException
   {
      if (descriptions == null) fetchData();

      Object obj = descriptions.get(prog.getId());
      if (obj instanceof String)
      {
         obj = new ProgramDescription(prog, (String) obj);
         descriptions.put(prog.getId(), obj);
      }

      return (ProgramDescription) obj;
   }

   @Override
   public void persist(ProgramDescription desc)
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public ProgramDescription merge(ProgramDescription desc)
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
