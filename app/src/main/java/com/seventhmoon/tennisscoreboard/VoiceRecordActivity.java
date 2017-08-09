package com.seventhmoon.tennisscoreboard;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seventhmoon.tennisscoreboard.Audio.VoicePlay;
import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.RecordArrayAdapter;
import com.seventhmoon.tennisscoreboard.Data.RecordItem;


import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.seventhmoon.tennisscoreboard.GameActivity.voicePlay;


public class VoiceRecordActivity extends AppCompatActivity {
    private static final String TAG = VoiceRecordActivity.class.getName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private Context context;
    public ArrayList<RecordItem> recordList = new ArrayList<>();
    private RecordArrayAdapter recordArrayAdapter;
    private LinearLayout layoutRecord;
    private LinearLayout lauoutPlayStop;
    private LinearLayout layoutRecordRecord;
    private ListView listView;

    private ImageView imgPlayStop;
    private ImageView imgRecord;
    private TextView textViewTime;

    private boolean is_recording = false;
    private boolean is_playing = false;

    private ArrayList<String> saveNameList = new ArrayList<>();

    private MediaRecorder mRecorder;
    private long mStartTime = 0;

    private int[] amplitudes = new int[100];
    private int i = 0;
    private Handler mHandler = new Handler();

    private File mOutputFile;
    private String currentSelectedName;

