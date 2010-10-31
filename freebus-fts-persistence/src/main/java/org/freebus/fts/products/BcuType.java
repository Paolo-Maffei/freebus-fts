package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The BCU type
 */
@Entity
@Table(name = "bcu_type")
public class BcuType
{
   @Id
   @Column(name = "bcu_type_number", nullable = false)
   private int id;

   @Column(name = "bcu_type_name", nullable = false)
   private String name;

   @Column(name = "bcu_type_cpu")
   private String cpuName;

   /**
    * Create a BCU-type object.
    */
   public BcuType()
   {
   }

   /**
    * Create a BCU-type object.
    *
    * @param id - the id.
    * @param name - the name of the BCU type.
    * @param cpuName - the name of the CPU.
    */
   public BcuType(int id, String name, String cpuName)
   {
      this.id = id;
      this.name = name;
      this.cpuName = cpuName;
   }

   /**
    * @return the ID of the BCU type
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the ID of the BCU type.
    *
    * @param id - the id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return The name of the BCU type.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name of the BCU type.
    *
    * @param name - the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return The name of the CPU. May be null.
    */
   public String getCpuName()
   {
      return cpuName;
   }

   /**
    * Set the name of the CPU.
    *
    * @param cpuName - the CPU name to set (null allowed).
    */
   public void setCpuName(String cpuName)
   {
      this.cpuName = cpuName;
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
      if (!(o instanceof Mask))
         return false;
      final BcuType oo = (BcuType) o;
      return id == oo.id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return name;
   }
}
