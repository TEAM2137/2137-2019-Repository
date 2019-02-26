package org.torc.robot2019.tools;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class MotorControllers {
	public static void TalonSRXConfig(TalonSRX talon, int timeoutMs, int slotIdx, int PIDLoopIdx, double kF, double kP, double kI, double kD) {
		TalonSRXConfigFull(talon, timeoutMs, slotIdx, PIDLoopIdx, kF, kP, kI, kD);
	}
	public static void TalonSRXConfig(TalonSRX talon, int timeoutMs, int slotIdx, int PIDLoopIdx) {
		TalonSRXConfigFull(talon, timeoutMs, slotIdx, PIDLoopIdx, 0, 0, 0, 0);
	}
	public static void TalonSRXConfig(TalonSRX talon) {
		TalonSRXConfigFull(talon, 10, 0, 0, 0, 0, 0, 0);
	}
	
	private static void TalonSRXConfigFull(TalonSRX talon, int timeoutMs, int slotIdx, int PIDLoopIdx, double kF, double kP, double kI, double kD) {
		talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, PIDLoopIdx, timeoutMs);
		TalonSRXSensorZero(talon, timeoutMs, slotIdx);
		//talon.setSensorPhase(true);
		
        talon.configNominalOutputForward(0, timeoutMs);
        talon.configNominalOutputReverse(0, timeoutMs);
        talon.configPeakOutputForward(1, timeoutMs);
        talon.configPeakOutputReverse(-1, timeoutMs);

        talon.configAllowableClosedloopError(0, PIDLoopIdx, timeoutMs);
        
        talon.config_kF(PIDLoopIdx, kF, timeoutMs);
        talon.config_kP(PIDLoopIdx, kP, timeoutMs);
        talon.config_kI(PIDLoopIdx, kI, timeoutMs); 
		talon.config_kD(PIDLoopIdx, kD, timeoutMs);
		
		//talon.configPeakCurrentLimit(amps);
		//talon.configPeakCurrentDuration(milliseconds);
		//talon.configContinuousCurrentLimit(amps);
	}
	
	public static void TalonSRXSensorZero(TalonSRX talon, int timeoutMs, int PIDLoopIdx) {
		int absolutePosition = talon.getSelectedSensorPosition(timeoutMs) & 0xFFF; /* mask out the bottom12 bits, we don't care about the wrap arounds */
		/* use the low level API to set the quad encoder signal */
        talon.setSelectedSensorPosition(absolutePosition, PIDLoopIdx, timeoutMs);
	}
	
}
