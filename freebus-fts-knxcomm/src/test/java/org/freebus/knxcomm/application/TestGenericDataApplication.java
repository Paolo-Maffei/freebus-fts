package org.freebus.knxcomm.application;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.freebus.fts.common.HexString;
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
   public final void testFromRawData() throws IOException
   {
      final byte[] data = HexString.valueOf("80 01 04 09");
      final Application app = ApplicationFactory.createApplication(data);

      assertEquals(ApplicationType.GroupValue_Write, app.getType());
      assertArrayEquals(new int[] { 1, 4, 9 }, ((GroupValueWrite) app).getData());
   }

   @Test
   public final void testFromRawData2() throws IOException
   {
      final byte[] data = HexString.valueOf("87");
      final Application app = ApplicationFactory.createApplication(data);

      assertEquals(ApplicationType.GroupValue_Write, app.getType());
      final GroupValueWrite iapp = (GroupValueWrite) app;

      assertNull(iapp.getData());
      assertEquals(7, iapp.getApciValue());
   }

   @Test
   public final void testFromRawData3() throws IOException
   {
      final byte[] data = HexString.valueOf("41 05 01 04 09");
      final Application app = ApplicationFactory.createApplication(data);

      assertEquals(ApplicationType.GroupValue_Response, app.getType());
      final GroupValueResponse iapp = (GroupValueResponse) app;

      assertArrayEquals(new int[] { 5, 1, 4, 9 }, iapp.getData());
   }

   @Test
   public final void testFromRawData4() throws IOException
   {
      final byte[] data = HexString.valueOf("45");
      final Application app = ApplicationFactory.createApplication(data);

      assertEquals(ApplicationType.GroupValue_Response, app.getType());
      final GroupValueResponse iapp = (GroupValueResponse) app;

      assertNull(iapp.getData());
      assertEquals(5, iapp.getApciValue());
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
      final int[] rawData = new int[5];

      assertEquals(5, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x40, 5, 1, 4, 9 }, rawData);
   }

   @Test
   public final void testToRawData4()
   {
      final int[] data = new int[] { 5, 1, 4, 9 };
      final GroupValueResponse app = new GroupValueResponse(data);
      final int[] rawData = new int[5];

      assertEquals(5, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x40, 5, 1, 4, 9 }, rawData);
   }

   @Test
   public final void testToRawData5()
   {
      final int[] data = new int[] { 1 };
      final GroupValueWrite app = new GroupValueWrite(data);
      final int[] rawData = new int[2];

      assertEquals(2, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x80, 0x01 }, rawData);
   }

   @Test
   public final void testToRawData6()
   {
      final GroupValueResponse app = new GroupValueResponse(12);
      final int[] rawData = new int[1];

      assertEquals(1, app.toRawData(rawData, 0));
      assertArrayEquals(new int[] { 0x4c }, rawData);
      assertEquals(12, app.getApciValue());
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
