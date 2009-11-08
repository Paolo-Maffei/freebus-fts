package org.freebus.fts.products;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Values of a parameter type.
 */
@Entity
@Table(name = "parameter_list_of_values")
public class ParameterValue implements Serializable
{
   private static final long serialVersionUID = -8116925766267246754L;

   @Id
   @Column(name = "parameter_value_id", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "parameter_type_id", columnDefinition = "INT", nullable = false)
   private int parameterTypeId;

   @Column(name = "displayed_value")
   private String displayedValue;

   @Column(name = "display_oder", columnDefinition = "SMALLINT")
   private int displayOrder;

   @Column(name = "real_value", columnDefinition = "INT")
   private int intValue;

   @Column(name = "binary_value")
   private String binaryValue;

   @Column(name = "double_value", columnDefinition = "DOUBLE")
   private double doubleValue;

   /**
    * @return the id
    */
   public int getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the parameterTypeId
    */
   public int getParameterTypeId()
   {
      return parameterTypeId;
   }

   /**
    * @param parameterTypeId the parameterTypeId to set
    */
   public void setParameterTypeId(int parameterTypeId)
   {
      this.parameterTypeId = parameterTypeId;
   }

   /**
    * @return the displayedValue
    */
   public String getDisplayedValue()
   {
      return displayedValue;
   }

   /**
    * @param displayedValue the displayedValue to set
    */
   public void setDisplayedValue(String displayedValue)
   {
      this.displayedValue = displayedValue;
   }

   /**
    * @return the displayOrder
    */
   public int getDisplayOrder()
   {
      return displayOrder;
   }

   /**
    * @param displayOrder the displayOrder to set
    */
   public void setDisplayOrder(int displayOrder)
   {
      this.displayOrder = displayOrder;
   }

   /**
    * @return the intValue
    */
   public int getIntValue()
   {
      return intValue;
   }

   /**
    * @param intValue the intValue to set
    */
   public void setIntValue(int intValue)
   {
      this.intValue = intValue;
   }

   /**
    * @return the binaryValue
    */
   public String getBinaryValue()
   {
      return binaryValue;
   }

   /**
    * @param binaryValue the binaryValue to set
    */
   public void setBinaryValue(String binaryValue)
   {
      this.binaryValue = binaryValue;
   }

   /**
    * @return the doubleValue
    */
   public double getDoubleValue()
   {
      return doubleValue;
   }

   /**
    * @param doubleValue the doubleValue to set
    */
   public void setDoubleValue(double doubleValue)
   {
      this.doubleValue = doubleValue;
   }
}
