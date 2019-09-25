package Competition.Commands;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import Competition.Robot;
import Competition.RobotMap;
import Competition.Subsystems.DriveSubsystem;
import FtcExplosivesPackage.BioCommand;
import FtcExplosivesPackage.BiohazardNavX;
import FtcExplosivesPackage.BiohazardTele;
import Utilities.PID;
import Utilities.Utility;
import FtcExplosivesPackage.ToxinFieldBasedControl;

public class DriveCommand extends BioCommand {

    private DcMotor bright, fright, bleft, fleft;
    private BiohazardNavX gyro;
    DriveSubsystem drive;

    private Gamepad driver;

    Utility u;

    PID turnPID = new PID();


    BiohazardTele op;
    private float sidePower, straightPower, turnPower, frightPower, brightPower, fleftPower, bleftPower;
    private boolean slowPower = false;
    private boolean isFieldOrientedControl = false;
    private double resetTime = System.currentTimeMillis();
    private final double DEADBAND = 0.05;
    boolean buffer = true;


    public DriveCommand(BiohazardTele op) {
        super(op, "drive");
        u = new Utility(op);
        Robot robot = new Robot(op);
        robot.enable();

        drive = Robot.drive;

        turnPID.setup(0.05, 0, 0, 0, 0.5, 0);
        this.op = op;
    }

    //finds the biggest number that is greater than one and proportionally
    //reduces it and the other numbers so the greatest value is equal to one
    public float ScaleAdjustment(float a, float b, float c, float d, float maxValue){
        float largestValue = Math.max(Math.max(a,b) ,Math.max(c,d));
        float adjustment = 0;
        if(largestValue > maxValue){
            adjustment = maxValue/largestValue;
        } else {
            adjustment = 1;
        }
        return adjustment;
    }


    @Override
    public void init() {
        bright = RobotMap.bright;
        fright = RobotMap.fright;
        bleft = RobotMap.bleft;
        fleft = RobotMap.fleft;

        gyro = RobotMap.gyro;

        driver = Robot.driver;
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {

        if (driver.b) {

            buffer = System.currentTimeMillis() > resetTime + 200;

            if (isFieldOrientedControl) {
                ToxinFieldBasedControl.Point leftStick = ToxinFieldBasedControl.getLeftJoystick(driver, gyro);
                sidePower = (float) leftStick.x;
                straightPower = (float) leftStick.y;
                turnPower = driver.right_stick_x;
            } else {
                sidePower = driver.left_stick_x;
                straightPower = driver.left_stick_y;
                turnPower = driver.right_stick_x;
            }

            if (driver.x && buffer && !slowPower) {
                slowPower = true;
                resetTime = System.currentTimeMillis();
                buffer = false;
            }

            if (driver.x && buffer && slowPower) {

                slowPower = false;
                resetTime = System.currentTimeMillis();
                buffer = false;

            }

            if (driver.a && buffer && !isFieldOrientedControl) {
                isFieldOrientedControl = true;
                resetTime = System.currentTimeMillis();
                buffer = false;
            }

            if (driver.a && buffer && isFieldOrientedControl) {

                isFieldOrientedControl = false;
                resetTime = System.currentTimeMillis();
                buffer = false;

            }

            if (slowPower) {
                sidePower /= 2;
                straightPower /= 5;
                turnPower /= 5;
            }

            frightPower = +sidePower + straightPower + turnPower;
            brightPower = -sidePower + straightPower + turnPower;
            bleftPower = +sidePower + straightPower - turnPower;
            fleftPower = -sidePower + straightPower - turnPower;

            //finds the greatest number than finds the scale factor to make that equal to one.
            float scaleAdjust = ScaleAdjustment(frightPower, brightPower, bleftPower, fleftPower, 1);
            frightPower *= scaleAdjust;
            brightPower *= scaleAdjust;
            fleftPower *= scaleAdjust;
            brightPower *= scaleAdjust;

            //deadband system is set to 0.05
            if (Math.abs(straightPower) > DEADBAND || Math.abs(sidePower) > DEADBAND || Math.abs(turnPower) > DEADBAND) {

                fright.setPower(frightPower);
                bright.setPower(brightPower);
                bleft.setPower(bleftPower);
                fleft.setPower(fleftPower);

            } else {

                fright.setPower(0);
                bright.setPower(0);
                bleft.setPower(0);
                fleft.setPower(0);

            }
        }

        op.telemetry.addData("left  y", Robot.driver.left_stick_y);
        op.telemetry.addData("right y", Robot.driver.right_stick_y);
        op.telemetry.addData("Bright", bright.getCurrentPosition());
        op.telemetry.addData("Fright", fright.getCurrentPosition());
        op.telemetry.addData("Bleft", bleft.getCurrentPosition());
        op.telemetry.addData("fleft", fleft.getCurrentPosition());
        op.telemetry.addData("straight", straightPower);
        op.telemetry.addData("side", sidePower);
        op.telemetry.addData("turn", turnPower);
        op.telemetry.addData("slow down", slowPower);
        op.telemetry.addData("Field Oriented", isFieldOrientedControl);
        op.telemetry.update();
    }


    private void autoStack() {
        double stackX = VisionCommand.stackX;
        int width = VisionCommand.stackWid;
        double brp, frp, blp, flp;

        if (stackX > 98) {
            brp = -1;
            frp = 1;
            blp = 1;
            flp = -1;
        } else if (stackX < 78) {
            brp = 1;
            frp = -1;
            blp = -1;
            flp = 1;
        } else {
            if (width > 70) {
                brp = 0;
                frp = 0;
                blp = 0;
                flp = 0;
            } else {
                brp = -0.3;
                frp = -0.3;
                blp = -0.3;
                flp = -0.3;
            }
        }

        double mod = 0;
        if (Math.abs(stackX - 88) < 40) {
            mod = turnPID.status(gyro.getYaw());
        }

        setPows(brp + mod, frp + mod, blp - mod, flp - mod);
    }

    private void setPows(double brp, double frp, double blp, double flp) {
        bright.setPower(brp);
        fright.setPower(frp);
        bleft.setPower(blp);
        fleft.setPower(flp);
    }

    @Override
    public void stop() {
        bright.setPower(0);
        fright.setPower(0);
        bleft.setPower(0);
        fleft.setPower(0);
    }
}
