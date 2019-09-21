package Competition.Programs.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Competition.Robot;
import Competition.RobotMap;
import Competition.Subsystems.DriveSubsystem;
import Competition.Subsystems.VisionSubsystem;
import DubinsCurve.Node;
import DubinsCurve.curveProcessor3;
import DubinsCurve.myPoint;
import FtcExplosivesPackage.ExplosiveAuto;

@Autonomous (name = "Blue Skystone Only", group = "Blue")
public class BlueSkyOnly extends ExplosiveAuto {

    DriveSubsystem drive;
    VisionSubsystem vision;
    curveProcessor3 curve;

    @Override
    public void initHardware() {
        RobotMap robotMap = new RobotMap(hardwareMap);
        Robot robot = new Robot(this);
        robot.enable();

        Robot.track.setCurrentNode(1.5, -2.625, 0);
        RobotMap.gyro.startAng = 90;

        drive = Robot.drive;
        vision = Robot.vision;

        curve = new curveProcessor3(drive, telemetry, this);
    }

    @Override
    public void initAction() {
        int skyPos = vision.grabSkyPos();

        drive.moveStraightPID(200);

        if (skyPos == 0) {
            drive.straightToPoint(new myPoint(1.8, -1));
        } else if (skyPos == 1) {
            drive.straightToPoint(new myPoint(1.5, -1));
        } else {
            drive.straightToPoint(new myPoint(1.2, -1));
        }

        curve.move(drive.track.getCurrentNode(), new Node(-0.5, -1.5, 270));
        drive.moveStraightPID(-500);
        drive.moveTurnPID(90);

        if (skyPos == 0) {
            curve.move(drive.track.getCurrentNode(), new Node(2.8, -1, 0));
        } else if (skyPos == 1) {
            curve.move(drive.track.getCurrentNode(), new Node(2.5, -1, 0));
        } else {
            curve.move(drive.track.getCurrentNode(), new Node(2.2, -1, 0));
        }
        curve.move(drive.track.getCurrentNode(), new Node(-0.5, -1.5, 270));
        drive.moveStraightPID(-500);
    }

    @Override
    public void body() throws InterruptedException {

    }

    @Override
    public void exit() throws InterruptedException {

    }
}
