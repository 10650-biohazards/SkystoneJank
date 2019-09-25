package DubinsCurve;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

import Competition.Subsystems.DriveSubsystem;
import Utilities.Utility;


/**
 * Version 1.1 of the Dubin's Curve Algorithm
 * Created by Core on 20/08/19
 */

public class curveProcessor3 {

    private myArc firstArc;
    private myArc secondArc;
    private myStraight straight;

    private double targetSlope;

    private double xDiff;
    private double yDiff;

    private final int TIICKSPERTILE = 4071;

    private double lastTrackDist;

    private Telemetry t;

    DriveSubsystem drive;

    Utility u;

    public curveProcessor3(DriveSubsystem drive, Telemetry t, LinearOpMode op) {
        this.drive = drive;
        this.t = t;
        u = new Utility(op);
    }

    public void move(Node start, Node end) {
        findCurves(start, end);

        double currAng = drive.gyro.getYaw();
        if (currAng < 0) {
            currAng += 360;
        }

        //t.addData("Current Angle", currAng);
        //t.addData("Arc Length", firstArc.length);
        //t.addData("Target Angle", currAng + firstArc.length);
        //t.update();
        telemtry(start, end);

        u.waitMS(30000);

        if (firstArc.right) {
            drive.swingTurnPID(currAng + firstArc.length, true);
        } else {
            drive.swingTurnPID(currAng - firstArc.length, false);
        }

        drive.moveStraightPID(straight.length * TIICKSPERTILE);

        if (secondArc.right) {
            drive.swingTurnPID(end.rawAng, true);
        } else {
            drive.swingTurnPID(end.rawAng, false);
        }

        drive.setPows(0, 0, 0, 0);
    }

    public void findCurves(Node start, Node end) {

        double xOffset = start.x;
        double yOffset = start.y;

        start.x -= xOffset;
        start.y -= yOffset;
        end.x   -= xOffset;
        end.y   -= yOffset;

        double angOffset = start.rawAng;
        start.calcAng -= angOffset;
        end.calcAng -= angOffset;
        if (end.calcAng < 0) {
            end.calcAng += 360;
        }

        xDiff = end.x;
        yDiff = end.y;

        double startAng = slopeFinder(xDiff, yDiff);
        double relAng = startAng - start.rawAng;
        if (relAng < 0) {
            relAng += 360;
        }

        double distFromCent = xDiff / Math.sin(Math.toRadians(startAng));

        end.x = Math.sin(Math.toRadians(relAng)) * distFromCent;
        end.y = Math.cos(Math.toRadians(relAng)) * distFromCent;







        firstArc = new myArc(start);
        secondArc = new myArc(end);

        ArrayList<Double> distances = new ArrayList<>();

        RSR(start, end);
        distances.add(lastTrackDist);
        RSL(start, end);
        distances.add(lastTrackDist);
        LSL(start, end);
        distances.add(lastTrackDist);
        LSR(start, end);
        distances.add(lastTrackDist);

        double minDist = Double.MAX_VALUE;
        int minIndex = -1;
        int ticker = 0;
        String[] paths = {"RSR", "RSL", "LSL", "LSR"};
        for (double distance : distances) {
            //t.addData("Distance:" + distance);
            if (distance < minDist) {
                minIndex = ticker;
                minDist = distance;
            }
            ticker++;
        }

        if (paths[minIndex].equals("RSR")) {
            //t.addData("RIGHT STRAIGHT RIGHT");
            RSR(start, end);
        }
        if (paths[minIndex].equals("RSL")) {
            //t.addData("RIGHT STRAIGHT LEFT");
            RSL(start, end);
        }
        if (paths[minIndex].equals("LSL")) {
            //t.addData("LEFT STRAIGHT LEFT");
            LSL(start, end);
        }
        if (paths[minIndex].equals("LSR")) {
            //t.addData("LEFT STRAIGHT RIGHT");
            LSR(start, end);
        }

        telemtry(start, end);
    }

    private double findDist(myPoint point1, myPoint point2) {
        return Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }

    private void RSR(Node start, Node end) {

        firstArc = new myArc(start);
        secondArc = new myArc(end);

        firstArc.setDirection(true);
        secondArc.setDirection(true);

        //NEW STUFF
        xDiff = secondArc.findCenter().x - firstArc.findCenter().x;
        yDiff = secondArc.findCenter().y - firstArc.findCenter().y;
        targetSlope = slopeFinder(xDiff, yDiff);

        firstArc.setLength(targetSlope - start.calcAng);

        double temp = end.calcAng - targetSlope;
        if (temp < 0) {
            temp += 360;
        }
        secondArc.setLength(temp);

        double dist = findDist(firstArc.findCenter(), secondArc.findCenter());

        straight = new myStraight(dist);

        totalDist();
    }

