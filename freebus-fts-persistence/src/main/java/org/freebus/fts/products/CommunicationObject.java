package org.freebus.fts.products;

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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.log4j.Logger;

/**
 * A communication object of a program.
 */
@Entity
@Table(name = "communication_object")
public class CommunicationObject
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequence",  name = "GenCommunicationObjectId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "object_id", nullable = false)
   private int id;

   @Column(name = "object_name", nullable = false)
   private String name = "";

   @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "program_id", nullable = false)
   private Program program;

   @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "parameter_id", nullable = true)
   private Parameter parameter;

   @Column(name = "object_function")
   private String function;

   @Column(name = "object_readenabled", nullable = false)
   private boolean readEnabled;

   @Column(name = "object_writeenabled", nullable = false)
   private boolean writeEnabled;

   @Column(name = "object_commenabled", nullable = false)
   private boolean commEnabled;

   @Column(name = "object_transenabled", nullable = false)
   private boolean transEnabled;

   @Column(name = "display_order")
   private int displayOrder;

   @Column(name = "parent_parameter_value")
   private int parentParameterValue;

   @Column(name = "object_description")
   private String description;

   @Enumerated(EnumType.ORDINAL)
   @Column(name = "object_type", nullable = false)
   private ObjectType objectType;

   @Enumerated(EnumType.ORDINAL)
   @Column(name = "object_priority", nullable = false)
   private ObjectPriority objectPriority;

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
    * @return the parentParameterValue
    */
   public int getParentParameterValue()
   {
      return parentParameterValue;
   }

   /**
    * @param parentParameterValue the parentParameterValue to set
    */
   public void setParentParameterValue(int parentParameterValue)
   {
      this.parentParameterValue = parentParameterValue;
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
   public ObjectType getObjectType()
   {
      return objectType;
   }

   /**
    * Set the object type
    */
   public void setObjectType(ObjectType objectType)
   {
      this.objectType = objectType;
   }

   /**
    * @return the objectPriority
    */
   public ObjectPriority getObjectPriority()
   {
      return objectPriority;
   }

   /**
    * @param objectPriority the objectPriority to set
    */
   public void setObjectPriority(ObjectPriority objectPriority)
   {
      this.objectPriority = objectPriority;
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
    * Prepare the value for persisting.
    */
   @PrePersist
   protected void prePersist()
   {
      Logger.getLogger(getClass()).info("CommunicationObject.prePersist: objectType=" + objectType);
   }
}
