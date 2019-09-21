package Competition;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import ftc.vision.ColorTesting.colorProcessor;
import ftc.vision.ColorTesting.colorResult;
import ftc.vision.ImageProcessorResult;
import ftc.vision.VufFrameGrabber;
import ftc.vision.judgeDay.trackResult;

@TeleOp (name = "OpenCV Vuforia Test")
public class CVVuforia extends OpMode {

    WebcamName webcam;
    VufFrameGrabber grabber;

    @Override
    public void init() {
        webcam = hardwareMap.get(WebcamName.class, "stoned cam");

        //grabber = FtcRobotControllerActivity.vufFrameGrabber;
        grabber.initCamera(webcam);

        grabber.setImageProcessor(new colorProcessor());
    }

    @Override
    public void loop() {

        grabber.grabSingleFrame();

        while (!grabber.isResultReady());

        ImageProcessorResult imageProcessorResult = grabber.getResult();
        colorResult color = (colorResult) imageProcessorResult.getResult();

    }
}
