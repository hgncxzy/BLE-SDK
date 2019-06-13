package com.fcbox.ble.baseble.common;

/**
 * BLE异常Code
 */
public enum BleExceptionCode {
    TIMEOUT,    //超时
    CONNECT_ERR,    //连接异常
    GATT_ERR,   //GATT异常
    INITIATED_ERR,  //初始化异常
    OTHER_ERR   //其他异常
}
