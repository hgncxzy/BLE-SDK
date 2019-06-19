package com.xzy.ble.baseble.model.adrecord

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import com.xzy.ble.baseble.utils.AdRecordUtil

import java.util.ArrayList
import java.util.Collections

/**
 * 广播包解析仓库
 */
@Suppress("unused")
class AdRecordStore : Parcelable {
    private val mAdRecords: SparseArray<AdRecord>?
    /**
     * Gets the short local device name.
     *
     * @return the local name complete
     */
    private val localNameComplete: String?
    /**
     * Gets the complete local device name.
     *
     * @return the local name short
     */
    private val localNameShort: String?

    /**
     * Gets the record as collection.
     *
     * @return the records as collection
     */
    val recordsAsCollection: Collection<AdRecord>
        get() = Collections.unmodifiableCollection(asList(mAdRecords))

    constructor(`in`: Parcel) {
        val b = `in`.readBundle(javaClass.classLoader)
        mAdRecords = b!!.getSparseParcelableArray(RECORDS_ARRAY)
        localNameComplete = b.getString(LOCAL_NAME_COMPLETE)
        localNameShort = b.getString(LOCAL_NAME_SHORT)
    }

    /**
     * Instantiates a new Bluetooth LE device Ad Record Store.
     *
     * @param adRecords the ad records
     */
    constructor(adRecords: SparseArray<AdRecord>) {
        mAdRecords = adRecords
        localNameComplete = AdRecordUtil.getRecordDataAsString(mAdRecords.get(AdRecord.BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME))
        localNameShort = AdRecordUtil.getRecordDataAsString(mAdRecords.get(AdRecord.BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME))

    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * retrieves an individual record.
     *
     * @param record the record
     * @return the record
     */
    fun getRecord(record: Int): AdRecord {
        return mAdRecords!!.get(record)
    }

    /**
     * Gets the record data as string.
     *
     * @param record the record
     * @return the record data as string
     */
    fun getRecordDataAsString(record: Int): String {
        return AdRecordUtil.getRecordDataAsString(mAdRecords!!.get(record))
    }

    /**
     * Checks if is record present.
     *
     * @param record the record
     * @return true, if is record present
     */
    fun isRecordPresent(record: Int): Boolean {
        return mAdRecords!!.indexOfKey(record) >= 0
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    override fun toString(): String {
        return "AdRecordStore [mLocalNameComplete=$localNameComplete, mLocalNameShort=$localNameShort]"
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    override fun writeToParcel(parcel: Parcel, arg1: Int) {
        val b = Bundle()
        b.putString(LOCAL_NAME_COMPLETE, localNameComplete)
        b.putString(LOCAL_NAME_SHORT, localNameShort)
        b.putSparseParcelableArray(RECORDS_ARRAY, mAdRecords)
        parcel.writeBundle(b)
    }

    companion object {

        @SuppressLint("ParcelCreator")
        val CREATOR: Parcelable.Creator<AdRecordStore> = object : Parcelable.Creator<AdRecordStore> {
            override fun createFromParcel(`in`: Parcel): AdRecordStore {
                return AdRecordStore(`in`)
            }

            override fun newArray(size: Int): Array<AdRecordStore?> {
                return arrayOfNulls(size)
            }
        }
        private const val RECORDS_ARRAY = "records_array"
        private const val LOCAL_NAME_COMPLETE = "local_name_complete"
        private const val LOCAL_NAME_SHORT = "local_name_short"

        /**
         * As list.
         *
         * @param <C>         the generic type
         * @param sparseArray the sparse array
         * @return the collection
        </C> */
        fun <C> asList(sparseArray: SparseArray<C>?): Collection<C>? {
            if (sparseArray == null) return null
            val arrayList = ArrayList<C>(sparseArray.size())
            for (i in 0 until sparseArray.size()) {
                arrayList.add(sparseArray.valueAt(i))
            }
            return arrayList
        }
    }
}
