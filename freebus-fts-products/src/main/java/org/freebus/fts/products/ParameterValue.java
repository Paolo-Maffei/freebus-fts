package org.freebus.fts.products;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.persistence.vdx.VdxField;

/**
 * Values of a parameter type.
 */
@Entity
@Table(name = "parameter_list_of_values")
public class ParameterValue implements Serializable
{
   private static final long serialVersionUID = -8116925766267246754L;

   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenParameterValueId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "parameter_value_id", nullable = false)
   private int id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "parameter_type_id", nullable = false, referencedColumnName = "parameter_type_id")
   private ParameterType parameterType;

   @Column(name = "displayed_value")
   private String displayedValue;

   @VdxField(name = "display_oder")
   @Column(name = "display_oder")
   private int displayOrder;

   @Column(name = "real_value")
   private int intValue;

   @Column(name = "binary_value")
   private String binaryValue;

   @Column(name = "double_value")
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
    * @return the parameter type to which this object belongs.
    */
   public ParameterType getParameterType()
   {
      return parameterType;
   }

   /**
    * Set the parameter type to which this object belongs.
    */
   public void setParameterType(ParameterType parameterType)
   {
      this.parameterType = parameterType;
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

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return id;
   }
}
