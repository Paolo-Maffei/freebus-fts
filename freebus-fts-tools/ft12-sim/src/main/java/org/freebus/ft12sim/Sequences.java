package org.freebus.ft12sim;

import java.util.ArrayList;

public class Sequences extends ArrayList<Sequence>
{
   private static final long serialVersionUID = 1L;

   public Sequence FindSequence(int[] Frame)
   {
      for (int i = 0; i < this.size(); i++)
      {
         if (true == this.get(i).CheckRequestFrame(Frame))
            return this.get(i);
      }
      return null;
   }

}
