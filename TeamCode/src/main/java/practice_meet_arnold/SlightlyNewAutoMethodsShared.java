package practice_meet_arnold;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

public class SlightlyNewAutoMethodsShared {

    Hardware_Class r = new Hardware_Class();
    
    /**
     * Declares all of our variables, motors, servos, and sensors.
     */
    private LinearOpMode opMode = null;

    private VuforiaLocalizer vuforia = null;

    private HardwareMap hardwareMap = null;

    private double target_diff = 0.0,
            curr_arm_pos = 0.0,
            arm_speed = 0.0;

    private boolean target_acquired = false,
            target_near = false;

    private final int STANDARD_WAIT_MS = 100,
                      AUTO_TOTAL_SECONDS = 30;

    private int attemptNumber = 0,
                JEWEL_TURN = 60;

    private double  leftTargetEnc,
                    middleTargetEnc,
                    rightTargetEnc,
                    rightEnc,
                    leftEnc,
                    middleEnc,
                    rightPow,
                    leftPow,
                    middlePow,
                    targetTime,
                    ultraDist;

    private boolean anytargetacquired = false,
                    turning = false,
                    strafing = false,
                    timerDone = false,
                    timerActive = false;


    /**
     * Constructor method.
     * @param _opMode Allows the opmode to access traditionally LinearOpMode functions.
     */
    public SlightlyNewAutoMethodsShared(LinearOpMode _opMode) {
        opMode = _opMode;
        hardwareMap = opMode.hardwareMap;
    }

    /**
     * Locates all motors, sensors, and servos on the robot and reverses the left motor, right back motor,
     * and top arm. Uses the get_DC_motor and get_servo methods to make things shorter.
     */
    public void setup() {
        r.init(hardwareMap);
    }

    /**
     * Makes the robot wait for a certain time. Doesn't allow any other functions to run simultaneously.
     * @param milliseconds: Desired amount of time.
     */
    public void wait_MS(double milliseconds) {
        targetTime = System.nanoTime() + (1E6 * milliseconds);
        r.leftMotor.setPower(0);
        r.leftBackMotor.setPower(0);
        r.rightMotor.setPower(0);
        r.rightBackMotor.setPower(0);
        r.strafeMotor.setPower(0);
        while (System.nanoTime() < targetTime && !opMode.isStopRequested()) {}
    }

    /**
     * Uses the wait_MS method to wait a standard amount of time.
     */
    public void standard_wait() {
        wait_MS(STANDARD_WAIT_MS);
    }

    /**
     * Very similar to wait_MS, but it allows other methods and motors to run at the same time.
     * @param milliseconds Desired amoutn of time for the timer.
     */
    private void start_timer(double milliseconds) {
        targetTime = System.nanoTime() + (1E6 * milliseconds);
        timerDone = false;
    }

    /**
     * Checks to see whether or not the timer is done.
     */
    private void update_timer_booleans() {
        final boolean ACTIVE = System.nanoTime() < targetTime && !opMode.isStopRequested();
        timerDone = !ACTIVE;
        timerActive = ACTIVE;
    }

    /**
     * Returns the time left in seconds in the autonomous period.
     * */
    public int get_time_left() {
        return AUTO_TOTAL_SECONDS - (int) opMode.getRuntime();
    }

    /**
     * Moves the gripper to whatever position we want.
     * @param target_pos: The desired position of the gripper.
     */
    public void move_gripper(double target_pos) {
        r.gripperServo.setPosition(target_pos);
        wait_MS(200);
    }

    /**
     * Moves the jewel servo to whatever position we desire.
     * @param target_pos: The target position of the jewel servo.
     */
    public void move_jewel_servo(double target_pos) {
        r.jewelServo.setPosition(target_pos);
        wait_MS(200);
    }

    /**
     * Detects whether a glyph is within the gripper.
     * @return Returns true if a glyph is within three inches of the ultrasonic
     * sensor at the back of the gripper.
     */
    public boolean hasGlyph() {
        ultraDist = r.glyphSensor.getDistance(DistanceUnit.INCH);
        return ultraDist < 3;
    }

