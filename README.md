# BLE-SDK

*注意：如果该 SDK 出现无法接收蓝牙从设备返回的数据，请使用我的这个项目：[RxAndroidBluetooth](https://github.com/hgncxzy/RxAndroidBluetooth)* *或者我的这个项目：[AndroidBleTools](https://github.com/hgncxzy/AndroidBleTools)*

*调试工具请参考 [AndroidBleTools](https://github.com/hgncxzy/AndroidBleTools) 或者 [Xbluetooth](https://github.com/duoshine/Xbluetooth)*

## BLE 蓝牙 SDK 接入教程

### 1.  简介

通过该蓝牙SDK,可以实现与相关蓝牙硬件模组传感器之间的连接与通信，使用了 [kotlin](https://play.kotlinlang.org/byExample/overview) 和 低功耗蓝牙通信等技术。

### 2.  接入方式

#### 2.1. 引入 aar

将 [baseble-release.aar](https://github.com/hgncxzy/BLE-SDK/blob/master/aar/baseble-release.aar) 导入项目的 libs 文件夹下。

#### 2.2. 修改 build.gradle 配置文件

添加

```groovy
repositories  {  
    flatDir {    
        dirs 'libs' 
    }
}
```

这个是添加一个本地仓库，并把 libs 目录作为仓库的地址。

#### 2.3. 修改 dependencies 添加依赖

添加一行：

```groovy
implementation(name:'baseble-release', ext:'aar')
```

其中 name 就是 libs 目录下 baseble-release.aar 文件名称，ext，就是 baseble-release.aar 的扩展名。构建完后就可以直接使用该库的功能了。

### 3.  使用介绍

下面介绍蓝牙 SDK 的具体用法。

#### 3.1. 权限配置

蓝牙操作针对 6.0 以下系统需要配置如下权限：

```xml
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
```

而 6.0 以上系统还需要增加模糊定位权限：

```xml
<uses-permission-sdk-23 android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

**为了简便操作，库中对蓝牙操作需要的权限都做了相关设置不需要重复设置，但 6.0 以上系统需要动态申请模糊定位权限。**

动态申请权限以及蓝牙使能检查的代码如下：

```kotlin
private fun checkPermissionAndBleEnable(){
        if (Build.VERSION.SDK_INT >= 6.0) {
            PermissionUtil.checkPermission(this,MY_PERMISSION_REQUEST_CONSTANT)
        }
        if (BleUtil.isSupportBle(this)) {
            if (!BleUtil.isBleEnable(this)) {
                BleUtil.enableBluetooth(this, REQUEST_ENABLE_BT)
            } else {
               // todo(逻辑代码)
            }
        } else {
            Toast.makeText(this, "该设备不支持 ble", Toast.LENGTH_SHORT).show()
        }
}
```

其中 PermissionUtil 与 BleUtil 均已经在 SDK 中封装好，直接调用即可。

#### 3.2. 初始化 SDK

在使用该库前需要进行初始化，初始化代码如下所示：

```kotlin
//蓝牙相关配置修改
BaseBle.config()!!
                .setscanBleTimeout(-1)//扫描超时时间，这里设置为永久扫描
                .setconnectBleTimeout(10 * 1000)//连接超时时间
                .setoperateBleTimeout(5 * 1000)//设置数据操作超时时间
                .setconnectBleRetryCount(3)//设置连接失败重试次数
                .setconnectBleRetryInterval(1000)//设置连接失败重试间隔时间
                .setoperateBleRetryCount(1)//设置数据操作失败重试次数
                .setoperateBleRetryInterval(1000000000)//设置数据操作失败重试间隔时间
                .setmaxConnectBleCount(1)//设置最大连接设备数量
                .setBleServiceUUID("0000ffe0-0000-1000-8000-00805f9b34fb")// 服务 UUID
                .setBleCharacteristicUUID("0000ffe1-0000-1000-8000-00805f9b34fb")// 特征码 UUID
                .setBleDescriptorUUID("0000ffe1-0000-1000-8000-00805f9b34fb") // 描述 UUID
 //蓝牙信息初始化，全局唯一，必须在应用初始化时调用
 BaseBle.getInstance()!!.init(this)
```

初始化可以是在 Application 中也可以是在 MainActivity 中，只需要是在使用蓝牙功能前就行。还有需要注意的是，蓝牙配置必须在蓝牙初始化前进行修改，如果默认配置满足要求也可以不修改配置。最后面三个方法中的 UUID 请硬件工程师提供后，修改即可。

#### 3.3. 注册广播

定义广播接收器，接收从 SDK 发过来的广播消息。

##### 3.3.1. 定义广播接收器

定义广播接收器，详见 sample-ble

```kotlin
inner class MyReceiver : BroadcastReceiver(){
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when {
                SEND_CMD_SUCCESS == action -> {
                    val bundle = intent.getBundleExtra("bundle_data")
                    val data = bundle.getByteArray("byte_array_data")!!
                    mSendDataTv?.text = "发送数据："+HexUtil.parseBytesToHexString(data)
                    Log.d(TAG,"发送数据成功"+ HexUtil.parseBytesToHexString(data))
                }
                SEND_CMD_FAILED == action -> Log.d(TAG,"发送数据失败")
                RECEIVE_DATA_SUCCESS == action -> {
                    val bundle = intent.getBundleExtra("bundle_data")
                    val data = bundle.getByteArray("byte_array_data")!!
                    mReceiveDataTv?.text ="接收数据："+ HexUtil.parseBytesToHexString(data)
                    Log.d(BleConfig.TAG, "正常--收到数据：" + HexUtil.parseBytesToHexString(data))
                }
                RECEIVE_DATA_FAILED == action -> Log.d(TAG,"")
            }
        }
    }
```

##### 3.3.2. 注册广播

调用以下方法注册，详见 sample-ble

```kotlin
private fun registerMyReceiver(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(SEND_CMD_SUCCESS)
        intentFilter.addAction(SEND_CMD_FAILED)
        intentFilter.addAction(RECEIVE_DATA_SUCCESS)
        intentFilter.addAction(RECEIVE_DATA_FAILED)
        registerReceiver(myReceiver,intentFilter)
    }
```

##### 3.3.3. 反注册广播

```kotlin
 override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver) // 反注册广播
        BleConfig.getInstance()?.deviceMirror?.unregisterNotify(true)
        BaseBle.getInstance()!!.disconnect()
        BaseBle.getInstance()!!.clear()
    }
```



#### 3.4. 通过 MAC 地址直连

```kotlin
     BaseBle.getInstance()!!.connectByMac(deviceMac, object : IConnectCallback {
            override fun onConnectSuccess(deviceMirror: DeviceMirror) {
                val disposable = Flowable.just(1)
                        .doOnNext {
                            Log.d("test","状态：已连接")
                            mConnStatusTv!!.text = "状态：已连接"
                            // 更新设备镜像
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

```

在连接成功的回调函数（onConnectSuccess）中，别忘了设置当前连接设备的镜像：

```kotlin
BleConfig.getInstance()?.deviceMirror = deviceMirror
```

#### 3.5. 发送数据

将发送数据封装成 byte[],调用以下方法即可发送：

```kotlin
Command.write(cmd: ByteArray)
```

#### 3.6.接收数据

通过广播接收器接收数据，见 3.3.1.

#### 3.5. 断开设备

退出应用时，断开连接。

```kotlin
BaseBle.getInstance()!!.disconnect()
```



#### 3.6. 释放资源

退出应用时，清除资源。

```kotlin
BaseBle.getInstance()!!.clear()
```

#### 3.7. 参考资料
2. [参考了这个 git 项目](https://github.com/xiaoyaoyou1212/BLE)
2. [Android 蓝牙权限](https://www.jianshu.com/p/449242010612)
3. [关于经典蓝牙和低功耗蓝牙的区别](https://blog.csdn.net/lyl953147712/article/details/77025294)
4. [BLE、BR、EDR 等特定术语解读](http://www.sohu.com/a/235874099_404276)

#### 3.8. 作者
1. ID : hgncxzy

2. 邮箱：hgncxzy@qq.com
3. 项目地址：https://github.com/hgncxzy/BLE-SDK
