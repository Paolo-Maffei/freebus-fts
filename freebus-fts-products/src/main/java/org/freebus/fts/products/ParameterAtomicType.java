package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The atomic type of a parameter: none, unsigned, signed, string, enum, long enum.
 */
@Entity
@Table(name = "parameter_atomic_type")
public class ParameterAtomicType
{
   @Id
   @Column(name = "atomic_type_number", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "atomic_type_name", unique = true, nullable = false)
   private String name;

   @Column(name = "dispattr", columnDefinition = "CHAR")
   private String dispAttr;

   /**
    * @return the atomic type number.
    */
   public int getId()
   {
      return id;
   }

   /**
    * Set the atomic type number.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the atomic type name.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the atomic type name.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the display-attribute character (e.g. '+', '$', 'Z').
    */
   public char getDispAttr()
   {
      return dispAttr.charAt(0);
   }

   /**
    * Set the display-attribute character (e.g. '+', '$', 'Z').
    */
   public void setDispAttr(char dispAttr)
   {
      this.dispAttr = Character.toString(dispAttr);
   }
}
