package com.example.xzy.activity;





import static com.example.xzy.config.Config.TARGET_DEVICE_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.xzy.app.BleApplication;
import com.example.xzy.R;

/**
 * ble 蓝牙连接类
 *  Created by XuZhuYun 2019/4/3 10:33 .
 */
@SuppressLint("NewApi")
public class BleConnectActivity extends Activity {
	private static final String TAG = "MainActivity";
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private ArrayList<HashMap<String, Object>> listItem;
	private SimpleAdapter adapter;
	private ProgressBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new Handler();

		Button startBtn= findViewById(R.id.btstart);
		Button stopBtn= findViewById(R.id.btstop);
		bar = findViewById(R.id.bar);
		bar.setVisibility(View.GONE);
		ListView listView = findViewById(R.id.list);
		listItem = new ArrayList<>();


		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this,"没有蓝牙", Toast.LENGTH_SHORT).show();
			finish();
		}

		// 初始化一个蓝牙适配器
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = Objects.requireNonNull(bluetoothManager).getAdapter();

		//  检查是否支持蓝牙的设备。
		if (mBluetoothAdapter == null) {
			Toast.makeText(this,"设备不支持", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		adapter = new SimpleAdapter(this,listItem,android.R.layout.simple_expandable_list_item_2,
				new String[]{"name","address"},new int[]{android.R.id.text1,android.R.id.text2});

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				BluetoothDevice device=	(BluetoothDevice) listItem.get(arg2).get("device");
				Log.e(TAG, "点击的按钮"+arg2+device.getAddress()+"cacaca");

				Intent intent=new Intent(getApplicationContext(), TestActivity.class);

				intent.putExtra("address",device.getAddress());
				intent.putExtra("name",device.getName());

				if (mScanning) {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mScanning = false;
				}

				startActivity(intent);
			}
		});

		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				scanLeDevice(true);
				Log.e(TAG, "开始搜寻");
			}
		});

		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				scanLeDevice(false);
				Log.e(TAG, "停止");
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();

		// 确保蓝牙是在设备上启用。如果当前没有启用蓝牙，
		// 意图显示一个对话框询问用户授予权限以使它。
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
		//      scanLeDevice(true);
	}


	private void scanLeDevice(final boolean enable) {
		if (enable) {
			//停止后一个预定义的扫描周期扫描。
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					bar.setVisibility(View.GONE);
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
				}
			}, 10000);
			bar.setVisibility(View.VISIBLE);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			bar.setVisibility(View.GONE);
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}

		invalidateOptionsMenu();
	}

	// 扫描装置的回调。
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {

				@Override
				public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							HashMap<String, Object> map = new HashMap<>();

							Log.e(TAG, "RSSI=:"+rssi+"");

							map.put("name", device.getName());
							map.put("address",device.getAddress());
							map.put("device", device);
							if(!listItem.contains(map)){
								listItem.add(map);
								adapter.notifyDataSetChanged();
							}
							// 根据设备名称来过滤 ，也可以通过设备 mac 地址来过滤
							if(TARGET_DEVICE_NAME.equals(device.getName())){
								Toast.makeText(BleApplication.getInstance().getApplicationContext(),"find target.",Toast.LENGTH_SHORT).show();
								Intent intent=new Intent(getApplicationContext(), TestActivity.class);
								intent.putExtra("address",device.getAddress());
								intent.putExtra("name",device.getName());

								if (mScanning) {
									mBluetoothAdapter.stopLeScan(mLeScanCallback);
									mScanning = false;
								}

								startActivity(intent);
							}
							Log.e(TAG,"发现蓝牙"+device.getAddress()+"状态"+device.getBondState()+"type"+device.getType()+device.describeContents());
						}
					});
				}
			};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 将菜单；这将项目添加到动作条如果真的存在。
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
