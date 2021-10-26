package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageArch extends BaseMessage {
    private double x1,y1,z1,rx1,ry1,rz1,x2,y2,z2,rx2,ry2,rz2;
    @Override
    public void constructSendData() {
        this.messageStrContent=("Arch("+x1+","+y1+","+z1+","+rx1+","+ry1+","+rz1+","+x2+","+y2+","+z2+","+rx2+","+ry2+","+rz2+")");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=this.messageStrContent.getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=this.messageStrContent.getBytes( Charset.forName("US-ASCII"));

    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent=data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        }else
            this.messageStrContent = new String(data.getBodyBytes(),Charset.forName("US-ASCII"));
    }

    public void setPoint(double[] point1,double[]point2)
    {
        x1=point1[0];
        y1=point1[1];
        z1=point1[2];
        rx1=point1[3];
        ry1=point1[4];
        rz1=point1[5];
        x2=point2[0];
        y2=point2[1];
        z2=point2[2];
        rx2=point2[3];
        ry2=point2[4];
        rz2=point2[5];
        constructSendData();
    }

    public double[]getPoint1(){
        return new double[]{x1,y1,z1,rx1,ry1,rz1};
    }

    public double[]getPoint2(){
        return new double[]{x2,y2,z2,rx2,ry2,rz2};
    }

}
