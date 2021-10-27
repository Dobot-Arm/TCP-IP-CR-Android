package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageRelMovL extends BaseMessage {
    double offsetX,offsetY,offsetZ;

    @Override
    public void constructSendData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=("RelMovL("+offsetX+","+offsetY+","+offsetZ+")").getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=("RelMovL("+offsetX+","+offsetY+","+offsetZ+")").getBytes( Charset.forName("US-ASCII"));
        this.messageStrContent=("RelMovL("+offsetX+","+offsetY+","+offsetZ+")");
    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent=data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        }else
            this.messageStrContent = new String(data.getBodyBytes(),Charset.forName("US-ASCII"));
    }



    public void setPoint(double[] point)
    {
        offsetX=point[0];
        offsetY=point[1];
        offsetZ=point[2];
        constructSendData();
    }

    public double[]getPoint(){
        return new double[]{offsetX,offsetY,offsetZ};
    }
}
