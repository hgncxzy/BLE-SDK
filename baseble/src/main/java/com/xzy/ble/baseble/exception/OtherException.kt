package com.xzy.ble.baseble.exception

import com.xzy.ble.baseble.common.BleExceptionCode


/**
 * 其他异常
 */
class OtherException(description: String) : BleException(BleExceptionCode.OTHER_ERR, description)
