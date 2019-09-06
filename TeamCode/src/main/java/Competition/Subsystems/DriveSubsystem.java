package Competition.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import Competition.RobotMap;
import DubinsCurve.Node;
import DubinsCurve.myPoint;
import FtcExplosivesPackage.BioSubsystem;
import FtcExplosivesPackage.BiohazardNavX;
import Utilities.PID;
import Utilities.Utility;
import Utilities.driveTracker2;

public class DriveSubsystem extends BioSubsystem {

    public DcMotor bright, fright, bleft, fleft;
    Utility u;
    LinearOpMode op;
    public BiohazardNavX gyro;

    driveTracker2 track;

    public DriveSubsystem(LinearOpMode op) {
        super(op);
        u = new Utility(op);
        this.op = op;
    }

    public void setTracker(driveTracker2 track) {
        this.track = track;
    }

    public void straightToPoint(myPoint point) {

        Node currentNode = track.getCurrentNode();

        double xDiff = point.x - currentNode.x;
        double yDiff = point.y - currentNode.y;

        double angle = slopeFinder(xDiff, yDiff);

        moveTurnPID(angle);

        double dist = findDist(point, currentNode.coords());
        moveStraightPID(dist);
    }

    public void moveStraightPID(double targDist, int stopTime) {
        PID movePID = new PID();
        movePID.setup(0.0002, 0, 0, 0.2, 20,bright.getCurrentPosition() + targDist);

        u.startTimer(stopTime);

        while (!u.timerDone() && !movePID.done()) {
            double power = movePID.status(bright.getCurrentPosition());
            setPows(power, power, power, power);

            track.refresh();

            if (!op.opModeIsActive()) return;
        }
        setPows(0, 0, 0, 0);
        u.waitMS(200);
    }
    public void moveStraightPID(double targDist) {moveStraightPID(targDist, 3000);}


    public void moveTurnPID(double targetAng) {
        PID movePID = new PID();
        movePID.setup(0.1, 0, 0, 0.07, 0.25,targetAng);

        u.startTimer(5000);

        while (!u.timerDone() && !movePID.done()) {
            double power = movePID.status(gyro.getYaw());
            setPows(power, power, -power, -power);

            track.refresh();

            if (!op.opModeIsActive()) return;
        }
        setPows(0, 0, 0, 0);
        u.waitMS(200);
    }

    public void swingTurnPID(double targetAng, boolean right) {
        PID breakPIDF = new PID();
        PID breakPIDB = new PID();
        breakPIDF.setup(0.05, 0, 0, 0, 0, 0);
        breakPIDB.setup(0.05, 0, 0, 0, 0, 0);

        if (right) {
            breakPIDB.setTarget(bright.getCurrentPosition());
            breakPIDF.setTarget(fright.getCurrentPosition());
        } else {
            breakPIDB.setTarget(bleft.getCurrentPosition());
            breakPIDF.setTarget(fleft.getCurrentPosition());
        }

        PID movePID = new PID();
        movePID.setup(0.0002, 0, 0, 0.2, 20,targetAng);

        u.startTimer(5000);

        while (!u.timerDone() && !movePID.done()) {
            double currAng = gyro.getYaw();
            if (currAng < 0) {
                currAng += 360;
            }


            double power = movePID.status(currAng);

            setPows(power, power, -power, -power);

            track.refresh();

            if (!op.opModeIsActive()) return;
        }
        setPows(0, 0, 0, 0);
        u.waitMS(200);
    }



    public void setPows(double brp, double frp, double blp, double flp) {
        bright.setPower(brp);
        fright.setPower(frp);
        bleft.setPower(blp);
        fleft.setPower(flp);
    }

    @Override
    public void enable() {
        bright = RobotMap.bright;
        fright = RobotMap.fright;
        bleft = RobotMap.bleft;
        fleft = RobotMap.fleft;

        gyro = RobotMap.gyro;
    }

    @Override
    public void disable() {

    }

    @Override
    public void stop() {

    }

    private double findDist(myPoint point1, myPoint point2) {
        return Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }

    private double slopeFinder(double xDiff, double yDiff) {
        double raw = Math.atan(yDiff/xDiff);
        if (xDiff >= 0 && yDiff >= 0) {
            raw *= -1;
            raw += Math.PI / 2;
            return Math.toDegrees(raw);
        } else if (xDiff >= 0 && yDiff <= 0) {
            raw *= -1;
            raw += Math.PI / 2;
            return Math.toDegrees(raw);
        } else if (xDiff <= 0 && yDiff <= 0) {
            raw *= -1;
            raw += 3 * (Math.PI / 2);
            return Math.toDegrees(raw);
        } else if (xDiff <= 0 && yDiff >= 0) {
            raw *= -1;
            raw += 3 * (Math.PI / 2);
            return Math.toDegrees(raw);
        }
        return 42;
    }
}
