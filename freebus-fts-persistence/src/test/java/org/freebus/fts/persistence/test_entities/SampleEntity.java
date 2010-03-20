package org.freebus.fts.persistence.test_entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.persistence.db.TestCustomSequencesTable;

/**
 * Used in {@link TestCustomSequencesTable}
 */
@Entity
@Table(name = "sample_entity")
public class SampleEntity
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 1, table = "sequence",  name = "GenSampleEntityId",
         pkColumnName = "seq_name", valueColumnName = "seq_count")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "project_id", nullable = false)
   public int id;

   public String name;
}
