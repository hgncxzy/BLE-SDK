package com.fcbox.ble.baseble.common;


/**
 * BLE常量
 */
@SuppressWarnings("unused")
public class BleConstant {
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static final int TIME_FOREVER = -1;

     static final int DEFAULT_SCAN_TIME = 20000;
     static final int DEFAULT_CONN_TIME = 10000;
     static final int DEFAULT_OPERATE_TIME = 5000;

     static final int DEFAULT_RETRY_INTERVAL = 1000;
     static final int DEFAULT_RETRY_COUNT = 3;
     static final int DEFAULT_MAX_CONNECT_COUNT = 5;

    public static final int MSG_CONNECT_TIMEOUT = 0x01;
    public static final int MSG_WRITE_DATA_TIMEOUT = 0x02;
    public static final int MSG_READ_DATA_TIMEOUT = 0x03;
    public static final int MSG_RECEIVE_DATA_TIMEOUT = 0x04;
    public static final int MSG_CONNECT_RETRY = 0x05;
    public static final int MSG_WRITE_DATA_RETRY = 0x06;
    public static final int MSG_READ_DATA_RETRY = 0x07;
    public static final int MSG_RECEIVE_DATA_RETRY = 0x08;

    static final int DEFAULT_SCAN_REPEAT_INTERVAL = -1;
}
