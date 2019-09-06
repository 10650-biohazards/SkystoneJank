package Competition;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import Competition.Subsystems.DriveSubsystem;
import Utilities.driveTracker;
import Utilities.driveTracker2;

public class Robot {

    public static DriveSubsystem drive;

    public static driveTracker2 track;

    public static Gamepad driver, manipulator;

    public Robot(LinearOpMode op) {
        drive = new DriveSubsystem(op);
        driver = op.gamepad1;
        manipulator = op.gamepad2;
    }

    public void enable() {
        track = new driveTracker2(drive);
        drive.enable();
        drive.setTracker(track);
    }
}
