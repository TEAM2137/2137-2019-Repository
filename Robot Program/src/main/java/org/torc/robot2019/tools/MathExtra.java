package org.torc.robot2019.tools;

public class MathExtra {

	public static float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}

	public static double clamp(double val, double min, double max) {
	    return Math.max(min, Math.min(max, val));
	}

	public static int clamp(int val, int min, int max) {
	    return Math.max(min, Math.min(max, val));
	}

	public static double applyDeadband(double value, double deadband) {
		if (Math.abs(value) > deadband) {
			if (value > 0.0) {
				return (value - deadband) / (1.0 - deadband);
			} 
			else {
				return (value + deadband) / (1.0 - deadband);
			}
		} 
		else {
			  	return 0.0;
		}
	}
	
	public static double lerp(double var1, double var2, double t) {
		t = clamp(t, -1, 1);
		return (1 - t) * var1 + t * var2;
	}

	public static float lerp(float var1, float var2, float t) {
		t = clamp(t, -1, 1);
		return (1 - t) * var1 + t * var2;
	}
	
	public static int lerp(int var1, int var2, double t) {
		t = clamp(t, -1, 1);
		return (int) ((1 - t) * var1 + t * var2);
	}
}
