package cc.dobot.crtcpdemo.client;

import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;

import java.nio.ByteOrder;
import java.util.Hashtable;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.dobot.crtcpdemo.RobotState;
import cc.dobot.crtcpdemo.TransformUtils;
import cc.dobot.crtcpdemo.message.constant.Robot;
import cc.dobot.crtcpdemo.tcp.TcpClient;

public class MoveMessageClient extends TcpClient {

    private static final String TAG = MoveMessageClient.class.getSimpleName();

    private volatile int seqNum = 1; //递增自然数

    private static MoveMessageClient instance;
    private volatile Hashtable<ISendable, MessageCallback> callbackTable = new Hashtable<>();
    private volatile Queue<ISendable> sendableQueue = new LinkedBlockingQueue<>();
    private volatile Queue<ISendable> writeMsgQueue = new LinkedBlockingQueue<>();
    private int writeQueueSizeLimit = 1;
    private int waitTime = 30; //回调超时时间
    private ExecutorService sendExecutor = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService writeExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService timeExecutor = Executors.newScheduledThreadPool(writeQueueSizeLimit + 1);
    private SendRunnable sendRunnable = new SendRunnable();
    private WriteRunnable writeRunnable = new WriteRunnable();
    private volatile boolean doSend = false;
    private RobotState state;

