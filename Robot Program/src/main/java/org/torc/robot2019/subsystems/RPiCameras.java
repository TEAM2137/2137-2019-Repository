/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.torc.robot2019.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Add your docs here.
 */
public class RPiCameras extends Subsystem {

  public static enum CameraSelect {
    kFront, kRear;
  }
  
  private static NetworkTableInstance NtInstance = NetworkTableInstance.getDefault();

  private static NetworkTable NtTable = NtInstance.getTable("dataTable");

  private static NetworkTableEntry NtCameraSelect = NtTable.getEntry("CameraSelect");

  public static void setSelectedCamera(CameraSelect _cameraSelect) {
    switch(_cameraSelect) {
      case kFront:
        NtCameraSelect.setString("front");
        break;
      case kRear:
        NtCameraSelect.setString("rear");
        break;
    }
  }

  public static CameraSelect getSelectedCamera() {
    if (NtCameraSelect.getString("front").toLowerCase().contains("rear")) {
      return CameraSelect.kRear;
    }
    else {
      return CameraSelect.kFront;
    }
  }

  @Override
  public void initDefaultCommand() {
  }
}
