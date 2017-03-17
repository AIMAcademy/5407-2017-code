package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.*;
import java.awt.Image;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.cscore.AxisCamera;
import edu.wpi.cscore.CameraServerJNI;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Robot extends IterativeRobot {

	Timer timer;
	Sensors sensors;
	RobotBase robotBase;
	Inputs inputs;
	Solenoids solenoids;
	Shooter shooter;
	Climber climber;
	
	Thread visionThread;

	int autonCounter;

	boolean gotAngle;
	boolean stayStraight;
	double presentXDistance;
	double presentYDistance;
	double presentAngle;

	GripTest grip;

	NetworkTable table;
	Mat mat;


	boolean bp_MinDisplay;


	final String defaultAuton = "Just Shoot";
	final String customAuton1 = "Center Gear";
	final String customAuton2 = "Just Baseline";
	final String customAuton3 = "Right Side Gear";
	final String customAuton4 = "Left Side Gear";
	final String customAuton5 = "Right Side Gear + Shoot (RED)";
	final String customAuton6 = "Left Side Gear + Shoot (BLUE)";
	String autonSelected;
	SendableChooser<String> chooser;


	final String fullSpeed = "1.0";
	final String threeQuarterSpeed = "0.75";
	final String halfSpeed = "0.5";
	final String quarterSpeed = "0.25";
	double lowShootSpeed;
	SendableChooser<String> shootChooser;


	final double autonAngle = 1.0;
	final double autonSpeed = 0.55;

	String inHighGear;
	String grabberPosition;



	//@Override
	public void robotInit() {	
		sensors = new Sensors(0); 				//Ultrasonic
		robotBase = new RobotBase(0, 1, 2, 3); 	//These are for the 4 wheel motors
		climber = new Climber(4);				//This is for the 1 winch motor
		shooter = new Shooter(5,				//Ball Shoot
				6,								//Ball Feed
				7);  							//Low Shooter
		inputs = new Inputs(0, 1,				//This is for the 2 xBox controllers
				2);								//This is for the climb switch				
		solenoids = new Solenoids(0,  			//Dual Speed Shift
				1,  							//Gear Lift
				2,								//Gear Tilt
				3,								//Gear Grab
				4,								//Low Shooter
				5);								//Camera Light
		timer = new Timer();

		
		gotAngle = false;
		stayStraight = true;


		// SmartBoard Choosers

		chooser = new SendableChooser<String>();
		chooser.addDefault(defaultAuton, defaultAuton);
		chooser.addObject(customAuton1, customAuton1);
		chooser.addObject(customAuton2, customAuton2);
		chooser.addObject(customAuton3, customAuton3);
		chooser.addObject(customAuton4, customAuton4);
		chooser.addObject(customAuton5, customAuton5);
		chooser.addObject(customAuton6, customAuton6);
		SmartDashboard.putData("Auton choices", chooser);



		shootChooser = new SendableChooser<String>();
		shootChooser.addDefault("Full Speed", fullSpeed);
		shootChooser.addObject("3/4 Speed", threeQuarterSpeed);
		shootChooser.addObject("1/2 Speed", halfSpeed);
		shootChooser.addObject("1/4 Speed", quarterSpeed);
		SmartDashboard.putData("Shooter choices", shootChooser);
		
	}

	public void robotPeriodic(){
		autonSelected =  chooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonSelected);
	}

	//@Override
	public void autonomousInit() {
		
		//Sets initial auton conditions
		autonCounter = 0;
		
		timer.reset();
		timer.start();
		
		solenoids.initializeSolenoids();
		solenoids.s_GearGrab.set(false);

		sensors.analogGyro.reset();
		presentAngle = sensors.getPresentAngle();
		
		presentXDistance = sensors.encX.getDistance();
		presentYDistance = sensors.encY.getDistance();

		autonSelected =  chooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonSelected);

		lowShootSpeed = Double.parseDouble(shootChooser.getSelected());




		
	}



	//@Override
	public void autonomousPeriodic() {

		lowShootSpeed = Double.parseDouble(shootChooser.getSelected());
		presentXDistance = sensors.encX.getDistance();
		presentYDistance = sensors.encY.getDistance();
		presentAngle = sensors.getPresentAngle();




		if (autonSelected == defaultAuton){
			defaultAuton();
		}

		else if (autonSelected == customAuton1){
			customAuton1();
		}

		else if (autonSelected == customAuton2){
			customAuton2();
		}

		else if (autonSelected == customAuton3){
			customAuton3();
		}

		else if (autonSelected == customAuton4){
			customAuton4();
		}

		else if (autonSelected == customAuton5){
			customAuton5();
		}

		else if (autonSelected == customAuton6){
			customAuton6();
		}





		/*		
		double[] defaultValue = new double[0];
		String answer = new String("");
		double[] centerX = table.getNumberArray("centerX", defaultValue);

		for (double center:centerX){
			answer.concat(Double.toString(center));
			answer.concat(" ");
		}
		SmartDashboard.putString("Center: ", answer);
		 */


	}


	//@Override
	public void teleopInit() {


		sensors.analogGyro.reset();
		sensors.setFollowAngle(0.0);
		sensors.encX.reset();
		sensors.encY.reset();



		bp_MinDisplay = true;


	}



	//@Override
	public void teleopPeriodic() {
		// Reads all the inputs from the controller
		inputs.readValues();
		checkMinDisplay();	


		if (inputs.tapStart2()){
			stayStraight = !stayStraight;
		}

		if (stayStraight){

			if (!gotAngle){
				sensors.setFollowAngle(0.0);
				gotAngle = true;
			}
			else if (inputs.d_LeftXAxis2 > 0.2 || inputs.d_LeftXAxis2 < -0.2){
				robotBase.omniDrive(inputs.d_RightYAxis2, inputs.d_RightXAxis2, inputs.d_LeftXAxis2);
				sensors.setFollowAngle(0.0);

			}
			else {
				robotBase.driveStraight(inputs.d_RightYAxis2, inputs.d_RightXAxis2, sensors.getFollowAngle(), sensors.getPresentAngle());
			}

		}
		else{
			gotAngle = false;
			robotBase.omniDrive(inputs.d_RightYAxis2, inputs.d_RightXAxis2, inputs.d_LeftXAxis2);
		}




		shooter.mot_BallShoot.set(-1*inputs.d_RightTrigger2);
		shooter.mot_LowShooter.set(-1*inputs.d_RightTrigger2);

		shooter.mot_BallFeed.set(-1*inputs.d_LeftTrigger2);



		solenoids.s_DualSpeedShifter.set(inputs.b_RightBumper2);


		if (inputs.b_climbSwitch){
			climber.mot_climbMotor.set(inputs.d_LeftYAxis2);
		}
		else {
			climber.mot_climbMotor.set(0.0); 
		}

		if (inputs.tapA1()){
			solenoids.s_GearTilt.set(!solenoids.s_GearTilt.get());
		}
		if (inputs.tapLeftBumper1()){
			solenoids.s_GearGrab.set(!solenoids.s_GearGrab.get());
		}
		if (inputs.tapRightBumper1()){
			solenoids.s_GearLift.set(!solenoids.s_GearLift.get());
		}
		if (inputs.tapRightStick1()){
			solenoids.s_Light.set(!solenoids.s_Light.get());
		}


		if (inputs.tapX1()){
			solenoids.resetGrabber();
		}
		if (inputs.tapRightStick1()){
			solenoids.s_Light.set(!solenoids.s_Light.get());
		}

		if (inputs.tapY1()){
			solenoids.donePickUp = false;
		}
		solenoids.pickUp();

		SmartDashboard.putNumber("Real Encoder X : ", sensors.encX.get());
		SmartDashboard.putNumber("Real Encoder Y : ", sensors.encY.get());
		SmartDashboard.putBoolean("CLimbSwitch", inputs.b_climbSwitch);
		
		
		if (solenoids.s_DualSpeedShifter.get() == false){
			inHighGear = "LOW";
		}
		else {
			inHighGear = "HIGH!!!";
		}
		SmartDashboard.putString("High Gear", inHighGear);

		if (solenoids.s_GearGrab.get() == false){
			grabberPosition = "OPEN";
		}
		else {
			grabberPosition = "CLOSED!!!!";
		}
		SmartDashboard.putString("Grabber Position", grabberPosition);
		

	}


	//@Override
	public void testPeriodic() {
		LiveWindow.run();
	}


















	public void checkMinDisplay(){
		this.bp_MinDisplay = Preferences.getInstance().getBoolean("R_MinDisplay(bool)", true);	// ****  we do not zero this ****
	}




	// just shoots balls at selected speed
	public void defaultAuton(){
		if (autonSelected == defaultAuton){
			if (timer.get()<10){
				solenoids.s_LowShooter.set(true);
				shooter.shoot(lowShootSpeed);
			}
			else {
				solenoids.s_LowShooter.set(false);
				shooter.stop();
			}
		}
	}

	// Center Gear
	public void customAuton1(){

		// drive forward
		if (autonCounter ==0){
			if (timer.get() < 2.3){
				robotBase.autonDriveStraight(-0.55, 0, 1, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drop off gear
		else if (autonCounter == 1){
			if (timer.get()<0.5){
				solenoids.resetGrabber();
			}
			else {
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drive backward
		else if (autonCounter ==2){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(0.55, 0, 1, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//  drive sideways.  Direction based on Side Choice
		else if (autonCounter == 3){
			if (timer.get() < 2.5){
				robotBase.autonDriveStraight(0, 0.55, 1, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//cross baseline
		else if (autonCounter ==4){
			if (timer.get() < 2.75){
				robotBase.autonDriveStraight(-0.55, 0, 1, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}
	}

	// Just cross baseline
	public void customAuton2(){
		// drive forward
		if (autonCounter ==0){
			if (timer.get() < 3.25){
				robotBase.driveStraight(-0.42, 0, 5, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}
	}

	// Right Side Gear
	public void customAuton3(){

		
		// drive forward
		if (autonCounter ==0){
			if (timer.get() < 1.95){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//turn 60 degrees left
		if (autonCounter == 1){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(0, 0, -60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}


		// drive forward
		if (autonCounter == 2){
			if (timer.get() < 2.2){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, -60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drop off gear
		else if (autonCounter == 3){
			if (timer.get()<0.5){
				solenoids.resetGrabber();
			}
			else {
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drive backward
		else if (autonCounter == 4){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(autonSpeed, 0, -60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//  drive sideways.  Direction based on Side Choice
		else if (autonCounter == 5){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(0, 1 * autonSpeed, autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//cross baseline
		else if (autonCounter ==6){
			if (timer.get() < 1.25){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

	}	

	// Left Side Gear
	public void customAuton4(){

		
		// drive forward
		if (autonCounter ==0){
			if (timer.get() < 1.95){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//turn 60 degrees left
		if (autonCounter == 1){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(0, 0, -60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}


		// drive forward
		if (autonCounter == 2){
			if (timer.get() < 2.2){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, -60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drop off gear
		else if (autonCounter == 3){
			if (timer.get()<0.5){
				solenoids.resetGrabber();
			}
			else {
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drive backward
		else if (autonCounter == 4){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(autonSpeed, 0, -60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//  drive sideways.  Direction based on Side Choice
		else if (autonCounter == 5){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(0, 1 * autonSpeed, autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//cross baseline
		else if (autonCounter ==6){
			if (timer.get() < 1.25){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}
	}

	// Right Side Gear + Shoot (RED)
	public void customAuton5(){

		
		// drive forward
		if (autonCounter == 0){
			if (timer.get() < 1.95){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//turn 60 degrees left
		if (autonCounter == 1){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(0, 0, -60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}


		// drive forward
		if (autonCounter == 2){
			if (timer.get() < 2.2){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, -60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drop off gear
		else if (autonCounter == 3){
			if (timer.get()<0.5){
				solenoids.resetGrabber();
			}
			else {
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drive backward
		else if (autonCounter == 4){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(autonSpeed, 0, -60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// turn around
		else if (autonCounter == 5){
			if (timer.get() < 2){
				robotBase.autonDriveStraight(0, 0, -225 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}
		
		// drive to boiler
		else if (autonCounter == 6){
			if (timer.get() < 1.75){
				robotBase.autonDriveStraight(-1*autonSpeed, 0, -225 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}
		
		// Shoot balls
		else if (autonCounter == 7){
			if (timer.get() < 3){
				shooter.shoot(1.0);
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}
		
		// baseline
		if (autonCounter ==8){
			if (timer.get() <2){
				robotBase.autonDriveStraight(1, -1, -225 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

	}

	// Left Side Gear + Shoot (BLUE)
	public void customAuton6(){
		
		// drive forward
		if (autonCounter ==0){
			if (timer.get() < 1.7){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//turn 60 degrees left
		if (autonCounter == 1){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(0, 0, 60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}


		// drive forward
		if (autonCounter == 2){
			if (timer.get() < 2.0){
				robotBase.autonDriveStraight(-1 * autonSpeed, 0, 60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drop off gear
		else if (autonCounter == 3){
			if (timer.get()<0.5){
				solenoids.resetGrabber();
			}
			else {
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drive backward
		else if (autonCounter == 4){
			if (timer.get() < 1){
				robotBase.autonDriveStraight(autonSpeed, 0, 60 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}


		// turn around
		else if (autonCounter == 5){
			if (timer.get() < 2){
				robotBase.autonDriveStraight(0, 0, 225 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}
		
		// drive to boiler
		else if (autonCounter == 6){
			if (timer.get() < 1.75){
				robotBase.autonDriveStraight(-1*autonSpeed, 0, 225 + autonAngle, sensors.getPresentAngle());
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}
		
		// Shoot balls
		else if (autonCounter == 7){
			if (timer.get() < 3){
				shooter.shoot(1.0);
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

	}


}



