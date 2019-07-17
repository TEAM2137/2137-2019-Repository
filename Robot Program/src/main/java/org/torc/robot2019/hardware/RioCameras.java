/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.hardware;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RioCameras extends Subsystem {

  public static enum CameraSelect {
    kFront, kRear;
  }

  private static final Size CAMERA_RESOLUTION = new Size(640, 360);

  private CameraSelect selectedCamera = CameraSelect.kFront;

  private static RioCameras SingleInstance;

  //private CameraSwitcher cSwitcherCommand;
  
  private RioCameras() {
    /*
    cSwitcherCommand = new CameraSwitcher();
    cSwitcherCommand.start();
    */

    new Thread(() -> {
      UsbCamera cameraF = new UsbCamera("CameraF", 0);
      UsbCamera cameraR = new UsbCamera("CameraR", 1);;
      cameraF.setVideoMode(VideoMode.PixelFormat.kMJPEG, (int)CAMERA_RESOLUTION.width, (int)CAMERA_RESOLUTION.height, 30);
      cameraR.setVideoMode(VideoMode.PixelFormat.kMJPEG, (int)CAMERA_RESOLUTION.width, (int)CAMERA_RESOLUTION.height, 30);
      
      cameraF.setFPS(30);
      cameraR.setFPS(30);
      cameraF.setResolution((int)CAMERA_RESOLUTION.width, (int)CAMERA_RESOLUTION.height);
      cameraR.setResolution((int)CAMERA_RESOLUTION.width, (int)CAMERA_RESOLUTION.height);
      

      CameraServer.getInstance().startAutomaticCapture(cameraF);
      CameraServer.getInstance().startAutomaticCapture(cameraR);
      
      CvSink cameraFSink = CameraServer.getInstance().getVideo(cameraF);
      CvSink cameraRSink = CameraServer.getInstance().getVideo(cameraR);

      CvSource outputStream = CameraServer.getInstance().putVideo("RioCameras", (int)CAMERA_RESOLUTION.width, (int)CAMERA_RESOLUTION.height);
      
      Mat source = new Mat();
      //Mat temp = new Mat();
      
      while(!Thread.interrupted()) {
          if (selectedCamera == CameraSelect.kFront) {
            cameraFSink.grabFrame(source);
          }
          else {
            cameraRSink.grabFrame(source);
          }

          if (!source.empty()) {
            outputStream.putFrame(source);
          }
          source.release();
      }
    }).start();

  }

  public static RioCameras GetInstance() {
    if (SingleInstance == null) {
      SingleInstance = new RioCameras();
    }
    return SingleInstance;
  }

  public CameraSelect getSelectedCamera() {
    return selectedCamera;
  }

  public void setSelectedCamera(CameraSelect _camera) {
    selectedCamera = _camera;
  }

  @Override
  public void initDefaultCommand() {
  }
}
/*
class CameraSwitcher extends CLCommand {
  
  public CameraSwitcher() {
    
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {
  }

  @Override
  protected void end() {

  }

  @Override
  protected void interrupted() {
  }
}
*/
