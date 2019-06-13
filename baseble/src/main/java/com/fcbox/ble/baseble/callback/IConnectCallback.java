package com.fcbox.ble.baseble.callback;


import com.fcbox.ble.baseble.core.DeviceMirror;
import com.fcbox.ble.baseble.exception.BleException;


/**
 * 连接设备回调
 */
public interface IConnectCallback {
    //连接成功
    void onConnectSuccess(DeviceMirror deviceMirror);

    //连接失败
    void onConnectFailure(BleException exception);

    //连接断开
    void onDisconnect(boolean isActive);
}
