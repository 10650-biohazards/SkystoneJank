package ftc.vision.SkyStone;

public class stackResult {

    public int width;
    public double xCoord;

    public stackResult(double xCoord, int width) {
        this.xCoord = xCoord;
        this.width = width;
    }

    @Override
    public String toString() {
        return "X: " + xCoord + " Width: " + width;
    }
}
