package org.freebus.knxcomm.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.freebus.fts.common.HexString;
import org.freebus.fts.common.address.PhysicalAddress;
import org.junit.Test;

public class TestIndividualAddressWrite
{
   @Test
   public final void testIndividualAddressWrite()
   {
      final IndividualAddressWrite app = new IndividualAddressWrite();
      assertNull(app.getAddress());
      assertEquals(ApplicationType.IndividualAddress_Write, app.getType());
   }

   @Test
   public final void testIndividualAddressWritePhysicalAddress()
   {
      final IndividualAddressWrite app = new IndividualAddressWrite(new PhysicalAddress(3, 45, 67));
      assertEquals(new PhysicalAddress(3, 45, 67), app.getAddress());
   }

   @Test
   public final void testGetSetAddress()
   {
      final IndividualAddressWrite app = new IndividualAddressWrite();

      app.setAddress(new PhysicalAddress(1, 17, 4));
      assertEquals(new PhysicalAddress(1, 17, 4), app.getAddress());

      app.setAddress(null);
      assertEquals(null, app.getAddress());
   }

   @Test
   public final void testFromRawData() throws IOException
   {
      final byte[] data = HexString.valueOf("c0 12 34");
      final Application gapp = ApplicationFactory.createApplication(0, data);

      assertEquals(ApplicationType.IndividualAddress_Write, gapp.getType());
      final IndividualAddressWrite app = (IndividualAddressWrite) gapp;

      assertEquals(new PhysicalAddress(1, 2, 0x34), app.getAddress());
   }

   @Test
   public final void testToRawData()
   {
      final IndividualAddressWrite app = new IndividualAddressWrite(new PhysicalAddress(50, 60));
      final int[] rawData = new int[16];
      assertEquals(3, app.toRawData(rawData, 0));
      assertEquals(ApplicationType.IndividualAddress_Write.getApci() & 255, rawData[0]);
      assertEquals(50, rawData[1]);
      assertEquals(60, rawData[2]);
   }

   @Test
   public final void testToRawDataNullAddr()
   {
      final IndividualAddressWrite app = new IndividualAddressWrite();
      final int[] rawData = new int[16];
      assertEquals(3, app.toRawData(rawData, 0));
      assertEquals(ApplicationType.IndividualAddress_Write.getApci() & 255, rawData[0]);
      assertEquals(0, rawData[1]);
      assertEquals(0, rawData[2]);
   }
}
