/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.commands;

import org.torc.robot2019.subsystems.*;
import org.torc.robot2019.program.KMap;
import org.torc.robot2019.program.KMap.KNumeric;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.Notifier;

import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;

public class FollowPathBackward extends Command {

  int iterations = 0;

  private EncoderFollower m_left_follower;
  private EncoderFollower m_right_follower;

  private Notifier m_follower_notifier;

  private String pathName = "";

  private BasicDriveTrain driveTrain;

  private double PID_P = KMap.GetKNumeric(KNumeric.DBL_DRIVE_TRAIN_PATHFINDER_P);
  private double PID_I = KMap.GetKNumeric(KNumeric.DBL_DRIVE_TRAIN_PATHFINDER_I);
  private double PID_D = KMap.GetKNumeric(KNumeric.DBL_DRIVE_TRAIN_PATHFINDER_D);

  private double k_max_velocity = KMap.GetKNumeric(KNumeric.DBL_DRIVE_TRAIN_PATHFINDER_MAX_VELOCITY_FPS);
  private double k_wheel_diameter = KMap.GetKNumeric(KNumeric.DBL_DRIVE_TRAIN_WHEEL_DIAMETER);
  private double k_ticks_per_rev = KMap.GetKNumeric(KNumeric.DBL_DRIVE_TRAIN_TICKS_PER_REV);

  Boolean finished = true;

  public FollowPathBackward(BasicDriveTrain drive, String nameOfPath) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    driveTrain = drive;
    requires(driveTrain);
    pathName = nameOfPath;
  }

  private void followPath() {
    if (m_left_follower.isFinished() || m_right_follower.isFinished()) {
      m_follower_notifier.stop();
      driveTrain.setPercSpeed(0, 0);
      finished = true;
    } else {
      double left_speed = m_left_follower.calculate(driveTrain.getDriveEncoder(BasicDriveTrain.DriveSide.kLeft));
      double right_speed = m_right_follower.calculate(-driveTrain.getDriveEncoder(BasicDriveTrain.DriveSide.kRight));
      double heading = -driveTrain.getGyroAngle();
      double desired_heading = Pathfinder.r2d(m_left_follower.getHeading()); // TODO: Check if inversion is needed. PF wants clockwise as positive, Gyro was counterclockwise as positive
      double heading_difference = Pathfinder.boundHalfDegrees(desired_heading - heading);
      double turn =  0.8 * (-1.0/80.0) * heading_difference;
      // double turn = 0;
      driveTrain.setPercSpeed(-(left_speed + turn), -(right_speed - turn));


      iterations++;

      System.out.println("-------------------------------");
      System.out.println("loops     " + iterations);
      // System.out.println("left      " + left_speed);
      // System.out.println("right     " + right_speed);
      System.out.println("act_head  " + heading);
      // System.out.println("des_head  " + desired_heading);
      System.out.println("dif_head  " + heading_difference);
      System.out.println("turn      " + turn);
      // System.out.println("left_pow  " + (left_speed + turn));
      // System.out.println("right_pow " + (right_speed - turn));
      System.out.println("left_enc  " + (((double)driveTrain.getDriveEncoder(BasicDriveTrain.DriveSide.kLeft))) / 3586.0 * Math.PI * 6.0 / 12.0);
      System.out.println("right_enc " + -((double) driveTrain.getDriveEncoder(BasicDriveTrain.DriveSide.kRight))  / 3586.0 * Math.PI * 6.0 / 12.0);
      // System.out.println("left_vel  " + m_left_motor.getSensorCollection().getQuadratureVelocity());
      // System.out.println("right_vel " + m_right_motor.getSensorCollection().getQuadratureVelocity());
    }
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    driveTrain.resetDriveEncoder(BasicDriveTrain.DriveSide.kLeft);
    driveTrain.resetDriveEncoder(BasicDriveTrain.DriveSide.kRight);
    driveTrain.resetGyro();

    Trajectory left_trajectory = PathfinderFRC.getTrajectory(pathName + ".left");
    Trajectory right_trajectory = PathfinderFRC.getTrajectory(pathName + ".right");

    m_left_follower = new EncoderFollower(left_trajectory);
    m_right_follower = new EncoderFollower(right_trajectory);

    m_left_follower.configureEncoder(driveTrain.getDriveEncoder(BasicDriveTrain.DriveSide.kLeft), (int) k_ticks_per_rev, k_wheel_diameter);
    // You must tune the PID values on the following line!
    m_left_follower.configurePIDVA(PID_P, PID_I, PID_D, 1 / k_max_velocity, 0);

    m_right_follower.configureEncoder(driveTrain.getDriveEncoder(BasicDriveTrain.DriveSide.kRight), (int) k_ticks_per_rev, k_wheel_diameter);
    // You must tune the PID values on the following line!
    m_right_follower.configurePIDVA(PID_P, PID_I, PID_D, 1 / k_max_velocity, 0);
    
    m_follower_notifier = new Notifier(new Runnable(){
      @Override
      public void run() {
        followPath();
      }});
    m_follower_notifier.startPeriodic(left_trajectory.get(0).dt);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return finished;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
