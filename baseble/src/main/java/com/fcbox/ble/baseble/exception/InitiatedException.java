package com.fcbox.ble.baseble.exception;


import com.fcbox.ble.baseble.common.BleExceptionCode;


/**
 * 初始化异常
 */
@SuppressWarnings("unused")
public class InitiatedException extends BleException {
    public InitiatedException() {
        super(BleExceptionCode.INITIATED_ERR, "Initiated Exception Occurred! ");
    }
}
