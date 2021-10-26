package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessagePayLoad extends BaseMessage {

    private double weight=0;
    private double inertia=0;
    @Override
    public void constructSendData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=("PayLoad("+weight+","+inertia+")").getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=("PayLoad("+weight+","+inertia+")").getBytes( Charset.forName("US-ASCII"));
        this.messageStrContent=("PayLoad("+weight+","+inertia+")");
    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent=data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        }else
            this.messageStrContent = new String(data.getBodyBytes(),Charset.forName("US-ASCII"));
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        constructSendData();
    }

    public double getInertia() {
        return inertia;
    }

    public void setInertia(double inertia) {
        this.inertia = inertia;
        constructSendData();
    }
}
