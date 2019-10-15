package Competition.Programs.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Competition.Robot;
import Competition.RobotMap;
import Competition.Subsystems.DriveSubsystem;
import Competition.Subsystems.HookSubsystem;
import Competition.Subsystems.VisionSubsystem;
import DubinsCurve.curveProcessor3;
import DubinsCurve.myPoint;
import FtcExplosivesPackage.ExplosiveAuto;
import Utilities.Utility;

@Autonomous (name = "Blue Foundation Only", group = "blue")
public class BlueFoundationOnly extends ExplosiveAuto {

    DriveSubsystem drive;
    //VisionSubsystem vision;
    HookSubsystem hooker;
    curveProcessor3 curve;

    Utility u = new Utility(this);

    @Override
    public void initHardware() {
        RobotMap robotMap = new RobotMap(hardwareMap);
        Robot robot = new Robot(this);
        robot.enable();

        Robot.track.setCurrentNode(-1, -3, 90);
        RobotMap.gyro.startAng = 90;

        drive = Robot.drive;
        //vision = Robot.vision;
        hooker = Robot.hooker;

        curve = new curveProcessor3(drive, telemetry, this);
    }

    @Override
    public void initAction() {

    }

    @Override
    public void body() throws InterruptedException {

        //drive.moveStraightPID(500);


        drive.moveStrafePow(-1, 500);
        drive.moveTurnPID(90);
        drive.moveStrafePow(-0.5, 300);
        hooker.hook();
        u.waitMS(1000);
        drive.moveStrafePow(1, 1800);
        hooker.release();
        drive.moveStraightPID(1000);
        drive.moveTurnPID(10);
        drive.moveStraightPID(1300/4);
        drive.moveTurnPID(270);
        drive.moveStraightPID(4000/4, 2000);
        drive.moveTurnPID(270);
        drive.moveStraightPID(-3000/4);
    }

    @Override
    public void exit() throws InterruptedException {

    }
}
