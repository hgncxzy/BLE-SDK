package com.xzy.ble.baseble.core

import com.xzy.ble.baseble.common.BleConfig
import com.xzy.ble.baseble.common.ConnectState
import com.xzy.ble.baseble.model.BluetoothLeDevice
import java.util.ArrayList
import java.util.Comparator


/**
 * 设备镜像池，用来管理多个设备连接后的操作
 */
@Suppress("unused")
class DeviceMirrorPool {
    private val mDeviceMirrorMap: LruHashMap<String, DeviceMirror>

    /**
     * 获取连接池设备镜像Map集合
     *
     * @return Map<String></String>, DeviceMirror>
     */
    val deviceMirrorMap: Map<String, DeviceMirror>
        get() = mDeviceMirrorMap

    /**
     * 获取连接池设备镜像List集合
     *
     * @return List<DeviceMirror>
    </DeviceMirror> */
    private val deviceMirrorList: List<DeviceMirror>
        @Synchronized get() {
            val deviceMirrors = ArrayList(mDeviceMirrorMap.values)
            deviceMirrors.sortWith(Comparator { lhs, rhs -> lhs.uniqueSymbol.compareTo(rhs.uniqueSymbol, ignoreCase = true) })
            return deviceMirrors
        }

    /**
     * 获取连接池设备详细信息List集合
     *
     * @return List<BluetoothLeDevice>
    </BluetoothLeDevice> */
    val deviceList: List<BluetoothLeDevice>
        @Synchronized get() {
            val deviceList = ArrayList<BluetoothLeDevice>()
            for (deviceMirror in deviceMirrorList) {
                deviceMirror.bluetoothLeDevice.let { deviceList.add(it!!) }
            }
            return deviceList
        }

    constructor() {
        mDeviceMirrorMap = LruHashMap(BleConfig.getInstance()!!.maxConnectBleCount)
    }

    constructor(deviceMirrorSize: Int) {
        mDeviceMirrorMap = LruHashMap(deviceMirrorSize)
    }

    /**
     * 添加设备镜像
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     */
    @Synchronized
    fun addDeviceMirror(bluetoothLeDevice: BluetoothLeDevice?) {
        if (bluetoothLeDevice == null) {
            return
        }
        val key = bluetoothLeDevice.address + bluetoothLeDevice.name
        if (!mDeviceMirrorMap.containsKey(key)) {
            mDeviceMirrorMap[key] = DeviceMirror(bluetoothLeDevice)
        }
    }

    /**
     * 添加设备镜像
     *
     * @param deviceMirror 设备镜像
     */
    @Synchronized
    fun addDeviceMirror(deviceMirror: DeviceMirror?) {
        if (deviceMirror == null) {
            return
        }
        if (!mDeviceMirrorMap.containsKey(deviceMirror.uniqueSymbol)) {
            mDeviceMirrorMap[deviceMirror.uniqueSymbol] = deviceMirror
        }
    }

    /**
     * 删除设备镜像
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     */
    @Synchronized
    fun removeDeviceMirror(bluetoothLeDevice: BluetoothLeDevice?) {
        if (bluetoothLeDevice == null) {
            return
        }
        val key = bluetoothLeDevice.address + bluetoothLeDevice.name
        mDeviceMirrorMap.remove(key)
    }

    /**
     * 删除设备镜像
     *
     * @param deviceMirror 设备镜像
     */
    @Synchronized
    fun removeDeviceMirror(deviceMirror: DeviceMirror?) {
        if (deviceMirror == null) {
            return
        }
        if (mDeviceMirrorMap.containsKey(deviceMirror.uniqueSymbol)) {
            deviceMirror.clear()
            mDeviceMirrorMap.remove(deviceMirror.uniqueSymbol)
        }
    }

    /**
     * 判断是否包含设备镜像
     *
     * @param deviceMirror 设备镜像
     * @return boolean
     */
    @Synchronized
    fun isContainDevice(deviceMirror: DeviceMirror?): Boolean {
        return deviceMirror != null && mDeviceMirrorMap.containsKey(deviceMirror.uniqueSymbol)
    }

    /**
     * 判断是否包含设备镜像
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     * @return boolean
     */
    @Synchronized
    fun isContainDevice(bluetoothLeDevice: BluetoothLeDevice?): Boolean {
        return bluetoothLeDevice != null && mDeviceMirrorMap.containsKey(bluetoothLeDevice.address + bluetoothLeDevice.name)
    }

    /**
     * 获取连接池中该设备镜像的连接状态，如果没有连接则返回CONNECT_DISCONNECT。
     *
     * @param bluetoothLeDevice  BluetoothLeDevice
     * @return ConnectState
     */
    @Synchronized
    fun getConnectState(bluetoothLeDevice: BluetoothLeDevice): ConnectState {
        val deviceMirror = getDeviceMirror(bluetoothLeDevice)
        return deviceMirror?.connectState ?: ConnectState.CONNECT_DISCONNECT
    }

    /**
     * 获取连接池中的设备镜像，如果没有连接则返回空
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     * @return DeviceMirror
     */
    @Synchronized
    fun getDeviceMirror(bluetoothLeDevice: BluetoothLeDevice?): DeviceMirror? {
        if (bluetoothLeDevice != null) {
            val key = bluetoothLeDevice.address + bluetoothLeDevice.name
            if (mDeviceMirrorMap.containsKey(key)) {
                return mDeviceMirrorMap[key]
            }
        }
        return null
    }

    /**
     * 断开连接池中某一个设备
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     */
    @Synchronized
    fun disconnect(bluetoothLeDevice: BluetoothLeDevice) {
        if (isContainDevice(bluetoothLeDevice)) {
            getDeviceMirror(bluetoothLeDevice)!!.disconnect()
        }
    }

    /**
     * 断开连接池中所有设备
     */
    @Synchronized
    fun disconnect() {
        for ((_, value) in mDeviceMirrorMap) {
            value.disconnect()
        }
        mDeviceMirrorMap.clear()
    }

    /**
     * 清除连接池
     */
    @Synchronized
    fun clear() {
        for ((_, value) in mDeviceMirrorMap) {
            value.clear()
        }
        mDeviceMirrorMap.clear()
    }

}
