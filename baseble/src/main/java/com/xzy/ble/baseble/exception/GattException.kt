package com.xzy.ble.baseble.exception


import com.xzy.ble.baseble.common.BleExceptionCode

/**
 * Gatt异常
 */
@Suppress("unused")
class GattException(private var gattStatus: Int) : BleException(BleExceptionCode.GATT_ERR, "Gatt Exception Occurred! ") {

    fun getGattStatus(): Int {
        return gattStatus
    }

    fun setGattStatus(gattStatus: Int): GattException {
        this.gattStatus = gattStatus
        return this
    }

    override fun toString(): String {
        return "GattException{" +
                "gattStatus=" + gattStatus +
                '}'.toString() + super.toString()
    }
}
