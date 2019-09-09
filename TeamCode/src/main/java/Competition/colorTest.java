package Competition;

import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import FtcExplosivesPackage.ExplosiveTele;
import Gagarin.GagarinRobot;
import Utilities.PID;
import ftc.vision.ColorTesting.colorProcessor;
import ftc.vision.FrameGrabber;
import ftc.vision.ImageProcessorResult;
import ftc.vision.judgeDay.trackResult;

@TeleOp (name = "Color Test")
public class colorTest extends ExplosiveTele {
    String TAG = "tester";

    private FrameGrabber grabber = FtcRobotControllerActivity.frameGrabber;

    @Override
    public void initHardware() {
        grabber.setImageProcessor(new colorProcessor());
    }

    @Override
    public void initAction() {
        Log.i(TAG, "Init action");
    }

    @Override
    public void firstLoop() {
        Log.i(TAG, "First loop");
    }

    @Override
    public void bodyLoop() {
        grabber.grabSingleFrame();
        while (!grabber.isResultReady());

        ImageProcessorResult imageProcessorResult = grabber.getResult();
    }

    @Override
    public void exit() {

    }
}
