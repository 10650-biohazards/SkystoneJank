package Competition.Programs.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Competition.Commands.DriveCommand;
import Competition.Commands.VisionCommand;
import Competition.Robot;
import Competition.RobotMap;
import FtcExplosivesPackage.BiohazardTele;

@TeleOp (name = "Most Basic TeleOp")
public class BasicTele extends BiohazardTele {

    DriveCommand drive;
    VisionCommand vision;

    @Override
    public void initHardware() {
        RobotMap robotMap = new RobotMap(hardwareMap);
        Robot robot = new Robot(this);
        robot.enable();

        drive = new DriveCommand(this);
        vision = new VisionCommand(this);

        drive.enable();
        vision.enable();
    }

    @Override
    public void initAction() {

    }

    @Override
    public void firstLoop() {

    }

    @Override
    public void bodyLoop() {

    }

    @Override
    public void exit() {

    }
}