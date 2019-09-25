package practice_meet_arnold;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

//@Autonomous(name = "Arnold Depot")
public class BlueAutoFar extends LinearOpMode {

    /**
     * First we get off the balancing stone,
     * and then we put the glyph in the correct cryptobox column.
     */
    @Override
    public void runOpMode() throws InterruptedException {
        SlightlyNewAutoMethodsShared auto = new SlightlyNewAutoMethodsShared(this);

        auto.setup();

        waitForStart();

        //get off stone
        auto.move_gripper(0.25);
        auto.move_straight(0.3, 2500);
        auto.move_gripper(0.45);
        auto.move_straight(0.3, -1000);
    }
}

