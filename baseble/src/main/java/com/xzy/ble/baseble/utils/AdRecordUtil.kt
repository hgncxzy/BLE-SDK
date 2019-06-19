package com.xzy.ble.baseble.utils

import android.annotation.SuppressLint
import android.util.SparseArray

import com.xzy.ble.baseble.model.adrecord.AdRecord

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.HashMap
import kotlin.experimental.and

/**
 * 广播包解析工具类
 */
@Suppress("unused")
object AdRecordUtil {

    fun getRecordDataAsString(nameRecord: AdRecord?): String {
        return if (nameRecord == null) {
            ""
        } else String(nameRecord.data!!)
    }

    fun getServiceData(serviceData: AdRecord?): ByteArray? {
        if (serviceData == null) {
            return null
        }
        if (serviceData.type != AdRecord.BLE_GAP_AD_TYPE_SERVICE_DATA) return null

        val raw = serviceData.data
        //Chop out the uuid
        return Arrays.copyOfRange(raw, 2, raw!!.size)
    }

    fun getServiceDataUuid(serviceData: AdRecord?): Int {
        if (serviceData == null) {
            return -1
        }
        if (serviceData.type != AdRecord.BLE_GAP_AD_TYPE_SERVICE_DATA) return -1

        val raw = serviceData.data
        //Find UUID data in byte array
        var uuid = (raw!![1] and 0xFF.toByte()).toInt() shl(8)
        uuid = uuid.plus(raw[0] and 0xFF.toByte())

        return uuid
    }

    /*
     * Read out all the AD structures from the raw scan record
     */
    fun parseScanRecordAsList(scanRecord: ByteArray): List<AdRecord> {
        val records = ArrayList<AdRecord>()

        var index = 0
        while (index < scanRecord.size) {
            val length = scanRecord[index++].toInt()
            //Done once we run out of records
            if (length == 0) break

            val type = scanRecord[index] and 0xFF.toByte()

            //Done if our record isn't a valid type
            if (type.toInt() == 0) {
                break
            }

            val data = Arrays.copyOfRange(scanRecord, index + 1, index + length)

            records.add(AdRecord(length, type.toInt(), data))

            //Advance
            index += length
        }

        return Collections.unmodifiableList(records)
    }

    fun parseScanRecordAsMap(scanRecord: ByteArray): Map<Int, AdRecord> {
        @SuppressLint("UseSparseArrays") val records = HashMap<Int, AdRecord>()

        var index = 0
        while (index < scanRecord.size) {
            val length = scanRecord[index++].toInt()
            //Done once we run out of records
            if (length == 0) break

            val type = scanRecord[index] and 0xFF.toByte()

            //Done if our record isn't a valid type
            if (type.toInt() == 0) {
                break
            }

            val data = Arrays.copyOfRange(scanRecord, index + 1, index + length)

            records[type.toInt()] = AdRecord(length, type.toInt(), data)

            //Advance
            index += length
        }

        return Collections.unmodifiableMap(records)
    }

    fun parseScanRecordAsSparseArray(scanRecord: ByteArray): SparseArray<AdRecord> {
        val records = SparseArray<AdRecord>()

        var index = 0
        while (index < scanRecord.size) {
            val length = scanRecord[index++].toInt()
            //Done once we run out of records
            if (length == 0) break

            val type = scanRecord[index] and 0xFF.toByte()

            //Done if our record isn't a valid type
            if (type.toInt() == 0) break

            val data = Arrays.copyOfRange(scanRecord, index + 1, index + length)

            records.put(type.toInt(), AdRecord(length, type.toInt(), data))

            //Advance
            index += length
        }

        return records
    }
}// TO AVOID INSTANTIATION
