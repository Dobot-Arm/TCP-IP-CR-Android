package cc.dobot.crtcpdemo;

import android.os.Handler;

import com.xuhao.didi.core.pojo.OriginalData;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.NumberFormat;

import cc.dobot.crtcpdemo.client.APIMessageClient;
import cc.dobot.crtcpdemo.client.MessageCallback;
import cc.dobot.crtcpdemo.client.MoveMessageClient;
import cc.dobot.crtcpdemo.client.StateMessageClient;
import cc.dobot.crtcpdemo.message.base.BaseMessage;
import cc.dobot.crtcpdemo.message.constant.CmdSet;
import cc.dobot.crtcpdemo.message.factory.MessageFactory;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageClearError;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageDOExecute;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageEmergencyStop;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageJointMovJ;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageMovJ;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageMovL;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageMoveJog;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageResetRobot;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageSetArmOrientation;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageSpeedFactor;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageStartPath;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageStopScript;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageTool;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageUser;

public class MainPresent implements MainContract.Present, StateMessageClient.StateRefreshListener {
    Handler handler = new Handler();
    MainContract.View view;
    boolean isConnected;
    boolean isPowerOn;
    boolean isEnable;
    boolean isInit = false;

    public MainPresent(MainContract.View view) {
        this.view = view;

    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void connectRobot(String currentIP, int dashPort, int movePort, int feedBackPort) {

        StateMessageClient.getInstance().initTcp(currentIP, feedBackPort);
        StateMessageClient.getInstance().setListener(this);
        MoveMessageClient.getInstance().initTcp(currentIP, movePort);
        APIMessageClient.getInstance().initTcp(currentIP, dashPort);
        StateMessageClient.getInstance().connect();
        MoveMessageClient.getInstance().connect();
        APIMessageClient.getInstance().connect();
        isConnected = true;
        isInit = true;
        view.refreshConnectionState(true);
        view.refreshLogList(true,"Connect Robot");
    }

    @Override
    public void disconnectRobot() {
        StateMessageClient.getInstance().disConnect();
        MoveMessageClient.getInstance().disConnect();
        APIMessageClient.getInstance().disConnect();
        isConnected = false;
        view.refreshConnectionState(false);
        view.refreshLogList(true,"Disconnect Robot");
    }

    @Override
    public boolean isPowerOn() {
        return isPowerOn;
    }

    @Override
    public void setRobotPower(final boolean powerOn) {
        BaseMessage message;
        message = (BaseMessage) MessageFactory.getInstance().createMsg(CmdSet.POWER_ON);
        view.refreshLogList(true,message.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(message, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                isPowerOn = powerOn;
                System.out.println("setRobotPower msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }

    @Override
    public boolean isEnable() {
        return isEnable;
    }


    @Override
    public void setRobotEnable(final boolean enable) {

        BaseMessage message;
        if (enable)
            message = (BaseMessage) MessageFactory.getInstance().createMsg(CmdSet.ENABLE_ROBOT);
        else
            message = (BaseMessage) MessageFactory.getInstance().createMsg(CmdSet.DISABLE_ROBOT);
        view.refreshLogList(true,message.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(message, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                isEnable = enable;
                if (msg != null && state == MsgState.MSG_REPLY) {
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.refreshEnableState(isEnable);
                            // if (isEnable) {
                           /*     CRMessageAccJ crMessageAccJ = (CRMessageAccJ) MessageFactory.getInstance().createMsg(CmdSet.ACC_J);
                                CRMessageAccL crMessageAccL = (CRMessageAccL) MessageFactory.getInstance().createMsg(CmdSet.ACC_L);
                                crMessageAccJ.setR(50);
                                crMessageAccL.setR(50);
                                APIMessageClient.getInstance().sendMsg(crMessageAccJ, new MessageCallback() {
                                    @Override
                                    public void onMsgCallback(MsgState state, OriginalData msg) {
                                        System.out.println("crMessageAccJ msgState:" + state);

                                    }
                                });
                                APIMessageClient.getInstance().sendMsg(crMessageAccL, new MessageCallback() {
                                    @Override
                                    public void onMsgCallback(MsgState state, OriginalData msg) {
                                        System.out.println("crMessageAccL msgState:" + state);
                                    }
                                });*/
                                   /* setUser(0);
                                    setTool(0);
                                    setArmOrientation(1,1,-1,1);*/
                            //  }
                        }
                    });
                } else
                    System.out.println("setRobotEnable msgState:" + state);
            }
        });
    }

    @Override
    public void resetRobot() {
        CRMessageResetRobot crMessageResetRobot=(CRMessageResetRobot)MessageFactory.getInstance().createMsg(CmdSet.RESET_ROBOT);
        view.refreshLogList(true,crMessageResetRobot.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(crMessageResetRobot, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("setUser msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }

    private void setUser(int index) {
        CRMessageUser crMessageUser = (CRMessageUser) MessageFactory.getInstance().createMsg(CmdSet.USER);
        crMessageUser.setIndex(index);
        view.refreshLogList(true,crMessageUser.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(crMessageUser, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("setUser msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }

    private void setTool(int index) {
        CRMessageTool crMessageTool = (CRMessageTool) MessageFactory.getInstance().createMsg(CmdSet.TOOL);
        crMessageTool.setIndex(index);
        view.refreshLogList(true,crMessageTool.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(crMessageTool, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("setTool msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }


    private void setArmOrientation(int r, int d, int n, int cfg) {
        CRMessageSetArmOrientation crMessageSetArmOrientation = (CRMessageSetArmOrientation) MessageFactory.getInstance().createMsg(CmdSet.SET_ARM_ORIENTATION);
        crMessageSetArmOrientation.setLorR(r);
        crMessageSetArmOrientation.setUorD(d);
        crMessageSetArmOrientation.setForN(n);
        crMessageSetArmOrientation.setConfig6(cfg);
        view.refreshLogList(true,crMessageSetArmOrientation.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(crMessageSetArmOrientation, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("setArmOrientation msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }

    @Override
    public void clearAlarm() {
        CRMessageClearError crMessageClearError= (CRMessageClearError) MessageFactory.getInstance().createMsg(CmdSet.CLEAR_ERROR);
        view.refreshLogList(true,crMessageClearError.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(crMessageClearError, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("clearAlarm msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }

    @Override
    public void setSpeedRatio(int speedRatio) {
        CRMessageSpeedFactor msg = (CRMessageSpeedFactor) MessageFactory.getInstance().createMsg(CmdSet.SPEED_FACTOR);
        msg.setRatio(speedRatio);
        view.refreshLogList(true,msg.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(msg, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("setSpeedRatio msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }

    @Override
    public void emergencyStop() {
        CRMessageEmergencyStop msg= (CRMessageEmergencyStop) MessageFactory.getInstance().createMsg(CmdSet.EMERGENCY_STOP);
        view.refreshLogList(true,msg.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(msg, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("emergencyStop msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }

    @Override
    public void doMovJ(final double[] point) {
        BaseMessage message;
        message = (BaseMessage) MessageFactory.getInstance().createMsg(CmdSet.ENABLE_ROBOT);
        view.refreshLogList(true,message.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(message, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                if (msg != null && state == MsgState.MSG_REPLY) {
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
                    CRMessageMovJ crMessageMovJ = (CRMessageMovJ) MessageFactory.getInstance().createMsg(CmdSet.MOV_J);
                    crMessageMovJ.setPoint(point);
                    view.refreshLogList(true,crMessageMovJ.getMessageStringContent());
                    MoveMessageClient.getInstance().sendMsg(crMessageMovJ, null);
                    isRunPath=true;
                }
            }
        });
    }

    @Override
    public void doMovL(final double[] point) {
        BaseMessage message;
        message = (BaseMessage) MessageFactory.getInstance().createMsg(CmdSet.ENABLE_ROBOT);
        view.refreshLogList(true,message.getMessageStringContent());

        APIMessageClient.getInstance().sendMsg(message, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                if (msg != null && state == MsgState.MSG_REPLY) {
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
                    CRMessageMovL crMessageMovL = (CRMessageMovL) MessageFactory.getInstance().createMsg(CmdSet.MOV_L);
                    crMessageMovL.setPoint(point);
                    view.refreshLogList(true,crMessageMovL.getMessageStringContent());
                    MoveMessageClient.getInstance().sendMsg(crMessageMovL, null);
                }
            }
        });

    }

    @Override
    public void doJointMovJ(final double[] point) {
        BaseMessage message;
        message = (BaseMessage) MessageFactory.getInstance().createMsg(CmdSet.ENABLE_ROBOT);
        view.refreshLogList(true,message.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(message, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                if (msg != null && state == MsgState.MSG_REPLY) {
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
                    CRMessageJointMovJ crMessageJointMovJ = (CRMessageJointMovJ) MessageFactory.getInstance().createMsg(CmdSet.JOINT_MOV_J);
                    crMessageJointMovJ.setPoint(point);
                    view.refreshLogList(true,crMessageJointMovJ.getMessageStringContent());
                    MoveMessageClient.getInstance().sendMsg(crMessageJointMovJ, null);
                }
            }
        });
    }

    @Override
    public void stopMove() {
        CRMessageMoveJog msg = (CRMessageMoveJog) MessageFactory.getInstance().createMsg(CmdSet.MOVE_JOG);
        msg.setStop(true);
        view.refreshLogList(true,msg.getMessageStringContent());
        MoveMessageClient.getInstance().sendMsg(msg, null);
    }

    @Override
    public void stopScript() {
        CRMessageStopScript msg = (CRMessageStopScript) MessageFactory.getInstance().createMsg(CmdSet.STOP_SCRIPT);
        view.refreshLogList(true,msg.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(msg, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("stopScript msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }

    boolean isRunPath = false;

    @Override
    public void startPathTrack(final String path) {
        BaseMessage message;
        message = (BaseMessage) MessageFactory.getInstance().createMsg(CmdSet.ENABLE_ROBOT);
        view.refreshLogList(true,message.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(message, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("start path  ENABLE_ROBOT msgState:" + state);
                if (msg != null && state == MsgState.MSG_REPLY) {
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
                    MoveMessageClient.getInstance().sendMsg(messageStartPath, null);
                    CRMessageGetPathStartPose crMessageGetPathStartPose = (CRMessageGetPathStartPose) MessageFactory.getInstance().createMsg(CmdSet.GET_PATH_START_POSE);
                    crMessageGetPathStartPose.setTraceName(path);
                    view.refreshLogList(true,crMessageGetPathStartPose.getMessageStringContent());

                    APIMessageClient.getInstance().sendMsg(crMessageGetPathStartPose, new MessageCallback() {
                        @Override
                        public void onMsgCallback(MsgState state, OriginalData msg) {
                            System.out.println("get path start pose:" + state);
                            if (state == MsgState.MSG_REPLY) {
                                view.refreshLogList(false,new String(msg.getTotalBytes()));
                                String pathStartPoseStr = new String(msg.getTotalBytes(), Charset.forName("US-ASCII"));
                                CRMessageJointMovJ crMessageJointMovJ = (CRMessageJointMovJ) MessageFactory.getInstance().createMsg(CmdSet.JOINT_MOV_J);
                                crMessageJointMovJ.setMessageStringContent("JointMovJ(" + pathStartPoseStr.substring(1, pathStartPoseStr.length() - 1) + ")");
                                crMessageJointMovJ.setMessageContent(crMessageJointMovJ.getMessageStringContent().getBytes(Charset.forName("US-ASCII")));
                                view.refreshLogList(true,crMessageJointMovJ.getMessageStringContent());
                                MoveMessageClient.getInstance().sendMsg(crMessageJointMovJ, null);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isRunPath = true;
                                        CRMessageStartPath messageStartPath = (CRMessageStartPath) MessageFactory.getInstance().createMsg(CmdSet.START_PATH);
                                        messageStartPath.setTraceName(path);
                                        messageStartPath.setConst(0);
                                        messageStartPath.setCart(0);
                                        view.refreshLogList(true,messageStartPath.getMessageStringContent());
                                        MoveMessageClient.getInstance().sendMsg(messageStartPath, null);
                                    }
                                }, 3000);
                            }
                        }
                    });
                    /*    */
                }
            }
        });

    }

    @Override
    public void setIO(int index, int value) {
        CRMessageDOExecute messageDO = (CRMessageDOExecute) MessageFactory.getInstance().createMsg(CmdSet.DO_EXECUTE);
        messageDO.setIndex(index);
        messageDO.setStatus(value);
        view.refreshLogList(true,messageDO.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(messageDO, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                System.out.println("setIO msgState:" + state);
                if (msg!=null)
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
            }
        });
    }

    @Override
    public int getIO(int index) {
        int i = index / 8;
        byte DOarray = StateMessageClient.getInstance().getState().getDO()[i];
        int mod = index - 1 - i * 8;
        return DOarray >> mod & 0x01;
    }

    @Override
    public void setJogMove(boolean isCoordinate, int pos) {
        final String jogStr;
        switch (pos) {
            case 0:
                jogStr = !isCoordinate ? "j1+" : "x+";
                break;
            case 1:
                jogStr = !isCoordinate ? "j2+" : "y+";
                break;
            case 2:
                jogStr = !isCoordinate ? "j3+" : "z+";
                break;
            case 3:
                jogStr = !isCoordinate ? "j4+" : "rx+";
                break;
            case 4:
                jogStr = !isCoordinate ? "j5+" : "ry+";
                break;
            case 5:
                jogStr = !isCoordinate ? "j6+" : "rz+";
                break;
            case 6:
                jogStr = !isCoordinate ? "j1-" : "x-";
                break;
            case 7:
                jogStr = !isCoordinate ? "j2-" : "y-";
                break;
            case 8:
                jogStr = !isCoordinate ? "j3-" : "z-";
                break;
            case 9:
                jogStr = !isCoordinate ? "j4-" : "rx-";
                break;
            case 10:
                jogStr = !isCoordinate ? "j5-" : "ry-";
                break;
            case 11:
                jogStr = !isCoordinate ? "j6-" : "rz-";
                break;
            default:
                jogStr = "";
        }
        if (isRunPath) {
            BaseMessage message;
            message = (BaseMessage) MessageFactory.getInstance().createMsg(CmdSet.MANUAL);
            view.refreshLogList(true,message.getMessageStringContent());
            APIMessageClient.getInstance().sendMsg(message, new MessageCallback() {
                        @Override
                        public void onMsgCallback(MsgState state, OriginalData originalData) {
                            System.out.println("JOG MANUAL msgState:" + state);
                            if (state == MsgState.MSG_REPLY) {
                                CRMessageMoveJog msg = (CRMessageMoveJog) MessageFactory.getInstance().createMsg(CmdSet.MOVE_JOG);
                                msg.setAxisID(jogStr);
                                view.refreshLogList(true,msg.getMessageStringContent());
                                MoveMessageClient.getInstance().sendMsg(msg, null);
                            }
                        }
                    }
            );
            isRunPath = false;
        } else {
            CRMessageMoveJog msg = (CRMessageMoveJog) MessageFactory.getInstance().createMsg(CmdSet.MOVE_JOG);
            msg.setAxisID(jogStr);
            view.refreshLogList(true,msg.getMessageStringContent());
            MoveMessageClient.getInstance().sendMsg(msg, null);
        }

    }

    @Override
    public void onStateRefresh(final RobotState state) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.refreshRobotMode(state.getMode());
                view.refreshSpeedScaling(state.getSpeedScaling());
                view.refreshDI(state.getDI());
                view.refreshDO(state.getDO());
                view.refreshProgramState(state.getProgramState());
                view.refreshQActual(state.getqActual());
                view.refreshToolVectorActual(state.getToolVectorActual());
            }
        });

    }

    @Override
    public double[] getCurrentCoordinate() {
        return StateMessageClient.getInstance().getState().getToolVectorActual();
    }

    @Override
    public double[] getCurrentJoint() {
        return StateMessageClient.getInstance().getState().getqActual();
    }
}
