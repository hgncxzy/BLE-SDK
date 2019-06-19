package com.xzy.ble.baseble.common

/**
 * 属性类型
 */
enum class PropertyType(val propertyValue: Int) {
    PROPERTY_READ(0x01),
    PROPERTY_WRITE(0x02),
    PROPERTY_NOTIFY(0x04),
    PROPERTY_INDICATE(0x08)
}
