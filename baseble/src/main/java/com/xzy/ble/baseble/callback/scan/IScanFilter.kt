package com.xzy.ble.baseble.callback.scan

import com.xzy.ble.baseble.model.BluetoothLeDevice


/**
 * 扫描过滤接口，根据需要实现过滤规则
 */
interface IScanFilter {
    fun onFilter(bluetoothLeDevice: BluetoothLeDevice): BluetoothLeDevice?
}
