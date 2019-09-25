package Competition.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import Competition.RobotMap;
import Utilities.PID;

public class LiftThread implements Runnable {

    final int TICKS_PER_ROT = 42;
    final double STARTING_ANG = 42;

    DcMotor rotator;

    LinearOpMode op;

    public void enable(LinearOpMode linear) {
        Thread t = new Thread(this);
        rotator = RobotMap.rotator;
        op = linear;
        t.start();
    }

    @Override
    public void run() {
        PID rotatePID = new PID();
        rotatePID.setup(0.05, 0, 0, 0, 0.1, STARTING_ANG);

        while (op.opModeIsActive()) {
            rotatePID.setTarget(LiftSubsystem.rotTarget);
            double currAng = STARTING_ANG + ((rotator.getCurrentPosition() / TICKS_PER_ROT) * 360);
            double power = rotatePID.status(currAng);
            rotator.setPower(power);
        }
    }
}