package org.torc.robot2019.commands;

import java.util.ArrayList;

import org.torc.robot2019.robot.InheritedPeriodic;
import org.torc.robot2019.robot.Robot;
import org.torc.robot2019.tools.CLCommand;
import org.torc.robot2019.tools.LimelightControl;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionCorrector extends Subsystem {

	protected ArrayList<VisionEvent> vEventList;
	// The PID calculated offset to be used after calculation.
	protected double currentOffset = 0;

	private VisionPIDCommand pidCommand;
	
	public VisionCorrector() {
		vEventList = new ArrayList<VisionEvent>();

		pidCommand = new VisionPIDCommand(this);
		pidCommand.start();
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

	@Override
	protected void initDefaultCommand() {

	}
}

@FunctionalInterface
interface VisionEvent {
	public void useOffset(double _offset);
}

class VisionPIDCommand extends CLCommand {

	private double errSum = 0;
	private double dLastPos = 0;
	
	private double pGain = 0.01;// = 0.0025;//0.24;//0.045;
	private double iGain = 0;// = 0;
	private double dGain = 0;// = 0;//1.5;//0.68;

	protected VisionCorrector correctorSubsystem;

	protected VisionPIDCommand(VisionCorrector _correctorSubsystem) {
		this.setRunWhenDisabled(true);
		correctorSubsystem = _correctorSubsystem;
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	public void execute() {

		double currentAngle = LimelightControl.getTx();

		double err = currentAngle;//targetedTarget.center.x - (432 / 2);//MathExtra.clamp(gyroVal - angleTarget, -20, 20);

		SmartDashboard.putNumber("FoundTargetError", err);

		// Add to error sum for Integral
		errSum += err;
		
		double offset = (pGain * err) + (errSum * iGain) + (dGain * (currentAngle - dLastPos));
		
		dLastPos = currentAngle;

		// Set currentOffset
		correctorSubsystem.currentOffset = offset;

		for (VisionEvent v : correctorSubsystem.vEventList) {
			v.useOffset(offset);
		}

	}
}
