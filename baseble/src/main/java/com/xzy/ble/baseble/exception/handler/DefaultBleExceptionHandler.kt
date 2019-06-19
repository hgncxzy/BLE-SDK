package com.xzy.ble.baseble.exception.handler

import android.content.ContentValues.TAG

import android.util.Log
import com.xzy.ble.baseble.exception.*


/**
 * 异常默认处理
 */
class DefaultBleExceptionHandler : BleExceptionHandler() {
    override fun onConnectException(e: ConnectException?) {
        Log.e(TAG, e!!.description)
    }

    override fun onGattException(e: GattException?) {
        Log.e(TAG, e!!.description)
    }

    override fun onTimeoutException(e: TimeoutException?) {
        Log.e(TAG, e!!.description)
    }

    override fun onInitiatedException(e: InitiatedException?) {
        Log.e(TAG, e!!.description)
    }

    override fun onOtherException(e: OtherException?) {
        Log.e(TAG, e!!.description)
    }
}
