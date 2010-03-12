package org.freebus.fts.products.services.vdx;

import javax.persistence.EntityTransaction;

/**
 * A dummy transaction implementation.
 */
public class VdxEntityTransaction implements EntityTransaction
{
   private int active;
   private boolean rollbackOnly;

   @Override
   public void begin()
   {
      ++active;
      rollbackOnly = false;
   }

   @Override
   public void commit()
   {
      if (active < 1) throw new IllegalStateException("transaction is not active");
      --active;
   }

   @Override
   public boolean getRollbackOnly()
   {
      if (active < 1) throw new IllegalStateException("transaction is not active");
      return rollbackOnly;
   }

   @Override
   public boolean isActive()
   {
      return active > 0;
   }

   @Override
   public void rollback()
   {
      if (active < 1) throw new IllegalStateException("transaction is not active");
      --active;
   }

   @Override
   public void setRollbackOnly()
   {
      rollbackOnly = true;
   }
}
