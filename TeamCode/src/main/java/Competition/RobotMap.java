package Competition;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import FtcExplosivesPackage.BiohazardNavX;

public class RobotMap {
    public static DcMotor bright, fright, bleft, fleft;
    public static BiohazardNavX gyro;

    public RobotMap(HardwareMap hw) {
        bright = hw.get(DcMotor.class, "bright");
        fright = hw.get(DcMotor.class, "fright");
        bleft = hw.get(DcMotor.class, "bleft");
        fleft = hw.get(DcMotor.class, "fleft");

        gyro = new BiohazardNavX(hw, "navX", 0);
    }

}