    /**
     * As the arm gets closer to the target encoder ticks it will slow down depending on how close the arm is to the target.
     * the arm speed value when the arm is going up is higher than when it is lowering because it has to go against gravity.
     * We also set the target aquired speed to 0.15 which is the exact speed that will counteract gravity and keep the arm
     * level.
     * @param target_arm_encoder_value Whatever encoder value we desire the arm to be at.
     * @param hasGlyph Allows us to determine manually whether or not the robot has a glyph in the gripper.
     */
    public void move_arm(double target_arm_encoder_value, boolean hasGlyph) {
        while (!opMode.isStopRequested() && !target_acquired && get_time_left() > 3) {
            curr_arm_pos = r.topArm.getCurrentPosition();

            target_diff = Math.abs(curr_arm_pos - target_arm_encoder_value);
            target_acquired = target_diff <= 10;
            target_near = target_diff <= 500;

            if (curr_arm_pos > target_arm_encoder_value) {
                arm_speed = 0.5;
                arm_speed *= (target_diff / 1000) + 1;
                if (hasGlyph()) {
                    arm_speed *= 1.25;
                }
            }
            if (curr_arm_pos < target_arm_encoder_value) {
                arm_speed = -0.05;
                arm_speed *= (target_diff / 10000) + 1;
                if (target_near) {
                    arm_speed = 0.0;
                }
            }

            if (target_acquired) {
                arm_speed = 0.15;
            }
            r.topArm.setPower(arm_speed);
        }
        standard_wait();
    }

    /**
     * Uses a variety of variables to move the robot along a certain distance.
     * @param move_power The desired power for the motors to move at.
     * @param encoder_ticks The target number of encoder ticks the robot need to turn.
     * @param catch_time The desired time until the safety activates and the program moves on.
     */
    public void move_straight_with_catch_time(double move_power, double encoder_ticks, int catch_time) {

        turning  = false;
        strafing = false;

        middlePow = 0;
        rightPow = 0;
        leftPow = 0;

        update_motors();
        rightTargetEnc = rightEnc + encoder_ticks;
        leftTargetEnc = leftEnc + encoder_ticks;
        start_timer(catch_time);

        do {
            update_timer_booleans();
            //move_right_motor(move_power);
            move_left_motor(move_power);
            update_motors();
        } while (!targets_acquired(rightTargetEnc, leftTargetEnc, encoder_ticks) && !opMode.isStopRequested() && !timerDone);

        standard_wait();
    }

    /**
     * Moves the robot forward at a set power for a certain number of encoder ticks
     * @param move_power is the speed of the robot
     * @param encoder_ticks is the distance the robot moves
     */
    public void move_straight(double move_power, double encoder_ticks) {
        move_straight_with_catch_time(move_power, encoder_ticks, 3000);
    }

    /**
     * Moves the robot side to side.
     * @param move_power The desired power for the strafe motor.
     * @param tiles The desired distance sideways for the robot to move.
     */
    public void move_strafe(double move_power, double tiles) {

        turning  = false;
        strafing = true;

        middlePow = 0;
        rightPow = 0;
        leftPow = 0;

        update_motors();
        middleTargetEnc = middleEnc + tiles;

        do {
            move_strafe_motor(move_power);
            update_motors();
        } while (!targets_acquired(middleTargetEnc, 0, tiles) && !opMode.isStopRequested());

        standard_wait();

    }

    /**
     * Allows the robot to turn.
     * @param turn_power The power the motors are intended to run at.
     * @param encoder_ticks How far we want to turn.
     */
    public void move_turn(double turn_power, double encoder_ticks) {
        turning  = true;
        strafing = false;

        middlePow = 0;
        rightPow = 0;
        leftPow = 0;

        update_motors();
        rightTargetEnc = rightEnc - encoder_ticks;
        leftTargetEnc  = leftEnc  + encoder_ticks;

        start_timer(3000);

        do {
            update_timer_booleans();
            move_left_motor(turn_power);
            move_right_motor(turn_power);
            update_motors();
        } while (!targets_acquired(rightTargetEnc, leftTargetEnc, encoder_ticks) && !opMode.isStopRequested() && !timerDone);

        standard_wait();
    }

