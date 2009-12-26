package org.freebus.fts.products;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A parameter of a program.
 */
@Entity
@Table(name = "parameter")
public class Parameter implements Serializable
{
   private static final long serialVersionUID = 6541645278498553618L;

   @Id
   @Column(name = "parameter_id", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "parameter_name")
   private String name;

   @Column(name = "parameter_label")
   private String label;

   @Column(name = "parameter_description")
   private String description;

   @Column(name = "program_id", columnDefinition = "INT", nullable = false)
   private int programId;

   @Column(name = "parameter_type_id", columnDefinition = "INT", nullable = false)
   private int parameterTypeId;

   @Column(name = "parameter_low_access", columnDefinition = "SMALLINT")
   private int lowAccess;

   @Column(name = "parameter_high_access", columnDefinition = "SMALLINT")
   private int highAccess;

   @Column(name = "par_parameter_id", columnDefinition = "INT")
   private int parentId;

   @Column(name = "parent_parm_value", columnDefinition = "INT")
   private int parentValue;

   @Column(name = "parameter_size", columnDefinition = "SMALLINT")
   private int size;

   @Column(name = "parameter_function")
   private String function;

   @Column(name = "parameter_display_order", columnDefinition = "INT")
   private int displayOrder;

   @Column(name = "parameter_address", columnDefinition = "INT")
   private int address;

   @Column(name = "parameter_bitoffset", columnDefinition = "SMALLINT")
   private int bitOffset;

   @Column(name = "parameter_default_long", columnDefinition = "INT")
   private int defaultLong;

   @Column(name = "parameter_default_string")
   private String defaultString;

   @Column(name = "parameter_default_double", columnDefinition = "DOUBLE")
   private int defaultDouble;

   @Column(name = "patch_always", columnDefinition = "SMALLINT")
   private int patchAlways;

   @Column(name = "address_space", columnDefinition = "SMALLINT")
   private int addressSpace;

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
      this.name = name;
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
    * @return the parentId
    */
   public int getParentId()
   {
      return parentId;
   }

   /**
    * @param parentId the parentId to set
    */
   public void setParentId(int parentId)
   {
      this.parentId = parentId;
   }

   /**
    * @return the parentValue
    */
   public int getParentValue()
   {
      return parentValue;
   }

   /**
    * @param parentValue the parentValue to set
    */
   public void setParentValue(int parentValue)
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
    * @return the address
    */
   public int getAddress()
   {
      return address;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(int address)
   {
      this.address = address;
   }

   /**
    * @return the bitOffset
    */
   public int getBitOffset()
   {
      return bitOffset;
   }

   /**
    * @param bitOffset the bitOffset to set
    */
   public void setBitOffset(int bitOffset)
   {
      this.bitOffset = bitOffset;
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
   public int getDefaultDouble()
   {
      return defaultDouble;
   }

   /**
    * @param defaultDouble the defaultDouble to set
    */
   public void setDefaultDouble(int defaultDouble)
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
}
