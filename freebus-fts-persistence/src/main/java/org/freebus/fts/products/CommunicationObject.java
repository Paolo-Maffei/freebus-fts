package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.common.types.ObjectPriority;
import org.freebus.fts.common.types.ObjectType;
import org.freebus.fts.persistence.vdx.VdxField;

/**
 * A communication object of a program. Communication objects are the interface
 * to other devices on the bus. Group data telegrams send and receive the data
 * of the communication objects.
 */
@Entity
@Table(name = "communication_object")
public class CommunicationObject implements Comparable<CommunicationObject>
{
   @Id
   @TableGenerator(name = "CommunicationObject", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "CommunicationObject")
   @Column(name = "object_id", nullable = false)
   private int id;

   @Column(name = "object_name", nullable = false)
   private String name = "";

   @ManyToOne(optional = false)
   @JoinColumn(name = "program_id", nullable = false)
   private Program program;

   @ManyToOne(optional = true)
   @JoinColumn(name = "parameter_id")
   private Parameter parameter;

   @Column(name = "object_function")
   private String function;

   @Column(name = "object_number", nullable = false)
   private int number;

   @Column(name = "object_unique_number", nullable = false)
   private int uniqueNumber;

   @Column(name = "object_readenabled", nullable = false)
   private boolean readEnabled;

   @Column(name = "object_writeenabled", nullable = false)
   private boolean writeEnabled;

   @Column(name = "object_commenabled", nullable = false)
   private boolean commEnabled;

   @Column(name = "object_transenabled", nullable = false)
   private boolean transEnabled;

   @Column(name = "display_order")
   @VdxField(name = "object_display_order")
   private int displayOrder;

   @Column(name = "parent_parameter_value")
   private Integer parentParameterValue;

   @Column(name = "object_description")
   private String description;

   @Enumerated(EnumType.ORDINAL)
   @Column(name = "object_type", nullable = false)
   private ObjectType type;

   @Enumerated(EnumType.ORDINAL)
   @Column(name = "object_priority", nullable = false)
   private ObjectPriority priority;

   @Column(name = "object_updateenabled", nullable = false)
   private boolean updateEnabled;

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
    * @return the program
    */
   public Program getProgram()
   {
      return program;
   }

   /**
    * Set the program.
    */
   public void setProgram(Program program)
   {
      this.program = program;
   }

   /**
    * @return the parameter
    */
   public Parameter getParameter()
   {
      return parameter;
   }

   /**
    * Set the parameter.
    */
   public void setParameter(Parameter parameter)
   {
      this.parameter = parameter;
   }

   /**
    * @return the descriptive function name.
    */
   public String getFunction()
   {
      return function;
   }

   /**
    * Set the descriptive function name.
    */
   public void setFunction(String function)
   {
      this.function = function;
   }

   /**
    * Set the unique number.
    * 
    * @param number - The unique number to set.
    * 
    * @see #getUniqueNumber()
    */
   public void setUniqueNumber(int uniqueNumber)
   {
      this.uniqueNumber = uniqueNumber;
   }

   /**
    * The unique number is unique within the {@link Program application program}
    * .
    * 
    * @return The unique number.
    */
   public int getUniqueNumber()
   {
      return uniqueNumber;
   }

   /**
    * Set the number.
    * 
    * @param number - The unique number to set.
    * 
    * @see #getNumber()
    */
   public void setNumber(int number)
   {
      this.number = number;
   }

   /**
    * The "number" is used to identify communication objects that belong
    * together.
    * 
    * When parameter visibility is changed, the communication objects would
    * change too. With this number it is possible to find the correct "other"
    * communication object that is the same as the one that was currently in
    * use.
    * 
    * @return The number of this communication object.
    */
   public int getNumber()
   {
      return number;
   }

   /**
    * @return the readEnabled
    */
   public boolean isReadEnabled()
   {
      return readEnabled;
   }

   /**
    * @param readEnabled the readEnabled to set
    */
   public void setReadEnabled(boolean readEnabled)
   {
      this.readEnabled = readEnabled;
   }

   /**
    * @return the writeEnabled
    */
   public boolean isWriteEnabled()
   {
      return writeEnabled;
   }

   /**
    * @param writeEnabled the writeEnabled to set
    */
   public void setWriteEnabled(boolean writeEnabled)
   {
      this.writeEnabled = writeEnabled;
   }

   /**
    * @return the commEnabled
    */
   public boolean isCommEnabled()
   {
      return commEnabled;
   }

   /**
    * @param commEnabled the commEnabled to set
    */
   public void setCommEnabled(boolean commEnabled)
   {
      this.commEnabled = commEnabled;
   }

   /**
    * @return the transEnabled
    */
   public boolean isTransEnabled()
   {
      return transEnabled;
   }

   /**
    * @param transEnabled the transEnabled to set
    */
   public void setTransEnabled(boolean transEnabled)
   {
      this.transEnabled = transEnabled;
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
    * The communication object is visible/available if the
    * {@link #getParameter() parameter} has this value.
    * 
    * @return the expected parameter value.
    */
   public Integer getParameterValue()
   {
      return parentParameterValue;
   }

   /**
    * Set the expected value of the parameter.
    * 
    * @param value - the expected parameter value to set
    */
   public void setParameterValue(Integer value)
   {
      this.parentParameterValue = value;
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
    * @return the object type
    */
   public ObjectType getType()
   {
      return type;
   }

   /**
    * Set the object type.
    * 
    * @param type - the object type to set.
    */
   public void setType(ObjectType type)
   {
      this.type = type;
   }

   /**
    * Get the priority of the communication object.
    *
    * @return the object priority
    */
   public ObjectPriority getPriority()
   {
      return priority;
   }

   /**
    * Set the priority of the communication object.
    * 
    * @param priority the priority to set
    */
   public void setPriority(ObjectPriority priority)
   {
      this.priority = priority;
   }

   /**
    * @return the updateEnabled
    */
   public boolean isUpdateEnabled()
   {
      return updateEnabled;
   }

   /**
    * @param updateEnabled the updateEnabled to set
    */
   public void setUpdateEnabled(boolean updateEnabled)
   {
      this.updateEnabled = updateEnabled;
   }

   /**
    * Compare two Objects by display order.
    */
   @Override
   public int compareTo(CommunicationObject o)
   {
      return displayOrder - o.displayOrder;
   }
}
