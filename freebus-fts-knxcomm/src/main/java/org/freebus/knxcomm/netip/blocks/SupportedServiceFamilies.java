package org.freebus.knxcomm.netip.blocks;

import java.util.HashMap;
import java.util.Map;

import org.freebus.knxcomm.netip.types.DeviceDescriptionType;
import org.freebus.knxcomm.netip.types.ServiceFamily;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Supported service families device information block.
 */
public class SupportedServiceFamilies implements Block
{
   private final Map<ServiceFamily, Integer> families = new HashMap<ServiceFamily, Integer>();

   /**
    * Add a service family. If the same family was added before then it is
    * overwritten by this call.
    */
   public void addServiceFamily(ServiceFamily family, int version)
   {
      families.put(family, version);
   }

   /**
    * @return the version of a service family, or zero if not found.
    */
   public int getServiceFamily(ServiceFamily family)
   {
      final Integer v = families.get(family);
      if (v == null) return 0;
      return v;
   }

   /**
    * @return true if the object contains the given service family.
    */
   public boolean hasServiceFamily(ServiceFamily family)
   {
      return families.containsKey(family);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      int pos = start;

      final int numServices = (rawData[pos++] - 2) >> 1;
      // type code is skipped

      families.clear();
      for (int i = 0; i < numServices; ++i)
         families.put(ServiceFamily.valueOf(rawData[++pos]), rawData[++pos]);

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      int pos = start - 1;

      rawData[++pos] = (families.size() << 1) + 2;
      rawData[++pos] = DeviceDescriptionType.SERVICE_FAMILIES.code;

      for (ServiceFamily sf: families.keySet())
      {
         rawData[++pos] = sf.code;
         rawData[++pos] = families.get(sf);
      }

      return pos - start;
   }

}
