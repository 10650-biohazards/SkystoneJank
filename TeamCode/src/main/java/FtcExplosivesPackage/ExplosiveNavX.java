package FtcExplosivesPackage;

import com.qualcomm.hardware.kauailabs.NavxMicroNavigationSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

/**
 * Created by robotics9277 on 12/15/2017.
 */

public class ExplosiveNavX implements ExplosivePIDEnabledHardware {
    private static ExplosiveNavX instance = null;
    private NavxMicroNavigationSensor imu = null;
    public double startAng, startPitch;

    public ExplosiveNavX(OpMode opMode, String name, double startAng){
        imu = opMode.hardwareMap.get(NavxMicroNavigationSensor.class, name);
        this.startAng = startAng;
    }

    public static ExplosiveNavX getInstance(OpMode opmode, String name){
        if(instance == null){
            instance = new ExplosiveNavX(opmode, name, 0);
        }
        return instance;
    }

    public double getYaw(){
        try{
            return startAng - imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
        } catch(Exception e){
            android.util.Log.d("Robot", "GYRO ERROR: " + e.getMessage());
            return getYaw();
        }
    }

    public double getPitch() {
        try{
            return startPitch - (imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).secondAngle * -1);
        } catch (Exception e) {
            android.util.Log.d("Robot", "GYRO ERROR: " + e.getMessage());
            return getPitch();
        }
    }

    public void resetPitch() {
        startPitch = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).secondAngle;
    }

    public double getRaw() {
        return -imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }

    @Override
    public void close(){
        imu = null;
        instance = null;
    }

    @Override
    public double getLatency(){
        return (System.nanoTime() - imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).acquisitionTime)/1000000;
    }

    public double getUpdatedYaw(){
        while((System.nanoTime() - imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).acquisitionTime)/1000000 > 5){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }

    @Override
    public double getOutput() {
        return getYaw();
    }
}