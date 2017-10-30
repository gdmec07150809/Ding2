/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.administrator.ding;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import static android.content.ContentValues.TAG;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public  class BluetoothActivity extends Activity {
   // private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

	public static final int SHOW_RESPONSE = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000;//10秒扫描完毕

    private DeviceListAdapter mDevListAdapter;
	private DeviceListAdapter mDevListAdapterNew;
	private TextView strong;

	ListView lv_bleList;
	//List<String> list=new ArrayList<String>();
	private List<BluetoothDevice> mBleArray;
	private List<BluetoothDevice> newBleArray;
	private List<String> rssis;
	private List<BluetoothDevice> mBleArrayByNames;
	private Button Manual_btn, automatic, exit_btn;
	private TextView add_machine,left,fan;
	int time;
	String language;
	String on_activity="bluetooth";
	String [] str;
	boolean isFlag=true;
	private Spinner Right;
	private List<String> data_list;
	private ArrayAdapter<String> arr_adapter;
	boolean isF=true;
	String name, id, locationName, locationId;
	String keep;
	private ImageView setting_back;
	private long clickTime=0;
	boolean isSort=false;
	boolean isLevel=false;
	boolean isShow=true;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(BluetoothActivity.this,MainActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("keep",keep);
			bundle.putString("lang",language);
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行
		}
		return true;
	}


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
		//获取传过来的屏幕参数
		keep=getIntent().getStringExtra("keep");
		//获取传过来的语言参数
		language=getIntent().getStringExtra("lang");
		//获取传过来的时间参数
		time= Integer.parseInt(getIntent().getStringExtra("time"));
		//判断是否让屏幕常亮
		if(keep.equals("true")){
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		add_machine=findViewById(R.id.add_machine);
		//left=findViewById(R.id.left);
		Right=findViewById(R.id.Right);
		Manual_btn =findViewById(R.id.Manual);
		automatic =findViewById(R.id.automatic);
        fan =findViewById(R.id.fan);
		exit_btn =findViewById(R.id.exit);
		starTimer.sendEmptyMessageDelayed(0,100);
		//判断是否用英文
		if(language!=null){
			if(language.equals("english")){
				add_machine.setText("Device List");
				//left.setText("Sort by name");
				//Right.setText("Sort by level");
				Manual_btn.setText("Manual");
				automatic.setText("Automatic");
                fan.setText("back");
				exit_btn.setText("Exit");
			}
		}


		setting_back=findViewById(R.id.settings_back);
		//添加返回按钮的监听
		setting_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(BluetoothActivity.this,MainActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("keep",keep);
				bundle.putString("lang",language);
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行
			}
		});

		//getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //判断蓝牙是否启动,关闭则启动
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
		//退出程序事件
		exit_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				System.exit(0);
			}
		});

        lv_bleList = (ListView) findViewById(R.id.listView);
