package Gagarin;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import FtcExplosivesPackage.BiohazardBNO055Gyro;
import FtcExplosivesPackage.BiohazardTele;
import FtcExplosivesPackage.ExplosiveAuto;
import FtcExplosivesPackage.ExplosiveNavX;
import FtcExplosivesPackage.ExplosiveTele;
import Gagarin.Subsystems.ArmSubsystem;
import Gagarin.Subsystems.DriveSubsystem;
import Gagarin.Subsystems.IntakeSubsystem;
import Gagarin.Subsystems.LiftSubsystem;
import Gagarin.Subsystems.MarkerSubsystem;

public class GagarinRobot {

    public DriveSubsystem drive;
    public IntakeSubsystem intake;
    public ArmSubsystem arm;
    public LiftSubsystem lift;
    public MarkerSubsystem mark;
    public BiohazardBNO055Gyro intakeGyro;

    //LinearOpMode op;
    //OpMode op;
    public ExplosiveNavX gyro;

    public DcMotor bright, fright, bleft, fleft, liftMotor, intakeMotor, slideMotor, rackMotor;
    public Servo markServo, lRotator, rRotator, door;

    public AnalogInput potent;
    public ModernRoboticsI2cRangeSensor ultra;

    public GagarinRobot(BiohazardTele op) {
        //this.op = op;

        //intakeGyro = new BiohazardBNO055Gyro(op.hardwareMap, "imu");

        bright = op.hardwareMap.get(DcMotor.class, "rightB");
        fright = op.hardwareMap.get(DcMotor.class, "right");
        bleft = op.hardwareMap.get(DcMotor.class, "leftB");
        fleft = op.hardwareMap.get(DcMotor.class, "left");
        liftMotor = op.hardwareMap.get(DcMotor.class, "actuator");
        intakeMotor = op.hardwareMap.get(DcMotor.class,"intake");
        slideMotor = op.hardwareMap.get(DcMotor.class, "Post-Progressive Jazz Funk");
        rackMotor = op.hardwareMap.get(DcMotor.class, "ARMaan");

        bright.setDirection(DcMotor.Direction.REVERSE);
        fright.setDirection(DcMotor.Direction.REVERSE);

        potent = op.hardwareMap.get(AnalogInput.class, "Diamond in the Rough");

        markServo = op.hardwareMap.get(Servo.class, "drunkard servo");

        lRotator = op.hardwareMap.get(Servo.class, "left rotator");
        rRotator = op.hardwareMap.get(Servo.class, "right rotator");

        gyro = new ExplosiveNavX(op, "41", 0);
        ultra = op.hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "SONIC THE HEDGEHOG");

        initSubsystems(op);
    }

    public GagarinRobot(ExplosiveAuto op) {
        //this.op = op;

        //intakeGyro = new BiohazardBNO055Gyro(op.hardwareMap, "imu");

        bright = op.hardwareMap.get(DcMotor.class, "rightB");
        fright = op.hardwareMap.get(DcMotor.class, "right");
        bleft = op.hardwareMap.get(DcMotor.class, "leftB");
        fleft = op.hardwareMap.get(DcMotor.class, "left");
        liftMotor = op.hardwareMap.get(DcMotor.class, "actuator");
        intakeMotor = op.hardwareMap.get(DcMotor.class,"intake");
        slideMotor = op.hardwareMap.get(DcMotor.class, "Post-Progressive Jazz Funk");
        rackMotor = op.hardwareMap.get(DcMotor.class, "ARMaan");

        bright.setDirection(DcMotor.Direction.REVERSE);
        fright.setDirection(DcMotor.Direction.REVERSE);

        potent = op.hardwareMap.get(AnalogInput.class, "Diamond in the Rough");

        markServo = op.hardwareMap.get(Servo.class, "drunkard servo");

        lRotator = op.hardwareMap.get(Servo.class, "left rotator");
        rRotator = op.hardwareMap.get(Servo.class, "right rotator");

        gyro = new ExplosiveNavX(op, "41", 0);
        ultra = op.hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "SONIC THE HEDGEHOG");

        initSubsystems(op);
    }

    public GagarinRobot(ExplosiveTele op, HardwareMap hardwareMap) {
        //this.op = op;

        //intakeGyro = new BiohazardBNO055Gyro(hardwareMap, "imu");

        bright = hardwareMap.get(DcMotor.class, "rightB");
        fright = hardwareMap.get(DcMotor.class, "right");
        bleft = hardwareMap.get(DcMotor.class, "leftB");
        fleft = hardwareMap.get(DcMotor.class, "left");
        liftMotor = hardwareMap.get(DcMotor.class, "actuator");
        intakeMotor = hardwareMap.get(DcMotor.class,"intake");
        slideMotor = hardwareMap.get(DcMotor.class, "Post-Progressive Jazz Funk");
        rackMotor = hardwareMap.get(DcMotor.class, "ARMaan");

        bleft.setDirection(DcMotor.Direction.REVERSE);
        fright.setDirection(DcMotor.Direction.REVERSE);

        markServo = hardwareMap.get(Servo.class, "drunkard servo");
        door = hardwareMap.get(Servo.class, "right trapdoor");

        lRotator = hardwareMap.get(Servo.class, "left rotator");
        rRotator = hardwareMap.get(Servo.class, "right rotator");

        potent = hardwareMap.get(AnalogInput.class, "Diamond in the Rough");
        gyro = new ExplosiveNavX(op, "41", 0);
        ultra = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "SONIC THE HEDGEHOG");
    }

    public void initSubsystems(LinearOpMode op) {
        drive = new DriveSubsystem(op, fleft, fright, bleft, bright, gyro, ultra);
        intake = new IntakeSubsystem(op, intakeMotor, lRotator, rRotator, intakeGyro);
        arm = new ArmSubsystem(op, slideMotor);
        lift = new LiftSubsystem(op, liftMotor);
        mark = new MarkerSubsystem(op, markServo);
    }
}