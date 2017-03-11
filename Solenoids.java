package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class Solenoids {

	Solenoid s_DualSpeedShifter;
	Solenoid s_GearLift;
	Solenoid s_GearTilt;
	Solenoid s_GearGrab;
	Solenoid s_LowShooter;
	Solenoid s_Light;

	Timer solTimer;

	boolean isTiming;
	boolean donePickUp;


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
		s_LowShooter = new Solenoid(i_sol4);
		s_Light = new Solenoid(i_light);

		solTimer = new Timer();

		isTiming = false;
		donePickUp = true;

		initializeSolenoids();
	}


	public void initializeSolenoids(){
		s_DualSpeedShifter.set(false);
		s_GearLift.set(false);
		s_GearTilt.set(false);
		s_GearGrab.set(true);
		s_LowShooter.set(false);
		s_Light.set(false);
	}

	public void resetGrabber(){
		s_GearLift.set(false);
		s_GearTilt.set(false);
		s_GearGrab.set(true);
	}

	public void pickUp(){

		if (!donePickUp){
			
			
			if (!isTiming){
				solTimer.reset();
				solTimer.start();
				isTiming = true;
				this.resetGrabber();
			}


			if (solTimer.get() < 0.1){
				s_GearTilt.set(true);
			}
			else if (solTimer.get() < 0.2){
				s_GearLift.set(true);
			}
			else if (solTimer.get() < 0.3){
				s_GearGrab.set(false);
			}
			else if (solTimer.get() < 0.4){
				s_GearLift.set(false);
			}
			else if (solTimer.get() < 0.5){
				s_GearTilt.set(false);
			}
			else {
				donePickUp = true;
				isTiming = false;
				solTimer.stop();
			}
		}	
	}

}
