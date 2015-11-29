package com.b1g0n3.mycamremote;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.mafro.android.wakeonlan.MagicPacket;
import com.b1g0n3.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    String Version = "2.0c";
    private ProgressBar progressBar;
    private TextView textView2,version;
    private ImageView imageView1,imageView2,imageView3,imageView4;
    WifiManager mainWifi;
    WifiReceiver receiverWifi,receiverGopro;
    StringBuilder sb = new StringBuilder();
    List<ScanResult> wifiList;
    String oldssid;
    String[] ssid = new String[4];
    String[] bssid = new String[4];
    String[] cameraType = new String[4];
    String[] Api = new String[4];
    String[] BssidGopro = {"D4:D9:19","D8:96:85","F4:DD:9E","D6:D9:19","F6:DD:9E","04:E6:76"};
    String[] BssidApi = {"3","3","4","4","4","1"};
    String[] BssidModel = {"Gopro3","GoPro3","Gopro4Silver","GoPro4Black","Gopro4s","Xiaomi Yi"};
    public Button Button1,Button2,Button3,Button4;
    public Button ButtonRefresh, ButtonSettings,ButtonHelp;
    int CameraCount, resourceId1, resourceId2, boucle;
    String ssidCible,bssidCible;
    String ip = "10.5.5.9";
    public static final char SEPARATOR = ':';
    ProgressDialog dialog;
    static String GoproPassword = "";
    String goproModel = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dialog = new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView2 = (TextView) findViewById(R.id.textStatusCamera);
        Button1 = (Button)findViewById(R.id.button1);
        Button2 = (Button)findViewById(R.id.button2);
        Button3 = (Button)findViewById(R.id.button3);
        Button4 = (Button)findViewById(R.id.button4);
        ButtonRefresh = (Button)findViewById(R.id.buttonRefresh);
        ButtonSettings = (Button)findViewById(R.id.buttonSettings);
        ButtonHelp = (Button)findViewById(R.id.ButtonHelp);
        version=(TextView) findViewById(R.id.version);
        version.setText(Version);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        String icon_green = "@drawable/gopro_icon_green";
        String icon_grey = "@drawable/gopro_icon_grey";
        Resources res = getResources();
        resourceId1 = res.getIdentifier(icon_green, "drawable", getPackageName() );
        resourceId2 = res.getIdentifier(icon_grey, "drawable", getPackageName() );
        progressBar.setProgress(0);
        tools.out("---------------------------------- Start of use");
        ButtonRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ScanCamera();
            }
        });
    }

    protected void ScanCamera() {
        Button1.setClickable(false);Button2.setClickable(false);Button3.setClickable(false);Button4.setClickable(false);
        Button1.setEnabled(false);Button2.setEnabled(false);Button3.setEnabled(false);Button4.setEnabled(false);
        imageView1.setImageResource(resourceId2); imageView2.setImageResource(resourceId2);
        imageView3.setImageResource(resourceId2); imageView4.setImageResource(resourceId2);
        // Initiate wifi service manager
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // Check for wifi is disabled
        if (!mainWifi.isWifiEnabled())
        {
            Toast.makeText(getApplicationContext(), "Trying to enable wifi..", Toast.LENGTH_LONG).show();
            mainWifi.setWifiEnabled(true);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isConnected()) {
            WifiInfo wifiInfo=mainWifi.getConnectionInfo();
            oldssid=wifiInfo.getSSID();
            tools.out("ancien wifi connecté :" + oldssid);
            boucle=0;
        } else {
            boucle= 1;
            tools.out("pas de wifi connecté");
        }
        new RefreshWifiTask().execute();

        ButtonSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GlobalSettingsActivity.class));
                overridePendingTransition(R.anim.slideup_in, R.anim.slideup_out);
            }
        });

        ButtonHelp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                overridePendingTransition(R.anim.slideup_in, R.anim.slideup_out);
            }
        });

        Button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ssidCible=ssid[0];
                bssidCible=bssid[0];
                ConnectGopro();
            }
        });
        Button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ssidCible = ssid[1];
                bssidCible = bssid[1];
                ConnectGopro();
            }
        });
        Button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ssidCible = ssid[2];
                bssidCible = bssid[2];
                ConnectGopro();
            }
        });
        Button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ssidCible=ssid[3];
                bssidCible=bssid[3];
                ConnectGopro();
            }
        });
    }

    public Integer getModel(String bssid) {
        Integer model = 0;
        for(int i = 0; i < BssidGopro.length; i++){
            if (BssidGopro[i].contains(bssid.substring(0,8).toUpperCase()) )  {
                tools.out("MainActivity: " + "i=" + i + " BssidGopro[i]=" + BssidGopro[i] + " bingo...");
                model=i;
            }
        }
        tools.out("MainActivity: model=" + model);
        return model;
    }

    protected void ConnectGopro() {
        dialog.setCancelable(true);
        dialog.setMessage("Connecting...");
        tools.out("ConnectGopro(): connecting...");
        dialog.show();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isConnected()) {
            WifiInfo wifiInfo=mainWifi.getConnectionInfo();
            oldssid=wifiInfo.getSSID();
            tools.out("ancien wifi connecté :" + oldssid);
        }
        if (ssidCible.equals(oldssid) ) {
            dialog.dismiss();
            Integer goproIndex = getModel(bssidCible);
            goproModel=BssidApi[goproIndex];
            tools.out("MainActivity ConnectGopro goProIndex=" + goproIndex);
            tools.out("MainActivity ConnectGopro GoProModel=" + goproModel);
//            unregisterReceiver(broadcastReceiver);
            tools.out("MainActivity ConnectGopro Activity" + "new Intent");
            Intent i=null;
            if (goproIndex<5) {
                tools.out("je reveille " + ssidCible + " à l'adesse " + bssidCible);
                try {
                    MagicPacket.send(bssidCible, ip);
                } catch (UnknownHostException e) { e.printStackTrace(); } catch (SocketException e) { e.printStackTrace(); } catch (IllegalArgumentException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace();}
                i=new Intent(MainActivity.this,PreviewActivityGopro.class);
                tools.out("MainActivity putExtra");
                i.putExtra("GoproModel", String.valueOf(goproModel));
                i.putExtra("GoproIndex", String.valueOf(goproIndex));
            }
            if (goproIndex == 5) {
                i=new Intent(MainActivity.this,PreviewActivityXiaomi.class);
            }
            tools.out("MainActivity ConnectGopro StartActivity");
            startActivity(i);
            overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out);
        } else {
            List<WifiConfiguration> list = mainWifi.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + ssidCible + "\"")) {
                    mainWifi.disconnect();
                    mainWifi.enableNetwork(i.networkId, true);
                    mainWifi.reconnect();
                    break;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        }
    }

    protected void onResume() {
        super.onResume();
        ScanCamera();
    }

    protected void onDestroy() {
        tools.out("---------------------------------- Bye bye");
        super.onDestroy();
    }

    protected void onPause() {
        try
        {
            if (receiverWifi != null)
                unregisterReceiver(receiverWifi);
        }
        catch (IllegalArgumentException e) { System.out.println("IllegalArgumentException..."); }
        super.onPause();
    }

    public class RefreshWifiTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            ButtonRefresh.setVisibility(View.INVISIBLE);
            ButtonRefresh.setEnabled(false);
            ButtonHelp.setVisibility(View.INVISIBLE);
            ButtonHelp.setEnabled(false);
            ButtonSettings.setVisibility(View.INVISIBLE);
            ButtonSettings.setEnabled(false);
            textView2.setText("Searching for camera...");
            progressBar.setVisibility(View.VISIBLE);
            receiverWifi = new WifiReceiver();
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(receiverWifi, mIntentFilter);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String response = "";
            int progress;
            mainWifi.startScan();
            for (progress=0;progress<=100;progress++)
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(progress);
                progress++;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ButtonRefresh.setFocusable(true); ButtonRefresh.setFocusableInTouchMode(true); ButtonRefresh.requestFocus();
                    Button1.setTypeface(null, Typeface.NORMAL); Button1.setText("NO CAMERA"); Button1.setClickable(false); Button1.setEnabled(false); Button1.setFocusable(false);
                    Button2.setTypeface(null, Typeface.NORMAL); Button2.setText("NO CAMERA"); Button2.setClickable(false); Button2.setEnabled(false); Button2.setFocusable(false);
                    Button3.setTypeface(null, Typeface.NORMAL); Button3.setText("NO CAMERA"); Button3.setClickable(false); Button3.setEnabled(false); Button3.setFocusable(false);
                    Button4.setTypeface(null, Typeface.NORMAL); Button4.setText("NO CAMERA"); Button4.setClickable(false); Button4.setEnabled(false); Button4.setFocusable(false); }
            });

            for (int i = 0; i < CameraCount; i++) {
                final String text1=ssid[i]+"\n("+BssidModel[getModel(bssid[i])]+")";
                if (i == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { Button1.setText(text1); Button1.setTypeface(null, Typeface.BOLD); Button1.setClickable(true);
                            Button1.setEnabled(true); Button1.setFocusable(true); imageView1.setImageResource(resourceId1);
                        }
                    });
                }
                if (i == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { Button2.setText(text1); Button2.setTypeface(null, Typeface.BOLD); Button2.setClickable(true);
                            Button2.setEnabled(true); Button2.setFocusable(true); imageView2.setImageResource(resourceId1);
                        }
                    });
                }
                if (i == 2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { Button3.setText(text1); Button3.setTypeface(null, Typeface.BOLD); Button3.setClickable(true);
                            Button3.setEnabled(true); Button3.setFocusable(true); imageView3.setImageResource(resourceId1);
                        }
                    });
                }
                if (i == 3) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { Button4.setText(text1); Button4.setTypeface(null, Typeface.BOLD); Button4.setClickable(true);
                            Button4.setEnabled(true); Button4.setFocusable(true); imageView4.setImageResource( resourceId1 );
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.INVISIBLE);
            textView2.setText("");
            ButtonSettings.setVisibility(View.VISIBLE);
            ButtonSettings.setEnabled(true);
            ButtonHelp.setVisibility(View.VISIBLE);
            ButtonHelp.setEnabled(true);
            ButtonRefresh.setVisibility(View.VISIBLE);
            ButtonRefresh.setEnabled(true);
            if (CameraCount==0) {
                ButtonRefresh.setFocusable(true); ButtonRefresh.setFocusableInTouchMode(true); ButtonRefresh.requestFocus();
            }
            try
            {
                if (receiverWifi != null)
                    System.out.println("unregisterReceiver(receiverWifi)...");
                unregisterReceiver(receiverWifi);
            }
            catch (IllegalArgumentException e) { System.out.println("IllegalArgumentException..."); }
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            wifiList = mainWifi.getScanResults();
            CameraCount=0;
            for(int i = 0; i < wifiList.size(); i++){
                System.out.println("ssid="+wifiList.get(i).SSID+" / bssid="+wifiList.get(i).BSSID);
                if (Arrays.asList(BssidGopro).contains(wifiList.get(i).BSSID.substring(0,8).toUpperCase() )& CameraCount<4) {
                    ssid[CameraCount] = wifiList.get(i).SSID;
                    bssid[CameraCount] = wifiList.get(i).BSSID;
                    CameraCount++;
                }
            }
        }
    }



    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            tools.out("MainActivity: broadcastReceiver onReceive");
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            tools.out("MainActivity: broadcastReceiver onReceive:info="+info);
            if(info != null) {
                if(info.isConnected()) {
                    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssidfinal = wifiInfo.getSSID();
                    if(ssidfinal.equals(ssidCible)) {
                        dialog.dismiss();
                        Integer goproIndex = getModel(bssidCible);
                        goproModel=BssidApi[goproIndex];
                        tools.out("MainActivity BroadcastReceiver goProIndex=" + goproIndex);
                        tools.out("MainActivity BroadcastReceiver GoProModel=" + goproModel);
                        unregisterReceiver(broadcastReceiver);
                        tools.out("MainActivity Activity" + "new Intent");
                        Intent i=null;
                        if (goproIndex<5) {
                            tools.out("je reveille " + ssidfinal + " à l'adesse " + bssidCible);
                            try {
                                MagicPacket.send(bssidCible, ip);
                            } catch (UnknownHostException e) { e.printStackTrace(); } catch (SocketException e) { e.printStackTrace(); } catch (IllegalArgumentException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace();}
                            i=new Intent(MainActivity.this,PreviewActivityGopro.class);
                            tools.out("MainActivity putExtra");
                            i.putExtra("GoproModel", String.valueOf(goproModel));
                            i.putExtra("GoproIndex", String.valueOf(goproIndex));
                        }
                        if (goproIndex == 5) {
                            i=new Intent(MainActivity.this,PreviewActivityXiaomi.class);
                        }
                        tools.out("MainActivity BroadcastReceiver StartActivity");
                        startActivity(i);
                        overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out);
                    } else {
                        dialog.hide();
                        Toast.makeText(getApplicationContext(), "Unable to connect to "+ssidCible+".\n Check Wi-fi settings and password.",Toast.LENGTH_LONG).show();
                    }
                }
            }

        }
    };

}




