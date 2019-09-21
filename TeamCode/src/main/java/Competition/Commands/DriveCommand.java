package Competition.Commands;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import Competition.Robot;
import Competition.RobotMap;
import FtcExplosivesPackage.BioCommand;
import FtcExplosivesPackage.BiohazardNavX;
import FtcExplosivesPackage.BiohazardTele;
import FtcExplosivesPackage.ToxinFieldBasedControl;

public class DriveCommand extends BioCommand {

    private DcMotor bright, fright, bleft, fleft;
    private BiohazardNavX gyro;
    private Gamepad driver;


    BiohazardTele op;
    private float sidePower, straightPower, turnPower, frightPower, brightPower, fleftPower, bleftPower;
    private boolean slowPower = false;
    private boolean isFieldOrientedControl = false;
    private double resetTime = System.currentTimeMillis();
    private final double DEADBAND = 0.05;
    boolean buffer = true;


    public DriveCommand(BiohazardTele op) {
        super(op, "drive");
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
    public void start(){


    }

    @Override
    public void loop() {

        buffer = System.currentTimeMillis() > resetTime + 200;

        if(isFieldOrientedControl){
            ToxinFieldBasedControl.Point leftStick = ToxinFieldBasedControl.getLeftJoystick(driver, gyro);
            sidePower = (float)leftStick.x;
            straightPower = (float)leftStick.y;
            turnPower = driver.right_stick_x;
        } else {
            sidePower = driver.left_stick_x;
            straightPower = driver.left_stick_y;
            turnPower = driver.right_stick_x;
        }

        if (driver.x && buffer && !slowPower){
            slowPower = true;
            resetTime = System.currentTimeMillis();
            buffer = false;
        }

        if (driver.x && buffer && slowPower){

            slowPower = false;
            resetTime = System.currentTimeMillis();
            buffer = false;

        }

        if (driver.a && buffer && !isFieldOrientedControl){
            isFieldOrientedControl = true;
            resetTime = System.currentTimeMillis();
            buffer = false;
        }

        if (driver.a && buffer && isFieldOrientedControl){

            isFieldOrientedControl = false;
            resetTime = System.currentTimeMillis();
            buffer = false;

        }

        if (slowPower){
            sidePower /= 2;
            straightPower /= 5;
            turnPower /= 5;
        }

        frightPower = +sidePower + straightPower + turnPower;
        brightPower = -sidePower + straightPower + turnPower;
        bleftPower  = +sidePower + straightPower - turnPower;
        fleftPower  = -sidePower + straightPower - turnPower;

        //finds the greatest number than finds the scale factor to make that equal to one.
        float scaleAdjust = ScaleAdjustment(frightPower, brightPower, bleftPower, fleftPower, 1);
        frightPower *= scaleAdjust;
        brightPower *= scaleAdjust;
        fleftPower  *= scaleAdjust;
        brightPower *= scaleAdjust;

        //deadband system is set to 0.05
        if(Math.abs(straightPower) > DEADBAND || Math.abs(sidePower) > DEADBAND || Math.abs(turnPower) > DEADBAND) {

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


        op.telemetry.addData("straight", straightPower);
        op.telemetry.addData("side", sidePower);
        op.telemetry.addData("turn", turnPower);
        op.telemetry.addData("slow down", slowPower);
        op.telemetry.addData("Field Oriented", isFieldOrientedControl);
        op.telemetry.update();

    }

    @Override
    public void stop() {

        bright.setPower(0);
        fright.setPower(0);
        bleft.setPower(0);
        fleft.setPower(0);

    }

}
