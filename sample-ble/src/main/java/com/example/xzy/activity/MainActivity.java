package com.example.xzy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fcbox.ble.baseble.FcBoxBle;
import com.fcbox.ble.baseble.callback.IConnectCallback;
import com.fcbox.ble.baseble.callback.scan.IScanCallback;
import com.fcbox.ble.baseble.callback.scan.SingleFilterScanCallback;
import com.fcbox.ble.baseble.core.DeviceMirror;
import com.fcbox.ble.baseble.exception.BleException;
import com.fcbox.ble.baseble.model.BluetoothLeDevice;
import com.fcbox.ble.baseble.model.BluetoothLeDeviceStore;

public class MainActivity extends AppCompatActivity {
    private String deviceMac = "00:15:80:90:76:20";
    private static final String TAG = "[ble-test]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //蓝牙相关配置修改
        FcBoxBle.config()
                .setScanTimeout(-1)//扫描超时时间，这里设置为永久扫描
                .setConnectTimeout(10 * 1000)//连接超时时间
                .setOperateTimeout(5 * 1000)//设置数据操作超时时间
                .setConnectRetryCount(3)//设置连接失败重试次数
                .setConnectRetryInterval(1000)//设置连接失败重试间隔时间
                .setOperateRetryCount(3)//设置数据操作失败重试次数
                .setOperateRetryInterval(1000)//设置数据操作失败重试间隔时间
                .setMaxConnectCount(3);//设置最大连接设备数量
        //蓝牙信息初始化，全局唯一，必须在应用初始化时调用
        FcBoxBle.getInstance().init(this);

        //该方式是扫到指定设备就停止扫描
        FcBoxBle.getInstance().startScan(new SingleFilterScanCallback(new IScanCallback() {
            @Override
            public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
                Toast.makeText(MainActivity.this,"找到目标设备",Toast.LENGTH_SHORT).show();
                FcBoxBle.getInstance().connect(bluetoothLeDevice, new IConnectCallback() {
                    @Override
                    public void onConnectSuccess(DeviceMirror deviceMirror) {
                        Log.d(TAG,"连接成功");
                    }

                    @Override
                    public void onConnectFailure(BleException exception) {
                        Log.d(TAG,"连接失败");
                    }

                    @Override
                    public void onDisconnect(boolean isActive) {
                        Log.d(TAG,"断开连接");
                    }
                });
            }

            @Override
            public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {
                Toast.makeText(MainActivity.this,"扫描结束",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanTimeout() {
                Toast.makeText(MainActivity.this,"扫描超时",Toast.LENGTH_SHORT).show();
            }
        }).setDeviceMac(deviceMac));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FcBoxBle.getInstance().disconnect();
        FcBoxBle.getInstance().clear();
    }
}
