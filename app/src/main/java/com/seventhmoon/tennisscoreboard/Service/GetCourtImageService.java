package com.seventhmoon.tennisscoreboard.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.PageItem;

import static com.seventhmoon.tennisscoreboard.MainMenu.initData;
import static com.seventhmoon.tennisscoreboard.Sql.Jdbc.is_query;
import static com.seventhmoon.tennisscoreboard.FullScreenView.pageItem;

public class GetCourtImageService extends IntentService {
    private static final String TAG = GetCourtImageService.class.getName();

    public GetCourtImageService() {
        super("GetCourtImageService");
    }
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");


        context = getApplicationContext();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent");

        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");

        Log.e(TAG, "longitude = "+longitude+", latitude = "+latitude);



        if (!is_query) { // not in query

            pageItem = initData.jdbc.queryCourtTableImage(context, Double.valueOf(longitude), Double.valueOf(latitude));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        Intent newNotifyIntent = new Intent(Constants.ACTION.GET_COURT_IMAGE_COMPLETE);
        context.sendBroadcast(newNotifyIntent);
    }
}
