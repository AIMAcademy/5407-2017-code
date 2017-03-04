package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Talon;

public class Climber {
	
	Talon mot_climbMotor;
	
	public Climber(int i_PWNConnector_ClimbMotor){
		mot_climbMotor = new Talon(i_PWNConnector_ClimbMotor);
		mot_climbMotor.set(0.0);
	}

}
