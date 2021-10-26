package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessagePositiveSolution extends BaseMessage {
    double j1,j2,j3,j4,j5,j6;

    @Override
    public void constructSendData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=("PositiveSolution("+j1+","+j2+","+j3+","+j4+","+j5+","+j6+")").getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=("PositiveSolution("+j1+","+j2+","+j3+","+j4+","+j5+","+j6+")").getBytes( Charset.forName("US-ASCII"));
        this.messageStrContent=("PositiveSolution("+j1+","+j2+","+j3+","+j4+","+j5+","+j6+")");
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
        j1=point[0];
        j2=point[1];
        j3=point[2];
        j4=point[3];
        j5=point[4];
        j6=point[5];
        constructSendData();
    }

    public double[]getPoint(){
        return new double[]{j1,j2,j3,j4,j5,j6};
    }
}
