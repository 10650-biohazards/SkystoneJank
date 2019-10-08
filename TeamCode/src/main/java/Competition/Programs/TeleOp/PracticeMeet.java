package Competition.Programs.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Competition.Commands.DriveCommand;
import Competition.Commands.MechCommand;
import Competition.PracticeMap;
import Competition.PracticeRobot;
import FtcExplosivesPackage.BiohazardTele;

@TeleOp (name = "Practice Meet TeleOp")
public class PracticeMeet extends BiohazardTele {

    DriveCommand drive;
    MechCommand mech;

    @Override
    public void initHardware() {
        PracticeMap robotMap = new PracticeMap(hardwareMap);
        PracticeRobot robot = new PracticeRobot(this);
        robot.enable();

        drive = new DriveCommand(this);
        mech = new MechCommand(this);

        drive.enable();
        mech.enable();
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