    private VoicePlay recordPlay;
    private ArrayList<String> recordPlayList = new ArrayList<>();

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.voice_record_activity);

        context = getBaseContext();

        if (voicePlay == null) {
            recordPlay = new VoicePlay(context);
        } else {
            Log.e(TAG, "voicePlay is running");
        }

        listView = (ListView) findViewById(R.id.listViewVoiceRecord);
        layoutRecord = (LinearLayout) findViewById(R.id.layoutRecord);
        lauoutPlayStop = (LinearLayout) findViewById(R.id.layoutRecordPlayStop);
        layoutRecordRecord = (LinearLayout) findViewById(R.id.layoutRecordRecord);
        imgPlayStop = (ImageView) findViewById(R.id.imgRecordPlayStop);
        imgRecord = (ImageView) findViewById(R.id.imgRecordRecord);
        textViewTime = (TextView) findViewById(R.id.textViewTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkAndRequestPermissions()) {
                // carry on the normal flow, as the case of  permissions  granted.

                initItems();
            }
        } else {
            initItems();
        }




    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {

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

    @Override
    public void onBackPressed() {

        AlertDialog.Builder confirmdialog = new AlertDialog.Builder(VoiceRecordActivity.this);
        confirmdialog.setTitle(getResources().getString(R.string.record_stop));
        confirmdialog.setIcon(R.drawable.ball_icon);
        confirmdialog.setCancelable(false);
        confirmdialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                finish();

            }
        });
        confirmdialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        confirmdialog.show();
    }

    private void initItems() {
        RecordItem item0 = new RecordItem("0 : 15", "user_0_15.m4a");
        recordList.add(item0);

        RecordItem item1 = new RecordItem("0 : 30", "user_0_30.m4a");
        recordList.add(item1);

        recordArrayAdapter = new RecordArrayAdapter(VoiceRecordActivity.this, R.layout.voice_record_item, recordList);
        listView.setAdapter(recordArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < listView.getCount(); i++) {
                    RecordItem item = (RecordItem) parent.getItemAtPosition(i);

                    if (i == position) {
                        item.setSelected(true);
                        currentSelectedName = item.getFilename();
                        recordPlayList.clear();
                        recordPlayList.add(currentSelectedName);
                    } else {
                        item.setSelected(false);
                    }

                }

                listView.invalidateViews();



                layoutRecord.setVisibility(View.VISIBLE);

            }
        });

        imgPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_playing) { //stop->play

                    if (!is_recording) {

                        imgPlayStop.setImageResource(R.drawable.ic_stop_white_48dp);
                        is_playing = true;


                        if (voicePlay != null) {
                            voicePlay.doStopAudioPlayMulti();
                            voicePlay.audioPlayMultiFile(recordPlayList);
                        } else {
                            recordPlay.doStopAudioPlayMulti();
                            recordPlay.audioPlayMultiFile(recordPlayList);
                        }

                    } else { //recording ->
                        if (mRecorder != null) {
                            stopRecording(true);
                        }
                        imgRecord.setImageResource(R.drawable.ic_fiber_manual_record_white_48dp);
                        imgPlayStop.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                        is_recording = false;
                        is_playing = false;
                    }
                } else { //playing->stop
                    imgPlayStop.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    is_playing = false;
                }
            }
        });

        lauoutPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_playing) { //stop->play

                    if (!is_recording) {

                        imgPlayStop.setImageResource(R.drawable.ic_stop_white_48dp);
                        is_playing = true;

                        if (voicePlay != null) {
                            voicePlay.doStopAudioPlayMulti();
                            voicePlay.audioPlayMultiFile(recordPlayList);
                        } else {
                            recordPlay.doStopAudioPlayMulti();
                            recordPlay.audioPlayMultiFile(recordPlayList);
                        }
                    } else { //recording -> stop
                        if (mRecorder != null) {
                            stopRecording(true);
                        }

                        imgRecord.setImageResource(R.drawable.ic_fiber_manual_record_white_48dp);
                        imgPlayStop.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                        is_recording = false;
                        is_playing = false;
                    }
                } else { //playing->stop
                    imgPlayStop.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    is_playing = false;
                }
            }
        });

        imgRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_recording) {
                    if (!is_playing) { //not record->recording
                        imgPlayStop.setImageResource(R.drawable.ic_stop_white_48dp);
                        imgRecord.setImageResource(R.drawable.ic_fiber_manual_record_red_48dp);
                        is_recording = true;

                        Log.d(TAG, "output: " + getOutputFile());
                        startRecording();
                    } else {
                        imgPlayStop.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                        imgRecord.setImageResource(R.drawable.ic_fiber_manual_record_white_48dp);
                        is_recording = false;
                    }
                }
            }
        });

        layoutRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_recording) {
                    if (!is_playing) { //not record->recording
                        imgPlayStop.setImageResource(R.drawable.ic_stop_white_48dp);
                        imgRecord.setImageResource(R.drawable.ic_fiber_manual_record_red_48dp);
                        is_recording = true;

                        Log.d(TAG, "output: " + getOutputFile());
                        startRecording();
                    } else {
                        imgPlayStop.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                        imgRecord.setImageResource(R.drawable.ic_fiber_manual_record_white_48dp);
                        is_recording = false;
                    }
                }
            }
        });

        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.PLAY_MULTIFILES_COMPLETE)) {
                    Log.e(TAG, "receive PLAY_MULTIFILES_COMPLETE");
                    imgPlayStop.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    is_playing = false;
                }
            }
        };


        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.PLAY_MULTIFILES_COMPLETE);
            registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }
    }

    private Runnable mTickExecutor = new Runnable() {
        @Override
        public void run() {
            tick();
            mHandler.postDelayed(mTickExecutor,100);
        }
    };

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            mRecorder.setAudioEncodingBitRate(48000);
        } else {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioEncodingBitRate(64000);
        }
        mRecorder.setAudioSamplingRate(16000);
        mOutputFile = getOutputFile();
        mOutputFile.getParentFile().mkdirs();
        mRecorder.setOutputFile(mOutputFile.getAbsolutePath());

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartTime = SystemClock.elapsedRealtime();
            mHandler.postDelayed(mTickExecutor, 100);
            Log.d("Voice Recorder","started recording to "+mOutputFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("Voice Recorder", "prepare() failed "+e.getMessage());
        }
    }

    protected  void stopRecording(boolean saveFile) {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        mStartTime = 0;
        mHandler.removeCallbacks(mTickExecutor);
        if (!saveFile && mOutputFile != null) {
            mOutputFile.delete();
        }
    }

    private File getOutputFile() {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.TAIWAN);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/.tennisScoredBoard/user/"
                + currentSelectedName);
    }

    private void tick() {
        long time = (mStartTime < 0) ? 0 : (SystemClock.elapsedRealtime() - mStartTime);
        int minutes = (int) (time / 60000);
        int seconds = (int) (time / 1000) % 60;
        int milliseconds = (int) (time / 100) % 10;
        textViewTime.setText(minutes+":"+(seconds < 10 ? "0"+seconds : seconds)+"."+milliseconds);
        if (mRecorder != null) {
            amplitudes[i] = mRecorder.getMaxAmplitude();
            //Log.d("Voice Recorder","amplitude: "+(amplitudes[i] * 100 / 32767));
            if (i >= amplitudes.length -1) {
                i = 0;
            } else {
                ++i;
            }
        }
    }


    private  boolean checkAndRequestPermissions() {
        //int permissionSendMessage = ContextCompat.checkSelfPermission(this,
        //        android.Manifest.permission.WRITE_CALENDAR);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        //int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO);
        }
        //if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.WRITE_CALENDAR);
        //}
        //if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        //}

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Log.e(TAG, "result size = "+grantResults.length+ "result[0] = "+grantResults[0]+", result[1] = "+grantResults[1]);


        /*switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    Log.i(TAG, "WRITE_CALENDAR permissions granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "READ_CONTACTS permissions denied");

                    RetryDialog();
                }
            }
            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }*/
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                //perms.put(android.Manifest.permission.WRITE_CALENDAR, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                //perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (//perms.get(android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED )
                    //&& perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        Log.d(TAG, "record permission granted");

                        // process the normal flow
                        //else any one or both the permissions are not granted
                        initItems();
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (//ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_CALENDAR) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)
                            //|| ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                                ) {
                            showDialogOK(getResources().getString(R.string.permission_record),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.dialog_confirm), okListener)
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), okListener)
                .create()
                .show();
    }
}
