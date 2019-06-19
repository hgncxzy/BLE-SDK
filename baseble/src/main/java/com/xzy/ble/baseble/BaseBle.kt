package com.xzy.ble.baseble

import android.content.ContentValues.TAG

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.xzy.ble.baseble.callback.IConnectCallback
import com.xzy.ble.baseble.callback.scan.IScanCallback
import com.xzy.ble.baseble.callback.scan.ScanCallback
import com.xzy.ble.baseble.callback.scan.SingleFilterScanCallback
import com.xzy.ble.baseble.common.BleConfig
import com.xzy.ble.baseble.common.ConnectState
import com.xzy.ble.baseble.core.DeviceMirror
import com.xzy.ble.baseble.core.DeviceMirrorPool
import com.xzy.ble.baseble.exception.TimeoutException
import com.xzy.ble.baseble.model.BluetoothLeDevice
import com.xzy.ble.baseble.model.BluetoothLeDeviceStore

import java.util.Objects

/**
 *
 * BLE设备操作入口
 * *
 */
@Suppress("unused")
class BaseBle private constructor() {
    /**
     * 获取Context
     *
     * @return 返回Context
     */
    var context: Context? = null
        private set//上下文
    /**
     * 获取蓝牙管理
     *
     * @return 返回蓝牙管理
     */
    var bluetoothManager: BluetoothManager? = null
        private set//蓝牙管理
    /**
     * 获取蓝牙适配器
     *
     * @return 返回蓝牙适配器
     */
    var bluetoothAdapter: BluetoothAdapter? = null
        private set//蓝牙适配器
    /**
     * 获取设备镜像池
     *
     * @return  DeviceMirrorPool
     */
    var deviceMirrorPool: DeviceMirrorPool? = null
        private set//设备连接池
    private var lastDeviceMirror: DeviceMirror? = null//上次操作设备镜像

    /**
     * 获取当前连接失败重试次数
     *
     * @return int
     */
    val connectBleRetryCount: Int
        get() = if (lastDeviceMirror == null) {
            0
        } else lastDeviceMirror!!.connectBleRetryCount

    /**
     * 获取当前读取数据失败重试次数
     *
     * @return int
     */
    val readDataRetryCount: Int
        get() = if (lastDeviceMirror == null) {
            0
        } else lastDeviceMirror!!.readDataRetryCount

    /**
     * 获取当前使能数据失败重试次数
     *
     * @return int
     */
    val receiveDataRetryCount: Int
        get() = if (lastDeviceMirror == null) {
            0
        } else lastDeviceMirror!!.receiveDataRetryCount

    /**
     * 获取当前写入数据失败重试次数
     *
     * @return int
     */
    val writeDataRetryCount: Int
        get() = if (lastDeviceMirror == null) {
            0
        } else lastDeviceMirror!!.writeDataRetryCount

    /**
     * 初始化
     *
     * @param context 上下文
     */
    fun init(context: Context?) {
        if (this.context == null && context != null) {
            this.context = context.applicationContext
            bluetoothManager = this.context!!.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = Objects.requireNonNull<BluetoothManager>(bluetoothManager).adapter
            deviceMirrorPool = DeviceMirrorPool()
        }
    }

    /**
     * 开始扫描
     *
     * @param scanCallback 自定义回调
     */
    fun startScan(scanCallback: ScanCallback?) {
        if (scanCallback == null) {
            throw IllegalArgumentException("this ScanCallback is Null!")
        }
        scanCallback.setScan(true).scan()
    }

    /**
     * 停止扫描
     *
     * @param scanCallback 自定义回调
     */
    fun stopScan(scanCallback: ScanCallback?) {
        if (scanCallback == null) {
            throw IllegalArgumentException("this ScanCallback is Null!")
        }
        scanCallback.setScan(false).removeHandlerMsg().scan()
    }

    /**
     * 连接设备
     *
     * @param bluetoothLeDevice 蓝牙设备
     * @param connectCallback 连接回调函数
     */
    fun connect(bluetoothLeDevice: BluetoothLeDevice?, connectCallback: IConnectCallback?) {
        if (bluetoothLeDevice == null || connectCallback == null) {
            Log.e(TAG, "This bluetoothLeDevice or connectCallback is null.")
            return
        }
        if (deviceMirrorPool != null && !deviceMirrorPool!!.isContainDevice(bluetoothLeDevice)) {
            var deviceMirror = DeviceMirror(bluetoothLeDevice)
            if (lastDeviceMirror != null && !TextUtils.isEmpty(lastDeviceMirror!!.uniqueSymbol)
                    && lastDeviceMirror!!.uniqueSymbol == deviceMirror.uniqueSymbol) {
                deviceMirror = lastDeviceMirror as DeviceMirror//防止重复创建设备镜像
            }
            deviceMirror.connect(connectCallback)
            lastDeviceMirror = deviceMirror
        } else {
            Log.i(TAG, "This device is connected.")
        }
    }

