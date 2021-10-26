package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageMovJ extends BaseMessage {
    double x,y,z,rx,ry,rz;

    @Override
    public void constructSendData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=("MovJ("+x+","+y+","+z+","+rx+","+ry+","+rz+")").getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=("MovJ("+x+","+y+","+z+","+rx+","+ry+","+rz+")").getBytes( Charset.forName("US-ASCII"));
        this.messageStrContent=("MovJ("+x+","+y+","+z+","+rx+","+ry+","+rz+")");
    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent=data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        }else
            this.messageStrContent = new String(data.getBodyBytes(),Charset.forName("US-ASCII"));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        constructSendData();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        constructSendData();
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
        constructSendData();
    }

    public double getRx() {
        return rx;
    }

    public void setRx(double rx) {
        this.rx = rx;
        constructSendData();
    }

    public double getRy() {
        return ry;
    }

    public void setRy(double ry) {
        this.ry = ry;
        constructSendData();
    }

    public double getRz() {
        return rz;
    }

    public void setRz(double rz) {
        this.rz = rz;
        constructSendData();
    }

    public void setPoint(double[] point)
    {
        x=point[0];
        y=point[1];
        z=point[2];
        rx=point[3];
        ry=point[4];
        rz=point[5];
        constructSendData();
    }

    public double[]getPoint(){
        return new double[]{x,y,z,rx,ry,rz};
    }
}
