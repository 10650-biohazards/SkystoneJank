package DubinsCurve;

import DubinsCurve.myPoint;

public class Node {

    public double x, y, ang;
    public double rawAng;
    public double calcAng;

    public Node(double x, double y, double ang) {
        this.x = x;
        this.y = y;
        this.ang = ang;
        this.rawAng = ang;
        this.calcAng = ang;

        if (this.ang < 0) {
            this.ang += 360;
        }
    }

    public myPoint coords() {
        return new myPoint(x, y);
    }

    public void print(String tag) {
        System.out.println();
        System.out.println("//////Node " + tag + "//////");
        System.out.println("Location: " + coords().toString());
        System.out.println("Calculate Angle: " + calcAng);
        System.out.println();
    }
}
