package com.fcbox.ble.baseble.callback.scan;


import com.fcbox.ble.baseble.model.BluetoothLeDevice;


/**
 * 扫描过滤接口，根据需要实现过滤规则
 */
public interface IScanFilter {
    BluetoothLeDevice onFilter(BluetoothLeDevice bluetoothLeDevice);
}
