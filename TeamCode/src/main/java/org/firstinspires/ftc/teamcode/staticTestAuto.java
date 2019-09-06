package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import FtcExplosivesPackage.ExplosiveNavX;

@Autonomous (name = "Static test")
public class staticTestAuto extends LinearOpMode {

    ExplosiveNavX gyro = new ExplosiveNavX(this, "41", 0);

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Angle", gyro.getYaw());
            telemetry.update();
        }

        staticTestTele.startAngle = gyro.getYaw();
    }
}
