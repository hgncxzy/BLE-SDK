package com.fcbox.ble.baseble.exception;


import com.fcbox.ble.baseble.common.BleExceptionCode;

/**
 * 超时异常
 */
@SuppressWarnings("unused")
public class TimeoutException extends BleException {
    public TimeoutException() {
        super(BleExceptionCode.TIMEOUT, "Timeout Exception Occurred! ");
    }
}
