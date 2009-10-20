package org.freebus.fts.eib;


/**
 * The control-types of EIB bus telegrams.
 * This are the corresponding bits from the control field of a telegram.
 */
public enum ControlType
{
   /**
    * A data telegram.
    */
   DATA(0),

   /**
    * A poll telegram.
    */
   POLL(1);
   
   /**
    * The id.
    */
   public final int id;

   /**
    * @return the transport-type for the given transport control field contents.
    */
   static public ControlType valueOf(int id)
   {
      for (ControlType e: values())
         if (e.id == id) return e;

      throw new IllegalArgumentException("Invalid control type: #" + Integer.toString(id));
   }

   /*
    * Internal constructor
    */
   private ControlType(int id)
   {
      this.id = id;
   }
}
