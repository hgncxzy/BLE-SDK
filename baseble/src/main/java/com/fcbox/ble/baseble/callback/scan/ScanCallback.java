package com.fcbox.ble.baseble.callback.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;

import com.fcbox.ble.baseble.FcBoxBle;
import com.fcbox.ble.baseble.common.BleConfig;
import com.fcbox.ble.baseble.model.BluetoothLeDevice;
import com.fcbox.ble.baseble.model.BluetoothLeDeviceStore;


/**
 * 扫描设备回调
 */
@SuppressWarnings("unused")
public class ScanCallback implements BluetoothAdapter.LeScanCallback, IScanFilter {
     private Handler handler = new Handler(Looper.myLooper());
     private boolean isScan = true;//是否开始扫描
     boolean isScanning = false;//是否正在扫描
     BluetoothLeDeviceStore bluetoothLeDeviceStore;//用来存储扫描到的设备
     IScanCallback scanCallback;//扫描结果回调

     ScanCallback(IScanCallback scanCallback) {
        this.scanCallback = scanCallback;
        if (scanCallback == null) {
            throw new NullPointerException("this scanCallback is null!");
        }
        bluetoothLeDeviceStore = new BluetoothLeDeviceStore();
    }

    public ScanCallback setScan(boolean scan) {
        isScan = scan;
        return this;
    }

    public boolean isScanning() {
        return isScanning;
    }

    public void scan() {
        if (isScan) {
            if (isScanning) {
                return;
            }
            bluetoothLeDeviceStore.clear();
            if (BleConfig.getInstance().getScanTimeout() > 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isScanning = false;

                        if (FcBoxBle.getInstance().getBluetoothAdapter() != null) {
                            FcBoxBle.getInstance().getBluetoothAdapter().stopLeScan(ScanCallback.this);
                        }

                        if (bluetoothLeDeviceStore.getDeviceMap() != null
                                && bluetoothLeDeviceStore.getDeviceMap().size() > 0) {
                            scanCallback.onScanFinish(bluetoothLeDeviceStore);
                        } else {
                            scanCallback.onScanTimeout();
                        }
                    }
                }, BleConfig.getInstance().getScanTimeout());
            }else if (BleConfig.getInstance().getScanRepeatInterval() > 0){
                //如果超时时间设置为一直扫描（即 <= 0）,则判断是否设置重复扫描间隔
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isScanning = false;

                        if (FcBoxBle.getInstance().getBluetoothAdapter() != null) {
                            FcBoxBle.getInstance().getBluetoothAdapter().stopLeScan(ScanCallback.this);
                        }

                        if (bluetoothLeDeviceStore.getDeviceMap() != null
                                && bluetoothLeDeviceStore.getDeviceMap().size() > 0) {
                            scanCallback.onScanFinish(bluetoothLeDeviceStore);
                        } else {
                            scanCallback.onScanTimeout();
                        }
                        isScanning = true;
                        if (FcBoxBle.getInstance().getBluetoothAdapter() != null) {
                            FcBoxBle.getInstance().getBluetoothAdapter().startLeScan(ScanCallback.this);
                        }
                        handler.postDelayed(this,BleConfig.getInstance().getScanRepeatInterval());
                    }
                }, BleConfig.getInstance().getScanRepeatInterval());
            }
            isScanning = true;
            if (FcBoxBle.getInstance().getBluetoothAdapter() != null) {
                FcBoxBle.getInstance().getBluetoothAdapter().startLeScan(ScanCallback.this);
            }
        } else {
            isScanning = false;
            if (FcBoxBle.getInstance().getBluetoothAdapter() != null) {
                FcBoxBle.getInstance().getBluetoothAdapter().stopLeScan(ScanCallback.this);
            }
        }
    }

    public ScanCallback removeHandlerMsg() {
        handler.removeCallbacksAndMessages(null);
        bluetoothLeDeviceStore.clear();
        return this;
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        BluetoothLeDevice bluetoothLeDevice = new BluetoothLeDevice(bluetoothDevice, rssi, scanRecord, System.currentTimeMillis());
        BluetoothLeDevice filterDevice = onFilter(bluetoothLeDevice);
        if (filterDevice != null) {
            bluetoothLeDeviceStore.addDevice(filterDevice);
            scanCallback.onDeviceFound(filterDevice);
        }
    }

    @Override
    public BluetoothLeDevice onFilter(BluetoothLeDevice bluetoothLeDevice) {
        return bluetoothLeDevice;
    }

}
