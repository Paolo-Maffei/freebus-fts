package org.freebus.fts.products;

/**
 * Details about a parameter of a program.
 */
public class ParameterType
{
   private int id;
   private int atomicTypeNumber;
   private int programId;
   private String name;
   private Object minValue = null;
   private Object maxValue = null;
   private int lowAccess, highAccess;
   private int size;
}
