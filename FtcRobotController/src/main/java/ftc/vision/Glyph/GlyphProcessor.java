package ftc.vision.Glyph;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ftc.vision.ImageProcessor;
import ftc.vision.ImageProcessorResult;
import ftc.vision.ImageUtil;

public class GlyphProcessor implements ImageProcessor<GlyphResult> {
    private static final String TAG = "GlyphProcessor";

    @Override
    public ImageProcessorResult<GlyphResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {

        //Initializing different colors
        double[] greyPixel = {100, 100, 100}, brownPixel = {99, 80, 26};
        double[] greenPixel = {0, 255, 0}, redPixel = {255, 0, 0};
        double[][] newColors = {greyPixel, brownPixel};
        double[][] oldColors = {greenPixel, redPixel};

        //Final data variables
        GlyphResult.GlyphColor color = null;
        Point loc =  null;

        //convert to hsv
        Mat hsv = new Mat();
        Imgproc.cvtColor(rgbaFrame, hsv, Imgproc.COLOR_RGB2HSV);

        Mat returnMat;
        returnMat = hsv.clone();

        List<Point> points = new ArrayList<>();

        points.add(new Point(50, 40));
        points.add(new Point(100, 40));
        points.add(new Point(140, 40));
        points.add(new Point(50, 80));
        points.add(new Point(100, 80));
        points.add(new Point(140, 80));
        points.add(new Point(50, 120));
        points.add(new Point(100, 120));
        points.add(new Point(140, 120));
        points.add(new Point(141, 3));

        for (Point currPoint : points) {
            Imgproc.rectangle(returnMat, currPoint, new Point(currPoint.x + 3, currPoint.y + 3), new Scalar(0, 0, 0), 2);
            int x = (int) currPoint.x, y = (int) currPoint.y;
            Log.i(TAG, "("+ currPoint.x + ", " + currPoint.y + ")" + ": {" + returnMat.get(x, y)[0] + ", " + returnMat.get(x, y)[1] + ", " + returnMat.get(x, y)[2] + "}");
            Imgproc.putText(returnMat, "(" + currPoint.x + ", " + currPoint.y + ")", currPoint, 0, 0.25, new Scalar(255, 255, 255), 2);
        }





        //h range is 0-179
        //s range is 0-255
        //v range is 0-255

        //Detting definitions for the limits of brown and gray
        List<Scalar> hsvMaxs = new ArrayList<>();
        List<Scalar> hsvMins = new ArrayList<>();

        hsvMaxs.add(new Scalar(179, 70, 228)); //brown2 maxs
        hsvMins.add(new Scalar(132, 11, 57)); //brown2 mins

        hsvMaxs.add(new Scalar(105, 117, 205)); //grey maxs
        hsvMins.add(new Scalar(1, 23, 79)); //grey mins

        hsvMaxs.add(new Scalar(14, 63, 165)); //brown1 maxs
        hsvMins.add(new Scalar(0, 15, 115)); //brown1 mins

        List<Mat> rgbaChannels = new ArrayList<>();

        Mat maskedImage;

        //Variables for contour detection
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        List<Rect> rects = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            maskedImage = new Mat();

            //Applying limits
            ImageUtil.hsvInRange(hsv, hsvMins.get(i), hsvMaxs.get(i), maskedImage);

            //Finding contours for rectangle detection
            Mat contTemp = maskedImage.clone();
            Imgproc.findContours(contTemp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            for (MatOfPoint currCont : contours) {
                Rect rect = Imgproc.boundingRect(currCont);

                if (i == 0) {
                    rect.setBrown(true);
                } else {
                    rect.setBrown(false);
                }

                rects.add(rect);
            }

            rgbaChannels.add(maskedImage.clone());
        }

        //Adding blank Mat
        rgbaChannels.add(Mat.zeros(hsv.size(), CvType.CV_8UC1));

        Core.merge(rgbaChannels, rgbaFrame);



        int h = rgbaFrame.height(), w = rgbaFrame.width();

        for (int n = 0; n < 2; n++) {
            for (int j = 0; j < w * h; j++) {

                double[] pixel = rgbaFrame.get((j - (j % w)) / w, j % w);

                if (Arrays.equals(pixel, oldColors[n])) {
                    rgbaFrame.put((j - (j % w)) / w, j % w, newColors[n]);
                }
            }
        }


        double bestScore = Integer.MIN_VALUE + 1;
        Rect bestRect = null;

        for (Rect rect : rects) {
            double proportion = rect.height / rect.width;

            double score = Integer.MIN_VALUE;

            if (proportion < 1) {
                proportion = 1 / proportion;
            }

            if (rect.area() > 100 && proportion < 2) {
                Imgproc.rectangle(rgbaFrame, rect.tl(), rect.br(), new Scalar(255, 255, 255), 1);
                score = rect.area();
            } else if (rect.area() > 100){
                Imgproc.rectangle(rgbaFrame, rect.tl(), rect.br(), new Scalar(255, 255, 0), 1);
            } else {
                //Imgproc.rectangle(rgbaFrame, rect.tl(), rect.br(), new Scalar(255, 0, 0), 1);
            }


            if (score > bestScore) {
                bestScore = score;
                bestRect = rect;
            }
        }

        if (bestRect != null) {
            Imgproc.rectangle(rgbaFrame, bestRect.tl(), bestRect.br(), new Scalar(0, 255, 0), 1);
            if (bestRect.isBrown()) {
                color = GlyphResult.GlyphColor.BROWN;
            } else {
                color = GlyphResult.GlyphColor.GRAY;
            }

            loc = bestRect.mid();
        }

        return new ImageProcessorResult<>(startTime, rgbaFrame, new GlyphResult(color, loc));
    }
}
