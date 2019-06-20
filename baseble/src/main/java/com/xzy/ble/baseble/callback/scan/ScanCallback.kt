package com.xzy.ble.baseble.callback.scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.Looper
import com.xzy.ble.baseble.BaseBle
import com.xzy.ble.baseble.common.BleConfig
import com.xzy.ble.baseble.model.BluetoothLeDevice
import com.xzy.ble.baseble.model.BluetoothLeDeviceStore


/**
 * 扫描设备回调
 */
@Suppress("unused", "DEPRECATION")
open class ScanCallback(internal var scanCallback: IScanCallback?//扫描结果回调
) : BluetoothAdapter.LeScanCallback, IScanFilter {
    private val handler = Handler(Looper.myLooper())
    private var isScan = true//是否开始扫描
    var isScanning = false
        internal set//是否正在扫描
    internal var bluetoothLeDeviceStore: BluetoothLeDeviceStore//用来存储扫描到的设备

    init {
        if (scanCallback == null) {
            throw NullPointerException("this scanCallback is null!")
        }
        bluetoothLeDeviceStore = BluetoothLeDeviceStore()
    }

    fun setScan(scan: Boolean): ScanCallback {
        isScan = scan
        return this
    }

    fun scan() {
        if (isScan) {
            if (isScanning) {
                return
            }
            // 清除存储对象 Map<String,BluetoothDevice>
            bluetoothLeDeviceStore.clear()
            // 如果设置了超时时间，则到达超时时间后，停止扫描
            if (BleConfig.getInstance()!!.scanBleTimeout > 0) {
                handler.postDelayed({
                    isScanning = false
                    if (BaseBle.getInstance()?.bluetoothAdapter != null) {
                        BaseBle.getInstance()?.bluetoothAdapter!!.stopLeScan(this@ScanCallback)
                    }

                    if (bluetoothLeDeviceStore.deviceMap.isNotEmpty()) {
                        scanCallback!!.onScanFinish(bluetoothLeDeviceStore)
                    } else {
                        scanCallback!!.onscanBleTimeout()
                    }
                }, BleConfig.getInstance()!!.scanBleTimeout.toLong())
            } else if (BleConfig.getInstance()!!.scanBleRepeatInterval > 0) {
                //如果超时时间设置为一直扫描（即 <= 0）,则判断是否设置重复扫描间隔
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        isScanning = false
                        // 在执行下一次扫描之前，结束掉前一次的扫描
                        if (BaseBle.getInstance()?.bluetoothAdapter != null) {
                            BaseBle.getInstance()?.bluetoothAdapter!!.stopLeScan(this@ScanCallback)
                        }

                        if (bluetoothLeDeviceStore.deviceMap.isNotEmpty()) {
                            scanCallback!!.onScanFinish(bluetoothLeDeviceStore)
                        } else {
                            scanCallback!!.onscanBleTimeout()
                        }
                        isScanning = true
                        if (BaseBle.getInstance()?.bluetoothAdapter != null) {
                            BaseBle.getInstance()?.bluetoothAdapter!!.startLeScan(this@ScanCallback)
                        }
                        handler.postDelayed(this, BleConfig.getInstance()!!.scanBleRepeatInterval.toLong())
                    }
                }, BleConfig.getInstance()!!.scanBleRepeatInterval.toLong())
            }
            isScanning = true
            if (BaseBle.getInstance()?.bluetoothAdapter != null) {
                BaseBle.getInstance()?.bluetoothAdapter!!.startLeScan(this@ScanCallback)
            }
        } else {
            isScanning = false
            if (BaseBle.getInstance()?.bluetoothAdapter != null) {
                BaseBle.getInstance()?.bluetoothAdapter!!.stopLeScan(this@ScanCallback)
            }
        }
    }

    fun removeHandlerMsg(): ScanCallback {
        handler.removeCallbacksAndMessages(null)
        bluetoothLeDeviceStore.clear()
        return this
    }

    override fun onLeScan(bluetoothDevice: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
        val bluetoothLeDevice = BluetoothLeDevice(bluetoothDevice, rssi, scanRecord, System.currentTimeMillis())
        val filterDevice = onFilter(bluetoothLeDevice)
        if (filterDevice != null) {
            bluetoothLeDeviceStore.addDevice(filterDevice)
            scanCallback!!.onDeviceFound(filterDevice)
        }
    }

    override fun onFilter(bluetoothLeDevice: BluetoothLeDevice): BluetoothLeDevice? {
        return bluetoothLeDevice
    }

}
