package ftc.vision.SkyStone;

import android.util.Log;

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

public class stoneProcessor implements ImageProcessor<stoneResult> {
    private static final String TAG = "stoneProcessor";


    
    @Override
    public ImageProcessorResult<stoneResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {

        Point location;
        double rectAngle;
        
        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "0_camera", startTime);
        }

        //convert to hsv
        Mat hsv = new Mat();
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

        double maxSize = Double.MIN_VALUE;
        RotatedRect maxRect = null;

        for (MatOfPoint currCont : contours) {
            RotatedRect rotRect = Imgproc.minAreaRect(new MatOfPoint2f(currCont.toArray()));
            double area = rotRect.size.height * rotRect.size.width;

            if (area > maxSize) {
                maxSize = area;
                maxRect = rotRect;
            }
        }

        if (maxRect != null) {
            location = maxRect.center;
            Imgproc.circle(rgbaFrame, maxRect.center, 2, new Scalar(255, 255, 255), 2);
            rectAngle = maxRect.angle;
        } else {
            location = null;
            rectAngle = Double.MAX_VALUE;

        }
        //End



        return new ImageProcessorResult<>(startTime, rgbaFrame, new stoneResult(location, rectAngle));
    }
}
