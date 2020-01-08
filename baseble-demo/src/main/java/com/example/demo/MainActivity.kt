package com.example.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import com.example.demo.config.Config.byte2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.demo.config.Config.TAG
import com.example.demo.config.Config.targetDeviceMac
import com.xzy.ble.baseble.BaseBle
import com.xzy.ble.baseble.biz.Command
import com.xzy.ble.baseble.callback.IConnectCallback
import com.xzy.ble.baseble.callback.scan.ScanCallback
import com.xzy.ble.baseble.common.BleConfig
import com.xzy.ble.baseble.common.BleConstant.MY_PERMISSION_REQUEST_CONSTANT
import com.xzy.ble.baseble.common.BleConstant.RECEIVE_DATA_FAILED
import com.xzy.ble.baseble.common.BleConstant.RECEIVE_DATA_SUCCESS
import com.xzy.ble.baseble.common.BleConstant.REQUEST_ENABLE_BT
import com.xzy.ble.baseble.common.BleConstant.SEND_CMD_FAILED
import com.xzy.ble.baseble.common.BleConstant.SEND_CMD_SUCCESS
import com.xzy.ble.baseble.core.DeviceMirror
import com.xzy.ble.baseble.exception.BleException
import com.xzy.ble.baseble.model.BluetoothLeDevice
import com.xzy.ble.baseble.model.BluetoothLeDeviceStore
import com.xzy.ble.baseble.utils.BleUtil
import com.xzy.ble.baseble.utils.HexUtil
import com.xzy.ble.baseble.utils.PermissionUtil
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import com.xzy.ble.baseble.callback.scan.IScanCallback
import com.xzy.ble.baseble.callback.scan.SingleFilterScanCallback


/**
 * demo
 * Created by xzy 2019/6/13 10:33 .
 */
