package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageMoveJog extends BaseMessage {

    private String axisID = "";
    private boolean isStop = false;

    @Override
    public void constructSendData() {
        if (isStop) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                this.messageContent="MoveJog()".getBytes(StandardCharsets.US_ASCII);
            }else
                this.messageContent="MoveJog()".getBytes( Charset.forName("US-ASCII"));
            this.messageStrContent="MoveJog()";
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                this.messageContent = ("MoveJog(" + axisID + ")").getBytes(StandardCharsets.US_ASCII);
            } else
                this.messageContent = ("MoveJog(" + axisID + ")").getBytes(Charset.forName("US-ASCII"));
            this.messageStrContent = ("MoveJog(" + axisID + ")");
        }
    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent = data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        } else
            this.messageStrContent = new String(data.getBodyBytes(), Charset.forName("US-ASCII"));
    }

    public String getAxisID() {
        return axisID;
    }

    public void setAxisID(String axisID) {
        this.axisID = axisID;
        constructSendData();
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
        constructSendData();
    }
}
