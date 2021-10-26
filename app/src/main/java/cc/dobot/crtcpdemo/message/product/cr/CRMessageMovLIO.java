package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageMovLIO extends BaseMessage {
    double x,y,z,rx,ry,rz;
    int mode;
    int distance;
    int index;
    int status;

    @Override
    public void constructSendData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=("MovLIO("+x+","+y+","+z+","+rx+","+ry+","+rz+","+mode+","+distance+","+index+","+status+")").getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=("MovLIO("+x+","+y+","+z+","+rx+","+ry+","+rz+","+mode+","+distance+","+index+","+status+")").getBytes( Charset.forName("US-ASCII"));
        this.messageStrContent=("MovLIO("+x+","+y+","+z+","+rx+","+ry+","+rz+","+mode+","+distance+","+index+","+status+")");
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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        constructSendData();
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
        constructSendData();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        constructSendData();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        constructSendData();
    }
}