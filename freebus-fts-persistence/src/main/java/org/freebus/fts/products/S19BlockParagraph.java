package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * A paragraph of a {@link S19Block S19 block}.
 */
@Entity
@Table(name = "s19_block_paragraph")
public class S19BlockParagraph
{
   @Id
   @TableGenerator(name = "S19BlockParagraph", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "S19BlockParagraph")
   @Column(name = "s19_block_paragraph_id", nullable = false)
   private int id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "block_id", nullable = false)
   private S19Block block;

   @Column(name = "pt_column_id", nullable = false)
   private int ptColumnId;

   @Column(name = "data_long")
   private int dataLong;

   @Lob
   @Column(name = "data_binary")
   private byte[] dataBinary;

   /**
    * Create a paragraph of a {@link S19Block S19 block}.
    */
   public S19BlockParagraph()
   {
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
    * @return the block
    */
   public S19Block getBlock()
   {
      return block;
   }

   /**
    * @param block the block to set
    */
   public void setBlock(S19Block block)
   {
      this.block = block;
   }

   /**
    * Get the PT (property? property type?) column id.
    * 
    * @return the ptColumnId
    */
   public int getPtColumnId()
   {
      return ptColumnId;
   }

   /**
    * @param ptColumnId the ptColumnId to set
    */
   public void setPtColumnId(int ptColumnId)
   {
      this.ptColumnId = ptColumnId;
   }

   /**
    * @return the dataLong
    */
   public int getDataLong()
   {
      return dataLong;
   }

   /**
    * @param dataLong the dataLong to set
    */
   public void setDataLong(int dataLong)
   {
      this.dataLong = dataLong;
   }

   /**
    * @return the dataBinary
    */
   public byte[] getDataBinary()
   {
      return dataBinary;
   }

   /**
    * @param dataBinary the dataBinary to set
    */
   public void setDataBinary(byte[] dataBinary)
   {
      this.dataBinary = dataBinary;
   }
}
