package org.freebus.knxcomm.application.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class TestMemoryAddressMapper
{
   @Test
   public void testMemoryAddressMapper()
   {
      final MemoryAddressMapper mapper = new MemoryAddressMapper(0x0010);
      assertNotNull(mapper);

      assertEquals(0x4e, mapper.getAddress(MemoryLocation.Masktype, 0));
      assertEquals(0x5100, mapper.getAddress(MemoryLocation.SystemROM, 0x100));
      assertEquals(MemoryLocation.Masktype, mapper.getMapping(MemoryLocation.Masktype).getLocation());
   }

   @Test(expected = IOException.class)
   public void testMemoryAddressMapperNotFound() throws IOException
   {
      MemoryAddressMapper.getProperties(0xffff);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testMemoryAddressMapperNoMapping() throws IOException
   {
      final MemoryAddressMapper mapper = new MemoryAddressMapper(0x0010);
      assertNotNull(mapper);
      mapper.getAddress(null, 1);
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public void testMemoryAddressMapperOutOfRange() throws IOException
   {
      final MemoryAddressMapper mapper = new MemoryAddressMapper(0x0010);
      assertNotNull(mapper);
      mapper.getAddress(MemoryLocation.Masktype, 2);
   }
}
