package org.freebus.fts.products;

import javax.persistence.CascadeType;
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

/**
 * A parameter of a program.
 */
@Entity(name = "parameter")
@Table(name = "\"parameter\"")
public class Parameter
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequence",  name = "GenParameterId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "parameter_id", nullable = false)
   private int id;

   @Column(name = "parameter_name", nullable = false)
   private String name = "";

   @Column(name = "parameter_label")
   private String label;

   @Column(name = "parameter_description")
   private String description;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "program_id", nullable = false)
   private Program program;

   @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
   @JoinColumn(name = "parameter_type_id", nullable = false)
   private ParameterType paramType;

   @Column(name = "parameter_number")
   private int number;

   @Column(name = "parameter_low_access", columnDefinition = "SMALLINT")
   private int lowAccess;

   @Column(name = "parameter_high_access", columnDefinition = "SMALLINT")
   private int highAccess;

   @ManyToOne(optional = true)
   @JoinColumn(name = "par_parameter_id", nullable = true)
   private Parameter parent;

   @Column(name = "parent_parm_value", nullable = true)
   private Integer parentValue;

   @Column(name = "parameter_size", columnDefinition = "SMALLINT")
   private int size;

   @Column(name = "parameter_function")
   private String function;

   @Column(name = "parameter_display_order")
   private int displayOrder;

   @Column(name = "parameter_address")
   private Integer address;

   @Column(name = "parameter_bitoffset", columnDefinition = "SMALLINT")
   private int bitOffset;

   @Column(name = "parameter_default_long")
   private int defaultLong;

   @Column(name = "parameter_default_string")
   private String defaultString;

   @Column(name = "parameter_default_double", columnDefinition = "DOUBLE")
   private double defaultDouble;

   @Column(name = "patch_always", columnDefinition = "SMALLINT")
   private int patchAlways;

   @Column(name = "address_space", columnDefinition = "SMALLINT")
   private int addressSpace;

   @Column(name = "visible")
   private boolean visible;

   /**
    * Create an empty parameter object.
    */
   public Parameter()
   {
   }

   /**
    * Create a parameter object with a parameter type.
    *
    * @param paramType - the parameter type of the parameter.
    */
   public Parameter(final ParameterType paramType)
   {
      this.paramType = paramType;
   }

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
      this.name = name == null ? "" : name;
   }

   /**
    * @return the label
    */
   public String getLabel()
   {
      return label;
   }

   /**
    * @param label the label to set
    */
   public void setLabel(String label)
   {
      this.label = label;
   }

   /**
    * @return the description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return the program to which the parameter belongs.
    */
   public Program getProgram()
   {
      return program;
   }

   /**
    * Set the program to which the parameter belongs.
    */
   public void setProgram(Program program)
   {
      this.program = program;
   }

   /**
    * @return the parameter type.
    */
   public ParameterType getParameterType()
   {
      return paramType;
   }

   /**
    * Set the parameter type.
    */
   public void setParameterType(ParameterType paramType)
   {
      this.paramType = paramType;
   }

   /**
    * @param number the number to set
    */
   public void setNumber(int number)
   {
      this.number = number;
   }

   /**
    * @return the number
    */
   public int getNumber()
   {
      return number;
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
    * @return the parent parameter
    */
   public Parameter getParent()
   {
      return parent;
   }

   /**
    * Set the parent parameter.
    */
   public void setParent(Parameter parent)
   {
      this.parent = parent;
   }

   /**
    * @return the parent parameter value, or null if undefined.
    */
   public Integer getParentValue()
   {
      return parentValue;
   }

   /**
    * Set the parent parameter value. May be null, if undefined.
    */
   public void setParentValue(Integer parentValue)
   {
      this.parentValue = parentValue;
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
    * @return the function
    */
   public String getFunction()
   {
      return function;
   }

   /**
    * @param function the function to set
    */
   public void setFunction(String function)
   {
      this.function = function;
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
    * @return the address of the parameter, may be null.
    */
   public Integer getAddress()
   {
      return address;
   }

   /**
    * Set the address of the parameter.
    *
    * @param address the address to set, may be null.
    */
   public void setAddress(Integer address)
   {
      this.address = address;
   }

   /**
    * @return the bit offset of the parameter value.
    */
   public int getBitOffset()
   {
      return bitOffset;
   }

   /**
    * Set the bit offset of the parameter value.
    *
    * @param bitOffset the bitOffset to set
    */
   public void setBitOffset(int bitOffset)
   {
      this.bitOffset = bitOffset;
   }

   /**
    * Returns the default value. Depending on the atomic type of
    * the parameter value this can be an {@link Integer}, {@link String},
    * or <code>null</code>.
    *
    * @return the default value.
    */
   public Object getDefault()
   {
      if (paramType == null)
         return null;

      final ParameterAtomicType atomicType = paramType.getAtomicType();

      if (atomicType == ParameterAtomicType.SIGNED || atomicType == ParameterAtomicType.UNSIGNED ||
            atomicType == ParameterAtomicType.ENUM || atomicType == ParameterAtomicType.LONG_ENUM)
         return defaultLong;
      else if (atomicType == ParameterAtomicType.STRING)
         return defaultString;

      return null;
   }

   /**
    * @return the defaultLong
    */
   public int getDefaultLong()
   {
      return defaultLong;
   }

   /**
    * @param defaultLong the defaultLong to set
    */
   public void setDefaultLong(int defaultLong)
   {
      this.defaultLong = defaultLong;
   }

   /**
    * @return the defaultString
    */
   public String getDefaultString()
   {
      return defaultString;
   }

   /**
    * @param defaultString the defaultString to set
    */
   public void setDefaultString(String defaultString)
   {
      this.defaultString = defaultString;
   }

   /**
    * @return the defaultDouble
    */
   public double getDefaultDouble()
   {
      return defaultDouble;
   }

   /**
    * @param defaultDouble the defaultDouble to set
    */
   public void setDefaultDouble(double defaultDouble)
   {
      this.defaultDouble = defaultDouble;
   }

   /**
    * @return the patchAlways
    */
   public int getPatchAlways()
   {
      return patchAlways;
   }

   /**
    * @param patchAlways the patchAlways to set
    */
   public void setPatchAlways(int patchAlways)
   {
      this.patchAlways = patchAlways;
   }

   /**
    * @return the addressSpace
    */
   public int getAddressSpace()
   {
      return addressSpace;
   }

   /**
    * @param addressSpace the addressSpace to set
    */
   public void setAddressSpace(int addressSpace)
   {
      this.addressSpace = addressSpace;
   }

   /**
    * @param visible the visible to set
    */
   public void setVisible(boolean visible)
   {
      this.visible = visible;
   }

   /**
    * @return the visible
    */
   public boolean isVisible()
   {
      return visible;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Parameter))
         return false;

      final Parameter oo = (Parameter) o;

      if ((program == null && oo.program != null) || (name == null && oo.name != null))
         return false;

      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getClass().getSimpleName() + " #" + id + " \"" + name + "\"";
   }
}
