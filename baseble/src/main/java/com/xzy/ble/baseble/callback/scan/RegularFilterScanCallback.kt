package com.xzy.ble.baseble.callback.scan

import android.text.TextUtils
import com.xzy.ble.baseble.model.BluetoothLeDevice
import java.util.regex.Pattern

/**
 * 根据正则过滤扫描设备，这里设置的是根据一定信号范围内指定正则设备名称的过滤
 */
@Suppress("unused")
class RegularFilterScanCallback(scanCallback: IScanCallback) : ScanCallback(scanCallback) {
    private var pattern: Pattern? = null
    private var deviceRssi: Int = 0//设备的信号

    init {
        pattern = Pattern.compile("^[\\x00-\\xff]*$")
    }

    fun setRegularDeviceName(regularDeviceName: String): RegularFilterScanCallback {
        //正则表达式表示的设备名称
        if (!TextUtils.isEmpty(regularDeviceName)) {
            pattern = Pattern.compile(regularDeviceName)
        }
        return this
    }

    fun setDeviceRssi(deviceRssi: Int): RegularFilterScanCallback {
        this.deviceRssi = deviceRssi
        return this
    }

    override fun onFilter(bluetoothLeDevice: BluetoothLeDevice): BluetoothLeDevice? {
        var tempDevice: BluetoothLeDevice? = null
        val tempName = bluetoothLeDevice.name
        val tempRssi = bluetoothLeDevice.rssi
        if (!TextUtils.isEmpty(tempName)) {
            val matcher = pattern!!.matcher(tempName)
            if (this.deviceRssi < 0) {
                if (matcher.matches() && tempRssi >= this.deviceRssi) {
                    tempDevice = bluetoothLeDevice
                }
            } else {
                if (matcher.matches()) {
                    tempDevice = bluetoothLeDevice
                }
            }
        }
        return tempDevice
    }
}
