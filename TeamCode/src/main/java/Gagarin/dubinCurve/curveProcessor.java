package Gagarin.dubinCurve;

import DubinsCurve.Node;
import DubinsCurve.myArc;
import DubinsCurve.myPoint;
import DubinsCurve.myStraight;
import Gagarin.Subsystems.DriveSubsystem;

/**
 * Version 1.0 of the Dubin's Curve Algorithm
 */

public class curveProcessor {

    private myArc firstArc;
    private myArc secondArc;
    private myStraight straight;

    private double slope;

    private double xDiff;
    private double yDiff;

    private direction first, second;

    DriveSubsystem drive;

    private final int TIICKSPERTILE = 1075;

    public curveProcessor(DriveSubsystem drive) {
        this.drive = drive;
    }

    public void move(Node start, Node end) {
        findCurves(start, end);

        if (firstArc.right) {
            drive.swing_turn_PID(drive.gyro.getYaw() + Math.toDegrees(firstArc.length), true);
        } else {
            drive.swing_turn_PID(drive.gyro.getYaw() - Math.toDegrees(firstArc.length), false);
        }

        drive.move_straight_raw(straight.length * TIICKSPERTILE);

        if (secondArc.right) {
            drive.swing_turn_PID(end.rawAng, true);
        } else {
            drive.swing_turn_PID(end.rawAng, false);
        }

        drive.set_Pows(0, 0, 0, 0);
    }

    public void findCurves(Node start, Node end) {

        double xMod = start.x;
        double yMod = start.y;

        start.x -= xMod;
        start.y -= yMod;

        end.x -= xMod;
        end.y -= yMod;

        double endRad = Math.sqrt(Math.pow(end.x, 2) + Math.pow(end.y, 2));
        double relAng;

        if (end.x < 0 && end.y < 0) {
            relAng = -90 - Math.toDegrees(Math.atan(end.y/end.x));
        } else if (end.x < 0 && end.y > 0) {
            relAng = -90 - Math.toDegrees(Math.atan(end.y/end.x));
        } else if (end.x > 0 && end.y > 0) {
            relAng = 90 - Math.toDegrees(Math.atan(end.y/end.x));
        } else if (end.x > 0 && end.y < 0) {
            relAng = 90 - Math.toDegrees(Math.atan(end.y/end.x));
        } else {
            relAng = 0;
        }

        System.out.println("Pre-Relative Ang: " + relAng);

        double angMod = start.rawAng;
        start.calcAng -= angMod;
        relAng -= angMod;

        end.x = endRad * Math.sin(Math.toRadians(relAng));
        end.y = endRad * Math.cos(Math.toRadians(relAng));

        System.out.println("Dist: " + endRad);
        System.out.println("Angle Mod: " + angMod);
        System.out.println("Relative Ang: " + relAng);

        end.calcAng -= angMod;

        firstArc = new myArc(start);
        secondArc = new myArc(end);

        double m = Math.tan(Math.toRadians(90 - end.ang));
        double b = end.y - (m * end.x);

        double closeX = (((2 * firstArc.RADIUS) - (2 * m * b)) / (2 + (2 * m * m)));
        double closeY = (m * closeX) + b;

        double closestDist = findDist(new myPoint(0, 0), new myPoint(closeX, closeY));

        //System.out.println(closestDist);
        //System.out.println(b);
        //System.out.println(m);


        /**
         * INITIAL CLASSIFICATION OF CURVES
         */
        if (end.x > 0) {
            first = direction.RIGHT;
        } else {
            first = direction.LEFT;
        }

        if (closestDist > firstArc.RADIUS && b > 0) {
            second = direction.RIGHT;
        } else {
            second = direction.LEFT;
        }

        if ((end.ang < 0 ? end.ang + 360 : end.ang) > 180) {
            second = second == direction.LEFT ? direction.RIGHT : direction.LEFT;
        }

        /**
         * Printing and setting the curve types
         */
        if (first == direction.RIGHT) {
            if (second == direction.RIGHT) {
                System.out.println("RIGHT STRAIGHT RIGHT");
                RSR(start, end);
            } else {
                System.out.println("RIGHT STRAIGHT LEFT");
                RSL(start, end);
            }
        } else {
            if (second == direction.RIGHT) {
                System.out.println("LEFT STRAIGHT RIGHT");
                LSR(start, end);
            } else {
                System.out.println("LEFT STRAIGHT LEFT");
                LSL(start, end);
            }
        }


    }

    public double findDist(myPoint point1, myPoint point2) {
        return Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }

    public void RSR(Node start, Node end) {
        firstArc.setDirection(true);
        secondArc.setDirection(true);

        xDiff = secondArc.findCenter().x - firstArc.findCenter().x;
        yDiff = secondArc.findCenter().y - firstArc.findCenter().y;
        slope = Math.atan(yDiff / xDiff);

        firstArc.setLength((-slope + (Math.PI/2)) - Math.toRadians(start.calcAng));
        secondArc.setLength(Math.abs(Math.abs(Math.toRadians(end.calcAng)) - firstArc.length));
        //secondArc.setLength(Math.toRadians(end.ang) - (-slope + (Math.PI/2)));

        double dist = findDist(new myPoint(firstArc.findCenter().x, firstArc.findCenter().y), new myPoint(secondArc.findCenter().x, secondArc.findCenter().y));

        straight = new myStraight(dist);
    }

