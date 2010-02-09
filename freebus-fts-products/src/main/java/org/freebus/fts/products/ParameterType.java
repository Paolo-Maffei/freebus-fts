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
 * The type of a program's parameter. The parameter type holds the
 * possible values for a parameter.
 * 
 * What one would expect here, which type a parameter is, contains
 * the class {@link ParameterAtomicType}, which can be access with
 * {@link #getAtomicType()}.
 */
@Entity
@Table(name = "parameter_type")
public class ParameterType
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenParameterTypeId")
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

   @Column(name = "minimum_value")
   private double minValue;

   @Column(name = "maximum_value")
   private double maxValue;

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

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return id;
   }
}
