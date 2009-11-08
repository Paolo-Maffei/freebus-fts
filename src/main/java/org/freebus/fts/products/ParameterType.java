package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type of a program's parameter.
 */
@Entity
@Table(name = "parameter_type")
public class ParameterType
{
   @Id
   @Column(name = "parameter_type_id", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "atomic_type_number", columnDefinition = "SMALLINT")
   private int atomicTypeId;

   @Column(name = "program_id", columnDefinition = "INT")
   private int programId;

   @Column(name = "parameter_type_name", nullable = false)
   private String name;

   @Column(name = "minimum_value", columnDefinition = "DOUBLE")
   private double minValue;

   @Column(name = "maximum_value", columnDefinition = "DOUBLE")
   private double maxValue;

   @Column(name = "parameter_type_low_access", columnDefinition = "SMALLINT")
   private int lowAccess;

   @Column(name = "parameter_type_high_access", columnDefinition = "SMALLINT")
   private int highAccess;

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
    * @return the atomic type number.
    */
   public int getAtomicTypeId()
   {
      return atomicTypeId;
   }

   /**
    * Set the atomic type number.
    */
   public void setAtomicTypeId(int atomicTypeNumber)
   {
      this.atomicTypeId = atomicTypeNumber;
   }

   /**
    * @return the programId
    */
   public int getProgramId()
   {
      return programId;
   }

   /**
    * @param programId the programId to set
    */
   public void setProgramId(int programId)
   {
      this.programId = programId;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the minimum value. The minimum value is always a double, even for integer
    * parameters.
    */
   public double getMinValue()
   {
      return minValue;
   }

   /**
    * Set the minimum value. The minimum value is always a double, even for integer
    * parameters.
    */
   public void setMinValue(double minValue)
   {
      this.minValue = minValue;
   }

   /**
    * @return the maximum value. The maximum value is always a double, even for integer
    * parameters.
    */
   public double getMaxValue()
   {
      return maxValue;
   }

   /**
    * Set the maximum value. The maximum value is always a double, even for integer
    * parameters.
    */
   public void setMaxValue(double maxValue)
   {
      this.maxValue = maxValue;
   }

   /**
    * @return the lowAccess
    */
   public int getLowAccess()
   {
      return lowAccess;
   }

   /**
    * @param lowAccess the lowAccess to set
    */
   public void setLowAccess(int lowAccess)
   {
      this.lowAccess = lowAccess;
   }

   /**
    * @return the highAccess
    */
   public int getHighAccess()
   {
      return highAccess;
   }

   /**
    * @param highAccess the highAccess to set
    */
   public void setHighAccess(int highAccess)
   {
      this.highAccess = highAccess;
   }

   /**
    * @return the size
    */
   public int getSize()
   {
      return size;
   }

   /**
    * @param size the size to set
    */
   public void setSize(int size)
   {
      this.size = size;
   }

   @Column(name = "parameter_type_size", columnDefinition = "SMALLINT")
   private int size;
}
