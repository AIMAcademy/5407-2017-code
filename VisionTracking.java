package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.AnalogInput;

public class VisionTracking {

	
	AnalogInput xOffset;
	
	
	public VisionTracking(int i_PWNConnector_VisionTracking){
		xOffset = new AnalogInput(i_PWNConnector_VisionTracking);
	}
	
	public double getXOffset(){
		return -1*xOffset.getAverageVoltage();
	}
	
	
	
}
