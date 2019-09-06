package Utilities;

import DubinsCurve.Node;
import Gagarin.Subsystems.DriveSubsystem;

public class driveTracker {

    DriveSubsystem drive;

    private double x = 0, y = 0;

    private double oldEnc = 0;

    public double deltaEnc;

    private final int TIICKSPERTILE = 1075;



    private Node currentNode;

    public driveTracker(DriveSubsystem drive) {
        this.drive = drive;
    }

    public void setCurrentNode(double x, double y, double Ang) {
        currentNode = new Node(x, y, Ang);
        this.x = x;
        this.y = y;
    }

    private double avgEnc() {
        double sum = 0;
        sum -= drive.bright.getCurrentPosition();
        sum -= drive.bleft.getCurrentPosition();
        sum -= drive.fright.getCurrentPosition();
        sum -= drive.fleft.getCurrentPosition();

        return sum / 4;
    }

    public void refresh() {
        double currAng = drive.gyro.getYaw();
        double currEnc = avgEnc();

        deltaEnc = currEnc - oldEnc;
        oldEnc = currEnc;

        y += Math.cos(Math.toRadians(currAng)) * deltaEnc;
        x += Math.sin(Math.toRadians(currAng)) * deltaEnc;
    }

    public double getX() {
        return x / TIICKSPERTILE;
    }

    public double getY() {
        return y / TIICKSPERTILE;
    }

    public Node getCurrentNode() {
        return currentNode;
    }
}
