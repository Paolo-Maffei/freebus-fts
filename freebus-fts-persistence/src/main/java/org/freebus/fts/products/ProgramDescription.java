package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.freebus.fts.persistence.vdx.VdxEntity;
import org.freebus.fts.persistence.vdx.VdxField;

/**
 * A program description.
 */
/*
 * Note for developers: this class cannot be in a JPA @OneToMany relation with
 * CatalogEntry, as this class has a two-column key.
 */
@Entity
@Table(name = "program_description")
@VdxEntity(name = "program_description_non_vd")
public class ProgramDescription
{
   @Id
   @JoinColumn(name = "program_id", nullable = false)
   private Program program;

   @Lob
   @Column(name = "text", nullable = false)
   @VdxField(name = "text")
   private String description;

   /**
    * Create a program description object.
    */
   public ProgramDescription()
   {
   }

   /**
    * Create a program description object.
    * 
    * @param program - the program to which the program description belongs.
    * @param description - the description text.
    */
   public ProgramDescription(Program program, String description)
   {
      this.program = program;
      this.description = description;
   }

   /**
    * @return The program to which the description belongs.
    */
   public Program getProgram()
   {
      return program;
   }

   /**
    * Set the program to which the description belongs.
    * 
    * @param program - the program to set.
    */
   public void setProgram(Program program)
   {
      this.program = program;
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
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return program == null ? 0 : program.getId();
   }
}
