package com.fcbox.ble.baseble.exception.handler;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.fcbox.ble.baseble.exception.ConnectException;
import com.fcbox.ble.baseble.exception.GattException;
import com.fcbox.ble.baseble.exception.InitiatedException;
import com.fcbox.ble.baseble.exception.OtherException;
import com.fcbox.ble.baseble.exception.TimeoutException;


/**
 * 异常默认处理
 */
@SuppressWarnings("unused")
public class DefaultBleExceptionHandler extends BleExceptionHandler {
    @Override
    protected void onConnectException(ConnectException e) {
        Log.e(TAG,e.getDescription());
    }

    @Override
    protected void onGattException(GattException e) {
        Log.e(TAG,e.getDescription());
    }

    @Override
    protected void onTimeoutException(TimeoutException e) {
        Log.e(TAG,e.getDescription());
    }

    @Override
    protected void onInitiatedException(InitiatedException e) {
        Log.e(TAG,e.getDescription());
    }

    @Override
    protected void onOtherException(OtherException e) {
        Log.e(TAG,e.getDescription());
    }
}
