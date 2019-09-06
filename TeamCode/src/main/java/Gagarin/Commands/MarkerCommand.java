package Gagarin.Commands;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import FtcExplosivesPackage.Command;
import FtcExplosivesPackage.ExplosiveTele;

public class MarkerCommand extends Command {

    Servo mark;
    OpMode op;

    public MarkerCommand(ExplosiveTele op, Servo mark) {
        super(op, "lift");
        this.mark = mark;
        this.op = op;
    }


    @Override
    public void init() {
        mark.setPosition(0.25);
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {

        if (op.gamepad1.right_trigger > 0.1) {
            mark.setPosition(0.0);
        }
    }

    @Override
    public void stop() {

    }
}
