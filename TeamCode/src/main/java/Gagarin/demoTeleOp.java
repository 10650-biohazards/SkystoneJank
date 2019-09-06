package Gagarin;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import FtcExplosivesPackage.ExplosiveTele;
import Gagarin.Commands.ArmCommand;
import Gagarin.Commands.DriveCommand;
import Gagarin.Commands.IntakeCommand;
import Gagarin.Commands.LiftCommand;
import Gagarin.Commands.MarkerCommand;

@TeleOp(name = "Gagarin Demo")
public class demoTeleOp extends ExplosiveTele {

    public GagarinRobot robot;

    @Override
    public void initHardware() {
        robot = new GagarinRobot(this, hardwareMap);

        ArmCommand arm = new ArmCommand(this, robot.slideMotor, robot.rackMotor, robot.potent);
        DriveCommand drive = new DriveCommand(this, robot.fleft, robot.fright, robot.bleft, robot.bright, robot.gyro);
        IntakeCommand intake = new IntakeCommand(this, robot.door, robot.intakeMotor, robot.lRotator, robot.rRotator,
                                                 robot.potent);
        LiftCommand lift = new LiftCommand(this, robot.liftMotor);
        MarkerCommand mark = new MarkerCommand(this, robot.markServo);


        arm.enable();
        drive.enable();
        intake.enable();
        lift.enable();
        mark.enable();
    }

    @Override
    public void initAction() {
        robot.markServo.setPosition(0);
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