class MainActivity : AppCompatActivity() {
    val mCompositeDisposable = CompositeDisposable()
    private val myReceiver: MyReceiver = MyReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initBleConfig()
        // 权限检查与蓝牙使能
        checkPermissionAndBleEnable()
        registerMyReceiver()
    }

    private fun registerMyReceiver(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(SEND_CMD_SUCCESS)
        intentFilter.addAction(SEND_CMD_FAILED)
        intentFilter.addAction(RECEIVE_DATA_SUCCESS)
        intentFilter.addAction(RECEIVE_DATA_FAILED)
        registerReceiver(myReceiver,intentFilter)
    }

   inner class MyReceiver : BroadcastReceiver(){
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                SEND_CMD_SUCCESS -> {
                    val bundle = intent.getBundleExtra("bundle_data")
                    val data = bundle?.getByteArray("byte_array_data")!!
                    mSendDataTv?.text = "发送数据："+HexUtil.parseBytesToHexString(data)
                    Log.d(TAG,"发送数据成功"+ HexUtil.parseBytesToHexString(data))
                }
                SEND_CMD_FAILED -> Log.d(TAG,"发送数据失败")
                RECEIVE_DATA_SUCCESS -> {
                    val bundle = intent.getBundleExtra("bundle_data")
                    val data = bundle?.getByteArray("byte_array_data")!!
                    mReceiveDataTv?.text ="接收数据："+ HexUtil.parseBytesToHexString(data)
                    Log.d(BleConfig.TAG, "正常--收到数据：" + HexUtil.parseBytesToHexString(data))
                }
                RECEIVE_DATA_FAILED -> Log.d(TAG,"")
            }
        }
    }


    private fun initBleConfig(){
        //蓝牙相关配置修改
        BaseBle.config()!!
                .setscanBleTimeout(10*1000)//扫描超时时间，设置 -1 为永久扫描 ,这里设置 10秒后停止扫描
                .setconnectBleTimeout(10 * 1000)//连接超时时间
                .setoperateBleTimeout(5 * 1000)//设置数据操作超时时间
                .setconnectBleRetryCount(3)//设置连接失败重试次数
                .setconnectBleRetryInterval(1000)//设置连接失败重试间隔时间
                .setoperateBleRetryCount(1)//设置数据操作失败重试次数
                .setoperateBleRetryInterval(1000000000)//设置数据操作失败重试间隔时间
                .setmaxConnectBleCount(1)//设置最大连接设备数量
                .setBleServiceUUID("0000ffe0-0000-1000-8000-00805f9b34fb")
                .setBleCharacteristicUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
                .setBleDescriptorUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
        //蓝牙信息初始化，全局唯一，必须在应用初始化时调用
        BaseBle.getInstance()!!.init(this)
    }

    private fun checkPermissionAndBleEnable(){
        if (Build.VERSION.SDK_INT >= 6.0) {
            PermissionUtil.checkPermission(this,MY_PERMISSION_REQUEST_CONSTANT)
        }
        checkBleEnable()
    }

    private fun initView() {
        mConnBtn.setOnClickListener { connDevice() }
        mDisconnBtn.setOnClickListener {
            if (mConnStatusTv.text.toString().contains("已连接")) {
                BaseBle.getInstance()!!.disconnect()
            }
        }
        mSendBtn!!.setOnClickListener { sendData() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSION_REQUEST_CONSTANT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"已获取模糊定位权限",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"未获取到模糊定位权限,ble 无法正常工作",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkBleEnable() {
        if (BleUtil.isSupportBle(this)) {
            if (!BleUtil.isBleEnable(this)) {
                BleUtil.enableBluetooth(this, REQUEST_ENABLE_BT)
            } else {
               // todo(逻辑代码)
                Log.d("xzy","这里添加逻辑代码")
            }
        } else {
            Toast.makeText(this, "该设备不支持 ble", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
              // todo(逻辑代码)
                Log.d("xzy","这里添加逻辑代码")
            } else {
                Toast.makeText(this, "未开启 ble", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 连接
     */
    private fun connDevice() {
        // 通过 mac 地址连接设备
        BaseBle.getInstance()!!.connectByMac(targetDeviceMac, object : IConnectCallback {
            override fun onConnectSuccess(deviceMirror: DeviceMirror) {
                val disposable = Flowable.just(1)
                        .doOnNext {
                            Log.d("test","状态：已连接")
                            mConnStatusTv!!.text = "状态：已连接"
                            BleConfig.getInstance()?.deviceMirror = deviceMirror
                        }
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({  }, { throwable -> Log.e("error", ""+throwable.message) })
                mCompositeDisposable.add(disposable)
            }

            override fun onConnectFailure(exception: BleException) {
                val disposable = Flowable.just(1)
                        .doOnNext {
                            mConnStatusTv!!.text = "状态：连接失败"
                            Log.e("test","状态：连接失败")
                        }
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ }, { Log.e("error", ""+exception.description) })
                mCompositeDisposable.add(disposable)
            }

            override fun onDisconnect(isActive: Boolean) {
                val disposable = Flowable.just(1)
                        .doOnNext {
                            mConnStatusTv!!.text = "状态：断开连接"
                            Log.e("test","状态：连接失败")
                        }
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ }, { Log.e("error", "isActive:$isActive") })
                mCompositeDisposable.add(disposable)
            }
        })

        // 扫描所有的设备
        BaseBle.getInstance()?.startScan(ScanCallback(object : IScanCallback {
            override fun onscanBleTimeout() {
            }

            override fun onDeviceFound(bluetoothLeDevice: BluetoothLeDevice) {
                    if(targetDeviceMac == bluetoothLeDevice.address){
                        Log.d(TAG,"找到目标设备1")
                    }
            }

            override fun onScanFinish(bluetoothLeDeviceStore: BluetoothLeDeviceStore) {
                Log.d(TAG,"扫描结束1")
            }

        }))

        // 通过 MAC 地址扫描指定的设备
        BaseBle.getInstance()?.startScan(SingleFilterScanCallback(object:IScanCallback{
            override fun onDeviceFound(bluetoothLeDevice: BluetoothLeDevice) {
                Log.d(TAG,"找到目标设备2")
            }

            override fun onScanFinish(bluetoothLeDeviceStore: BluetoothLeDeviceStore) {
                Log.d(TAG,"扫描结束2")
            }

            override fun onscanBleTimeout() {

            }

        }).setDeviceMac(targetDeviceMac))

    }

    /**
     * 发送数据
     */
    private fun sendData() {
        if (!mConnStatusTv!!.text.toString().contains("已连接")) return
        Command.write(byte2)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
        BleConfig.getInstance()?.deviceMirror?.unregisterNotify(true)
        BaseBle.getInstance()!!.disconnect()
        BaseBle.getInstance()!!.clear()
    }
}
