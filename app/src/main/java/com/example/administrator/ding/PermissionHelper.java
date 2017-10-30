package com.example.administrator.ding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by Sunday on 16/1/28.
 */
public class PermissionHelper {

    private Context mContext;
    public static final String PACKAGE = "package:";

    public PermissionHelper(Context context) {
        this.mContext = context;
    }

    /**
     * 判断权限集合
     *
     * @param permissions 检测权限的集合
     * @return 权限已全部获取返回true，未全部获取返回false
     */
    public boolean checkPermissions(String... permissions) {
        for (String permission : permissions) {
            if (!checkPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断权限是否获取
     *
     * @param permission 权限名称
     * @return 已授权返回true，未授权返回false
     */
    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 获取权限
     *
     * @param resultCode
     * @return
     */
    public void permissionsCheck(String permission, int resultCode) {
        // 注意这里要使用shouldShowRequestPermissionRationale而不要使用requestPermission方法
        // 因为requestPermissions方法会显示不在询问按钮
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permission)) {
            //如果用户以前拒绝过改权限申请，则给用户提示
            showMissingPermissionDialog();
        } else {
            //进行权限请求
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{permission},
                    resultCode);
        }
//        ActivityCompat.requestPermissions((Activity) mContext, new String[]{permission},resultCode);
    }


    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        startAppSettings();
    }

    // 启动应用的设置
    public void startAppSettings() {
        Toast.makeText(mContext,"请授予应用定位权限，否则程序可能不能正常运行", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE + mContext.getPackageName()));
        mContext.startActivity(intent);
    }
}
