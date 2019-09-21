package Competition.Commands;


import android.graphics.Bitmap;

import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.R;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import Competition.RobotMap;
import FtcExplosivesPackage.BioCommand;
import FtcExplosivesPackage.BiohazardTele;
import ftc.vision.ColorTesting.colorProcessor;
import ftc.vision.ImageProcessorResult;
import ftc.vision.SkyStone.stackResult;
import ftc.vision.SkyStone.stoneResult;
import ftc.vision.VufFrameGrabber;

public class VisionCommand extends BioCommand {

    public static stoneStatus status;
    public static stackStatus stackStatus;
    public static double stackX;
    public static int stackWid;
    stoneResult intakeResult;
    stackResult stackResult;

    VufFrameGrabber intakeGrabber;

    VuforiaLocalizer locale;

    colorProcessor processor;

    public VisionCommand(BiohazardTele op) {
        super(op, "vision");
    }

    @Override
    public void init() {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.vuforiaLicenseKey = "AdqB5nn/////AAABmUClSp+3R05smR58cdh8aZV3nkIpGVv7EGel7SA8/fwMAZOKlcl" +
                "O126lVGQjTFwJ0cglWGGZ8iJqfX8HgccMbSHVTKpO9BQ8juqXzGbrq/NThsooigOztG" +
                "h/uG0olrnNHJdzMKH9OuLer0qRDcybQ2rd3PtqMzVsRVqYRD8vmNaCiYwUC3zwXu7jK" +
                "di1CuP6A8Zjhs0/Z4Fz/tw0LyT0XLLKcnoVRqBkkln9jQeZAWMgnzfDCQOJRAGqz2y2" +
                "t0yuHqouG2UhMDo42/z/xNLNqtCjvtLohcG4wkE5lOTdYZKt5BDHifAyuf3GQQzk1aR" +
                "MilJpCGa5hzioMAGmDnYA8kqmcVAuu10ps4ZsFGBJkR7B";
        params.cameraName = RobotMap.stoneCam;


        this.locale = ClassFactory.getInstance().createVuforia(params);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true); //enables RGB565 format for the image
        locale.setFrameQueueCapacity(1); //tells VuforiaLocalizer to only store one frame at a time
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        intakeVision();
        stackVision();
    }

    public void stackVision() {
        processVuforia();
        stackX = stackResult.xCoord;
        stackWid = stackResult.width;

        if (stackX > 98) {
            stackStatus = VisionCommand.stackStatus.OFFRIGHT;
        } else if (stackX < 78) {
            stackStatus = VisionCommand.stackStatus.OFFLEFT;
        } else {
            if (stackWid > 70) {
                stackStatus = VisionCommand.stackStatus.DONE;
            } else {
                stackStatus = VisionCommand.stackStatus.ADVANCE;
            }
        }
    }

    public void intakeVision() {
        processVuforia();

        if (intakeResult.centerPoint != null) {
            double stoneAng = intakeResult.rotation;

            if (Math.abs(stoneAng) < 5) {
                status = stoneStatus.ONTARGET;
            } else if (stoneAng < 0) {
                status = stoneStatus.TILTLEFT;
            } else if (stoneAng > 0) {
                status = stoneStatus.TILTRIGHT;
            }
        } else {
            status = stoneStatus.NONE;
        }
    }

    public void processVuforia() {
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
        Mat mat = new Mat();
        if (rgb != null) {
            Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);

            Utils.bitmapToMat(bm, mat);
        }

        ImageProcessorResult imageProcessorResult = processor.process(0, mat, false);
        intakeResult = (stoneResult) imageProcessorResult.getResult();
    }

    @Override
    public void stop() {

    }

    public enum stoneStatus {
        NONE,
        ONTARGET,
        TILTRIGHT,
        TILTLEFT
    }

    public enum stackStatus {
        NONE,
        ADVANCE,
        OFFLEFT,
        OFFRIGHT,
        DONE
    }
}
