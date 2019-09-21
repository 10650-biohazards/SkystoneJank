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

@Autonomous (name = "Red Foundation Only")
public class RedFoundationOnly extends ExplosiveAuto {

    DriveSubsystem drive;
    //VisionSubsystem vision;
    HookSubsystem hooker;
    curveProcessor3 curve;

    @Override
    public void initHardware() {
        RobotMap robotMap = new RobotMap(hardwareMap);
        Robot robot = new Robot(this);
        robot.enable();

        Robot.track.setCurrentNode(1, -3, 180);
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
        drive.moveStraightPID(-2000);
        hooker.hook();
        drive.moveStraightPID(1500);
        hooker.release();
        drive.straightToPoint(new myPoint(0, -2.5));
    }

    @Override
    public void exit() throws InterruptedException {

    }
}
