package Gagarin.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import FtcExplosivesPackage.Subsystem;

public class LiftSubsystem extends Subsystem {

    public DcMotor liftMotor;
    private LinearOpMode op;

    public LiftSubsystem (LinearOpMode op, DcMotor liftMotor) {
        super(op);
        this.op = op;
        this.liftMotor = liftMotor;
    }

    public void lower_robot() {
        int initPos = liftMotor.getCurrentPosition();
        while (liftMotor.getCurrentPosition() > initPos - 20000 && op.opModeIsActive()) {
            liftMotor.setPower(-1);

            if (!op.opModeIsActive()) {
                return;
            }
        }
        liftMotor.setPower(0);
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