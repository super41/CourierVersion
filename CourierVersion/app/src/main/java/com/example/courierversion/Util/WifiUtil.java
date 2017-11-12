package com.example.courierversion.Util;

/**
 * Created by XJP on 2017/11/8.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.example.courierversion.R;

import java.util.List;

/**
 * Created by Administrator on 2016/8/28.
 */
public class WifiUtil {

    public WifiManager mWifiManager;

    private WifiInfo mWifiInfo;

    private List<ScanResult> mWifiList;

    private List<WifiConfiguration> mWificonfiguration;

    private WifiManager.WifiLock mWifiLock;

    private Context context;

    public WifiUtil(Context context) {
        this.context=context;
        mWifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public boolean openWifi() {
        boolean result=true;
        if (!mWifiManager.isWifiEnabled()) {
            return mWifiManager.setWifiEnabled(true);
        }
        return  result;
    }
    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener) {
        String title = context.getResources().getString(R.string.permission_apply_title);
        String cancel=context.getResources().getString(R.string.permission_cancel);
        String setting=context.getResources().getString(R.string.permission_setting);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(setting, okListener)
                .setNegativeButton(cancel, null)
                .create()
                .show();
    }

    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public void acquireWifiLoc() {
        mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        if(mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    public List<WifiConfiguration> getConfigurations () {
        return  mWificonfiguration;
    }

    public Boolean connectConfiguration(int index) {

//        if(index > mWificonfiguration.size()) {
//            return;
//        }
        mWifiManager.enableNetwork(index, true);
        mWifiManager.saveConfiguration();
        mWifiManager.reconnect();

        return true;
    }

    public void startScan() {
        mWifiManager.startScan();
        mWifiList = mWifiManager.getScanResults();
        mWificonfiguration = mWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getmWifiList() {
        return mWifiList;
    }

    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + String.valueOf(i+1)+":");
            stringBuilder.append(mWifiList.get(i).toString());
            stringBuilder.append("/n");
        }
        return  stringBuilder;
    }

    public String getMacAddress() {
        return  (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getSSID() {
        return  (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();

    }

    public  String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    public boolean  connect(WifiConfiguration wifiConfiguration) {

        if (!this.openWifi()) {
            String msg=context.getResources().getString(R.string.permission_wifi_hint);
            showMessageOKCancel((Activity) context, msg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            });
            return false;
        }
        Log.d("xjp", "connect: loop...");
        while (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED ){
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
                Log.d("xjp", "connect: loop...");
            } catch (InterruptedException ie) {
            }
        }

        int wcgID = mWifiManager.addNetwork(wifiConfiguration);
        Log.e("wcgID", wcgID+"true");
        mWifiManager.enableNetwork(wcgID, true);
        mWifiManager.saveConfiguration();
        mWifiManager.reconnect();
        Log.d("xjp", "connect: finnal");
        return  true;

    }

    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedGroupCiphers.clear();
        configuration.allowedKeyManagement.clear();
        configuration.allowedPairwiseCiphers.clear();
        configuration.allowedProtocols.clear();
        configuration.SSID = "\"" + SSID + "\"";

      /*  WifiConfiguration tempConfig = this.isExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }*/

        switch (Type) {
            case 1:
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//                configuration.status = WifiConfiguration.Status.ENABLED;
                break;
            case 2:
                configuration.hiddenSSID = false;
                configuration.wepKeys[0] = "\"" + Password +"\"";
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                break;
            case 3:

                configuration.preSharedKey = "\"" + Password + "\"";
                configuration.hiddenSSID = false;
                // configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                configuration.status = WifiConfiguration.Status.ENABLED;
                break;
        }
        return  configuration;
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig:
                existingConfigs) {
            if (existingConfig.SSID.equals("\"" +SSID+"\"")) {
                return  existingConfig;
            }

        }
        return null;
    }




}