    private MoveMessageClient() {
        this.readerProtocol = new IReaderProtocol() {
            @Override
            public int getHeaderLength() {
                //返回不能为零或负数的报文头长度(字节数)
                return 1;
            }

            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                //根据header中payload的长度获取,此处加上尾校验码长度，防止第二次发送数据时库内buff没清空导致第二次数据返回解析异常
                // System.out.println("get body length:"+header);
                //     System.out.println("bodylength:" + TransformUtils.byteToShort(headParams.getCmdLength()));
                return 0;//*header.length*//*TcpClient.this.BODY_LENGTH;
            }

            @Override
            public boolean isKnowLength() {
                return true;
            }
        };
        this.state = new RobotState();

    }

    public RobotState getState() {
        return state;
    }


    public static MoveMessageClient getInstance() {
        if (instance == null) {
            synchronized (MoveMessageClient.class) {
                if (instance == null) {
                    instance = new MoveMessageClient();
                }
            }
        }
        return instance;
    }

    public void setStateRefreshListener() {

    }

    //处理socket信息
    @Override
    public void handleReceiveMsg(OriginalData data) {

/*        System.out.println("DI:" + TransformUtils.bytesToLong(
                data.getSubBodyData(6, 14), 0, true));
        System.out.println("DO:" + TransformUtils.bytesToLong(
                data.getSubBodyData(14, 22), 0, true));
        System.out.println("mode:" + TransformUtils.bytesToLong(
                data.getSubBodyData(22, 30), 0, true));
        System.out.println("control time:" + TransformUtils.bytesToLong(
                data.getSubBodyData(30, 38), 0, true));
        System.out.println("power time:" + TransformUtils.bytesToLong(
                data.getSubBodyData(38, 46), 0, true));
        System.out.println("test value:" + TransformUtils.bytesToLong(
                data.getSubBodyData(46, 54), 0, true));
        System.out.println("safety mode:" + TransformUtils.bytesToLong(
                data.getSubBodyData(54, 62), 0, true));
        System.out.println(" Speed scaling:" + TransformUtils.bytesToLong(
                data.getSubBodyData(62, 70), 0, true));
        System.out.println(" Linear momentum norm:" + TransformUtils.bytesToLong(
                data.getSubBodyData(70, 78), 0, true));

        System.out.println(" V main:" + TransformUtils.bytesToLong(
                data.getSubBodyData(78, 86), 0, true));
        System.out.println(" V robot:" + TransformUtils.bytesToLong(
                data.getSubBodyData(86, 94), 0, true));
        System.out.println(" I robot:" + TransformUtils.bytesToLong(
                data.getSubBodyData(94, 102), 0, true));

        System.out.println(" Program state:" + TransformUtils.bytesToLong(
                data.getSubBodyData(102, 110), 0, true));
        System.out.println(" Safety Status:" + TransformUtils.bytesToLong(
                data.getSubBodyData(110, 118), 0, true));

        System.out.println(" Tool Accelerometer values x:" + TransformUtils.bytesToLong(
                data.getSubBodyData(118, 126), 0, true));
        System.out.println(" Tool Accelerometer values y:" + TransformUtils.bytesToLong(
                data.getSubBodyData(126, 164), 0, true));
        System.out.println(" Tool Accelerometer values z:" + TransformUtils.bytesToLong(
                data.getSubBodyData(134, 142), 0, true));

        System.out.println(" Elbow position x:" + TransformUtils.bytesToLong(
                data.getSubBodyData(142, 150), 0, true));
        System.out.println(" Elbow position y:" + TransformUtils.bytesToLong(
                data.getSubBodyData(150, 158), 0, true));
        System.out.println(" Elbow position z:" + TransformUtils.bytesToLong(
                data.getSubBodyData(158, 166), 0, true));
        System.out.println(" Elbow velocity x:" + TransformUtils.bytesToLong(
                data.getSubBodyData(166, 174), 0, true));
        System.out.println(" Elbow velocity y:" + TransformUtils.bytesToLong(
                data.getSubBodyData(174, 182), 0, true));
        System.out.println(" Elbow velocity z:" + TransformUtils.bytesToLong(
                data.getSubBodyData(182, 190), 0, true));

        System.out.println(" q target j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(190, 198), 0, true));
        System.out.println(" q target j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(198, 206), 0, true));
        System.out.println(" q target j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(206, 214), 0, true));
        System.out.println(" q target j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(214, 222), 0, true));
        System.out.println(" q target j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(222, 230), 0, true));
        System.out.println(" q target j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(230, 238), 0, true));

        System.out.println(" qd target j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(238, 246), 0, true));
        System.out.println(" qd target j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(246, 254), 0, true));
        System.out.println(" qd target j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(254, 262), 0, true));
        System.out.println(" qd target j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(262, 270), 0, true));
        System.out.println(" qd target j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(270, 278), 0, true));
        System.out.println(" qd target j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(278, 286), 0, true));

        System.out.println(" qdd target j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(286, 294), 0, true));
        System.out.println(" qdd target j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(294, 302), 0, true));
        System.out.println(" qdd target j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(302, 310), 0, true));
        System.out.println(" qdd target j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(310, 318), 0, true));
        System.out.println(" qdd target j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(318, 326), 0, true));
        System.out.println(" qdd target j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(326, 334), 0, true));

        System.out.println(" I target j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(334, 342), 0, true));
        System.out.println(" I target j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(342, 350), 0, true));
        System.out.println(" I target j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(350, 358), 0, true));
        System.out.println(" I target j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(358, 366), 0, true));
        System.out.println(" I target j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(366, 374), 0, true));
        System.out.println(" I target j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(374, 382), 0, true));

        System.out.println(" M target j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(382, 390), 0, true));
        System.out.println(" M target j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(390, 398), 0, true));
        System.out.println(" M target j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(398, 406), 0, true));
        System.out.println(" M target j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(406, 414), 0, true));
        System.out.println(" M target j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(414, 422), 0, true));
        System.out.println(" M target j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(422, 430), 0, true));

        System.out.println(" q actual j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(430, 438), 0, true));
        System.out.println(" q actual j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(438, 446), 0, true));
        System.out.println(" q actual j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(446, 454), 0, true));
        System.out.println(" q actual j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(454, 462), 0, true));
        System.out.println(" q actual j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(462, 470), 0, true));
        System.out.println(" q actual j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(470, 478), 0, true));

        System.out.println(" qd actual j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(478, 486), 0, true));
        System.out.println(" qd actual j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(486, 494), 0, true));
        System.out.println(" qd actual j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(494, 502), 0, true));
        System.out.println(" qd actual j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(502, 510), 0, true));
        System.out.println(" qd actual j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(510, 518), 0, true));
        System.out.println(" qd actual j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(518, 526), 0, true));

        System.out.println(" I actual j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(526, 534), 0, true));
        System.out.println(" I actual j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(534, 542), 0, true));
        System.out.println(" I actual j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(542, 550), 0, true));
        System.out.println(" I actual j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(550, 558), 0, true));
        System.out.println(" I actual j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(558, 566), 0, true));
        System.out.println(" I actual j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(566, 574), 0, true));

        System.out.println(" I control j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(574, 582), 0, true));
        System.out.println(" I control j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(582, 590), 0, true));
        System.out.println(" I control j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(590, 598), 0, true));
        System.out.println(" I control j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(598, 606), 0, true));
        System.out.println(" I control j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(606, 614), 0, true));
        System.out.println(" I control j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(614, 622), 0, true));

        System.out.println(" Tool vector actual x:" + TransformUtils.bytesToLong(
                data.getSubBodyData(622, 630), 0, true));
        System.out.println(" Tool vector actual y:" + TransformUtils.bytesToLong(
                data.getSubBodyData(630, 638), 0, true));
        System.out.println(" Tool vector actual z:" + TransformUtils.bytesToLong(
                data.getSubBodyData(638, 646), 0, true));
        System.out.println(" Tool vector actual rx:" + TransformUtils.bytesToLong(
                data.getSubBodyData(646, 654), 0, true));
        System.out.println(" Tool vector actual ry:" + TransformUtils.bytesToLong(
                data.getSubBodyData(654, 662), 0, true));
        System.out.println(" Tool vector actual rz:" + TransformUtils.bytesToLong(
                data.getSubBodyData(662, 670), 0, true));

        System.out.println(" TCP speed actual x:" + TransformUtils.bytesToLong(
                data.getSubBodyData(670, 678), 0, true));
        System.out.println(" TCP speed actual y:" + TransformUtils.bytesToLong(
                data.getSubBodyData(678, 686), 0, true));
        System.out.println(" TCP speed actual z:" + TransformUtils.bytesToLong(
                data.getSubBodyData(686, 694), 0, true));
        System.out.println(" TCP speed actual rx:" + TransformUtils.bytesToLong(
                data.getSubBodyData(694, 702), 0, true));
        System.out.println(" TCP speed actual ry:" + TransformUtils.bytesToLong(
                data.getSubBodyData(702, 710), 0, true));
        System.out.println(" TCP speed actual rz:" + TransformUtils.bytesToLong(
                data.getSubBodyData(710, 718), 0, true));

        System.out.println(" TCP force x:" + TransformUtils.bytesToLong(
                data.getSubBodyData(718, 726), 0, true));
        System.out.println(" TCP force y:" + TransformUtils.bytesToLong(
                data.getSubBodyData(726, 734), 0, true));
        System.out.println(" TCP force z:" + TransformUtils.bytesToLong(
                data.getSubBodyData(734, 742), 0, true));
        System.out.println(" TCP force rx:" + TransformUtils.bytesToLong(
                data.getSubBodyData(742, 750), 0, true));
        System.out.println(" TCP force ry:" + TransformUtils.bytesToLong(
                data.getSubBodyData(750, 758), 0, true));
        System.out.println(" TCP force rz:" + TransformUtils.bytesToLong(
                data.getSubBodyData(758, 766), 0, true));

        System.out.println(" Tool vector target x:" + TransformUtils.bytesToLong(
                data.getSubBodyData(766, 774), 0, true));
        System.out.println(" Tool vector target y:" + TransformUtils.bytesToLong(
                data.getSubBodyData(774, 782), 0, true));
        System.out.println(" Tool vector target z:" + TransformUtils.bytesToLong(
                data.getSubBodyData(782, 790), 0, true));
        System.out.println(" Tool vector target rx:" + TransformUtils.bytesToLong(
                data.getSubBodyData(790, 798), 0, true));
        System.out.println(" Tool vector target ry:" + TransformUtils.bytesToLong(
                data.getSubBodyData(798, 806), 0, true));
        System.out.println(" Tool vector target rz:" + TransformUtils.bytesToLong(
                data.getSubBodyData(806, 814), 0, true));

        System.out.println(" TCP speed target x:" + TransformUtils.bytesToLong(
                data.getSubBodyData(814, 822), 0, true));
        System.out.println(" TCP speed target y:" + TransformUtils.bytesToLong(
                data.getSubBodyData(822, 830), 0, true));
        System.out.println(" TCP speed target z:" + TransformUtils.bytesToLong(
                data.getSubBodyData(830, 838), 0, true));
        System.out.println(" TCP speed target rx:" + TransformUtils.bytesToLong(
                data.getSubBodyData(838, 846), 0, true));
        System.out.println(" TCP speed target ry:" + TransformUtils.bytesToLong(
                data.getSubBodyData(846, 854), 0, true));
        System.out.println(" TCP speed target rz:" + TransformUtils.bytesToLong(
                data.getSubBodyData(854, 862), 0, true));

        System.out.println(" Motor temperatures j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(862, 870), 0, true));
        System.out.println(" Motor temperatures j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(870, 878), 0, true));
        System.out.println(" Motor temperatures j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(878, 886), 0, true));
        System.out.println(" Motor temperatures j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(886, 894), 0, true));
        System.out.println(" Motor temperatures j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(894, 902), 0, true));
        System.out.println(" Motor temperatures j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(902, 910), 0, true));


        System.out.println(" Joint Modes j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(910, 918), 0, true));
        System.out.println(" Joint Modes j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(918, 926), 0, true));
        System.out.println(" Joint Modes j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(926, 934), 0, true));
        System.out.println(" Joint Modes j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(934, 942), 0, true));
        System.out.println(" Joint Modes j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(942, 950), 0, true));
        System.out.println(" Joint Modes j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(950, 958), 0, true));

        System.out.println(" V actual j1:" + TransformUtils.bytesToLong(
                data.getSubBodyData(958, 966), 0, true));
        System.out.println(" V actual j2:" + TransformUtils.bytesToLong(
                data.getSubBodyData(966, 974), 0, true));
        System.out.println(" V actual j3:" + TransformUtils.bytesToLong(
                data.getSubBodyData(974, 982), 0, true));
        System.out.println(" V actual j4:" + TransformUtils.bytesToLong(
                data.getSubBodyData(982, 990), 0, true));
        System.out.println(" V actual j5:" + TransformUtils.bytesToLong(
                data.getSubBodyData(990, 998), 0, true));
        System.out.println(" V actual j6:" + TransformUtils.bytesToLong(
                data.getSubBodyData(998, 1006), 0, true));*/
    }

    /**
     * 初始化TCP
     *
     * @param ip
     * @param port
     */
    public void initTcp(String ip, int port) {
        setConnectInfo(ip, port);
    }

    /**
     * 实现TCP连接
     *
     * @return
     */
    public synchronized boolean connect() {
        return tcpConnect();
    }

    /**
     * 断开TCP连接
     */
    public void disConnect() {
        tcpDisconnect();
    }

    /**
     * 发送消息
     *
     * @param sendable        待发送的msg对象
     * @param messageCallback 发送msg回调
     */
    public synchronized void sendMsg(ISendable sendable, MessageCallback messageCallback) {
        //System.out.println("send msg:"+sendable.toString());
        if (!isConnected() || null == sendable) {
            refuseSendMessage(messageCallback);
            return;
        }
        sendableQueue.add(sendable);
        if (messageCallback != null)
            callbackTable.put(sendable, messageCallback);
        sendExecutor.execute(sendRunnable);
    }


    /**
     * TCPClient未连接，拒绝发送msg，OriginalData为null
     *
     * @param messageCallback
     */
    private void refuseSendMessage(MessageCallback messageCallback) {
        if (messageCallback != null)
            messageCallback.onMsgCallback(MessageCallback.MsgState.MSG_REFUSE, null);
    }


    private class SendRunnable implements Runnable {

        @Override
        public synchronized void run() {
            if (!doSend) {
                if (sendableQueue.size() > 0) {
                    doSend = true;
                    ISendable sendable = sendableQueue.element();
                    AlarmRunnable runnable = new AlarmRunnable();
                    // System.out.println("do send msg:"+TransformUtils.bytes2HexString(sendable.parse()));
                    runnable.setMsg(sendable);
                    timeExecutor.schedule(runnable, waitTime, TimeUnit.SECONDS);
                    writeExecutor.schedule(writeRunnable, 0, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    private class WriteRunnable implements Runnable {

        @Override
        public synchronized void run() {
            ISendable sendable = sendableQueue.element();
            if (isConnected()) {
                sendData(sendable);
                writeMsgQueue.add(sendable);
                sendableQueue.remove(sendable);
                doSend = false;
                sendExecutor.execute(sendRunnable);
            }
        }
    }

    private class AlarmRunnable implements Runnable {

        private ISendable sendable;

        public void setMsg(ISendable sendable) {
            this.sendable = sendable;
        }

        @Override
        public void run() {
            if (callbackTable.containsKey(sendable)) {
                MessageCallback messageCallback = callbackTable.get(sendable);
                if (null != messageCallback) {
                    messageCallback.onMsgCallback(MessageCallback.MsgState.MSG_TIMEOUT, null);
                    callbackTable.remove(sendable);
                }
                if (!doSend)
                    sendExecutor.execute(sendRunnable);
            }
        }
    }

    public interface StateRefreshListener {
        void onStateRefresh(RobotState state);
    }
}