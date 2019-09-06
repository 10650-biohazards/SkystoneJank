package Gagarin.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import FtcExplosivesPackage.Subsystem;

public class ArmSubsystem extends Subsystem {

    public DcMotor slide;

    public ArmSubsystem(OpMode op, DcMotor slide) {
        super(op);
        this.slide = slide;
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
