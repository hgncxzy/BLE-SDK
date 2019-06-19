package com.xzy.ble.baseble.callback
import com.xzy.ble.baseble.core.BluetoothGattChannel
import com.xzy.ble.baseble.exception.BleException
import com.xzy.ble.baseble.model.BluetoothLeDevice


/**
 *
 * 操作数据回调
 *
 */
interface IBleCallback {
    fun onSuccess(data: ByteArray, bluetoothGattChannel: BluetoothGattChannel, bluetoothLeDevice: BluetoothLeDevice)

    fun onFailure(exception: BleException)
}
