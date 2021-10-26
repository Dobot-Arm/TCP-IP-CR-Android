package cc.dobot.crtcpdemo.tcp;

import com.xuhao.didi.core.pojo.OriginalData;

public interface ClientCallback {

    interface ConnectCallback extends ClientCallback{
        void onClientConnectSuccess();
        void onClientConnectFailed();
    }

    interface ClientMsgCallback extends ClientCallback{
        void onClientMsgCallback(byte[] cmdId, OriginalData data);
        void onClientDisconnection();
    }
}
