package com.xzy.ble.baseble.callback.scan

import com.xzy.ble.baseble.model.BluetoothLeDevice


/**
 * 指定设备集合进行过滤，一般用设备名称和 Mac 地址集合
 */
@Suppress("unused")
class ListFilterScanCallback(scanCallback: IScanCallback) : ScanCallback(scanCallback) {
    private var deviceNameList: List<String>? = null//指定设备名称集合
    private var deviceMacList: List<String>? = null//指定设备 Mac 地址集合

    fun setDeviceNameList(deviceNameList: List<String>): ListFilterScanCallback {
        this.deviceNameList = deviceNameList
        return this
    }

    fun setDeviceMacList(deviceMacList: List<String>): ListFilterScanCallback {
        this.deviceMacList = deviceMacList
        return this
    }

    override fun onFilter(bluetoothLeDevice: BluetoothLeDevice): BluetoothLeDevice? {
        var tempDevice: BluetoothLeDevice? = null
        if (deviceNameList != null && deviceNameList!!.isNotEmpty()) {
            for (deviceName in deviceNameList!!) {
                if (deviceName.equals(bluetoothLeDevice.name.trim { it <= ' ' }, ignoreCase = true)) {
                    tempDevice = bluetoothLeDevice
                }
            }
        } else if (deviceMacList != null && deviceMacList!!.isNotEmpty()) {
            for (deviceMac in deviceMacList!!) {
                if (deviceMac.equals(bluetoothLeDevice.address.trim { it <= ' ' }, ignoreCase = true)) {
                    tempDevice = bluetoothLeDevice
                }
            }
        }
        return tempDevice
    }
}
