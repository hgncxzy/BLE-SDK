package com.xzy.ble.baseble.callback.scan

import com.xzy.ble.baseble.model.BluetoothLeDevice
import java.util.UUID

/**
 * 根据指定uuid过滤设备
 */
@Suppress("unused")
class UuidFilterScanCallback(scanCallback: IScanCallback) : ScanCallback(scanCallback) {
    private var uuid: UUID? = null//设备uuid

    fun setUuid(uuid: String): UuidFilterScanCallback {
        this.uuid = UUID.fromString(uuid)
        return this
    }

    fun setUuid(uuid: UUID): UuidFilterScanCallback {
        this.uuid = uuid
        return this
    }

    override fun onFilter(bluetoothLeDevice: BluetoothLeDevice): BluetoothLeDevice? {
        var tempDevice: BluetoothLeDevice? = null
        if (bluetoothLeDevice.device.uuids != null
                && bluetoothLeDevice.device.uuids.isNotEmpty()) {
            for (parcelUuid in bluetoothLeDevice.device.uuids) {
                if (uuid != null && uuid === parcelUuid.uuid) {
                    tempDevice = bluetoothLeDevice
                }
            }
        }
        return tempDevice
    }
}
