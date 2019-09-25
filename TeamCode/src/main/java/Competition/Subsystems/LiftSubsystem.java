package Competition.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import Competition.RobotMap;
import FtcExplosivesPackage.BioSubsystem;

public class LiftSubsystem extends BioSubsystem {

    DcMotor lift, rotator;

    static int rotTarget;

    LinearOpMode op;

    public LiftSubsystem(LinearOpMode op) {
        super(op);
        this.op = op;
    }

    @Override
    public void enable() {
        lift = RobotMap.lift;
        rotator = RobotMap.rotator;

        rotTarget = lift.getCurrentPosition();

        LiftThread thread = new LiftThread();
        thread.enable(op);
    }

    @Override
    public void disable() {

    }

    public void setrotTarget(int input) {
        rotTarget = input;
    }

    @Override
    public void stop() {

    }
}
