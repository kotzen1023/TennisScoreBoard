package com.seventhmoon.tennisscoreboard;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenu extends Activity{
    private static final String TAG = MainMenu.class.getName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";
    //private  String saveEncryptKey="";
    private boolean is_initData = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

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


        super.onDestroy();

    }


}
