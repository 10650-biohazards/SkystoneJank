package DubinsCurve;

public class myArc {

    public static final double RADIUS = 0.5520833333;
    Node startNode;
    public double length;
    public boolean right;

    public myArc(Node startNode) {
        this.startNode = startNode;
    }

    public myPoint findCenter() {
        double x = startNode.x;
        double y = startNode.y;

        if (right) {
            x += RADIUS * Math.sin(Math.toRadians(startNode.calcAng) + (Math.PI / 2));
            y += RADIUS * Math.sin(Math.toRadians(startNode.calcAng) + (Math.PI));
        } else {
            x -= RADIUS * Math.sin(Math.toRadians(startNode.calcAng) + (Math.PI / 2));
            y += RADIUS * Math.sin(Math.toRadians(startNode.calcAng));
        }

        return new myPoint(x, y);
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double findDistance() {
        double circum = 2 * Math.PI * RADIUS;
        double ratio = length / 360;
        return circum * ratio;
    }

    public myPoint fin() {
        double x, y;

        if (right) {
            y = findCenter().y + (Math.sin(Math.toRadians(startNode.calcAng) + length) * RADIUS);
            x = findCenter().x - (Math.cos(Math.toRadians(startNode.calcAng) + length) * RADIUS);
        } else {
            y = 0;
            x = 0;
        }

        return new myPoint(x, y);
    }

    public void setDirection(boolean right) {
        this.right = right;
    }
}
