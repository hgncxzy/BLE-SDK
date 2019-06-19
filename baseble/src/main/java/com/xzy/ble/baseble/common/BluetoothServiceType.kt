package com.xzy.ble.baseble.common

import android.bluetooth.BluetoothClass

/**
 * BLE服务类型
 */
enum class BluetoothServiceType(//电话服务

        val code: Int) {
    AUDIO(BluetoothClass.Service.AUDIO), //音频服务
    CAPTURE(BluetoothClass.Service.CAPTURE), //捕捉服务
    INFORMATION(BluetoothClass.Service.INFORMATION), //信息服务
    LIMITED_DISCOVERABILITY(BluetoothClass.Service.LIMITED_DISCOVERABILITY), //有限发现服务
    NETWORKING(BluetoothClass.Service.NETWORKING), //网络服务
    OBJECT_TRANSFER(BluetoothClass.Service.OBJECT_TRANSFER), //对象传输服务
    POSITIONING(BluetoothClass.Service.POSITIONING), //定位服务
    RENDER(BluetoothClass.Service.RENDER), //给予服务
    TELEPHONY(BluetoothClass.Service.TELEPHONY)
}
