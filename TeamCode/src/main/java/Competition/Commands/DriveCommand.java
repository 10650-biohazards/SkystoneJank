package Competition.Commands;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import Competition.Robot;
import Competition.RobotMap;
import FtcExplosivesPackage.BioCommand;
import FtcExplosivesPackage.BiohazardNavX;
import FtcExplosivesPackage.BiohazardTele;

public class DriveCommand extends BioCommand {

    private DcMotor bright, fright, bleft, fleft;
    private BiohazardNavX gyro;

    private Gamepad driver;

    public DriveCommand(BiohazardTele op) {
        super(op, "drive");
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


    }

    @Override
    public void stop() {
        bright.setPower(0);
        fright.setPower(0);
        bleft.setPower(0);
        fleft.setPower(0);
    }
}
