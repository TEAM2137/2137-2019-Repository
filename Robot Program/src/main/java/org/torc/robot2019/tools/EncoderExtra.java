package org.torc.robot2019.tools;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DriverStation;

public class EncoderExtra {

	Encoder targetEnc;

	boolean tracking = false;

	//Timer t;

	// In ms
	float checkRate = 500;

	public EncoderExtra(Encoder enc) {
		targetEnc = enc;
	}

	/*
	 * Returns the traveled distance of an encoder based on it's angle and wheel size
	 */
	public static float EncoderDistance(float diameter, double angle) {
		// Diameter * PI = Circumference
		// Circumference * Revolutions = Distance
		return (float) ((diameter * Math.PI) * (angle / 360));
	}
	
	public void startTracking() {
		tracking = true;
		/*
		t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				trackRate();
			}
		}, 0, (long) checkRate);
		*/
	}

	void trackRate() {
		DriverStation.reportWarning("TRACKING!!!", false);
		
	}

	/*
	 * public float getRPM() { if (!tracking) {
	 * DriverStation.reportWarning("EncoderExtra must be tracking to get RPM!!",
	 * true); return 0F; }
	 * 
	 * }
	 */

}
