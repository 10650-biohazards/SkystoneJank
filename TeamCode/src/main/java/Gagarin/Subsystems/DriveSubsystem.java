package Gagarin.Subsystems;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import FtcExplosivesPackage.ExplosiveNavX;
import FtcExplosivesPackage.Subsystem;
import Utilities.driveTracker;
import Utilities.PID;
import Utilities.Utility;

public class DriveSubsystem extends Subsystem {

    public ExplosiveNavX gyro;
    public ModernRoboticsI2cRangeSensor ultra;
    public DcMotor fleft, fright, bleft, bright;
    private LinearOpMode op;
    Utility u;
    PID PID = new PID();

    private PID anglePID;
    PID powerPID;

    driveTracker track;

    public DriveSubsystem(LinearOpMode op, DcMotor fleft, DcMotor fright, DcMotor bleft, DcMotor bright, ExplosiveNavX gyro,
                          ModernRoboticsI2cRangeSensor ultra) {
        super(op);
        this.gyro = gyro;
        this.bleft = bleft;
        this.fleft = fleft;
        this.fright = fright;
        this.bright = bright;
        this.ultra = ultra;
        this.op = op;
        u = new Utility(op);
    }

    public void setTracker(driveTracker track) {
        this.track = track;
    }

    public void straight_hold_ang(int targetDist, int catchTime) {
        double mod = 0;
        PID.setup(0.0003,0, 0.2,42, 42, targetDist + bright.getCurrentPosition());
        PID angPID = new PID();
        angPID.setup(0.02, 0, 42, 0.1, 0.5, gyro.getYaw());
        u.startTimer(catchTime);
        op.telemetry.addData("pow", PID.status(bright.getCurrentPosition()));
        op.telemetry.addData("enc", bright.getCurrentPosition());
        double power;

        while(!PID.done() && !u.timerDone()) {
            mod = 0;

            op.telemetry.addData("pow", PID.status(bright.getCurrentPosition()));
            op.telemetry.addData("enc", bright.getCurrentPosition());
            op.telemetry.update();
            power = PID.status(bright.getCurrentPosition());
            power = u.limit(1, -1, power);

            if (!angPID.done()) {
                mod = angPID.status(gyro.getYaw());
            }

            set_Pows(power - mod,-power + mod, power + mod,-power - mod);

            if (!op.opModeIsActive()) {
                return;
            }
        }

        set_Pows(0,0,0,0);
        u.waitMS(200);
    }

    public void move_straight_PID(int targetDist, int catchTime) {
        PID.setup(0.0002,0, 0, 0.2,42, targetDist - bright.getCurrentPosition());
        double target = targetDist - bright.getCurrentPosition();
        u.startTimer(catchTime);
        //op.telemetry.addData("pow", PID.status(bright.getCurrentPosition()));
        //op.telemetry.addData("enc", bright.getCurrentPosition());
        double power;

        while(!PID.done() && !u.timerDone()) {
            op.telemetry.addData("pow", PID.status(-bright.getCurrentPosition()));
            op.telemetry.addData("enc", -bright.getCurrentPosition());
            op.telemetry.addData("target", target);
            op.telemetry.update();
            power = PID.status(-bright.getCurrentPosition());
            set_Pows(-power,-power,-power,-power);

            if (!op.opModeIsActive()) {
                return;
            }
        }

        set_Pows(0,0,0,0);
        u.waitMS(200);
    }

    public void move_straight_PID(int targetDist) {
        move_straight_PID(targetDist,3000);
    }

    public void move_straight_raw(double targetDist) {
        PID.setup(0.0002,0, 0, 0.2,42, targetDist - bright.getCurrentPosition());
        double target = targetDist - bright.getCurrentPosition();
        u.startTimer(3000);
        //op.telemetry.addData("pow", PID.status(bright.getCurrentPosition()));
        //op.telemetry.addData("enc", bright.getCurrentPosition());
        double power;

        while(!PID.done() && !u.timerDone()) {
            op.telemetry.addData("pow", PID.status(-bright.getCurrentPosition()));
            op.telemetry.addData("enc", -bright.getCurrentPosition());
            op.telemetry.addData("target", target);
            op.telemetry.addData("Delta Encoder", track.deltaEnc);
            op.telemetry.update();

            set_Pows(-0.3,-0.3,-0.3,-0.3);

            power = PID.status(-bright.getCurrentPosition());
            set_Pows(-power,-power,-power,-power);


            if (!op.opModeIsActive()) {
                return;
            }
            track.refresh();
        }

        set_Pows(0,0,0,0);
        u.waitMS(2000);
        track.refresh();
    }

