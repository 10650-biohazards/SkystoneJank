package Competition.Programs.Autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


import Competition.Robot;
import Competition.RobotMap;
import Competition.Subsystems.DriveSubsystem;
import DubinsCurve.curveProcessor3;
import DubinsCurve.myPoint;
import FtcExplosivesPackage.ExplosiveAuto;

@Autonomous (name = "Sample Auto")
public class sampleAuto extends ExplosiveAuto {

    DriveSubsystem drive;

    curveProcessor3 curve;

    @Override
    public void initHardware() {
        RobotMap robotMap = new RobotMap(hardwareMap);
        Robot robot = new Robot(this);
        robot.enable();

        Robot.track.setCurrentNode(0, 0, 90);
        RobotMap.gyro.startAng = 90;

        drive = Robot.drive;

        curve = new curveProcessor3(drive, telemetry, this);
    }

    @Override
    public void initAction() {

    }

    @Override
    public void body() throws InterruptedException {
        drive.straightToPoint(new myPoint(1, -2));
        drive.straightToPoint(new myPoint(0, 2));
        drive.straightToPoint(new myPoint(-1, 2));
        drive.straightToPoint(new myPoint(3, 3));
    }

    @Override
    public void exit() throws InterruptedException {

    }
}
