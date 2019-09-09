package Competition;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import FtcExplosivesPackage.BiohazardNavX;

public class RobotMap {
    public static DcMotor bright, fright, bleft, fleft, intLeft, intRight;
    public static BiohazardNavX gyro;
    public static WebcamName stoneCam;

    public RobotMap(HardwareMap hw) {
        bright = hw.get(DcMotor.class, "bright");
        fright = hw.get(DcMotor.class, "fright");
        bleft = hw.get(DcMotor.class, "bleft");
        fleft = hw.get(DcMotor.class, "fleft");

        intLeft = hw.get(DcMotor.class, "intLeft");
        intRight = hw.get(DcMotor.class, "intRight");

        gyro = new BiohazardNavX(hw, "navX", 0);

        stoneCam = hw.get(WebcamName.class, "stoned cam");
    }

}
