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

    public static Mat input;

    @Override
    public ImageProcessorResult<skyResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {

        int yMax = 140, yMin = 194;
        int startX = 0, stoneWidth = 170, buffer = 60;

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
        Imgproc.line(rgbaFrame, new Point(startX + stoneWidth,0 ), new Point(startX + stoneWidth, rgbaFrame.height()), new Scalar(0, 0, 0), 70);
        Imgproc.line(rgbaFrame, new Point(startX + (stoneWidth * 2),0 ), new Point(startX + (stoneWidth * 2), rgbaFrame.height()), new Scalar(0, 0, 0), 70);
        Imgproc.line(rgbaFrame, new Point(startX + (stoneWidth * 3),0 ), new Point(startX + (stoneWidth * 3), rgbaFrame.height()), new Scalar(0, 0, 0), 70);
        Imgproc.rectangle(rgbaFrame, new Point(0,yMin + 10), new Point(rgbaFrame.width(), yMin + 20), new Scalar(0, 0, 0), -1);
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

        //Core's additions
        ArrayList<Rect> stones = new ArrayList<>();
        for (MatOfPoint currCont : contours) {
            Rect rect = Imgproc.boundingRect(currCont);
            if (rect.area() > 200) {
                Imgproc.rectangle(rgbaFrame, rect.bl(), rect.tr(), new Scalar(0, 255, 0), 3);
                Imgproc.rectangle(rgbaFrame, new Point(rect.mid().x + 2, rect.mid().y + 2), new Point(rect.mid().x - 2, rect.mid().y - 2), new Scalar(255, 255, 255), 3);
                stones.add(rect);
            }
        }

        boolean[] stonePresent = {false, false, false};
        for (Rect slot :slots) {
            Imgproc.rectangle(rgbaFrame, slot.bl(), slot.tr(), new Scalar(0, 0, 255), 3);
        }
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


        input = rgbaFrame.clone();

        return new ImageProcessorResult<>(startTime, rgbaFrame, new skyResult(result));
    }
}