//		lv_bleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//				Intent intent=new Intent(BluetoothActivity.this,editActivity.class);
//				Bundle bundle=new Bundle();
//                bundle.putString("keep",keep);
//                bundle.putString("time",time+"");
//                bundle.putString("lang",language);
//                bundle.putString("id",id);
//                bundle.putString("name",name);
//                bundle.putString("locationName",locationName);
//                bundle.putString("locationId",locationId);
//                bundle.putString("on",on_activity);
//				bundle.putString("device_name",mDevListAdapter.getItem(i).getName());
//				bundle.putString("device_mac",mDevListAdapter.getItem(i).getAddress());
//                intent.putExtras(bundle);
//				startActivity(intent);
//			}
//		});
		mBleArray = new ArrayList<BluetoothDevice>();
		rssis=new ArrayList<String>();
		mDevListAdapter = new DeviceListAdapter();
		mDevListAdapterNew = new DeviceListAdapter();
		lv_bleList.setAdapter(mDevListAdapter);
		//手动搜索事件
		Manual_btn.setOnClickListener(new OnClickListener() {
			@Override
		public void onClick(View view) {
				Manual_btn.setBackgroundResource(R.drawable.button_shape_normal);
				automatic.setBackgroundResource(R.drawable.button_shape_active);
				//Toast.makeText(BluetoothActivity.this,"手动搜索",Toast.LENGTH_SHORT).show();
				mBleArray.clear();
				rssis.clear();
				isF=true;
				isSort=false;
				isLevel=false;
				scanLeDevice(true);//开始扫描
				isFlag=false;

		}
	});
		//自动搜索事件
		automatic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Manual_btn.setBackgroundResource(R.drawable.button_shape_active);
				automatic.setBackgroundResource(R.drawable.button_shape_normal);
				starTimer.sendEmptyMessageDelayed(0,100);
				isSort=false;
				isLevel=false;
				//mDevListAdapter.notifyDataSetChanged();
				isFlag=true;
			}
		});

		//数据
		data_list = new ArrayList<String>();
		if(language.equals("english")){
			data_list.clear();
			data_list.add("Sort by name");
			data_list.add("Sort by level");
		}else{
			data_list.clear();
			data_list.add("按名称排序");
			data_list.add("按信号强弱排序");
		}

		//适配器
		arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
		//设置样式
		arr_adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice );
		//加载适配器
		Right.setAdapter(arr_adapter);
		//下拉列表监听事件
		Right.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				switch (i){
					case 0:
						TextView tv1=(TextView) view;
						tv1.setTextSize(18.0f); //设置大小
						Manual_btn.setBackgroundResource(R.drawable.button_shape_active);
						automatic.setBackgroundResource(R.drawable.button_shape_active);
						isF=true;
						isSort=true;
						isLevel=false;
						scanLeDevice(true);//开始扫描
						if(mDevListAdapter!=null){
							mDevListAdapter = new DeviceListAdapter();
							lv_bleList.setAdapter(mDevListAdapter);
						}
						if(isShow==false){
							isFlag=false;
						}
						break;
					case 1:
						TextView tv2=(TextView) view;
						tv2.setTextSize(18.0f);
						//设置大小
						Manual_btn.setBackgroundResource(R.drawable.button_shape_active);
						automatic.setBackgroundResource(R.drawable.button_shape_active);
						isF=true;
						isSort=false;
						isLevel=true;
						isShow=true;
						scanLeDevice(true);//开始扫描
						if(mDevListAdapter!=null){
							mDevListAdapter = new DeviceListAdapter();
							lv_bleList.setAdapter(mDevListAdapter);
						}
						isFlag=false;
						break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});

    }

	//自动搜索
	private Handler starTimer = new Handler(){
		@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0){
				if(isFlag==true){
					isF=true;
					isLevel=false;
					mBleArray.clear();
					rssis.clear();
					scanLeDevice(true);//开始扫描
					mDevListAdapter = new DeviceListAdapter();
					if(mDevListAdapter!=null){
						lv_bleList.setAdapter(mDevListAdapter);
					}
					//mDevListAdapter.notifyDataSetChanged();
					starTimer.sendEmptyMessageDelayed(0,time*1000);
				}
			}
		}
	};

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
				@Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                   // invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
	}

	private LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
							 byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					System.out.println(" 强度 "+rssi);
					System.out.println("设备："+device);
					String newRssi=rssi+"";
					mDevListAdapter.addDevice(device,newRssi);
					if(isF==true){
						lv_bleList.setAdapter(mDevListAdapter);
					}


				}
			});
		}
	};

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	protected void onResume() {//打开APP时扫描设备
		super.onResume();
		scanLeDevice(true);
	}
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	protected void onPause() {//停止扫描
		super.onPause();
		scanLeDevice(false);
	}
	//按名称排序
	private void sortName(List<BluetoothDevice> mBle){
		str=new String[mBle.size()];
		newBleArray= new ArrayList<BluetoothDevice>();
		for(int i=0;i<mBle.size();i++){
			str[i]=mBle.get(i).getName();
		}
		Arrays.sort(str);
		for(int i=0;i<str.length;i++){
			System.out.println("排序后："+str[i]);
			for (int j=0;j<mBle.size();j++){
				if(str[i].equals(mBle.get(j).getName())){
					System.out.println("排序了："+mBle.get(j).getName());
					newBleArray.add(mBle.get(j));
				}
			}
		}
		if(newBleArray.size()==mBle.size()){
			mBleArray.clear();
			for(int i=0;i<newBleArray.size();i++){
				mBleArray.add(newBleArray.get(i));
			}
		}
	}
	//按信号强弱排序
	private void sortLevel(List<String> rssis){
		str=new String[rssis.size()];
		for (int i=0;i<rssis.size();i++){
			str[i]=rssis.get(i);
		}
		Arrays.sort(str);
		if(str.length==rssis.size()){
			rssis.clear();
			for(int i=0;i<str.length;i++){
				rssis.add(str[i]);
			}
		}
	}
//适配器
	class DeviceListAdapter extends BaseAdapter {

		private ViewHolder viewHolder;
		private String[]str=null;
		public DeviceListAdapter() {
		}
		public void addDevice(BluetoothDevice device, String rssi) {
			if(device.getName()!=null){
				if (!mBleArray.contains(device)) {
					mBleArray.add(device);
					rssis.add(rssi);
				}
				if(isSort==true){
					isLevel=false;
					sortName(mBleArray);
				}
				if(isLevel==true){
					isSort=false;
					sortLevel(rssis);
				}
			}
		}

		public void clear(){
			mBleArray.clear();
		}

		@Override
		public int getCount() {
			return mBleArray.size();
		}

		@Override
		public BluetoothDevice getItem(int position) {
			return mBleArray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(BluetoothActivity.this).inflate(
						R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_devName = (TextView) convertView
						.findViewById(R.id.device_name);
				viewHolder.itemId = (TextView) convertView
						.findViewById(R.id.itemId);
				viewHolder.tv_devAddress = (TextView) convertView
						.findViewById(R.id.device_address);
                viewHolder.device_uuid = (TextView) convertView
                        .findViewById(R.id.device_uuid);
				viewHolder.strong = (TextView) convertView
						.findViewById(R.id.strong);
				convertView.setTag(viewHolder);
			} else {
				convertView.getTag();
			}

			// add-Parameters
			BluetoothDevice device = mBleArray.get(position);
			String devName = device.getName();
			if (devName != null && devName.length() > 0) {
				viewHolder.tv_devName.setText("SSID："+devName);
			} else {
				viewHolder.tv_devName.setText("SSID: unknow_device"+(position+1));
			}
			viewHolder.tv_devAddress.setText("MAC： "+device.getAddress());
           viewHolder.device_uuid.setText("SN： ");
			viewHolder.itemId.setText(position+1+"");
			//System.out.println("号："+position);
			if(position<mBleArray.size()){
				//System.out.println("新号："+position);
				viewHolder.strong.setText(rssis.get(position)+"");
			}

			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_devName, tv_devAddress,itemId,device_uuid,strong;
	}

}