package Gagarin.Commands;

import com.qualcomm.robotcore.hardware.DcMotor;

import FtcExplosivesPackage.Command;
import FtcExplosivesPackage.ExplosiveNavX;
import FtcExplosivesPackage.ExplosiveTele;
import Utilities.PID;

public class DriveCommand extends Command {

    private ExplosiveTele op;

    private double MULTIPLIER = 1.0;

    private boolean number = true;
    private boolean number2 = true;
    private boolean firstCycle = true;

    private double goToAng;

    private long resetTime = 0;

    private PID holdPID = new PID();
    private PID turnPID = new PID();

    private double rightPower, leftPower, rightPowerB, leftPowerB;

    private DcMotor bright, fright, bleft, fleft;
    private ExplosiveNavX gyro;

    public DriveCommand(ExplosiveTele opmode, DcMotor fleft, DcMotor fright, DcMotor bleft,
                        DcMotor bright, ExplosiveNavX gyro) {

        super(opmode, "drive");
        this.op = opmode;

        this.bright = bright;
        this.fright = fright;
        this.fleft = fleft;
        this.bleft = bleft;
        this.gyro = gyro;
    }

    public double optimizeAngle(double input) {
        input = input % 360;
        if (input < 0) {
            input += 360;
        }

        return input;
    }

    public void autoTurn() {
        double currAng;

        turnPID.setup(0.02, 0, 0, 0.00, 0.1, goToAng);
        double power;
        while (!turnPID.done() && !op.gamepad1.b) {
            currAng = optimizeAngle(gyro.getYaw());
            op.telemetry.addData("pow", turnPID.status(currAng));
            op.telemetry.addData("ang", gyro.getYaw());
            op.telemetry.update();

            power = turnPID.status(gyro.getYaw());
            set_Pows(-power,power,power,-power);
        }
        set_Pows(0,0,0,0);
    }

    private void hold_ang() {
        boolean buffer = System.currentTimeMillis() < resetTime + 1000;
        if(op.gamepad1.b && !buffer){
            resetTime = System.currentTimeMillis();
            number2 = !number2;
            firstCycle = true;

        }

        if (!number2) {
            if (firstCycle){
                double nomAng = gyro.getYaw();
                holdPID.setup(0.1, 0, 0, 0.2, 0.5, nomAng);
                firstCycle = false;
            }
            double modifier = holdPID.status(gyro.getYaw());

            rightPower += modifier;
            leftPower -= modifier;
            rightPowerB += modifier;
            leftPowerB -= modifier;
        }
    }

    private void set_Pows(double brp, double frp, double blp, double flp) {
        bright.setPower(brp);
        fright.setPower(frp);
        bleft.setPower(blp);
        fleft.setPower(flp);
    }

    @Override
    public void init() {
        bright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        fright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        bleft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        fleft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void start() {
        resetTime = System.currentTimeMillis();
        fright.setPower(0);
        fleft.setPower(0);
        bright.setPower(0);
        bleft.setPower(0);
    }

    @Override
    public void loop() {

        if (op.gamepad1.start) {
            goToAng = optimizeAngle(gyro.getYaw());
        }

        if (op.gamepad1.y) {
            autoTurn();
        }

        leftPower = 0;
        leftPowerB = 0;
        rightPower = 0;
        rightPowerB = 0;


        if (Math.abs(op.gamepad1.left_stick_y) <= 0.1) {
            leftPower = 0;
            leftPowerB = 0;
            rightPower = 0;
            rightPowerB = 0;
        } else {
            rightPower = -op.gamepad1.left_stick_y;
            leftPower = -op.gamepad1.left_stick_y;
            rightPowerB = -op.gamepad1.left_stick_y;
            leftPowerB = -op.gamepad1.left_stick_y;
        }


        //strafe
        if (Math.abs(op.gamepad1.left_stick_x) > 0.05) {
            leftPower += op.gamepad1.left_stick_x;
            rightPower -= op.gamepad1.left_stick_x;
            rightPowerB += op.gamepad1.left_stick_x;
            leftPowerB -= op.gamepad1.left_stick_x;
        }


        if (Math.abs(op.gamepad1.right_stick_x) > 0.05) {
            rightPower  -= op.gamepad1.right_stick_x;
            leftPowerB  += op.gamepad1.right_stick_x;
            leftPower   += op.gamepad1.right_stick_x;
            rightPowerB -= op.gamepad1.right_stick_x;
        }

        hold_ang();

        boolean buffer = System.currentTimeMillis() < resetTime + 1000;
        if(op.gamepad1.a && !buffer){
            resetTime = System.currentTimeMillis();
            number = !number;

        }
        if (!number) {
            MULTIPLIER = 0.5;
        }
        if(number) {
            MULTIPLIER = 1;
        }

        leftPower *= MULTIPLIER;
        leftPowerB *= MULTIPLIER;
        rightPower *= MULTIPLIER;
        rightPowerB *= MULTIPLIER;

        fright.setPower(-rightPower);
        fleft.setPower(-leftPower);
        bright.setPower(rightPowerB);
        bleft.setPower(leftPowerB);

        //op.telemetry.addData("back right", rightPowerB);
        //op.telemetry.addData("front right", rightPower);
        //op.telemetry.addData("back left", leftPowerB);
        //op.telemetry.addData("front left", leftPower);
        //op.telemetry.update();
    }

    @Override
    public void stop() {
        fright.setPower(0);
        fleft.setPower(0);
        bright.setPower(0);
        bleft.setPower(0);
    }
}