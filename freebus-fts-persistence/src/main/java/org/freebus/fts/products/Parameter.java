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

import org.freebus.fts.interfaces.Named;

/**
 * A parameter of a program.
 */
@Entity(name = "parameter")
@Table(name = "\"parameter\"")
public class Parameter implements Named
{
   @Id
   @TableGenerator(name = "Parameter", initialValue = 1, allocationSize = 100)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "Parameter")
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

   // The parameter number is unique for the program and starts with 1
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
   @Override
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
    * 
    * @param program - the program to set.
    */
   public void setProgram(Program program)
   {
      this.program = program;
   }

   /**
    * Get the parameter type.
    * 
    * @return the parameter type.
    */
   public ParameterType getParameterType()
   {
      return paramType;
   }

   /**
    * Set the parameter type.
    * 
    * @param paramType - the parameter type to set.
    */
   public void setParameterType(ParameterType paramType)
   {
      this.paramType = paramType;
   }

   /**
    * Get the {@link ParameterAtomicType atomic type} of the parameter. This is
    * a shortcut that fetches the atomic type from the parameter's
    * {@link #getParameterType() parameter type} object.
    * 
    * @return The atomic type of the parameter, or null if the parameter has no
    *         {@link #getParameterType() parameter type}.
    */
   public ParameterAtomicType getAtomicType()
   {
      if (paramType == null)
         return null;
      return paramType.getAtomicType();
   }

   /**
    * Set the parameter number.
    * 
    * @param number the number to set
    * @see #getNumber()
    */
   public void setNumber(int number)
   {
      this.number = number;
   }

   /**
    * Get the parameter number. The parameter number is unique for the
    * {@link #getProgram() program} and starts with 1 for the first parameter of
    * a program. It is probably used to identify the parameter when the
    * parameter-id was mapped when the parameter is imported.
    * 
    * @return the number
    */
   public int getNumber()
   {
      return number;
   }

   /**
    * Get the parameter low-access. Observed values: 0, 1, 2.
    * 
    * @return the low-access
    */
   public int getLowAccess()
   {
      return lowAccess;
   }

   /**
    * Set the parameter low-access.
    * 
    * @param lowAccess - the low-access to set
    * @see #getLowAccess()
    */
   public void setLowAccess(int lowAccess)
   {
      this.lowAccess = lowAccess;
   }

   /**
    * Get the parameter high-access. The following values can be returned:
    * <ul>
    * <li>0 - the parameter is invisible,
    * <li>1 - the parameter is read-only,
    * <li>2 - the parameter is read/write.
    * </ul>
    * 
    * @return the high-access.
    */
   public int getHighAccess()
   {
      return highAccess;
   }

   /**
    * Set the parameter high-access.
    * 
    * @param highAccess - the high-access to set
    * @see #getHighAccess()
    */
   public void setHighAccess(int highAccess)
   {
      this.highAccess = highAccess;
   }

   /**
    * @return The parent parameter or null if the parameter has no parent.
    */
   public Parameter getParent()
   {
      return parent;
   }

   /**
    * Set the parent parameter.
    * 
    * @param parent - the parent parameter (null allowed).
    */
   public void setParent(Parameter parent)
   {
      this.parent = parent;
   }

   /**
    * @return the expected parent parameter's value, or null if undefined.
    */
   public Integer getParentValue()
   {
      return parentValue;
   }

   /**
    * Set the expected parent parameter's value. May be null, if undefined.
    * 
    * @param parentValue - the parent value to set.
    */
   public void setParentValue(Integer parentValue)
   {
      this.parentValue = parentValue;
   }

   /**
    * @return The size of the parameter in bits (?).
    */
   public int getSize()
   {
      return size;
   }

   /**
    * Set the size of the parameter.
    * 
    * @param size - the size to set
    */
   public void setSize(int size)
   {
      this.size = size;
   }

   /**
    * @return the function.
    */
   public String getFunction()
   {
      return function;
   }

   /**
    * @param function - the function to set
    */
   public void setFunction(String function)
   {
      this.function = function;
   }

   /**
    * @return the display order
    */
   public int getDisplayOrder()
   {
      return displayOrder;
   }

   /**
    * @param displayOrder - the display order to set
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
    * Returns the default value. Depending on the atomic type of the parameter
    * value this can be an {@link Integer}, {@link String}, or <code>null</code>
    * .
    * 
    * @return the default value.
    */
   public Object getDefault()
   {
      if (paramType == null)
         return null;

      final ParameterAtomicType atomicType = paramType.getAtomicType();

      if (atomicType == ParameterAtomicType.SIGNED || atomicType == ParameterAtomicType.UNSIGNED
            || atomicType == ParameterAtomicType.ENUM || atomicType == ParameterAtomicType.LONG_ENUM)
         return defaultLong;
      else if (atomicType == ParameterAtomicType.STRING)
         return defaultString;

      return null;
   }

   /**
    * @return the default long value.
    */
   public int getDefaultLong()
   {
      return defaultLong;
   }

   /**
    * Set the default long value.
    * 
    * @param defaultLong - the default value to set
    */
   public void setDefaultLong(int defaultLong)
   {
      this.defaultLong = defaultLong;
   }

   /**
    * @return the default string value.
    */
   public String getDefaultString()
   {
      return defaultString;
   }

   /**
    * Set the default string value.
    * 
    * @param defaultString - the default value to set
    */
   public void setDefaultString(String defaultString)
   {
      this.defaultString = defaultString;
   }

   /**
    * @return the default double value.
    */
   public double getDefaultDouble()
   {
      return defaultDouble;
   }

   /**
    * Set the default double value.
    * 
    * @param defaultDouble - the default value to set
    */
   public void setDefaultDouble(double defaultDouble)
   {
      this.defaultDouble = defaultDouble;
   }

   /**
    * Get the patch-always flag. Seems to be boolean (0 / 1). Observed values:
    * 0.
    * 
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
    * Returns the address space. Seems to be a newer feature. Observed values:
    * 0, 1, 2. Default is 0.
    * 
    * @return the address space
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
    * Test if the parameter denotes a page. This is, if the
    * {@link #getAddress() address} is null.
    * 
    * @return true if the parameter is a page.
    * @see #isLabel()
    */
   public boolean isPage()
   {
      return address == null;
   }

   /**
    * Test if the parameter denotes a label (a fixed text without a value). This
    * is, if the {@link #getAddress() address} is not null and the
    * {@link #getAtomicType() atomic type} is {@link ParameterAtomicType#NONE}.
    * 
    * @return true if the parameter is a page.
    * @see #isPage()
    */
   public boolean isLabel()
   {
      return getAtomicType() == ParameterAtomicType.NONE && address != null;
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
