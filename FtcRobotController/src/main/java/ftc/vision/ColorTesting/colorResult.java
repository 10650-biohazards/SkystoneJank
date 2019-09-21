package ftc.vision.ColorTesting;

import org.opencv.core.Rect;

import java.util.ArrayList;

public class colorResult {

    public int area;
    public int sum;
    public ArrayList<Rect> rects;

    public colorResult(int area, int sum, ArrayList<Rect> rects) {
        this.area = area;
        this.sum = sum;
        this.rects = rects;
    }

    @Override
    public String toString() {
        return "";
    }
}