    public void move_turn_gyro(double targetAng, int catchTime){
        PID.setup(0.1/*0.004*/, 0, 0, 0.07, 0.5, targetAng);
        u.startTimer(catchTime);
        double power;
        while (!PID.done() && !u.timerDone()) {
            op.telemetry.addData("pow", PID.status(gyro.getYaw()));
            op.telemetry.addData("ang", gyro.getYaw());
            op.telemetry.addData("time", System.currentTimeMillis() - u.startTime);
            op.telemetry.update();

            power = PID.status(gyro.getYaw());
            set_Pows(power,power,-power,-power);

            if (!op.opModeIsActive()) {
                return;
            }
        }

        set_Pows(0,0,0,0);
        u.waitMS(200);
    }

    public void move_turn_gyro(double targetAng) {
        move_turn_gyro(targetAng, 3000);
    }

    public void swing_turn_PID(double targetAng, boolean right){
        PID breakPIDF = new PID();
        PID breakPIDB = new PID();
        breakPIDF.setup(0.05, 0, 0, 0, 0, 0);
        breakPIDB.setup(0.05, 0, 0, 0, 0, 0);

        PID.setup(0.04, 0, 0, 0.07, 0.5, targetAng);
        u.startTimer(5000);
        double power;

        if (right) {
            breakPIDB.setTarget(bright.getCurrentPosition());
            breakPIDF.setTarget(fright.getCurrentPosition());
        } else {
            breakPIDB.setTarget(bleft.getCurrentPosition());
            breakPIDF.setTarget(fleft.getCurrentPosition());
        }


        while (!PID.done() && !u.timerDone()) {
            double currAng = gyro.getYaw();
            if (currAng < 0) {
                currAng += 360;
            }
            power = PID.status(currAng);

            op.telemetry.addData("Target", targetAng);
            op.telemetry.addData("pow", PID.status(gyro.getYaw()));
            op.telemetry.addData("ang", currAng);
            op.telemetry.addData("time", System.currentTimeMillis() - u.startTime);
            op.telemetry.update();

            if (right) {
                set_Pows(breakPIDB.status(bright.getCurrentPosition()), breakPIDF.status(fright.getCurrentPosition()), -power, -power);
            } else {
                set_Pows(-power, -power, breakPIDB.status(bleft.getCurrentPosition()), breakPIDF.status(fleft.getCurrentPosition()));
            }

            if (!op.opModeIsActive()) {
                return;
            }

            track.refresh();
        }

        set_Pows(0,0,0,0);

        u.waitMS(2000);
        track.refresh();
    }

    public void swing_turn_gyro(double targetAng, boolean right) {
        u.startTimer(10000);
        boolean done = false;
        while (!done && !u.timerDone()) {
            op.telemetry.addData("ang", gyro.getYaw());
            op.telemetry.addData("time", System.currentTimeMillis() - u.startTime);
            op.telemetry.update();
            if (right) {
                set_Pows(0, 0, -1, -1);
            } else {
                set_Pows(1, 1, 0, 0);
            }

            if (right && gyro.getYaw() > targetAng - 0.5) {
                done = true;
            }

            if (!right && gyro.getYaw() < targetAng + 0.5) {
                done = true;
            }

            if (!op.opModeIsActive()) {
                return;
            }
        }
    }

    public double optimizeAngle(double input) {
        input = input % 360;
        if (input < 0) {
            input += 360;
        }

        return input;
    }

    public void followWall(double angle, boolean forward) {
        anglePID = new PID();
        powerPID = new PID();

        double angleThingy;

        anglePID.setup(5, 0, 0, 0, 42, 3);

        int startEnc = bright.getCurrentPosition();

        while (2500 > Math.abs(bright.getCurrentPosition() - startEnc)) {
            double currDist = ultra.getDistance(DistanceUnit.INCH);

            double currAngle = refine_ang(gyro.getYaw());

            currAngle -= angle;
            currAngle = Math.toRadians(currAngle);

            currDist = currDist * Math.cos(currAngle);

            if (forward) {
                angleThingy = angle + anglePID.status(currDist);
            } else {
                angleThingy = angle - anglePID.status(currDist);
            }

            if (Double.isNaN(angleThingy)) {
                op.telemetry.addData("REEEEEEEEE!!!!", " ");
                anglePID.reset();
                angleThingy = forward ? angle + 15 : angle - 15;
            }

            powerPID.setup(0.05, 0, 0, 0.05, 42, angleThingy);

            double rightPow;
            double leftPow;

            double status = powerPID.status(gyro.getYaw());

            if (forward) {
                rightPow = -status + 1;
                leftPow = status + 1;
            } else {
                rightPow = -status - 1;
                leftPow = status - 1;
            }


            set_Pows(-rightPow, -rightPow, -leftPow, -leftPow);

            if (!op.opModeIsActive()) {
                set_Pows(0,0,0,0);
                return;
            }

            op.telemetry.addData("Raw dist", ultra.getDistance(DistanceUnit.INCH));
            op.telemetry.addData("Refined dist", currDist);
            op.telemetry.addData("Raw Ang", refine_ang(gyro.getYaw()));
            op.telemetry.addData("target Ang", angleThingy);
            op.telemetry.addData("Left Power", leftPow);
            op.telemetry.addData("Right Power", rightPow);
            op.telemetry.update();
        }
        set_Pows(0,0,0,0);
    }

