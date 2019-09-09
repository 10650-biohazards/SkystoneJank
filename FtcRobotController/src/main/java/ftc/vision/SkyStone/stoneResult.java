package ftc.vision.SkyStone;

import org.opencv.core.Point;

public class stoneResult {

    public Point centerPoint;

    public double rotation;

    public stoneResult(Point screenLoc, double rotation) {
        this.centerPoint = screenLoc;
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        if (centerPoint != null) {
            return centerPoint.toString() + " " + rotation + "º";
        } else {
            return "Sorry, can't help you!";
        }
    }
}
