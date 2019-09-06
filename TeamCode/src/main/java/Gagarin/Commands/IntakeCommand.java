package Gagarin.Commands;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import FtcExplosivesPackage.Command;
import FtcExplosivesPackage.ExplosiveTele;
import Gagarin.Constants;
import Utilities.PID;

public class IntakeCommand extends Command {

    private Servo door, lRotator, rRotator;
    private DcMotor intakeMotor;

    private boolean engaged = false;

    private double resetTime = System.currentTimeMillis();
    private boolean number = true;

    private OpMode op;
    private PID intakePID = new PID();

    private Orientation currOrient;
    private double currPitch = 0;

    private AnalogInput potent;
    private Constants c = new Constants();
    private double armAngle;
    private double armPercentOfMax;
    private double startingPitch = 0;

    private double resetTime2 = System.currentTimeMillis();

    final double INTAKE_MOVE_SPEED = 0.015;

    public IntakeCommand(ExplosiveTele op, Servo door, DcMotor intakeMotor, Servo lRotator, Servo rRotator,
                         AnalogInput potent) {
        super(op, "intake");

        this.op = op;
        this.door = door;
        this.intakeMotor = intakeMotor;

        this.lRotator = lRotator;
        this.rRotator = rRotator;

        this.potent = potent;
    }


    @Override
    public void init() {

    }

    @Override
    public void start() {
        intakePID.setup(1,0,0,0,0, 0);
        lRotator.setPosition(0.5);
        rRotator.setPosition(0.5);
    }

    @Override
    public void loop() {

        if (op.gamepad2.left_trigger > 0.05) {
            door.setPosition(0.2);
        } else {
            door.setPosition(0.8);
        }

        //Intake motor
        final double INTAKE_POWER = 1.0;
        if (op.gamepad2.a) {
            intakeMotor.setPower(INTAKE_POWER);
        } else if (op.gamepad2.x) {
            intakeMotor.setPower(-(INTAKE_POWER / 2));
        } else if (op.gamepad2.left_trigger > 0.05) {
            intakeMotor.setPower(0.3);
        } else {
            intakeMotor.setPower(0);
        }



        //intake rotator
        if (op.gamepad2.dpad_up) {
            lRotator.setPosition(lRotator.getPosition() - INTAKE_MOVE_SPEED);
            rRotator.setPosition(rRotator.getPosition() + INTAKE_MOVE_SPEED);
        }
        if (op.gamepad2.dpad_down) {
            lRotator.setPosition(lRotator.getPosition() + INTAKE_MOVE_SPEED);
            rRotator.setPosition(rRotator.getPosition() - INTAKE_MOVE_SPEED);
        }



        /*
        currPitch = (intakeGyro.x() * -1) + startingPitch;

        boolean buffer = System.currentTimeMillis() < resetTime2 + 1000;
        if (op.gamepad2.left_bumper && ! buffer) {
            engaged = true;
            startingPitch = 0 - currPitch;
            resetTime2 = System.currentTimeMillis();
        }

        armPercentOfMax = -((potent.getVoltage() - c.RandPMax) / (c.RandPMax - c.RandPMin));
        armAngle = -14 + (65 * armPercentOfMax);

        double idePower = Math.cos(Math.toRadians(currPitch)) * 0.1;

        if (op.gamepad2.dpad_right) {
            intakePID.adjTarg(-7);

            double movingSpeed = intakePID.status(currPitch) + idePower;
            rRotator.setPosition(0.5 + movingSpeed);
            lRotator.setPosition(0.5 - movingSpeed);
        }

        if (op.gamepad2.dpad_left) {
            intakePID.adjTarg(armAngle);

            double movingSpeed = intakePID.status(currPitch) + idePower;
            rRotator.setPosition(0.5 + movingSpeed);
            lRotator.setPosition(0.5 - movingSpeed);
        }

        if (op.gamepad2.dpad_up) {
            if (engaged) {
                double movingSpeed = limit(0.5, 0.2, Math.cos(Math.toRadians(currPitch)) * 0.5);

                rRotator.setPosition(0.5 + movingSpeed);
                lRotator.setPosition(0.5 - movingSpeed);
            } else {
                rRotator.setPosition(1.0);
                lRotator.setPosition(0.0);
            }
        } else if (op.gamepad2.dpad_down) {
            if (engaged) {
                double movingSpeed = limit(0.5, 0.1, Math.sin(Math.toRadians(currPitch)) * 0.5);

                rRotator.setPosition(0.5 - movingSpeed);
                lRotator.setPosition(0.5 + movingSpeed);
            } else {
                rRotator.setPosition(0.0);
                lRotator.setPosition(1.0);
            }
        } else {
            rRotator.setPosition(0.5 + idePower);
            lRotator.setPosition(0.5 - idePower);
        }
        */


        op.telemetry.addData("Left", lRotator.getPosition());
        op.telemetry.addData("Right", rRotator.getPosition());
        //op.telemetry.addData("Idle Power", idePower);
        //op.telemetry.addData("INTAKE GYRO", currPitch);
        op.telemetry.update();

    }

    public double limit(double maVal, double miVal, double input) {
        return input > maVal ? maVal : input < miVal ? miVal : input;
    }

    @Override
    public void stop() {

    }
}