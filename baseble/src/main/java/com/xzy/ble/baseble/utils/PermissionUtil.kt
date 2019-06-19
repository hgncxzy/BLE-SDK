package com.xzy.ble.baseble.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

/**
 * Description : 权限操作工具类
 * Created by XuZhuYun 2019/6/18 10:55 .
 */
@Suppress("unused")
object PermissionUtil {

    fun checkPermission(activity: Activity,requestCode: Int){
        val checkAccessFinePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (checkAccessFinePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    requestCode)
        }
    }
}