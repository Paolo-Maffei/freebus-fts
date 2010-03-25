package org.freebus.knxcomm.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestGenericApplication
{
   @Test
   public final void testGenericApplicationApplicationType()
   {
      final GenericApplication app = new GenericApplication(ApplicationType.IndividualAddress_Read);

      assertNotNull(app);
      assertNotNull(app.hashCode());
      assertNotNull(app.toString());
      assertEquals(ApplicationType.IndividualAddress_Read, app.getType());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testGenericApplicationApplicationTypeNull()
   {
      new GenericApplication(null);
   }

   @Test
   public final void testFromRawData() throws InvalidDataException
   {
      final GenericApplication app = new GenericApplication(ApplicationType.IndividualAddress_Read);
      app.fromRawData(new int[] { 0x00, 1, 4, 9 }, 0, 4);

      assertEquals(ApplicationType.IndividualAddress_Read, app.getType());
   }

   @Test
   public final void testFromRawData2() throws InvalidDataException
   {
      final GenericApplication app = new GenericApplication(ApplicationType.IndividualAddress_Read);
      app.fromRawData(new int[] { 0x00 }, 0, 1);

      assertEquals(ApplicationType.IndividualAddress_Read, app.getType());
   }

   @Test
   public final void testFromRawData3() throws InvalidDataException
   {
      final GenericApplication app = new GenericApplication(ApplicationType.GroupValue_Response);
      app.fromRawData(new int[] { 0x45, 1, 4, 9 }, 0, 4);

      assertEquals(ApplicationType.GroupValue_Response, app.getType());
   }

   @Test
   public final void testFromRawData4() throws InvalidDataException
   {
      final GenericApplication app = new GenericApplication(ApplicationType.GroupValue_Response);
      app.fromRawData(new int[] { 0x45 }, 0, 1);

      assertEquals(ApplicationType.GroupValue_Response, app.getType());
   }

   @Test
   public final void testToRawData()
   {
      final GenericApplication app = new GenericApplication(ApplicationType.IndividualAddress_Read);
      final int[] rawData = new int[1];

      assertEquals(1, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x00 }, rawData);
   }

   @Test
   public final void testToRawData2()
   {
      final GenericApplication app = new GenericApplication(ApplicationType.GroupValue_Response);
      final int[] rawData = new int[1];

      assertEquals(1, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x40 }, rawData);
   }

   @Test
   public final void testEqualsObject()
   {
      final GenericApplication app1 = new GenericApplication(ApplicationType.IndividualAddress_Read);
      final GenericApplication app2 = new GenericApplication(ApplicationType.IndividualAddress_Read);
      final GenericApplication app3 = new GenericApplication(ApplicationType.GroupValue_Response);

      assertFalse(app1.equals(null));
      assertFalse(app1.equals(new Object()));
      assertTrue(app1.equals(app1));
      assertTrue(app1.equals(app2));
      assertFalse(app1.equals(app3));
   }

}
