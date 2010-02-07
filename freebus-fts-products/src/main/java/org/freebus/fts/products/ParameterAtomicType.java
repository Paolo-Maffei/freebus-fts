package org.freebus.fts.products;


/**
 * The atomic type of a parameter: none, unsigned, signed, string, enum, long enum.
 */
public enum ParameterAtomicType
{
   /**
    * No parameter type.
    */
   NONE(' ', null),

   /**
    * Unsigned integer.
    */
   UNSIGNED('+', Integer.class),

   /**
    * Signed integer.
    */
   SIGNED('-', Integer.class),

   /**
    * String.
    */
   STRING('$', String.class),

   /**
    * Enumeration.
    */
   ENUM('Y', Integer.class),

   /**
    * Long enumeration.
    */
   LONG_ENUM('Z', Integer.class);


   private final char dispAttr;
   private final Class<?> parameterClass;

   /*
    * Internal constructor.
    */
   private ParameterAtomicType(char dispAttr, Class<?> parameterClass)
   {
      this.dispAttr = dispAttr;
      this.parameterClass = parameterClass;
   }

   /**
    * @return the display-attribute character.
    */
   public char getDispAttr()
   {
      return dispAttr;
   }

   /**
    * @return the class that is used for parameters of this type.
    */
   public Class<?> getParameterClass()
   {
      return parameterClass;
   }

   /**
    * @return the object for the given ordinal.
    */
   public static ParameterAtomicType valueOf(int ordinal)
   {
      for (ParameterAtomicType o: values())
         if (o.ordinal() == ordinal)
            return o;
      return null;
   }
}
