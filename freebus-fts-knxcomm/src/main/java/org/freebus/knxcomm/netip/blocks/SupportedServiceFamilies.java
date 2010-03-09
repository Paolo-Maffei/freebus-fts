package org.freebus.knxcomm.netip.blocks;

import java.util.HashMap;
import java.util.Map;

import org.freebus.knxcomm.netip.types.DescriptionInfoType;
import org.freebus.knxcomm.netip.types.ServiceFamily;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Supported service families device information block.
 */
public class SupportedServiceFamilies implements DescriptionInfoBlock
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
   public int fromData(int[] data, int start) throws InvalidDataException
   {
      int pos = start;

      final int numServices = (data[pos++] - 2) >> 1;

      final int typeCode = data[pos++];
      final DescriptionInfoType type = DescriptionInfoType.valueOf(typeCode);
      if (type != DescriptionInfoType.SERVICE_FAMILIES)
         throw new InvalidDataException("Invalid type " + type + ", expected " + DescriptionInfoType.SERVICE_FAMILIES, typeCode);

      families.clear();
      for (int i = 0; i < numServices; ++i)
         families.put(ServiceFamily.valueOf(data[pos++]), data[pos++]);

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toData(int[] data, int start)
   {
      int pos = start;

      data[pos++] = (families.size() << 1) + 2;
      data[pos++] = DescriptionInfoType.SERVICE_FAMILIES.code;

      for (ServiceFamily sf: families.keySet())
      {
         data[pos++] = sf.code;
         data[pos++] = families.get(sf);
      }

      return pos - start;
   }

}
