package org.freebus.fts.components.parametereditor;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceParameters;

/**
 * Class that converts the parameter values of a {@link Device} to/from a map of
 * {@link ParamData} objects.
 */
public final class DeviceParamData
{
   /**
    * Create {@link ParamData parameter data} objects for all parameters of the
    * given device.
    * 
    * @param device - the device to process.
    * 
    * @return a map of parameter-data objects, with the parameter as key and the
    *         parameter-data object as value.
    */
   public static Map<Parameter, ParamData> createParamData(final Device device)
   {
      final Map<Parameter, ParamData> paramDatas = new HashMap<Parameter, ParamData>();

      // Fill the set of parameter data objects
      final Program program = device.getProgram();
      final DeviceParameters devParams = device.getDeviceParameters();
      for (final Parameter param : program.getParameters())
      {
         final ParameterAtomicType atomicType = param.getParameterType().getAtomicType();
         final ParamData data = new ParamData(param);

         if (atomicType == ParameterAtomicType.STRING)
            data.setValue(devParams.getValue(param));
         else data.setValue(devParams.getIntValue(param));

         paramDatas.put(param, data);
      }

      // Resolve parent/child relationships
      for (final Parameter param : paramDatas.keySet())
      {
         final Parameter parentParam = param.getParent();
         if (parentParam == null)
            continue;

         final ParamData data = paramDatas.get(param);
         final ParamData parentData = paramDatas.get(parentParam);
         if ((parentData != null) && (data != null))
            parentData.addChild(data);
         else
         {
            Logger.getLogger(DeviceParamData.class).debug("failed to get parentData");
            continue;
         }
      }

      return paramDatas;
   }

   /**
    * Apply the values and visibility of the {@link ParamData parameter-data}
    * objects of the map to the {@link Device device}.
    * 
    * @param device - the device to apply the parameter-data values to.
    * @param paramDatas - the map of the parameter-data objects to apply to the
    *           device.
    */
   public static void applyParamData(final Device device, Map<Parameter, ParamData> paramDatas)
   {
      final Program program = device.getProgram();
      final DeviceParameters devParams = device.getDeviceParameters();

      for (final Parameter param : program.getParameters())
      {
         final ParamData data = paramDatas.get(param);

         devParams.setValue(param, data.getValue());
         devParams.setVisible(param, data.isVisible());
      }
   }

   /*
    * Disabled
    */
   private DeviceParamData()
   {
   }
}
