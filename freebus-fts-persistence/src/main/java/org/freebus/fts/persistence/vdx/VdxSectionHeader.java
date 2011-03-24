package org.freebus.fts.persistence.vdx;

/**
 * Header information about a section in a vd_ file.
 */
public final class VdxSectionHeader
{
   /**
    * The name of the section.
    */
   public final String name;

   /**
    * The id of the section.
    */
   public final int id;

   /**
    * The offset of the section in the vd_ file.
    * This is the position of the first header line.
    */
   public final long offset;

   /**
    * The names of the fields of the section entries.
    */
   public final String[] fields;

   /**
    * The types of the fields of the section entries.
    */
   public final VdxFieldType[] types;

   /**
    * The sizes of the fields of the section entries.
    */
   public final int[] sizes;

   /**
    * Create a header object.
    */
   VdxSectionHeader(String name, int id, long offset, final String[] fields, final VdxFieldType[] types, final int[] sizes)
   {
      this.name = name;
      this.id = id;
      this.offset = offset;
      this.fields = fields;
      this.types = types;
      this.sizes = sizes;
   }

   /**
    * Get the index of a field.
    * 
    * @param name - the name of the searched field.
    * 
    * @return the index of the field with the given name, or -1 if not found.
    */
   public int getIndexOf(String name)
   {
      int i;
      for (i=fields.length-1; i>=0; --i)
         if (fields[i].equalsIgnoreCase(name)) break;
      return i;
   }

   /**
    * @return a human readable representation of the object
    */
   @Override
   public String toString()
   {
      return Integer.toString(id) + " " + name + " offset " + Long.toString(offset);
   }
}
