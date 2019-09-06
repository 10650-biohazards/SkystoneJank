package Gagarin.Commands;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import FtcExplosivesPackage.Command;
import FtcExplosivesPackage.ExplosiveTele;

public class LiftCommand extends Command {

    private DcMotor liftMotor;

    private OpMode op;

    public LiftCommand(ExplosiveTele op, DcMotor liftMotor){
        super(op, "lift");

        this.liftMotor = liftMotor;

        this.op = op;
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        //op.telemetry.addData("LYFT Encoder", liftMotor.getCurrentPosition());

        if(Math.abs(op.gamepad1.right_trigger) > 0.05){
            liftMotor.setPower(1);
        } else if(op.gamepad1.right_bumper){
            liftMotor.setPower(-1);
        } else {
            liftMotor.setPower(0);
        }
    }

    @Override
    public void stop() {

    }
}