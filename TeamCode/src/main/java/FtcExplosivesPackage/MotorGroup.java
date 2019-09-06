package FtcExplosivesPackage;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by robotics9277 on 11/4/2017.
 */

public class MotorGroup {
    DcMotor[] motors;
    TelemetryLog motorLog;
    OpMode opMode;

    public MotorGroup(OpMode opmode, DcMotor... motors){
        this.motors = motors;
        motorLog = new TelemetryLog(opmode);
        this.opMode = opmode;
    }

    public void setDirection(DcMotor.Direction... directions){
        if(directions.length == motors.length){
            int curr = 0;
            for(DcMotor current : motors){
                current.setDirection(directions[curr]);
                curr++;
            }
        }
        else{
            motorLog.add("Set Reversed has incorrect # of args");
        }
    }

    public void set(double pow){
        for(DcMotor motor : motors){
            motor.setPower(pow);
        }
    }

    public double getPosition(){
        double out = 0;
        for(DcMotor motor: motors){
            out += motor.getCurrentPosition();
        }
        out /= motors.length;
        return out;
    }

    public void reset() throws InterruptedException{
        set(0);
        for(DcMotor motor : motors){
            while(motor.getCurrentPosition() != 0) motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            try{
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}