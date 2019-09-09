package ftc.vision.ColorTesting;

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
import java.util.List;

import ftc.vision.ImageProcessor;
import ftc.vision.ImageProcessorResult;
import ftc.vision.ImageUtil;

public class colorProcessor implements ImageProcessor<colorResult> {
    private static final String TAG = "trackProcessor";
    private static final double MIN_MASS = 6;


    private static Point r1 = new Point(0, 0);
    private static Point r2 = new Point(176, 144);
    private static Rect lookingBox = new Rect(r1, r2);

    private static Point lastGoodLoc;
    public static condition cond;
    
    @Override
    public ImageProcessorResult<colorResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {

        Point location;
        
        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "0_camera", startTime);
        }

        //convert to hsv
        Mat hsv = new Mat();
        Imgproc.cvtColor(rgbaFrame, hsv, Imgproc.COLOR_RGB2HSV);

        //h range is 0-179
        //s range is 0-255
        //v range is 0-255

        //values stored as list of minimum and maximum hsv values, red then green then blue
        List<Scalar> hsvMin = new ArrayList<>();
        List<Scalar> hsvMax = new ArrayList<>();

        //Scalar redMin = new Scalar(150, 50, 100); //red min original
        //Scalar redMax = new Scalar(30, 255, 255); //red max original

        Scalar redMin = new Scalar(17, 50, 175); //red min original
        Scalar redMax = new Scalar(29, 255, 255); //red max original


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
        ImageUtil.hsvInRange(hsv, redMin, redMax, maskedImage);

        Mat contTemp = maskedImage.clone();
        Imgproc.findContours(contTemp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        rgbaChannels.add(maskedImage.clone());

        //add empty alpha channels
        rgbaChannels.add(Mat.zeros(hsv.size(), CvType.CV_8UC1));

        Core.merge(rgbaChannels, rgbaFrame);

        Log.i(TAG, lookingBox.mid().toString());


        return new ImageProcessorResult<>(startTime, rgbaFrame, new colorResult());
    }

    private enum condition {
        INFRAME,
        OFFLEFT,
        OFFRIGHT,
        UP
    }
}
