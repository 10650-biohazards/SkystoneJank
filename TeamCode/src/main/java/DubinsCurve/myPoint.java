package DubinsCurve;

public class myPoint {

    public double x, y;

    public myPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
