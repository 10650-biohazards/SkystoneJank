package Competition.Programs.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Competition.Settings;

public class updateSettings extends LinearOpMode {

    Settings settings = new Settings();

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Stack long ways? (Y/X)", "");
        telemetry.update();
        while (!gamepad1.x || gamepad1.y) {
            if (gamepad1.x) {
                settings.longStack = false;
            }
            if (gamepad1.y) {
                settings.longStack = true;
            }
        }

        telemetry.addData("Double wide stack? (Y/X)", "");
        telemetry.update();
        while (!gamepad1.x || gamepad1.y) {
            if (gamepad1.x) {
                settings.doubleStack = false;
            }
            if (gamepad1.y) {
                settings.doubleStack = true;
            }
        }

        if (settings.doubleStack) {
            telemetry.addData("Crisscross? (Y/X)", "");
            telemetry.update();
            while (!gamepad1.x || gamepad1.y) {
                if (gamepad1.x) {
                    settings.crisscross = false;
                }
                if (gamepad1.y) {
                    settings.crisscross = true;
                }
            }
        }


        requestOpModeStop();
    }
}
