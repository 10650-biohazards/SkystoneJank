package practice_meet_arnold;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Arnold_Hardware {
    public DcMotor rightBackMotor, leftBackMotor,topArm;
    public Servo jewelServo, gripperServo;
    public ModernRoboticsI2cColorSensor armSensor;
    public ModernRoboticsI2cRangeSensor glyphSensor;

    HardwareMap hwMap = null;

    public Arnold_Hardware() {}

    private DcMotor get_DC_motor(String id) { return hwMap.get(DcMotor.class, id); }
    private Servo get_servo(String id) { return hwMap.get(Servo.class, id); }

    public void init(HardwareMap ahwMap) {
        hwMap = ahwMap;

        rightBackMotor  = get_DC_motor("rightB");
        leftBackMotor   = get_DC_motor("leftB");
        topArm          = get_DC_motor("arm");
        rightBackMotor.setDirection(DcMotor.Direction.REVERSE);
        topArm        .setDirection(DcMotor.Direction.REVERSE);

        jewelServo   = get_servo("jewel");
        gripperServo = get_servo("leftGrip");

        armSensor = hwMap.get(ModernRoboticsI2cColorSensor.class, "color");
        glyphSensor = hwMap.get(ModernRoboticsI2cRangeSensor.class, "SONIC THE HEDGEHOG");
    }
}