package com.example.xzy.util;

import android.annotation.SuppressLint;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description : 通用工具类
 * Created by xzy 2018/12/4 17:38 .
 */
@SuppressWarnings("unused")
public class CommonUtil {

    /**
     * 获得当前时间.
     *
     * @return String
     */
    public static String getCurrTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss  ");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }


    /**
     * java 字节转 16 进制字符串.
     *
     * @param b 字节数组
     * @return 16 进制字符串
     */
    public  static String parseBytesToHexString(byte[] b) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte aB : b) {
            int temp = aB & 0xFF;
            String hex = Integer.toHexString(temp);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            stringBuffer.append(hex.toUpperCase()).append(" ");
        }
        return stringBuffer.toString();
    }


    /**
     *
     * @param src byte[]
     * @return String
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * 十六进制字符串转 10 进制整数
     * @param hexString  十六进制字符串转
     * @return  10 进制整数
     */
    public static int hexStringToInt(String hexString){
       String s = Integer.valueOf(hexString,16).toString();
        return Integer.parseInt(s);
    }

    /**
     * 检查字符串中是否与待添加的文本有重复内容
     *
     * @param textView 文本控件
     * @param msg      待添加的消息
     */
    public static boolean checkRepeat(TextView textView, String msg) {
        if (textView.getText().toString().contains(msg)) {
            return msg.contains("找到目标设备");
        }
        return false;
    }


    public static void hideStatusBar(Window window) {
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
    }

    public static void showStatusBar(Window window) {
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
    }


    // System.arrayCopy() 方法
    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }
}
