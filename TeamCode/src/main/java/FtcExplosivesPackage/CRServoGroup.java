package FtcExplosivesPackage;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;

/**
 * Created by robotics9277 on 12/9/2017.
 */

public class CRServoGroup {
    CRServo[] servos;
    TelemetryLog CRServoLog;

    public CRServoGroup(OpMode opmode, CRServo... servos){
        this.servos = servos;
        this.CRServoLog = new TelemetryLog(opmode);
    }

    public void setDirections(CRServo.Direction... directions){
        if(directions.length == servos.length){
            int curr = 0;
            for(CRServo current : servos){
                current.setDirection(directions[curr]);
                curr++;
            }
        } else{
            CRServoLog.add("Set Reversed has incorrect # of args");
        }
    }

    public void set(double pow){
        for(CRServo servo : servos){
            servo.setPower(pow);
        }
    }
}
