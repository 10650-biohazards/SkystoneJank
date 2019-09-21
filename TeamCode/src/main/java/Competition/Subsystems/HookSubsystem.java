package Competition.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import Competition.RobotMap;
import FtcExplosivesPackage.Subsystem;

public class HookSubsystem extends Subsystem {

    private Servo hooker;

    public HookSubsystem(OpMode op) {
        super(op);
    }

    @Override
    public void enable() {
        hooker = RobotMap.hooker;
    }

    @Override
    public void disable() {

    }

    public void hook() {
        hooker.setPosition(1);
    }

    public void release() {
        hooker.setPosition(0);
    }

    @Override
    public void stop() {

    }
}
