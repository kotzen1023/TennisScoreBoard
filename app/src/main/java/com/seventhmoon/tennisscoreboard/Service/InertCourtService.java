package com.seventhmoon.tennisscoreboard.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.seventhmoon.tennisscoreboard.Data.Constants;

import static com.seventhmoon.tennisscoreboard.MainMenu.initData;
import static com.seventhmoon.tennisscoreboard.Sql.Jdbc.is_query;
import static com.seventhmoon.tennisscoreboard.Sql.Jdbc.is_update;


public class InertCourtService extends IntentService {
    private static final String TAG = InertCourtService.class.getName();
    private Context context;

    public InertCourtService() {
        super("InertCourtService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        context = getApplicationContext();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent");

        /*
        insertIntent.putExtra("name", editTextCourtName.getText().toString());
                    insertIntent.putExtra("longitude", String.valueOf(longitude));
                    insertIntent.putExtra("latitude", String.valueOf(latitude));
                    insertIntent.putExtra("type", String.valueOf(courtTypeSpinner.getSelectedItemPosition()));
                    insertIntent.putExtra("usage", String.valueOf(courtUsageSpinner.getSelectedItemPosition()));
                    insertIntent.putExtra("light", String.valueOf(lightSpinner.getSelectedItemPosition()));
                    insertIntent.putExtra("courts", String.valueOf(editTextCourtNum.getText().toString()));
                    insertIntent.putExtra("ifCharge", String.valueOf(ifChargeSpinner.getSelectedItemPosition()));
                    insertIntent.putExtra("charge", editTextCharge.getText().toString());
                    insertIntent.putExtra("maintenance", String.valueOf(ratingBarMaintenance.getRating()));
                    insertIntent.putExtra("traffic", String.valueOf(ratingBarTraffic.getRating()));
                    insertIntent.putExtra("parking", String.valueOf(ratingBarParking.getRating()));
         */

        String name = intent.getStringExtra("name");
        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");
        String type = intent.getStringExtra("type");
        String usage = intent.getStringExtra("usage");
        String light = intent.getStringExtra("light");
        String courts = intent.getStringExtra("courts");
        String ifCharge = intent.getStringExtra("ifCharge");
        String charge = intent.getStringExtra("charge");
        String maintenance = intent.getStringExtra("maintenance");
        String traffic = intent.getStringExtra("traffic");
        String parking = intent.getStringExtra("parking");
        byte blob[] = intent.getByteArrayExtra("blob");


        Log.d(TAG, "name = "+name);
        Log.d(TAG, "longitude = "+longitude);
        Log.d(TAG, "latitude = "+latitude);
        Log.d(TAG, "type = "+type);
        Log.d(TAG, "usage = "+usage);
        Log.d(TAG, "light = "+light);
        Log.d(TAG, "courts = "+courts);
        Log.d(TAG, "ifCharge = "+ifCharge);
        Log.d(TAG, "charge = "+charge);
        Log.d(TAG, "maintenance = "+maintenance);
        Log.d(TAG, "traffic = "+traffic);
        Log.d(TAG, "parking = "+parking);


        if (!is_update) {// not in query
            //initData.jdbc.insertTableCourt(context, Double.valueOf(longitude), Double.valueOf(latitude));
            initData.jdbc.insertTableCourt(name,
                    longitude,
                    latitude,
                    type,
                    usage,
                    light,
                    courts,
                    ifCharge,
                    charge,
                    maintenance,
                    traffic,
                    parking,
                    blob
            );
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        Intent newNotifyIntent = new Intent(Constants.ACTION.INSERT_COURT_INFO_COMPLETE);
        context.sendBroadcast(newNotifyIntent);
    }
}
