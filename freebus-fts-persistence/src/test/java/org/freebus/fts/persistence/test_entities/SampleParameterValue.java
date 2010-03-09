package org.freebus.fts.persistence.test_entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.persistence.vdx.VdxField;

/**
 * Values of a parameter type.
 */
@Entity
@Table(name = "parameter_list_of_values")
public class SampleParameterValue implements Serializable
{
   public static final long serialVersionUID = -1L;

   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequences", name = "GenParameterValueId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "parameter_value_id", nullable = false)
   public int id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "parameter_type_id", nullable = false, referencedColumnName = "parameter_type_id")
   public int parameterType;

   @Column(name = "displayed_value")
   public String displayedValue;

   @VdxField(name = "display_order")
   @Column(name = "display_order")
   public int displayOrder;

   @Column(name = "real_value")
   public int intValue;

   @Column(name = "binary_value")
   public String binaryValue;

   @Column(name = "double_value")
   public double doubleValue;
}
