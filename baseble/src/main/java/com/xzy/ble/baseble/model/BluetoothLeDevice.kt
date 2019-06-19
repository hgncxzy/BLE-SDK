@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.xzy.ble.baseble.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

import com.xzy.ble.baseble.common.BluetoothServiceType
import com.xzy.ble.baseble.model.adrecord.AdRecordStore
import com.xzy.ble.baseble.model.resolver.BluetoothClassResolver
import com.xzy.ble.baseble.utils.AdRecordUtil
import com.xzy.ble.baseble.utils.HexUtil

import java.io.Serializable
import java.util.Arrays
import java.util.Collections
import java.util.HashSet
import java.util.LinkedHashMap


/**
 * 设备信息
 */
@Suppress("unused")
class BluetoothLeDevice : Parcelable {
    /**
     * Gets the ad record store.
     *
     * @return the ad record store
     */
    private val adRecordStore: AdRecordStore?
    /**
     * Gets the device.
     *
     * @return the device
     */
    val device: BluetoothDevice
    private val mRssiLog: MutableMap<Long, Int>
    /**
     * Gets the scan record.
     *
     * @return the scan record
     */
    val scanRecord: ByteArray
    /**
     * Gets the first rssi.
     *
     * @return the first rssi
     */
    val firstRssi: Int
    /**
     * Gets the first timestamp.
     *
     * @return the first timestamp
     */
    val firstTimestamp: Long
    /**
     * Gets the rssi.
     *
     * @return the rssi
     */
    var rssi: Int = 0
        private set
    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    var timestamp: Long = 0
        private set
    @Transient
    private var mServiceSet: Set<BluetoothServiceType>? = null

    /**
     * Gets the address.
     *
     * @return the address
     */
    val address: String
        get() = device.address

    /**
     * Gets the bluetooth device bond state.
     *
     * @return the bluetooth device bond state
     */
    val bluetoothDeviceBondState: String
        get() = resolveBondingState(device.bondState)

    /**
     * Gets the bluetooth device class name.
     *
     * @return the bluetooth device class name
     */
    val bluetoothDeviceClassName: String
        get() = BluetoothClassResolver.resolveDeviceClass(device.bluetoothClass.deviceClass)

    val bluetoothDeviceKnownSupportedServices: Set<BluetoothServiceType>?
        get() {
            if (mServiceSet == null) {
                synchronized(this) {
                    if (mServiceSet == null) {
                        val serviceSet = HashSet<BluetoothServiceType>()
                        for (service in BluetoothServiceType.values()) {

                            if (device.bluetoothClass.hasService(service.code)) {
                                serviceSet.add(service)
                            }
                        }
                        mServiceSet = Collections.unmodifiableSet(serviceSet)
                    }
                }
            }

            return mServiceSet
        }

    /**
     * Gets the bluetooth device major class name.
     *
     * @return the bluetooth device major class name
     */
    val bluetoothDeviceMajorClassName: String
        get() = BluetoothClassResolver.resolveMajorDeviceClass(device.bluetoothClass.majorDeviceClass)

    /**
     * Gets the name.
     *
     * @return the name
     */
    val name: String
        get() = device.name

    /**
     * Gets the rssi log.
     *
     * @return the rssi log
     */
    protected val rssiLog: MutableMap<Long, Int>
        get() {
            synchronized(mRssiLog) {
                return mRssiLog
            }
        }

    /**
     * Gets the running average rssi.
     *
     * @return the running average rssi
     */
    val runningAverageRssi: Double
        get() {
            var sum = 0
            var count = 0

            synchronized(mRssiLog) {

                for (aLong in mRssiLog.keys) {
                    count++
                    sum += mRssiLog[aLong]!!
                }
            }

            return if (count > 0) {
                (sum / count).toDouble()
            } else {
                0.0
            }

        }

    /**
     * 获取major
     * @return major
     */
    val major: Long
        get() = HexUtil.byteToLong(HexUtil.subBytes(scanRecord, 25, 2), 0, 2, true)

