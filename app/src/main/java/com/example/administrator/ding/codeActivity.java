package com.example.administrator.ding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.administrator.ding.R.id.device_name;
import static com.example.administrator.ding.R.id.listView;
import static com.example.administrator.ding.R.id.time;

/**
 * Created by Administrator on 2017/10/8.
 */

public class codeActivity extends Activity {
    private ImageView set;
    private TextView add_machine,code,fan;
    private Spinner Right;
    private Button sao_btn, clean_btn, exit_btn;
    private List<String> resultlist=null;
    private List<String> newResult=null;
    String language,str,keep;
    private ListView listView;
    private BaseAdapter codeAdapter;
    TextView resultNew;
    String name, id, locationName, locationId;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    String on_activity="code";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code);

         language=getIntent().getStringExtra("lang");
        add_machine=findViewById(R.id.add_machine);
        Right=findViewById(R.id.Right);
        sao_btn=findViewById(R.id.sao);
        clean_btn=findViewById(R.id.clean);
        set=findViewById(R.id.settings_back);
        fan=findViewById(R.id.fan);
        listView=findViewById(R.id.listView);
        resultlist=new ArrayList<String>();//储存扫描到的数据
        newResult=new ArrayList<String>();//储存不重复list
        if(getIntent().getStringArrayListExtra("result")!=null){
            newResult = getIntent().getStringArrayListExtra("result");
            for(int i=0;i<newResult.size();i++){
                System.out.println("转:"+newResult.get(i));
            }
            codeAdapter= new codeAdapter(codeActivity.this,newResult);
            listView.setAdapter(codeAdapter);
        }
        //code=findViewById(R.id.code);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent=new Intent(codeActivity.this,editActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString("keep",keep);
//                bundle.putString("time",time+"");
//                bundle.putString("lang",language);
//                bundle.putString("id",id);
//                bundle.putString("name",name);
//                bundle.putString("locationName",locationName);
//                bundle.putString("locationId",locationId);
//                bundle.putString("on",on_activity);
//                bundle.putString("device_name",newResult.get(i));
//                intent.putExtras(bundle);
//                intent.putStringArrayListExtra("result", (ArrayList<String>) newResult);
//                startActivity(intent);
//            }
//        });
        // one_min_btn = (Button) findViewById(R.id.one_min_btn);
        exit_btn=findViewById(R.id.exit);
        //判断是否英文显示
        if(language.equals("english")){
            add_machine.setText("Device List");
           // left.setText("Positive Sequence");
            // Right.setText("Reverse");
            exit_btn.setText("Exit");
            sao_btn.setText("Scan");
            clean_btn.setText("clean");
            fan.setText("back");
           // code.setText("QR/Bar Code");
        }
        //获取传来的复选框状态，是否需要保持常亮
        keep=getIntent().getStringExtra("keep");
        if(keep.equals("true")){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        //返回事件
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(codeActivity.this,MainActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("lang",language);
                bundle.putString("keep",keep);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
               android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行
            }
        });
        //退出按钮事件
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
        //扫一扫事件
        sao_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator=new IntentIntegrator(codeActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                clean_btn.setBackgroundResource(R.drawable.button_shape_active);
                integrator.setPrompt("扫描二维码/条形码");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.initiateScan();
            }
        });

        //清空按钮事件
        clean_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newResult.clear();
                resultlist.clear();
                clean_btn.setBackgroundResource(R.drawable.button_shape_normal);
                codeAdapter= new codeAdapter(codeActivity.this,resultlist);
                listView.setAdapter(codeAdapter);
            }
        });

        //数据
        data_list = new ArrayList<String>();
        if(language.equals("english")){
            data_list.clear();
            data_list.add("Alphabetical positive sequence");
            data_list.add("Reverse order by letters");
        }else{
            data_list.clear();
            data_list.add("按字母正序排序");
            data_list.add("按字母倒序排序");
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
                        TextView tv=(TextView) view;
                        tv.setTextSize(18.0f); //设置大小
                        String[] results=new String[newResult.size()];
                        for(int k=0;k<newResult.size();k++){
                            results[k]=newResult.get(k);
                        }
                        Arrays.sort(results);
                        newResult.clear();
                        for (int j=0;j<results.length;j++){
                            newResult.add(results[j]);
                        }
                        codeAdapter= new codeAdapter(codeActivity.this,newResult);
                        listView.setAdapter(codeAdapter);
                        break;
                    case 1:
                        TextView tv1=(TextView) view;
                        tv1.setTextSize(18.0f); //设置大小
                        String[] resultss=new String[newResult.size()];
                        for(int k=0;k<newResult.size();k++){
                            resultss[k]=newResult.get(k);
                        }
                        Arrays.sort(resultss);
                        newResult.clear();
                        for (int j=resultss.length-1;j>=0;j--){
                            if(!resultss[j].isEmpty()){
                                newResult.add(resultss[j]);
                            }

                        }
                        codeAdapter= new codeAdapter(codeActivity.this,newResult);
                        listView.setAdapter(codeAdapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent=new Intent(codeActivity.this,MainActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("lang",language);
        bundle.putString("keep",keep);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //处理返回的数据
        if(str!=null){
            System.out.println("返回："+str);
            resultlist.add(str+"");

                for(int i=0;i<resultlist.size();i++){
                    if(!newResult.contains(resultlist.get(i))){
                        newResult.add(resultlist.get(i));
                    }
                }
                codeAdapter= new codeAdapter(codeActivity.this,newResult);
                listView.setAdapter(codeAdapter);

        }


    }
    //适配器
    class  codeAdapter extends BaseAdapter{
        private Context context;

        private List<String> stringList;
        public codeAdapter(codeActivity codeActivity, List<String> resultlist) {
            this.context=codeActivity;
            this.stringList=resultlist;
        }

        @Override
        public int getCount() {
            return stringList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View contentView = null;
            TextView result = null;
            TextView itemId=null;
            if(contentView==null){
                contentView= LayoutInflater.from(context).inflate(R.layout.code_item,viewGroup,false);
                result = (TextView) contentView.findViewById(R.id.result);
               itemId = (TextView) contentView.findViewById(R.id.itemId);
               
            }
           result.setText(stringList.get(i));
            itemId.setText((i+1)+"");
            return contentView;
        }
    };
   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result.getContents()==null){
            Toast.makeText(this,"扫描失败",Toast.LENGTH_SHORT).show();
            str=null;
        }else{
            resultNew=findViewById(R.id.result);

            str=result.getContents();
            //resultNew.setText("扫描结果："+result.getContents());
        }
    }
}
