package com.xzy.ble.baseble.exception


import com.xzy.ble.baseble.common.BleExceptionCode

import java.io.Serializable


/**
 * BLE异常基类
 */
@Suppress("unused")
open class BleException(private var code: BleExceptionCode?, var description: String?) : Serializable {

    fun getCode(): BleExceptionCode? {
        return code
    }

    fun setCode(code: BleExceptionCode): BleException {
        this.code = code
        return this
    }

    fun getExDescription(): String? {
        return description
    }

    fun setDescription(description: String): BleException {
        this.description = description
        return this
    }

    override fun toString(): String {
        return "BleException{" +
                "code=" + code +
                ", description='" + description + '\''.toString() +
                '}'.toString()
    }
}
