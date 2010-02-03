package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.freebus.fts.persistence.vdx.VdxField;

/**
 * A line of a product description.
 */
@Entity
@Table(name = "product_description",
       uniqueConstraints = @UniqueConstraint(columnNames = { "catalog_entry_id", "display_order" }))
public class ProductDescription
{
   @Id
   @Column(name = "catalog_entry_id", columnDefinition = "INT", nullable = false)
   private int catalogEntryId;

   @Id
   @Column(name = "display_order", columnDefinition = "INT", nullable = false)
   private int displayOrder;

   @Column(name = "product_description", nullable = false)
   @VdxField(name = "product_description_text")
   private String description;

   /**
    * @return the catalogEntryId
    */
   public int getCatalogEntryId()
   {
      return catalogEntryId;
   }

   /**
    * @param catalogEntryId the catalogEntryId to set
    */
   public void setCatalogEntryId(int catalogEntryId)
   {
      this.catalogEntryId = catalogEntryId;
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
}