    private static double refine_ang(double ang) {
        ang %= 360;
        ang += ang < 0 ? 360 : 0;
        return ang;
    }

    private static double torads(double ang) {
        return ang * (Math.PI / 180);
    }

    public void omni_drive(double targetang, double power) {
        double refinedAng;
        double aAng;
        double bAng;

        double aPowrr;
        double bPowrr;

        targetang = refine_ang(targetang);

        refinedAng = refine_ang(gyro.getYaw());
        refinedAng = targetang - refinedAng;
        refinedAng = refine_ang(refinedAng);


        System.out.println(refinedAng);


        aAng = refinedAng + 45;
        bAng = refinedAng - 45;

        if ((refinedAng < 90 && refinedAng > 0) || (refinedAng < 270 && refinedAng > 180)) {
            aPowrr = refinedAng < 90 ? 1 : -1;
            bPowrr = Math.cos(torads(aAng)) / Math.cos(torads(bAng));
            bPowrr *= refinedAng > 180 ? -1 : 1;
        } else {
            aPowrr = -Math.cos(torads(bAng)) / Math.cos(torads(aAng));
            aPowrr *= refinedAng > 270 ? -1 : 1;
            bPowrr = refinedAng < 180 ? -1 : 1;
        }

        aPowrr *= power;
        bPowrr *= power;
        set_Pows(bPowrr, aPowrr, aPowrr, bPowrr);
        op.telemetry.addData("aAngle", aAng);
        op.telemetry.addData("bAngle", bAng);
    }

    public void auto_omni(int dist, int ang, int catchtime){
        PID.setup(0.0003,0, 0, 0.2,42, dist + bright.getCurrentPosition());
        u.startTimer(catchtime);

        while(!PID.done() && !u.timerDone()) {

            omni_drive(ang, PID.status(bright.getCurrentPosition()));

            if (!op.opModeIsActive()) {
                return;
            }
        }
        omni_drive(42, 0);
    }

    public void omni_turn(double targetang, double power, double targetAng) {
        PID turn = new PID();
        turn.setup(0.05, 0, 0, 0.1, 0.25, targetAng);
        double refinedAng;
        double aAng;
        double bAng;

        double aPowrr;
        double bPowrr;

        targetang = refine_ang(targetang);

        refinedAng = refine_ang(gyro.getYaw());
        refinedAng = targetang - refinedAng;
        refinedAng = refine_ang(refinedAng);


        System.out.println(refinedAng);


        aAng = refinedAng + 45;
        bAng = refinedAng - 45;

        if ((refinedAng < 90 && refinedAng > 0) || (refinedAng < 270 && refinedAng > 180)) {
            aPowrr = refinedAng < 90 ? 1 : -1;
            bPowrr = Math.cos(torads(aAng)) / Math.cos(torads(bAng));
            bPowrr *= refinedAng > 180 ? -1 : 1;
        } else {
            aPowrr = -Math.cos(torads(bAng)) / Math.cos(torads(aAng));
            aPowrr *= refinedAng > 270 ? -1 : 1;
            bPowrr = refinedAng < 180 ? -1 : 1;
        }

        aPowrr *= power;
        bPowrr *= power;

        double mod = 0;
        if (!turn.done()){
            mod = turn.status(gyro.getYaw());
        }

        set_Pows(bPowrr - mod, aPowrr - mod, aPowrr + mod, bPowrr + mod);
        op.telemetry.addData("aAngle", aAng);
        op.telemetry.addData("bAngle", bAng);
    }

    public void move_strafe_PID(int targetDist, int catchTime){
        PID.setup(0.0002, 0, 0, 0.2, 42, targetDist + bright.getCurrentPosition());
        u.startTimer(catchTime);
        double power;
        do {
            op.telemetry.addData("pow", PID.status(bright.getCurrentPosition()));
            op.telemetry.addData("enc", bright.getCurrentPosition());
            op.telemetry.update();
            power = PID.status(bright.getCurrentPosition());
            set_Pows(power,-power,-power,power);
        } while(!PID.done() && !u.timerDone() && op.opModeIsActive());
        set_Pows(0,0,0,0);
        u.waitMS(200);
    }

    public void set_Pows(double brp, double frp, double blp, double flp) {
        bright.setPower(brp);
        fright.setPower(frp);
        bleft.setPower(blp);
        fleft.setPower(flp);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void stop() {

    }
}