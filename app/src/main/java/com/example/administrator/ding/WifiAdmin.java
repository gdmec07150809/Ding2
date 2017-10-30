package com.example.administrator.ding;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WifiAdmin {
    //����һ��WifiManager����
    private WifiManager mWifiManager;
    //����һ��WifiInfo����
    private WifiInfo mWifiInfo;
    List WifiArray=null;
    String[] arraylist=null;
    //ɨ��������������б�
    private List<ScanResult> mWifiList;
    //���������б�
    private List<WifiConfiguration> mWifiConfigurations;
    WifiLock mWifiLock;
    public WifiAdmin(Context context){
        //ȡ��WifiManager����
        mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //ȡ��WifiInfo����
        mWifiInfo=mWifiManager.getConnectionInfo();
    }
    //��wifi
    public void openWifi(){
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }
    //�ر�wifi
    public void closeWifi(){
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
        }
    }
     // ��鵱ǰwifi״̬
    public int checkState() {
        return mWifiManager.getWifiState();
    }
    //����wifiLock
    public void acquireWifiLock(){
        mWifiLock.acquire();
    }
    //����wifiLock
    public void releaseWifiLock(){
        //�ж��Ƿ�����
        if(mWifiLock.isHeld()){
            mWifiLock.acquire();
        }
    }
    //����һ��wifiLock
    public void createWifiLock(){
        mWifiLock=mWifiManager.createWifiLock("test");
    }
    //�õ����úõ�����
    public List<WifiConfiguration> getConfiguration(){
        return mWifiConfigurations;
    }
    //ָ�����úõ������������
    public void connetionConfiguration(int index){
        if(index>mWifiConfigurations.size()){
            return ;
        }
        //�������ú�ָ��ID������
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }
    public void startScan(){
        mWifiManager.startScan();
        //�õ�ɨ����
        mWifiList=mWifiManager.getScanResults();
        //�õ����úõ���������
        mWifiConfigurations=mWifiManager.getConfiguredNetworks();
    }
    //�õ������б�
    public List<ScanResult> getWifiList(){
        //进行排序

        WifiArray = new ArrayList(mWifiList.size());
       arraylist=new String[mWifiList.size()];
       // System.out.println("_________________________________");
        for(int i=0;i<mWifiList.size();i++){
            //System.out.println(mWifiList.get(i).SSID);
            arraylist[i]=mWifiList.get(i).SSID;
        }
        Arrays.sort(arraylist);
        for(int i=0;i<arraylist.length;i++){
            for(int j=0;j<mWifiList.size();j++) {
                if (mWifiList.get(j).SSID.contains(arraylist[i])) {
                    WifiArray.add(mWifiList.get(j));

                }
            }
        }
        return WifiArray;
    }
    //�鿴ɨ����
    public StringBuffer lookUpScan(){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<mWifiList.size();i++){
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
             // ��ScanResult��Ϣת����һ���ַ�����
            // ���аѰ�����BSSID��SSID��capabilities��frequency��level
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }
    public String getMacAddress(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.getMacAddress();
    }
    public String getBSSID(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.getBSSID();
    }
    public int getIpAddress(){
        return (mWifiInfo==null)?0:mWifiInfo.getIpAddress();
    }
    //�õ����ӵ�ID
    public int getNetWordId(){
        return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();
    }
    //�õ�wifiInfo��������Ϣ
    public String getWifiInfo(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.toString();
    }
    //���һ�����粢����
    public void addNetWork(WifiConfiguration configuration){
        int wcgId=mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgId, true);
    }
    //�Ͽ�ָ��ID������
    public void disConnectionWifi(int netId){
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
}

