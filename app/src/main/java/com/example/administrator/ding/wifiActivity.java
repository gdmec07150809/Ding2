package com.example.administrator.ding;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class wifiActivity extends Activity {

    private List<ScanResult> scanResults=null;
    private List<ScanResult> NameScanResults=null;
    private List<ScanResult> LevelScanResults=null;
    private ListView listView;
    private WifiAdapter wifiAdapter;
    private Button Manual_btn, automatic, exit_btn;

    private ImageView setting_back, Lup;

    static List<String> BbsidList=new ArrayList<>();
    public static final int SHOW_RESPONSE = 0;
    //权限检测类
    private PermissionHelper mPermissionHelper;

    public static final int ACCESS_FINE_LOCATION_CODE = 1;//SDcard权限
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    public static final int ACCESS_COARSE_LOCATION_CODE = 2;//SDcard权限
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public static final int ACCESS_WIFI_STATE_CODE = 3;//SDcard权限
    public static final String ACCESS_WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;
    private WifiManager wifiManager;
    private TextView add_machine,fan;
    String start_time;
    int time;
    String keep;
    String language;
    Double [] levels=null;
    String [] names=null;
    boolean isFlag=true;
    boolean isF=true;
    String name, id, locationName, locationId;
    private Spinner Right;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private String on_activity="wifi";
    private long clickTime=0;
    private String biao="1";
    boolean isShow=true;
    private static final String fileName = "sharedfile";//定义保存的文件的名称
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent(wifiActivity.this,MainActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("keep",keep);
            bundle.putString("time",start_time);
            bundle.putString("lang",language);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = this.getIntent().getExtras();
        //获取传过来的屏幕参数
         keep=bundle.getString("keep");

        //获取传过来的语言参数
        language=bundle.getString("lang");
        //获取传过来的时间参数
        start_time= bundle.getString("time");
        time= Integer.parseInt(start_time);
        //获取缓存
        SharedPreferences share1 = super.getSharedPreferences(fileName,
                MODE_PRIVATE);
        biao=share1.getString("biao",1+"");
        //判断是否让屏幕常亮
        if(keep.equals("true")){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        add_machine=findViewById(R.id.add_machine);
        setting_back=findViewById(R.id.settings_back);
        fan=findViewById(R.id.fan);
       // left=findViewById(R.id.left);
        Right=findViewById(R.id.Right);
        Manual_btn = (Button) findViewById(R.id.Manual);
        automatic = (Button) findViewById(R.id.automatic);
        automatic.setBackgroundResource(R.drawable.button_shape_normal);
        Manual_btn.setBackgroundResource(R.drawable.button_shape_active);
       // Lup=findViewById(R.id.Lup);
        //Lup.setBackgroundResource(R.drawable.up);
        exit_btn = (Button) findViewById(R.id.exit);
        //判断是否用英文
        if(language.equals("english")){
            add_machine.setText("Device List");

          //  left.setText("Sort by name");
          //  Right.setText("Sort by level");
            Manual_btn.setText("Manual");
            automatic.setText("Automatic");
            fan.setText("Back");
            exit_btn.setText("Exit");
        }

        mPermissionHelper = new PermissionHelper(this);





        //添加返回按钮的监听
        setting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(wifiActivity.this,MainActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("keep",keep);
                bundle.putString("time",start_time);
                bundle.putString("lang",language);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行
            }
        });

        //wifi管理器
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            openWifi();
        }
        listView = (ListView) findViewById(R.id.listView);
        scanResults = new ArrayList<>();//WIFI列表
        HashSet h = new HashSet(scanResults);
        scanResults.clear();
        scanResults.addAll(h);
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
                        TextView tv3=(TextView) view;
                        tv3.setTextSize(18.0f); //设置大小
                        if(isShow==false){
                            biao="2";
                            nameSort();
                        }

                        break;
                    case 1:
                        TextView tv2=(TextView) view;
                        tv2.setTextSize(18.0f);
                        biao="3";
                        //设置大小
                        isShow=false;
                        informationSort();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if(biao.equals("1")||biao.equals("2")){
            Right.setSelection(0);
        }else{
            Right.setSelection(1);
        }
        if(isFlag){
            one_handler.sendEmptyMessage(1);
        }

        //按名称排序事件
