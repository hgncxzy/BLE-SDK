package com.fcbox.ble.baseble.core;

import com.fcbox.ble.baseble.common.BleConfig;
import com.fcbox.ble.baseble.common.ConnectState;
import com.fcbox.ble.baseble.model.BluetoothLeDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * 设备镜像池，用来管理多个设备连接后的操作
 */
@SuppressWarnings("unused")
public class DeviceMirrorPool {
    private final LruHashMap<String, DeviceMirror> DEVICE_MIRROR_MAP;

    public DeviceMirrorPool() {
        DEVICE_MIRROR_MAP = new LruHashMap<>(BleConfig.getInstance().getMaxConnectCount());
    }

    public DeviceMirrorPool(int deviceMirrorSize) {
        DEVICE_MIRROR_MAP = new LruHashMap<>(deviceMirrorSize);
    }

    /**
     * 添加设备镜像
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     */
    public synchronized void addDeviceMirror(BluetoothLeDevice bluetoothLeDevice) {
        if (bluetoothLeDevice == null) {
            return;
        }
        String key = bluetoothLeDevice.getAddress() + bluetoothLeDevice.getName();
        if (!DEVICE_MIRROR_MAP.containsKey(key)) {
            DEVICE_MIRROR_MAP.put(key, new DeviceMirror(bluetoothLeDevice));
        }
    }

    /**
     * 添加设备镜像
     *
     * @param deviceMirror 设备镜像
     */
    public synchronized void addDeviceMirror(DeviceMirror deviceMirror) {
        if (deviceMirror == null) {
            return;
        }
        if (!DEVICE_MIRROR_MAP.containsKey(deviceMirror.getUniqueSymbol())) {
            DEVICE_MIRROR_MAP.put(deviceMirror.getUniqueSymbol(), deviceMirror);
        }
    }

    /**
     * 删除设备镜像
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     */
    public synchronized void removeDeviceMirror(BluetoothLeDevice bluetoothLeDevice) {
        if (bluetoothLeDevice == null) {
            return;
        }
        String key = bluetoothLeDevice.getAddress() + bluetoothLeDevice.getName();
        DEVICE_MIRROR_MAP.remove(key);
    }

    /**
     * 删除设备镜像
     *
     * @param deviceMirror 设备镜像
     */
    public synchronized void removeDeviceMirror(DeviceMirror deviceMirror) {
        if (deviceMirror == null) {
            return;
        }
        if (DEVICE_MIRROR_MAP.containsKey(deviceMirror.getUniqueSymbol())) {
            deviceMirror.clear();
            DEVICE_MIRROR_MAP.remove(deviceMirror.getUniqueSymbol());
        }
    }

    /**
     * 判断是否包含设备镜像
     *
     * @param deviceMirror 设备镜像
     * @return boolean
     */
    public synchronized boolean isContainDevice(DeviceMirror deviceMirror) {
        return deviceMirror != null && DEVICE_MIRROR_MAP.containsKey(deviceMirror.getUniqueSymbol());
    }

    /**
     * 判断是否包含设备镜像
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     * @return boolean
     */
    public synchronized boolean isContainDevice(BluetoothLeDevice bluetoothLeDevice) {
        return bluetoothLeDevice != null && DEVICE_MIRROR_MAP.containsKey(bluetoothLeDevice.getAddress() +
                bluetoothLeDevice.getName());
    }

    /**
     * 获取连接池中该设备镜像的连接状态，如果没有连接则返回CONNECT_DISCONNECT。
     *
     * @param bluetoothLeDevice  BluetoothLeDevice
     * @return ConnectState
     */
    public synchronized ConnectState getConnectState(BluetoothLeDevice bluetoothLeDevice) {
        DeviceMirror deviceMirror = getDeviceMirror(bluetoothLeDevice);
        if (deviceMirror != null) {
            return deviceMirror.getConnectState();
        }
        return ConnectState.CONNECT_DISCONNECT;
    }

    /**
     * 获取连接池中的设备镜像，如果没有连接则返回空
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     * @return DeviceMirror
     */
    public synchronized DeviceMirror getDeviceMirror(BluetoothLeDevice bluetoothLeDevice) {
        if (bluetoothLeDevice != null) {
            String key = bluetoothLeDevice.getAddress() + bluetoothLeDevice.getName();
            if (DEVICE_MIRROR_MAP.containsKey(key)) {
                return DEVICE_MIRROR_MAP.get(key);
            }
        }
        return null;
    }

    /**
     * 断开连接池中某一个设备
     *
     * @param bluetoothLeDevice BluetoothLeDevice
     */
    public synchronized void disconnect(BluetoothLeDevice bluetoothLeDevice) {
        if (isContainDevice(bluetoothLeDevice)) {
            getDeviceMirror(bluetoothLeDevice).disconnect();
        }
    }

    /**
     * 断开连接池中所有设备
     */
    public synchronized void disconnect() {
        for (Map.Entry<String, DeviceMirror> stringDeviceMirrorEntry : DEVICE_MIRROR_MAP.entrySet()) {
            stringDeviceMirrorEntry.getValue().disconnect();
        }
        DEVICE_MIRROR_MAP.clear();
    }

    /**
     * 清除连接池
     */
    public synchronized void clear() {
        for (Map.Entry<String, DeviceMirror> stringDeviceMirrorEntry : DEVICE_MIRROR_MAP.entrySet()) {
            stringDeviceMirrorEntry.getValue().clear();
        }
        DEVICE_MIRROR_MAP.clear();
    }

    /**
     * 获取连接池设备镜像Map集合
     *
     * @return Map<String, DeviceMirror>
     */
    public Map<String, DeviceMirror> getDeviceMirrorMap() {
        return DEVICE_MIRROR_MAP;
    }

    /**
     * 获取连接池设备镜像List集合
     *
     * @return List<DeviceMirror>
     */
    public synchronized List<DeviceMirror> getDeviceMirrorList() {
        final List<DeviceMirror> deviceMirrors = new ArrayList<>(DEVICE_MIRROR_MAP.values());
        Collections.sort(deviceMirrors, new Comparator<DeviceMirror>() {
            @Override
            public int compare(final DeviceMirror lhs, final DeviceMirror rhs) {
                return lhs.getUniqueSymbol().compareToIgnoreCase(rhs.getUniqueSymbol());
            }
        });
        return deviceMirrors;
    }

    /**
     * 获取连接池设备详细信息List集合
     *
     * @return List<BluetoothLeDevice>
     */
    public synchronized List<BluetoothLeDevice> getDeviceList() {
        final List<BluetoothLeDevice> deviceList = new ArrayList<>();
        for (DeviceMirror deviceMirror : getDeviceMirrorList()) {
            if (deviceMirror != null) {
                deviceList.add(deviceMirror.getBluetoothLeDevice());
            }
        }
        return deviceList;
    }

}
