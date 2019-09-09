package ftc.vision.SkyStone;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import ftc.vision.ImageProcessor;
import ftc.vision.ImageProcessorResult;
import ftc.vision.ImageUtil;

public class skyProcessor implements ImageProcessor<skyResult> {
    private static final String TAG = "stoneProcessor";

    @Override
    public ImageProcessorResult<skyResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {

        int yMax = 42, yMin = 84;
        int startX = 42, stoneWidth = 42, buffer = 42;

        ArrayList<Rect> slots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            slots.add(new Rect(new Point(startX + (stoneWidth * i) + buffer, yMax), new Point(startX + (stoneWidth * (i + 1)) - buffer, yMin)));
        }


        Point location;
        double rectAngle;

        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "0_camera", startTime);
        }

        //convert to hsv
        Mat hsv = new Mat();
        Imgproc.line(rgbaFrame, new Point(startX + stoneWidth,0 ), new Point(startX + stoneWidth, 144), new Scalar(0, 0, 0), 3);
        Imgproc.line(rgbaFrame, new Point(startX + (stoneWidth * 2),0 ), new Point(startX + (stoneWidth * 2), 144), new Scalar(0, 0, 0), 3);
        Imgproc.line(rgbaFrame, new Point(startX + (stoneWidth * 3),0 ), new Point(startX + (stoneWidth * 3), 144), new Scalar(0, 0, 0), 3);
        Imgproc.cvtColor(rgbaFrame, hsv, Imgproc.COLOR_RGB2HSV);

        //h range is 0-179
        //s range is 0-255
        //v range is 0-255

        Scalar yellowMin = new Scalar(150, 50, 150);
        Scalar yellowMax = new Scalar(179, 255, 255);


        List<Mat> rgbaChannels = new ArrayList<>();

        //Keeps track of highest masses for left and right
        double[] maxMass = {Double.MIN_VALUE, Double.MIN_VALUE};

        //Keeps track of the index of the highest mass for lest and right
        int[] maxMassIndex = {3, 3};


        Mat maskedImage;
        Mat colSum = new Mat();
        double mass;
        int[] data = new int[3];

        //Core's additions
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        //End



        maskedImage = new Mat();
        ImageUtil.hsvInRange(hsv, yellowMin, yellowMax, maskedImage);

        Mat contTemp = maskedImage.clone();
        Imgproc.findContours(contTemp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        rgbaChannels.add(maskedImage.clone());



        //add empty alpha channels
        rgbaChannels.add(Mat.zeros(hsv.size(), CvType.CV_8UC1));

        Core.merge(rgbaChannels, rgbaFrame);

        //Core's additions
        ArrayList<Rect> stones = new ArrayList<>();
        for (MatOfPoint currCont : contours) {
            Rect rect = Imgproc.boundingRect(currCont);
            if (rect.area() > 40) {
                stones.add(rect);
            }
        }

        boolean[] stonePresent = {false, false, false};
        for (Rect currRect : stones) {
            for (int i = 0; i < 3; i++) {
                if (slots.get(i).contains(currRect.mid())) {
                    stonePresent[i] = true;
                }
            }
        }

        int result = -1;
        for (int i = 0; i < 3; i++) {
            if (!stonePresent[i]) {
                result = i;
            }
        }




        return new ImageProcessorResult<>(startTime, rgbaFrame, new skyResult(result));
    }
}
