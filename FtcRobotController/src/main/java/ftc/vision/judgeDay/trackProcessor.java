package ftc.vision.judgeDay;

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

public class trackProcessor implements ImageProcessor<trackResult> {
    private static final String TAG = "trackProcessor";
    private static final double MIN_MASS = 6;


    private static Point r1 = new Point(0, 0);
    private static Point r2 = new Point(176, 144);
    private static Rect lookingBox = new Rect(r1, r2);

    private static Point lastGoodLoc;
    public static condition cond;
    
    @Override
    public ImageProcessorResult<trackResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {

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

        Scalar redMin = new Scalar(150, 50, 150); //red min original
        Scalar redMax = new Scalar(179, 255, 255); //red max original


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

        //rgbaChannels.add(maskedImage.clone());

        /*
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
        }*/

        //add empty alpha channels
        rgbaChannels.add(Mat.zeros(hsv.size(), CvType.CV_8UC1));

        Core.merge(rgbaChannels, rgbaFrame);

        //Core's additions

        double maxSize = Double.MIN_VALUE;
        Rect maxRect = null;

        for (MatOfPoint currCont : contours) {
            Rect rect = Imgproc.boundingRect(currCont);

            if (lookingBox.contains(rect.mid())) {
                //Imgproc.rectangle(rgbaFrame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);

                if (rect.area() > maxSize) {
                    maxSize = rect.area();
                    maxRect = rect;
                }
            } else {
                //Imgproc.rectangle(rgbaFrame, rect.tl(), rect.br(), new Scalar(255, 0, 0), 2);
            }
        }

        if (maxRect != null) {
            location = maxRect.mid();
            Imgproc.circle(rgbaFrame, maxRect.mid(), 5, new Scalar(255, 255, 255), 2);
        } else {
            location = null;
        }
        //End



        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "1_binary", startTime);
        }





        if (location != null) {
            lastGoodLoc = location;
            lookingBox = new Rect(new Point(location.x - 20, location.y - 20), new Point(location.x + 20, location.y + 20));

            cond = condition.INFRAME;
        } else {
            /*if (lastGoodLoc.y < 16) {
                cond = condition.UP;
                lookingBox = new Rect(new Point(176, 16), new Point(0, 0));
            } else if (lastGoodLoc.x > 72) {
                cond = condition.OFFRIGHT;
                lookingBox = new Rect(new Point(176, 144), new Point(160, 0));
            } else {
                cond = condition.OFFLEFT;
                lookingBox = new Rect(new Point(16, 144), new Point(0, 0));
            }*/
        }

        Imgproc.rectangle(rgbaFrame, lookingBox.tl(), lookingBox.br(), new Scalar(0, 0, 255), 5);

        Log.i(TAG, lookingBox.mid().toString());


        return new ImageProcessorResult<>(startTime, rgbaFrame, new trackResult(location));
    }

    private enum condition {
        INFRAME,
        OFFLEFT,
        OFFRIGHT,
        UP
    }
}
