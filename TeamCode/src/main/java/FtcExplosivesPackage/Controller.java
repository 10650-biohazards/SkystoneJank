package FtcExplosivesPackage;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Varun on 11/16/2017.
 */

public class Controller {
    Gamepad gamepad;
    public double joystickDeadzoneMagnitude, triggerDeadzoneMagnitude;
    DeadzoneType dt;

    public enum DeadzoneType{
        CIRCULAR,
        SQUARE
    }

    private enum InputType{
        JOYSTICK,
        TRIGGER
    }

    public Controller(Gamepad gamepad){
        this.gamepad = gamepad;
        this.joystickDeadzoneMagnitude = 0;
        this.triggerDeadzoneMagnitude = 0;
        this.dt = DeadzoneType.SQUARE;
    }

    public void setJoystickDeadzone(DeadzoneType dt, double mag){
        this.dt = dt;
        this.joystickDeadzoneMagnitude = mag;
    }

    public void setTriggerDeadzone(double mag){
        this.triggerDeadzoneMagnitude = mag;
    }

    public double lx(){
        return getOutput(InputType.JOYSTICK, gamepad.left_stick_x, gamepad.left_stick_y);
    }

    public double ly(){
        return getOutput(InputType.JOYSTICK, gamepad.left_stick_y, gamepad.left_stick_x);
    }

    public double rx(){
        return getOutput(InputType.JOYSTICK, gamepad.right_stick_x, gamepad.right_stick_y);
    }

    public double ry(){
        return getOutput(InputType.JOYSTICK, gamepad.right_stick_y, gamepad.right_stick_x);
    }

    public double rightTrigger(){
        return getOutput(InputType.TRIGGER, gamepad.right_trigger, gamepad.right_trigger);
    }

    public double leftTrigger(){
        return getOutput(InputType.TRIGGER, gamepad.left_trigger, gamepad.left_trigger);
    }

    public boolean leftStickPressed(){
        return gamepad.left_stick_button;
    }

    public boolean rightStickPressed(){
        return gamepad.right_stick_button;
    }

    public boolean leftBumper(){
        return gamepad.left_bumper;
    }

    public boolean rightBumper(){
        return gamepad.right_bumper;
    }

    public boolean dpadUp(){
        return gamepad.dpad_up;
    }

    public boolean dpadDown(){
        return gamepad.dpad_down;
    }

    public boolean dpadLeft(){
        return gamepad.dpad_left;
    }

    public boolean dpadRight(){
        return gamepad.dpad_right;
    }

    public boolean a(){
        return gamepad.a;
    }

    public boolean b(){
        return gamepad.b;
    }

    public boolean x(){
        return gamepad.x;
    }

    public boolean y(){
        return gamepad.y;
    }

    private double getOutput(InputType inputType, double input1, double input2){
        switch(inputType){
            case JOYSTICK: if(this.dt == DeadzoneType.CIRCULAR){
                                if(Math.sqrt(Math.pow(input1,2) + Math.pow(input2,2)) > joystickDeadzoneMagnitude) return input1;
                                else return 0;
                            } else if(this.dt == DeadzoneType.SQUARE){
                                if(Math.abs(input1) > joystickDeadzoneMagnitude) return input1;
                                else return 0;
                            }
            case TRIGGER: if(Math.abs(input1) > triggerDeadzoneMagnitude) return input1;
                          else return 0;
        }
        return 0;
    }
}