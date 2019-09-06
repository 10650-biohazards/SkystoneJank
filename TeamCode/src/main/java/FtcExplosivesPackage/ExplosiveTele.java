package FtcExplosivesPackage;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Created by Varun on 11/16/2017.
 */

public abstract class ExplosiveTele extends OpMode {

    public Controller dController, mController;
    boolean isStarted, isLooping, isFinished;

    public abstract void initHardware();

    public abstract void initAction();

    public abstract void firstLoop();

    public abstract void bodyLoop();

    public abstract void exit();

    @Override
    public void init(){
        telemetry.addData("Initializing", "Started");
        telemetry.update();

        isFinished = false;
        isStarted = false;
        isLooping = false;

        dController = new Controller(gamepad1);
        mController = new Controller(gamepad2);

        initHardware();
        initAction();

        telemetry.addData("Initializing", "Finished");
        telemetry.update();
    }

    @Override
    public void start(){
        telemetry.addData("First Loop", "Started");
        telemetry.update();

        firstLoop();

        isStarted = true;

        telemetry.addData("First Loop", "Finished");
        telemetry.update();
    }

    @Override
    public void loop(){
        bodyLoop();
        isLooping = true;
    }

    @Override
    public void stop(){
        telemetry.addData("Exit", "Started");

        exit();

        isFinished = true;
    }
}
