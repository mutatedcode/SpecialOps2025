// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot; // Hey, this is an FRC Program!

// I DELETED WPILIB NEW COMMANDS FROM VENDOR LIBRARIES!

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser; // Let us use sendable chooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; // Let us use smart dashboard

// we added the imports below this comment

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig; // Create new config file
import com.revrobotics.spark.SparkLowLevel.MotorType; // Force it to make sure it knows that it is a brush motor type
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer; // Knows how long to run

import edu.wpi.first.wpilibj.XboxController;
/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCenterCoral = "Center and Coral"; // display name
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>(); // Let's create a new sendable chooser

  // private means that it can only be accessed within the class TimedRobot
  // final means it won't change, you can't change the variable value
  private final SparkMax leftLeader = new SparkMax( /** CAN ID */  18, MotorType.kBrushed);
  private final SparkMax leftFollower = new SparkMax(/** CAN ID */ 19, MotorType.kBrushed);
  private final SparkMax rightLeader = new SparkMax( /** CAN ID */ 14, MotorType.kBrushed);
  private final SparkMax rightFollower = new SparkMax( /**  CAN ID */ 15, MotorType.kBrushed);

  private final SparkMax rollerMotor = new SparkMax(10, MotorType.kBrushed);
  // 5th CAN ID is port 10!
  
  private final DifferentialDrive myDrive = new DifferentialDrive(leftLeader, rightLeader);
  
  private final SparkMaxConfig driveConfig = new SparkMaxConfig();
  private final SparkMaxConfig rollerConfig = new SparkMaxConfig();

  private final Timer timer1 = new Timer();
  
  private final double ROLLER_EJECT_VALUE = .44;
  private double DriveSpeed = 1;
  
  private final XboxController gamepad0 = new XboxController(0);
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("Center and Coral", kCenterCoral);
    SmartDashboard.putData("Auto choices", m_chooser);

    // If you use too much current, the breaker will trip, and stop for a short period of time
    // If you pop the 120 amp breaker, the breaker will not reset and that will be the end of your match
    driveConfig.smartCurrentLimit(60);
    driveConfig.voltageCompensation(12);
    // If we do not to volt compensation, it will send half of the battery
    // Makes auton smoother

    driveConfig.follow(rightLeader);
    // If the robot is reset, it will go back to safe parameters
    leftFollower.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    // Persist Mode: The things that we asked it to save, it will save

    driveConfig.follow(leftLeader);
    rightFollower.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    driveConfig.disableFollowerMode();
    rightLeader.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    
    driveConfig.inverted(true);
    leftLeader.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    rollerConfig.smartCurrentLimit(60);
    rollerConfig.voltageCompensation(10);
    rollerMotor.configure(rollerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

     timer1.start();
  } // Runs only one time

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {} // Runs over and over again regardless of the mode

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    timer1.restart();
  } // first 15 seconds

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCenterCoral:
        // Put custom auto code here
        // stop and deposit the coral
        // stop everything!!!

        if (timer1.get() < 1.85) { // drive to reef
          myDrive.tankDrive(.5, .5);
        }
        else if (timer1.get() < 3) { // spit out coral
          myDrive.tankDrive(0, 0);
        }
        else if (timer1.get() < 3.85) { // spit out coral
          myDrive.tankDrive(0, 0);
          rollerMotor.set(ROLLER_EJECT_VALUE);
        }

        else {
          myDrive.tankDrive(0, 0);
          rollerMotor.set(0);
        }
          break;
        case kDefaultAuto:
        default:

          break;
        }     
 
    }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    if(gamepad0.getLeftBumperButton()) {
      DriveSpeed = .5;
    }
    if(gamepad0.getRightBumperButton()) {
      DriveSpeed = 1;
    }
    // Tank Drive
    // myDrive.tankDrive(-gamepad0.getLeftY(), -gamepad0.getRightY());

    // Arcade Drive
    myDrive.arcadeDrive(-gamepad0.getLeftY() * DriveSpeed, -gamepad0.getRightX() * DriveSpeed);

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
