package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import Utilities.Utility;
import ftc.vision.FrameGrabber;
import ftc.vision.Glyph.GlyphResult;
import ftc.vision.ImageProcessorResult;

public class visionThread implements Runnable {

    FrameGrabber grabber = FtcRobotControllerActivity.frameGrabber;

    GlyphResult.GlyphColor color = null;

    Utility u;

    LinearOpMode op;

    visionThread(LinearOpMode op) {
        this.op = op;
        u = new Utility(op);
    }

    public void enable() {
        Thread t = new Thread(this);
        t.start();
    }

    public boolean isResultReady() {
        return grabber.isResultReady();
    }

    @Override
    public void run() {

        while (op.opModeIsActive()) {
            grabber.grabSingleFrame();

            while (!grabber.isResultReady());

            ImageProcessorResult imageProcessorResult = grabber.getResult();
            GlyphResult result = (GlyphResult) imageProcessorResult.getResult();

            color = result.getGlyphColor();

            u.waitMS(40);
        }
    }
}
