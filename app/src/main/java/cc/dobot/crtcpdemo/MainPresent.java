package cc.dobot.crtcpdemo;

import android.os.FileUtils;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xuhao.didi.core.pojo.OriginalData;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.dobot.crtcpdemo.client.APIMessageClient;
import cc.dobot.crtcpdemo.client.MessageCallback;
import cc.dobot.crtcpdemo.client.MoveMessageClient;
import cc.dobot.crtcpdemo.client.StateMessageClient;
import cc.dobot.crtcpdemo.data.AlarmArray;
import cc.dobot.crtcpdemo.data.AlarmData;
import cc.dobot.crtcpdemo.data.AlertJsonData;
import cc.dobot.crtcpdemo.message.base.BaseMessage;
import cc.dobot.crtcpdemo.message.constant.CmdSet;
import cc.dobot.crtcpdemo.message.constant.Robot;
import cc.dobot.crtcpdemo.message.factory.MessageFactory;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageClearError;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageDOExecute;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageEmergencyStop;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageGetErrorID;
import cc.dobot.crtcpdemo.message.product.cr.CRMessageGetPathStartPose;
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

import static cc.dobot.crtcpdemo.TransformUtils.transAlertJson2AlarmData;
import static cc.dobot.crtcpdemo.TransformUtils.transServoAlertJson2AlarmData;

public class MainPresent implements MainContract.Present, StateMessageClient.StateRefreshListener {
    Handler handler = new Handler();
    MainContract.View view;
    boolean isConnected;
    boolean isPowerOn;
    boolean isEnable;
    boolean isInit = false;

    public static final String ALARM_CONTROLLER_PATH ="alarm_controller.json";
    public static final String ALARM_SERVO_PATH = "alarm_servo.json";

    public ExecutorService transAlertLogExecutor = Executors.newSingleThreadExecutor();
    //关于报错数据
    List<AlarmData> dataList = new LinkedList<>();

    List<String> controlErrorIdList = new LinkedList<>();
    List<String> serverErrorIdList = new LinkedList<>();

    //临时变量
    List<String> tempControl = new LinkedList<>();
    List<String> tempServer = new LinkedList<>();

    private List<AlertJsonData> controllerJson = new ArrayList<>();

    public List<AlertJsonData> getControllerJson() {
        return controllerJson;
    }

    public void setControllerJson(List<AlertJsonData> controllerJson) {
        this.controllerJson = controllerJson;
    }

    private List<AlertJsonData> serverJson = new ArrayList<>();

    public List<AlertJsonData> getServerJson() {
        return serverJson;
    }

    public void setServerJson(List<AlertJsonData> serverJson) {
        this.serverJson = serverJson;
    }


