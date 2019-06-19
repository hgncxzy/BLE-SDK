package com.xzy.ble.baseble.common
import com.xzy.ble.baseble.common.BleConstant.CHARACTERISTIC_UUID
import com.xzy.ble.baseble.common.BleConstant.DEFAULT_CONN_TIME
import com.xzy.ble.baseble.common.BleConstant.DEFAULT_MAX_CONNECT_COUNT
import com.xzy.ble.baseble.common.BleConstant.DEFAULT_OPERATE_TIME
import com.xzy.ble.baseble.common.BleConstant.DEFAULT_RETRY_COUNT
import com.xzy.ble.baseble.common.BleConstant.DEFAULT_RETRY_INTERVAL
import com.xzy.ble.baseble.common.BleConstant.DEFAULT_SCAN_REPEAT_INTERVAL
import com.xzy.ble.baseble.common.BleConstant.DEFAULT_SCAN_TIME
import com.xzy.ble.baseble.common.BleConstant.DESCRIPTOR_UUID
import com.xzy.ble.baseble.common.BleConstant.SERVICE_UUID
import com.xzy.ble.baseble.core.DeviceMirror
import java.util.*


/**
 * 蓝牙通信相关配置
 */
@Suppress("unused")
class BleConfig private constructor() {

    var scanBleTimeout = DEFAULT_SCAN_TIME//扫描超时时间（毫秒）
    var connectBleTimeout = DEFAULT_CONN_TIME//连接超时时间（毫秒）
    var operateBleTimeout = DEFAULT_OPERATE_TIME//数据操作超时时间（毫秒）
    var connectBleRetryCount = DEFAULT_RETRY_COUNT//连接重试次数
    var connectBleRetryInterval = DEFAULT_RETRY_INTERVAL//连接重试间隔（毫秒）
    var operateBleRetryCount = DEFAULT_RETRY_COUNT//数据操作重试次数
    var operateBleRetryInterval = DEFAULT_RETRY_INTERVAL//数据操作重试间隔时间（毫秒）
    var maxConnectBleCount = DEFAULT_MAX_CONNECT_COUNT//最大连接数量
    var scanBleRepeatInterval = DEFAULT_SCAN_REPEAT_INTERVAL//每隔X时间重复扫描 (毫秒)
    var serviceUUID = SERVICE_UUID // 服务 UUID
    var characteristicUUID = CHARACTERISTIC_UUID // 特征 UUID
    var descriptorUUID =DESCRIPTOR_UUID // 描述 UUID
    var deviceMirror: DeviceMirror? = null // 设备镜像

    /**
     * 获取蓝牙设备镜像
     *
     * @return 返回 DeviceMirror
     */
    fun getBleDeviveMirror():DeviceMirror?{
        return deviceMirror
    }

    /**
     * 设置蓝牙设备镜像
     *
     * @return 返回 BleConfig
     */
    fun setBleDeviveMirror(deviceMirror: DeviceMirror):BleConfig{
        this.deviceMirror = deviceMirror
        return this
    }

    /**
     * 获取服务 UUID
     *
     * @return 返回服务 UUID
     */
    fun getBleServiceUUID():UUID{
        return serviceUUID
    }

    /**
     * 设置服务 UUID
     *
     * @return 返回 BleConfig
     */
    fun setBleServiceUUID(serviceUUID: String):BleConfig{
        this.serviceUUID = UUID.fromString(serviceUUID)
        return this
    }

    /**
     * 获取特征 UUID
     *
     * @return 返回特征 UUID
     */
    fun getBleCharacteristicUUID():UUID{
        return characteristicUUID
    }

    /**
     * 设置特征 UUID
     *
     * @return 返回 BleConfig
     */
    fun setBleCharacteristicUUID(characteristicUUID: String):BleConfig{
        this.characteristicUUID = UUID.fromString(characteristicUUID)
        return this
    }

    /**
     * 获取描述 UUID
     *
     * @return 返回描述 UUID
     */
    fun getBleDescriptorUUID():UUID{
        return descriptorUUID
    }

    /**
     * 设置描述 UUID
     *
     * @return 返回 BleConfig
     */
    fun setBleDescriptorUUID(descriptorUUID: String):BleConfig{
        this.descriptorUUID = UUID.fromString(descriptorUUID)
        return this
    }

    /**
     * 获取发送数据超时时间
     *
     * @return 返回发送数据超时时间
     */
    fun getoperateBleTimeout(): Int {
        return operateBleTimeout
    }

