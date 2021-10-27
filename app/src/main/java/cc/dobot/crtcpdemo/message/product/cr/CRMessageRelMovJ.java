package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageRelMovJ extends BaseMessage {
    double offset1,offset2,offset3,offset4,offset5,offset6;

    @Override
    public void constructSendData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=("RelMovJ("+offset1+","+offset2+","+offset3+","+offset4+","+offset5+","+offset6+")").getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=("RelMovJ("+offset1+","+offset2+","+offset3+","+offset4+","+offset5+","+offset6+")").getBytes( Charset.forName("US-ASCII"));
        this.messageStrContent=("RelMovJ("+offset1+","+offset2+","+offset3+","+offset4+","+offset5+","+offset6+")");
    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent=data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        }else
            this.messageStrContent = new String(data.getBodyBytes(),Charset.forName("US-ASCII"));
    }



    public void setOffset(double[] point)
    {
        offset1=point[0];
        offset2=point[1];
        offset3=point[2];
        offset4=point[3];
        offset5=point[4];
        offset6=point[5];
        constructSendData();
    }

    public double[]getOffset(){
        return new double[]{offset1,offset2,offset3,offset4,offset5,offset6};
    }
}
