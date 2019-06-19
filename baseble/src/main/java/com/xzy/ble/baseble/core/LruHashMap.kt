package com.xzy.ble.baseble.core

import java.util.LinkedHashMap


/**
 * Lru算法实现的 HashMap
 * @param <K> 键
 * @param <V> 值
</V></K> */
class LruHashMap<K, V>(private val MAX_SAVE_SIZE: Int) : LinkedHashMap<K, V>(Math.ceil(MAX_SAVE_SIZE / 0.75).toInt() + 1, 0.75f, true) {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        if (eldest != null) {
            if (size > MAX_SAVE_SIZE && eldest.value is DeviceMirror) {
                (eldest.value as DeviceMirror).disconnect()
            }
        }
        return size > MAX_SAVE_SIZE
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for ((key, value) in entries) {
            sb.append(String.format("%s:%s ", key, value))
        }
        return sb.toString()
    }
}
