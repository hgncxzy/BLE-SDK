package com.xzy.ble.baseble.exception


import com.xzy.ble.baseble.common.BleExceptionCode


/**
 * 初始化异常
 */
class InitiatedException : BleException(BleExceptionCode.INITIATED_ERR, "Initiated Exception Occurred! ")
