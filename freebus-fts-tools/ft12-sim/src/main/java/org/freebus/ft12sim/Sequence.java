package org.freebus.ft12sim;


public class Sequence {
	public int[] getResciveFrame() {
		return ResciveFrame;
	}


	public void setResciveFrame(int[] resciveFrame) {
		ResciveFrame = resciveFrame;
	}


	public void setTransmitFrames(int[][] transmitFrames) {
		TransmitFrames = transmitFrames;
	}


	public void setDiscription(String discription) {
		Discription = discription;
	}
	int[] ResciveFrame;
	int[][]TransmitFrames;
	String Discription;

	
	public boolean CheckRequestFrame(int[] Farme){
		return this.ResciveFrame.equals(Farme);
	}
	
	public int[][] getTransmitFrames(){
		return this.TransmitFrames;
	}
	public String getDiscription(){
	return this.Discription;
	}
}
