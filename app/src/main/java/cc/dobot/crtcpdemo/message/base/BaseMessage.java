package cc.dobot.crtcpdemo.message.base;

import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;

import java.util.Arrays;

public abstract class BaseMessage implements ISendable {
    protected byte[] messageContent;
    protected String messageStrContent;
    protected int REPLY_SIZE=-1;
    public BaseMessage() {
        constructSendData();
    }

    public void setMessageContent(byte[] messageContent) {
        this.messageContent = messageContent;
    }

    public void setMessageStringContent(String messageStrContent) {
        this.messageStrContent = messageStrContent;
    }

    public byte[] getMessageBytesContent(){
        return messageContent;
    }
    public String getMessageStringContent(){
        return messageStrContent;
    }


    @Override
    public byte[] parse() {
//        Log.i("test", "parse: "+ BytesUtils.toHexStringForLog(obj2RawBytes()));
        return messageContent;
    }

    public int getReplySize() {
//        Log.i("test", "parse: "+ BytesUtils.toHexStringForLog(obj2RawBytes()));
        return REPLY_SIZE;
    }
    abstract public void constructSendData();

    abstract public void transformReceiveData2Object(OriginalData data);

    @Override
    public String toString() {
        return messageStrContent==null?"":messageStrContent;
    }
}
