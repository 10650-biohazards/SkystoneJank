package org.firstinspires.ftc.teamcode;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class testClass {

    public static void main(String[] args) {
        Point r1 = new Point(1, 1);
        Point r2 = new Point(3, 3);

        Rect rect = new Rect(r1, r2);

        Point p = new Point(2.9, 2);

        System.out.println(rect.contains(p));
    }
}
