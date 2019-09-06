package FtcExplosivesPackage;

/**
 * Created by Varun on 2/7/2018.
 */

public interface ExplosivePIDEnabledHardware {
    double getOutput();
    double getLatency();
    void close();
}
