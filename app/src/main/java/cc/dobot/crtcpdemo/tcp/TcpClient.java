package cc.dobot.crtcpdemo.tcp;

import android.os.Handler;
import android.text.TextUtils;

import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.ISocketActionListener;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import cc.dobot.crtcpdemo.TransformUtils;


public abstract class TcpClient {

    private static final String TAG = TcpClient.class.getSimpleName();

    private static final int RECONNECT_TIME = 3; //连接失败重连次数
    protected IReaderProtocol readerProtocol;
    private String ip;
    private int port;
    protected ConnectionInfo info;
    private IConnectionManager manager;
    private List<ClientCallback.ConnectCallback> connectCallbacks = new ArrayList<>();
    private List<ClientCallback.ClientMsgCallback> clientStateCallbacks = new ArrayList<>();
    private volatile boolean isConnected = false;
    private int connectFailedTimes = 0;
    private Handler handler;

    public enum CallbackType {
        CONNECT_SUCCESS, CONNECT_FAILED, DISCONNECT, READ_DATA
    }

    ISocketActionListener socketActionListener = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            super.onSocketConnectionSuccess(info, action);
            isConnected = true;
            connectFailedTimes = 0;
            if (connectCallbacks.size() > 0)
                notifyCallback(CallbackType.CONNECT_SUCCESS, new byte[]{0x00, 0x00}, null);
           /* OkSocket.open(info)
                    .getPulseManager()
                    .pulse();*/
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            super.onSocketConnectionFailed(info, action, e);
            connectFailedTimes++;
            if (connectFailedTimes > RECONNECT_TIME) {
                manager.disconnect();
            }
            isConnected = false;
            if (connectCallbacks.size() > 0)
                notifyCallback(CallbackType.CONNECT_FAILED, new byte[]{0x00, 0x00}, null);
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            super.onSocketDisconnection(info, action, e);
            isConnected = false;
            if (clientStateCallbacks.size() > 0)
                notifyCallback(CallbackType.DISCONNECT, new byte[]{0x00, 0x00}, null);
        }

        @Override
        public synchronized void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            super.onSocketReadResponse(info, action, data);
            handleReceiveMsg(data);
        }

        @Override
        public void onSocketIOThreadShutdown(String action, Exception e) {
            super.onSocketIOThreadShutdown(action, e);
        }

    };

  /*  private IReaderProtocol readerProtocol = new IReaderProtocol() {
        @Override
        public int getHeaderLength() {
            //返回不能为零或负数的报文头长度(字节数)
            return HEAD_SIZE;
        }

        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {
            //根据header中payload的长度获取,此处加上尾校验码长度，防止第二次发送数据时库内buff没清空导致第二次数据返回解析异常
            // System.out.println("get body length:"+header);
            //     System.out.println("bodylength:" + TransformUtils.byteToShort(headParams.getCmdLength()));
            System.out.println("head 0:"+(int)header[0]+" 1:"+(int)header[1]+" header length:"+ TransformUtils.byteToShort(header));
            return *//*header.length*//*TcpClient.this.BODY_LENGTH;
        }
    };*/


    public TcpClient() {
    }

    public TcpClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }


    public void setConnectInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean tcpConnect() {
        if (TextUtils.isEmpty(ip) || port <= 0)
            return false;
        else {    //Connection parameter Settings (IP, port number), which is also a unique identifier for a connection.
            info = new ConnectionInfo(ip, port);
            //Call OkSocket open() the channel for this connection, and the physical connections will be connected.
            manager = OkSocket.open(info);
            OkSocketOptions options = manager.getOption();
            OkSocketOptions.Builder okOptionsBuilder = new OkSocketOptions.Builder(options);
            if (readerProtocol!=null)
            okOptionsBuilder/*.setReadByteOrder(ByteOrder.BIG_ENDIAN)*/
                    .setReaderProtocol(readerProtocol);
                    /*.setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                        @Override
                        public void handleCallbackEvent(ActionDispatcher.ActionRunnable actionRunnable) {
                            handler.post(actionRunnable); //回调到主线程
                        }
                    });*/
            manager.option(okOptionsBuilder.setConnectTimeoutSecond(30).build());
            manager.registerReceiver(socketActionListener);
            manager.connect();
            return true;
        }
    }

    public void tcpDisconnect() {
        if (info != null) {
            manager = OkSocket.open(info);
            manager.disconnect();
            info = null;

        }
    }

    public void clearCallBack(){
        connectCallbacks.clear();
        clientStateCallbacks.clear();
    }

    public void sendData(ISendable sendable) {
        manager.send(sendable);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void addConnectCallback(ClientCallback.ConnectCallback callback) {
        if (callback != null)
            connectCallbacks.add(callback);
    }

    public void addClientMsgCallback(ClientCallback.ClientMsgCallback callback) {
        if (callback != null) {
            clientStateCallbacks.add(callback);
        }
    }

    public void setHandle(Handler handle) {
        this.handler = handle;
    }

    public void notifyCallback(CallbackType type, byte[] cmdId, OriginalData data) {
        switch (type) {
            case CONNECT_SUCCESS:
                for (ClientCallback.ConnectCallback callback : connectCallbacks)
                    callback.onClientConnectSuccess();
                break;
            case CONNECT_FAILED:
                for (ClientCallback.ConnectCallback callback : connectCallbacks)
                    callback.onClientConnectFailed();
                break;
            case DISCONNECT:
                for (ClientCallback.ClientMsgCallback callback : clientStateCallbacks)
                    callback.onClientDisconnection();
                break;
            case READ_DATA:
                for (ClientCallback.ClientMsgCallback callback : clientStateCallbacks)
                    callback.onClientMsgCallback(cmdId, data);
                break;
        }
    }

    public abstract void handleReceiveMsg(OriginalData data);

}
