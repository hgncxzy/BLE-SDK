package com.xzy.ble.baseble.exception


import com.xzy.ble.baseble.common.BleExceptionCode

/**
 * 超时异常
 */
class TimeoutException : BleException(BleExceptionCode.TIMEOUT, "Timeout Exception Occurred! ")
