package com.xzy.ble.baseble.model.resolver

import android.bluetooth.BluetoothClass

/**
 * 蓝牙设备类别
 */

object BluetoothClassResolver {
    fun resolveDeviceClass(btClass: Int): String {
        when (btClass) {
            BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER -> return "A/V, Camcorder"
            BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO -> return "A/V, Car Audio"
            BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE -> return "A/V, Handsfree"
            BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES -> return "A/V, Headphones"
            BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO -> return "A/V, HiFi Audio"
            BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER -> return "A/V, Loudspeaker"
            BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE -> return "A/V, Microphone"
            BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO -> return "A/V, Portable Audio"
            BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX -> return "A/V, Set Top Box"
            BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED -> return "A/V, Uncategorized"
            BluetoothClass.Device.AUDIO_VIDEO_VCR -> return "A/V, VCR"
            BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CAMERA -> return "A/V, Video Camera"
            BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING -> return "A/V, Video Conferencing"
            BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER -> return "A/V, Video Display and Loudspeaker"
            BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY -> return "A/V, Video Gaming Toy"
            BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR -> return "A/V, Video Monitor"
            BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET -> return "A/V, Video Wearable Headset"
            BluetoothClass.Device.COMPUTER_DESKTOP -> return "Computer, Desktop"
            BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA -> return "Computer, Handheld PC/PDA"
            BluetoothClass.Device.COMPUTER_LAPTOP -> return "Computer, Laptop"
            BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA -> return "Computer, Palm Size PC/PDA"
            BluetoothClass.Device.COMPUTER_SERVER -> return "Computer, Server"
            BluetoothClass.Device.COMPUTER_UNCATEGORIZED -> return "Computer, Uncategorized"
            BluetoothClass.Device.COMPUTER_WEARABLE -> return "Computer, Wearable"
            BluetoothClass.Device.HEALTH_BLOOD_PRESSURE -> return "Health, Blood Pressure"
            BluetoothClass.Device.HEALTH_DATA_DISPLAY -> return "Health, Data Display"
            BluetoothClass.Device.HEALTH_GLUCOSE -> return "Health, Glucose"
            BluetoothClass.Device.HEALTH_PULSE_OXIMETER -> return "Health, Pulse Oximeter"
            BluetoothClass.Device.HEALTH_PULSE_RATE -> return "Health, Pulse Rate"
            BluetoothClass.Device.HEALTH_THERMOMETER -> return "Health, Thermometer"
            BluetoothClass.Device.HEALTH_UNCATEGORIZED -> return "Health, Uncategorized"
            BluetoothClass.Device.HEALTH_WEIGHING -> return "Health, Weighting"
            BluetoothClass.Device.PHONE_CELLULAR -> return "Phone, Cellular"
            BluetoothClass.Device.PHONE_CORDLESS -> return "Phone, Cordless"
            BluetoothClass.Device.PHONE_ISDN -> return "Phone, ISDN"
            BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY -> return "Phone, Modem or Gateway"
            BluetoothClass.Device.PHONE_SMART -> return "Phone, Smart"
            BluetoothClass.Device.PHONE_UNCATEGORIZED -> return "Phone, Uncategorized"
            BluetoothClass.Device.TOY_CONTROLLER -> return "Toy, Controller"
            BluetoothClass.Device.TOY_DOLL_ACTION_FIGURE -> return "Toy, Doll/Action Figure"
            BluetoothClass.Device.TOY_GAME -> return "Toy, Game"
            BluetoothClass.Device.TOY_ROBOT -> return "Toy, Robot"
            BluetoothClass.Device.TOY_UNCATEGORIZED -> return "Toy, Uncategorized"
            BluetoothClass.Device.TOY_VEHICLE -> return "Toy, Vehicle"
            BluetoothClass.Device.WEARABLE_GLASSES -> return "Wearable, Glasses"
            BluetoothClass.Device.WEARABLE_HELMET -> return "Wearable, Helmet"
            BluetoothClass.Device.WEARABLE_JACKET -> return "Wearable, Jacket"
            BluetoothClass.Device.WEARABLE_PAGER -> return "Wearable, Pager"
            BluetoothClass.Device.WEARABLE_UNCATEGORIZED -> return "Wearable, Uncategorized"
            BluetoothClass.Device.WEARABLE_WRIST_WATCH -> return "Wearable, Wrist Watch"
            else -> return "Unknown, Unknown (class=$btClass)"
        }
    }

    fun resolveMajorDeviceClass(majorBtClass: Int): String {
        return when (majorBtClass) {
            BluetoothClass.Device.Major.AUDIO_VIDEO -> "Audio/ Video"
            BluetoothClass.Device.Major.COMPUTER -> "Computer"
            BluetoothClass.Device.Major.HEALTH -> "Health"
            BluetoothClass.Device.Major.IMAGING -> "Imaging"
            BluetoothClass.Device.Major.MISC -> "Misc"
            BluetoothClass.Device.Major.NETWORKING -> "Networking"
            BluetoothClass.Device.Major.PERIPHERAL -> "Peripheral"
            BluetoothClass.Device.Major.PHONE -> "Phone"
            BluetoothClass.Device.Major.TOY -> "Toy"
            BluetoothClass.Device.Major.UNCATEGORIZED -> "Uncategorized"
            BluetoothClass.Device.Major.WEARABLE -> "Wearable"
            else -> "Unknown ($majorBtClass)"
        }
    }
}
