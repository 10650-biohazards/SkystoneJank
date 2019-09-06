package FtcExplosivesPackage;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class BioSubsystem {
    private boolean init = false;
    TelemetryLog subErrorLog;

    public BioSubsystem(LinearOpMode op){
        subErrorLog = new TelemetryLog(op);
    }

    public abstract void enable();

    public abstract void disable();

    public abstract void stop();

    public ArrayList<DcMotor> experimentalGetAllMotors() {
        ArrayList<DcMotor> motors = new ArrayList<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getType().isAssignableFrom(DcMotor.class)) {
                try {
                    motors.add((DcMotor) f.get(this));
                } catch (Exception e) {
                    subErrorLog.add("Experimental get all motors failed");
                }
            }
        }
        return motors;
    }

    public void experimentalCloseAllDevices() {
        Field[] fields = getClass().getDeclaredFields();
        HardwareDevice d;
        for (Field f : fields) {
            if (f.getType().isAssignableFrom(HardwareDevice.class)) {
                try {
                    d = (HardwareDevice) f.get(this);
                    d.close();
                } catch (Exception e) {
                    subErrorLog.add("Experimental close all hardware failed");
                }
            }
        }
    }
}