    /**
     * Instantiates a new Bluetooth LE device.
     *
     * @param device     a standard android Bluetooth device
     * @param rssi       the RSSI value of the Bluetooth device
     * @param scanRecord the scan record of the device
     * @param timestamp  the timestamp of the RSSI reading
     */
    constructor(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray, timestamp: Long) {
        this.device = device
        firstRssi = rssi
        firstTimestamp = timestamp
        adRecordStore = AdRecordStore(AdRecordUtil.parseScanRecordAsSparseArray(scanRecord))
        this.scanRecord = scanRecord
        mRssiLog = LinkedHashMap(MAX_RSSI_LOG_SIZE)
        updateRssiReading(timestamp, rssi)
    }

    /**
     * Instantiates a new Bluetooth LE device.
     *
     * @param device the device
     */
    constructor(device: BluetoothLeDevice) {
        rssi = device.rssi
        timestamp = device.timestamp
        this.device = device.device
        firstRssi = device.firstRssi
        firstTimestamp = device.firstTimestamp
        adRecordStore = AdRecordStore(AdRecordUtil.parseScanRecordAsSparseArray(device.scanRecord))
        mRssiLog = device.rssiLog
        scanRecord = device.scanRecord
    }

    /**
     * Instantiates a new bluetooth le device.
     *
     * @param in the in
     */
    protected constructor(`in`: Parcel) {
        val b = `in`.readBundle(javaClass.classLoader)

        rssi = b.getInt(PARCEL_EXTRA_CURRENT_RSSI, 0)
        timestamp = b!!.getLong(PARCEL_EXTRA_CURRENT_TIMESTAMP, 0)
        device = b.getParcelable(PARCEL_EXTRA_BLUETOOTH_DEVICE)
        firstRssi = b.getInt(PARCEL_EXTRA_FIRST_RSSI, 0)
        firstTimestamp = b.getLong(PARCEL_EXTRA_FIRST_TIMESTAMP, 0)
        adRecordStore = b.getParcelable(PARCEL_EXTRA_DEVICE_SCANRECORD_STORE)
        mRssiLog = b.getSerializable(PARCEL_EXTRA_DEVICE_RSSI_LOG) as MutableMap<Long, Int>
        scanRecord = b.getByteArray(PARCEL_EXTRA_DEVICE_SCANRECORD)
    }

