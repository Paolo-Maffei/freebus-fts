package org.freebus.fts.project;

/**
 * Room types.
 */
/*
 * Note for programmers: add new types always at the end of the enum.
 * Otherwise existing projects will break.
 */
public enum RoomType
{
   /**
    * A room.
    */
   ROOM('F'),
   
   /**
    * A control cabinet.
    */
   CABINET('P');

   /**
    * The room type outline character that ETS uses.
    */
   public final char outline;

   /*
    * Internal constructor.
    */
   private RoomType(char outline)
   {
      this.outline = outline;
   }
}
