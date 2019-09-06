package Gagarin.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import FtcExplosivesPackage.BiohazardBNO055Gyro;
import FtcExplosivesPackage.Subsystem;

public class IntakeSubsystem extends Subsystem {

    public DcMotor intakeMotor;
    Utilities.Utility u;
    OpMode op;

    public Servo lRotator, rRotator;
    public BiohazardBNO055Gyro intakeGyro;

    public IntakeSubsystem(LinearOpMode op, DcMotor intakeMotor, Servo lRotator, Servo rRotator, BiohazardBNO055Gyro intakeGyro) {
        super(op);
        this.intakeMotor = intakeMotor;
        this.lRotator = lRotator;
        this.rRotator = rRotator;
        this.intakeGyro = intakeGyro;
        this.op = op;
        u = new Utilities.Utility(op);
    }

    public void intake(){
        intakeMotor.setPower(0.2);
        u.waitMS(3000);
        intakeMotor.setPower(0);
    }

    public void outttake(){
        intakeMotor.setPower(-0.2);
        u.waitMS(3000);
        intakeMotor.setPower(0);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void stop() {

    }
}