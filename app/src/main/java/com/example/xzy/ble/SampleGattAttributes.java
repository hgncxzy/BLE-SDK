package com.example.xzy.ble;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
	private static HashMap<String, String> attributes = new HashMap<>();
	// public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
	static String HEART_RATE_MEASUREMENT = "bef8d6c9-9c21-4c9e-b632-bd58c1009f9f";
	static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

	static {
		// 样品服务。
		attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
		attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
		//样本特征。
		attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
		attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
}
