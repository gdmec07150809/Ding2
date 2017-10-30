package com.example.administrator.ding;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */
public class WifiAdapter extends BaseAdapter {

    private Context context;
    private ViewHolder holder;
    private List<ScanResult> scanResults = new ArrayList<>();

    public WifiAdapter(Context context, List<ScanResult> scanResults){
        this.context = context;
        holder = new ViewHolder();
        this.scanResults = scanResults;
    }

    @Override
    public int getCount() {
        return scanResults.size();
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
        if (contentView == null ){
            contentView = LayoutInflater.from(context).inflate(R.layout.item,viewGroup,false);
            holder.num_text = (TextView) contentView.findViewById(R.id.num_text);
            holder.ssid_text = (TextView) contentView.findViewById(R.id.ssid_text);
            holder.mac_text = (TextView) contentView.findViewById(R.id.mac_text);
            holder.SN = (TextView) contentView.findViewById(R.id.SN);
           // holder.du = (TextView) contentView.findViewById(R.id.du);
            holder.wifi_png = (ImageView) contentView.findViewById(R.id.wifi_png);
            holder.strong = (TextView) contentView.findViewById(R.id.strong);
            contentView.setTag(holder);
        }else{
            holder = (ViewHolder) contentView.getTag();
        }
        int num = i+1;
        holder.num_text.setText(num+"");
        holder.ssid_text.setText("SSID: "+scanResults.get(i).SSID);
        holder.mac_text.setText("MAC: "+scanResults.get(i).BSSID);
        holder.SN.setText("S   N: "+"");
        int level =  scanResults.get(i).level;
       // holder.du.setText("强 度:"+Math.abs(level));
        holder.strong.setText(""+level);
        if((Math.abs(level))<=60){
            holder.wifi_png.setBackgroundResource(R.drawable.wifi_five);
        }else if(Math.abs(level)<=70){
            holder.wifi_png.setBackgroundResource(R.drawable.wifi_four);
        }else if(Math.abs(level)<=80){
            holder.wifi_png.setBackgroundResource(R.drawable.wifi_three);
        }else{
            holder.wifi_png.setBackgroundResource(R.drawable.wifi_two);
        }
        return contentView;
    }

    private class ViewHolder{
        TextView num_text;
        TextView ssid_text;
        TextView mac_text;
        TextView SN;
        TextView du,strong;
        ImageView wifi_png;
    }

}
