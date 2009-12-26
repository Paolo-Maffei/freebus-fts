package org.freebus.fts.products;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A communication object of a program.
 */
@Entity
@Table(name = "communication_object")
public class CommunicationObject implements Serializable
{
   private static final long serialVersionUID = 3402103922389574903L;

   @Id
   @Column(name = "object_id", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "object_name")
   private String name;
   
   @Column(name = "program_id", columnDefinition = "INT", nullable = false)
   private int programId;
   
   @Column(name = "parameter_id", columnDefinition = "INT", nullable = false)
   private int parameterId;

   @Column(name = "object_function")
   private String function;

   @Column(name = "object_readenabled", columnDefinition = "BOOLEAN", nullable = false)
   private boolean readEnabled;

   @Column(name = "object_writeenabled", columnDefinition = "BOOLEAN", nullable = false)
   private boolean writeEnabled;

   @Column(name = "object_commenabled", columnDefinition = "BOOLEAN", nullable = false)
   private boolean commEnabled;

   @Column(name = "object_transenabled", columnDefinition = "BOOLEAN", nullable = false)
   private boolean transEnabled;
   
   @Column(name = "display_order", columnDefinition = "INT")
   private int displayOrder;
   
   @Column(name = "parent_parameter_value", columnDefinition = "INT")
   private int parentParameterValue;

   @Column(name = "object_description")
   private String description;
   
   @Column(name = "object_type", columnDefinition = "SMALLINT")
   private int objectType;
   
   @Column(name = "object_priority", columnDefinition = "SMALLINT")
   private int objectPriority;

   @Column(name = "object_updateenabled", columnDefinition = "BOOLEAN", nullable = false)
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
      this.name = name;
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
    * @return the parameterId
    */
   public int getParameterId()
   {
      return parameterId;
   }

   /**
    * @param parameterId the parameterId to set
    */
   public void setParameterId(int parameterId)
   {
      this.parameterId = parameterId;
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
    * @return the objectType
    */
   public int getObjectType()
   {
      return objectType;
   }

   /**
    * @param objectType the objectType to set
    */
   public void setObjectType(int objectType)
   {
      this.objectType = objectType;
   }

   /**
    * @return the objectPriority
    */
   public int getObjectPriority()
   {
      return objectPriority;
   }

   /**
    * @param objectPriority the objectPriority to set
    */
   public void setObjectPriority(int objectPriority)
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
}
