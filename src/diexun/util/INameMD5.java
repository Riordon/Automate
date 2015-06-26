package diexun.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class INameMD5 {

    // 全局数组
    private final static String[] strDigits = {"0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static final ReentrantLock lock = new ReentrantLock();

    public INameMD5() {
    }

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuilder sBuffer = new StringBuilder();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    /**
     * 图片MD5加密名称
     *
     * @return
     */
    public synchronized static String GetImageMD5Code() {
        String resultString = null;
        String strObj = new Date().toString();
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        
        resultString = resultString + getSyncNumber() + "_" + getFixLenthString(4) +".jpg";
        return resultString;
    }

    /*
     * 返回长度为【strLength】的随机数，在前面补0
     */
    private static  String getFixLenthString(int strLength) {
        
        Random rm = new Random();
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        String fixLenthString = String.valueOf(pross);
        return fixLenthString.substring(1, strLength + 1);
    }
    
    private static int number = 0; //初始化为0 , 最大值为10万
    private static int getSyncNumber() {
        lock.lock();
        try {
            ++number;
            if (number == 100000) {
                number = 0;
            }
        } finally {
            lock.unlock();
        }
        return number;
    }

}
