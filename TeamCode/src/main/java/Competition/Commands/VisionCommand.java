package Competition.Commands; /*

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.opencv.core.Point;

import Competition.RobotMap;
import FtcExplosivesPackage.BioCommand;
import FtcExplosivesPackage.BiohazardTele;
import ftc.vision.ImageProcessorResult;
import ftc.vision.SkyStone.stoneResult;
import ftc.vision.VufFrameGrabber;
import ftc.vision.judgeDay.trackResult;

public class VisionCommand extends BioCommand {

    public static Point intakeStoneLoc;
    public static double intakeStoneAng;
    public static stoneStatus status;

    stoneResult intakeResult;

    VufFrameGrabber intakeGrabber;



    public VisionCommand(BiohazardTele op) {
        super(op, "vision");
    }

    @Override
    public void init() {

        intakeGrabber = new VufFrameGrabber(FtcRobotControllerActivity.cameraBridgeViewBase, FtcRobotControllerActivity.FRAME_WIDTH_REQUEST, FtcRobotControllerActivity.FRAME_HEIGHT_REQUEST, RobotMap.stoneCam);

        intakeGrabber.grabSingleFrame();

        while (!intakeGrabber.isResultReady());

        ImageProcessorResult imageProcessorResult = intakeGrabber.getResult();
        intakeResult = (stoneResult) imageProcessorResult.getResult();
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        intakeVision();

    }

    public void intakeVision() {
        intakeGrabber.grabSingleFrame();
        while (!intakeGrabber.isResultReady());

        ImageProcessorResult imageProcessorResult = intakeGrabber.getResult();
        intakeResult = (stoneResult) imageProcessorResult.getResult();

        if (intakeResult.centerPoint != null) {
            Point stoneLoc = intakeResult.centerPoint;
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

    @Override
    public void stop() {

    }

    public enum stoneStatus {
        NONE,
        ONTARGET,
        TILTRIGHT,
        TILTLEFT
    }
}
*/