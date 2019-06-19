package com.xzy.ble.baseble.common

import java.util.*


/**
 * BLE常量
 */
@Suppress("unused")
object BleConstant{

    const val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    var SERVICE_UUID:UUID = UUID.randomUUID()
    var CHARACTERISTIC_UUID:UUID = UUID.randomUUID()
    var DESCRIPTOR_UUID:UUID = UUID.randomUUID()

    const val TIME_FOREVER = -1

    internal const val DEFAULT_SCAN_TIME = 20000
    internal const val DEFAULT_CONN_TIME = 10000
    internal const val DEFAULT_OPERATE_TIME = 5000

    internal const val DEFAULT_RETRY_INTERVAL = 1000
    internal const val DEFAULT_RETRY_COUNT = 3
    internal const val DEFAULT_MAX_CONNECT_COUNT = 5

    const val MSG_CONNECT_TIMEOUT = 0x01
    const val MSG_WRITE_DATA_TIMEOUT = 0x02
    const val MSG_READ_DATA_TIMEOUT = 0x03
    const val MSG_RECEIVE_DATA_TIMEOUT = 0x04
    const val MSG_CONNECT_RETRY = 0x05
    const val MSG_WRITE_DATA_RETRY = 0x06
    const val MSG_READ_DATA_RETRY = 0x07
    const val MSG_RECEIVE_DATA_RETRY = 0x08

    internal const val DEFAULT_SCAN_REPEAT_INTERVAL = -1

    // 蓝牙使能识别码
    const val REQUEST_ENABLE_BT = 0x001
    // 权限请求码
    const val MY_PERMISSION_REQUEST_CONSTANT = 0x002

    const val SEND_CMD_SUCCESS = "com.fc_box.send.cmd.SUCCESS"
    const val SEND_CMD_FAILED = "com.fc_box.send.cmd.FAILED"
    const val RECEIVE_DATA_SUCCESS = "com.fc_box.receive_data_SUCCESS"
    const val RECEIVE_DATA_FAILED = "com.fc_box.receive_data_FAILED"

}
