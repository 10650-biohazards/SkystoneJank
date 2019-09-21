package Competition;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp (name = "transmission Test")
public class transmissionTest extends OpMode {

    DcMotor forward, back;

    @Override
    public void init() {
        forward = hardwareMap.get(DcMotor.class, "for");
        back = hardwareMap.get(DcMotor.class, "back");
    }

    @Override
    public void loop() {
        forward.setPower(gamepad1.left_stick_y);
        back.setPower(gamepad1.right_stick_y);
    }
}
