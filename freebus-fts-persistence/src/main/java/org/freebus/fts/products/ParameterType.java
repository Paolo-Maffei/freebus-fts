package org.freebus.fts.products;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.persistence.vdx.VdxField;

/**
 * The type of a program's parameter. The parameter type is used to group
 * parameters of the same type that can have the same range of values.
 * 
 * The parameter type holds e.g. the possible values for a parameter and
 * minimum/maximum values for numbers.
 * 
 * What one would expect here, which type a parameter is, contains the class
 * {@link ParameterAtomicType}, which can be access with
 * {@link #getAtomicType()}.
 */
@Entity
@Table(name = "parameter_type")
public class ParameterType
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequence", name = "GenParameterTypeId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "parameter_type_id", nullable = false)
   private int id;

   @VdxField(name = "atomic_type_number")
   @Enumerated(EnumType.ORDINAL)
   @Column(name = "atomic_type_id")
   private ParameterAtomicType atomicType;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "program_id", nullable = false)
   private Program program;

   @Column(name = "parameter_type_name", nullable = false)
   private String name;

   @VdxField(name = "parameter_minimum_value")
   @Column(name = "min_value")
   private int minValue;

   @VdxField(name = "parameter_maximum_value")
   @Column(name = "max_value")
   private int maxValue;

   @VdxField(name = "parameter_minimum_double_value")
   @Column(name = "min_double_value")
   private double minDoubleValue;

   @VdxField(name = "parameter_maximum_double_value")
   @Column(name = "max_double_value")
   private double maxDoubleValue;

   @Column(name = "parameter_type_low_access")
   private int lowAccess;

   @Column(name = "parameter_type_high_access")
   private int highAccess;

   @Column(name = "parameter_type_size")
   private int size;

   @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = "parameter_type_id")
   private Set<ParameterValue> values;

   /**
    * Create an empty parameter type.
    */
   public ParameterType()
   {
   }

   /**
    * Create a parameter type.
    * 
    * @param atomicType - the parameter's atomic type.
    */
   public ParameterType(ParameterAtomicType atomicType)
   {
      this.atomicType = atomicType;
   }

   /**
    * @return the id
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the id.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the atomic type of the parameter.
    */
   public ParameterAtomicType getAtomicType()
   {
      return atomicType;
   }

   /**
    * Set the atomic type of the parameter.
    */
   public void setAtomicType(ParameterAtomicType atomicType)
   {
      this.atomicType = atomicType;
   }

   /**
    * @return the program to which the parameter type belongs.
    */
   public Program getProgram()
   {
      return program;
   }

   /**
    * Set the program to which the parameter type belongs.
    */
   public void setProgram(Program program)
   {
      this.program = program;
   }

   /**
    * @return the name of the parameter type.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the parameter type.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the minimum value.
    */
   public int getMinValue()
   {
      return minValue;
   }

   /**
    * Set the minimum value.
    */
   public void setMinValue(int minValue)
   {
      this.minValue = minValue;
   }

   /**
    * @return the maximum value.
    */
   public int getMaxValue()
   {
      return maxValue;
   }

   /**
    * Set the maximum value.
    */
   public void setMaxValue(int maxValue)
   {
      this.maxValue = maxValue;
   }

   /**
    * @return the minimum double value.
    */
   public double getMinDoubleValue()
   {
      return minDoubleValue;
   }

   /**
    * Set the minimum value.
    */
   public void setMinDoubleValue(double minValue)
   {
      this.minDoubleValue = minValue;
   }

   /**
    * @return the maximum value.
    */
   public double getMaxDoubleValue()
   {
      return maxDoubleValue;
   }

   /**
    * Set the maximum value.
    */
   public void setMaxDoubleValue(double maxValue)
   {
      this.maxDoubleValue = maxValue;
   }

   /**
    * @return the allowed values or null if no specific allowed values are set.
    */
   public Set<ParameterValue> getValues()
   {
      return values;
   }

   /**
    * Set the allowed values or null if no specific allowed values are set.
    */
   public void setValues(Set<ParameterValue> values)
   {
      this.values = values;
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
    * @return the size of the parameter in bits.
    */
   public int getSize()
   {
      return size;
   }

   /**
    * Set the size of the parameter in bits.
    * 
    * @param size the size to set
    */
   public void setSize(int size)
   {
      this.size = size;
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
