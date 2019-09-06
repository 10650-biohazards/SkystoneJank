package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import FtcExplosivesPackage.ExplosiveNavX;

@TeleOp (name = "Static Test")
public class staticTestTele extends OpMode {

    static double startAngle;
    ExplosiveNavX gyro = new ExplosiveNavX(this, "41", startAngle);

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        telemetry.addData("Starting Angle", startAngle);
        telemetry.addData("Raw gyro", gyro.getRaw());
        telemetry.addData("Fixed gyro", gyro.getYaw());
        telemetry.update();
    }
}
