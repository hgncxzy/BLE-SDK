package com.example.xzy.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.xzy.app.BleApplication;

/**
 *权限配置
 */
public class PermissionActivity extends AppCompatActivity {
    // 蓝牙使能识别码
    public static final int REQUEST_ENABLE_BT = 0x001;
    // 权限请求码
    public static final int MY_PERMISSION_REQUEST_CONSTANT = 0x002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ENABLE_BT) {
                checkPermission();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.e("","RESULT_CANCELED");
        }
    }

    /**
     * 权限请求结果
     *
     * @param requestCode  请求码
     * @param permissions  权限集合
     * @param grantResults 请求结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    handle();
                }
            }
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 6.0) {
            int checkAccessFinePermission = ActivityCompat.checkSelfPermission(BleApplication.getInstance().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkAccessFinePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_CONSTANT);
            } else {
                // 跳转到柜机页面
                handle();
            }

        } else {
            // 跳转到柜机页面
            handle();
        }
    }

    private void handle() {
        startActivity(new Intent().setClass(this, BleConnectActivity.class));
        finish();
    }
}
