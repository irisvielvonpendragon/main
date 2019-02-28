package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

import edu.wpi.first.wpilibj.VictorSP;

public class Lift extends Subsystem {

    private static volatile Lift instance;

    private TorqueMotor pulleyA;
    private TorqueMotor pulleyB;

    private final ScheduledPID liftPID;
    
    private double speed;
    private double currentPos;
    private double setpoint;
    private double prevSetpoint;
    private boolean clockwise = true;

    private Lift() {
        pulleyA = new TorqueMotor(new VictorSP(Ports.LF_MOTOR_A), !clockwise);
        pulleyB = new TorqueMotor(new VictorSP(Ports.LF_MOTOR_B), !clockwise);

        liftPID = new ScheduledPID.Builder(0, -0.4, 0.4, 1)
                .setPGains(0.7)
                .setIGains(0.2)
                //.setDGains(0.01)
                .build();
        speed = 0;
        setpoint = input.getLFSetpoint(0);
    }

    @Override
    public void autoInit() {
        speed = 0;
    }

    @Override
    public void teleopInit() {
        speed = 0;
    }

    @Override
    public void disabledInit() {
        speed = 0;
    }

    @Override
    public void run(RobotState state) {
        if (state == RobotState.AUTO) {

        }

        else if (state == RobotState.TELEOP) {
            runLiftPID();
        }

        else if (state == RobotState.VISION) {
            runLiftBottom();
        }

        else if (state == RobotState.LINE) {
            runLiftPID();
        }
        
        output();
    }

    private void runLiftPID() {
        setpoint = input.getLFSetpoint();
        currentPos = feedback.getLFPosition();
        if (setpoint != prevSetpoint) {
            liftPID.changeSetpoint(setpoint);
            prevSetpoint = setpoint;
        }

        speed = liftPID.calculate(currentPos) ;
    }

    private void runLiftBottom() {
        setpoint = input.getLFSetpoint(0);
        currentPos = feedback.getLFPosition();
        if (setpoint != prevSetpoint) {
            liftPID.changeSetpoint(setpoint);
            prevSetpoint = setpoint;
        }

        speed = liftPID.calculate(currentPos);
    }

    private double addBaseOutput(double speed) {
        if (feedback.getLFPosition() < input.getLFSetpoint(1)) {
            return speed + 0.04;
        }
        else if (feedback.getLFPosition() < input.getLFSetpoint(2)) {
            return speed + 0.08;
        }
        return speed;
    }

    @Override
    protected void output() {
        //speed = addBaseOutput(speed);
        pulleyA.set(speed);
        pulleyB.set(speed);
    }

    @Override
    public void disabledContinuous() {}

    @Override
    public void autoContinuous() {}

    @Override
    public void teleopContinuous() {}

    @Override
    public void smartDashboard() {}

    public static Lift getInstance() {
        if (instance == null) {
            synchronized (Lift.class) {
                if (instance == null)
                    instance = new Lift();
            }
        }
        return instance;
    }
}
