package org.freebus.knxcomm.jobs;

import org.freebus.fts.common.address.Address;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.telegram.Transport;

public class TelegramSearchConditions
{
   /**
    * @return the applicationType
    */
   public ApplicationType getApplicationType()
   {
      return applicationType;
   }
   /**
    * @param applicationType the applicationType to set
    */
   public void setApplicationType(ApplicationType applicationType)
   {
      this.applicationType = applicationType;
   }
   /**
    * @return the destAddress
    */
   public Address getDestAddress()
   {
      return destAddress;
   }
   /**
    * @param destAddress the destAddress to set
    */
   public void setDestAddress(Address destAddress)
   {
      this.destAddress = destAddress;
   }
   /**
    * @return the fromAddress
    */
   public Address getFromAddress()
   {
      return fromAddress;
   }
   /**
    * @param fromAddress the fromAddress to set
    */
   public void setFromAddress(Address fromAddress)
   {
      this.fromAddress = fromAddress;
   }
   /**
    * @return the routingCounter
    */
   public Integer getRoutingCounter()
   {
      return routingCounter;
   }
   /**
    * @param routingCounter the routingCounter to set
    */
   public void setRoutingCounter(Integer routingCounter)
   {
      this.routingCounter = routingCounter;
   }
   /**
    * @return the sequenceNumber
    */
   public Integer getSequenceNumber()
   {
      return sequneceNumber;
   }
   /**
    * @param sequenceNumber the sequenceNumber to set
    */
   public void setSequenceNumber(Integer sequenceNumber)
   {
      this.sequneceNumber = sequenceNumber;
   }
   /**
    * @return the transport
    */
   public Transport getTransport()
   {
      return transport;
   }
   /**
    * @param transport the transport to set
    */
   public void setTransport(Transport transport)
   {
      this.transport = transport;
   }
   ApplicationType applicationType;
   Address destAddress;
   Address fromAddress;
   Integer routingCounter;
   Integer sequneceNumber;
   Transport transport;
}
