package FtcExplosivesPackage;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 *   Created by Aidan 9/14/19
 */

public class ToxinFieldBasedControl{


    public static class Point{
        public double x;
        public double y;
        public Point(double X, double Y){
            x = X;
            y = Y;
        }
        public Point(){
            x = 0;
            y = 0;
        }

    }

    public static Point Rotate2D(Point point, float angle){
        double angleRad = Math.toRadians(angle);
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        return new Point(point.x * cos - point.y * sin,point.x * sin + point.y * cos);

    }

    public static Point getLeftJoystick(Gamepad driver, BiohazardNavX gyro){
        Point stick  = new Point(driver.left_stick_x, driver.left_stick_y);

        float gyroAngle = (float)gyro.getYaw();

        return Rotate2D(stick, -gyroAngle);

    }

    public static Point getRightJoystick(Gamepad driver, BiohazardNavX gyro){
        Point stick = new Point(driver.right_stick_x, driver.right_stick_y);

        float gyroAngle = (float)gyro.getYaw();

        return Rotate2D(stick, -gyroAngle);



    }
}
