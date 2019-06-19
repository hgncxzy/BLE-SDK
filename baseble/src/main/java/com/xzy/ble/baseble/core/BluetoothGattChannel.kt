package com.xzy.ble.baseble.core

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import com.xzy.ble.baseble.common.PropertyType


import java.util.UUID

/**
 * BluetoothGatt 相关信息
 */
@Suppress("unused")
class BluetoothGattChannel private constructor(
        bluetoothGatt: BluetoothGatt?, internal val propertyType: PropertyType?, serviceUUID: UUID?, characteristicUUID: UUID?, descriptorUUID: UUID?) {
    var service: BluetoothGattService? = null
        private set
    internal var characteristic: BluetoothGattCharacteristic? = null
        private set
    var descriptor: BluetoothGattDescriptor? = null
    val gattInfoKey: String

    init {
        val stringBuilder = StringBuilder()
        if (propertyType != null) {
            stringBuilder.append(propertyType.propertyValue)
        }
        if (serviceUUID != null && bluetoothGatt != null) {
            service = bluetoothGatt.getService(serviceUUID)
            stringBuilder.append(serviceUUID.toString())
        }
        if (service != null && characteristicUUID != null) {
            characteristic = service!!.getCharacteristic(characteristicUUID)
            stringBuilder.append(characteristicUUID.toString())
        }
        if (characteristic != null && descriptorUUID != null) {
            descriptor = characteristic!!.getDescriptor(descriptorUUID)
            stringBuilder.append(descriptorUUID.toString())
        }
        gattInfoKey = stringBuilder.toString()
    }

    internal fun getDescriptor(): BluetoothGattDescriptor? {
        return descriptor
    }

    internal fun setDescriptor(descriptor: BluetoothGattDescriptor): BluetoothGattChannel {
        this.descriptor = descriptor
        return this
    }

    class Builder {
        private var bluetoothGatt: BluetoothGatt? = null
        private var propertyType: PropertyType? = null
        private var serviceUUID: UUID? = null
        private var characteristicUUID: UUID? = null
        private var descriptorUUID: UUID? = null

        fun setBluetoothGatt(bluetoothGatt: BluetoothGatt): Builder {
            this.bluetoothGatt = bluetoothGatt
            return this
        }

        fun setCharacteristicUUID(characteristicUUID: UUID): Builder {
            this.characteristicUUID = characteristicUUID
            return this
        }

        fun setDescriptorUUID(descriptorUUID: UUID): Builder {
            this.descriptorUUID = descriptorUUID
            return this
        }

        fun setPropertyType(propertyType: PropertyType): Builder {
            this.propertyType = propertyType
            return this
        }

        fun setServiceUUID(serviceUUID: UUID): Builder {
            this.serviceUUID = serviceUUID
            return this
        }

        fun builder(): BluetoothGattChannel {
            return BluetoothGattChannel(bluetoothGatt, propertyType, serviceUUID, characteristicUUID, descriptorUUID)
        }
    }
}
