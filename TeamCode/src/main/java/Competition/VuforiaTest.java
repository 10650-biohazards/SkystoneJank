package Competition;

import android.content.Context;
import android.graphics.Bitmap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.R;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

@TeleOp (name = "Vision")
public class VuforiaTest extends OpMode {

    WebcamName webcamName;

    VuforiaLocalizer locale;

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

        VuforiaLocalizer.CloseableFrame frame = null; //takes the frame at the head of the queue
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

        if (rgb != null) {
            Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);

            Mat mat = new Mat();
            Utils.bitmapToMat(bm, mat);
        }


    }

    @Override
    public void loop() {

    }
}
