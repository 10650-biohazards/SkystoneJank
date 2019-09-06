package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import FtcExplosivesPackage.ExplosiveAuto;
import Gagarin.GagarinRobot;
import Gagarin.Subsystems.DriveSubsystem;
import ftc.vision.Beacon.BeaconColorResult;
import ftc.vision.FrameGrabber;
import ftc.vision.ImageProcessorResult;

@Autonomous(name = "Is this really going to work?")
public class beaconTest extends ExplosiveAuto {

    FrameGrabber frameGrabber = FtcRobotControllerActivity.frameGrabber;

    GagarinRobot robot;
    DriveSubsystem drive;

    @Override
    public void initHardware() {
        robot = new GagarinRobot(this);
        this.drive = robot.drive;
    }

    @Override
    public void initAction() {

    }

    @Override
    public void body() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        frameGrabber.grabSingleFrame();

        telemetry.addData("Processing time B", System.currentTimeMillis() - startTime);

        while (!frameGrabber.isResultReady()) {}

        telemetry.addData("Processing time C", System.currentTimeMillis() - startTime);

        ImageProcessorResult imageProcessorResult = frameGrabber.getResult();
        BeaconColorResult result = (BeaconColorResult) imageProcessorResult.getResult();

        telemetry.addData("Processing time D", System.currentTimeMillis() - startTime);

        telemetry.addData("Result", result);
        telemetry.update();

        if (result.getLeftColor() == BeaconColorResult.BeaconColor.RED) {
            drive.move_turn_gyro(-25);
            drive.move_straight_PID(750);
        }
    }

    @Override
    public void exit() throws InterruptedException {

    }
}
