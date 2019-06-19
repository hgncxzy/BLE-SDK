package com.xzy.ble.baseble.model

import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap
import java.util.Objects

/**
 * 设备信息集合
 */
@Suppress("unused")
class BluetoothLeDeviceStore {

    private val mDeviceMap: MutableMap<String, BluetoothLeDevice>

    val deviceMap: Map<String, BluetoothLeDevice>
        get() = mDeviceMap

    val deviceList: List<BluetoothLeDevice>
        get() {
            val methodResult = ArrayList(mDeviceMap.values)

            methodResult.sortWith(Comparator { arg0, arg1 -> arg0.address.compareTo(arg1.address, ignoreCase = true) })

            return methodResult
        }

    init {
        mDeviceMap = HashMap()
    }

    fun addDevice(device: BluetoothLeDevice?) {
        if (device == null) {
            return
        }
        if (mDeviceMap.containsKey(device.address)) {
            Objects.requireNonNull<BluetoothLeDevice>(mDeviceMap[device.address]).updateRssiReading(device.timestamp, device.rssi)
        } else {
            mDeviceMap[device.address] = device
        }
    }

    fun removeDevice(device: BluetoothLeDevice?) {
        if (device == null) {
            return
        }
        mDeviceMap.remove(device.address)
    }

    fun clear() {
        mDeviceMap.clear()
    }

    override fun toString(): String {
        return "BluetoothLeDeviceStore{" +
                "DeviceList=" + deviceList +
                '}'.toString()
    }
}
