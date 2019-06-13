package com.fcbox.ble.baseble.callback;


import com.fcbox.ble.baseble.exception.BleException;


/**
 * 获取信号值回调
 */
public interface IRssiCallback {
    void onSuccess(int rssi);

    void onFailure(BleException exception);
}
