package com.fcbox.ble.baseble.callback.scan;


import com.fcbox.ble.baseble.FcBoxBle;
import com.fcbox.ble.baseble.model.BluetoothLeDevice;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 设置扫描指定的单个设备，一般是设备名称和 Mac 地址
 */
public class SingleFilterScanCallback extends ScanCallback {
    private AtomicBoolean hasFound = new AtomicBoolean(false);
    private String deviceName;//指定设备名称
    private String deviceMac;//指定设备Mac地址

    public SingleFilterScanCallback(IScanCallback scanCallback) {
        super(scanCallback);
    }

    public ScanCallback setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public ScanCallback setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
        return this;
    }

    @Override
    public BluetoothLeDevice onFilter(BluetoothLeDevice bluetoothLeDevice) {
        BluetoothLeDevice tempDevice = null;
        if (!hasFound.get()) {
            if (bluetoothLeDevice != null && bluetoothLeDevice.getAddress() != null && deviceMac != null
                    && deviceMac.equalsIgnoreCase(bluetoothLeDevice.getAddress().trim())) {
                hasFound.set(true);
                isScanning = false;
                removeHandlerMsg();
                FcBoxBle.getInstance().stopScan(SingleFilterScanCallback.this);
                tempDevice = bluetoothLeDevice;
                bluetoothLeDeviceStore.addDevice(bluetoothLeDevice);
                scanCallback.onScanFinish(bluetoothLeDeviceStore);
            } else if (bluetoothLeDevice != null && bluetoothLeDevice.getName() != null && deviceName != null
                    && deviceName.equalsIgnoreCase(bluetoothLeDevice.getName().trim())) {
                hasFound.set(true);
                isScanning = false;
                removeHandlerMsg();
                FcBoxBle.getInstance().stopScan(SingleFilterScanCallback.this);
                tempDevice = bluetoothLeDevice;
                bluetoothLeDeviceStore.addDevice(bluetoothLeDevice);
                scanCallback.onScanFinish(bluetoothLeDeviceStore);
            }
        }
        return tempDevice;
    }
}
