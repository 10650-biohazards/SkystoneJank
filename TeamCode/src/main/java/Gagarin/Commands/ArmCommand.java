package Gagarin.Commands;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

import FtcExplosivesPackage.Command;
import FtcExplosivesPackage.ExplosiveTele;
import Gagarin.Constants;
import Utilities.PID;

public class ArmCommand extends Command {

    private double armTarg;

    private DcMotor slideMotor;
    private DcMotor rackPinion;

    Constants c = new Constants();

    private int slideMax = 0, slideMin = -6000;

    private AnalogInput potent;

    private OpMode op;

    private PID armPID = new PID();

    public ArmCommand (ExplosiveTele op, DcMotor slideMotor, DcMotor rackPinion, AnalogInput potent){
        super(op, "arm");
        this.slideMotor = slideMotor;
        this.rackPinion = rackPinion;

        this.potent = potent;

        this.op = op;
    }

    private double limit(double maVal, double miVal, double input) {
        return input > maVal ? maVal : input < miVal ? miVal : input;
    }

    @Override
    public void init() {
        armTarg = potent.getVoltage();
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armPID.setup(4, 0, 0, -0.10, 0.01, armTarg);
    }

    @Override
    public void start() {}

    @Override
    public void loop() {

        //Linear Slides
        //final int SLIDE_CHANGE_RATE = 10;

        /*
        if (Math.abs(op.gamepad2.left_stick_y) > 0.05) {
            slideTarget += op.gamepad2.left_stick_y * SLIDE_CHANGE_RATE;
        }

        slidePID.setup(0.0005,0, 0, 0.2,42, slideTarget);
        */

        if (Math.abs(op.gamepad2.left_stick_y) > 0.05) {
            /*if (op.gamepad2.left_stick_y < 0 && slideMotor.getCurrentPosition() > slideMin) {
                slideMotor.setPower(op.gamepad2.left_stick_y);
            } else if (op.gamepad2.left_stick_y > 0 && slideMotor.getCurrentPosition() < slideMax) {
                slideMotor.setPower(op.gamepad2.left_stick_y);
            } else {
                slideMotor.setPower(0);
            }*/
            slideMotor.setPower(op.gamepad2.left_stick_y);
        } else {
            slideMotor.setPower(0);
        }

        /*
        if (op.gamepad2.left_stick_y > 0 && slideMotor.getCurrentPosition() > slideMin) {
            op.telemetry.addData("YOOOOOOOO", " ");
        } else if (op.gamepad2.left_stick_y < 0 && slideMotor.getCurrentPosition() < slideMax) {
            op.telemetry.addData("YOOOOOOOO", " ");
        } else {
            op.telemetry.addData("NOOOOOOOO", " ");
        }
        */

        //Rack and Pinion
        double currAng = potent.getVoltage();
        //double currAng = convertPotTorad(potent.getVoltage());



        final double ARM_CHANGE_RATE = 0.005;

        //if (Math.abs(op.gamepad2.right_stick_y) > 0.05) {
        //    armTarg += ARM_CHANGE_RATE * op.gamepad2.right_stick_y;
        //}

        if (op.gamepad2.right_stick_y < -0.05) {
            armTarg = c.RandPMin;
        }

        if (op.gamepad2.right_stick_y > 0.05) {
            armTarg = c.RandPMax;
        }

        if (op.gamepad1.y) {
            armTarg = c.RandPMin;
        }

        armTarg = limit(c.RandPMax, c.RandPMin, armTarg);

        armPID.adjTarg(armTarg);

        if (armPID.e > 0) {
            armPID.adjP(1);
        } else {
            armPID.adjP(4);
        }

        double power = -armPID.status(currAng, true);

        /*op.telemetry.addData("E", armPID.e);
        op.telemetry.addData("P", armPID.P);
        op.telemetry.addData("P x E", (-armPID.e * armPID.P));
        op.telemetry.addData("POWER", power);
        op.telemetry.update();*/

        if (!armPID.done()) {
            power += 0.1;
        } else {
            power = 0.1;
        }

        rackPinion.setPower(power /* * Math.cos(currAng)*/);

        /*op.telemetry.addData("ERROR", armPID.e);
        op.telemetry.addData("kP", (armPID.e * armPID.P));
        op.telemetry.addData("DERIV", armPID.det);
        op.telemetry.addData("kD", (armPID.det * armPID.D));
        op.telemetry.addData("POWA", power);
        op.telemetry.addData("SLIDE MOTOR", slideMotor.getCurrentPosition());
        op.telemetry.addData("Actual", currAng);
        op.telemetry.addData("target", armTarg);
        op.telemetry.update();*/
    }

    @Override
    public void stop() {

    }
}