//        left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nameSort();
//            }
//        });

         Manual_btn.setClickable(true);

        //手动搜索按钮事件
        Manual_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(wifiActivity.this, "手动搜索", Toast.LENGTH_SHORT).show();
                Manual_btn.setBackgroundResource(R.drawable.button_shape_normal);
                automatic.setBackgroundResource(R.drawable.button_shape_active);
                isFlag=false;
                star_handler.sendEmptyMessageDelayed(1, 100);

            }
        });
        //自动搜索按钮事件
        automatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                automatic.setBackgroundResource(R.drawable.button_shape_normal);
                Manual_btn.setBackgroundResource(R.drawable.button_shape_active);
               // Toast.makeText(wifiActivity.this, "自动搜索", Toast.LENGTH_SHORT).show();
                isFlag=true;
                one_handler.sendEmptyMessageDelayed(1, time*1000);
            }
        });
       // 退出程序事件
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            HashSet h = new HashSet(scanResults);
            scanResults.clear();
            scanResults.addAll(h);
            wifiAdapter = new WifiAdapter(wifiActivity.this, scanResults);
            Log.e("@@@", "refresh");
            listView.setAdapter(wifiAdapter);
        }
    };
    //打开wifi
    private void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }
    //按强弱方法
    private void informationSort(){
        //储存缓存刷新时间
        SharedPreferences share = super.getSharedPreferences(fileName, MODE_PRIVATE);//实例化
        SharedPreferences.Editor editor = share.edit(); //使处于可编辑状态
        editor.putString("timeStr", time+"");
        editor.putString("biao", biao+"");
        editor.putString("keep", keep+"");
        editor.commit();    //提交数据保存
        HashSet h = new HashSet(scanResults);
        scanResults.clear();
        scanResults.addAll(h);
        Collections.sort(scanResults, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult scanResult, ScanResult t1) {
                return t1.level-scanResult.level;
            }
        });
        wifiAdapter = new WifiAdapter(wifiActivity.this, scanResults);
        listView.setAdapter(wifiAdapter);
    }
    //按名称排序方法
    private void nameSort(){
        //储存缓存刷新时间
        SharedPreferences share = super.getSharedPreferences(fileName, MODE_PRIVATE);//实例化
        SharedPreferences.Editor editor = share.edit(); //使处于可编辑状态
        editor.putString("timeStr", time+"");
        editor.putString("biao", biao+"");
        editor.putString("keep", keep+"");
        editor.commit();    //提交数据保存
        NameScanResults = new ArrayList<>();//WIFI列表
        scanResults = getAllNetWorkList(wifiActivity.this);
        HashSet h = new HashSet(scanResults);
        scanResults.clear();
        scanResults.addAll(h);
        Collections.sort(scanResults, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult scanResult, ScanResult t1) {
                return scanResult.SSID.compareTo(t1.SSID);
            }
        });
        wifiAdapter = new WifiAdapter(wifiActivity.this, scanResults);
        listView.setAdapter(wifiAdapter);
    }

    boolean pression = false;
    private Handler star_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HashSet h = new HashSet(scanResults);
            scanResults.clear();
            scanResults.addAll(h);
            if(biao.equals("2")||biao.equals("1")){
                Collections.sort(scanResults, new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult scanResult, ScanResult t1) {
                        return scanResult.SSID.compareTo(t1.SSID);
                    }
                });
            }else if(biao.equals("3")){
                Collections.sort(scanResults, new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult scanResult, ScanResult t1) {
                        return t1.level-scanResult.level;
                    }
                });
            }
                wifiAdapter = new WifiAdapter(wifiActivity.this, scanResults);
                listView.setAdapter(wifiAdapter);

