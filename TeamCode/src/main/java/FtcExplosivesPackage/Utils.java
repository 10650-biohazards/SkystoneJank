package FtcExplosivesPackage;

/**
 * Created by robotics9277 on 11/4/2017.
 */

public class Utils {
    public static double maxDouble(double... nums) {
        double currMax = Math.abs(nums[0]);

        for (double i : nums) {
            currMax = Math.abs(i) > currMax ? Math.abs(i) : currMax;
        }

        return currMax;
    }

    public static void sleep(long millis) throws InterruptedException{
        try {
            Thread.sleep(millis);
        } catch (Error e) {
            e.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static double getSquaredOutput(double input){
        double sign = getSign(input);
        double out = input * input;
        return out * sign;
    }

    public static double getSign(double input){
        if(input == 0) return 0;

        return ((input)/(Math.abs(input)));
    }

    public static double minMax(double input, double min, double max){
        if(input > max) return max;
        else if(input < min) return min;

        return input;
    }
}
