package org.freebus.fts.persistence.vdx;

import java.util.Vector;

/**
 * A section of a vd_ file.
 */
public class VdxSection
{
   private final VdxSectionHeader header;
   private final Vector<String[]> elemValues = new Vector<String[]>(1000);
   

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
    * Add the field values to the list of section element values.
    * 
    * @param values - the values to add.
    */
   public void addElementValues(String[] values)
   {
      elemValues.add(values);
   }

   /**
    * Get the element values of a specific element.
    * 
    * @param idx - the index of the element.
    * 
    * @return the field's values of the idx-th section element.
    */
   public String[] getElementValues(int idx)
   {
      return elemValues.get(idx);
   }

   /**
    * Get the value of a single field.
    * 
    * @param idx - the index of the element.
    * @param fieldIdx - the index of the field within the element.
    * 
    * @return the fieldIdx-th field of the idx-th section element.
    */
   public String getValue(int idx, int fieldIdx)
   {
      return elemValues.get(idx)[fieldIdx];
   }

   /**
    * Get the integer value of a single field.
    * 
    * @param idx - the index of the element.
    * @param fieldIdx - the index of the field within the element.

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
    * Get the value of a single field.
    * 
    * @param idx - the index of the element.
    * @param fieldName - the name of the field.
    * 
    * @return the fieldIdx-th field of the section element with the name fieldName.
    */
   public String getValue(int idx, String fieldName)
   {
      final int fieldIdx = header.getIndexOf(fieldName);
      return elemValues.get(idx)[fieldIdx];
   }

   /**
    * Get the integer value of a single field.
    * 
    * @param idx - the index of the element.
    * @param fieldName - the name of the field.

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
    * Clear the list of of section element values.
    */
   public void clear()
   {
      elemValues.clear();
   }
}
