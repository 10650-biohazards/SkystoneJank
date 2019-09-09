package FtcExplosivesPackage;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public abstract class BiohazardTele extends LinearOpMode {

    public Controller dController, mController;
    boolean isStarted, isLooping, isFinished;

    public abstract void initHardware();

    public abstract void initAction();

    public abstract void firstLoop();

    public abstract void bodyLoop();

    public abstract void exit();

    @Override
    public void runOpMode() throws InterruptedException {

        //INITIALIZATION
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

        waitForStart();


        //FIRST LOOP
        telemetry.addData("First Loop", "Started");
        telemetry.update();

        firstLoop();

        isStarted = true;

        telemetry.addData("First Loop", "Finished");
        telemetry.update();


        //BODY LOOP
        isLooping = true;
        while (opModeIsActive()) {
            bodyLoop();
        }


        //EXIT
        telemetry.addData("Exit", "Started");
        telemetry.update();
        exit();
        isFinished = true;
    }
}
