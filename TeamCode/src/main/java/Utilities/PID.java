package Utilities;

public class PID {

    public double P, I, D, bias, tol, tarVal, t = 0, initD, targetE, startTime, initE, timeElapsed, fullPowSecs, det;
    public double e;
    public double integral = 0, oldE, oldTime = System.currentTimeMillis();
    private final double SECS_PER_DEG = 42;
    private boolean turn, fullPow = false;

    private double timeD;

    //private PID turnPID = new PID();

    public PID (){

    }

    public void setup(double P, double I, double D, double bias, double tol, double tarVal){
        this.P = P;
        this.I = I;
        this.D = D;
        this.initD = bias;
        this.tol = tol;
        this.tarVal = tarVal;

        turn = false;

        e = tol + 1;
        oldE = tol + 2;

        oldTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
    }

    public void setup(double P, double I, double D, double tol, double tarVal, double inputVal, boolean irrelevant){
        this.P = P;
        this.I = I;
        this.initD = D;
        this.tol = tol;
        this.tarVal = tarVal;

        timeElapsed = 0;

        turn = true;
        startTime = System.currentTimeMillis();
        fullPowSecs = (initE - 8) * SECS_PER_DEG;

        initE = tarVal - inputVal;
        e = tol + 1;
    }

    public double status(double inputValue) {
        e = tarVal - inputValue;

        timeD = System.currentTimeMillis()- oldTime;

        det = (e - oldE) / (timeD / 1000);
        integral += e * (timeD / 1000);
        timeElapsed = System.currentTimeMillis() - startTime;


        double progress = initE - e;
        double targProgress;

        if (turn) {
            fullPow = e > 8;
            if (!fullPow) {
                double tanLine = 1 - ((timeElapsed - fullPowSecs) / 5);
                targProgress = (initE - 8) + (tanLine * (timeElapsed - fullPowSecs));

                //turnPID.setup(1.0,0.0,0.0,0.0,0.25,targProgress);

                //t = turnPID.status(progress);
            }
        }

        bias = e < 0 ? -initD : initD;
        oldE = e;
        oldTime = System.currentTimeMillis();
        return (P * e) + (I * integral) + (D * det) + bias;
    }

    public double status(double inputValue, boolean oh_no) {
        e = tarVal - inputValue;

        timeD = System.currentTimeMillis()- oldTime;

        det = (e - oldE) / (timeD / 1000);
        integral += e * (timeD / 1000);
        timeElapsed = System.currentTimeMillis() - startTime;


        double progress = initE - e;
        double targProgress;

        if (turn) {
            fullPow = e > 8;
            if (!fullPow) {
                double tanLine = 1 - ((timeElapsed - fullPowSecs) / 5);
                targProgress = (initE - 8) + (tanLine * (timeElapsed - fullPowSecs));

                //turnPID.setup(1.0,0.0,0.0,0.0,0.25,targProgress);

                //t = turnPID.status(progress);
            }
        }

        bias = initD;
        oldE = e;
        oldTime = System.currentTimeMillis();
        return (P * e) + (I * integral) + (D * det) + bias;
    }

    public boolean done() {return Math.abs(e)< tol;}

    public void reset() {
        det = 0;
        integral = 0;
    }

    public void adjTarg(double newTarg) {
        tarVal = newTarg;
    }

    public void adjP(double newP) {
        P = newP;
    }

    public void setTarget(double newTarg) {
        tarVal = newTarg;
    }
}