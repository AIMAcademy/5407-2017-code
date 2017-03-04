package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Talon;

public class Shooter {
	
	Talon mot_BallFeed;
	Talon mot_BallShoot;
	
	public Shooter(int i_PWNConnector_BallShoot,
			int i_PWNConnector_BallFeed){
		

		mot_BallShoot = new Talon(i_PWNConnector_BallShoot);
		mot_BallShoot.set(0.0);
		mot_BallFeed = new Talon(i_PWNConnector_BallFeed);
		mot_BallFeed.set(0.0);
	}
	

	
	public void shoot(){
		this.mot_BallFeed.set(1);
		this.mot_BallShoot.set(-1);
	}
	
	public void stop(){
		this.mot_BallFeed.set(0);
		this.mot_BallShoot.set(0);
	}

}