    /**
     * Calls a series of methods grabs the robot from the Glyph Pit.
     */
    public void grabGlyph() {

        final double TURN_POW = 0.5,
                     MOVE_POW = 0.5;
        final int STRAIGHT_MOVE_AMT = 600;
        int turn_amt = 0;

        standard_wait();
        attemptNumber = 0;

        do {
            move_gripper(0.3);
            if (attemptNumber == 0) {      turn_amt = 0;    }
            else if (attemptNumber == 1) { turn_amt = 100;  }
            else if (attemptNumber == 2) { turn_amt = -150; }
            move_turn(TURN_POW, turn_amt);
            move_straight(MOVE_POW, STRAIGHT_MOVE_AMT);
            move_gripper(0.12);
            move_straight(MOVE_POW, (100-STRAIGHT_MOVE_AMT));
            attemptNumber += 1;
        } while (!hasGlyph() && attemptNumber < 3 && get_time_left() > 10);

        if (attemptNumber == 1) {      turn_amt = 0;    }
        else if (attemptNumber == 2) { turn_amt = -100; }
        else if (attemptNumber == 3) { turn_amt = 50; }
        move_turn(TURN_POW, turn_amt);

        standard_wait();

    }

    /**
     * A sequence of events which knocks off the correct jewel.
     * @param red Whether or not we are on the red alliance.
     */
    public void move_jewel(boolean red) {
        r.jewelServo.setPosition(0.6);
        wait_MS(1000);
        r.jewelServo.setPosition(0.7);
        wait_MS(500);
        boolean seesRed = r.armSensor.red() > 0;
        boolean seesBlue = r.armSensor.blue() > 0;
        if (red) {
            if (seesRed) {
                JEWEL_TURN *= -1;
            }
        } else {
            if (seesBlue) {
                JEWEL_TURN *= -1;
            }
        }
        if (r.armSensor.red() == 225) {
            JEWEL_TURN = 0;
        }
        move_turn(0.15, JEWEL_TURN);
        r.jewelServo.setPosition(0.0);
        move_turn(0.15, -JEWEL_TURN);
        standard_wait();
    }

    /**
     * Updates a series of booleans which determine whether we have hit the target distance.
     * @param righttarget Target encoder value for the right motor.
     * @param lefttarget Target encoder value for the left motor.
     * @param distance The distance we seek to move.
     */
    public boolean targets_acquired(double righttarget, double lefttarget, double distance) {
        anytargetacquired = false;

        if (turning) {
            if (distance > 0) {
                anytargetacquired = rightEnc < righttarget;
                anytargetacquired = leftEnc > lefttarget;
            } else {
                anytargetacquired = rightEnc > righttarget;
                anytargetacquired = leftEnc < lefttarget;
            }
        } else if (strafing) {
            if (distance > 0) {
                anytargetacquired = middleEnc > righttarget;
            } else {
                anytargetacquired = middleEnc < righttarget;
            }
        } else {
            if (distance > 0) {
                anytargetacquired = rightEnc > righttarget;
                anytargetacquired = leftEnc > lefttarget;
            } else {
                anytargetacquired = rightEnc < righttarget;
                anytargetacquired = leftEnc < lefttarget;
            }
        }
        return anytargetacquired;
    }

    /**
     * Refreshes data on the motors and resets their power.
     */
    public void update_motors() {
        rightEnc = -r.rightMotor.getCurrentPosition();
        leftEnc = -r.leftMotor.getCurrentPosition();
        middleEnc = r.strafeMotor.getCurrentPosition();

        r.strafeMotor.    setPower(middlePow);
        r.rightMotor.     setPower(rightPow);
        r.rightBackMotor. setPower(rightPow);
        r.leftMotor.      setPower(leftPow);
        r.leftBackMotor.  setPower(leftPow);
    }

    /**
     * Sets the power for the motor to be positive or negative.
     * @param power Desired motor power.
     */
    public void move_right_motor(double power) {
        if (rightEnc > rightTargetEnc) {
            rightPow = power * -1;
        } else {
            rightPow = power;
        }
    }

    /**
     * Sets the power for the motor to be positive or negative.
     * @param power Desired motor power.
     */
    public void move_left_motor(double power){
        if (leftEnc > leftTargetEnc) {
            leftPow = power * -1;
            rightPow = power * -1;
        } else {
            leftPow = power;
            rightPow = power;
        }
    }

    /**
     * Sets the power for the motor to be positive or negative.
     * @param power Desired motor power.
     */
    public void move_strafe_motor(double power){
        if (middleEnc > middleTargetEnc) {
            middlePow = power * -1;
        } else {
            middlePow = power;
        }
    }
}