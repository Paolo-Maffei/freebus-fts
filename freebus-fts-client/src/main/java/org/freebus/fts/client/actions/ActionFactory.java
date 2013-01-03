package org.freebus.fts.client.actions;

import java.util.HashMap;
import java.util.Map;

import org.freebus.fts.common.exception.FtsRuntimeException;

/**
 * Factory for {@link BasicAction actions}.
 */
public class ActionFactory
{
   private static final ActionFactory INSTANCE = new ActionFactory();
   
   private final Map<String,BasicAction> actions = new HashMap<String,BasicAction>();
   private final String actionsPackageName = "org.freebus.fts.client.actions.";

   /**
    * @return The action factory instance.
    */
   public static ActionFactory getInstance()
   {
      return INSTANCE;
   }

   /**
    * Get an action object. The actions are singleton objects.
    * 
    * @param id - the id of the action.
    * @return The action object.
    */
   public BasicAction getAction(final String id)
   {
      synchronized (actions)
      {
         BasicAction action = actions.get(id);
         if (action == null)
         {
            String className = actionsPackageName + id.substring(0, 1).toUpperCase() + id.substring(1);
            Class<?> clazz = null;

            try
            {
               clazz = getClass().getClassLoader().loadClass(className);
               action = (BasicAction) clazz.newInstance();
            }
            catch (ClassNotFoundException e)
            {
               throw new FtsRuntimeException("Class not found: " + className);
            }
            catch (InstantiationException e)
            {
               throw new FtsRuntimeException("Could not instantiate class " + clazz);
            }
            catch (IllegalAccessException e)
            {
               throw new FtsRuntimeException("Could not instantiate class " + clazz);
            }

            actions.put(id, action);
         }

         return action;
      }
   }
}
