package org.freebus.fts.comm;

/**
 * A "Physical External Interface" (PEI) message.
 * This is a message to/from the BCU / network interface / FT1.2-controller.
 *
 * This class is package internal.
 */
final class PeiMessage
{
   public int tries = 0;
   public final int maxTries;
   public final int[] data;
   public final int length;

   public PeiMessage(int[] data, int length, int maxTries)
   {
      this.data = data;
      this.length = length;
      this.maxTries = maxTries;
   }
}
