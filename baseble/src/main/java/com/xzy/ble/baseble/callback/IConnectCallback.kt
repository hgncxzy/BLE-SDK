package com.xzy.ble.baseble.callback
import com.xzy.ble.baseble.core.DeviceMirror
import com.xzy.ble.baseble.exception.BleException


/**
 * 连接设备回调
 */
interface IConnectCallback {
    //连接成功
    fun onConnectSuccess(deviceMirror: DeviceMirror)

    //连接失败
    fun onConnectFailure(exception: BleException)

    //连接断开
    fun onDisconnect(isActive: Boolean)
}