    /**
     * Adds the to rssi log.
     *
     * @param timestamp   the timestamp
     * @param rssiReading the rssi reading
     */
    private fun addToRssiLog(timestamp: Long, rssiReading: Int) {
        synchronized(mRssiLog) {
            if (timestamp - this.timestamp > LOG_INVALIDATION_THRESHOLD) {
                mRssiLog.clear()
            }

            rssi = rssiReading
            this.timestamp = timestamp
            mRssiLog.put(timestamp, rssiReading)
        }
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    override fun describeContents(): Int {
        return 0
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val other = other as BluetoothLeDevice?
        if (rssi != other!!.rssi) return false
        if (timestamp != other.timestamp) return false
        if (device != other.device) return false
        if (firstRssi != other.firstRssi) return false
        if (firstTimestamp != other.firstTimestamp) return false
        if (adRecordStore == null) {
            if (other.adRecordStore != null) return false
        } else if (adRecordStore != other.adRecordStore) return false
        if (mRssiLog != other.mRssiLog) return false
        return Arrays.equals(scanRecord, other.scanRecord)
    }

    /**
     * 获取minor
     * @return minor
     */
    fun minor(): Long {
        return HexUtil.byteToLong(HexUtil.subBytes(scanRecord, 27, 2), 0, 2, true)
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + rssi
        result = prime * result + (timestamp xor timestamp.ushr(32)).toInt()
        result = prime * result + device.hashCode()
        result = prime * result + firstRssi
        result = prime * result + (firstTimestamp xor firstTimestamp.ushr(32)).toInt()
        result = prime * result + (adRecordStore?.hashCode() ?: 0)
        result = prime * result + mRssiLog.hashCode()
        result = prime * result + Arrays.hashCode(scanRecord)
        return result
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    override fun toString(): String {
        return "BluetoothLeDevice [mDevice=" + device + ", " +
                "mRssi=" + firstRssi + ", mScanRecord=" + HexUtil.encodeHexStr(scanRecord) +
                ", mRecordStore=" + adRecordStore + ", getBluetoothDeviceBondState()=" +
                bluetoothDeviceBondState + ", getBluetoothDeviceClassName()=" +
                bluetoothDeviceClassName + "]"
    }

    /**
     * Update rssi reading.
     *
     * @param timestamp   the timestamp
     * @param rssiReading the rssi reading
     */
    fun updateRssiReading(timestamp: Long, rssiReading: Int) {
        addToRssiLog(timestamp, rssiReading)
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    override fun writeToParcel(parcel: Parcel, arg1: Int) {
        val b = Bundle(javaClass.classLoader)

        b.putByteArray(PARCEL_EXTRA_DEVICE_SCANRECORD, scanRecord)

        b.putInt(PARCEL_EXTRA_FIRST_RSSI, firstRssi)
        b.putInt(PARCEL_EXTRA_CURRENT_RSSI, rssi)

        b.putLong(PARCEL_EXTRA_FIRST_TIMESTAMP, firstTimestamp)
        b.putLong(PARCEL_EXTRA_CURRENT_TIMESTAMP, timestamp)

        b.putParcelable(PARCEL_EXTRA_BLUETOOTH_DEVICE, device)
        b.putParcelable(PARCEL_EXTRA_DEVICE_SCANRECORD_STORE, adRecordStore)
        b.putSerializable(PARCEL_EXTRA_DEVICE_RSSI_LOG, mRssiLog as Serializable?)

        parcel.writeBundle(b)
    }

    companion object {

        /**
         * The Constant CREATOR.
         */
        @SuppressLint("ParcelCreator")
        val CREATOR: Parcelable.Creator<BluetoothLeDevice> = object : Parcelable.Creator<BluetoothLeDevice> {
            override fun createFromParcel(`in`: Parcel): BluetoothLeDevice {
                return BluetoothLeDevice(`in`)
            }

            override fun newArray(size: Int): Array<BluetoothLeDevice?> {
                return arrayOfNulls(size)
            }
        }
        private const val MAX_RSSI_LOG_SIZE = 10
        private const val PARCEL_EXTRA_BLUETOOTH_DEVICE = "bluetooth_device"
        private const val PARCEL_EXTRA_CURRENT_RSSI = "current_rssi"
        private const val PARCEL_EXTRA_CURRENT_TIMESTAMP = "current_timestamp"
        private const val PARCEL_EXTRA_DEVICE_RSSI_LOG = "device_rssi_log"
        private const val PARCEL_EXTRA_DEVICE_SCANRECORD = "device_scanrecord"
        private const val PARCEL_EXTRA_DEVICE_SCANRECORD_STORE = "device_scanrecord_store"
        private const val PARCEL_EXTRA_FIRST_RSSI = "device_first_rssi"
        private const val PARCEL_EXTRA_FIRST_TIMESTAMP = "first_timestamp"
        private const val LOG_INVALIDATION_THRESHOLD = (10 * 1000).toLong()

        /**
         * Resolve bonding state.
         *
         * @param bondState the bond state
         * @return the string
         */
        private fun resolveBondingState(bondState: Int): String {
            return when (bondState) {
                BluetoothDevice.BOND_BONDED//已配对
                -> "Paired"
                BluetoothDevice.BOND_BONDING//配对中
                -> "Pairing"
                BluetoothDevice.BOND_NONE//未配对
                -> "UnBonded"
                else -> "Unknown"//未知状态
            }
        }
    }
}
