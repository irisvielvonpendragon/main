package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.*;
import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveBase extends Subsystem {

    private static volatile DriveBase instance;

    private TorqueMotor leftFore;
    private TorqueMotor leftMid;
	private TorqueMotor leftRear;
    private TorqueMotor rightFore;
    private TorqueMotor rightMid;
    private TorqueMotor rightRear;
    private DoubleSolenoid gearShift;
    private Relay lightRing;
    
    private final ScheduledPID visionPID;
    private double leftSpeed = 0.0;
    private double rightSpeed = 0.0;
    private boolean highGear = false;
    
    private boolean clockwise = true;

    private DriveBase() {
        leftFore = new TorqueMotor(new VictorSP(Ports.DB_LEFT_FORE_MOTOR), !clockwise);
        leftMid = new TorqueMotor(new VictorSP(Ports.DB_LEFT_MID_MOTOR), !clockwise);
        leftRear = new TorqueMotor(new VictorSP(Ports.DB_LEFT_REAR_MOTOR), !clockwise);
        
        rightFore = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_FORE_MOTOR), clockwise);
        rightMid = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_MID_MOTOR), clockwise);
        rightRear = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_REAR_MOTOR), clockwise);
        
        gearShift = new DoubleSolenoid(0, Ports.DB_SOLE_A, Ports.DB_SOLE_B);

        lightRing = new Relay(Ports.LR_RELAY);

        visionPID = new ScheduledPID.Builder(0, 0.5, 1)
                .setPGains(0.5 / Constants.CAMERA_ANGLE_X)
                .build();
    }

    @Override
    public void autoInit() {
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void teleopInit() {
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void disabledInit() {
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void run(RobotState state) {
        if (state == RobotState.AUTO) {
            lightRing.set(Relay.Value.kForward);

            leftSpeed = input.getDBLeftSpeed();
            rightSpeed = input.getDBRightSpeed();
        }

        else if (state == RobotState.TELEOP || state == RobotState.DB_ONLY) {
            lightRing.set(Relay.Value.kOff);

            leftSpeed = input.getDBLeftSpeed();
            rightSpeed = input.getDBRightSpeed();

            if (input.calcLFSetpoint() > 4.0) {
                leftSpeed *= 0.7;
                rightSpeed *= 0.7;
            }
            // else if (input.calcLFSetpoint() > 2.0) {
            //     leftSpeed *= 1;
            //     rightSpeed *= 1;
            // }
        }

        else if (state == RobotState.VISION) {
            lightRing.set(Relay.Value.kForward);

            double currentOffset = feedback.getNTTargetOffset();
            double adjustment = visionPID.calculate(currentOffset);

            leftSpeed = 0.5 * input.getDBLeftSpeed() - adjustment;
            rightSpeed = 0.5 * input.getDBRightSpeed() + adjustment;

            // leftSpeed = input.getDBLeftSpeed() * (0.5 - adjustment);
            // rightSpeed = input.getDBRightSpeed() * (adjustment + 0.5);
        }

        else if (state == RobotState.LINE) {
            // leftSpeed = 0.5 * input.getDBLeftSpeed();
            // rightSpeed = 0.5 * input.getDBRightSpeed();

            if (feedback.getLNLeft() && !feedback.getLNRight()) {
                // leftSpeed -= 0.4;
                // rightSpeed += 0.4;
                leftSpeed = 0;
                rightSpeed = 0.3;
            }
            else if (!feedback.getLNLeft() && feedback.getLNRight()) {
                // leftSpeed += 0.4;
                // rightSpeed -= 0.4;
                leftSpeed = 0.3;
                rightSpeed = 0;
            }
            else {
                leftSpeed = 0.15;
                rightSpeed = 0.15;
            }
            // Good work Jacob
        }
        
        setGears(state);
        output();
    }

    @Override
    protected void output() {
        if (highGear) {
            gearShift.set(Value.kForward);
        } else {
            gearShift.set(Value.kReverse);
        }
        
        leftFore.set(leftSpeed);
        leftMid.set(leftSpeed);
        leftRear.set(leftSpeed);
        
		rightFore.set(rightSpeed);
		rightMid.set(rightSpeed);
        rightRear.set(rightSpeed);
    }

    public boolean getHighGear() {
        return highGear;
    }

    // Potential auto gear shift?
    private void setGears(RobotState state) {
        if (state == RobotState.AUTO || state == RobotState.TELEOP) {
            highGear = input.getDBHighGear();
        }
        else {
            highGear = false;
        }
    }

    @Override
    public void disabledContinuous() {}

    @Override
    public void autoContinuous() {}

    @Override
    public void teleopContinuous() {}

    @Override
    public void smartDashboard() {
        SmartDashboard.putBoolean("DB_highGear", highGear);
    }
    
    public static DriveBase getInstance() {
        if (instance == null) {
            synchronized (DriveBase.class) {
                if (instance == null)
                    instance = new DriveBase();
            }
        }
        return instance;
    }
}