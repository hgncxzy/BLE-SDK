package com.xzy.ble.baseble.model.adrecord

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

import java.util.Arrays

/**
 * 广播包解析model
 */
@Suppress("unused")
class AdRecord : Parcelable {
    /* Model Object Definition */
    private val length: Int
    val type: Int
    val data: ByteArray?

    private val humanReadableType: String
        get() = getHumanReadableAdType(type)

    constructor(length: Int, type: Int, data: ByteArray) {
        this.length = length
        this.type = type
        this.data = data
    }

    constructor(`in`: Parcel) {
        val b = `in`.readBundle(javaClass.classLoader)
        length = b!!.getInt(PARCEL_RECORD_LENGTH)
        type = b.getInt(PARCEL_RECORD_TYPE)
        data = b.getByteArray(PARCEL_RECORD_DATA)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "AdRecord [mLength=" + length + ", mType=" + type + ", mData=" + Arrays.toString(data) + ", getHumanReadableType()=" +
                humanReadableType + "]"
    }

    override fun writeToParcel(parcel: Parcel, arg1: Int) {
        val b = Bundle(javaClass.classLoader)

        b.putInt(PARCEL_RECORD_LENGTH, length)
        b.putInt(PARCEL_RECORD_TYPE, type)
        b.putByteArray(PARCEL_RECORD_DATA, data)

        parcel.writeBundle(b)
    }