    /**
     * 连接指定mac地址的设备
     *
     * @param mac             设备mac地址
     * @param connectCallback 连接回调
     */
    fun connectByMac(mac: String?, connectCallback: IConnectCallback?) {
        if (mac == null || connectCallback == null) {
            Log.e(TAG, "This mac or connectCallback is null.")
            return
        }
        startScan(SingleFilterScanCallback(object : IScanCallback {
            override fun onDeviceFound(bluetoothLeDevice: BluetoothLeDevice) {

            }

            override fun onScanFinish(bluetoothLeDeviceStore: BluetoothLeDeviceStore) {
                if (bluetoothLeDeviceStore.deviceList.isNotEmpty()) {
                    Handler(Looper.getMainLooper()).post { connect(bluetoothLeDeviceStore.deviceList[0], connectCallback) }
                } else {
                    connectCallback.onConnectFailure(TimeoutException())
                }
            }

            override fun onscanBleTimeout() {
                connectCallback.onConnectFailure(TimeoutException())
            }

        }).setDeviceMac(mac))
    }

    /**
     * 连接指定设备名称的设备
     *
     * @param name            设备名称
     * @param connectCallback 连接回调
     */
    fun connectByName(name: String?, connectCallback: IConnectCallback?) {
        if (name == null || connectCallback == null) {
            Log.e(TAG, "This name or connectCallback is null.")
            return
        }
        startScan(SingleFilterScanCallback(object : IScanCallback {
            override fun onDeviceFound(bluetoothLeDevice: BluetoothLeDevice) {

            }

            override fun onScanFinish(bluetoothLeDeviceStore: BluetoothLeDeviceStore) {
                if (bluetoothLeDeviceStore.deviceList.isNotEmpty()) {
                    Handler(Looper.getMainLooper()).post { connect(bluetoothLeDeviceStore.deviceList[0], connectCallback) }
                } else {
                    connectCallback.onConnectFailure(TimeoutException())
                }
            }

            override fun onscanBleTimeout() {
                connectCallback.onConnectFailure(TimeoutException())
            }

        }).setDeviceName(name))
    }

    /**
     * 获取连接池中的设备镜像，如果没有连接则返回空
     *
     * @param bluetoothLeDevice 蓝牙设备
     * @return DeviceMirror
     */
    fun getDeviceMirror(bluetoothLeDevice: BluetoothLeDevice): DeviceMirror? {
        return if (deviceMirrorPool != null) {
            deviceMirrorPool!!.getDeviceMirror(bluetoothLeDevice)
        } else null
    }

    /**
     * 获取该设备连接状态
     *
     * @param bluetoothLeDevice 蓝牙设备
     * @return ConnectState
     */
    fun getConnectState(bluetoothLeDevice: BluetoothLeDevice): ConnectState {
        return if (deviceMirrorPool != null) {
            deviceMirrorPool!!.getConnectState(bluetoothLeDevice)
        } else ConnectState.CONNECT_DISCONNECT
    }

    /**
     * 判断该设备是否已连接
     *
     * @param bluetoothLeDevice 当前蓝牙设备
     * @return boolean
     */
    fun isConnect(bluetoothLeDevice: BluetoothLeDevice): Boolean {
        return if (deviceMirrorPool != null) {
            deviceMirrorPool!!.isContainDevice(bluetoothLeDevice)
        } else false
    }

    /**
     * 断开某一个设备
     *
     * @param bluetoothLeDevice 需要断开的设备
     */
    fun disconnect(bluetoothLeDevice: BluetoothLeDevice) {
        if (deviceMirrorPool != null) {
            deviceMirrorPool!!.disconnect(bluetoothLeDevice)
        }
    }

    /**
     * 断开所有设备
     */
    fun disconnect() {
        if (deviceMirrorPool != null) {
            deviceMirrorPool!!.disconnect()
        }
    }

    /**
     * 清除资源，在退出应用时调用
     */
    fun clear() {
        if (deviceMirrorPool != null) {
            deviceMirrorPool!!.clear()
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: BaseBle? = null//入口操作管理
        private val bleConfig = BleConfig.getInstance()

        /**
         * 单例方式获取蓝牙通信入口
         *
         * @return 返回ViseBluetooth
         */
        fun getInstance(): BaseBle? {
            if (instance == null) {
                synchronized(BaseBle::class.java) {
                    if (instance == null) {
                        instance = BaseBle()
                    }
                }
            }
            return instance
        }

        /**
         * 获取配置对象，可进行相关配置的修改
         *
         * @return BleConfig
         */
        fun config(): BleConfig? {
            return bleConfig
        }
    }
}
