package Gagarin.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import FtcExplosivesPackage.Subsystem;

public class MarkerSubsystem extends Subsystem {

    public Servo markServo;
    OpMode op;

    public MarkerSubsystem(OpMode op, Servo markServo) {
        super(op);
        this.op = op;
        this.markServo = markServo;
    }

    public void dump() {
        markServo.setPosition(0.7);
    }

    public void raise() {
        markServo.setPosition(0.0);
    }

    public void adjust() {markServo.setPosition(0.65);}

    @Override
    public void enable() {
        markServo.setPosition(0.0);
    }

    @Override
    public void disable() {

    }

    @Override
    public void stop() {

    }
}