    /**
     * 设置发送数据超时时间
     *
     * @param operateBleTimeout 发送数据超时时间
     * @return 返回FcBoxBle
     */
    fun setoperateBleTimeout(operateBleTimeout: Int): BleConfig {
        this.operateBleTimeout = operateBleTimeout
        return this
    }

    /**
     * 获取连接超时时间
     *
     * @return 返回连接超时时间
     */
    fun getconnectBleTimeout(): Int {
        return connectBleTimeout
    }

    /**
     * 设置连接超时时间
     *
     * @param connectBleTimeout 连接超时时间
     * @return 返回FcBoxBle
     */
    fun setconnectBleTimeout(connectBleTimeout: Int): BleConfig {
        this.connectBleTimeout = connectBleTimeout
        return this
    }

    /**
     * 获取扫描超时时间
     *
     * @return 返回扫描超时时间
     */
    fun getscanBleTimeout(): Int {
        return scanBleTimeout
    }

    /**
     * 设置扫描超时时间
     *
     * @param scanBleTimeout 扫描超时时间
     * @return 返回FcBoxBle
     */
    fun setscanBleTimeout(scanBleTimeout: Int): BleConfig {
        this.scanBleTimeout = scanBleTimeout
        return this
    }

    /**
     * 获取连接重试次数
     *
     * @return int
     */
    fun getconnectBleRetryCount(): Int {
        return connectBleRetryCount
    }

    /**
     * 设置连接重试次数
     *
     * @param connectBleRetryCount 重试次数
     * @return BleConfig
     */
    fun setconnectBleRetryCount(connectBleRetryCount: Int): BleConfig {
        this.connectBleRetryCount = connectBleRetryCount
        return this
    }

    /**
     * 获取连接重试间隔时间
     *
     * @return int
     */
    fun getconnectBleRetryInterval(): Int {
        return connectBleRetryInterval
    }

    /**
     * 设置连接重试间隔时间
     *
     * @param connectBleRetryInterval 重试间隔时间
     * @return BleConfig
     */
    fun setconnectBleRetryInterval(connectBleRetryInterval: Int): BleConfig {
        this.connectBleRetryInterval = connectBleRetryInterval
        return this
    }

    /**
     * 获取最大连接数量
     *
     * @return int
     */
    fun getmaxConnectBleCount(): Int {
        return maxConnectBleCount
    }

    /**
     * 设置最大连接数量
     *
     * @param maxConnectBleCount  最大连接数量
     * @return BleConfig
     */
    fun setmaxConnectBleCount(maxConnectBleCount: Int): BleConfig {
        this.maxConnectBleCount = maxConnectBleCount
        return this
    }

    /**
     * 获取操作数据重试次数
     *
     * @return int
     */
    fun getoperateBleRetryCount(): Int {
        return operateBleRetryCount
    }

    /**
     * 设置操作数据重试次数
     *
     * @param operateBleRetryCount 重试次数
     * @return BleConfig
     */
    fun setoperateBleRetryCount(operateBleRetryCount: Int): BleConfig {
        this.operateBleRetryCount = operateBleRetryCount
        return this
    }

    /**
     * 获取操作数据重试间隔时间
     *
     * @return int
     */
    fun getoperateBleRetryInterval(): Int {
        return operateBleRetryInterval
    }

    /**
     * 设置操作数据重试间隔时间
     *
     * @param operateBleRetryInterval  重试间隔时间
     * @return BleConfig
     */
    fun setoperateBleRetryInterval(operateBleRetryInterval: Int): BleConfig {
        this.operateBleRetryInterval = operateBleRetryInterval
        return this
    }

    /**
     * 获取扫描间隔时间
     * @return int
     */
    fun getscanBleRepeatInterval(): Int {
        return scanBleRepeatInterval
    }

    /**
     * 设置每隔多少时间重复扫描一次
     * 设置扫描间隔时间 （毫秒）
     * @param scanBleRepeatInterval 扫描间隔时间
     * @return BleConfig
     */
    fun setscanBleRepeatInterval(scanBleRepeatInterval: Int): BleConfig {
        this.scanBleRepeatInterval = scanBleRepeatInterval
        return this
    }

    companion object {
        const val TAG = "[FcBox-ble-sdk-log]"
        private var instance: BleConfig? = null

        fun getInstance(): BleConfig? {
            if (instance == null) {
                synchronized(BleConfig::class.java) {
                    if (instance == null) {
                        instance = BleConfig()
                    }
                }
            }
            return instance
        }
    }
}
