package Competition.Programs.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Competition.Robot;
import Competition.RobotMap;
import Competition.Subsystems.DriveSubsystem;
import Competition.Subsystems.VisionSubsystem;
import DubinsCurve.curveProcessor3;
import FtcExplosivesPackage.ExplosiveAuto;

@Autonomous (name = "Red Skystone Only")
public class Template extends ExplosiveAuto {

    DriveSubsystem drive;
    VisionSubsystem vision;
    curveProcessor3 curve;

    @Override
    public void initHardware() {
        RobotMap robotMap = new RobotMap(hardwareMap);
        Robot robot = new Robot(this);
        robot.enable();

        Robot.track.setCurrentNode(-1.5, -2.625, 0);
        RobotMap.gyro.startAng = 90;

        drive = Robot.drive;
        vision = Robot.vision;

        curve = new curveProcessor3(drive, telemetry, this);
    }

    @Override
    public void initAction() {

    }

    @Override
    public void body() throws InterruptedException {

    }

    @Override
    public void exit() throws InterruptedException {

    }
}
