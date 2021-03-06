package org.freebus.fts.persistence.vdx;

import java.io.File;
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
 * A class that reads vd_ files. The section names are all converted to lower
 * case.
 */
public final class VdxFileReader
{
   private static final String SECTION_SEPARATOR = "---------------------------";
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
    * Create a new VDX-file reader object. The file <code>fileName</code> is
    * read upon creation.
    *
    * @param file is the file that shall be read.
    *
    * @throws IOException if the file cannot be read.
    */
   public VdxFileReader(File file) throws IOException
   {
      this.fileName = file.getPath();

      try
      {
         // final ZipFileType zipFileType = ZipFileType.inspectFile(fileName);

         in = new RandomAccessFile(file, "r");
         reader = new FileBlockReader(in, 1049600);

         scanHeader();
         scanFile();
      }
      catch (IOException e)
      {
         if (in != null)
            in.close();
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
    * Create a new VDX-file reader object. The file <code>fileName</code> is
    * read upon creation.
    *
    * @param fileName - the name of the file that shall be read.
    *
    * @throws IOException if the file cannot be read.
    */
   public VdxFileReader(String fileName) throws IOException
   {
      this(new File(fileName));
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
      if (in != null)
         try
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
    * Get a header section.
    * 
    * @param name - the name of the header section.
    * 
    * @return the header of the section with the given name.
    */
   public VdxSectionHeader getSectionHeader(String name)
   {
      return sectionHeaders.get(name.toLowerCase());
   }

   /**
    * Test if a header section exists.
    * 
    * @param name - the name of the header section.
    *
    * @return true if the section with the given name exists.
    */
   public boolean hasSection(String name)
   {
      return sectionHeaders.containsKey(name.toLowerCase());
   }

   /**
    * Get a specific section of the file. The section is read, if required.
    *
    * @param name - the name of the section.
    *
    * @return the section with the given name or null if no such section exists
    *         or if the section contains no records.
    *
    * @throws IOException in case of I/O problems.
    */
   public VdxSection getSection(String name) throws IOException
   {
      name = name.toLowerCase();
      if (sections.containsKey(name))
         return sections.get(name);

      final VdxSectionHeader header = sectionHeaders.get(name);
      if (header == null)
         return null;

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
         if (line == null || !line.startsWith("C"))
            break;
      }

      // Read the entries of the section
      while (line != null && !line.startsWith(SECTION_SEPARATOR) && !line.equals("XXX"))
      {
         final String[] values = new String[numFields];
         for (int i = 0; i <= numFields; ++i)
         {
            line = reader.readLine();
            if (line == null)
            {
               if (i > 0 && i < numFields)
                  throw new IOException("file is truncated");
               break;
            }

            if (line.startsWith("\\\\") && i > 0)
            {
               values[--i] += line.substring(2).replace("//", "/");
               continue;
            }
            if (i == numFields)
               break;

            values[i] = line.replace("//", "/");
         }

         section.addElementValues(values);
      }

      sections.put(name, section);
      return section;
   }

   /**
    * Loads the VDX section sectionName. For each record, creates an object of
    * the type entryClass.
    *
    * @param sectionName - the name of the VDX section to process.
    * @param entryClass - the class for the entries.
    * 
    * @return the list of created entries.
    *
    * @throws IOException in case of I/O problems.
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
            if (a == null)
               continue;
            fieldName = ((Column) a).name();
         }

         int fieldIdx = header.getIndexOf(fieldName);
         field.setAccessible(true);
         if (fieldIdx >= 0)
            fieldMappings.put(fieldIdx, field);
      }

      final Object[] objs = new Object[num];
      final Set<Integer> fieldIdxs = fieldMappings.keySet();
      int skipped = 0;

      try
      {
         for (int i = 0; i < num; ++i)
         {
            final Object obj = entryClass.newInstance();
            final String[] values = section.getElementValues(i);

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
               String value = values[fieldIdx];

               if (type == String.class)
                  field.set(obj, value.trim());
               else if (type == int.class)
               {
                  int pos = value.indexOf('.');
                  if (pos >= 0)
                     value = value.substring(0, pos);
                  field.setInt(obj, value.isEmpty() ? 0 : Integer.parseInt(value));
               }
               else if (type == double.class || type == Double.class)
                  field.setDouble(obj, value.isEmpty() ? 0.0 : Double.parseDouble(value));
               else if (type == float.class || type == Float.class)
                  field.setFloat(obj, value.isEmpty() ? 0.0f : Float.parseFloat(value));
               else if (type == boolean.class || type == Boolean.class)
               {
                  if (value.isEmpty())
                     value = "0";
                  field.setBoolean(obj, Integer.parseInt(value) != 0);
               }
               else
               {
                  final Class<?> fieldClass = (Class<?>) type;
                  final Class<?> componentType = fieldClass.getComponentType();

                  if (fieldClass.isArray() && componentType == byte.class)
                  {
                     // Assume hex data string
                     final int len = value.length() >> 1;
                     final byte[] data = new byte[len];
                     for (int k = 0, l = 0; k < len; ++k, l += 2)
                        data[k] = (byte) (Integer.parseInt(value.substring(l, l + 2), 16));
                     field.set(obj, data);
                  }
                  else if (fieldClass.isEnum())
                  {
                     @SuppressWarnings({ "unchecked", "rawtypes" })
                     Class<? extends Enum> enumClass = (Class<? extends Enum>) type;

                     if (value.isEmpty())
                     {
                        field.set(obj, null);
                     }
                     else if (value.matches("\\d*"))
                     {
                        final int ordinal = Integer.parseInt(value);
                        Enum<?> enumVal = null;

                        for (Enum<?> e : enumClass.getEnumConstants())
                        {
                           if (e.ordinal() == ordinal)
                           {
                              enumVal = e;
                              break;
                           }
                        }

                        if (enumVal != null)
                           field.set(obj, enumVal);
                        else throw new IllegalArgumentException("Could not initialize enum of type " + type
                              + " with value: " + value);
                     }
                     else
                     {
                        @SuppressWarnings("unchecked")
                        Enum<?> enumVal = Enum.valueOf(enumClass, value.toUpperCase().replace(' ', '_'));
                        field.set(obj, enumVal);
                     }
                  }
                  else
                  {
                     throw new IOException("Variable type not supported by vdx-to-db mapper: " + type.toString());
                  }
               }
            }

            objs[i] = obj;
         }
      }
      catch (IllegalAccessException e)
      {
         throw new IOException(e);
      }
      catch (InstantiationException e)
      {
         throw new IOException(e);
      }

      if (skipped > 0)
      {
         final Object[] tmpObjs = new Object[num - skipped];
         int j = 0;
         for (int i = 0; i < num; ++i)
         {
            if (objs[i] == null)
               continue;
            tmpObjs[j++] = objs[i];
         }
         return tmpObjs;
      }

      return objs;
   }

   /**
    * Remove a specific section contents from the reader. Only the contents of
    * the section is removed, the section header stays loaded.
    *
    * You can call this method free some memory, which is recommended when
    * working with large files.
    * 
    * @param name - the name of the section.
    */
   public void removeSectionContents(String name)
   {
      sections.remove(name);
   }

   /**
    * Read the vdx file header. The read-pointer stands on the T line of the
    * first section afterwards.
    *
    * @throws IOException in case of I/O problems.
    */
   private void scanHeader() throws IOException
   {
      String line;
      char lineType;

      format = null;
      setVersion(null);

      in.seek(0);

      line = reader.readLine();
      if (line != null)
         line = line.trim();
      if (!"EX-IM".equals(line))
         throw new IOException("no vdx file");

      while (true)
      {
         line = reader.readLine();
         if (line == null)
            break;
         lineType = line.charAt(0);
         if (lineType == '-' && line.startsWith(SECTION_SEPARATOR))
            break;
         if (lineType == 'K')
         {
            if (format == null || format.isEmpty())
               format = line.substring(2).trim();
         }
         else if (lineType == 'V')
            setVersion(line.substring(2).trim());
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
      final Vector<String> fieldNames = new Vector<String>(32);
      final Vector<VdxFieldType> fieldTypes = new Vector<VdxFieldType>(32);
      final Vector<Integer> fieldSizes = new Vector<Integer>(32);
      String line;
      long offset;

      sectionHeaders.clear();

      while (!reader.atEnd())
      {
         // Read the section title
         offset = reader.getFilePointer();
         line = reader.readLine();
         if (line == null)
            break;

         String[] words = line.split(" ");
         if (words[0].equals("XXX"))
            break;
         else if (!words[0].equals("T"))
            throw new IOException("No section start at byte-pos " + Long.toString(reader.getFilePointer()));

         final int sectionId = Integer.parseInt(words[1]);
         String sectionName = words[2];

         if (sectionName != null)
            sectionName = sectionName.trim().toLowerCase();
         if (sectionHeaders.containsKey(sectionName))
         {
            throw new IOException("Duplicate section \"" + sectionName + "\" at byte-pos "
                  + Long.toString(reader.getFilePointer()));
         }

         // Read the fields of the section
         fieldNames.clear();
         fieldTypes.clear();
         fieldSizes.clear();
         while (!reader.atEnd() && reader.read() == 'C')
         {
            // A field definition line looks like this:
            // C1 T3 1 4 N MANUFACTURER_ID

            // Skip the field-id "1" (we already read the 'C')
            reader.readWord();

            // Skip the section name "T3"
            reader.readWord(); 

            // the field data type
            try
            {
               fieldTypes.add(VdxFieldType.valueOfTypeId(Integer.parseInt(reader.readWord())));
            }
            catch (Exception e)
            {
               throw new IOException("failed to get field type, section \"" + sectionName + "\" at byte-pos "
                     + Long.toString(reader.getFilePointer()), e);
            }

            // The size of the field in bytes
            fieldSizes.add(Integer.valueOf(reader.readWord()));

            // Skip the null-allowed Y|N switch
            reader.readWord();

            // the field name
            line = reader.readLine();
            if (line == null)
               break;
            fieldNames.add(line.trim().toLowerCase());
         }

         final String[] fieldNamesArr = new String[fieldNames.size()];
         fieldNames.toArray(fieldNamesArr);

         final VdxFieldType[] fieldTypesArr = new VdxFieldType[fieldTypes.size()];
         fieldTypes.toArray(fieldTypesArr);

         final int[] fieldSizesArr = new int[fieldSizes.size()];
         for (int i = fieldSizes.size() - 1; i >= 0; --i)
            fieldSizesArr[i] = fieldSizes.get(i);

         // Create the section header object
         sectionHeader = new VdxSectionHeader(sectionName, sectionId, offset, fieldNamesArr, fieldTypesArr, fieldSizesArr);
         sectionHeaders.put(sectionName, sectionHeader);

         // Skip the section entries
         final int numFields = fieldNamesArr.length;
         line = reader.readLine();
         while (line != null && !line.startsWith(SECTION_SEPARATOR) && !line.equals("XXX"))
         {
            for (int i = 0; i <= numFields; ++i)
            {
               line = reader.readLine();
               if (line == null)
               {
                  if (i > 0 && i < numFields)
                     throw new IOException("file is truncated");
                  break;
               }

               if (line.startsWith("\\\\") && i > 0)
               {
                  --i;
                  continue;
               }
               if (i == numFields)
                  break;
            }
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
    * Set the language that shall be used for multi-lingual texts. Use
    * {@link #getLanguages()} to obtain the list of available languages in a VD_
    * file.
    * 
    * @param language - the language to set.
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
    * Set the language-id of the language that shall be used for multi-lingual
    * texts.
    * 
    * @param languageId - the language ID to set.
    *
    * @see #setLanguage(String)
    */
   public synchronized void setLanguageId(int languageId)
   {
      this.languageId = languageId;
   }

   /**
    * @return the language-id that is currently set.
    */
   public synchronized int getLanguageId()
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
            if (section == null)
               return null;

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
