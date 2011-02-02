package org.freebus.fts.client.workbench;

/**
 * A key object that identifies an editor with an object. This is an internal
 * class of the {@link WorkBench}.
 */
final class EditorKey
{
   private final Class<? extends WorkBenchEditor> clazz;
   private final Object obj;
   private final int hashCode;

   /**
    * Create an editor-key object.
    * 
    * @param clazz - the class of the editor
    * @param obj - the edited object
    */
   EditorKey(Class<? extends WorkBenchEditor> clazz, Object obj)
   {
      this.clazz = clazz;
      this.obj = obj;

      hashCode = (clazz.hashCode() << 16) | (obj == null ? 0 : obj.hashCode());
   }

   /**
    * @return The class of the editor.
    */
   public Class<? extends WorkBenchEditor> getClazz()
   {
      return clazz;
   }

   /**
    * @return The edited object.
    */
   public Object getObj()
   {
      return obj;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return hashCode;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;
      if (!(this instanceof EditorKey))
         return false;

      final EditorKey oo = (EditorKey) o;

      if (clazz != oo.clazz)
         return false;

      if (obj == null)
         return oo.obj == null;
      
      return obj.equals(oo.obj);
   }
}
