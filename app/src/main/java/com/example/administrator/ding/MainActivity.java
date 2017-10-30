package com.example.administrator.ding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private TextView come,add_machine,fan,interval,screen,ding_new,label;
    private RadioGroup group,language_group;
    String CheckButton="WIFI";
    private CheckBox ch_box;
    String keep="false";
    private RadioButton btn1,btn2,btn3,btn4,btn5,btn6,china;
    private EditText edittime;
    private ImageView setting_back;
    String language="china";
    String lang;
    String time;
    private static final String fileName = "sharedfile";// 定义保存的文件的名称
    private long clickTime=0;
    //重写onKeyDown方法,实现双击退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再次点击退出",  Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            Log.e(TAG, "exit application");
            this.finish();
            System.exit(0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set);
        come= (TextView) findViewById(R.id.come);
        group = (RadioGroup) findViewById(R.id.group);
        btn1= (RadioButton) findViewById(R.id.button1);
        btn2= (RadioButton) findViewById(R.id.button2);
        btn3= (RadioButton) findViewById(R.id.button3);
        btn4= (RadioButton) findViewById(R.id.button4);
        btn5= (RadioButton) findViewById(R.id.button5);
        btn6= (RadioButton) findViewById(R.id.button6);
        come= (TextView) findViewById(R.id.come);
        label= (TextView) findViewById(R.id.label);
        interval= (TextView) findViewById(R.id.interval);
        language_group = (RadioGroup) findViewById(R.id.language_group);
        ch_box= (CheckBox) findViewById(R.id.ch_box);
        edittime= (EditText) findViewById(R.id.edittime);
        add_machine= (TextView) findViewById(R.id.add_machine);
        screen= (TextView) findViewById(R.id.screen);
        ding_new= (TextView) findViewById(R.id.ding_new);
        china= (RadioButton) findViewById(R.id.china);
        //获取缓存
        SharedPreferences share = super.getSharedPreferences(fileName,
                MODE_PRIVATE);
        edittime.setText( share.getString("timeStr",10+""));// 如果没有值，则显示“10”
        keep=share.getString("keep",false+"");
        if(keep.equals("true")){
            ch_box.setChecked(true);
        }
        //  判断是否用英文显示
        if(getIntent().getStringExtra("lang")!=null){
            lang=getIntent().getStringExtra("lang");
            if(lang.equals("english")){
                come.setText("confirm");
                btn4.setText("Rfid(need NFC)：");
                btn5.setText("ZigBee(need ZigBee)：");
                btn6.setText("QR/Bar Code：");
                add_machine.setText("Setting");
                interval.setText("Search interval");
                screen.setText("The screen is always on");
                china.setText("Chinese：");
                ding_new.setText("ding_new v0.1");
                label.setText("Tag format");
            }
        }

//确定事件
        come.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckButton.equals("WIFI")){
                    Intent intent=new Intent(MainActivity.this,wifiActivity.class);
                    Bundle bundle=new Bundle();
                    time=edittime.getText().toString();
                    bundle.putString("keep",keep);
                    bundle.putString("time",time);
                    bundle.putString("lang",language);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行
                }else if(CheckButton.equals("二维码/条形码")){
                    Intent intent=new Intent(MainActivity.this,codeActivity.class);
                    Bundle bundle=new Bundle();
                    time=edittime.getText().toString();
                    bundle.putString("keep",keep);
                    bundle.putString("lang",language);
                    bundle.putString("time",time);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行
                }else if(CheckButton.equals("Bluetooth")){
                    Intent intent=new Intent(MainActivity.this,BluetoothActivity.class);
                    Bundle bundle=new Bundle();
                    time=edittime.getText().toString();
                    bundle.putString("keep",keep);
                    bundle.putString("lang",language);
                    bundle.putString("time",time);
                    intent.putExtras(bundle);
                    startActivity(intent);
                   android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行
                } else{
                    Toast.makeText(MainActivity.this,"抱歉此功能暂未开发",Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 单选按钮组监听事件
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 根据ID判断选择的按钮
                if (checkedId == R.id.button1) {
                    CheckButton="GPRS";
                } else if (checkedId == R.id.button2) {
                    CheckButton="WIFI";
                }else if (checkedId == R.id.button3) {
                    CheckButton="Bluetooth";
                }else if (checkedId == R.id.button4) {
                    CheckButton="Rfid";
                }else if (checkedId == R.id.button5) {
                    CheckButton="ZigBee";
                }else if (checkedId == R.id.button6) {
                    CheckButton="二维码/条形码";
                }
            }
        });
        // 单选按钮组监听事件
        language_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 根据ID判断选择的按钮
                if (checkedId == R.id.china) {
                    language="china";
                } else if (checkedId == R.id.english) {
                    language="english";
                }
            }
        });
        //屏幕常亮复选框事件
        ch_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    keep="true";
                    Toast.makeText(MainActivity.this,"保持常亮",Toast.LENGTH_SHORT).show();
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }else {
                    keep="false";
                    Toast.makeText(MainActivity.this,"取消常亮",Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });
    }
}
