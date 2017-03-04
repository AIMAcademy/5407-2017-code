package org.usfirst.frc.team5407.robot;

//import edu.wpi.first.wpilibj.AnalogGyro;
//import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.AnalogInput;
//import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Encoder;


public class Sensors {
	
	ADXRS450_Gyro analogGyro;
	
/*	AnalogPotentiometer testPotentiometer;
	AnalogInput hallEffectSensor;
	*/
	
	AnalogInput ultrasonic;
	Encoder encX;
	Encoder encY;
	
	
	
	final double xDistance = 6*3.14159265358979/100 *130/135 * 132/125 * 132/130 * 135/143;
	final double yDistance = 6*3.14159265358979/2000 * 14/60 * 161/152 * 146/163 *145/147;
	
	double d_potStart;
	double d_potNow;
	
	double followAngle;
	
	public Sensors(int i_PWNConnector_ultrasonic){
	
		ultrasonic = new AnalogInput(i_PWNConnector_ultrasonic);
		analogGyro = new ADXRS450_Gyro();
		encX = new Encoder(0,1,false);
		encY = new Encoder(2,3,false);
		
		encX.setDistancePerPulse(xDistance);
		encY.setDistancePerPulse(yDistance);
	}
	

	
	/*public double getPotAngle(){
		this.d_potNow = this.testPotentiometer.get();
		if (this.d_potStart<=0.5){
			if (this.d_potNow < this.d_potStart+0.5){
				return 360*(this.d_potNow-this.d_potStart);
			}
			else {
				return 360*(this.d_potNow-this.d_potStart-1);
			}
		}
		else {
			if (this.d_potNow>this.d_potStart-0.5){
				return 360*(this.d_potNow-this.d_potStart);
			}
			else {
				return 360*(this.d_potNow-this.d_potStart+1); 
			}
			
		}
	}*/
	
	public double getDistance(){
		return 83 * this.ultrasonic.getAverageVoltage();
	}
	
	public void setFollowAngle(double offset){
		this.followAngle = this.analogGyro.getAngle() + offset;
	}
	
	public double getFollowAngle() {
		return this.followAngle;
	}
	
	public double getPresentAngle(){
		return this.analogGyro.getAngle();
	}
	

}
