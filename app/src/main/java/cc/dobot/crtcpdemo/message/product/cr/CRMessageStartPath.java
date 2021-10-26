package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageStartPath extends BaseMessage {

    private String traceName="";
    private int _const;
    private int cart;

    @Override
    public void constructSendData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent=("StartPath("+traceName+","+_const+","+cart+")").getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent=("StartPath("+traceName+","+_const+","+cart+")").getBytes( Charset.forName("US-ASCII"));
        this.messageStrContent=("StartPath("+traceName+","+_const+","+cart+")");
    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent=data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        }else
            this.messageStrContent = new String(data.getBodyBytes(),Charset.forName("US-ASCII"));
    }

    public String getTraceName() {
        return traceName;
    }

    public void setTraceName(String traceName) {
        this.traceName = traceName;
        constructSendData();
    }

    public int getConst() {
        return _const;
    }

    public void setConst(int _const) {
        this._const = _const;
        constructSendData();
    }

    public int getCart() {
        return cart;
    }

    public void setCart(int cart) {
        this.cart = cart;
        constructSendData();
    }
}
