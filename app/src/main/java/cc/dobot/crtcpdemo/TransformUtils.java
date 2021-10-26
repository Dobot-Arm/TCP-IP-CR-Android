package cc.dobot.crtcpdemo;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

/**
 * Created by x on 2018/5/15.
 */

public class TransformUtils {
    public static byte[] intToBytes(int src) {
        return new byte[]{
                (byte) ((src >> 24) & 0xFF),
                (byte) ((src >> 16) & 0xFF),
                (byte) ((src >> 8) & 0xFF),
                (byte) (src & 0xFF)
        };
    }

    public static int bytesToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    /**
     * @param b
     * @param start 开始位置
     * @param len   长度
     * @return
     */
    public static int bytesToInt(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = b[i] & 0xff;
            n <<= (--len) * 8;
            sum += n;
        }
        return sum;
    }

    /**
     * @param b
     * @return
     */
    public static int bytesToInt(byte b) {
        return b & 0xff;
    }

    /**
     * 浮点转换为字节
     *
     * @param f
     * @return
     */
    public static byte[] floatToBytes(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }

    /**
     * 字节转换为浮点
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float bytesToFloat(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * bytes数组转字符串
     *
     * @param bytes
     * @return
     */

    public static String bytesToString(byte[] bytes) {
        String strValue = new String(bytes);
        return strValue;
    }

    /**
     * bytes数组转字符串,去掉多余的0
     *
     * @param buffer
     * @return
     */
    public static String byteToStr(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, "GB2312");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 字符串转bytes数组
     *
     * @param strValue
     * @return
     */
    public static byte[] stringToBytes(String strValue) {
        if (strValue == null || strValue.length() == 0)
            return new byte[0];
        else {
            byte[] bytes = new byte[0];
            try {
                bytes = strValue.getBytes("GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return bytes;
        }
    }

    public static byte[] invertBytes(byte[] bytes) {
        if (bytes == null)
            return bytes;
        byte[] tempBytes = new byte[bytes.length];
        for (int i = 0; i < tempBytes.length; i++) {
            tempBytes[i] = bytes[tempBytes.length - i - 1];
        }
        return tempBytes;
    }

    /**
     * 将字节数组转为long
     *
     * @param bytes
     * @param start        起始偏移量
     * @param littleEndian 输入数组是否小端模式(大端：高位字节排放在内存的低地址端，小端：低位字节排放在内存的低地址端)
     * @return
     */
    public static long bytesToLong(byte[] bytes, int start, boolean littleEndian) {
        long value = 0;
        for (int count = 0; count < 8; ++count) {
            int shift = (littleEndian ? count : (7 - count)) << 3;
            value |= ((long) 0xff << shift) & ((long) bytes[start + count] << shift);
        }
        return value;
    }

    /**
     * 将long转成 bytes
     *
     * @param data
     * @param littleEndian 大小端
     * @return
     */
    public static byte[] longToBytes(long data, boolean littleEndian) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        //byte 数组与 long 的相互转换
        buffer.putLong(0, data);
        if (littleEndian) {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        return buffer.array();

    }

    public static short byteToShort(byte[] res) {
        return (short) (((res[1] << 8) | res[0] & 0xff));
    }

    public static byte[] short2byte(short s) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = 16 - (2 - i) * 8;
            if (i == 0) {
                b[0] = (byte) ((s >> offset) & 0xff);
            } else {
                b[1] = (byte) ((s >> offset) & 0xff);
            }

        }
        return b;
    }

    /**
     * 字节转十六进制
     *
     * @param b 需要进行转换的byte字节
     * @return 转换后的Hex字符串
     */
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static String bytes2HexString(byte[] bytes) {
        String ret = "";
        for (byte item : bytes) {
            String hex = Integer.toHexString(item & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase(Locale.CHINA);
        }
        return ret;
    }

    /*
     * 16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))) {
            return null;
        } else if (hex.length() % 2 != 0) {
            return null;
        } else {
            hex = hex.toUpperCase();
            int len = hex.length() / 2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i = 0; i < len; i++) {
                int p = 2 * i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
            }
            return b;
        }

    }

    /*
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //浮点到字节转换

    public static byte[] doubleToBytes(double d) {

        byte[] b = new byte[8];
        long l = Double.doubleToLongBits(d);

        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(l).byteValue();
            l = l >> 8;
        }
        return b;

    }


    //字节到浮点转换

    public static double bytesToDouble(byte[] b) {
        long l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        l &= 0xffffffffl;
        l |= ((long) b[4] << 32);
        l &= 0xffffffffffl;
        l |= ((long) b[5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) b[6] << 48);
        l &= 0xffffffffffffffl;
        l |= ((long) b[7] << 56);
        return Double.longBitsToDouble(l);

    }

}
