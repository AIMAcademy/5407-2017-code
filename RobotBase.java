package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Talon;

public class RobotBase {
	
	Talon mot_ForwardMotor1;
	Talon mot_ForwardMotor2;
	Talon mot_SidewaysMotor1;
	Talon mot_SidewaysMotor2;
	
	
	
	public RobotBase(int i_PWNConnector_ForwardMotor1, 
			int i_PWNConnector_ForwardMotor2,
			int i_PWNConnector_SidewaysMotor1,
			int i_PWNConnector_SidewaysMotor2){
		mot_ForwardMotor1 = new Talon(i_PWNConnector_ForwardMotor1);
		mot_ForwardMotor1.set(0.0);
		mot_ForwardMotor2 = new Talon(i_PWNConnector_ForwardMotor2);
		mot_ForwardMotor2.set(0.0);
		mot_SidewaysMotor1 = new Talon(i_PWNConnector_SidewaysMotor1);
		mot_SidewaysMotor1.set(0.0);
		mot_SidewaysMotor2 = new Talon(i_PWNConnector_SidewaysMotor2);
		mot_SidewaysMotor2.set(0.0);
		
	}
	
	public void omniDrive(double forwardSpeed, double sidewaysSpeed, double rotate){
		
		this.mot_ForwardMotor1.set(( forwardSpeed*forwardSpeed*forwardSpeed* -1) + rotate * 0.25);
		this.mot_ForwardMotor2.set(forwardSpeed*forwardSpeed*forwardSpeed  + rotate * 0.25); 
		this.mot_SidewaysMotor1.set(sidewaysSpeed*sidewaysSpeed*sidewaysSpeed + rotate * 0.25);
		this.mot_SidewaysMotor2.set((sidewaysSpeed*sidewaysSpeed*sidewaysSpeed* -1) + rotate * 0.25);  
	}
	
	public void tankDrive(double forwardSpeed, double rotate){
		this.mot_ForwardMotor1.set((forwardSpeed*-1) + rotate);
		this.mot_ForwardMotor2.set((forwardSpeed + rotate) );
	}
	
	public void driveForward(double forwardSpeed){
		this.mot_ForwardMotor1.set(forwardSpeed);
		this.mot_ForwardMotor2.set(forwardSpeed * -1);
	}
	
	public void driveSideways(double sidewaysSpeed){
		this.mot_SidewaysMotor1.set(sidewaysSpeed);
		this.mot_SidewaysMotor2.set(sidewaysSpeed * -1);
	}
	
	public void rotate(double rotate){
		this.mot_ForwardMotor1.set(rotate);
		this.mot_ForwardMotor2.set(rotate);
		this.mot_SidewaysMotor1.set(rotate);
		this.mot_SidewaysMotor2.set(rotate);
	}
	
	
	public void stop(){
		this.mot_ForwardMotor1.set(0.0);
		this.mot_ForwardMotor2.set(0.0); 
		this.mot_SidewaysMotor1.set(0.0);
		this.mot_SidewaysMotor2.set(0.0 );  
	}
	
	
	public void driveStraight(double forwardSpeed, double sidewaysSpeed, double followAngle, double presentAngle){
		this.omniDrive(forwardSpeed, sidewaysSpeed, (followAngle-presentAngle)/10);
		
	}
	
	
	
	
/*	public void driveStraight(double speed, double gyroReading){
		this.omniDrive(-1*speed, 0, -1*(gyroReading/10));
	}
	
	public void turnToAngle(double angle, double gyroReading){
		this.omnidrive(0 , 0, (angle- gyroReading)/10)
	}
	
	public void driveAtAngle(double speed, double angle, double gyroReading){
		this.driveStraight(speed, gyroReading + angle);
	}
	
	public void faceForward(double gyroReading){
		this.omniDrive(-0, 0, -1*(gyroReading/10));
	}*/
	
}
