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

    public driveTracker2 track;

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
        PID modPID = new PID();

        double target = -bright.getCurrentPosition() - targDist;
        double targAng = refine(gyro.getYaw());

        movePID.setup(0.00015, 0, 0, 0.2, 20, target);
        //modPID.setup(0.02, 0, 0, 0, 0, targAng);

        u.startTimer(stopTime);

        while (!u.timerDone() && !movePID.done()) {

            //double mod = modPID.status(refine(gyro.getYaw()));
            double mod = 0;
            double power = -movePID.status(bright.getCurrentPosition());

            op.telemetry.addData("POWER", power);
            op.telemetry.addData("br", bright.getCurrentPosition());
            op.telemetry.addData("fr", fright.getCurrentPosition());
            op.telemetry.addData("bl", bleft.getCurrentPosition());
            op.telemetry.addData("fl", fleft.getCurrentPosition());
            op.telemetry.addData("Target", target);
            op.telemetry.addData("TargAng", targAng);
            op.telemetry.update();

            setPows(power - mod, power - mod, power + mod, power + mod);

            track.refresh();

            if (!op.opModeIsActive()) return;
        }
        setPows(0, 0, 0, 0);
        u.waitMS(5000);
    }
    public void moveStraightPID(double targDist) {moveStraightPID(targDist, 3000);}

    public void moveStrafePID(double targDist, int stopTime) {
        PID movePID = new PID();
        movePID.setup(0.0002, 0, 0, 0.2, 20,bright.getCurrentPosition() + targDist);

        u.startTimer(stopTime);

        while (!u.timerDone() && !movePID.done()) {
            double power = movePID.status(bright.getCurrentPosition());
            setPows(power, -power, -power, power);

            track.refresh();

            if (!op.opModeIsActive()) return;
        }
        setPows(0, 0, 0, 0);
        u.waitMS(500);
    }

    public void moveStraightRaw(double targDist) {

        double target = bright.getCurrentPosition() + targDist;
        double initDiff = targDist - bright.getCurrentPosition();

        boolean forward = !(initDiff < 0);

        boolean done = false;
        while (!done) {
            if (forward) {
                setPows(1, 1, 1, 1);
                done = bright.getCurrentPosition() > target;
            } else

            track.refresh();

            if (!op.opModeIsActive()) return;
        }
        setPows(0, 0, 0, 0);
        u.waitMS(200);
    }


    public void moveTurnPID(double targetAng) {
        double mod = 0;

        double curr = refine(gyro.getYaw());
        if (Math.abs(targetAng - curr) > 180) {
            if (curr > 180) {
                mod = 360 - curr + 5;
            } else {
                mod = 360 - targetAng + 5;
            }
        }

        PID movePID = new PID();
        movePID.setup(0.005, 0, 0, 0.1, 0.25,refine(targetAng + mod));

        op.telemetry.addData("mod", mod);
        op.telemetry.addData("raw ang", gyro.getYaw());
        op.telemetry.addData("refined", refine(gyro.getYaw()));
        op.telemetry.addData("Raw Target", targetAng);
        op.telemetry.addData("modded ang", refine(gyro.getYaw() + mod));
        op.telemetry.addData("Modded Target", refine(targetAng + mod));
        op.telemetry.update();

        //u.waitMS(10000);

        u.startTimer(5000);

        while (!u.timerDone() && !movePID.done()) {
            double currAng = refine(gyro.getYaw() + mod);

            double power = movePID.status(currAng);
            setPows(-power, -power, power, power);

            op.telemetry.addData("mod", mod);
            op.telemetry.addData("Power", power);
            op.telemetry.addData("working", currAng);
            op.telemetry.addData("refined", refine(gyro.getYaw()));
            op.telemetry.addData("Raw Target", targetAng);
            op.telemetry.addData("Modded Target", refine(targetAng + mod));
            op.telemetry.update();

            track.refresh();

            if (!op.opModeIsActive()) return;
        }
        setPows(0, 0, 0, 0);
        u.waitMS(200);
    }

    public void swingTurnPID(double targetAng, boolean right) {
        double mod = 0;

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

        double curr = refine(gyro.getYaw());
        if (targetAng - curr < 0 && right) {
            mod = 360 - curr + 5;
        }
        if (refine(gyro.getYaw()) - targetAng < 0 && !right) {
            mod = 360 - targetAng + 5;
        }

        PID movePID = new PID();
        movePID.setup(0.1, 0, 0, 0.2, 0.2, refine(targetAng + mod));

        u.startTimer(30000);

        while (!u.timerDone() && !movePID.done()) {
            double currAng = refine(gyro.getYaw() + mod);

            double power = movePID.status(currAng);

            op.telemetry.addData("mod", mod);
            op.telemetry.addData("Power", power);
            op.telemetry.addData("working", currAng);
            op.telemetry.addData("refined", refine(gyro.getYaw()));
            op.telemetry.addData("Raw Target", targetAng);
            op.telemetry.addData("Modded Target", refine(targetAng + mod));
            op.telemetry.update();

            setPows(power, power, -power, -power);

            track.refresh();

            if (!op.opModeIsActive()) return;
        }
        setPows(0, 0, 0, 0);
        u.waitMS(200);
    }

    public double refine(double input) {
        input %= 360;
        if (input < 0) {
            input =+ 360;
        }
        return input;
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
