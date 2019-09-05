package ftc.vision.Beacon;

import org.opencv.core.Scalar;

import ftc.vision.ImageUtil;

public class BeaconColorResult {

    private final BeaconColor leftColor, rightColor;

    public BeaconColor getLeftColor() {
        return leftColor;
    }

    public BeaconColor getRightColor() {
        return rightColor;
    }

    public BeaconColorResult(BeaconColor leftColor, BeaconColor rightColor) {
        this.leftColor = leftColor;
        this.rightColor = rightColor;

    }

    public enum BeaconColor {
        RED (ImageUtil.RED),
        GREEN (ImageUtil.GREEN),
        BLUE (ImageUtil.BLUE),
        UNKNOWN (ImageUtil.BLACK);

        public final Scalar color;

        BeaconColor(Scalar color) {
            this.color = color;
        }
    }

    @Override
    public String toString() {
        return leftColor + ", " + rightColor;
    }
}
