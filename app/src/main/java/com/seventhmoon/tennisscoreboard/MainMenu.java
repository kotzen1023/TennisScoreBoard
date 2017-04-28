package com.seventhmoon.tennisscoreboard;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.InitData;

import com.seventhmoon.tennisscoreboard.Service.CheckMacExistsService;



public class MainMenu extends Activity{
    private static final String TAG = MainMenu.class.getName();
    //public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    //static SharedPreferences pref ;
    //static SharedPreferences.Editor editor;
    //private static final String FILE_NAME = "Preference";
    //private  String saveEncryptKey="";
    //private boolean is_initData = false;

    public static InitData initData = new InitData();

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    //private static String my_id;
    private static String id;
    private static String macAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        Log.d(TAG, "onCreate");

        IntentFilter filter;
        Intent intent = getIntent();
        macAddress = intent.getStringExtra("WiFiMac");

        Log.d(TAG, "macAddress = "+macAddress);


        ImageView imgLetsPlay = (ImageView) findViewById(R.id.imageLetsPlay);
        TextView txtLetsPlay = (TextView) findViewById(R.id.textLetsPlay);

        ImageView imgFindCourt = (ImageView) findViewById(R.id.imageFindCourt);
        TextView txtFindCourt = (TextView) findViewById(R.id.textFindCourt);

        //ImageView imgNotify = (ImageView) findViewById(R.id.imageTopNotify);
        //TextView txtNotify = (TextView) findViewById(R.id.textTopNotify);

        imgLetsPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, PlayMainActivity.class);
                startActivity(intent);
            }
        });

        txtLetsPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, PlayMainActivity.class);
                startActivity(intent);
            }
        });

        imgFindCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, FindCourtActivity.class);
                startActivity(intent);
            }
        });

        txtFindCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, FindCourtActivity.class);
                startActivity(intent);
            }
        });

        //check user mac(id ) exists

        //InitData initData = new InitData();
        initData.setWifiMac(macAddress);
        id = Build.MODEL;
        initData.setUpload_remain(0);

        String my_id = id +" - "+initData.getWifiMac();

        Intent checkIntent = new Intent(MainMenu.this, CheckMacExistsService.class);
        checkIntent.putExtra("my_id", my_id);
        startService(checkIntent);

        //initData.jdbc.queryUserIdTable(MainMenu.this, id +" - "+initData.getWifiMac());

        //while (is_query) {

        //}



        //Intent serviceintent = new Intent(MainMenu.this, CheckMacExistsService.class);
        //serviceintent.setAction(Constants.ACTION.GET_MESSAGE_LIST_ACTION);
        //serviceintent.putExtra("ACCOUNT", account);
        //serviceintent.putExtra("DEVICE_ID", device_id);
        //startService(serviceintent);



        /*imgNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopMenu.this, ConnectionDetails.class);
                startActivity(intent);
            }
        });

        txtNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopMenu.this, ConnectionDetails.class);
                startActivity(intent);
            }
        });*/


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.CHECK_MAC_EXIST_COMPLETE)) {
                    Log.d(TAG, "receive brocast !");

                    if (initData.isMatch_mac()) {
                        Log.d(TAG, "found same id! current_upload = "+initData.getUpload_remain());

                    } else {
                        Log.d(TAG, "id not found!");
                        initData.jdbc.insertTableUserId(id +" - "+macAddress, "5");
                    }

                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.CHECK_MAC_EXIST_COMPLETE);
            registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }
    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        if (isRegister && mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }


            isRegister = false;
            mReceiver = null;
        }

        super.onDestroy();

    }


}
