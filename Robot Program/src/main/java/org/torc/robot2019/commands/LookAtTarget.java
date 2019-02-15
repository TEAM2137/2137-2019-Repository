package org.torc.robot2019.commands;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

import com.ctre.phoenix.sensors.PigeonIMU;

import org.torc.robot2019.subsystems.BasicDriveTrain;
import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.tools.MathExtra;
import org.torc.robot2019.vision.VisionManager;
import org.torc.robot2019.vision.HatchTarget;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LookAtTarget extends CLCommand {

	private static final double CAMERA_FOV_DEG = 70;
	
	private double mainSpeed = 0;
	
	private double errSum = 0;
	private double dLastPos = 0;
	
	private double pGain;// = 0.0025;//0.24;//0.045;
	private double iGain;// = 0;
	private double dGain;// = 0;//1.5;//0.68;

	private PigeonIMU gyro;

	private double gyroBase = 0;

	private BasicDriveTrain driveTrain;

	private VisionManager visionManager;

	private HatchTarget targetedTarget;

	private final HatchTarget EMPTY_TARGET;

	private ArrayList<VisionEvent> vEventList;

	private double currentOffset = 0;
	
	public LookAtTarget(BasicDriveTrain _driveTrain, VisionManager _visionManager, PigeonIMU _gyro) {
		driveTrain = _driveTrain;

		visionManager = _visionManager;

		vEventList = new ArrayList<VisionEvent>();

		pGain = SmartDashboard.getNumber("kP", 0);
		iGain = SmartDashboard.getNumber("kI", 0);
		dGain = SmartDashboard.getNumber("kD", 0);

		gyro = _gyro;

		Dimension vRes = visionManager.getVisionResolution();

		EMPTY_TARGET = new HatchTarget(new Point(vRes.width / 2, vRes.height / 2), new Dimension(0, 0)); // In the middle of defining as middle of the screen (Perfect accuracy)

		targetedTarget = EMPTY_TARGET;
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		gyroBase = gyro.getFusedHeading();
		
		dLastPos = gyroBase;
	}
	
	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		
		ArrayList<HatchTarget> foundTargets = visionManager.getVisionTargets();
		
		/*
		if (foundTargets.size() <= 0) { // Don't move if nothing found
			System.out.println("No Found targets!");
			driveTrain.setPercSpeed(0, 0);
			return;
		}
		*/

		//SmartDashboard.putNumber("RelativeAngleTarget", getRelativeTargetAngle(foundTargets.get(0)));
		
		/*
		if (foundTargets.size() >= 1) {

			// If GoForTarget is true, set it false and select new target
			if (SmartDashboard.getBoolean("GoForTarget", false)) {
				SmartDashboard.putBoolean("GoForTarget", false);
				targetedTarget = foundTargets.get(0);
			}
		}
		else {
			//targetedTarget = EMPTY_TARGET; // Aim for current angle, nothing is seen
		}
		*/

		if (foundTargets.size() > 0) {
			targetedTarget = foundTargets.get(0);
		}

		double currentAngle = gyro.getFusedHeading();

		double angleTarget = currentAngle + getRelativeTargetAngle(targetedTarget);
		SmartDashboard.putNumber("AngleTarget", angleTarget);

		//System.out.println("VisionRelativeAngle: " + getRelativeTargetAngle(targetedTarget));

		double err = currentAngle - angleTarget;//targetedTarget.center.x - (432 / 2);//MathExtra.clamp(gyroVal - angleTarget, -20, 20);
		SmartDashboard.putNumber("FoundTargetError", err);
		err = MathExtra.clamp(err, -20, 20);

		// Add to error sum for Integral
		errSum += err;
		
		double offset = (pGain * err) + (errSum * iGain) + (dGain * (currentAngle - dLastPos));
		
		// Clamp offset
		/*
		double minClamp = 0.08;
		if (offset > 0) {
			offset = (offset>=minClamp)?offset:minClamp;
		}
		else {
			offset = (offset<=-minClamp)?offset:-minClamp;
		}
		*/

		/*
		double clampVal = 0.20;
		offset = MathExtra.clamp(offset, -clampVal, clampVal);
		*/

		/*
		rightSpeed = offset;
		leftSpeed = offset;//-
		*/
		dLastPos = currentAngle;

		currentOffset = offset;
		/*
		SmartDashboard.putNumber("rightSpeed", offset);
		SmartDashboard.putNumber("leftSpeed", -offset);

		driveTrain.setVelSpeed(leftSpeed, rightSpeed);
		*/

		for (VisionEvent v : vEventList) {
			v.useOffset(offset);
		}

	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		System.out.println("Finished Looking at Target!");
		driveTrain.setPercSpeed(0, 0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
	}

	public void setFinished(boolean _isFinished) {
		CLCommandDone = _isFinished;
	}

	private double getRelativeTargetAngle(HatchTarget _target) {

		int cameraWidth = visionManager.getVisionResolution().width;
		double cameraWHalf = (double)(cameraWidth / 2);

		//retVal = (double)Math.abs(_target.center.x / 2) / (double)(cameraWidth / 2);
		double mult = (double)((_target.center.x - cameraWHalf) / cameraWHalf) / 2;

		return CAMERA_FOV_DEG * mult;
	}

	public void addEventListener(VisionEvent _vEvent) {
		vEventList.add(_vEvent);
	}

	public void removeEventListener(VisionEvent _vEvent) {
		vEventList.remove(_vEvent);
	}

	public double getOffset() {
		return currentOffset;
	}
}

@FunctionalInterface
interface VisionEvent {
	public void useOffset(double _offset);
}