    public void RSL(Node start, Node end) {
        firstArc.setDirection(true);
        secondArc.setDirection(false);

        xDiff = secondArc.findCenter().x - firstArc.findCenter().x;
        yDiff = secondArc.findCenter().y - firstArc.findCenter().y;
        slope = Math.atan(yDiff / xDiff);

        double opp = firstArc.RADIUS;
        double adj = findDist(firstArc.findCenter(), secondArc.findCenter()) / 2;

        double relAng = Math.atan(opp/adj);

        double totAng;

        if (slope < 0 && secondArc.findCenter().x < 0) {
            slope = Math.abs(slope) - Math.toRadians(90);
            totAng = relAng + slope;
        } else {
            totAng = relAng + (Math.PI/2) -  slope;
        }

        System.out.println("Slope: " + Math.toDegrees(slope));
        System.out.println("Relative Angle: " + Math.toDegrees(relAng));
        System.out.println("Total Angle: " + Math.toDegrees(totAng));

        firstArc.setLength((totAng) - Math.toRadians(start.ang));
        secondArc.setLength((totAng) - Math.toRadians(end.ang));

        double dist = Math.sqrt(Math.pow(opp, 2) + Math.pow(adj, 2)) * 2;

        straight = new myStraight(dist);
    }

    public void LSL(Node start, Node end) {
        firstArc.setDirection(false);
        secondArc.setDirection(false);

        xDiff = secondArc.findCenter().x - firstArc.findCenter().x;
        yDiff = secondArc.findCenter().y - firstArc.findCenter().y;
        slope = Math.atan(yDiff / xDiff);

        firstArc.setLength(-((-slope - (Math.PI/2)) - Math.toRadians(start.ang)));
        secondArc.setLength(Math.abs(Math.abs(Math.toRadians(end.calcAng)) - firstArc.length));
        //secondArc.setLength((2 * Math.PI) - (Math.toRadians(end.ang) - slope));

        double dist = findDist(new myPoint(firstArc.findCenter().x, firstArc.findCenter().y), new myPoint(secondArc.findCenter().x, secondArc.findCenter().y));

        straight = new myStraight(dist);

        if (secondArc.length >= 2 * Math.PI) {
            secondArc.length -= 2 * Math.PI;
        }
    }

    public void LSR(Node start, Node end) {
        firstArc.setDirection(false);
        secondArc.setDirection(true);

        xDiff = secondArc.findCenter().x - firstArc.findCenter().x;
        yDiff = secondArc.findCenter().y - firstArc.findCenter().y;
        slope = Math.atan(yDiff / xDiff);

        double opp = firstArc.RADIUS;
        double adj = findDist(firstArc.findCenter(), secondArc.findCenter()) / 2;

        double relAng = Math.atan(opp/adj);

        double totAng;

        if (slope > 0 && secondArc.findCenter().x > -0.25) {
            slope = Math.abs(slope) - Math.toRadians(90);
            System.out.println("ALT");
            totAng = relAng + slope;
        } else {
            totAng = relAng + (Math.PI/2) + slope;
        }

        System.out.println("Slope: " + Math.toDegrees(slope));
        System.out.println("Relative Angle: " + Math.toDegrees(relAng));
        System.out.println("Total Angle: " + Math.toDegrees(totAng));

        firstArc.setLength((totAng) - Math.toRadians(start.ang));
        secondArc.setLength((totAng) + Math.toRadians(end.ang));

        double dist = Math.sqrt(Math.pow(opp, 2) + Math.pow(adj, 2)) * 2;

        straight = new myStraight(dist);

        if (secondArc.length > 2 * Math.PI) {
            secondArc.length -= 2 * Math.PI;
        }
    }

    public void telemtry(Node start, Node end) {
        System.out.println();
        System.out.println("////Data////");
        System.out.println("Slope: " + (-Math.toDegrees(slope) + 90));
        System.out.println("Line Length: " + straight.length + " Tiles.");
        System.out.println("Line Length: " + (straight.length * TIICKSPERTILE) + " Ticks.");
        System.out.println();
        System.out.println("////First Arc////");
        System.out.println("Length: " + Math.toDegrees(firstArc.length));
        System.out.println("Start Point: (" + start.x + ", " + start.y + ")");
        System.out.println("End Point: (" + firstArc.fin().x + ", " + firstArc.fin().y + ")");
        System.out.println("Direction: " + firstArc.right);
        System.out.println("Center: (" + firstArc.findCenter().x + ", " + firstArc.findCenter().y + ")");
        System.out.println();
        System.out.println("////Second Arc////");
        System.out.println("Length: " + Math.toDegrees(secondArc.length));
        System.out.println("Direction: " + secondArc.right);
        System.out.println("Center: (" + secondArc.findCenter().x + ", " + secondArc.findCenter().y + ")");
        System.out.println();
        System.out.println("////Starting Node////");
        System.out.println("Start Point: (" + start.x + ", " + start.y + ")");
        System.out.println("Raw Angle: " + start.rawAng);
        System.out.println("Calc Angle: " + start.calcAng);
        System.out.println();
        System.out.println("////Ending Node////");
        System.out.println("Start Point: (" + end.x + ", " + end.y + ")");
        System.out.println("Raw Angle: " + end.rawAng);
        System.out.println("Calc Angle: " + end.calcAng);
    }

    private enum direction {
        RIGHT,
        LEFT
    }
}