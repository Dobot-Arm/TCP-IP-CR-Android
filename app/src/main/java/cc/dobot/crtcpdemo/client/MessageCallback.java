package cc.dobot.crtcpdemo.client;

import com.xuhao.didi.core.pojo.OriginalData;

public interface MessageCallback {
    enum MsgState{
        MSG_REPLY,MSG_TIMEOUT,MSG_REFUSE
    }
    void onMsgCallback(MsgState state, OriginalData msg);
}
