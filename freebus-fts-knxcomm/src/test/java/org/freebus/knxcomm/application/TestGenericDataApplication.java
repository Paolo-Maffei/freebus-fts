package org.freebus.knxcomm.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestGenericDataApplication
{
   @Test
   public final void testGenericDataApplicationApplicationType()
   {
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.IndividualAddress_Read);

      assertNotNull(app);
      assertNotNull(app.hashCode());
      assertNotNull(app.toString());
      assertNull(app.getData());
      assertEquals(ApplicationType.IndividualAddress_Read, app.getType());
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testGenericDataApplicationApplicationTypeNull()
   {
      new GenericDataApplication(null);
   }

   @Test
   public final void testGenericDataApplicationApplicationTypeIntArray()
   {
      final int[] data = new int[] { 1, 2, 3, 4 };
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.IndividualAddress_Read, data);

      assertNotNull(app);
      assertNotNull(app.hashCode());
      assertNotNull(app.toString());
      assertArrayEquals(data, app.getData());
      assertFalse(data == app.getData());
      assertEquals(ApplicationType.IndividualAddress_Read, app.getType());
   }

   @Test
   public final void testGetSetData()
   {
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.IndividualAddress_Read);

      app.setData(new int[] { 4, 8, 12 });
      assertArrayEquals(new int[] { 4, 8, 12 }, app.getData());

      app.setData(null);
      assertNull(app.getData());
   }

   @Test
   public final void testFromRawData() throws InvalidDataException
   {
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.IndividualAddress_Read);
      app.fromRawData(new int[] { 0x00, 1, 4, 9 }, 0, 4);

      assertEquals(ApplicationType.IndividualAddress_Read, app.getType());
      assertArrayEquals(new int[] { 1, 4, 9 }, app.getData());
   }

   @Test
   public final void testFromRawData2() throws InvalidDataException
   {
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.IndividualAddress_Read);
      app.fromRawData(new int[] { 0x00 }, 0, 1);

      assertEquals(ApplicationType.IndividualAddress_Read, app.getType());
      assertNull(app.getData());
   }

   @Test
   public final void testFromRawData3() throws InvalidDataException
   {
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.GroupValue_Response);
      app.fromRawData(new int[] { 0x45, 1, 4, 9 }, 0, 4);

      assertEquals(ApplicationType.GroupValue_Response, app.getType());
      assertArrayEquals(new int[] { 5, 1, 4, 9 }, app.getData());
   }

   @Test
   public final void testFromRawData4() throws InvalidDataException
   {
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.GroupValue_Response);
      app.fromRawData(new int[] { 0x45 }, 0, 1);

      assertEquals(ApplicationType.GroupValue_Response, app.getType());
      assertArrayEquals(new int[] { 5 }, app.getData());
   }

   @Test
   public final void testToRawData()
   {
      final int[] data = new int[] { 1, 4, 9 };
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.IndividualAddress_Read, data);
      final int[] rawData = new int[4];

      assertEquals(4, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x00, 1, 4, 9 }, rawData);
   }

   @Test
   public final void testToRawData2()
   {
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.IndividualAddress_Read);
      final int[] rawData = new int[1];

      assertEquals(1, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x00 }, rawData);
   }

   @Test
   public final void testToRawData3()
   {
      final int[] data = new int[] { 5, 1, 4, 9 };
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.GroupValue_Response, data);
      final int[] rawData = new int[4];

      assertEquals(4, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x45, 1, 4, 9 }, rawData);
   }

   @Test
   public final void testToRawData4()
   {
      final int[] data = new int[] { 5, 1, 4, 9 };
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.GroupValue_Response, data);
      final int[] rawData = new int[4];

      assertEquals(4, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x45, 1, 4, 9 }, rawData);
   }

   @Test
   public final void testToRawData5()
   {
      final int[] data = new int[] { 0, 1 };
      final GenericDataApplication app = new GenericDataApplication(ApplicationType.GroupValue_Write, data);
      final int[] rawData = new int[2];

      assertEquals(2, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x80, 0x01 }, rawData);
   }

   @Test
   public final void testEqualsObject()
   {
      final GenericDataApplication app1 = new GenericDataApplication(ApplicationType.IndividualAddress_Read, new int[] { 1, 2 });
      final GenericDataApplication app2 = new GenericDataApplication(ApplicationType.IndividualAddress_Read, new int[] { 1, 2 });
      final GenericDataApplication app3 = new GenericDataApplication(ApplicationType.GroupValue_Response, new int[] { 1, 2 });

      assertFalse(app1.equals(null));
      assertFalse(app1.equals(new Object()));
      assertTrue(app1.equals(app1));
      assertTrue(app1.equals(app2));
      assertFalse(app1.equals(app3));

      app2.setData(null);
      assertFalse(app1.equals(app2));

      app1.setData(new int[] { 1, 3, 7 });
      assertFalse(app1.equals(app2));

      app1.setData(null);
      assertTrue(app1.equals(app2));
   }

}
