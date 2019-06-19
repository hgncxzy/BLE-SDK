package com.xzy.ble.baseble.core
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.xzy.ble.baseble.BaseBle
import com.xzy.ble.baseble.callback.IBleCallback
import com.xzy.ble.baseble.callback.IConnectCallback
import com.xzy.ble.baseble.callback.IRssiCallback
import com.xzy.ble.baseble.common.BleConfig
import com.xzy.ble.baseble.common.BleConstant
import com.xzy.ble.baseble.common.BleConstant.MSG_CONNECT_RETRY
import com.xzy.ble.baseble.common.BleConstant.MSG_CONNECT_TIMEOUT
import com.xzy.ble.baseble.common.BleConstant.MSG_READ_DATA_RETRY
import com.xzy.ble.baseble.common.BleConstant.MSG_READ_DATA_TIMEOUT
import com.xzy.ble.baseble.common.BleConstant.MSG_RECEIVE_DATA_RETRY
import com.xzy.ble.baseble.common.BleConstant.MSG_RECEIVE_DATA_TIMEOUT
import com.xzy.ble.baseble.common.BleConstant.MSG_WRITE_DATA_RETRY
import com.xzy.ble.baseble.common.BleConstant.MSG_WRITE_DATA_TIMEOUT
import com.xzy.ble.baseble.common.ConnectState
import com.xzy.ble.baseble.common.PropertyType
import com.xzy.ble.baseble.exception.BleException
import com.xzy.ble.baseble.exception.ConnectException
import com.xzy.ble.baseble.exception.GattException
import com.xzy.ble.baseble.exception.TimeoutException
import com.xzy.ble.baseble.model.BluetoothLeDevice
import com.xzy.ble.baseble.utils.HexUtil

import java.util.HashMap
import java.util.UUID


/**
 * 设备镜像（设备连接成功后返回的设备信息模型）
 */
