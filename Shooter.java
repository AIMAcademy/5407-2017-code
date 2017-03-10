package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Talon;

public class Shooter {
	
	Talon mot_BallFeed;
	Talon mot_BallShoot;
	Talon mot_LowShooter;
	
	public Shooter(int i_PWNConnector_BallShoot,
			int i_PWNConnector_BallFeed,
			int i_PWNConnector_LowShooter){
		

		mot_BallShoot = new Talon(i_PWNConnector_BallShoot);
		mot_BallShoot.set(0.0);
		mot_BallFeed = new Talon(i_PWNConnector_BallFeed);
		mot_BallFeed.set(0.0);
		mot_LowShooter = new Talon(i_PWNConnector_LowShooter);
	}
	

	
	public void shoot(double speed){
		this.mot_BallFeed.set(speed);
		this.mot_BallShoot.set(-1*speed);
		this.mot_LowShooter.set(-speed);
	}
	
	public void stop(){
		this.mot_BallFeed.set(0);
		this.mot_BallShoot.set(0);
		this.mot_LowShooter.set(0);
	}
	
	public void shootLow(double speed){
		this.mot_BallFeed.set(1);
		this.mot_LowShooter.set(speed);
	}

}
