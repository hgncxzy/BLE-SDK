package com.example.demo.config

import com.xzy.ble.baseble.utils.HexUtil


/**
 * Description : 常量
 * Created by XuZhuYun 2019/6/13 18:09 .
 */
@Suppress("unused")
object Config {
    var targetDeviceMac = "00:15:80:90:76:20"
    var targetDeviceName = "devName"
    const val TAG = "[XZYBox-ble-demo-log]"

    // 发送 byte 数组
    var byte1 = byteArrayOf(0x48, 0x42, 0xa0.toByte(), 0x01, 0x01)
    var byte2 = byteArrayOf(0x48, 0x42,0xa0.toByte(), 0x01, 0x01, HexUtil.xOrVerify(byte1))
}
