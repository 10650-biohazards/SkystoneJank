package Competition.Programs.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import Competition.Robot;
import Competition.RobotMap;
import FtcExplosivesPackage.BiohazardTele;

@TeleOp (name = "Tank")
public class tank extends BiohazardTele {

    DcMotor bright, fright, bleft, fleft;

    @Override
    public void initHardware() {
        RobotMap map = new RobotMap(hardwareMap);
        Robot robot = new Robot(this);
        robot.enable();

        bright = RobotMap.bright;
        fright = RobotMap.fright;
        bleft = RobotMap.bleft;
        fleft = RobotMap.fleft;
    }

    @Override
    public void initAction() {

    }

    @Override
    public void firstLoop() {

    }

    @Override
    public void bodyLoop() {
        bright.setPower(gamepad1.right_stick_y);
        fright.setPower(gamepad1.right_stick_y);
        bleft.setPower(gamepad1.left_stick_y);
        fleft.setPower(gamepad1.left_stick_y);

        telemetry.addData("Right y", gamepad1.right_stick_y);
        telemetry.addData("Left  y", gamepad1.left_stick_y);
        telemetry.addData("Range", RobotMap.frontRange.getDistance(DistanceUnit.INCH));
        telemetry.update();
    }

    @Override
    public void exit() {

    }
}