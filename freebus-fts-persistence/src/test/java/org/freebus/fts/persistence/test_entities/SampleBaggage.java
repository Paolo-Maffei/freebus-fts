package org.freebus.fts.persistence.test_entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.freebus.fts.persistence.vdx.VdxEntity;
import org.freebus.fts.persistence.vdx.VdxField;

/**
 * A test entity. Used for unit-tests only!
 */
@Entity
@VdxEntity(name = "ApplicationProgramBaggage")
public class SampleBaggage
{
   @Id
   @Column(name = "id")
   public double id;

   @VdxField(name = "ApplicationProgramID")
   @Column(name = "ApplicationID", nullable = false)
   public String appId;

   @Column(name = "Language_ID", nullable = false)
   public int langId;

   @Column(name = "any_key", nullable = false)
   public int anyKey;

   @Column(name = "active", nullable = false)
   public boolean active;

   @Column(name = "factor")
   public float factor;
}