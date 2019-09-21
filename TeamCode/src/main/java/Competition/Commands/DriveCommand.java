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

public class DriveCommand extends BioCommand {

    private DcMotor bright, fright, bleft, fleft;
    private BiohazardNavX gyro;
    DriveSubsystem drive;

    private Gamepad driver;

    Utility u;

    PID turnPID = new PID();

    public DriveCommand(BiohazardTele op) {
        super(op, "drive");
        u = new Utility(op);
        Robot robot = new Robot(op);
        robot.enable();

        drive = Robot.drive;

        turnPID.setup(0.05, 0, 0, 0, 0.5, 0);
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
        bright.setPower(-driver.right_stick_y);
        fright.setPower(-driver.right_stick_y);
        bleft.setPower(-driver.left_stick_y);
        fleft.setPower(-driver.left_stick_y);

        if (driver.a) {
            autoStack();
        }
    }

    private void autoStack() {
        double stackX = VisionCommand.stackX;
        int width  = VisionCommand.stackWid;

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
