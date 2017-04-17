package com.seventhmoon.tennisscoreboard.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.seventhmoon.tennisscoreboard.Data.Constants;

import static com.seventhmoon.tennisscoreboard.MainMenu.initData;
import static com.seventhmoon.tennisscoreboard.Sql.Jdbc.is_query;


public class CheckCourtTableService extends IntentService {
    private static final String TAG = CheckCourtTableService.class.getName();
    private Context context;

    public CheckCourtTableService() {
        super("CheckCourtTableService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");



        /*pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        name = pref.getString("NAME", "");
        account = pref.getString("ACCOUNT", "");
        alarm_interval = pref.getInt("ALARM_INTERVAL", 30);
        sync_option = pref.getInt("SYNC_SETTING", 0);*/

        context = getApplicationContext();

        // = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.TAIWAN);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent");

        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");

        Log.e(TAG, "longitude = "+longitude+", latitude = "+latitude);

        if (longitude != null && latitude != null) {

            if (!is_query) {// not in query
                initData.jdbc.queryCourtTable(context, Double.valueOf(longitude), Double.valueOf(latitude));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        Intent newNotifyIntent = new Intent(Constants.ACTION.GET_COURT_INFO_COMPLETE);
        context.sendBroadcast(newNotifyIntent);
    }
}
