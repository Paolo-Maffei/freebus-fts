package org.freebus.fts.products.services.vdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Program;
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

   private synchronized void fetchData() throws PersistenceException
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
         throw new PersistenceException(e);
      }
   }

   @Override
   public Program getProgram(int id) throws PersistenceException
   {
      if (programs == null) fetchData();

      for (Program program: programs)
         if (program.getId() == id) return program;

      return null;
   }

   @Override
   public List<Program> getPrograms() throws PersistenceException
   {
      if (programs == null) fetchData();
      return programs;
   }

   @Override
   public void persist(Program program)
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public Program merge(Program program)
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public List<Program> findProgram(Manufacturer manufacturer, int deviceType)
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
