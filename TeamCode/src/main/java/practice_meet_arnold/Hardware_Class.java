package practice_meet_arnold;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by robotics10650 on 2/5/2018.
 */

public class Hardware_Class {

    public DcMotor rightMotor, rightBackMotor, leftMotor, leftBackMotor, strafeMotor, topArm;
    public Servo jewelServo, gripperServo;
    public ModernRoboticsI2cColorSensor armSensor;
    public ModernRoboticsI2cRangeSensor glyphSensor;

    HardwareMap hwMap = null;

    public Hardware_Class() {

    }

    private DcMotor get_DC_motor(String id) { return hwMap.get(DcMotor.class, id); }
    private Servo get_servo(String id) { return hwMap.get(Servo.class, id); }

    public void init(HardwareMap ahwMap) {
        hwMap = ahwMap;

        strafeMotor     = get_DC_motor("middle");
        rightMotor      = get_DC_motor("right");
        rightBackMotor  = get_DC_motor("rightB");
        leftMotor       = get_DC_motor("left");
        leftBackMotor   = get_DC_motor("leftB");
        topArm          = get_DC_motor("arm");

        jewelServo   = get_servo("jewel");
        gripperServo = get_servo("leftGrip");

        armSensor = hwMap.get(ModernRoboticsI2cColorSensor.class, "color");
        glyphSensor = hwMap.get(ModernRoboticsI2cRangeSensor.class, "SONIC THE HEDGEHOG");

        leftMotor     .setDirection(DcMotor.Direction.REVERSE);
        rightBackMotor.setDirection(DcMotor.Direction.REVERSE);
        topArm        .setDirection(DcMotor.Direction.REVERSE);
    }
}
