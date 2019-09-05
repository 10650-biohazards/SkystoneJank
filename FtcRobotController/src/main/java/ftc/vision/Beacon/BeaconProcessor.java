package ftc.vision.Beacon;

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

/**
 * Created by vandejd1 on 8/29/16.
 * FTC Team EV 7393
 */
public class BeaconProcessor implements ImageProcessor<BeaconColorResult> {
    private static final String TAG = "BeaconProcessor";
    private static final double MIN_MASS = 6;
    
    @Override
    public ImageProcessorResult<BeaconColorResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {
        
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

        hsvMin.add(new Scalar(300/2, 50, 100)); //red min
        hsvMax.add(new Scalar( 60/2, 255, 255)); //red max

        hsvMin.add(new Scalar( 60/2, 50, 150)); //green min
        hsvMax.add(new Scalar(180/2, 255, 255)); //green max

        hsvMin.add(new Scalar(180/2, 50, 150)); //blue min
        hsvMax.add(new Scalar(300/2, 255, 255)); //blue max


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

            //applies column sum to binary image
            Core.reduce(maskedImage, colSum, 0, Core.REDUCE_SUM, 4);

            int start = 0;

            //This is the boundary
            int end = hsv.width() / 2;

            for (int j = 0; j < 2; j++) {

                mass = 0;
                for (int x = start; x < end; x++) {
                    colSum.get(0, x, data);
                    mass += data[0];
                }

                //Scales by image size
                mass /= hsv.size().area();

                if (mass >= MIN_MASS && mass > maxMass[j]) {
                    maxMass[j] = mass;
                    maxMassIndex[j] = i;
                }


                start = end;
                end = hsv.width();
            }
        }

        //add empty alpha channels
        rgbaChannels.add(Mat.zeros(hsv.size(), CvType.CV_8UC1));

        Core.merge(rgbaChannels, rgbaFrame);

        BeaconColorResult.BeaconColor[] beaconColors = BeaconColorResult.BeaconColor.values();
        BeaconColorResult.BeaconColor left = beaconColors[maxMassIndex[0]];
        BeaconColorResult.BeaconColor right = beaconColors[maxMassIndex[1]];

        //Core's additions
        for (MatOfPoint currCont : contours) {
            Rect rect = Imgproc.boundingRect(currCont);
            if (rect.height * rect.width > 100) {
                Imgproc.rectangle(rgbaFrame, rect.tl(), rect.br(), new Scalar(255, 255, 255), 2);
            }
        }
        //End

        //drawing pretty rectangles
        int barHeight = hsv.height() / 30;

        Imgproc.rectangle(rgbaFrame, new Point(0, 0), new Point(hsv.width()/2, barHeight), left.color, barHeight);
        Imgproc.rectangle(rgbaFrame, new Point(hsv.width()/2, 0), new Point(hsv.width(), barHeight), right.color, barHeight);


        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "1_binary", startTime);
        }

        return new ImageProcessorResult<>(startTime, rgbaFrame, new BeaconColorResult(left, right));
    }
}
