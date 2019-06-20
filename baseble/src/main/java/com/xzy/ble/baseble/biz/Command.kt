package com.xzy.ble.baseble.biz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.xzy.ble.baseble.BaseBle
import com.xzy.ble.baseble.callback.IBleCallback
import com.xzy.ble.baseble.common.BleConfig
import com.xzy.ble.baseble.common.BleConstant.RECEIVE_DATA_FAILED
import com.xzy.ble.baseble.common.BleConstant.RECEIVE_DATA_SUCCESS
import com.xzy.ble.baseble.common.BleConstant.SEND_CMD_FAILED
import com.xzy.ble.baseble.common.BleConstant.SEND_CMD_SUCCESS
import com.xzy.ble.baseble.common.PropertyType
import com.xzy.ble.baseble.core.BluetoothGattChannel
import com.xzy.ble.baseble.exception.BleException
import com.xzy.ble.baseble.model.BluetoothLeDevice

/**
 * Description : 命令发送与接收接口
 * Created by XuZhuYun 2019/6/18 16:54 .
 */
@Suppress("unused")
object Command {
    /**
     * 发送数据.
     *
     */
     fun write(cmd:ByteArray) {
        val bluetoothGattChannel = BluetoothGattChannel.Builder()
                .setBluetoothGatt(BleConfig.getInstance()!!.deviceMirror?.bluetoothGatt!!)
                .setPropertyType(PropertyType.PROPERTY_WRITE)
                .setServiceUUID(BleConfig.getInstance()!!.serviceUUID)
                .setCharacteristicUUID(BleConfig.getInstance()!!.characteristicUUID)
                .setDescriptorUUID(BleConfig.getInstance()!!.descriptorUUID)
                .builder()
        BleConfig.getInstance()!!.deviceMirror?.bindChannel(object : IBleCallback {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(data: ByteArray, bluetoothGattChannel: BluetoothGattChannel, bluetoothLeDevice: BluetoothLeDevice) {
                val bundle = Bundle()
                bundle.putByteArray("byte_array_data",data)
                BaseBle.getInstance()?.context?.sendBroadcast(Intent(SEND_CMD_SUCCESS).putExtra("bundle_data",bundle))
            }

            override fun onFailure(exception: BleException) {
                BaseBle.getInstance()?.context?.sendBroadcast(Intent(SEND_CMD_FAILED))
                Log.d(BleConfig.TAG, "BleException：" + exception.description)
            }
        }, bluetoothGattChannel)
        BleConfig.getInstance()!!.deviceMirror?.writeData(cmd)
        read()
    }

    /**
     * 接收数据.
     */
      private fun read() {
        val bluetoothGattChannel = BluetoothGattChannel.Builder()
                .setBluetoothGatt(BleConfig.getInstance()!!.deviceMirror?.bluetoothGatt!!)
                .setPropertyType(PropertyType.PROPERTY_INDICATE)
                .setServiceUUID(BleConfig.getInstance()!!.serviceUUID)
                .setCharacteristicUUID(BleConfig.getInstance()!!.characteristicUUID)
                .setDescriptorUUID(BleConfig.getInstance()!!.descriptorUUID)
                .builder()

        // 绑定通道
        BleConfig.getInstance()!!.deviceMirror?.bindChannel(object : IBleCallback {
            override fun onSuccess(data: ByteArray, bluetoothGattChannel: BluetoothGattChannel, bluetoothLeDevice: BluetoothLeDevice) {}

            override fun onFailure(exception: BleException) {
            }
        }, bluetoothGattChannel)
        BleConfig.getInstance()!!.deviceMirror?.registerNotify(true)

        // 注册通知
        BleConfig.getInstance()!!.deviceMirror?.setNotifyListener(bluetoothGattChannel.gattInfoKey, object : IBleCallback {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(data: ByteArray, bluetoothGattChannel: BluetoothGattChannel, bluetoothLeDevice: BluetoothLeDevice) {
                val bundle = Bundle()
                bundle.putByteArray("byte_array_data",data)
                BaseBle.getInstance()?.context?.sendBroadcast(Intent(RECEIVE_DATA_SUCCESS).putExtra("bundle_data",bundle))
            }

            override fun onFailure(exception: BleException) {
                BaseBle.getInstance()?.context?.sendBroadcast(Intent(RECEIVE_DATA_FAILED))
                Log.d(BleConfig.TAG, "BleException：" + exception.description)
            }
        })
    }

}