package com.example.wi_fiscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.Manifest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public WifiManager wifiManager;
    public List<ScanResult> wifiList;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int INITIAL_REQUEST = 1337;
    private static final int REQUEST_ACCESS_WIFI = INITIAL_REQUEST + 4;
    boolean scanFinished = false;
    private final Handler handler = new Handler();
    private BroadcastReceiver wifiScanReceiver;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv);
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        detectWifi();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String text = (String) ((TextView)view).getText();
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                wifiList = wifiManager.getScanResults();
                for (int i = 0; i<wifiList.size(); i++) {
                    String item = wifiList.get(i).toString();
                    String[] vector_item = item.split(",");
                    String item_essid = vector_item[0];
                    String ssid = item_essid.split(": ")[1];
                    if (ssid.equals(text)&& position==i)
                    {
                        intent.putExtra("result", item);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
    }

    public void start()
    {
        handler.postDelayed(new Runnable() {
            public void run()
            {
                if(!scanFinished)
                {
                    wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if(wifiManager.isWifiEnabled()==false)
                    {
                        wifiManager.setWifiEnabled(true);
                        Toast.makeText(MainActivity.this, "Wi-Fi enabled" , Toast.LENGTH_LONG).show();
                    }
                    wifiScanReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context c, Intent intent) {
                            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                            if (success) {
                                Log.d("scanSuccess", "scan successful");
                                wifiList = wifiManager.getScanResults();
                                String result=wifiList.toString();
                                Log.d("WiFiData", result);
                                list.clear();
                                for (int i = 0; i<wifiList.size(); i++) {
                                    String item = wifiList.get(i).toString();
                                    String[] vector_item = item.split(",");
                                    String item_essid = vector_item[0];
                                    String ssid = item_essid.split(": ")[1];
                                    list.add(ssid);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.d("scanFailed", "scan failed");
                                wifiList = wifiManager.getScanResults();
                                String result=wifiList.toString();
                                Log.d("WiFiData", result);
                                //Toast.makeText(MainActivity.this,result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    boolean success = wifiManager.startScan();
                    if (!success) {
                        Log.d("startScan", "startScan failed");
                    }
                }
            }
        }, 10);

    }

    @Override
    protected void onPause()
    {
        try{
            unregisterReceiver(wifiScanReceiver);
        }
        catch(Exception e)
        {}
        scanFinished = true;
        super.onPause();
    }
    @Override
    protected void onResume()
    {
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        scanFinished=false;
        start();
        super.onResume();
    }
    @Override
    protected void onStop()
    {
        try{
            unregisterReceiver(wifiScanReceiver);
        }
        catch(Exception e)
        {}
        scanFinished = true;
        super.onStop();
    }
    @Override
    protected void onDestroy()
    {
        try{
            unregisterReceiver(wifiScanReceiver);
        }
        catch(Exception e)
        {}
        scanFinished = true;
        super.onDestroy();
    }

    public void detectWifi(){
        if (!AccessWiFiState()|| !ChangeWiFiState() || !AccessInternet() || !AccessCL() || !AccessFL()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("Permissions", "Access\n");
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }
        //проверка разрешений
        int [] res={};
        onRequestPermissionsResult(REQUEST_ACCESS_WIFI, INITIAL_PERMS, res);
        Toast.makeText(this,"Scanning Wi-Fi...", Toast.LENGTH_LONG).show();
        start();
    }
    private boolean AccessWiFiState() {
        Log.d("AccessWiFiState", "Access\n");
        return (grantPermission(Manifest.permission.ACCESS_WIFI_STATE));
    }
    private boolean ChangeWiFiState() {
        Log.d("ChangeWiFiState", "Access\n");
        return (grantPermission(Manifest.permission.CHANGE_WIFI_STATE));
    }
    private boolean AccessInternet() {
        Log.d("AccessInternet", "Access\n");
        return (grantPermission(Manifest.permission.INTERNET));
    }
    private boolean AccessCL() {
        Log.d("AccessCL", "Access\n");
        return (grantPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }
    private boolean AccessFL() {
        Log.d("AccessFL", "Access\n");
        return (grantPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean grantPermission(String perm) {
        Log.d("grantPermission", "HELLO!\n");
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_WIFI:
                if (AccessWiFiState() && ChangeWiFiState() && AccessInternet() && AccessCL() && AccessFL()) {
                    Toast.makeText(this,"Permissions granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "The app was not allowed to analyze WiFi network. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}