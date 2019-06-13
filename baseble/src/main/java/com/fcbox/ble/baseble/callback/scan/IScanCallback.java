package com.fcbox.ble.baseble.callback.scan;

import com.fcbox.ble.baseble.model.BluetoothLeDevice;
import com.fcbox.ble.baseble.model.BluetoothLeDeviceStore;


/**
 * 扫描回调
 */
public interface IScanCallback {
    //发现设备
    void onDeviceFound(BluetoothLeDevice bluetoothLeDevice);

    //扫描完成
    void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore);

    //扫描超时
    void onScanTimeout();

}
