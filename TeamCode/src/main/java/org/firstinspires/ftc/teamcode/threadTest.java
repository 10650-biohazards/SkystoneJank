package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Utilities.Utility;

@Autonomous (name = "Vision Thread Test")
public class threadTest extends LinearOpMode {

    visionThread vision = new visionThread(this);
    Utility u = new Utility(this);

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        vision.enable();

        while (opModeIsActive()) {

            while (!vision.isResultReady());

            Log.i("Opmode", "Glyph color: " + vision.color);
        }
    }
}
