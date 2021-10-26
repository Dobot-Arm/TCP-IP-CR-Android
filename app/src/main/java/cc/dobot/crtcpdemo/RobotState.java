package cc.dobot.crtcpdemo;

import cc.dobot.crtcpdemo.client.StateMessageClient;
import cc.dobot.crtcpdemo.message.constant.Robot;

public class RobotState {
    byte[]DI;
    byte[]DO;
    Robot.Mode mode;
    long controlTime;
    long time;
    long testValue;
    int safetyMode;
    int speedScaling;
    double linearMomentumNorm;
    double vMain;
    double vRobot;
    double iRobot;
    int programState;
    int safetyStauts;
    double []toolAccelerometerValues;
    double []elbowPosition;
    double []elbowVelocity;
    double []qTarget;
    double []qdTarget;
    double []qddTarget;
    double []iTarget;
    double []mTarget;
    double []qActual;
    double []toolVectorActual;


    public RobotState() {
        DI=new byte[]{0,0,0,0,0,0,0,0};
        DO=new byte[]{0,0,0,0,0,0,0,0};
        mode=Robot.Mode.ROBOT_MODE_NO_CONTROLLER;
        speedScaling=1;
        qActual=new double[]{0,0,0,0,0,0};
        toolVectorActual=new double[]{0,0,0,0,0,0};
    }

    public byte[] getDI() {
        return DI;
    }

    public void setDI(byte[] DI) {
        this.DI = DI;
    }

    public int getDIValueByIndex(int index){
        int i=index/8;
        byte DIArray=  this.DI[i];
        int mod=index-1-i*8;
        return DIArray>>mod&0x01;
    }
    public byte[] getDO() {
        return DO;
    }

    public void setDO(byte[] DO) {
        this.DO = DO;
    }

    public int getDOValueByIndex(int index){
        int i=index/8;
        byte DOArray= this.DO[i];
        int mod=index-1-i*8;
        return DOArray>>mod&0x01;
    }

    public Robot.Mode getMode() {
        return mode;
    }

    public void setMode(Robot.Mode mode) {
        this.mode = mode;
    }

    public int getSpeedScaling() {
        return speedScaling;
    }

    public void setSpeedScaling(int speedScaling) {
        this.speedScaling = speedScaling;
    }

    public int getProgramState() {
        return programState;
    }

    public void setProgramState(int programState) {
        this.programState = programState;
    }

    public double[] getqActual() {
        return qActual;
    }

    public void setqActual(double[] qActual) {
        this.qActual = qActual;
    }

    public double[] getToolVectorActual() {
        return toolVectorActual;
    }

    public void setToolVectorActual(double[] toolVectorActual) {
        this.toolVectorActual = toolVectorActual;
    }


}
