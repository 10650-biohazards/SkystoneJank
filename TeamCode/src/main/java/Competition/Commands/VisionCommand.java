package Competition.Commands;


import android.graphics.Bitmap;

import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.R;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import Competition.Robot;
import Competition.RobotMap;
import FtcExplosivesPackage.BioCommand;
import FtcExplosivesPackage.BiohazardTele;
import Utilities.Utility;
import ftc.vision.ColorTesting.colorProcessor;
import ftc.vision.FrameGrabber;
import ftc.vision.ImageProcessorResult;
import ftc.vision.SkyStone.stackProcessor;
import ftc.vision.SkyStone.stackResult;
import ftc.vision.SkyStone.stoneProcessor;
import ftc.vision.SkyStone.stoneResult;
import ftc.vision.VufFrameGrabber;

public class VisionCommand extends BioCommand {

    public static stoneStatus status;
    public static stackStatus stackStatus;
    public static double stackX;
    public static int stackWid;
    stoneResult intakeResult;

    FrameGrabber grabber;

    VufFrameGrabber intakeGrabber;

    VuforiaLocalizer locale;
    VuforiaLocalizer.CloseableFrame frame = null; //takes the frame at the head of the queue

    stackProcessor processor;
    Utility u;

    BiohazardTele op;

    Mat mat;

    public VisionCommand(BiohazardTele op) {
        super(op, "vision");
        //u = new Utility(op);
        this.op = op;
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
        Mat mat = new Mat();
        if (rgb != null) {
            Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);

            Utils.bitmapToMat(bm, mat);
        }

        stackResult stackResult;
        processor = new stackProcessor();
        ImageProcessorResult imageProcessorResult = processor.process(0, mat, false);
        stackResult = (stackResult) imageProcessorResult.getResult();

        grabber = FtcRobotControllerActivity.frameGrabber;
        grabber.setImageProcessor(new stoneProcessor());
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        intakeVision();
        if (Robot.driver.a) {
            stackVision();
        }
    }

    public void stackVision() {
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



        stackX = result.xCoord;
        stackWid = result.width;

        if (stackX > 420) {
            op.telemetry.addData("Off right", "");
            stackStatus = VisionCommand.stackStatus.OFFRIGHT;
        } else if (stackX < 380) {
            op.telemetry.addData("Off left", "");
            stackStatus = VisionCommand.stackStatus.OFFLEFT;
        } else {
            if (stackWid > 300) {
                op.telemetry.addData("done", "");
                stackStatus = VisionCommand.stackStatus.DONE;
            } else {
                op.telemetry.addData("Advacne", "");
                stackStatus = VisionCommand.stackStatus.ADVANCE;
            }
        }

        op.telemetry.addData("Width", stackWid);
        op.telemetry.addData("X-val", stackX);
        op.telemetry.update();
    }

    public void intakeVision() {
        grabber.grabSingleFrame();
        while (!grabber.isResultReady());

        ImageProcessorResult imageProcessorResult = grabber.getResult();

        intakeResult = (stoneResult) imageProcessorResult.getResult();

        if (intakeResult.centerPoint != null) {
            double stoneAng = intakeResult.rotation;

            boolean longwise = intakeResult.rect.boundingRect().height < intakeResult.rect.boundingRect().width;

            if (longwise) {

            } else {

            }

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

        //VuforiaLocalizer.CloseableFrame frame = null; //takes the frame at the head of the queue
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
        //stackResult = (stackResult) imageProcessorResult.getResult();
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
