package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.freebus.fts.persistence.vdx.VdxField;

/*
 * Note for developers: this class cannot be in a JPA @OneToMany relation with
 * CatalogEntry, as this class has a two-column key.
 */
/**
 * Description for a product description. A better name would probably be
 * "catalog entry description", as this description depends on a {@link CatalogEntry}
 * and not on a (hardware) {@link Product}.
 */
@Entity
@Table(name = "product_description")
public class ProductDescription
{
   @Id
   @JoinColumn(name = "catalog_entry_id", nullable = false)
   private CatalogEntry catalogEntry;

   @Lob
   @Column(name = "text", nullable = false)
   @VdxField(name = "product_description_text")
   private String description;

   /**
    * Create a product description object.
    */
   public ProductDescription()
   {
   }

   /**
    * Create a product description object.
    * 
    * @param catEntry - the ID of the catalog entry to which the product description belongs.
    * @param description - the description text.
    */
   public ProductDescription(CatalogEntry catEntry, String description)
   {
      this.catalogEntry = catEntry;
      this.description = description;
   }

   /**
    * @return The catalog entry.
    */
   public CatalogEntry getCatalogEntry()
   {
      return catalogEntry;
   }

   /**
    * Set the catalog entry.
    *
    * @param catalogEntry - the catalog entry to set
    */
   public void setCatalogEntry(CatalogEntry catalogEntry)
   {
      this.catalogEntry = catalogEntry;
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
      return catalogEntry == null ? 0 : catalogEntry.getId();
   }
}
