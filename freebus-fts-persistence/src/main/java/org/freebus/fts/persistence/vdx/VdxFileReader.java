package org.freebus.fts.persistence.vdx;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.persistence.Column;

import org.freebus.fts.persistence.FileBlockReader;

/**
 * A class that reads vd_ files. The section names are all converted to lower case.
 */
public final class VdxFileReader
{
   private static final String sectionSeparator = "---------------------------";
   private final String fileName;
   private String format = null;
   private String version = null;
   private RandomAccessFile in = null;
   private int languageId = 0;
   private final FileBlockReader reader;
   private final Map<String, VdxSectionHeader> sectionHeaders = new LinkedHashMap<String, VdxSectionHeader>();
   private final Map<String, VdxSection> sections = new HashMap<String, VdxSection>(100);
   private Map<String, Integer> languages;

   /**
    * Create a new VDX-file reader object. The file <code>fileName</code> is read
    * upon creation.
    * 
    * @throws IOException if the file cannot be read.
    */
   public VdxFileReader(String fileName) throws IOException
   {
      this.fileName = fileName;

      try
      {
         // final ZipFileType zipFileType = ZipFileType.inspectFile(fileName);

         in = new RandomAccessFile(fileName, "r");
         reader = new FileBlockReader(in, 1049600);

         scanHeader();
         scanFile();
      }
      catch (IOException e)
      {
         if (in != null) in.close();
         throw new IOException("Failed to read file " + fileName + ": " + e.getMessage(), e);
      }

      try
      {
         setLanguage("German");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Destructor.
    * 
    * @throws RuntimeException if the file was not closed.
    */
   @Override
   protected void finalize() throws RuntimeException
   {
      if (in != null)
      {
         close();
         throw new RuntimeException("Internal error: file " + fileName + " was not properly closed");
      }
   }

   /**
    * Close the file.
    */
   public void close()
   {
      if (in != null) try
      {
         in.close();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      finally
      {
         in = null;
      }
   }

   /**
    * @return the name of the file that the file-reader processes.
    */
   public String getFileName()
   {
      return fileName;
   }
   
   /**
    * @return the names of all found sections.
    */
   public Set<String> getSectionNames()
   {
      return sectionHeaders.keySet();
   }

   /**
    * @return the header of the section with the given name.
    */
   public VdxSectionHeader getSectionHeader(String name)
   {
      return sectionHeaders.get(name.toLowerCase());
   }

   /**
    * @return true if the section with the given name exists.
    */
   public boolean hasSection(String name)
   {
      return sectionHeaders.containsKey(name.toLowerCase());
   }

   /**
    * Get a specific section of the file. The section is read, if required.
    * 
    * @return the section with the given name or null if no such section exists or
    *         if the section contains no records.
    * @throws IOException
    */
   public VdxSection getSection(String name) throws IOException
   {
      name = name.toLowerCase();
      if (sections.containsKey(name)) return sections.get(name);

      final VdxSectionHeader header = sectionHeaders.get(name);
      if (header == null) return null;

      final VdxSection section = new VdxSection(header);
      final int numFields = header.fields.length;
      String line;

      reader.seek(header.offset);
      // We are at the "T" title line now
      // Skip to the first entry. An entry starts with such a line:
      // R 1 T 3 manufacturer
      line = reader.readLine();
      while (line != null)
      {
         line = reader.readLine();
         if (line == null || !line.startsWith("C")) break;
      }

      // Read the entries of the section
      while (line != null && !line.startsWith(sectionSeparator) && !line.equals("XXX"))
      {
         final String[] values = new String[numFields];
         for (int i = 0; i <= numFields; ++i)
         {
            line = reader.readLine();
            if (line == null)
            {
               if (i > 0 && i < numFields) throw new IOException("file is truncated");
               break;
            }

            if (line.startsWith("\\\\") && i > 0)
            {
               values[--i] += line.substring(2).replace("//", "/");
               continue;
            }
            if (i == numFields) break;

            values[i] = line.replace("//", "/");
         }

         section.addElementValues(values);
      }

      sections.put(name, section);
      return section;
   }

   /**
    * Loads the VDX section sectionName. For each record, creates an object of the type
    * entryClass.
    * 
    * @param sectionName - the name of the VDX section to process.
    * @param entryClass - the class for the entries.
    * @return the list of created entries.
    * @throws IOException
    */
   public Object[] getSectionEntries(String sectionName, Class<?> entryClass) throws IOException
   {
      final VdxSection section = getSection(sectionName);
      final VdxSectionHeader header = section.getHeader();
      final int num = section.getNumElements();
      final Map<Integer, Field> fieldMappings = new HashMap<Integer, Field>();
      final int languageIdx = header.getIndexOf("language_id");
      String fieldName;

      for (final Field field : entryClass.getDeclaredFields())
      {
         Annotation a = field.getAnnotation(VdxField.class);
         if (a != null)
         {
            fieldName = ((VdxField) a).name();
         }
         else
         {
            a = field.getAnnotation(Column.class);
            if (a == null) continue;
            fieldName = ((Column) a).name();
         }

         int fieldIdx = header.getIndexOf(fieldName);
         field.setAccessible(true);
         if (fieldIdx >= 0) fieldMappings.put(fieldIdx, field);
      }

      final Object[] objs = new Object[num];
      final Set<Integer> fieldIdxs = fieldMappings.keySet();
      int skipped = 0;

      try
      {
         for (int i = 0; i < num; ++i)
         {
            final Object obj = entryClass.newInstance();
            final String values[] = section.getElementValues(i);

            if (languageIdx >= 0 && languageId > 0)
            {
               if (Integer.parseInt(values[languageIdx]) != languageId)
               {
                  ++skipped;
                  continue;
               }
            }

            for (int fieldIdx : fieldIdxs)
            {
               final Field field = fieldMappings.get(fieldIdx);
               final Type type = field.getGenericType();
               String val = values[fieldIdx];

               if (type == String.class)
                  field.set(obj, val.trim());
               else if (type == int.class)
               {
                  int pos = val.indexOf('.');
                  if (pos >= 0) val = val.substring(0, pos);
                  field.setInt(obj, val.isEmpty() ? 0 : Integer.parseInt(val));
               }
               else if (type == double.class || type == Double.class)
                  field.setDouble(obj, val.isEmpty() ? 0.0 : Double.parseDouble(val));
               else if (type == float.class || type == Float.class)
                  field.setFloat(obj, val.isEmpty() ? 0.0f : Float.parseFloat(val));
               else if (type == boolean.class || type == Boolean.class)
               {
                  if (val.isEmpty()) val = "0";
                  field.setBoolean(obj, Integer.parseInt(val) != 0);
               }
               else throw new IOException("Variable type not supported by vdx-to-db mapper: " + type.toString());
            }

            objs[i] = obj;
         }
      }
      catch (Exception e)
      {
         throw new IOException(e);
      }

      if (skipped > 0)
      {
         final Object[] tmpObjs = new Object[num - skipped];
         int j = 0;
         for (int i = 0; i < num; ++i)
         {
            if (objs[i] == null) continue;
            tmpObjs[j++] = objs[i];
         }
         return tmpObjs;
      }

      return objs;
   }

   /**
    * Remove a specific section contents from the reader. Only the contents of the
    * section is removed, the section header stays loaded.
    * 
    * You can call this method free some memory, which is recommended when working with
    * large files.
    */
   public void removeSectionContents(String name)
   {
      sections.remove(name);
   }

   /**
    * Read the vdx file header. The read-pointer stands on the T line of the first section
    * afterwards.
    * 
    * @throws IOException
    */
   private void scanHeader() throws IOException
   {
      String line;
      char lineType;

      format = null;
      setVersion(null);

      in.seek(0);

      line = reader.readLine();
      if (line != null) line = line.trim();
      if (!"EX-IM".equals(line)) throw new IOException("no vdx file");

      while (true)
      {
         line = reader.readLine();
         if (line == null) break;
         lineType = line.charAt(0);
         if (lineType == '-' && line.startsWith(sectionSeparator)) break;
         if (lineType == 'K')
         {
            if (format == null || format.isEmpty()) format = line.substring(2).trim();
         }
         else if (lineType == 'V') setVersion(line.substring(2).trim());
      }
   }

   /**
    * Find the sections and their offsets within the file.
    * 
    * @throws IOException
    */
   private void scanFile() throws IOException
   {
      VdxSectionHeader sectionHeader;
      final Vector<String> fieldLines = new Vector<String>(32);
      String line;
      long offset;

      sectionHeaders.clear();

      while (!reader.atEnd())
      {
         // Read the section title
         offset = reader.getFilePointer();
         if (reader.read() != 'T' || reader.read() != ' ')
            throw new IOException("No section start at byte-pos " + Long.toString(reader.getFilePointer()));

         final int sectionId = Integer.parseInt(reader.readWord());
         String sectionName = reader.readLine();
         if (sectionName != null) sectionName= sectionName.trim().toLowerCase();
         if (sectionHeaders.containsKey(sectionName))
         {
            throw new IOException("Duplicate section \"" + sectionName + "\" at byte-pos "
                  + Long.toString(reader.getFilePointer()));
         }

         // Read the fields of the section
         fieldLines.clear();
         while (!reader.atEnd() && reader.read() == 'C')
         {
            // A field definition line looks like this:
            // C1 T3 1 4 N MANUFACTURER_ID
            reader.readWord(); // Skip the field-id "1" (we already read the 'C')
            reader.readWord(); // Skip the section name "T3"
            reader.readWord(); // Skip the field data type
            reader.readWord(); // Skip the size of the field in bytes (int:4, short:2, string:length, ...)
            reader.readWord(); // Skip the null-allowed Y|N switch

            line = reader.readLine();
            if (line == null) break;
            fieldLines.add(line.trim().toLowerCase());
         }

         final String[] fieldNames = new String[fieldLines.size()];
         fieldLines.toArray(fieldNames);

         // Create the section header object
         sectionHeader = new VdxSectionHeader(sectionName, sectionId, offset, fieldNames);
         sectionHeaders.put(sectionName, sectionHeader);

         // Skip the section entries
         while (true)
         {
            if (!reader.readUntil('-')) break;
            line = reader.readLine();
            if (line == null || line.startsWith(sectionSeparator)) break;
         }
      }
   }

   /**
    * @param version the version to set
    */
   public void setVersion(String version)
   {
      this.version = version;
   }

   /**
    * @return the version
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * Set the language that shall be used for multi-lingual texts.
    * Use {@link getLanguages} to obtain the list of available languages in a VD_ file.
    */
   public synchronized void setLanguage(String language)
   {
      if (languages == null)
         getLanguages();

      if (languages.containsKey(language))
         languageId = languages.get(language);
      else languageId = 0;
   }
   
   /**
    * Set the language-id of the language that shall be used for multi-lingual texts.
    * @see setLanguage
    */
   public void setLanguageId(int languageId)
   {
      this.languageId = languageId;
   }

   /**
    * @return the language-id that is currently set.
    */
   public int getLanguageId()
   {
      return languageId;
   }

   /**
    * @return the list of available languages.
    */
   public synchronized Set<String> getLanguages()
   {
      if (languages == null)
      {
         languages = new TreeMap<String, Integer>();
         VdxSection section;
         try
         {
            section = getSection("ete_language");
            if (section == null) return null;

            final int langIdIdx = section.getHeader().getIndexOf("language_id");
            final int langNameIdx = section.getHeader().getIndexOf("language_name");

            for (int idx = section.getNumElements() - 1; idx >= 0; --idx)
            {
               languages.put(section.getValue(idx, langNameIdx), section.getIntValue(idx, langIdIdx));
            }
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
      return languages.keySet();
   }
}
