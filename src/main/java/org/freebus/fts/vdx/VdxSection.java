package org.freebus.fts.vdx;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * A section of a .vdx file
 */
public class VdxSection
{
   private final String name;
   private final int id;
   private final VdxSectionType type;
   private final Vector<String> fields = new Vector<String>();
   private final Map<Integer,String[]> elements = new HashMap<Integer,String[]>();
   private int keyFieldIdx = -1;
   private int nameFieldIdx = -1;

   /**
    * Create a new section.
    */
   public VdxSection(String name, int id, VdxSectionType type)
   {
      this.name = name;
      this.id = id;
      this.type = type;
   }

   /**
    * @return the name of the section.
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the type of the section.
    */
   public VdxSectionType getType()
   {
      return type;
   }

   /**
    * @return the id of the section.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the names of all fields.
    */
   public Vector<String> getFields()
   {
      return fields;
   }
   
   /**
    * Add a field name.
    */
   public void addField(String fieldName)
   {
      fields.add(fieldName);
   }
   
   /**
    * Searches the index for the field fieldName.
    * 
    * If not found, an exception is thrown if throwIfNotFound is true; -1 is
    * returned if throwIfNotFound is false.
    * 
    * @return the index of the field.
    */
   public int getFieldIndex(String fieldName, boolean throwIfNotFound)
   {
      for (int i=fields.size()-1; i>=0; --i)
         if (fields.get(i).equals(fieldName)) return i;

      if (throwIfNotFound)
         throw new RuntimeException("Section "+name+" contains no field "+fieldName);
      return -1;
   }

   /**
    * @return the index of the key field.
    */
   public int getKeyFieldIdx()
   {
      return keyFieldIdx;
   }

   /**
    * Set the index of the key field.
    */
   public void setKeyFieldIdx(int keyFieldIdx)
   {
      this.keyFieldIdx = keyFieldIdx;
   }

   /**
    * @return the index of the name field.
    */
   public int getNameFieldIdx()
   {
      return nameFieldIdx;
   }

   /**
    * Set the index of the name field.
    */
   public void setNameFieldIdx(int nameFieldIdx)
   {
      this.nameFieldIdx = nameFieldIdx;
   }

   /**
    * @return all id's of the elements that are contained.
    */
   public Set<Integer> getElementIds()
   {
      return elements.keySet();
   }

   /**
    * @return the element with the given id. Returned is a vector with
    *         the field's contents.
    */
   public String[] getElement(int id)
   {
      return elements.get(id);
   }

   /**
    * Get the value of the element id with the given field-name.
    * @param id is the id of the element
    * @param name is the name of the requested field
    */
   public String getValue(int id, String fieldName)
   {
      final String[] elem = elements.get(id);
      return elem[getFieldIndex(fieldName, true)];
   }

   /**
    * Get the integer value of the element id with the given field-name.
    * If the value is empty, zero is returned.
    * 
    * @param id is the id of the element
    * @param name is the name of the requested field
    */
   public int getIntegerValue(int id, String fieldName)
   {
      return getIntegerValue(id, getFieldIndex(fieldName, true));
   }

   /**
    * Get the integer value of the element id with the given field-index fieldIdx.
    * If the value is empty, zero is returned.
    * 
    * @param id is the id of the element
    * @param fieldIdx is the index of the requested field
    */
   public int getIntegerValue(int id, int fieldIdx)
   {
      final String val = elements.get(id)[fieldIdx];
      if (val.isEmpty()) return 0;
      return Integer.parseInt(val);
   }

   /**
    * Get the double value of the element id with the given field-name.
    * If the value is empty, zero is returned.
    * 
    * @param id is the id of the element
    * @param name is the name of the requested field
    */
   public double getDoubleValue(int id, String fieldName)
   {
      return getDoubleValue(id, getFieldIndex(fieldName, true));
   }

   /**
    * Get the integer value of the element id with the given field-index fieldIdx.
    * If the value is empty, zero is returned.
    * 
    * @param id is the id of the element
    * @param fieldIdx is the index of the requested field
    */
   public double getDoubleValue(int id, int fieldIdx)
   {
      final String val = elements.get(id)[fieldIdx];
      if (val.isEmpty()) return 0;
      return Double.parseDouble(val);
   }

   /**
    * Test if the section contains an element with the given id.
    */
   public boolean hasElement(int id)
   {
      return elements.containsKey(id);
   }

   /**
    * Add an element to the section.
    */
   public void addElement(int id, String[] values)
   {
      elements.put(id, values);
   }
}
