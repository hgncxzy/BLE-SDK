package com.xzy.ble.baseble.exception.handler
import com.xzy.ble.baseble.exception.*


/**
 * 异常处理
 */
abstract class BleExceptionHandler {
    fun handleException(exception: BleException?): BleExceptionHandler {
        if (exception != null) {
            if (exception is ConnectException) {
                onConnectException(exception as ConnectException?)
            } else if (exception is GattException) {
                onGattException(exception as GattException?)
            } else if (exception is TimeoutException) {
                onTimeoutException(exception as TimeoutException?)
            } else if (exception is InitiatedException) {
                onInitiatedException(exception as InitiatedException?)
            } else {
                onOtherException(exception as OtherException?)
            }
        }
        return this
    }

    /**
     * connect failed
     */
    protected abstract fun onConnectException(e: ConnectException?)

    /**
     * gatt error status
     */
    protected abstract fun onGattException(e: GattException?)

    /**
     * operation timeout
     */
    protected abstract fun onTimeoutException(e: TimeoutException?)

    /**
     * operation inititiated error
     */
    protected abstract fun onInitiatedException(e: InitiatedException?)

    /**
     * other exceptions
     */
    protected abstract fun onOtherException(e: OtherException?)
}
