package ftc.vision.SkyStone;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;

public class stoneResult {

    public Point centerPoint;

    public double rotation;

    public RotatedRect rect;

    public stoneResult(Point screenLoc, double rotation, RotatedRect rect) {
        this.centerPoint = screenLoc;
        this.rotation = rotation;
        this.rect = rect;
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
