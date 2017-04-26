package com.seventhmoon.tennisscoreboard.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.InitData;
import com.seventhmoon.tennisscoreboard.MainMenu;

import static com.seventhmoon.tennisscoreboard.MainMenu.initData;
import static com.seventhmoon.tennisscoreboard.Sql.Jdbc.is_query;


public class CheckMacExistsService extends IntentService {
    private static final String TAG = CheckMacExistsService.class.getName();
    private Context context;

    public CheckMacExistsService() {
        super("CheckMacExistsService");
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

        String my_id = intent.getStringExtra("my_id");

        if (!is_query) { // not in query

            initData.jdbc.queryUserIdTable(my_id);
        }
    }

    @Override
    public void onDestroy() {
        Intent newNotifyIntent = new Intent(Constants.ACTION.CHECK_MAC_EXIST_COMPLETE);
        context.sendBroadcast(newNotifyIntent);

        super.onDestroy();
        Log.d(TAG, "onDestroy()");


    }
}
