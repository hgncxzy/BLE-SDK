package com.xzy.ble.baseble.exception

import android.bluetooth.BluetoothGatt

import com.xzy.ble.baseble.common.BleExceptionCode


/**
 * 连接异常
 */
@Suppress("unused")
class ConnectException(private var bluetoothGatt: BluetoothGatt?, private var gattStatus: Int) : BleException(BleExceptionCode.CONNECT_ERR, "Connect Exception Occurred! ") {

    fun getGattStatus(): Int {
        return gattStatus
    }

    fun setGattStatus(gattStatus: Int): ConnectException {
        this.gattStatus = gattStatus
        return this
    }

    fun getBluetoothGatt(): BluetoothGatt? {
        return bluetoothGatt
    }

    fun setBluetoothGatt(bluetoothGatt: BluetoothGatt): ConnectException {
        this.bluetoothGatt = bluetoothGatt
        return this
    }

    override fun toString(): String {
        return "ConnectException{" +
                "gattStatus=" + gattStatus +
                ", bluetoothGatt=" + bluetoothGatt +
                "} " + super.toString()
    }
}