    private void RSL(Node start, Node end) {

        firstArc = new myArc(start);
        secondArc = new myArc(end);

        firstArc.setDirection(true);
        secondArc.setDirection(false);

        xDiff = secondArc.findCenter().x - firstArc.findCenter().x;
        yDiff = secondArc.findCenter().y - firstArc.findCenter().y;
        double centerSlope = slopeFinder(xDiff, yDiff);

        double hyp = findDist(firstArc.findCenter(), secondArc.findCenter()) / 2;
        double oppSide = myArc.RADIUS;
        double diffSlope = Math.toDegrees(Math.asin(oppSide/hyp));

        targetSlope = simplifyAng(centerSlope + diffSlope);

        firstArc.setLength(targetSlope - start.calcAng);


        double temp = targetSlope - end.calcAng;
        if (temp < 0) {
            temp += 360;
        }
        secondArc.setLength(temp);

        double dist = 2 * hyp * Math.cos(Math.toRadians(diffSlope));

        straight = new myStraight(dist);

        totalDist();
    }

    private void LSL(Node start, Node end) {

        firstArc = new myArc(start);
        secondArc = new myArc(end);

        firstArc.setDirection(false);
        secondArc.setDirection(false);



        //NEW STUFF
        xDiff = secondArc.findCenter().x - firstArc.findCenter().x;
        yDiff = secondArc.findCenter().y - firstArc.findCenter().y;
        targetSlope = slopeFinder(xDiff, yDiff);

        firstArc.setLength(360 - targetSlope);

        double temp = targetSlope - end.calcAng;
        if (temp < 0) {
            temp += 360;
        }
        secondArc.setLength(temp);

        double dist = findDist(firstArc.findCenter(), secondArc.findCenter());

        straight = new myStraight(dist);

        totalDist();
    }

    private void LSR(Node start, Node end) {

        firstArc = new myArc(start);
        secondArc = new myArc(end);

        firstArc.setDirection(false);
        secondArc.setDirection(true);

        xDiff = secondArc.findCenter().x - firstArc.findCenter().x;
        yDiff = secondArc.findCenter().y - firstArc.findCenter().y;
        double centerSlope = slopeFinder(xDiff, yDiff);

        double hyp = findDist(firstArc.findCenter(), secondArc.findCenter()) / 2;
        double oppSide = myArc.RADIUS;
        double diffSlope = Math.toDegrees(Math.asin(oppSide/hyp));

        targetSlope = centerSlope - diffSlope;

        firstArc.setLength(360 - targetSlope);


        double temp = end.calcAng - targetSlope;
        if (temp < 0) {
            temp += 360;
        }
        secondArc.setLength(temp);

        double dist = 2 * hyp * Math.cos(Math.toRadians(diffSlope));

        straight = new myStraight(dist);

        totalDist();
    }

    public void telemtry(Node start, Node end) {
        t.addData("", "");
        t.addData("////Data////", "");
        t.addData("Slope", targetSlope);
        t.addData("Line Length", straight.length);
        t.addData("", "");
        t.addData("////First Arc////", "");
        t.addData("Length", firstArc.length);
        String str = "Start Point: (" + start.x + ", " + start.y + ")";
        t.addData(str, "");
        str = "End Point: (" + firstArc.fin().x + ", " + firstArc.fin().y + ")";
        t.addData(str, "");
        t.addData("Direction", firstArc.right);
        str = "Center: (" + firstArc.findCenter().x + ", " + firstArc.findCenter().y + ")";
        t.addData(str, "");
        t.addData("", "");
        t.addData("////Second Arc////", "");
        t.addData("Length", secondArc.length);
        t.addData("Direction", secondArc.right);
        str = "Center: (" + secondArc.findCenter().x + ", " + secondArc.findCenter().y + ")";
        t.addData(str, "");
        t.addData("", "");
        //start.print("START");
        //end.print("END");
        t.addData("Total Distance Travelled", lastTrackDist);
    }

    private void totalDist() {
        lastTrackDist = firstArc.findDistance() + straight.length + secondArc.findDistance();
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

    private double simplifyAng(double input) {
        if (input > 360) {
            return input - 360;
        } else {
            return input;
        }
    }
}