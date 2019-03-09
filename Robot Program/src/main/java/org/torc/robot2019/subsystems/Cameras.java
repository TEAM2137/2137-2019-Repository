/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.subsystems;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Cameras extends Subsystem {

  public static enum CameraSelect {
    kFront, kRear;
  }

  private CameraSelect selectedCamera = CameraSelect.kFront;

  //private CameraSwitcher cSwitcherCommand;
  
  public Cameras() {
    /*
    cSwitcherCommand = new CameraSwitcher();
    cSwitcherCommand.start();
    */

    new Thread(() -> {
      UsbCamera cameraF = new UsbCamera("CameraF", 0);
      UsbCamera cameraR = new UsbCamera("CameraR", 1);;
      cameraF.setVideoMode(VideoMode.PixelFormat.kYUYV, 640, 360, 30);
      cameraR.setVideoMode(VideoMode.PixelFormat.kYUYV, 640, 360, 30);
      
      cameraF.setFPS(30);
      cameraR.setFPS(30);
      cameraF.setResolution(640, 480);
      cameraR.setResolution(640, 480);
      

      CameraServer.getInstance().startAutomaticCapture(cameraF);
      CameraServer.getInstance().startAutomaticCapture(cameraR);
      
      CvSink cameraFSink = CameraServer.getInstance().getVideo(cameraF);
      CvSink cameraRSink = CameraServer.getInstance().getVideo(cameraR);

      CvSource outputStream = CameraServer.getInstance().putVideo("BWCam", 160, 120);
      
      Mat source = new Mat();
      //Mat temp = new Mat();
      Mat output = new Mat();
      
      while(!Thread.interrupted()) {
          if (selectedCamera == CameraSelect.kFront) {
            cameraFSink.grabFrame(source);
          }
          else {
            cameraRSink.grabFrame(source);
          }

          if (!source.empty()) {
            if (source.size().width > 160 || source.size().height > 120) {
              Imgproc.resize(source, source, new Size(160, 120));
            }
            Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
            outputStream.putFrame(output);
          }
      }
    }).start();

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
