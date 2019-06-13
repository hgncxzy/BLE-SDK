package com.fcbox.ble.baseble.callback.scan;

import android.text.TextUtils;


import com.fcbox.ble.baseble.model.BluetoothLeDevice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据正则过滤扫描设备，这里设置的是根据一定信号范围内指定正则设备名称的过滤
 */
@SuppressWarnings("unused")
public class RegularFilterScanCallback extends ScanCallback {
    private Pattern pattern;
    private int deviceRssi;//设备的信号

    public RegularFilterScanCallback(IScanCallback scanCallback) {
        super(scanCallback);
        pattern = Pattern.compile("^[\\x00-\\xff]*$");
    }

    public RegularFilterScanCallback setRegularDeviceName(String regularDeviceName) {
        //正则表达式表示的设备名称
        if (!TextUtils.isEmpty(regularDeviceName)) {
            pattern = Pattern.compile(regularDeviceName);
        }
        return this;
    }

    public RegularFilterScanCallback setDeviceRssi(int deviceRssi) {
        this.deviceRssi = deviceRssi;
        return this;
    }

    @Override
    public BluetoothLeDevice onFilter(BluetoothLeDevice bluetoothLeDevice) {
        BluetoothLeDevice tempDevice = null;
        String tempName = bluetoothLeDevice.getName();
        int tempRssi = bluetoothLeDevice.getRssi();
        if (!TextUtils.isEmpty(tempName)) {
            Matcher matcher = pattern.matcher(tempName);
            if (this.deviceRssi < 0) {
                if (matcher.matches() && tempRssi >= this.deviceRssi) {
                    tempDevice = bluetoothLeDevice;
                }
            } else {
                if (matcher.matches()) {
                    tempDevice = bluetoothLeDevice;
                }
            }
        }
        return tempDevice;
    }
}
