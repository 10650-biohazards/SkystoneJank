package Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Utility {

    LinearOpMode op;
    public long startTime;
    int millis;

    public Utility(LinearOpMode op) {
        this.op = op;
    }

    public void startTimer(int millis) {
        this.millis = millis;
        startTime = System.currentTimeMillis();
    }

    public boolean timerDone() {
        return startTime + millis < System.currentTimeMillis();
    }

    public void waitMS(int millis) {
        long startTime = System.currentTimeMillis();

        while (startTime + millis >= System.currentTimeMillis()){
            if (!op.opModeIsActive()) {
                return;
            }
        }
    }

    public double limit(double maVal, double miVal, double input) {
        return input > maVal ? maVal : input < miVal ? miVal : input;
    }
}