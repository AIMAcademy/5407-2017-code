package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class Solenoids {

	Solenoid s_DualSpeedShifter;
	Solenoid s_GearLift;
	Solenoid s_GearTilt;
	Solenoid s_GearGrab;
	Solenoid s_PassiveGear;
	Solenoid s_Light;

	Timer solTimer1;
	boolean isTiming1;
	boolean donePickUp;
	
	Timer solTimer2;
	boolean isTiming2;
	boolean doneDrag;
	
	Timer solTimer3;
	boolean isTiming3;
	boolean doneReset;
	


	public Solenoids(int i_sol0, 
			int i_sol1, 
			int i_sol2, 
			int i_sol3,
			int i_sol4,
			int i_light){
		
		s_DualSpeedShifter = new Solenoid(i_sol0);
		s_GearLift = new Solenoid(i_sol1);
		s_GearTilt = new Solenoid(i_sol2);
		s_GearGrab = new Solenoid(i_sol3);
		s_PassiveGear = new Solenoid(i_sol4);
		s_Light = new Solenoid(i_light);
		
		// used in pickUp function
		solTimer1 = new Timer();
		isTiming1 = false;
		donePickUp = true;

		// used in drag function
		solTimer2 = new Timer();
		isTiming2 = false;
		doneDrag = true;
		
		// used in reset function
		solTimer3 = new Timer();
		isTiming3 = false;
		doneReset = true;
		
		initializeSolenoids();
	}


	public void initializeSolenoids(){
		s_DualSpeedShifter.set(false);
		s_GearLift.set(false);
		s_GearTilt.set(false);
		s_GearGrab.set(true);
		s_PassiveGear.set(false);
		s_Light.set(false);
	}

	public void resetGrabber(){
		s_GearLift.set(false);
		s_GearTilt.set(false);
		s_GearGrab.set(true);
	}
	
	
	// uses a timer to trigger a series of solenoids used to pick up gears
	public void pickUp(){

		if (!donePickUp){
			
			
			if (!isTiming1){
				solTimer1.reset();
				solTimer1.start();
				isTiming1 = true;
				this.resetGrabber();
			}


			if (solTimer1.get() < 0.1){
				s_GearTilt.set(true);
			}
			else if (solTimer1.get() < 0.2){
				s_GearLift.set(true);
			}
			else if (solTimer1.get() < 0.3){
				s_GearGrab.set(false);
			}
			else if (solTimer1.get() < 0.4){
				s_GearLift.set(false);
			}
			else if (solTimer1.get() < 0.5){
				s_GearTilt.set(false);
			}
			else {
				donePickUp = true;
				isTiming1 = false;
				solTimer1.stop();
			}
		}	
	}
	
	// uses a timer to trigger a series of solenoids used to drop the grabber to drag a gear
	public void drag(){

		if (!doneDrag){
			
			
			if (!isTiming2){
				solTimer2.reset();
				solTimer2.start();
				isTiming2 = true;
				this.resetGrabber();
			}


			if (solTimer2.get() < 0.1){
				s_GearTilt.set(true);
			}
			else if (solTimer2.get() < 0.2){
				s_GearLift.set(true);
			}
			else {
				doneDrag = true;
				isTiming2 = false;
				solTimer2.stop();
			}
		}	
	}
	
	// uses a timer to trigger a series of solenoids used to reset the grabber
	public void reset(){

		if (!doneReset){
			
			
			if (!isTiming3){
				solTimer3.reset();
				solTimer3.start();
				isTiming3 = true;
			}


			if (solTimer3.get() < 0.1){
				s_GearLift.set(false);
			}
			else if (solTimer3.get() < 0.2){
				s_GearTilt.set(false);
			}
			else if (solTimer3.get() < 0.3){
				s_GearGrab.set(true);
			}
			else {
				doneReset = true;
				isTiming3 = false;
				solTimer3.stop();
			}
		}	
	}
	
	
	

}
