package org.torc.robot2019.functions;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

public class ArmManager {

    private double dblJoyStickDeadZone  = 0.1;
    private double dblMaxArmPosition    = 0;
    private double dblMinArmPosition    = 0;
    private double dblMaxPowerOutput    = 1;
    private double dblCurrentTarget     = 0;
    private double dblLastTarget        = 0;
    private double dblGearRatio         = 1;

    private CANPIDController canPIDController;
    private CANSparkMax canSparkMax;
    private ControlType controlType;

    public ArmManager(double max, double min, CANSparkMax sparkMax){
        this.dblMaxArmPosition = max;
        this.dblMinArmPosition = min;

        this.canPIDController = sparkMax.getPIDController();
        this.controlType = ControlType.kPosition;
        this.canSparkMax = sparkMax;
    }

    public ArmManager(double max, double min, CANSparkMax sparkMax, ControlType _controlType){
        this.dblMaxArmPosition = max;
        this.dblMinArmPosition = min;

        this.canPIDController = sparkMax.getPIDController();
        this.controlType = _controlType;
        this.canSparkMax = sparkMax;
    }

    /**
     * Set a target position of a PID Controller
     * 
     * @param target -- This can be degrees or inches depending on the type of Arm (Pivot or Extend)
     */
    public void setTargetPositionPID(double target){
        if(target > this.dblMaxArmPosition)
            this.dblCurrentTarget = this.dblMaxArmPosition;
        else if (target < this.dblMinArmPosition)
            this.dblCurrentTarget = this.dblMinArmPosition;
        else 
            this.dblCurrentTarget = target;
    }

    /**
     * Set a target position without a PID Contoller
     * 
     * @param target -- This can be degrees or inches depending on the type of Arm (Pivot or Extend)
     */
    // public void setTargetPositionRAW(double target){
    //     this.dblCurrentTarget = target;
    // }

    /**
     * Place this in the loop so it runs over and over again
     * This is were all of the processing is done
     */
    public void ArmManagerLoop(){
        if(this.dblCurrentTarget != this.dblLastTarget)
            this.canPIDController.setReference(this.dblCurrentTarget * this.dblGearRatio, this.controlType);

        this.dblLastTarget = this.dblCurrentTarget;
    }

    /**
     * Place this in the loop so it runs over and over again
     * This is were all of the processing is done
     * If you move the controller the PID will be overrided
     */
    public void ArmManagerLoop(double joyStickVal){
        if(joyStickVal > dblJoyStickDeadZone && joyStickVal < -dblJoyStickDeadZone)
            this.canSparkMax.set(Range.clip(joyStickVal, -this.dblMaxPowerOutput, this.dblMaxPowerOutput));

        if(this.dblCurrentTarget != this.dblLastTarget)
            this.canPIDController.setReference(this.dblCurrentTarget * this.dblGearRatio, ControlType.kPosition);

        this.dblLastTarget = this.dblCurrentTarget;
    }

    /**
     * Max and Min Speed of the Motor
     * 
     * @param max -- Max speed of the motor
     * @param min -- Min speed of the motor
     */
    public void setSpeedBounds(double max, double min){
        this.canPIDController.setOutputRange(min, max);
    }

    /**
     * This is were you set each value of varable, pass 1234 if you do not want to set one
     * 
     * @param P -- P
     * @param I -- I
     * @param D -- D
     * @param FF -- FF
     * @param IZ -- IZ
     */
    public void setPID(Double P, Double I, Double D, Double FF, Double IZ) {
        if(P != null)
            this.canPIDController.setP(P);
        if(I != null)
            this.canPIDController.setI(I);
        if(D != null)
            this.canPIDController.setD(D); 
        if(FF != null)
            this.canPIDController.setFF(FF);
        if(IZ != null)
            this.canPIDController.setIZone(IZ);       
    }

    /**
     * How many REVs to go one degree
     * 
     * @param revs -- 1:revs
     */
    public void setREVPerUnit(double revs){
        this.dblGearRatio = revs;
    }

    /**
     * Returns the motor object
     * @return -- returns motor object
     */
    public CANSparkMax getMotor(){
        return this.canSparkMax;
    }

    /**
     * This is the value where we start caring and using it as input
     * @param dead -- The value of the JoyStickDeadZone
     */
    public void setJoyStickDeadBand(double dead){
        this.dblJoyStickDeadZone = dead;
    }

    /**
     * This is the value where we start caring and using it as input
     * @return -- The value of the Dead Band
     */
    public double getJoyStickDeadBand(){
        return this.dblJoyStickDeadZone;
    }

    /**
     * The fastest the motor can go
     * @param speed -- The max speed
     */
    public void setMaxSpeed(double speed){
        if(speed > 1) this.dblMaxPowerOutput = 1;
        else this.dblMaxPowerOutput = speed;
    }

    /**
     * Get the dastest the motor can go
     * @return -- The value of Max Power
     */
    public double getMaxSpeed(){
        return this.dblMaxPowerOutput;
    }
}