package practice_meet_arnold;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Arnold Auto", group = "Red")
public class ArnoldAutoMoveForward extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        PracticeMeetMethods auto = new PracticeMeetMethods(this);

        auto.setup();

        waitForStart();

        auto.move_straight(0.75, 1000);

    }

}
