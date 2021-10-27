package cc.dobot.crtcpdemo.client;

import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.dobot.crtcpdemo.TransformUtils;
import cc.dobot.crtcpdemo.message.base.BaseMessage;
import cc.dobot.crtcpdemo.tcp.TcpClient;


public class APIMessageClient extends TcpClient {

    private static final String TAG = APIMessageClient.class.getSimpleName();

    private volatile int seqNum = 1; //递增自然数

    private static APIMessageClient instance;
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
    private int bodyLength = 1;
    private int headLength = 1;

    private APIMessageClient() {
        this.readerProtocol = new IReaderProtocol() {
            @Override
            public int getHeaderLength() {
                //返回不能为零或负数的报文头长度(字节数)
                return headLength;
            }

            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                //根据header中payload的长度获取,此处加上尾校验码长度，防止第二次发送数据时库内buff没清空导致第二次数据返回解析异常
                // System.out.println("get body length:"+header);
                //     System.out.println("bodylength:" + TransformUtils.byteToShort(headParams.getCmdLength()));
                return bodyLength - headLength;//*header.length*//*TcpClient.this.BODY_LENGTH;
            }

            @Override
            public boolean isKnowLength() {
                return false;
            }
        };
    }

    public static APIMessageClient getInstance() {
        if (instance == null) {
            synchronized (APIMessageClient.class) {
                if (instance == null) {
                    instance = new APIMessageClient();
                }
            }
        }
        return instance;
    }

    //处理socket信息
    @Override
    public void handleReceiveMsg(OriginalData data) {

        String replyMSG = new String(data.getTotalBytes(), Charset.forName("US-ASCII"));
        //遍历发送msg
        for (ISendable sendable : writeMsgQueue) {
            BaseMessage baseMessage = (BaseMessage) sendable;
            if (baseMessage.getMessageStringContent().equals(replyMSG)) {
                MessageCallback messageCallback = callbackTable.get(baseMessage);
                writeMsgQueue.remove(baseMessage);
                if (null != messageCallback) {
                    messageCallback.onMsgCallback(MessageCallback.MsgState.MSG_REPLY, data);
                }
                callbackTable.remove(baseMessage);
                //System.out.println("is reply msg");
                return;
            } else if (replyMSG.startsWith("could not understand:") && replyMSG.contains(baseMessage.getMessageStringContent())) {
                MessageCallback messageCallback = callbackTable.get(baseMessage);
                writeMsgQueue.remove(baseMessage);
                if (null != messageCallback) {
                    messageCallback.onMsgCallback(MessageCallback.MsgState.MSG_REFUSE, data);
                }
                callbackTable.remove(baseMessage);
                return;

            }else if (replyMSG.length()==bodyLength)
            {
                MessageCallback messageCallback = callbackTable.get(baseMessage);
                writeMsgQueue.remove(baseMessage);
                if (null != messageCallback) {
                    messageCallback.onMsgCallback(MessageCallback.MsgState.MSG_REPLY, data);
                }
                callbackTable.remove(baseMessage);
                //System.out.println("is reply msg");
                return;
            }
            else {
                MessageCallback messageCallback = callbackTable.get(baseMessage);
                writeMsgQueue.remove(baseMessage);
                if (null != messageCallback) {
                    messageCallback.onMsgCallback(MessageCallback.MsgState.MSG_REPLY, data);
                }
                callbackTable.remove(baseMessage);
                //System.out.println("is reply msg");
                return;
            }

        }
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
        callbackTable.put(sendable, messageCallback);
        sendExecutor.execute(sendRunnable);
    }


    /**
     * TCPClient未连接，拒绝发送msg，OriginalData为null
     *
     * @param messageCallback
     */
    private void refuseSendMessage(MessageCallback messageCallback) {
        messageCallback.onMsgCallback(MessageCallback.MsgState.MSG_REFUSE, null);
    }


    private class SendRunnable implements Runnable {

        @Override
        public synchronized void run() {
            if (!doSend) {
                if (sendableQueue.size() > 0) {
                    doSend = true;
                    ISendable sendable = sendableQueue.element();
                    if (sendable.getReplySize() >= 0)
                        bodyLength = sendable.getReplySize();
                    else
                        bodyLength = sendable.parse().length;
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
}