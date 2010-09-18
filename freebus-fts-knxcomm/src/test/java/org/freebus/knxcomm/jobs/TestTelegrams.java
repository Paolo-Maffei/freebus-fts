package org.freebus.knxcomm.jobs;


import static org.junit.Assert.assertEquals;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.jobs.steps.TelegramSearchConditions;
import org.freebus.knxcomm.jobs.steps.Telegrams;
import org.freebus.knxcomm.telegram.Telegram;
import org.junit.Ignore;
import org.junit.Test;


public class TestTelegrams
{

   @Ignore
   private Telegrams createTelegrams(){
 Telegrams ts = new Telegrams();


      Telegram t1 = new Telegram();
      t1.setFrom(PhysicalAddress.valueOf("1.2.1"));
      t1.setDest(PhysicalAddress.valueOf("4.5.6"));
      t1.setApplicationType(ApplicationType.Memory_Read);
      t1.setRoutingCounter(6);
      t1.setSequence(4);
      ts.add(t1);


      Telegram t2 = new Telegram();
      t2.setFrom(PhysicalAddress.valueOf("1.2.2"));
      t2.setDest(PhysicalAddress.valueOf("4.5.6"));
      t2.setApplicationType(ApplicationType.Memory_Response);
      t2.setRoutingCounter(6);
      t2.setSequence(5);
      ts.add(t2);

      Telegram t3 = new Telegram();
      t3.setFrom(PhysicalAddress.valueOf("1.2.3"));
      t3.setDest(PhysicalAddress.valueOf("4.5.7"));
      t3.setApplicationType(ApplicationType.Memory_Read);
      t3.setRoutingCounter(6);
      t3.setSequence(7);
      ts.add(t3);


      Telegram t4 = new Telegram();
      t4.setFrom(PhysicalAddress.valueOf("1.2.4"));
      t4.setDest(PhysicalAddress.valueOf("4.5.6"));
      t4.setApplicationType(ApplicationType.ADC_Response);
      t4.setRoutingCounter(6);
      t4.setSequence(4);
      ts.add(t4);

      return ts;
   }
   @Test
   public final void testSearchTelegram(){
      Telegrams ts =  createTelegrams();

      TelegramSearchConditions tsc =new TelegramSearchConditions();
      tsc.setSequenceNumber(4);

      assertEquals(ts.get(0), ts.searchTelegram(tsc).get(0));
   }

   @Test
   public final void testSearchTelegramApplicationType(){
      Telegrams ts =  createTelegrams();

      TelegramSearchConditions tsc =new TelegramSearchConditions();
      tsc.setApplicationType(ApplicationType.ADC_Response);

      assertEquals(ts.get(3), ts.searchTelegram(tsc).get(0));
   }
}
