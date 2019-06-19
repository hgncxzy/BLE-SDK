package com.xzy.ble.baseble.callback


import com.xzy.ble.baseble.exception.BleException


/**
 * 获取信号值回调
 */
interface IRssiCallback {
    fun onSuccess(rssi: Int)

    fun onFailure(exception: BleException)
}