    public MainPresent(MainContract.View view) {
        this.view = view;
        getAlarmJson();

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
                                view.refreshLogList(false,new String(originalData.getTotalBytes()));
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

    boolean isSendErrorMsg=false;
    @Override
    public void onStateRefresh(final RobotState state) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.refreshRobotMode(state.getMode());
                if (state.getMode()== Robot.Mode.ROBOT_MODE_ERROR&&dataList.size()==0&&!isSendErrorMsg)
                    doGetErrorID();
                else if (state.getMode()!= Robot.Mode.ROBOT_MODE_ERROR&&dataList.size()>0&&!isSendErrorMsg)
                    doGetErrorID();
                view.refreshSpeedScaling(state.getSpeedScaling());
                view.refreshDI(state.getDI());
                view.refreshDO(state.getDO());
                view.refreshProgramState(state.getProgramState());
                view.refreshQActual(state.getqActual());
                view.refreshToolVectorActual(state.getToolVectorActual());
            }
        });

    }

    private void doGetErrorID() {
        isSendErrorMsg=true;
        CRMessageGetErrorID messageGetErrorID=new CRMessageGetErrorID();
        view.refreshLogList(true,messageGetErrorID.getMessageStringContent());
        APIMessageClient.getInstance().sendMsg(messageGetErrorID, new MessageCallback() {
            @Override
            public void onMsgCallback(MsgState state, OriginalData msg) {
                view.refreshLogList(false,"Get ErrorID:"+state.toString());
                if (state== MsgState.MSG_REPLY){
                    view.refreshLogList(false,new String(msg.getTotalBytes()));
                    String errorID=new String(msg.getTotalBytes());
                    int startErrorIDIndex=errorID.indexOf(",");
                    int endErrorIDIndex=errorID.indexOf(",GetErrorID();");
                    String subString=errorID.substring(startErrorIDIndex+1,endErrorIDIndex);
                     startErrorIDIndex=errorID.indexOf("{");
                     endErrorIDIndex=errorID.indexOf("}");
                    subString=errorID.substring(startErrorIDIndex+1,endErrorIDIndex);
                    subString="{\"alarms\":"+subString+"}";
                    Gson gson=new Gson();
                    AlarmArray alarmArray=gson.fromJson(subString,AlarmArray.class);
                    if (diffAlarmsData(alarmArray.getAlarms()))
                       /* serverErrorIdList.add("1:12832");
                        serverErrorIdList.add("1:29572");
                        serverErrorIdList.add("1:30080");
                        serverErrorIdList.add("1:34322");*/
                        setLogData(controlErrorIdList, serverErrorIdList);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.refreshAlarmList(dataList);
                        }
                    });
                    isSendErrorMsg=false;
                }
                if (state==MsgState.MSG_REFUSE)
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isSendErrorMsg=false;
                        }
                    },5000);

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



    private void setLogData(final List<String> controlErrorIdList, final List<String> serverErrorIdList) {
        transAlertLogExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //警报被清除
                if (controlErrorIdList.size() == 0 && serverErrorIdList.size() == 0 && dataList.size() > 0) {
                    dataList.clear();
                } else if ((controlErrorIdList.size() > 0 || serverErrorIdList.size() > 0) && dataList.size() == 0) {
                    //有警报
                    transAlertJson2AlarmData(
                            getControllerJson(),
                            dataList,
                            controlErrorIdList,
                            true);
                    transServoAlertJson2AlarmData(getServerJson(), dataList, serverErrorIdList);
                } else //需要比较的情况
                {
                    List<AlarmData> tempDataList = new LinkedList<>();
                    transAlertJson2AlarmData(
                            getControllerJson(),
                            tempDataList,
                            controlErrorIdList,
                            true);
                    transServoAlertJson2AlarmData(getServerJson(), tempDataList, serverErrorIdList);
                    dataList = tempDataList;
                }
                //dataList = transAlertJson2LogData(AppData.getInstance().getServerJson(), dataList, tempServer, false);
                //AppData.getInstance().setLogDataList(dataList);
            }
        });

    }


    //对日志进行比较
    private boolean diffAlarmsData(String[][] alarms) {
        //当警报被清除本地还有的情况下
        if (alarms == null && (controlErrorIdList.size() > 0 || serverErrorIdList.size() > 0)) {
            controlErrorIdList.clear();
            serverErrorIdList.clear();
            return true;
        } else if (alarms != null &&
                controlErrorIdList.size() == 0 && serverErrorIdList.size() == 0) { //当有报警 本地没有的情况下
            int i = 0;
            for (String[] t : alarms) {
                if (t != null) {
                    if (i == 0) {
                        Collections.addAll(controlErrorIdList, t);
                    } else
                        for (String s : t) {
                            //添加关节辨识
                            //  System.out.println("alarm:" + i + ":" + s);
                            s = "" + i + ":" + s;
                            serverErrorIdList.add(s);
                        }
                }
                i++;
            }
            return true;
        } else //当有报警本地也有 极少数情况下
            if (alarms != null && (controlErrorIdList.size() > 0 || serverErrorIdList.size() > 0)) {
                //先比较 后赋值
                int isDiff = 0;
                tempControl.clear();
                tempServer.clear();
                int i = 0;
                for (String[] t : alarms) {
                    if (t != null) {
                        if (i == 0) {
                            for (String s : t) {
                                tempControl.add(s);
                                if (!controlErrorIdList.contains(s)) {
                                    isDiff += 1;
                                }
                            }
                        } else
                            for (String s : t) {
                                //添加关节辨识
                                //  System.out.println("alarm:" + i + ":" + s);
                                s = "" + i + ":" + s;
                                tempServer.add(s);
                                if (!serverErrorIdList.contains(s)) {
                                    isDiff += 2;
                                }
                            }
                    }
                    i++;
                }
                controlErrorIdList.clear();
                serverErrorIdList.clear();
                switch (isDiff) {
                    case 0:
                        return false;
                    case 1:
                        controlErrorIdList.addAll(tempControl);
                        return true;
                    case 2:
                        serverErrorIdList.addAll(tempServer);
                        return true;
                    case 3:
                        controlErrorIdList.addAll(tempControl);
                        serverErrorIdList.addAll(tempServer);
                        return true;
                }
            }

        return false;
    }

    public void getAlarmJson() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new GsonBuilder()
                            .create();
                    Type listType = new TypeToken<List<AlertJsonData>>() {
                    }.getType();

                    String jsonAlarmController = getAssetsFileString(ALARM_CONTROLLER_PATH);
                    List<AlertJsonData> alarmControllerList = gson.fromJson(jsonAlarmController, listType);
                    if (alarmControllerList != null)
                       setControllerJson(alarmControllerList);

                    String jsonAlarmServo = getAssetsFileString(ALARM_SERVO_PATH);
                    List<AlertJsonData> alarmServoList = gson.fromJson(jsonAlarmServo, listType);
                    if (alarmServoList != null)
                        setServerJson(alarmServoList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    public static String getAssetsFileString(String path) {
        StringBuffer buffer = new StringBuffer();
        try {
            InputStream is = AppDemo.getInstance().getApplicationContext().getAssets().open(path);
            if (is != null) {
                InputStreamReader inputreader = new InputStreamReader(is);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                int i = 1;
                while ((line = buffreader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return buffer.toString();
    }
}
