/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.commands;

import java.io.OutputStream;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableInstance;

import org.torc.robot2019.program.TORCControls;
import org.torc.robot2019.program.TORCControls.ControllerInput;
import org.torc.robot2019.program.TORCControls.InputState;
import org.torc.robot2019.tools.CLCommand;

public class CameraManager extends CLCommand {

  //CameraServer server;

  UsbCamera frontCam;
  UsbCamera backCam;

  boolean isFrontCam = true;

  int frontCameraID;
  int backCameraID;

  int vertRes;
  int horzRes;

  CvSink cvSink;

  VideoSink server;

  public CameraManager(int _frontCameraID, int _backCameraID, int _horzRes, int _vertRes) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);

    frontCameraID = _frontCameraID;
    backCameraID = _backCameraID;

    horzRes = _horzRes;
    vertRes = _vertRes;



    frontCam = CameraServer.getInstance().startAutomaticCapture(frontCameraID);
    backCam = CameraServer.getInstance().startAutomaticCapture(backCameraID);

    frontCam.setResolution(horzRes, vertRes);
    backCam.setResolution(horzRes, vertRes);

    server = CameraServer.getInstance().getServer();

    cvSink = new CvSink("camera");

    cvSink.setSource(frontCam);

    cvSink.setEnabled(true);

  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    if (TORCControls.GetInput(ControllerInput.A_FrontCamera, InputState.Pressed) >= 1) {
      isFrontCam = true;
      System.out.println("FRONTCAM");
    } else if (TORCControls.GetInput(ControllerInput.A_BackCamera, InputState.Pressed) >= 1) {
      isFrontCam = false;
      System.out.println("BACKCAM");
    }

    if (isFrontCam == true){
      server.setSource(frontCam);
    } else {
      server.setSource(backCam);
    }

    CvSource outputStream = CameraServer.getInstance().putVideo("View", horzRes, vertRes);


    Mat source = new Mat();
    Mat output = new Mat();

    cvSink.grabFrame(source);
    if (!source.empty()) {
      Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
      outputStream.putFrame(output);
    }
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
