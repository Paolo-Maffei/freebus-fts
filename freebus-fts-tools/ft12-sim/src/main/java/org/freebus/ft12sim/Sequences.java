package org.freebus.ft12sim;

import java.util.ArrayList;

public class Sequences extends ArrayList<Sequence> {

	public Sequence FindSequence(int[] Frame) {
		for (int i = 0; i < this.size(); i++) {
			if (true == this.get(i).CheckRequestFrame(Frame))
				return this.get(i);
		}
		return null;
	}

}
