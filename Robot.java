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




/**
 * _
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot {

	Timer timer;
	Sensors sensors;
	RobotBase robotBase;
	Inputs inputs;
	Solenoids solenoids;
	Shooter shooter;
	Climber climber;

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


	final String defaultAuton = "Default Auton";
	final String customAuton1 = "My Auton1";
	final String customAuton2 = "My Auton2";
	final String customAuton3 = "My Auton3";
	final String customAuton4 = "My Auton4";
	final String customAuton5 = "My Auton5";
	String autonSelected;
	SendableChooser<String> chooser;


	final String fullSpeed = "1.0";
	final String threeQuarterSpeed = "0.75";
	final String halfSpeed = "0.5";
	final String quarterSpeed = "0.25";
	double lowShootSpeed;
	SendableChooser<String> shootChooser;
	
	
	final String leftStart = "Left Start";
	final String rightStart = "Right Start";
	final String centerStart = "Center Start";
	String startSide;
	double side;
	SendableChooser<String> sideChooser;


	final double distToCPeg = 100.25;
	final double distToSPeg = 84;
	final double distToBaseline = 150;


	/**
	 * _
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */

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

		/*
		grip = new GripTest();

		table = NetworkTable.getTable("GRIP/myContoursReport");
		 */


		bp_MinDisplay = true;
		Thread visionThread;




		visionThread = new Thread(() -> {
			// Get the Axis camera from CameraServer
			AxisCamera camera = CameraServer.getInstance().addAxisCamera("10.54.7.10");


			// Set the resolution
			camera.setResolution(640, 480);


			// Get a CvSink. This will capture Mats from the camera
			CvSink cvSink = CameraServer.getInstance().getVideo();

			// Setup a CvSource. This will send images back to the Dashboard
			CvSource outputStream = CameraServer.getInstance().putVideo("Rectangle", 640, 480);

			// Mats are very memory expensive. Lets reuse this Mat.
			mat = new Mat();

			// This cannot be 'true'. The program will never exit if it is. This
			// lets the robot stop this thread when restarting robot code or
			// deploying.
			while (!Thread.interrupted()) {
				// Tell the CvSink to grab a frame from the camera and put it
				// in the source mat. If there is an error notify the output.
				if (cvSink.grabFrame(mat) == 0) {
					// Send the output the error.
					outputStream.notifyError(cvSink.getError());
					// skip the rest of the current iteration
					continue;
				}

				// Put a scope on the image

				Imgproc.circle(mat, new Point(160, 117), 100, new Scalar(255, 0, 0), 5 );
				Imgproc.line(mat, new Point(160, 30), new Point(160, 90), new Scalar(0, 255, 255), 2); //Top Line
				Imgproc.line(mat, new Point(160,150), new Point(160, 205), new Scalar(0, 255, 255), 2); //Bottom Line 
				Imgproc.line(mat, new Point(65,117), new Point(130, 117), new Scalar(0, 255, 255), 2); // Right Side Line 
				Imgproc.line(mat, new Point(190,117), new Point(255, 117), new Scalar(0, 255, 255), 2); //Left Side Line
				Imgproc.circle(mat , new Point(160, 117), 10, new Scalar(255, 0, 0), 3  ); //Center Dot

				// Give the output stream a new image to display
				outputStream.putFrame(mat);
			}
		});
		visionThread.setDaemon(true);
		visionThread.start();



		chooser = new SendableChooser<String>();
		chooser.addDefault("Default Auton", defaultAuton);
		chooser.addObject("My Auton1", customAuton1);
		chooser.addObject("My Auton2", customAuton2);
		chooser.addObject("My Auton3", customAuton3);
		chooser.addObject("My Auton4", customAuton4);
		chooser.addObject("My Auton5", customAuton5);
		SmartDashboard.putData("Auton choices", chooser);



		shootChooser = new SendableChooser<String>();
		shootChooser.addDefault("Full Speed", fullSpeed);
		shootChooser.addObject("3/4 Speed", threeQuarterSpeed);
		shootChooser.addObject("1/2 Speed", halfSpeed);
		shootChooser.addObject("1/4 Speed", quarterSpeed);
		SmartDashboard.putData("Shooter choices", shootChooser);

		
		
		sideChooser = new SendableChooser<String>();
		sideChooser.addDefault("Center Start", centerStart);
		sideChooser.addObject("Right Start", rightStart);
		sideChooser.addObject("Left Start", leftStart);
		SmartDashboard.putData("Side Choice", sideChooser);
	}

	/**
	 * _
	 * This function is run once each time the robot enters autonomous mode
	 */

	//@Override
	public void autonomousInit() {
		timer.reset();
		timer.start();
		solenoids.initializeSolenoids();


		presentXDistance = sensors.encX.getDistance();
		presentYDistance = sensors.encY.getDistance();
		presentAngle = sensors.getPresentAngle();


		autonSelected =  chooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonSelected);

		lowShootSpeed = Double.parseDouble(shootChooser.getSelected());


		//Sets initial auton conditions
		solenoids.s_GearGrab.set(false);
		autonCounter = 0;
		sensors.analogGyro.reset();
	}

	/**
	 * _
	 * This function is called periodically during autonomous
	 */

	//@Override
	public void autonomousPeriodic() {

		lowShootSpeed = Double.parseDouble(shootChooser.getSelected());
		presentXDistance = sensors.encX.getDistance();
		presentYDistance = sensors.encY.getDistance();
		presentAngle = sensors.getPresentAngle();

		autonSelected = customAuton2;
		

		if (autonSelected == defaultAuton){
			defaultAuton();
		}

		else if (autonSelected == customAuton1){
			customAuton1();
		}

		else if (autonSelected == customAuton2){
			customAuton1();
		}

		else if (autonSelected == customAuton3){
			customAuton1();
		}

		else if (autonSelected == customAuton4){
			customAuton1();
		}

		else if (autonSelected == customAuton5){
			customAuton1();
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
	/**
	 * _
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */

	//@Override
	public void teleopInit() {

		solenoids.initializeSolenoids();

		sensors.analogGyro.reset();
		sensors.setFollowAngle(0.0);
		sensors.encX.reset();
		sensors.encY.reset();
		if (sideChooser.getSelected() == leftStart){
			side = 1.0;
		}
		else if (sideChooser.getSelected() == rightStart){
			side = -1.0;
		}
		else {
			side = 0.0;
		}
	

	}

	/**
	 * _
	 * This function is called periodically during operator control
	 */

	//@Override
	public void teleopPeriodic() {
		// Reads all the inputs from the controller
		inputs.readValues();

		checkMinDisplay();	

		//grip.process(mat);



		//robotBase.omniDrive(inputs.d_RightYAxis1, inputs.d_RightXAxis1, inputs.d_LeftXAxis1);

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

		shooter.mot_BallFeed.set(inputs.d_LeftTrigger2);
		
	
		if (inputs.tapRightBumper2()){
			solenoids.s_DualSpeedShifter.set(!solenoids.s_DualSpeedShifter.get());
		}

		if (inputs.b_climbSwitch && inputs.d_LeftYAxis2 < -0.2){
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

		//SmartDashboard.putBoolean("R_MinDisplay(bool)", bp_MinDisplay);
		//SmartDashboard.putNumber("Ultrasonic ", sensors.getDistance());
		SmartDashboard.putNumber("Real Encoder X : ", sensors.encX.get());
		SmartDashboard.putNumber("Real Encoder Y : ", sensors.encY.get());
		SmartDashboard.putBoolean("CLimbSwitch", inputs.b_climbSwitch);



	}

	/**
	 * _
	 * This function is called periodically during test mode
	 */

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

	// center start, deploy gear, cross baseline
	public void customAuton1(){

		// drive forward
		if (autonCounter ==0){
			if (timer.get() < 2){
				robotBase.driveStraight(-0.5, 0, 0, presentAngle);
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drop off gear
		else if (autonCounter ==1){
			if (timer.get()<0.25){
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
			if (timer.get() < 2){
				robotBase.driveStraight(0.5, 0, 0, presentAngle);
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//  drive sideways.  Direction based on Side Choice
		else if (autonCounter ==3){
			if (timer.get() < 1){
				robotBase.driveStraight(0, side * 0.5, 0, presentAngle);
			}
			else {
				robotBase.stop();
				autonCounter++;
			}
		}

		//cross baseline
		else if (autonCounter ==4){
			if (timer.get() < 2){
				robotBase.driveStraight(1, 0, 0, presentAngle);
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
		if (timer.get() <3)
		{
			robotBase.driveStraight(1, 0, 0.0, presentAngle);
		}
		else {
			robotBase.stop();
		}
	}

	public void customAuton3(){

	}	

	public void customAuton4(){

	}	

	
	//gear + shoot + baseline
	public void customAuton5(){


		//Drive Forward and Drop gear
		if (autonCounter == 0){
			if (timer.get()<3){
				robotBase.driveStraight(-0.75, 0, 0.0, sensors.getPresentAngle()); 
			}
			else {
				solenoids.s_GearGrab.set(true);								
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}
		 
		// wait 1/4 second to make sure the gear drops
		if (autonCounter == 1){
			if (timer.get() > 0.25){
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		// drive backward
		else if (autonCounter == 2){
			if (timer.get()<1){
				robotBase.driveStraight(0.75, 0, 0.0, sensors.getPresentAngle());	//Drive Backward
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//Turn Left 90 Degrees
		else if (autonCounter == 3){
			if (timer.get()<1){
				robotBase.driveStraight(0, 0, -90 * side, sensors.getPresentAngle());	
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//Drive Forward
		else if (autonCounter == 4){
			if (timer.get()<2){
				robotBase.driveStraight(-0.75, 0, -90 * side, sensors.getPresentAngle());	
			}
			else {
				robotBase.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//Shoot balls
		else if (autonCounter == 5){
			if (timer.get()<2){
				shooter.shoot(1);												
			}
			else {
				shooter.stop();
				autonCounter++;
				timer.reset();
				timer.start();
			}
		}

		//Drive Sideways over baseline
		else if (autonCounter == 6){
			if (timer.get() < 4){
				robotBase.driveStraight(0, 0.75, -90 * side, sensors.getPresentAngle());	
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




