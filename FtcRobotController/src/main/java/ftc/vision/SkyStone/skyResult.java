package ftc.vision.SkyStone;

public class skyResult {

    public int pos;

    public skyResult(int pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        if (pos != -1) {
            return "Position " + pos;
        } else {
            return "Sorry, can't help you!";
        }
    }
}
