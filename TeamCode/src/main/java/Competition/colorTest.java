package Competition;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import FtcExplosivesPackage.BiohazardTele;
import Gagarin.GagarinRobot;
import Utilities.PID;
import ftc.vision.FrameGrabber;
import ftc.vision.ImageProcessorResult;
import ftc.vision.SkyStone.stackProcessor;
import ftc.vision.SkyStone.stackResult;

@TeleOp (name = "Color Test")
public class colorTest extends BiohazardTele {
    String TAG = "tester";

    GagarinRobot robot;

    private stackResult newResult;

    PID turnPID = new PID();

    private FrameGrabber grabber = FtcRobotControllerActivity.frameGrabber;

    @Override
    public void initHardware() {
        robot = new GagarinRobot(this);

        grabber.setImageProcessor(new stackProcessor());

        robot.bright.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.fright.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.bleft.setDirection(DcMotorSimple.Direction.FORWARD);
        robot.fleft.setDirection(DcMotorSimple.Direction.FORWARD);

        turnPID.setup(0.05, 0, 0, 0, 0.5, 0);
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

        newResult = (stackResult) imageProcessorResult.getResult();

        telemetry.addData(newResult.toString(), "");

        double brp, frp, blp, flp;

        if (newResult.xCoord > 425) {
            brp = -1;
            frp = 1;
            blp = 1;
            flp = -1;
            telemetry.addData("SLIDE TO THE RIGHT", "");
        } else if (newResult.xCoord < 375) {
            brp = 1;
            frp = -1;
            blp = -1;
            flp = 1;
            telemetry.addData("SLIDE TO THE LEFT", "");
        } else {
            if (newResult.width > 400) {
                brp = 0;
                frp = 0;
                blp = 0;
                flp = 0;
                telemetry.addData("TARGET ACHIEVED", "");
            } else {
                brp = -0.3;
                frp = -0.3;
                blp = -0.3;
                flp = -0.3;
                telemetry.addData("ADVANCE!", "");
            }
        }

        double mod = 0;
        if (Math.abs(newResult.xCoord - 88) < 40) {
            mod = turnPID.status(robot.gyro.getYaw());
            telemetry.addData("Modifier", mod);
        }

        setPows(brp + mod, frp + mod, blp - mod, flp - mod);

        telemetry.update();
    }

    private void setPows(double brp, double frp, double blp, double flp) {
        robot.bright.setPower(brp);
        robot.fright.setPower(frp);
        robot.bleft.setPower(blp);
        robot.fleft.setPower(flp);
    }

    @Override
    public void exit() {

    }
}
