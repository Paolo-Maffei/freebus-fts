package org.freebus.fts.project;

import java.util.HashSet;
import java.util.Set;

/**
 * Project utility functions.
 */
public final class ProjectUtils
{
   /**
    * Get a set containing all addresses of the given objects.
    * 
    * @param objs - the objects to get the addresses from.
    * 
    * @return A set with the addresses.
    */
   public static Set<Integer> getAddresses(final Set<? extends Addressable> objs)
   {
      final Set<Integer> result = new HashSet<Integer>();

      for (final Addressable obj : objs)
         result.add(obj.getAddress());

      return result;
   }
   
   /**
    * Find a free address that is not used by any object in objs and is in the
    * range minAdddress to maxAddress (including min and max).
    * 
    * @param objs - the set of objects to inspect.
    * @param minAddress - the minimum valid address.
    * @param maxAddress - the maximum valid address.
    * 
    * @return A free address, or minAddress-1 if no free address could be found.
    */
   public static int getFreeAddress(final Set<? extends Addressable> objs, int minAddress, int maxAddress)
   {
      final Set<Integer> adds = getAddresses(objs);

      for (int address = minAddress; address <= maxAddress; ++address)
      {
         if (!adds.contains(address))
            return address;
      }

      return minAddress - 1;
   }

   /*
    * Disabled
    */
   private ProjectUtils()
   {
   }
}
