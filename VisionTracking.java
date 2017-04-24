package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
//import edu.wpi.first.wpilibj.DigitalInput;

public class VisionTracking {

	
	
	
	AnalogInput xOffset;
	I2C wire;
	byte[] buffer;
	String reading;
	
	public VisionTracking(){
		wire = new I2C(Port.kOnboard, 0x51);
		buffer = new byte[32];
		reading = new String("This is a test");
	}
	
	

	public void readI2C(){
		wire.read(0x25, 32, buffer);
		reading = buffer.toString();
	}


	

	
	
}
