package org.freebus.fts.vdx;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.persistence.Column;

/**
 * A section of a vd_ file.
 */
public class VdxSection
{
   private final VdxSectionHeader header;
   private final Vector<String[]> elemValues = new Vector<String[]>(100);
   

   /**
    * Create a new section object.
    * @param header
    */
   VdxSection(VdxSectionHeader header)
   {
      this.header = header;
   }

   /**
    * @return the section header.
    */
   public VdxSectionHeader getHeader()
   {
      return header;
   }

   /**
    * @return the number of elements that the section contains.
    */
   public int getNumElements()
   {
      return elemValues.size();
   }

   /**
    * @return the field's values of the idx-th section element.
    */
   public String[] getElementValues(int idx)
   {
      return elemValues.get(idx);
   }

   /**
    * Add the fields to the list of section element values.
    */
   public void addElementValues(String[] values)
   {
      elemValues.add(values);
   }

   /**
    * @return the fieldIdx-th field of the idx-th section element.
    */
   public String getValue(int idx, int fieldIdx)
   {
      return elemValues.get(idx)[fieldIdx];
   }

   /**
    * @return the fieldIdx-th field of the idx-th section element as an integer.
    *         Zero is returned if the field is empty.
    */
   public int getIntValue(int idx, int fieldIdx)
   {
      final String val = elemValues.get(idx)[fieldIdx];
      if (val.isEmpty()) return 0;
      return Integer.parseInt(val);
   }

   /**
    * @return the fieldIdx-th field of the section element with the name fieldName.
    */
   public String getValue(int idx, String fieldName)
   {
      final int fieldIdx = header.getIndexOf(fieldName);
      return elemValues.get(idx)[fieldIdx];
   }

   /**
    * @return the fieldIdx-th field of the section element with the name fieldName
    *         as an integer. Zero is returned if the field is empty.
    */
   public int getIntValue(int idx, String fieldName)
   {
      final int fieldIdx = header.getIndexOf(fieldName);
      final String val = elemValues.get(idx)[fieldIdx];
      if (val.isEmpty()) return 0;
      return Integer.parseInt(val);
   }

   /**
    * Read all elements of the section and create objects of the given class for each
    * element. The class entryClass must have JPA annotations.
    * @throws IOException 
    */
   public List<?> toObjects(Class<?> entryClass) throws IOException
   {
      final int num = elemValues.size();
      final Map<Integer,Field> fieldMappings = new HashMap<Integer,Field>();
      String fieldName;

      for (final Field field: entryClass.getDeclaredFields())
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

      final List<Object> objs = new LinkedList<Object>();
      final Set<Integer> fieldIdxs = fieldMappings.keySet();

      try
      {
         for (int i = 0; i < num; ++i)
         {
            final Object obj = entryClass.newInstance();
            final String values[] = elemValues.get(i);
   
            for (int fieldIdx: fieldIdxs)
            {
               final Field field = fieldMappings.get(fieldIdx);
               final Type type = field.getGenericType();
               String val = values[fieldIdx];

               if (type == String.class) field.set(obj, val.trim());
               else if (type == int.class)
               {
                  int pos = val.indexOf('.');
                  if (pos >= 0) val = val.substring(0, pos);
                  field.setInt(obj, val.isEmpty() ? 0 : Integer.parseInt(val));                  
               }
               else if (type == double.class) field.setDouble(obj, val.isEmpty() ? 0.0 : Double.parseDouble(val));
               else if (type == boolean.class) field.setBoolean(obj, val.isEmpty() ? false : Integer.parseInt(val) != 0);
               else throw new Exception("Variable type not supported by VdxSection mapper: " + type.toString());
            }

            objs.add(obj);
         }
      }
      catch (Exception e)
      {
         throw new IOException(e);
      }

      return objs;
   }

   /**
    * Clear the list of of section element values.
    */
   public void clear()
   {
      elemValues.clear();
   }
}
