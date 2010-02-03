package org.freebus.fts.products.services.vdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProgramService;

/**
 * Data access object for manufacturers stored in a VD_ file.
 */
public final class VdxProgramService implements ProgramService
{
   private final VdxFileReader reader;
   private List<Program> programs;

   VdxProgramService(VdxFileReader reader)
   {
      this.reader = reader;
   }

   private synchronized void fetchData() throws DAOException
   {
      if (programs != null) return;

      try
      {
         final Object[] arr = reader.getSectionEntries("application_program", Program.class);
         Arrays.sort(arr, new Comparator<Object>()
         {
            @Override
            public int compare(Object a, Object b)
            {
               return ((Program) a).getName().compareTo(((Program) b).getName());
            }
         });
         programs = new ArrayList<Program>(arr.length);
         for (Object obj : arr)
            programs.add((Program) obj);
      }
      catch (IOException e)
      {
         throw new DAOException(e);
      }
   }

   @Override
   public Program getProgram(int id) throws DAOException
   {
      if (programs == null) fetchData();

      for (Program program: programs)
         if (program.getId() == id) return program;

      return null;
   }

   @Override
   public List<Program> getPrograms() throws DAOException
   {
      if (programs == null) fetchData();
      return programs;
   }
}
