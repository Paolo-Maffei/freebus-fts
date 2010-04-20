package org.freebus.fts.components.telegramdetails;

/**
 * Interface for telegram details parts.
 */
public interface DetailsPart
{
   /**
    * Set the telegram bytes that the details part displays.
    *
    * @param data - the bytes to display.
    */
   public void setData(byte[] data);

   /**
    * @return the telegram bytes of the details part.
    */
   public byte[] getData();
}
