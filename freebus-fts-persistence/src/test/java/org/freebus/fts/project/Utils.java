package org.freebus.fts.project;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.products.ParameterType;
import org.freebus.fts.products.Program;

/**
 * Utility functions for (unit) tests.
 */
final class Utils
{
   /**
    * Create an example program object with two parameters.
    * 
    * @param id - the id of the program
    * @param name - the name of the program
    * @param manufacturer - the manufacturer of the program
    *
    * @return the created program.
    */
   static Program createProgram(int id, String name, Manufacturer manufacturer)
   {
      final Program program = new Program(id, "hello-bus", manufacturer);

      Parameter param;
      ParameterType paramType;
      int paramId = id;

      param = new Parameter();
      param.setId(++paramId);
      param.setName("param-" + paramId);

      paramType = new ParameterType();
      paramType.setAtomicType(ParameterAtomicType.UNSIGNED);
      paramType.setName("param-type-" + paramId);
      paramType.setProgram(program);

      param.setParameterType(paramType);
      program.addParameter(param);

      param = new Parameter();
      param.setId(++paramId);
      param.setName("param-" + paramId);

      paramType = new ParameterType();
      paramType.setAtomicType(ParameterAtomicType.STRING);
      paramType.setName("param-type-" + paramId);
      paramType.setProgram(program);

      param.setParameterType(paramType);
      program.addParameter(param);

      return program;
   }
}
