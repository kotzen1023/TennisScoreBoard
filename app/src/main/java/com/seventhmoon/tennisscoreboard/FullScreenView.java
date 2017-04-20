package com.seventhmoon.tennisscoreboard;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.PageItem;
import com.seventhmoon.tennisscoreboard.Service.CheckCourtTableService;
import com.seventhmoon.tennisscoreboard.Service.GetCourtImageService;


public class FullScreenView extends AppCompatActivity {
    private static final String TAG = FullScreenView.class.getName();

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    private ImageView imageView;
    //ProgressDialog loadDialog = null;
    public static PageItem pageItem;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_view);

        Intent intent = getIntent();

        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");

        imageView = (ImageView) findViewById(R.id.imageViewFullScreenView);

        IntentFilter filter;



        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_COURT_IMAGE_COMPLETE)) {
                    Log.d(TAG, "receive brocast !");

                    if (pageItem != null) {
                        imageView.setImageBitmap(pageItem.getPic());
                    } else {
                        toast("Can't read Image!");
                    }

                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.GET_COURT_IMAGE_COMPLETE);
            registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }

        if (Double.valueOf(longitude) != 0.0 && Double.valueOf(latitude) != 0.0) {
            //initData.jdbc.queryCourtTable(context, longitude, latitude);

            Intent checkIntent = new Intent(FullScreenView.this, GetCourtImageService.class);
            checkIntent.putExtra("longitude", longitude);
            checkIntent.putExtra("latitude", latitude);
            startService(checkIntent);
        }
    }

    @Override
    protected void onPause() {
        //Data.LockSerivce = true;

        super.onPause();

    }

    @Override
    protected void onResume() {

        /*if (Data.LockSerivce)
        {

            Intent intent = new Intent(FullscreenViewer.this, Nfc_read.class);
            startActivity(intent);
        }*/

        super.onResume();

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");

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

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public void onBackPressed() {

        finish();
    }
}
