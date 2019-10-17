package Competition;

import android.graphics.Bitmap;
import android.widget.TextView;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import org.firstinspires.ftc.teamcode.R;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import ftc.vision.SkyStone.skyProcessor;
import ftc.vision.SkyStone.skyResult;
import ftc.vision.ImageProcessorResult;
import ftc.vision.SkyStone.stackProcessor;
import ftc.vision.SkyStone.stackResult;

@TeleOp (name = "Vision")
public class VuforiaTest extends OpMode {

    WebcamName webcamName;

    VuforiaLocalizer locale;
    Mat mat;

    VuforiaLocalizer.CloseableFrame frame = null; //takes the frame at the head of the queue

    @Override
    public void init() {

        webcamName = hardwareMap.get(WebcamName.class, "stoned cam");

        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.vuforiaLicenseKey = "AdqB5nn/////AAABmUClSp+3R05smR58cdh8aZV3nkIpGVv7EGel7SA8/fwMAZOKlcl" +
                                   "O126lVGQjTFwJ0cglWGGZ8iJqfX8HgccMbSHVTKpO9BQ8juqXzGbrq/NThsooigOztG" +
                                   "h/uG0olrnNHJdzMKH9OuLer0qRDcybQ2rd3PtqMzVsRVqYRD8vmNaCiYwUC3zwXu7jK" +
                                   "di1CuP6A8Zjhs0/Z4Fz/tw0LyT0XLLKcnoVRqBkkln9jQeZAWMgnzfDCQOJRAGqz2y2" +
                                   "t0yuHqouG2UhMDo42/z/xNLNqtCjvtLohcG4wkE5lOTdYZKt5BDHifAyuf3GQQzk1aR" +
                                   "MilJpCGa5hzioMAGmDnYA8kqmcVAuu10ps4ZsFGBJkR7B";
        params.cameraName = webcamName;


        this.locale = ClassFactory.getInstance().createVuforia(params);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true); //enables RGB565 format for the image
        locale.setFrameQueueCapacity(1); //tells VuforiaLocalizer to only store one frame at a time

        /*To access the image: you need to iterate through the images of the frame object:*/

        try {
            frame = locale.getFrameQueue().take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Image rgb = null;

        long numImages = frame.getNumImages();


        for (int i = 0; i < numImages; i++) {
            if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                rgb = frame.getImage(i);
                break;
            }//if
        }//for

        /*rgb is now the Image object that we’ve used in the video*/

        mat = new Mat();
        if (rgb != null) {
            Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);
            bm.copyPixelsFromBuffer(rgb.getPixels());

            Utils.bitmapToMat(bm, mat);
        }

        skyResult result;
        skyProcessor processor = new skyProcessor();
        ImageProcessorResult imageProcessorResult = processor.process(0, mat, false);
        result = (skyResult) imageProcessorResult.getResult();

        /*
        int ticker = 0;
        for (Rect rect : result.rects) {
            telemetry.addData("Rect", ticker);
            telemetry.addData("Middle", rect.mid().toString());
            telemetry.addData("Height", rect.height);
            telemetry.addData("Width", rect.width);
            telemetry.addData("", "");

            ticker++;
        }



        telemetry.addData("Area", result.area);
        telemetry.addData("Rect num", result.sum);
        telemetry.addData("width", mat.width());
        telemetry.addData("height", mat.height());
        telemetry.update();
        */
    }

    @Override
    public void loop() {
        try {
            frame = locale.getFrameQueue().take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Image rgb = null;

        long numImages = frame.getNumImages();

        for (int i = 0; i < numImages; i++) {
            if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                rgb = frame.getImage(i);
                break;
            }//if
        }//for

        /*rgb is now the Image object that we’ve used in the video*/

        mat = new Mat();
        if (rgb != null) {
            Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);
            bm.copyPixelsFromBuffer(rgb.getPixels());

            Utils.bitmapToMat(bm, mat);
        }

        stackProcessor processor = new stackProcessor();

        stackResult result;
        ImageProcessorResult imageProcessorResult = processor.process(0, mat, false);
        result = (stackResult) imageProcessorResult.getResult();

        telemetry.addData(result.toString(), "");

        if (result.xCoord > 420) {
            telemetry.addData("Off right", "");
        } else if (result.xCoord < 380) {
            telemetry.addData("Off left", "");
        } else {
            if (result.width > 500) {
                telemetry.addData("done", "");
            } else {
                telemetry.addData("Advacne", "");
            }
        }


        /*
        int ticker = 0;
        for (Rect rect : result.rects) {
            telemetry.addData("Rect", ticker);
            telemetry.addData("Middle", rect.mid().toString());
            telemetry.addData("Height", rect.height);
            telemetry.addData("Width", rect.width);
            telemetry.addData("", "");

            ticker++;
        }



        telemetry.addData("Area", result.area);
        telemetry.addData("Rect num", result.sum);
        telemetry.addData("width", mat.width());
        telemetry.addData("height", mat.height());
        telemetry.update();
        */
    }
}
