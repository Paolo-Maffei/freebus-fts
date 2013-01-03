package org.freebus.fts.client.editors.inspectvdxfile;

import org.freebus.fts.persistence.vdx.VdxSection;

/**
 * Interface for panels that display the contents of a VDX table.
 * This is an internal class of {@link InspectVdxFile}.
 */
public interface TableContents
{
   /**
    * Set the table that is displayed.
    * 
    * @param table - the table to be displayed.
    * @param maxRecords - display this number of records.
    */
   public void setTable(VdxSection table, int maxRecords);

   /**
    * @return the name of the object.
    */
   public String getName();
}
