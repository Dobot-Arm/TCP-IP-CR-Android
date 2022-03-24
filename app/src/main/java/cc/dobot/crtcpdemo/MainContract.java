package cc.dobot.crtcpdemo;

import java.util.List;

import cc.dobot.crtcpdemo.data.AlarmData;
import cc.dobot.crtcpdemo.message.constant.Robot;

public interface MainContract {
    interface View{
        void refreshConnectionState(boolean isConnected);

        void refreshPowerState(boolean isPowerOn);

        void refreshEnableState(boolean isEnable);

        void refreshSpeedScaling(int speedScaling);

        void refreshRobotMode(Robot.Mode mode);

        void refreshDI(byte[] DI);

        void refreshDO(byte[] DO);

        void refreshQActual(double[] getqActual);

        void refreshToolVectorActual(double[] toolVectorActual);

        void refreshProgramState(int programState);

        void refreshErrorList(String errorInfo);

        void refreshLogList(boolean isSend,String log);

        void refreshAlarmList(List<AlarmData> dataList);
    }

    interface Present{
        boolean isConnected();
        void connectRobot(String currentIP, int dashPort, int movePort, int feedBackPort);
        void disconnectRobot();

        boolean isPowerOn();
        void setRobotPower(boolean powerOn);

        boolean isEnable();
        void setRobotEnable(boolean enable);
        void resetRobot();

        void clearAlarm();

        void setSpeedRatio(int speedRatio);

        void emergencyStop();

        void doMovJ(double[] point);
        void doMovL(double[] point);

        void doJointMovJ(double[] point);

        void stopMove();
        void stopScript();

        void startPathTrack(String path);

        void setIO(int index, int value);
        int getIO(int index);

        void setJogMove(boolean isCoordinate,int pos);

        double[] getCurrentCoordinate();


        double[] getCurrentJoint();



    }
}
