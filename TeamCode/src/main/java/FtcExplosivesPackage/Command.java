package FtcExplosivesPackage;

/**
 * Created by Varun on 11/16/2017.
 */

public abstract class Command implements Runnable {
    Thread t;
    private boolean isRunning = false, cancel = false;
    ExplosiveTele opMode;
    TelemetryLog cmdErrorLog;

    public Command(ExplosiveTele opMode, String name){
        this.t = new Thread(this);
        t.setName(name);
        this.opMode = opMode;
        cmdErrorLog = new TelemetryLog(opMode);
    }

    public void enable() {
        if (t == null) {
            cmdErrorLog.add("Attempted to start un-initialized Command. Remember to call super() in command constructor");
            throw new RuntimeException("Attempted to start un-initialized Command. Are you calling super() in the " +
                    "Command's constructor?");
        }
        t.start();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void close() {
        cancel = true;
    }

    public void forceStop() {
        cancel = true;
        t.interrupt();
        finish();
    }

    public abstract void init();

    public abstract void start();

    public abstract void loop();

    public abstract void stop();

    @Override
    public void run() {
        isRunning = true;

        init();

        while(!isStarted() && !cancel){
            try {
                Utils.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        start();

        while(!isLooping() && !cancel){
            try {
                Utils.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while(!isFinished() && !cancel){
            loop();
        }

        stop();

        finish();
    }

    private boolean isStarted(){
        return opMode.isStarted;

    }

    private boolean isLooping(){
        return opMode.isLooping;

    }

    private boolean isFinished(){
        return opMode.isFinished;

    }

    private void finish() {
        isRunning = false;
        t = null;
    }
}
