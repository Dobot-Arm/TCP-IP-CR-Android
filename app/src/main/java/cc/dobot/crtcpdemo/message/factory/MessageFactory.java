package cc.dobot.crtcpdemo.message.factory;

import com.xuhao.didi.core.iocore.interfaces.ISendable;

import java.util.HashMap;

import cc.dobot.crtcpdemo.message.constant.CmdSet;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageAccJ;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageAccL;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageAuto;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageClearError;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageDO;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageDOExecute;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageDisableRobot;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageEmergencyStop;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageEnableRobot;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageGetErrorID;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageJointMovJ;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageGetPathStartPose;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageGetPose;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageJointMovJ;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageManual;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageMovJ;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageMovL;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageMoveJog;
import cc.dobot.crtcpdemo.message.product.cr.CRMessagePowerOn;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageResetRobot;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageRobotMode;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageSetArmOrientation;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageSpeedFactor;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageStartPath;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageStopScript;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageTool;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageUser;


public class MessageFactory {

    private static MessageFactory instance;

    private MessageFactory() {

    }

    public static MessageFactory getInstance() {
        if (instance == null) {
            synchronized (MessageFactory.class) {
                if (instance == null) {
                    instance = new MessageFactory();
                }
            }
        }
        return instance;
    }

    public ISendable createMsg(String CMD_SET) {
        switch (CMD_SET) {
            case CmdSet.ENABLE_ROBOT:
                return new CRMessageEnableRobot();
            case CmdSet.DISABLE_ROBOT:
                return new CRMessageDisableRobot();
            case CmdSet.POWER_ON:
                return new CRMessagePowerOn();
            case CmdSet.CLEAR_ERROR:
                return new CRMessageClearError();
            case CmdSet.EMERGENCY_STOP:
                return new CRMessageEmergencyStop();
            case CmdSet.SPEED_FACTOR:
                return new CRMessageSpeedFactor();
            case CmdSet.DO:
                return new CRMessageDO();
            case CmdSet.MOV_L:
                return new CRMessageMovL();
            case CmdSet.MOV_J:
                return new CRMessageMovJ();
            case CmdSet.JOINT_MOV_J:
                return new CRMessageJointMovJ();
            case CmdSet.MOVE_JOG:
                return new CRMessageMoveJog();
            case CmdSet.DO_EXECUTE:
                return new CRMessageDOExecute();
            case CmdSet.TOOL:
                return new CRMessageTool();
            case CmdSet.USER:
                return new CRMessageUser();
            case CmdSet.SET_ARM_ORIENTATION:
                return new CRMessageSetArmOrientation();
            case CmdSet.ACC_J:
                return new CRMessageAccJ();
            case CmdSet.ACC_L:
                return new CRMessageAccL();
            case CmdSet.START_PATH:
                return new CRMessageStartPath();
            case CmdSet.STOP_SCRIPT:
                return new CRMessageStopScript();
            case CmdSet.RESET_ROBOT:
                return new CRMessageResetRobot();
            case CmdSet.GET_PATH_START_POSE:
                return new CRMessageGetPathStartPose();
            case CmdSet.GET_POSE:
                return new CRMessageGetPose();
            case CmdSet.GET_ERROR_ID:
                return new CRMessageGetErrorID();
            case CmdSet.AUTO:
                return new CRMessageAuto();
            case CmdSet.MANUAL:
                return new CRMessageManual();
        }
        return null;
    }


}
