package com.example.xzy.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import com.example.xzy.ble.BluetoothLeService;
import com.example.xzy.R;
import com.example.xzy.ble.SampleGattAttributes;
import com.example.xzy.util.CommonUtil;

/**
 * 功能测试类
 *  Created by xzy 2019/4/3 10:33 .
 */
@SuppressLint("NewApi")
public class TestActivity extends Activity {
    private static final String TAG = "BlueContActivity";
    private String mDeviceAddress;
    private TextView mStateTv;
    private EditText mDataEt;

    private ExpandableListView mGattServicesList;
    private String DATA;
    private int i;
    private ArrayList<BluetoothGattCharacteristic> mBluetoothGattCharacteristicArrayList = new ArrayList<>();


    private boolean result;
    private BluetoothLeService mBluetoothLeService;

    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();
    // 代码管理服务生命周期。
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            Log.e(TAG, "初始化蓝牙服务");
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "无法初始化蓝牙");
                finish();
            }
            // 自动连接到装置上成功启动初始化。
            result = mBluetoothLeService.connect(mDeviceAddress);


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService = null;
        }
    };


    /**
     *    处理各种事件的服务了。
     *     action_gatt_connected：连接到服务器：关贸总协定。
     *     action_gatt_disconnected：从关贸总协定的服务器断开。
     *     action_gatt_services_discovered：关贸总协定的服务发现。
     *     action_data_available：从设备接收数据。这可能是由于阅读或通知操作。
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                result = true;
                Log.e(TAG, "来了广播1");
                mStateTv.setText("连接");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                result = false;
                Log.e(TAG, "来了广播2");
                mBluetoothLeService.close();
                mStateTv.setText("未连接");
                clearUI();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                // 显示所有的支持服务的特点和用户界面。
                Log.e(TAG, "来了广播3");
                List<BluetoothGattService> supportedGattServices = mBluetoothLeService
                        .getSupportedGattServices();
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                for(int i=0;i<supportedGattServices.size();i++){
                    Log.e(TAG,"1:BluetoothGattService UUID=:"+supportedGattServices.get(i).getUuid());
                    List<BluetoothGattCharacteristic> cs = supportedGattServices.get(i).getCharacteristics();
                    for(int j=0;j<cs.size();j++){
                        Log.e(TAG,"2:   BluetoothGattCharacteristic UUID=:"+cs.get(j).getUuid());

                        List<BluetoothGattDescriptor> ds = cs.get(j).getDescriptors();
                        for(int f=0;f<ds.size();f++){
                            Log.e(TAG,"3:BluetoothGattDescriptor UUID=:"+ds.get(f).getUuid());

                            byte[] value = ds.get(f).getValue();

                            Log.e(TAG,"4:value=:"+Arrays.toString(value));
                            Log.e(TAG,"5:value=:"+Arrays.toString( ds.get(f).getCharacteristic().getValue()));
                        }
                    }
                }

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e(TAG, "来了广播4--->data:"+ CommonUtil.parseBytesToHexString(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA)));
                i++;
                DATA = ""+DATA+"\n第"+i+"条："+CommonUtil.parseBytesToHexString(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
                Log.e("a4", ""+DATA);
                mDataEt.setText(""+DATA);
                mDataEt.setSelection(DATA.length());
            }else if(BluetoothLeService.ACTION_RSSI.equals(action)){
                Log.e(TAG, "来了广播5");
                mRSSITv.setText("RSSI:"+intent.getStringExtra(BluetoothLeService.ACTION_DATA_RSSI));
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics2);
        mStateTv = findViewById(R.id.connection_state);
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra("name");
        mDeviceAddress = intent.getStringExtra("address");
        Log.e(TAG, "名字"+ deviceName +"地址"+mDeviceAddress);
        Objects.requireNonNull(getActionBar()).setTitle(deviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        DATA = "";
        i = 0;

        TextView addressTv = findViewById(R.id.device_address);
        addressTv.setText(mDeviceAddress);
        mDataEt = findViewById(R.id.data_value);
        mDataEt.setMovementMethod(ScrollingMovementMethod.getInstance());
        //tvdata.setSelected(true);
        mDataEt.requestFocus();//get the focus
        mRSSITv = findViewById(R.id.data_rssi);
        mGattServicesList = findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        EditText sendEt = findViewById(R.id.et_send);
        Button sendBtn = findViewById(R.id.btsend);

        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                boolean sendResult;
                // 发送 byte 数组
                byte[] byte1 = {0x48, 0x42,(byte) 0xA0,0x01,0x01};
                byte[] byte2 = new byte[]{0x48, 0x42,(byte) 0xA0,0x01,0x01,XorVerify(byte1)};
                sendResult = mBluetoothLeService.writeByteData(byte2);
                // 发送字符串
//                String sendStr = et_send.getText().toString();
//                sendResult = mBluetoothLeService.writeStringData(mNotifyCharacteristic,sendStr);
                // 发送成功就表示链路是通的，不一定有返回，与具体的固件版本是否可以有返回值有关
                Log.e(TAG, "发送 UUID "+ mNotifyCharacteristic.getUuid().toString() + "是否发送成功::"+sendResult);
            }
        });

        flg = true;
        Button btrssi= findViewById(R.id.btrssi);
        btrssi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (flg) {
                            try {
                                Thread.sleep(1000);
                                flg=mBluetoothLeService.readRSSI();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Log.e(TAG,"断网了");
                            }
                        }

                    }
                }).start();
            }
        });
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
          注册广播
         */
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            Log.e(TAG, "来了");
            result = mBluetoothLeService.connect(mDeviceAddress);
            Log.e(TAG, "连接请求的结果=" + result);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_settings:

                if (result) {
                    result = false;
                    mBluetoothLeService.disconnect();
                }
                onBackPressed();
                break;
            case R.id.action_cont:
                result = mBluetoothLeService.connect(mDeviceAddress);

                break;

            case R.id.action_close:
                if (result) {
                    result = false;
                    //	mBluetoothLeService.disconnect();
                    Log.e(TAG, "断开了");
                    mBluetoothLeService.close();
                    mStateTv.setText("连接断开");
                }

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 销毁广播接收器
     */
    @Override
    protected void onPause() {
        super.onPause();
        flg=false;
        unregisterReceiver(mGattUpdateReceiver);
    }
    /**
     * 结束服务
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid;
        String unknownServiceString = "service_UUID";
        String unknownCharaString = "characteristic_UUID";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<>();
        mGattCharacteristics = new ArrayList<>();

        // 循环遍历服务
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    "NAME", SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put("UUID", uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // 循环遍历特征
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                mBluetoothGattCharacteristicArrayList.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        "NAME", SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put("UUID", uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(mBluetoothGattCharacteristicArrayList);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        final BluetoothGattCharacteristic characteristic = mBluetoothGattCharacteristicArrayList.get(mBluetoothGattCharacteristicArrayList.size()-1);
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification(
                        mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            mBluetoothLeService.readCharacteristic(characteristic);

        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(
                    characteristic, true);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"NAME", "UUID"},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"NAME", "UUID"},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }


    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                        int childPosition, long id) {
                    Log.e(TAG,"点击了");
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);

                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }

            };
    private boolean flg;
    private TextView mRSSITv;

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataEt.setText("木有数据");
    }

    /**
     * 注册广播
     * @return IntentFilter
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_RSSI);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_RSSI);
        return intentFilter;
    }

    private static byte XorVerify(byte[] bytes) {
        byte XorValue = 0x00;
        byte i;
        for (i = 0x00; i < bytes.length; i++) {
            XorValue = (byte) (bytes[i] ^ XorValue);
        }
        return XorValue;
    }
}
