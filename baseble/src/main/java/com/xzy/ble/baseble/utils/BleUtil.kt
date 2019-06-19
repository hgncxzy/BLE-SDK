package com.xzy.ble.baseble.utils

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

import java.util.Objects

/**
 * 蓝牙基础操作工具类
 */
@Suppress("unused")
object BleUtil {
    fun enableBluetooth(activity: Activity, requestCode: Int) {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(intent, requestCode)
    }

    fun isSupportBle(context: Context?): Boolean {
        if (context == null || !context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false
        }
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return Objects.requireNonNull(manager).adapter != null
    }

    fun isBleEnable(context: Context): Boolean {
        if (!isSupportBle(context)) {
            return false
        }
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter.isEnabled
    }
}
