package com.xuhao.didi.core.pojo;

import java.io.Serializable;

/**
 * 原始数据结构体
 * Created by xuhao on 2017/5/16.
 */
public final class OriginalData implements Serializable {
    /**
     * 原始数据包头字节数组
     */
    private byte[] mHeadBytes;
    /**
     * 原始数据包体字节数组
     */
    private byte[] mBodyBytes;
    private byte[] mTotalBytes;

    public byte[] getHeadBytes() {
        return mHeadBytes;
    }

    public void setHeadBytes(byte[] headBytes) {
        mHeadBytes = headBytes;
    }

    public byte[] getBodyBytes() {
        return mBodyBytes;
    }

    public void setBodyBytes(byte[] bodyBytes) {
        mBodyBytes = bodyBytes;
    }

    public byte[]getTotalBytes(){
        mTotalBytes =new byte[mBodyBytes.length+mHeadBytes.length];
        mTotalBytes[0]=mHeadBytes[0];
        System.arraycopy(mBodyBytes,0,mTotalBytes,1,mBodyBytes.length);
        return mTotalBytes;
    }

    public byte[]getSubTotalData(int start,int end){
        if (mTotalBytes==null)
            mTotalBytes=getTotalBytes();
        byte[] subData =new byte[end-start];
        System.arraycopy(mTotalBytes,start,subData,0,subData.length);
        return subData;
    }
    public byte[]getSubBodyData(int start,int end){
        byte[] subData =new byte[end-start];
        System.arraycopy(mBodyBytes,start,subData,0,subData.length);
        return subData;
    }
}
