package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageSync extends BaseMessage {
    private static final String MESSAGE_CONTENT="Sync()";
    @Override
    public void constructSendData() {
        this.REPLY_SIZE=4;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=MESSAGE_CONTENT.getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=MESSAGE_CONTENT.getBytes( Charset.forName("US-ASCII"));
        this.messageStrContent=MESSAGE_CONTENT;
    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent=data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        }else
            this.messageStrContent = new String(data.getBodyBytes(),Charset.forName("US-ASCII"));

    }
}
