package com.fcbox.ble.baseble.exception;


import com.fcbox.ble.baseble.common.BleExceptionCode;


/**
 * 其他异常
 */
@SuppressWarnings("unused")
public class OtherException extends BleException {
    public OtherException(String description) {
        super(BleExceptionCode.OTHER_ERR, description);
    }
}
