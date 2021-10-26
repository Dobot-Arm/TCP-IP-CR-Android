package cc.dobot.crtcpdemo.message.product.cr;

import android.os.Build;

import com.xuhao.didi.core.pojo.OriginalData;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cc.dobot.crtcpdemo.message.base.BaseMessage;

public class CRMessageSetHoldRegs extends BaseMessage {

    private int id=1;
    private int addr=3095;
    private int count=1;
    private int table[]={0,0};
    private String type=null;
    @Override
    public void constructSendData() {
        this.messageStrContent=("SetHoldRegs("+id+","+addr+","+count+","+"{"+table[0]+","+table[1]+"}");
        if (type!=null)
        {
            this.messageStrContent=this.messageStrContent+","+type+")";
        }else
        {
            this.messageStrContent=this.messageStrContent+")";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageContent= this.messageStrContent.getBytes(StandardCharsets.US_ASCII);
        }else
            this.messageContent= this.messageStrContent.getBytes( Charset.forName("US-ASCII"));

    }

    @Override
    public void transformReceiveData2Object(OriginalData data) {

        this.messageContent=data.getBodyBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.messageStrContent = new String(data.getBodyBytes(), StandardCharsets.US_ASCII);
        }else
            this.messageStrContent = new String(data.getBodyBytes(),Charset.forName("US-ASCII"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        constructSendData();
    }

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
        constructSendData();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        constructSendData();
    }

    public int[] getTable() {
        return table;
    }

    public void setTable(int index,int value) {
        this.table[index] = value;
        constructSendData();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        constructSendData();
    }
}
