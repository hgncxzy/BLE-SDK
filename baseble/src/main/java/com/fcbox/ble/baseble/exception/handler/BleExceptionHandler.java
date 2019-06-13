package com.fcbox.ble.baseble.exception.handler;

import com.fcbox.ble.baseble.exception.BleException;
import com.fcbox.ble.baseble.exception.ConnectException;
import com.fcbox.ble.baseble.exception.GattException;
import com.fcbox.ble.baseble.exception.InitiatedException;
import com.fcbox.ble.baseble.exception.OtherException;
import com.fcbox.ble.baseble.exception.TimeoutException;


/**
 * 异常处理
 */
@SuppressWarnings("unused")
public abstract class BleExceptionHandler {
    public BleExceptionHandler handleException(BleException exception) {
        if (exception != null) {
            if (exception instanceof ConnectException) {
                onConnectException((ConnectException) exception);
            } else if (exception instanceof GattException) {
                onGattException((GattException) exception);
            } else if (exception instanceof TimeoutException) {
                onTimeoutException((TimeoutException) exception);
            } else if (exception instanceof InitiatedException) {
                onInitiatedException((InitiatedException) exception);
            } else {
                onOtherException((OtherException) exception);
            }
        }
        return this;
    }

    /**
     * connect failed
     */
    protected abstract void onConnectException(ConnectException e);

    /**
     * gatt error status
     */
    protected abstract void onGattException(GattException e);

    /**
     * operation timeout
     */
    protected abstract void onTimeoutException(TimeoutException e);

    /**
     * operation inititiated error
     */
    protected abstract void onInitiatedException(InitiatedException e);

    /**
     * other exceptions
     */
    protected abstract void onOtherException(OtherException e);
}
