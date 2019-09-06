package Gagarin;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import FtcExplosivesPackage.ExplosiveAuto;
import Utilities.PID;

@TeleOp (name = "Robot Breaker")
public class breakTest extends ExplosiveAuto {

    GagarinRobot robot;
    PID stopPID = new PID();
    double target;

    @Override
    public void initHardware() {
        robot = new GagarinRobot(this);
    }

    @Override
    public void initAction() {
        target = robot.fright.getCurrentPosition();
        stopPID.setup(0.05, 0, 0, 0, 0, target);
    }

    @Override
    public void body() throws InterruptedException {

        while (opModeIsActive()) {
            double currPos = robot.fright.getCurrentPosition();
            double power = stopPID.status(currPos);
            robot.fright.setPower(power);
            telemetry.addData("Power", power);
            telemetry.addData("Current Position", currPos);
            telemetry.addData("Target Position", target);
            telemetry.update();
        }
    }

    @Override
    public void exit() {

    }
}