    companion object {

        private const val BLE_GAP_AD_TYPE_FLAGS = 0x01//< Flags for discoverAbility.
        private const val BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE = 0x02//< Partial list of 16 bit service UUIDs.
        private const val BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE = 0x03//< Complete list of 16 bit service UUIDs.
        private const val BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE = 0x04//< Partial list of 32 bit service UUIDs.
        private const val BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE = 0x05//< Complete list of 32 bit service UUIDs.
        private const val BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE = 0x06//< Partial list of 128 bit service UUIDs.
        private const val BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE = 0x07//< Complete list of 128 bit service UUIDs.
        const val BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME = 0x08//< Short local device name.
        const val BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME = 0x09//< Complete local device name.
        private const val BLE_GAP_AD_TYPE_TX_POWER_LEVEL = 0x0A//< Transmit power level.
        private const val BLE_GAP_AD_TYPE_CLASS_OF_DEVICE = 0x0D//< Class of device.
        private const val BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C = 0x0E//< Simple Pairing Hash C.
        private const val BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R = 0x0F//< Simple Pairing Randomizer R.
        private const val BLE_GAP_AD_TYPE_SECURITY_MANAGER_TK_VALUE = 0x10//< Security Manager TK Value.
        private const val BLE_GAP_AD_TYPE_SECURITY_MANAGER_OOB_FLAGS = 0x11//< Security Manager Out Of Band Flags.
        private const val BLE_GAP_AD_TYPE_SLAVE_CONNECTION_INTERVAL_RANGE = 0x12//< Slave Connection Interval Range.
        private const val BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_16BIT = 0x14//< List of 16-bit Service Solicitation UUIDs.
        private const val BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_128BIT = 0x15//< List of 128-bit Service Solicitation UUIDs.
        const val BLE_GAP_AD_TYPE_SERVICE_DATA = 0x16//< Service Data - 16-bit UUID.
        private const val BLE_GAP_AD_TYPE_PUBLIC_TARGET_ADDRESS = 0x17//< Public Target Address.
        private const val BLE_GAP_AD_TYPE_RANDOM_TARGET_ADDRESS = 0x18//< Random Target Address.
        private const val BLE_GAP_AD_TYPE_APPEARANCE = 0x19//< Appearance.
        private const val BLE_GAP_AD_TYPE_ADVERTISING_INTERVAL = 0x1A//< Advertising Interval.
        private const val BLE_GAP_AD_TYPE_LE_BLUETOOTH_DEVICE_ADDRESS = 0x1B//< LE Bluetooth Device Address.
        private const val BLE_GAP_AD_TYPE_LE_ROLE = 0x1C//< LE Role.
        private const val BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C256 = 0x1D//< Simple Pairing Hash C-256.
        private const val BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R256 = 0x1E//< Simple Pairing Randomizer R-256.
        private const val BLE_GAP_AD_TYPE_SERVICE_DATA_32BIT_UUID = 0x20//< Service Data - 32-bit UUID.
        private const val BLE_GAP_AD_TYPE_SERVICE_DATA_128BIT_UUID = 0x21//< Service Data - 128-bit UUID.
        private const val BLE_GAP_AD_TYPE_3D_INFORMATION_DATA = 0x3D//< 3D Information Data.
        private const val BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF//< Manufacturer Specific Data.

        @SuppressLint("ParcelCreator")
        val CREATOR: Parcelable.Creator<AdRecord> = object : Parcelable.Creator<AdRecord> {
            override fun createFromParcel(`in`: Parcel): AdRecord {
                return AdRecord(`in`)
            }

            override fun newArray(size: Int): Array<AdRecord?> {
                return arrayOfNulls(size)
            }
        }
        private const val PARCEL_RECORD_DATA = "record_data"
        private const val PARCEL_RECORD_TYPE = "record_type"
        private const val PARCEL_RECORD_LENGTH = "record_length"

        private fun getHumanReadableAdType(type: Int): String {
            when (type) {
                BLE_GAP_AD_TYPE_FLAGS -> return "Flags for discoverAbility."
                BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE -> return "Partial list of 16 bit service UUIDs."
                BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE -> return "Complete list of 16 bit service UUIDs."
                BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE -> return "Partial list of 32 bit service UUIDs."
                BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE -> return "Complete list of 32 bit service UUIDs."
                BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE -> return "Partial list of 128 bit service UUIDs."
                BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE -> return "Complete list of 128 bit service UUIDs."
                BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME -> return "Short local device name."
                BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME -> return "Complete local device name."
                BLE_GAP_AD_TYPE_TX_POWER_LEVEL -> return "Transmit power level."
                BLE_GAP_AD_TYPE_CLASS_OF_DEVICE -> return "Class of device."
                BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C -> return "Simple Pairing Hash C."
                BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R -> return "Simple Pairing Randomizer R."
                BLE_GAP_AD_TYPE_SECURITY_MANAGER_TK_VALUE -> return "Security Manager TK Value."
                BLE_GAP_AD_TYPE_SECURITY_MANAGER_OOB_FLAGS -> return "Security Manager Out Of Band Flags."
                BLE_GAP_AD_TYPE_SLAVE_CONNECTION_INTERVAL_RANGE -> return "Slave Connection Interval Range."
                BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_16BIT -> return "List of 16-bit Service Solicitation UUIDs."
                BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_128BIT -> return "List of 128-bit Service Solicitation UUIDs."
                BLE_GAP_AD_TYPE_SERVICE_DATA -> return "Service Data - 16-bit UUID."
                BLE_GAP_AD_TYPE_PUBLIC_TARGET_ADDRESS -> return "Public Target Address."
                BLE_GAP_AD_TYPE_RANDOM_TARGET_ADDRESS -> return "Random Target Address."
                BLE_GAP_AD_TYPE_APPEARANCE -> return "Appearance."
                BLE_GAP_AD_TYPE_ADVERTISING_INTERVAL -> return "Advertising Interval."
                BLE_GAP_AD_TYPE_LE_BLUETOOTH_DEVICE_ADDRESS -> return "LE Bluetooth Device Address."
                BLE_GAP_AD_TYPE_LE_ROLE -> return "LE Role."
                BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C256 -> return "Simple Pairing Hash C-256."
                BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R256 -> return "Simple Pairing Randomizer R-256."
                BLE_GAP_AD_TYPE_SERVICE_DATA_32BIT_UUID -> return "Service Data - 32-bit UUID."
                BLE_GAP_AD_TYPE_SERVICE_DATA_128BIT_UUID -> return "Service Data - 128-bit UUID."
                BLE_GAP_AD_TYPE_3D_INFORMATION_DATA -> return "3D Information Data."
                BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA -> return "Manufacturer Specific Data."
                else -> return "Unknown AdRecord Structure: $type"
            }
        }
    }
}
