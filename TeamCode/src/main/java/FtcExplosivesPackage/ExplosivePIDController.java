package FtcExplosivesPackage;

/**
 * Created by FTC 9277 on 12/9/2017.
 */
public class ExplosivePIDController implements Runnable{
    ExplosivePIDEnabledHardware gyro;
    public boolean PIDEnabled = false, isTurning = false, isMoving = false, close = false, wasTurning = false;
    public double p, i, d, kP, kI, kD, currentAngle, expAngle, output, tolerance, lastP, lastTime, maxP;

    private Thread t;

    /**
     * Consructor for the PID controller
     * @param gyro ExplosiveBNO055 gyro(wrapped version of the integrated IMU)
     */
    public ExplosivePIDController(ExplosivePIDEnabledHardware gyro){
        this.gyro = gyro;

        kP = 0;
        currentAngle = 0;
        expAngle = 0;
        p = 0;
        i = 0;
        d = 0;
        lastP = 0;
        lastTime = System.currentTimeMillis();
        //movingScalar = 0;
        output = 0;
        tolerance = 0;
        maxP = 180;

        PIDEnabled = false;
        t = new Thread(this);
        t.start();
    }

    /**
     * Enabled PID
     */
    public void enable(){
        PIDEnabled = true;
    }

    /**
     * Enabled PID and set initial constants
     * @param kP Scalar constant for P
     * @param kI Scalar constant for I
     * @param kD Scalar constant for D
     */
    public void enable(double kP, double kI, double kD){
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        //this.movingScalar = movingScalar;
        PIDEnabled = true;
    }

    public void retune(double kP, double kI, double kD){
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    /**
     * Define whether robot is turning
     * @param isTurning True if turning, false if not
     */
    public void isTurning(boolean isTurning){
        this.isTurning = isTurning;
    }

    /**
     * Define whether robot is moving
     * @param isMoving True if moving, false if not
     */
    public void isMoving(boolean isMoving){
        this.isMoving = isMoving;
    }

    /**
     * Set the expected angle for the PID
     * @param target Expected angle in degrees
     */
    public void setTarget(double target){
        expAngle = target;
    }

    /**
     * Set the tolerance for PID error
     * @param tolerance Allowed tolerance in degrees
     */
    public void setTolerance(double tolerance){this.tolerance = tolerance;}

    /**
     * Resets PID to current angle
     */
    public void resetPID(){
        expAngle = currentAngle;
        p = 0;
        i = 0;
        d = 0;
        output = 0;
    }

    /**
     * Get PID Output
     * @return Output power
     */
    public double getOutput(){
        return output;
    }

    /**
     * Disable PID and stop thread
     */
    public void close(){
        gyro.close();
        PIDEnabled = false;
        close = true;
    }

    @Override
    public void run(){
        while(!close){
            if(PIDEnabled){
                currentAngle = gyro.getOutput();

                if(isTurning){
                    resetPID();
                    wasTurning = true;
                } else{
                    if(wasTurning){
                        resetPID();
                        wasTurning = false;
                        //currentAngle = gyro.getUpdatedYaw();
                        while(gyro.getLatency() > 5){
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        currentAngle = gyro.getOutput();
                        resetPID();
                    }

                    p = currentAngle - expAngle;

                    if(p > 180){
                        p -= 360;
                    } else if(p < -180){
                        p += 360;
                    }

                    if(Math.abs(p) < maxP/10){
                        i += p * (System.currentTimeMillis() - lastTime);
                        d = ((p - lastP)/(System.currentTimeMillis() - lastTime));
                    } else {
                        i = 0;
                        d = 0;
                    }


                    lastP = p;
                    lastTime = System.currentTimeMillis();

                    if(Utils.getSign(p) != Utils.getSign(i)) i = 0;

                    if(Math.abs(p) < Math.abs(tolerance)){
                        output = 0;
                    } else{
                        /*if(isMoving){
                            output = p * kP * movingScalar;
                        } else{
                            output = p * kP;
                        }*/
                        output = p * kP + i * kI + d * kD;
                    }
                }
            } else{
                try {
                    Utils.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
