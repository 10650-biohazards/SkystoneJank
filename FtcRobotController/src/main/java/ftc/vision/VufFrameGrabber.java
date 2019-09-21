package ftc.vision;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceView;

import com.qualcomm.ftcrobotcontroller.R;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class VufFrameGrabber implements CameraBridgeViewBase.CvCameraViewListener2 {

    WebcamName webcamName;

    VuforiaLocalizer locale;

    Mat mat = new Mat();

    public enum FrameGrabberMode {
        SINGLE,
        THROWAWAY,
        STOPPED
    }

    private FrameGrabber.FrameGrabberMode mode = FrameGrabber.FrameGrabberMode.STOPPED;

    private boolean saveImages;

    //the frame, the blank frame, and temporary images to flip the frame
    private Mat frame, blank, tmp1, tmp2;

    //logging tag
    private static final String TAG = "VufFrameGrabber";

    private boolean resultReady = false;

    //objects to run and store the result
    private ImageProcessor imageProcessor = null;
    private ImageProcessorResult result = null;

    //timing variables
    private long totalTime = 0, loopCount = 0, loopTimer = 0;

    public boolean isSaveImages() {
        return saveImages;
    }

    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    public FrameGrabber.FrameGrabberMode getMode() {
        return mode;
    }

    public void setSaveImages(boolean saveImages) {
        this.saveImages = saveImages;
    }

    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    public VufFrameGrabber(CameraBridgeViewBase cameraBridgeViewBase, int frameWidthRequest, int frameHeightRequest) {
        //cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);

        //cameraBridgeViewBase.setMinimumWidth(frameWidthRequest);
        //cameraBridgeViewBase.setMinimumHeight(frameHeightRequest);
        //cameraBridgeViewBase.setMaxFrameSize(frameWidthRequest, frameHeightRequest);
        //cameraBridgeViewBase.setCvCameraViewListener(this);
    }


    public void initCamera(WebcamName webcam) {
        //Start Vuforia
        webcamName = webcam;

        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.vuforiaLicenseKey = ""+
                "AT7gAkD/////AAAAmUwb/nuDM02ctd4TLa6P7oNFQDr8nKQlj6V4jxAg32zPHal3uS3JT0l8JrmS"+
                "ZtpEiw8oDdmBE1WZGqyAZBSkIoCSFt3/FpL0QN+9ujtS2TpEft8aU5SoAVchG+ANZ0/+VyDXbxFM"+
                "rMALB71bdd8BoVKBZ45KFiUMyRLwlmD/XzU8EZgnxGFK+WJbvJCADWOZpM8RBTHNe2ajirvT/uo6"+
                "IC9obkl9DXsFHjiv4BhXrh90TPV5kqxi8jfEInEuVyGlsMSs3MViQE626Ld6qekhx0djF/4Bto/d"+
                "QfQCr4z6HLnMW4l49ques9S9vnpmXhu5+mZfBSbFiUQdlxd0Zi34opSiDXfUC2gc1RxbvFuX5E6o";
        params.cameraName = webcamName;


        this.locale = ClassFactory.getInstance().createVuforia(params);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true); //enables RGB565 format for the image
        locale.setFrameQueueCapacity(1); //tells VuforiaLocalizer to only store one frame at a time
        //End Vuforia
    }

    private boolean isImageProcessorNull(){
        if(imageProcessor == null) {
            Log.e(TAG, "imageProcessor is null! Call setImageProcessor() to set it.");
            return true;
        } else {
            return false;
        }
    }

    public void grabSingleFrame(){

        //Begin Vuforia
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
            }
        }

        /*rgb is now the Image object that we’ve used in the video*/

        mat = new Mat();
        if (rgb != null) {
            Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);

            Utils.bitmapToMat(bm, mat);
        }
        //End Vuforia


        if(isImageProcessorNull()) return;
        mode = FrameGrabber.FrameGrabberMode.SINGLE;
        resultReady = false;
    }

    public void throwAwayFrames(){
        mode = FrameGrabber.FrameGrabberMode.THROWAWAY;
        resultReady = false;
    }

    public void stopFrameGrabber(){
        mode = FrameGrabber.FrameGrabberMode.STOPPED;
        totalTime = 0;
        loopCount = 0;
        loopTimer = 0;
    }

    //getter for the result
    public boolean isResultReady(){
        return resultReady;
    }
    public ImageProcessorResult getResult(){
        return result;
    }

    private void processFrame(){
        if(imageProcessor == null){
            return;
        }
        //start the loop timer
        if(mode == FrameGrabber.FrameGrabberMode.SINGLE){
            loopTimer = System.nanoTime();
        }
        long frameTime = System.currentTimeMillis();

        //get the rgb alpha image
        tmp1 = mat;
        ImageUtil.rotate(tmp1, frame, 90);

        //process the image using the provided imageProcessor
        result = imageProcessor.process(frameTime, frame, saveImages); //process
        frame = result.getFrame(); //get the output frame
        Log.i(TAG, "Result: " + result);

        Log.i(TAG, "frame size: " + frame.size());

        Core.transpose(frame, tmp1);
        Imgproc.resize(tmp1, tmp2, tmp2.size(), 0, 0, 0);
        Core.transpose(tmp2, frame);

        //Loop timer
        long now = System.nanoTime();
        long loopTime = now - loopTimer;
        if(loopTimer > 0) {
            loopCount++;
            totalTime += loopTime;
            Log.i(TAG, "LOOP #" + loopCount);
            Log.i(TAG, "LOOP TIME: " + loopTime / 1000000.0 + " ms");
            Log.i(TAG, "AVERAGE LOOP TIME: " + totalTime / 1000000.0 / loopCount + " ms");
            Log.i(TAG, "ESTIMATED AVERAGE FPS: " + 1000.0 * 1000000.0 * loopCount / totalTime);
        }
        loopTimer = now;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        //create the frame and tmp images
        frame = new Mat(height, width, CvType.CV_8UC4, new Scalar(0,0,0));
        blank = new Mat(height, width, CvType.CV_8UC4, new Scalar(0,0,0));
        tmp1 = new Mat(height, width, CvType.CV_8UC4);
        tmp2 = new Mat(width, height, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if(mode == FrameGrabber.FrameGrabberMode.SINGLE){ //if a single frame was requested
            processFrame(); //process it
            stopFrameGrabber(); //and stop grabbing
            resultReady = true;
        }
        else if(mode == FrameGrabber.FrameGrabberMode.THROWAWAY) { //if throwing away frames
            return blank;
        }
        else if(mode == FrameGrabber.FrameGrabberMode.STOPPED) { //if stopped
            //wait for a frame request from the main program
            //in the meantime hang to avoid grabbing extra frames and wasting battery
            resultReady = true;
            while (mode == FrameGrabber.FrameGrabberMode.STOPPED) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            resultReady = false;
        } else {
            stopFrameGrabber(); //paranoia
        }
        return frame; //this is displayed on the screen
    }
}
