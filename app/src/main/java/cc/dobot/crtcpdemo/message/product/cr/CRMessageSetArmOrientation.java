package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageSetArmOrientation extends BaseMessage {

    private int LorR=1;
    private int UorD=1;
    private int ForN=1;
    private int Config6=1;
    @Override
    public void constructSendData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=("SetArmOrientation("+LorR+","+UorD+","+ForN+","+Config6+")").getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=("SetArmOrientation("+LorR+","+UorD+","+ForN+","+Config6+")").getBytes( Charset.forName("US-ASCII"));
        this.messageStrContent=("SetArmOrientation("+LorR+","+UorD+","+ForN+","+Config6+")");
    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent=data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        }else
            this.messageStrContent = new String(data.getBodyBytes(),Charset.forName("US-ASCII"));
    }

    public int getLorR() {
        return LorR;
    }

    public void setLorR(int lorR) {
        LorR = lorR;
        constructSendData();
    }

    public int getUorD() {
        return UorD;
    }

    public void setUorD(int uorD) {
        UorD = uorD;
        constructSendData();
    }

    public int getForN() {
        return ForN;
    }

    public void setForN(int forN) {
        ForN = forN;
        constructSendData();
    }

    public int getConfig6() {
        return Config6;
    }

    public void setConfig6(int config6) {
        Config6 = config6;
        constructSendData();
    }

}
