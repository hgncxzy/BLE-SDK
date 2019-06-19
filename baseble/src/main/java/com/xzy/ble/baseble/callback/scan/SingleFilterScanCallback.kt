package com.xzy.ble.baseble.callback.scan
import com.xzy.ble.baseble.BaseBle
import com.xzy.ble.baseble.model.BluetoothLeDevice

import java.util.concurrent.atomic.AtomicBoolean


/**
 * 设置扫描指定的单个设备，一般是设备名称和 Mac 地址
 */
@Suppress("unused")
class SingleFilterScanCallback(scanCallback: IScanCallback) : ScanCallback(scanCallback) {
    private val hasFound = AtomicBoolean(false)
    private var deviceName: String? = null//指定设备名称
    private var deviceMac: String? = null//指定设备Mac地址

    fun setDeviceName(deviceName: String): ScanCallback {
        this.deviceName = deviceName
        return this
    }

    fun setDeviceMac(deviceMac: String): ScanCallback {
        this.deviceMac = deviceMac
        return this
    }

    override fun onFilter(bluetoothLeDevice: BluetoothLeDevice): BluetoothLeDevice? {
        var tempDevice: BluetoothLeDevice? = null
        if (!hasFound.get()) {
            if (deviceMac != null && deviceMac!!.equals(bluetoothLeDevice.address.trim { it <= ' ' }, ignoreCase = true)) {
                hasFound.set(true)
                isScanning = false
                removeHandlerMsg()
                BaseBle.getInstance()?.stopScan(this@SingleFilterScanCallback)
                tempDevice = bluetoothLeDevice
                bluetoothLeDeviceStore.addDevice(bluetoothLeDevice)
                scanCallback!!.onScanFinish(bluetoothLeDeviceStore)
            } else if (deviceName != null && deviceName.equals(bluetoothLeDevice.name.trim { it <= ' ' }, ignoreCase = true)) {
                hasFound.set(true)
                isScanning = false
                removeHandlerMsg()
                BaseBle.getInstance()?.stopScan(this@SingleFilterScanCallback)
                tempDevice = bluetoothLeDevice
                bluetoothLeDeviceStore.addDevice(bluetoothLeDevice)
                scanCallback!!.onScanFinish(bluetoothLeDeviceStore)
            }
        }
        return tempDevice
    }
}
