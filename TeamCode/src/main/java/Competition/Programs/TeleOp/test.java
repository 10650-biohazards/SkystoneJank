package Competition.Programs.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp (name = "TEST")
public class test extends OpMode {

    DcMotor lift, yes;

    @Override
    public void init() {
        lift = hardwareMap.get(DcMotor.class, "lift");
        yes  =hardwareMap.get(DcMotor.class, "fright");

    }

    @Override
    public void loop() {
        lift.setPower(gamepad1.left_stick_y);
        yes.setPower(gamepad1.right_stick_y);
    }
}
