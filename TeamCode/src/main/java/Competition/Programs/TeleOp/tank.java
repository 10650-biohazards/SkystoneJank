package Competition.Programs.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import Competition.Robot;
import Competition.RobotMap;
import FtcExplosivesPackage.BiohazardTele;
import Utilities.PID;
import Utilities.Utility;

@TeleOp (name = "Tank")
public class tank extends BiohazardTele {

    DcMotor bright, fright, bleft, fleft;

    PID breakPIDF = new PID();
    PID breakPIDB = new PID();
    Utility u = new Utility(this);

    @Override
    public void initHardware() {
        RobotMap robotMap = new RobotMap(hardwareMap);
        Robot robot = new Robot(this);
        robot.enable();

        bright = RobotMap.bright;
        fright = RobotMap.fright;
        bleft = RobotMap.bleft;
        fleft = RobotMap.fleft;
    }

    @Override
    public void initAction() {
        breakPIDF.setup(0.05, 0, 0, 0, 0, 0);
        breakPIDB.setup(0.05, 0, 0, 0, 0, 0);
    }

    @Override
    public void firstLoop() {
        RobotMap.hooker.setPosition(0.5);
    }

    @Override
    public void bodyLoop() {
        bright.setPower(breakPIDB.status(bright.getCurrentPosition()));
        fright.setPower(breakPIDF.status(fright.getCurrentPosition()));
        bleft.setPower(gamepad1.left_stick_y);
        fleft.setPower(gamepad1.left_stick_y);

        telemetry.addData("hook", RobotMap.hooker.getPosition());
        telemetry.update();
    }

    @Override
    public void exit() {

    }
}