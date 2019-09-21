package ftc.vision.SkyStone;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import ftc.vision.ColorTesting.colorResult;
import ftc.vision.ImageProcessor;
import ftc.vision.ImageProcessorResult;
import ftc.vision.ImageUtil;

public class stackProcessor implements ImageProcessor<stackResult> {
    String TAG = "Stack Processor";

    public static Mat input;

    @Override
    public ImageProcessorResult<stackResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {
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

        hsvMin.add(new Scalar(0, 0, 0)); //null min
        hsvMax.add(new Scalar(0, 0, 0)); //null max

        hsvMin.add(new Scalar(0, 0, 0)); //null min
        hsvMax.add(new Scalar(0, 0, 0)); //null max


        List<Mat> rgbaChannels = new ArrayList<>();


        Mat maskedImage;

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

        Rect maxRect = null;
        double maxArea = -1;
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            Imgproc.rectangle(rgbaFrame, rect.bl(), rect.tr(), new Scalar(255, 255, 255), 3);
            if (rect.area() > maxArea) {
                maxRect = rect;
                maxArea = rect.area();
            }
        }

        int width = -1;
        double xCoord = -1;
        if (maxRect != null) {
            width = maxRect.width;
            xCoord = maxRect.mid().x;
        }

        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "1_binary", startTime);
        }

        input = rgbaFrame.clone();

        return new ImageProcessorResult<>(startTime, rgbaFrame, new stackResult(xCoord, width));
    }
}