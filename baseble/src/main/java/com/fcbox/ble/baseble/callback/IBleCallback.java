package com.fcbox.ble.baseble.callback;


import com.fcbox.ble.baseble.core.BluetoothGattChannel;
import com.fcbox.ble.baseble.exception.BleException;
import com.fcbox.ble.baseble.model.BluetoothLeDevice;


/**
 *
 *  操作数据回调
 *
 */
public interface IBleCallback {
    void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice);

    void onFailure(BleException exception);
}
