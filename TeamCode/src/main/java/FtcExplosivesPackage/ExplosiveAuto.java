package FtcExplosivesPackage;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Varun on 11/16/2017.
 */

public abstract class ExplosiveAuto extends LinearOpMode {

    public abstract void initHardware();

    public abstract void initAction();

    public abstract void body() throws InterruptedException;

    public abstract void exit() throws InterruptedException;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Initializing", "Started");
        telemetry.update();

        initHardware();
        initAction();

        telemetry.addData("Initializing", "Finished");
        telemetry.update();

        waitForStart();

        telemetry.addData("Body", "Started");
        telemetry.update();

        body();

        telemetry.addData("Body", "Finished");
        telemetry.update();

        exit();
    }
}
