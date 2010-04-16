package org.freebus.knxcomm.jobs;

import java.util.ArrayList;


import org.freebus.knxcomm.telegram.Telegram;

public class Telegrams extends ArrayList<Telegram>
{

   TelegramSearchConditions telegramSearchConditions;
   private static final long serialVersionUID = 4781083680657979456L;

   /**
    * search all telegrams in the telegram list by the TelegramSearchConditions
    * @param telegramSearchConditions - the TelegramSearchConditions for the search
    * @return all telegrams with the search parameter
    */
   public Telegrams searchTelegram(TelegramSearchConditions telegramSearchConditions){
      this.telegramSearchConditions = telegramSearchConditions;
      return searchApplicationType();

   }


   private Telegrams searchFromAddress(){
      Telegrams ts = new Telegrams();
      if (telegramSearchConditions.getFromAddress() ==null)return this;
      for (Telegram  t : this){
         if (t.getFrom().equals(telegramSearchConditions.getFromAddress() ))ts.add(t);
      }
      return ts;
   }

   private Telegrams searchDestAddress(){
      Telegrams ts = new Telegrams();
      if (telegramSearchConditions.getDestAddress() ==null)return searchFromAddress();
      for (Telegram  t : searchFromAddress()){
         if (t.getDest().equals(telegramSearchConditions.getDestAddress() ))ts.add(t);
      }
      return ts;
   }

   private Telegrams searchRoutingCounter(){
      Telegrams ts = new Telegrams();
      if (telegramSearchConditions.getRoutingCounter() ==null)return searchDestAddress();
      for (Telegram  t : searchDestAddress()){
         if (t.getRoutingCounter() == telegramSearchConditions.getRoutingCounter())ts.add(t);
      }
      return ts;
   }

   private Telegrams searchSequneceNumber(){
      Telegrams ts = new Telegrams();
      if (telegramSearchConditions.getSequenceNumber() ==null)return searchRoutingCounter();
      for (Telegram  t : searchRoutingCounter()){
         if (t.getSequence() == telegramSearchConditions.getSequenceNumber())ts.add(t);
      }
      return ts;
   }

   private Telegrams searchTransport(){
      Telegrams ts = new Telegrams();
      if (telegramSearchConditions.getTransport() ==null)return searchSequneceNumber();
      for (Telegram  t : searchSequneceNumber()){
         if (t.getTransport().equals(telegramSearchConditions.getTransport()))ts.add(t);
      }
      return ts;
   }

   private Telegrams searchApplicationType(){
      Telegrams ts = new Telegrams();
      if (telegramSearchConditions.getApplicationType() ==null)return searchTransport();
      for (Telegram  t : searchTransport()){
         if (t.getApplicationType().equals(telegramSearchConditions.getApplicationType()))ts.add(t);
      }
      return ts;
   }


}