//            scanResults = getAllNetWorkList(wifiActivity.this);
//            star_handler.postDelayed(r, 100);
        }
    };

    //每隔1000*60*60时间刷新一次wifi列表
    private Handler one_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            wifiAdapter=null;
           scanResults = getAllNetWorkList(wifiActivity.this);
            HashSet h = new HashSet(scanResults);
            scanResults.clear();
            scanResults.addAll(h);
            if(biao.equals("2")||biao.equals("1")){
                Collections.sort(scanResults, new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult scanResult, ScanResult t1) {
                        return scanResult.SSID.compareTo(t1.SSID);
                    }
                });
            }else if(biao.equals("3")){
                Collections.sort(scanResults, new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult scanResult, ScanResult t1) {
                        return t1.level-scanResult.level;
                    }
                });
            }
            if(isFlag==true){
                wifiAdapter = new WifiAdapter(wifiActivity.this,scanResults);
                listView.setAdapter(wifiAdapter);
               // one_handler.postDelayed(r, 100);
                one_handler.sendEmptyMessageDelayed(1, time*1000);
            }
        }
    };

    //扫描wifi
    public static List<ScanResult> getAllNetWorkList(Context context) {
        WifiAdmin mWifiAdmin = new WifiAdmin(context);
        // 开始扫描网络
        mWifiAdmin.startScan();

        for (int i=0;i<mWifiAdmin.getWifiList().size();i++){
            BbsidList.add(mWifiAdmin.getWifiList().get(i).BSSID);
            //System.out.println("20秒获取： "+mWifiAdmin.getWifiList().get(i).BSSID);
        }
        return mWifiAdmin.getWifiList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作
                    //判断权限授权状态
                } else {
                    //如果请求失败
                    Toast.makeText(getApplicationContext(),"权限缺失，程序可能不能正常运行",Toast.LENGTH_SHORT).show();
                }
                break;
            case ACCESS_COARSE_LOCATION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作

                } else {
                    //如果请求失败
                    Toast.makeText(getApplicationContext(),"权限缺失，程序可能不能正常运行",Toast.LENGTH_SHORT).show();
                    mPermissionHelper.startAppSettings();
                }
                break;
            case ACCESS_WIFI_STATE_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作
                    //five_handler.sendEmptyMessage(1);
                    one_handler.sendEmptyMessage(1);
                    pression = true;
                } else {
                    //如果请求失败
                    Toast.makeText(getApplicationContext(),"权限缺失，程序可能不能正常运行",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void starWifi(){
        //判断权限授权状态
        boolean b = mPermissionHelper.checkPermission(ACCESS_FINE_LOCATION);
        //如果没有获取到权限,则尝试获取权限
        if (!b) {
            mPermissionHelper.permissionsCheck(ACCESS_FINE_LOCATION,ACCESS_FINE_LOCATION_CODE);
        } else {
            //如果请求成功，则进行相应的操作
            b = mPermissionHelper.checkPermission(ACCESS_COARSE_LOCATION);
            //如果没有获取到权限,则尝试获取权限
            if (!b) {
                mPermissionHelper.permissionsCheck(ACCESS_COARSE_LOCATION,ACCESS_COARSE_LOCATION_CODE);
            } else {
                //如果请求成功，则进行相应的操作
                b = mPermissionHelper.checkPermission(ACCESS_WIFI_STATE);
                //如果没有获取到权限,则尝试获取权限
                if (!b) {
                    mPermissionHelper.permissionsCheck(ACCESS_WIFI_STATE,ACCESS_WIFI_STATE_CODE);
                } else {
                    //如果请求成功，则进行相应的操作
                    //five_handler.sendEmptyMessage(1);
                    one_handler.sendEmptyMessage(1);
                    pression = true;
                }
            }
        }
    }


    boolean indexPression = false;

    @Override
    protected void onPause() {

        super.onPause();
        indexPression = pression;
        pression = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        pression = indexPression;
    }
}