@Suppress("unused")
class DeviceMirror(
        /**
         * 获取设备详细信息
         *
         * @return BluetoothLeDevice
         */
        val bluetoothLeDevice: BluetoothLeDevice?//设备基础信息
) {
    private var deviceMirror: DeviceMirror = this
    /**
     * 获取设备唯一标识
     *
     * @return String
     */
    var uniqueSymbol: String = ""//唯一符号

    /**
     * 获取蓝牙GATT
     *
     * @return 返回蓝牙GATT
     */
    var bluetoothGatt: BluetoothGatt? = null
        private set//蓝牙GATT
    private var rssiCallback: IRssiCallback? = null//获取信号值回调
    private var connectCallback: IConnectCallback? = null//连接回调
    /**
     * 获取当前连接失败重试次数
     *
     * @return int
     */
    var connectBleRetryCount = 0
        private set//当前连接重试次数
    /**
     * 获取当前写入数据失败重试次数
     *
     * @return int
     */
    var writeDataRetryCount = 0
        private set//当前写入数据重试次数
    /**
     * 获取当前读取数据失败重试次数
     *
     * @return int
     */
    var readDataRetryCount = 0
        private set//当前读取数据重试次数
    /**
     * 获取当前使能数据失败重试次数
     *
     * @return int
     */
    var receiveDataRetryCount = 0
        private set//当前接收数据重试次数
    private var isActiveDisconnect = false//是否主动断开连接
    private var isIndication: Boolean = false//是否是指示器方式
    private var enable: Boolean = false//是否设置使能
    private var writeData: ByteArray? = null//写入数据
    /**
     * 获取设备连接状态
     *
     * @return 返回设备连接状态
     */
    var connectState = ConnectState.CONNECT_INIT
        private set//设备状态描述
    @Volatile
    private var writeInfoMap = HashMap<String, BluetoothGattChannel>()//写入数据GATT信息集合
    @Volatile
    private var readInfoMap = HashMap<String, BluetoothGattChannel>()//读取数据GATT信息集合
    @Volatile
    private var enableInfoMap = HashMap<String, BluetoothGattChannel>()//设置使能GATT信息集合
    @Volatile
    private var bleCallbackMap = HashMap<String, IBleCallback>()//数据操作回调集合
    @Volatile
    private var receiveCallbackMap = HashMap<String, IBleCallback>()//数据接收回调集合

    private var isSendSuccess = false
    private val handler = object : Handler(Looper.myLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == MSG_CONNECT_TIMEOUT) {
                connectFailure(TimeoutException())
            } else if (msg.what == MSG_CONNECT_RETRY) {
                connect()
            } else if (msg.what == MSG_WRITE_DATA_TIMEOUT) {
                writeFailure(TimeoutException(), true)
            } else if (msg.what == MSG_WRITE_DATA_RETRY) {
                if (!isSendSuccess) {
                    write(writeData)
                }
            } else if (msg.what == MSG_READ_DATA_TIMEOUT) {
                readFailure(TimeoutException(), true)
            } else if (msg.what == MSG_READ_DATA_RETRY) {
                read()
            } else if (msg.what == MSG_RECEIVE_DATA_TIMEOUT) {
                enableFailure(TimeoutException(), true)
            } else if (msg.what == MSG_RECEIVE_DATA_RETRY) {
                enable(enable, isIndication)
            }
        }
    }

    /**
     * 蓝牙所有相关操作的核心回调类
     */
    private val coreGattCallback = object : BluetoothGattCallback() {

        /**
         * 连接状态改变，主要用来分析设备的连接与断开
         * @param gatt GATT
         * @param status 改变前状态
         * @param newState 改变后状态
         */
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.i(BleConfig.TAG, "onConnectionStateChange  status: " + status + " ,newState: " + newState +
                    "  ,thread: " + Thread.currentThread())
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                gatt.discoverServices()
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                close()
                if (connectCallback != null) {
                    handler.removeCallbacksAndMessages(null)
                    BaseBle.getInstance()!!.deviceMirrorPool!!.removeDeviceMirror(deviceMirror)
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        connectState = ConnectState.CONNECT_DISCONNECT
                        connectCallback!!.onDisconnect(isActiveDisconnect)
                    } else {
                        connectState = ConnectState.CONNECT_FAILURE
                        connectCallback!!.onConnectFailure(ConnectException(gatt, status))
                    }
                }
            } else if (newState == BluetoothGatt.STATE_CONNECTING) {
                connectState = ConnectState.CONNECT_PROCESS
            }
        }

        /**
         * 发现服务，主要用来获取设备支持的服务列表
         * @param gatt GATT
         * @param status 当前状态
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.i(BleConfig.TAG, "onServicesDiscovered  status: " + status + "  ,thread: " + Thread.currentThread())
            handler.removeMessages(MSG_CONNECT_TIMEOUT)
            if (status == 0) {
                Log.i(BleConfig.TAG, "onServicesDiscovered connectSuccess.")
                bluetoothGatt = gatt
                connectState = ConnectState.CONNECT_SUCCESS
                if (connectCallback != null) {
                    isActiveDisconnect = false
                    BaseBle.getInstance()?.deviceMirrorPool!!.addDeviceMirror(deviceMirror)
                    connectCallback!!.onConnectSuccess(deviceMirror)
                }
            } else {
                connectFailure(ConnectException(gatt, status))
            }
        }

        /**
         * 读取特征值，主要用来读取该特征值包含的可读信息
         * @param gatt GATT
         * @param characteristic 特征值
         * @param status 当前状态
         */
        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            Log.i(BleConfig.TAG, "onCharacteristicRead  status: " + status + ", data:" + HexUtil.encodeHexStr(characteristic.value) +
                    "  ,thread: " + Thread.currentThread())
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handleSuccessData(readInfoMap, characteristic.value, true)
            } else {
                readFailure(GattException(status), true)
            }
        }

        /**
         * 写入特征值，主要用来发送数据到设备
         * @param gatt GATT
         * @param characteristic 特征值
         * @param status 当前状态
         */
        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            Log.i(BleConfig.TAG, "onCharacteristicWrite  status: " + status + ", data:" + HexUtil.encodeHexStr(characteristic.value) +
                    "  ,thread: " + Thread.currentThread())
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handleSuccessData(writeInfoMap, characteristic.value, false)
            } else {
                writeFailure(GattException(status), true)
            }
        }

        /**
         * 特征值改变，主要用来接收设备返回的数据信息
         * @param gatt GATT
         * @param characteristic 特征值
         */
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            Log.i(BleConfig.TAG, "onCharacteristicChanged data:" + HexUtil.encodeHexStr(characteristic.value) +
                    "  ,thread: " + Thread.currentThread())
            for ((receiveKey, receiveValue) in receiveCallbackMap) {
                for ((bluetoothGattInfoKey, bluetoothGattInfoValue) in enableInfoMap) {
                    if (receiveKey == bluetoothGattInfoKey) {
                        bluetoothLeDevice?.let { receiveValue.onSuccess(characteristic.value, bluetoothGattInfoValue, it) }
                    }
                }
            }
        }

        /**
         * 读取属性描述值，主要用来获取设备当前属性描述的值
         * @param gatt GATT
         * @param descriptor 属性描述
         * @param status 当前状态
         */
        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            Log.i(BleConfig.TAG, "onDescriptorRead  status: " + status + ", data:" + HexUtil.encodeHexStr(descriptor.value) +
                    "  ,thread: " + Thread.currentThread())
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handleSuccessData(readInfoMap, descriptor.value, true)
            } else {
                readFailure(GattException(status), true)
            }
        }

        /**
         * 写入属性描述值，主要用来根据当前属性描述值写入数据到设备
         * @param gatt GATT
         * @param descriptor 属性描述值
         * @param status 当前状态
         */
        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            Log.i(BleConfig.TAG, "onDescriptorWrite  status: " + status + ", data:" + HexUtil.encodeHexStr(descriptor.value) +
                    "  ,thread: " + Thread.currentThread())
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handleSuccessData(writeInfoMap, descriptor.value, false)
            } else {
                writeFailure(GattException(status), true)
            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handleSuccessData(enableInfoMap, descriptor.value, false)
            } else {
                enableFailure(GattException(status), true)
            }
        }

        /**
         * 阅读设备信号值
         * @param gatt GATT
         * @param rssi 设备当前信号
         * @param status 当前状态
         */
        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            Log.i(BleConfig.TAG, "onReadRemoteRssi  status: " + status + ", rssi:" + rssi +
                    "  ,thread: " + Thread.currentThread())
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (rssiCallback != null) {
                    rssiCallback!!.onSuccess(rssi)
                }
            } else {
                if (rssiCallback != null) {
                    rssiCallback!!.onFailure(GattException(status))
                }
            }
        }
    }

    /**
     * 获取服务列表
     *
     * @return List<BluetoothGattService>
    </BluetoothGattService> */
    val gattServiceList: List<BluetoothGattService>?
        get() = if (bluetoothGatt != null) {
            bluetoothGatt!!.services
        } else null

    /**
     * 设备是否连接
     *
     * @return boolean
     */
    val isConnected: Boolean
        get() = connectState == ConnectState.CONNECT_SUCCESS

    init {
        if (bluetoothLeDevice != null) {
            this.uniqueSymbol = bluetoothLeDevice.address + bluetoothLeDevice.name
        }
    }

    /**
     * 连接设备
     *
     * @param connectCallback 连接回调
     */
    @Synchronized
    fun connect(connectCallback: IConnectCallback) {
        if (connectState == ConnectState.CONNECT_SUCCESS || connectState == ConnectState.CONNECT_PROCESS
                || connectState == ConnectState.CONNECT_INIT && connectBleRetryCount != 0) {
            Log.e(BleConfig.TAG, "this connect state is connecting, connectSuccess or current retry count less than config connect retry count.")
            return
        }
        handler.removeCallbacksAndMessages(null)
        this.connectCallback = connectCallback
        connectBleRetryCount = 0
        connect()
    }

    /**
     * 绑定一个具备读写或可通知能力的通道，设置需要操作数据的相关信息，包含：数据操作回调，数据操作类型，数据通道建立所需的UUID。
     *
     * @param bleCallback 回调
     * @param bluetoothGattChannel BluetoothGattChannel
     */
    @Synchronized
    fun bindChannel(bleCallback: IBleCallback?, bluetoothGattChannel: BluetoothGattChannel?) {
        if (bleCallback != null && bluetoothGattChannel != null) {
            val key = bluetoothGattChannel.gattInfoKey
            val propertyType = bluetoothGattChannel.propertyType
            if (!bleCallbackMap.containsKey(key)) {
                bleCallbackMap[key] = bleCallback
            }
            if (propertyType == PropertyType.PROPERTY_READ) {
                if (!readInfoMap.containsKey(key)) {
                    readInfoMap[key] = bluetoothGattChannel
                }
            } else if (propertyType == PropertyType.PROPERTY_WRITE) {
                if (!writeInfoMap.containsKey(key)) {
                    writeInfoMap[key] = bluetoothGattChannel
                }
            } else if (propertyType == PropertyType.PROPERTY_NOTIFY) {
                if (!enableInfoMap.containsKey(key)) {
                    enableInfoMap[key] = bluetoothGattChannel
                }
            } else if (propertyType == PropertyType.PROPERTY_INDICATE) {
                if (!enableInfoMap.containsKey(key)) {
                    enableInfoMap[key] = bluetoothGattChannel
                }
            }
        }
    }

    /**
     * 解绑通道
     *
     * @param bluetoothGattChannel  BluetoothGattChannel
     */
    @Synchronized
    fun unbindChannel(bluetoothGattChannel: BluetoothGattChannel?) {
        if (bluetoothGattChannel != null) {
            val key = bluetoothGattChannel.gattInfoKey
            if (bleCallbackMap.containsKey(key)) {
                bleCallbackMap.remove(key)
            }
            when {
                readInfoMap.containsKey(key) -> readInfoMap.remove(key)
                writeInfoMap.containsKey(key) -> writeInfoMap.remove(key)
                enableInfoMap.containsKey(key) -> enableInfoMap.remove(key)
            }
        }
    }

    /**
     * 写入数据
     *
     * @param data 字节数组
     */
    fun writeData(data: ByteArray?) {
        if (data == null || data.size > 20) {
            Log.e(BleConfig.TAG, "this data is null or length beyond 20 byte.")
            return
        }
        if (!checkBluetoothGattInfo(writeInfoMap)) {
            return
        }
        handler.removeMessages(MSG_WRITE_DATA_TIMEOUT)
        handler.removeMessages(MSG_WRITE_DATA_RETRY)
        writeDataRetryCount = 0
        writeData = data
        isSendSuccess = write(data)
    }

    /**
     * 读取数据
     */
    fun readData() {
        if (!checkBluetoothGattInfo(readInfoMap)) {
            return
        }
        handler.removeMessages(MSG_READ_DATA_TIMEOUT)
        handler.removeMessages(MSG_READ_DATA_RETRY)
        readDataRetryCount = 0
        read()
    }

    /**
     * 获取设备信号值
     *
     * @param rssiCallback rssi 回调
     */
    fun readRemoteRssi(rssiCallback: IRssiCallback) {
        this.rssiCallback = rssiCallback
        if (bluetoothGatt != null) {
            bluetoothGatt!!.readRemoteRssi()
        }
    }

    /**
     * 注册获取数据通知
     *
     * @param isIndication isIndication
     */
    fun registerNotify(isIndication: Boolean) {
        if (!checkBluetoothGattInfo(enableInfoMap)) {
            return
        }
        handler.removeMessages(MSG_RECEIVE_DATA_TIMEOUT)
        handler.removeMessages(MSG_RECEIVE_DATA_RETRY)
        receiveDataRetryCount = 0
        enable = true
        this.isIndication = isIndication
        enable(enable, this.isIndication)
    }

    /**
     * 取消获取数据通知
     *
     * @param isIndication isIndication
     */
    fun unregisterNotify(isIndication: Boolean) {
        if (!checkBluetoothGattInfo(enableInfoMap)) {
            return
        }
        handler.removeMessages(MSG_RECEIVE_DATA_TIMEOUT)
        handler.removeMessages(MSG_RECEIVE_DATA_RETRY)
        enable = false
        this.isIndication = isIndication
        enable(enable, this.isIndication)
    }

    /**
     * 设置接收数据监听
     *
     * @param key             接收数据回调key，由serviceUUID+characteristicUUID+descriptorUUID组成
     * @param receiveCallback 接收数据回调
     */
    fun setNotifyListener(key: String, receiveCallback: IBleCallback) {
        receiveCallbackMap[key] = receiveCallback
    }

    /**
     * 根据服务UUID获取指定服务
     *
     * @param serviceUuid 服务 uuid
     * @return BluetoothGattService
     */
    private fun getGattService(serviceUuid: UUID?): BluetoothGattService? {
        return if (bluetoothGatt != null && serviceUuid != null) {
            bluetoothGatt!!.getService(serviceUuid)
        } else null
    }

    /**
     * 获取某个服务的特征值列表
     *
     * @param serviceUuid 服务 uuid
     * @return List<BluetoothGattCharacteristic>
    </BluetoothGattCharacteristic> */
    fun getGattCharacteristicList(serviceUuid: UUID?): List<BluetoothGattCharacteristic>? {
        return if (getGattService(serviceUuid) != null && serviceUuid != null) {
            getGattService(serviceUuid)!!.characteristics
        } else null
    }

    /**
     * 根据特征值UUID获取某个服务的指定特征值
     *
     * @param serviceUuid 服务 uuid
     * @param characteristicUuid 特征 uuid
     * @return BluetoothGattCharacteristic
     */
    private fun getGattCharacteristic(serviceUuid: UUID?, characteristicUuid: UUID?): BluetoothGattCharacteristic? {
        return if (getGattService(serviceUuid) != null && serviceUuid != null && characteristicUuid != null) {
            getGattService(serviceUuid)!!.getCharacteristic(characteristicUuid)
        } else null
    }

    /**
     * 获取某个特征值的描述属性列表
     *
     * @param serviceUuid 服务 uuid
     * @param characteristicUuid 特征码 uuid
     * @return List<BluetoothGattDescriptor>
    </BluetoothGattDescriptor> */
    fun getGattDescriptorList(serviceUuid: UUID?, characteristicUuid: UUID?): List<BluetoothGattDescriptor>? {
        return if (getGattCharacteristic(serviceUuid, characteristicUuid) != null && serviceUuid != null && characteristicUuid != null) {
            getGattCharacteristic(serviceUuid, characteristicUuid)!!.descriptors
        } else null
    }

    /**
     * 根据描述属性UUID获取某个特征值的指定属性值
     *
     * @param serviceUuid 服务 uuid
     * @param characteristicUuid 特征码 uuid
     * @param descriptorUuid 描述 uuid
     * @return BluetoothGattDescriptor
     */
    fun getGattDescriptor(serviceUuid: UUID?, characteristicUuid: UUID?, descriptorUuid: UUID?): BluetoothGattDescriptor? {
        return if (getGattCharacteristic(serviceUuid, characteristicUuid) != null && serviceUuid != null && characteristicUuid != null && descriptorUuid != null) {
            getGattCharacteristic(serviceUuid, characteristicUuid)!!.getDescriptor(descriptorUuid)
        } else null
    }

    /**
     * 移除数据操作回调
     *
     * @param key 名称
     */
    @Synchronized
    fun removeBleCallback(key: String) {
        bleCallbackMap.remove(key)
    }

    /**
     * 移除接收数据回调
     *
     * @param key 名称
     */
    @Synchronized
    fun removeReceiveCallback(key: String) {
        receiveCallbackMap.remove(key)
    }

    /**
     * 移除所有回调
     */
    @Synchronized
    fun removeAllCallback() {
        bleCallbackMap.clear()
        receiveCallbackMap.clear()
    }

    /**
     * 刷新设备缓存
     *
     * @return 返回是否刷新成功
     */
    @Synchronized
    fun refreshDeviceCache(): Boolean {
        try {
            val refresh = BluetoothGatt::class.java.getMethod("refresh")
            if (bluetoothGatt != null) {
                val success = refresh.invoke(bluetoothGatt) as Boolean
                Log.i(BleConfig.TAG, "Refreshing result: $success")
                return success
            }
        } catch (e: Exception) {
            Log.e(BleConfig.TAG, "An exception occured while refreshing device$e")
        }

        return false
    }

    /**
     * 主动断开设备连接
     */
    @Synchronized
    fun disconnect() {
        connectState = ConnectState.CONNECT_INIT
        connectBleRetryCount = 0
        if (bluetoothGatt != null) {
            isActiveDisconnect = true
            bluetoothGatt!!.disconnect()
        }
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * 关闭GATT
     */
    @Synchronized
    fun close() {
        if (bluetoothGatt != null) {
            bluetoothGatt!!.close()
        }
    }

    override fun toString(): String {
        return "DeviceMirror{" +
                "bluetoothLeDevice=" + bluetoothLeDevice +
                ", uniqueSymbol='" + uniqueSymbol + '\''.toString() +
                '}'.toString()
    }

    /**
     * 清除设备资源，在不使用该设备时调用
     */
    @Synchronized
    fun clear() {
        Log.i(BleConfig.TAG, "deviceMirror clear.")
        disconnect()
        refreshDeviceCache()
        close()
        bleCallbackMap.clear()
        receiveCallbackMap.clear()
        writeInfoMap.clear()
        readInfoMap.clear()
        enableInfoMap.clear()
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * UUID转换
     *
     * @param uuid uuid
     * @return 返回UUID
     */
    private fun formUUID(uuid: String?): UUID? {
        return if (uuid == null) null else UUID.fromString(uuid)
    }

    /**
     * 检查BluetoothGattChannel集合是否有值
     *
     * @param bluetoothGattInfoHashMap HashMap
     * @return boolean
     */
    private fun checkBluetoothGattInfo(bluetoothGattInfoHashMap: HashMap<String, BluetoothGattChannel>?): Boolean {
        if (bluetoothGattInfoHashMap == null || bluetoothGattInfoHashMap.size == 0) {
            Log.e(BleConfig.TAG, "this bluetoothGattInfo map is not value.")
            return false
        }
        return true
    }

    /**
     * 连接设备
     */
    @Synchronized
    private fun connect() {
        handler.removeMessages(MSG_CONNECT_TIMEOUT)
        BleConfig.getInstance()?.connectBleTimeout?.toLong()?.let { handler.sendEmptyMessageDelayed(MSG_CONNECT_TIMEOUT, it) }
        connectState = ConnectState.CONNECT_PROCESS
        if (bluetoothLeDevice?.device != null) {
            bluetoothLeDevice.device.connectGatt(BaseBle.getInstance()?.context, false, coreGattCallback)
        }
    }

    /**
     * 设置使能
     *
     * @param enable       是否具备使能
     * @param isIndication 是否是指示器方式
     * @return boolean
     */
    @Synchronized
    private fun enable(enable: Boolean, isIndication: Boolean): Boolean {
        handler.removeMessages(MSG_RECEIVE_DATA_TIMEOUT)
        handler.sendEmptyMessageDelayed(MSG_RECEIVE_DATA_TIMEOUT, BleConfig.getInstance()!!.operateBleTimeout.toLong())
        var success = false
        for ((_, bluetoothGattInfoValue) in enableInfoMap) {
            if (bluetoothGatt != null && bluetoothGattInfoValue.characteristic != null) {
                success = bluetoothGatt!!.setCharacteristicNotification(bluetoothGattInfoValue.characteristic, enable)
            }
            var bluetoothGattDescriptor: BluetoothGattDescriptor? = null
            if (bluetoothGattInfoValue.characteristic != null && bluetoothGattInfoValue.descriptor != null) {
                bluetoothGattDescriptor = bluetoothGattInfoValue.descriptor
            } else if (bluetoothGattInfoValue.characteristic != null && bluetoothGattInfoValue.descriptor == null) {
                bluetoothGattDescriptor = if (bluetoothGattInfoValue.characteristic!!.descriptors != null && bluetoothGattInfoValue.characteristic!!.descriptors.size == 1) {
                    bluetoothGattInfoValue.characteristic!!.descriptors[0]
                } else {
                    bluetoothGattInfoValue.characteristic!!
                            .getDescriptor(UUID.fromString(BleConstant.CLIENT_CHARACTERISTIC_CONFIG))
                }
            }
            if (bluetoothGattDescriptor != null) {
                bluetoothGattInfoValue.descriptor = bluetoothGattDescriptor
                if (isIndication) {
                    if (enable) {
                        bluetoothGattDescriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                    } else {
                        bluetoothGattDescriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    }
                } else {
                    if (enable) {
                        bluetoothGattDescriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    } else {
                        bluetoothGattDescriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    }
                }
                if (bluetoothGatt != null) {
                    bluetoothGatt!!.writeDescriptor(bluetoothGattDescriptor)
                }
            }
        }
        return success
    }

    /**
     * 读取数据
     *
     * @return boolean
     */
    @Synchronized
    private fun read(): Boolean {
        handler.removeMessages(MSG_READ_DATA_TIMEOUT)
        BleConfig.getInstance()?.operateBleTimeout?.toLong()?.let { handler.sendEmptyMessageDelayed(MSG_READ_DATA_TIMEOUT, it) }
        var success = false
        for ((_, bluetoothGattInfoValue) in readInfoMap) {
            if (bluetoothGatt != null && bluetoothGattInfoValue.characteristic != null && bluetoothGattInfoValue.descriptor != null) {
                success = bluetoothGatt!!.readDescriptor(bluetoothGattInfoValue.descriptor)
            } else if (bluetoothGatt != null && bluetoothGattInfoValue.characteristic != null && bluetoothGattInfoValue.descriptor == null) {
                success = bluetoothGatt!!.readCharacteristic(bluetoothGattInfoValue.characteristic)
            }
        }
        return success
    }

    /**
     * 写入数据
     *
     * @param data 字节数组
     * @return  boolean
     */
    @Synchronized
    private fun write(data: ByteArray?): Boolean {
        handler.removeMessages(MSG_WRITE_DATA_TIMEOUT)
        handler.sendEmptyMessageDelayed(MSG_WRITE_DATA_TIMEOUT, BleConfig.getInstance()!!.operateBleTimeout.toLong())
        var success = false
        for ((_, bluetoothGattInfoValue) in writeInfoMap) {
            if (bluetoothGatt != null && bluetoothGattInfoValue.characteristic != null && bluetoothGattInfoValue.descriptor != null) {
                bluetoothGattInfoValue.descriptor!!.value = data
                success = bluetoothGatt!!.writeDescriptor(bluetoothGattInfoValue.descriptor)
            } else if (bluetoothGatt != null && bluetoothGattInfoValue.characteristic != null && bluetoothGattInfoValue.descriptor == null) {
                bluetoothGattInfoValue.characteristic!!.value = data
                success = bluetoothGatt!!.writeCharacteristic(bluetoothGattInfoValue.characteristic)
            }
        }
        return success
    }

    /**
     * 连接失败处理
     *
     * @param bleException 回调异常
     */
    private fun connectFailure(bleException: BleException) {
        if (connectBleRetryCount < BleConfig.getInstance()!!.connectBleRetryCount) {
            connectBleRetryCount++
            handler.removeMessages(MSG_CONNECT_TIMEOUT)
            BleConfig.getInstance()?.connectBleRetryInterval?.toLong()?.let { handler.sendEmptyMessageDelayed(MSG_CONNECT_RETRY, it) }
            Log.i(BleConfig.TAG, "connectFailure connectBleRetryCount is $connectBleRetryCount")
        } else {
            connectState = if (bleException is TimeoutException) {
                ConnectState.CONNECT_TIMEOUT
            } else {
                ConnectState.CONNECT_FAILURE
            }
            close()
            if (connectCallback != null) {
                connectCallback!!.onConnectFailure(bleException)
            }
            Log.i(BleConfig.TAG, "connectFailure $bleException")
        }
    }

    /**
     * 使能失败
     *
     * @param bleException 异常
     * @param isRemoveCall 是否删除回调
     */
    private fun enableFailure(bleException: BleException, isRemoveCall: Boolean) {
        if (receiveDataRetryCount < BleConfig.getInstance()!!.operateBleRetryCount) {
            receiveDataRetryCount++
            handler.removeMessages(MSG_RECEIVE_DATA_TIMEOUT)
            handler.sendEmptyMessageDelayed(MSG_RECEIVE_DATA_RETRY, BleConfig.getInstance()!!.operateBleRetryInterval.toLong())
            Log.i(BleConfig.TAG, "enableFailure receiveDataRetryCount is $receiveDataRetryCount")
        } else {
            handleFailureData(enableInfoMap, bleException, isRemoveCall)
            Log.i(BleConfig.TAG, "enableFailure $bleException")
        }
    }

    /**
     * 读取数据失败
     *
     * @param bleException 异常
     * @param isRemoveCall 是否移除调用
     */
    private fun readFailure(bleException: BleException, isRemoveCall: Boolean) {
        if (readDataRetryCount < BleConfig.getInstance()!!.operateBleRetryCount) {
            readDataRetryCount++
            handler.removeMessages(MSG_READ_DATA_TIMEOUT)
            handler.sendEmptyMessageDelayed(MSG_READ_DATA_RETRY, BleConfig.getInstance()!!.operateBleRetryInterval.toLong())
            Log.i(BleConfig.TAG, "readFailure readDataRetryCount is $readDataRetryCount")
        } else {
            handleFailureData(readInfoMap, bleException, isRemoveCall)
            Log.i(BleConfig.TAG, "readFailure $bleException")
        }
    }

    /**
     * 写入数据失败
     *
     * @param bleException 异常
     * @param isRemoveCall 是否移除调用
     */
    private fun writeFailure(bleException: BleException, isRemoveCall: Boolean) {
        if (writeDataRetryCount < BleConfig.getInstance()!!.operateBleRetryCount) {
            writeDataRetryCount++
            handler.removeMessages(MSG_WRITE_DATA_TIMEOUT)
            handler.sendEmptyMessageDelayed(MSG_WRITE_DATA_RETRY, BleConfig.getInstance()!!.operateBleRetryInterval.toLong())
            Log.i(BleConfig.TAG, "writeFailure writeDataRetryCount is $writeDataRetryCount")
        } else {
            handleFailureData(writeInfoMap, bleException, isRemoveCall)
            Log.i(BleConfig.TAG, "writeFailure $bleException")
        }
    }

    /**
     * 处理数据发送成功
     *
     * @param bluetoothGattInfoHashMap HashMap
     * @param value                    待发送数据
     * @param isRemoveCall             是否需要移除回调
     */
    @Synchronized
    private fun handleSuccessData(bluetoothGattInfoHashMap: HashMap<String, BluetoothGattChannel>, value: ByteArray, isRemoveCall: Boolean) {
        handler.removeCallbacksAndMessages(null)
        var removeBleCallbackKey: String? = null
        var removeBluetoothGattInfoKey: String? = null
        for ((bleCallbackKey, bleCallbackValue) in bleCallbackMap) {
            for ((bluetoothGattInfoKey, bluetoothGattInfoValue) in bluetoothGattInfoHashMap) {
                if (bleCallbackKey == bluetoothGattInfoKey) {
                    if (bluetoothLeDevice != null) {
                        bleCallbackValue.onSuccess(value, bluetoothGattInfoValue, bluetoothLeDevice)
                    }
                    removeBleCallbackKey = bleCallbackKey
                    removeBluetoothGattInfoKey = bluetoothGattInfoKey
                }
            }
        }
        synchronized(bleCallbackMap) {
            if (isRemoveCall && removeBleCallbackKey != null) {
                bleCallbackMap.remove(removeBleCallbackKey)
                bluetoothGattInfoHashMap.remove(removeBluetoothGattInfoKey)
            }
        }
    }

    /**
     * 处理数据发送失败
     *
     * @param bluetoothGattInfoHashMap HashMap
     * @param bleException             回调异常
     * @param isRemoveCall             是否需要移除回调
     */
    @Synchronized
    private fun handleFailureData(bluetoothGattInfoHashMap: HashMap<String, BluetoothGattChannel>, bleException: BleException,
                                  isRemoveCall: Boolean) {
        handler.removeCallbacksAndMessages(null)
        var removeBleCallbackKey: String? = null
        var removeBluetoothGattInfoKey: String? = null
        for ((bleCallbackKey, bleCallbackValue) in bleCallbackMap) {
            for ((bluetoothGattInfoKey) in bluetoothGattInfoHashMap) {
                if (bleCallbackKey == bluetoothGattInfoKey) {
                    bleCallbackValue.onFailure(bleException)
                    removeBleCallbackKey = bleCallbackKey
                    removeBluetoothGattInfoKey = bluetoothGattInfoKey
                }
            }
        }
        synchronized(bleCallbackMap) {
            if (isRemoveCall && removeBleCallbackKey != null) {
                bleCallbackMap.remove(removeBleCallbackKey)
                bluetoothGattInfoHashMap.remove(removeBluetoothGattInfoKey)
            }
        }
    }
}
