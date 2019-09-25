package practice_meet_arnold;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp (name = "Arnold")
public class Arnold_Tele_Demo extends OpMode {
    Arnold_Hardware h = new Arnold_Hardware();

    double  rightPow,
            leftPow,
            strafePow,
            curr_arm_pos,
            curr_arm_speed,
            target_diff,
            e, t;

    boolean target_acquired, target_near;

    double arm_encoder_target;

    double startTime;

    final double I = 0.15,
                 P = 0.15,
                 D = 0.15;

    private double MIN_ENCODER_TARGET = -1350,
                      MAX_ENCODER_TARGET = -60,
                      ARM_ENCODER_TARGET_CHANGE_SPEED = 7;

    final double SENSITIVITY = 0.75;

    public void init() {
        h.init(hardwareMap);
        h.gripperServo.setPosition(0.45);
    }

    private void set_pows() {
        h.rightMotor.setPower(rightPow);
        h.rightBackMotor.setPower(rightPow);
        h.leftMotor.setPower(leftPow);
        h.leftBackMotor.setPower(leftPow);
        h.strafeMotor.setPower(strafePow);
    }

    public void start() {

    }

    public void control_motors() {

        if (Math.abs(gamepad1.left_stick_y) > 0.05) {
            leftPow = -gamepad1.left_stick_y * SENSITIVITY;
            rightPow = -gamepad1.left_stick_y * SENSITIVITY;
        } else {
            leftPow = 0;
            rightPow = 0;
        }

        //if (Math.abs(gamepad1.left_stick_x) > 0.2) {
        //    strafePow = gamepad1.left_stick_x;
        //} else {
        //    strafePow = 0;
        //}

        if (Math.abs(gamepad1.right_stick_x) > 0.05) {
            leftPow += gamepad1.right_stick_x * SENSITIVITY;
            rightPow -= gamepad1.right_stick_x * SENSITIVITY;
        }

        telemetry.addData("left", leftPow);
        telemetry.addData("right", rightPow);
    }

    private void set_servos() {
        h.jewelServo.setPosition(0.0);
        if (gamepad1.dpad_left) {
            h.gripperServo.setPosition(0.40);
        } else if (gamepad1.dpad_right) {
            h.gripperServo.setPosition(0.25);
        }
    }

    private boolean hasGlyph() {return h.glyphSensor.getDistance(DistanceUnit.INCH) < 3;}

    private double limit(double mi, double ma, double v) {return v > ma ? ma : v < mi ? mi : v;}

    public void start_timer() {startTime = System.nanoTime() / 1E9;}
    public double check_timer() { return (System.nanoTime() / 1E9) - startTime;}

    public void update_arm() {
        curr_arm_pos = h.topArm.getCurrentPosition();

        if (gamepad2.a) {
            MAX_ENCODER_TARGET = curr_arm_pos;
        }

        if (gamepad2.b) {
            MIN_ENCODER_TARGET = curr_arm_pos;
        }

        if (gamepad2.dpad_down) {
            arm_encoder_target += ARM_ENCODER_TARGET_CHANGE_SPEED;
        } else if (gamepad2.dpad_up) {
            arm_encoder_target -= ARM_ENCODER_TARGET_CHANGE_SPEED;
        }

        arm_encoder_target = limit(MIN_ENCODER_TARGET, MAX_ENCODER_TARGET, arm_encoder_target);


        target_diff = curr_arm_pos - arm_encoder_target;
        target_acquired = Math.abs(target_diff) <= 5;
        target_near = target_diff <= 500;

        target_diff *= hasGlyph() ? 1.5 : 1;

        if (target_acquired || target_diff < 0) {start_timer();}
        t = check_timer();
        e = target_diff / 100;
        /*
        telemetry.addData("target diff: ", target_diff);
        telemetry.addData("target acq: ", target_acquired);
        telemetry.addData("encoder: ", curr_arm_pos);
        telemetry.addData("target: ", arm_encoder_target);
        telemetry.addData("t:", t);
        telemetry.addData("e:", e);
        */

        /*
        if (curr_arm_pos > arm_encoder_target) {
            curr_arm_speed = 0.5;
            curr_arm_speed *= (target_diff / 1000) + 1;
            if (hasGlyph()) {
                curr_arm_speed *= 1.25;
            }
        }
        if (curr_arm_pos < arm_encoder_target) {
            curr_arm_speed = -0.05;
            curr_arm_speed *= (target_diff / 10000) + 1;
            if (target_near) {
                curr_arm_speed = 0.0;
            }
        }

        if (gamepad2.right_stick_button) {
            curr_arm_speed *= 1.4;
        }

        if (target_acquired) {
            curr_arm_speed = 0.15;
        }
        */

        curr_arm_speed =  (e * P) + (t * I) + D;

        telemetry.addData("power: ", curr_arm_speed);
        telemetry.addData("pos: ", curr_arm_pos);

        h.topArm.setPower(curr_arm_speed);
    }

    public void loop() {
        update_arm();
        control_motors();
        set_pows();
        set_servos();
    }
}