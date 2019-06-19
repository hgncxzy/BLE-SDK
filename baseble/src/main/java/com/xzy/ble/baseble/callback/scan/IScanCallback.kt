package com.xzy.ble.baseble.callback.scan

import com.xzy.ble.baseble.model.BluetoothLeDevice
import com.xzy.ble.baseble.model.BluetoothLeDeviceStore


/**
 * 扫描回调
 */
interface IScanCallback {
    //发现设备
    fun onDeviceFound(bluetoothLeDevice: BluetoothLeDevice)

    //扫描完成
    fun onScanFinish(bluetoothLeDeviceStore: BluetoothLeDeviceStore)

    //扫描超时
    fun onscanBleTimeout()

}
