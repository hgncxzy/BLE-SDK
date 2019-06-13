package com.fcbox.ble.baseble.common;

/**
 * 属性类型
 */
public enum PropertyType {
    PROPERTY_READ(0x01),
    PROPERTY_WRITE(0x02),
    PROPERTY_NOTIFY(0x04),
    PROPERTY_INDICATE(0x08);

    private int propertyValue;

    PropertyType(int propertyValue) {
        this.propertyValue = propertyValue;
    }

    public int getPropertyValue() {
        return propertyValue;
    }
}
