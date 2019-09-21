package ftc.vision.ColorTesting;

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

import ftc.vision.Beacon.BeaconColorResult;
import ftc.vision.ImageProcessor;
import ftc.vision.ImageProcessorResult;
import ftc.vision.ImageUtil;

public class colorProcessor implements ImageProcessor<colorResult> {
    private static final String TAG = "trackProcessor";
    private static final int MIN_MASS = 6;

    public static Mat input;
    
    @Override
    public ImageProcessorResult<colorResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {



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

        hsvMin.add(new Scalar(10, 150, 100)); //yellow min
        hsvMax.add(new Scalar(29, 255, 255)); //yellow max

        hsvMin.add(new Scalar(0, 0, 0)); //red min
        hsvMax.add(new Scalar(0/2, 0, 0)); //red max

        hsvMin.add(new Scalar(0, 0, 0)); //blue min
        hsvMax.add(new Scalar(0, 0, 0)); //blue max




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

        for (int i = 0; i < 3; i++) {

            maskedImage = new Mat();

            //Applying HSV limits
            ImageUtil.hsvInRange(hsv, hsvMin.get(i), hsvMax.get(i), maskedImage);

            //Start Core's additions
            Mat contTemp = maskedImage.clone();
            Imgproc.findContours(contTemp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            //End Core's addition

            rgbaChannels.add(maskedImage.clone());
        }

        //add empty alpha channels
        rgbaChannels.add(Mat.zeros(hsv.size(), CvType.CV_8UC1));

        Core.merge(rgbaChannels, rgbaFrame);

        ArrayList<Rect> rects = new ArrayList<>();
        int sumArea = 0;
        int sum = 0;
        for (MatOfPoint cont : contours) {
            Rect rect = Imgproc.boundingRect(cont);
            if (rect.area() > 1000) {
                rects.add(rect);
                sum++;
                sumArea += rect.area();
                Imgproc.rectangle(rgbaFrame, rect.bl(), rect.tr(), new Scalar(0, 0, 255), 2);
            }
        }


        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "1_binary", startTime);
        }

        input = rgbaFrame.clone();

        return new ImageProcessorResult<>(startTime, rgbaFrame, new colorResult(sumArea, sum, rects));
    }
}

//hsvMin.add(new Scalar(19, 150, 200)); //yellow min
//hsvMax.add(new Scalar(29, 255, 255)); //yellow max