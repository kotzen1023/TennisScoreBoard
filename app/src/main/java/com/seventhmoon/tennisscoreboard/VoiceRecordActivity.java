package com.seventhmoon.tennisscoreboard;

import android.Manifest;
import android.app.AlertDialog;

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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import java.util.Map;

import static com.seventhmoon.tennisscoreboard.Data.FileOperation.check_user_voice_exist;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.clear_all_voice;

import static com.seventhmoon.tennisscoreboard.GameActivity.voicePlay;


public class VoiceRecordActivity extends AppCompatActivity {
    private static final String TAG = VoiceRecordActivity.class.getName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    //private Context context;
    public ArrayList<RecordItem> recordList = new ArrayList<>();
    private RecordArrayAdapter recordArrayAdapter;
    private LinearLayout layoutRecord;
    //private LinearLayout lauoutPlayStop;
    //private LinearLayout layoutRecordRecord;
    private ListView listView;

    private ImageView btnImport;
    private ImageView imgPlayStop;
    private ImageView imgRecord;
    private TextView textViewTime;

    private boolean is_recording = false;
    private boolean is_playing = false;

    //private ArrayList<String> saveNameList = new ArrayList<>();

    private MediaRecorder mRecorder;
    private long mStartTime = 0;

    private int[] amplitudes = new int[100];
    private int i = 0;
    private Handler mHandler = new Handler();

    private File mOutputFile;
    private String currentSelectedName;
    private int record_select;

    private VoicePlay recordPlay;
    private ArrayList<String> recordPlayList = new ArrayList<>();

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;


    //private ActionBar actionBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.voice_record_activity);
        Context context = getBaseContext();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.voice_user_record));
        }

        if (voicePlay == null) {
            recordPlay = new VoicePlay(context);
        } else {
            Log.e(TAG, "voicePlay is running");
        }

        listView = findViewById(R.id.listViewVoiceRecord);
        layoutRecord = findViewById(R.id.layoutRecord);
        //lauoutPlayStop = findViewById(R.id.layoutRecordPlayStop);
        //layoutRecordRecord = findViewById(R.id.layoutRecordRecord);
        btnImport = findViewById(R.id.imgRecordImport);
        imgPlayStop = findViewById(R.id.imgRecordPlayStop);
        imgRecord = findViewById(R.id.imgRecordRecord);
        textViewTime = findViewById(R.id.textViewTime);

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

        RecordItem item0 = new RecordItem("0", "user_love.m4a");
        if (check_user_voice_exist("user_love.m4a"))
            item0.setFileExist(true);
        recordList.add(item0);

        RecordItem item1 = new RecordItem("1", "user_1.m4a");
        if (check_user_voice_exist("user_1.m4a"))
            item1.setFileExist(true);
        recordList.add(item1);

        RecordItem item2 = new RecordItem("2", "user_2.m4a");
        if (check_user_voice_exist("user_2.m4a"))
            item2.setFileExist(true);
        recordList.add(item2);

        RecordItem item3 = new RecordItem("3", "user_3.m4a");
        if (check_user_voice_exist("user_3.m4a"))
            item3.setFileExist(true);
        recordList.add(item3);

        RecordItem item4 = new RecordItem("4", "user_4.m4a");
        if (check_user_voice_exist("user_4.m4a"))
            item4.setFileExist(true);
        recordList.add(item4);

        RecordItem item5 = new RecordItem("5", "user_5.m4a");
        if (check_user_voice_exist("user_5.m4a"))
            item5.setFileExist(true);
        recordList.add(item5);

        RecordItem item6 = new RecordItem("6", "user_6.m4a");
        if (check_user_voice_exist("user_6.m4a"))
            item6.setFileExist(true);
        recordList.add(item6);

        RecordItem item7 = new RecordItem("7", "user_7.m4a");
        if (check_user_voice_exist("user_7.m4a"))
            item7.setFileExist(true);
        recordList.add(item7);

        RecordItem item8 = new RecordItem("8", "user_8.m4a");
        if (check_user_voice_exist("user_8.m4a"))
            item8.setFileExist(true);
        recordList.add(item8);

        RecordItem item9 = new RecordItem("9", "user_9.m4a");
        if (check_user_voice_exist("user_9.m4a"))
            item9.setFileExist(true);
        recordList.add(item9);

        RecordItem item10 = new RecordItem("10", "user_10.m4a");
        if (check_user_voice_exist("user_10.m4a"))
            item10.setFileExist(true);
        recordList.add(item10);

        RecordItem item11 = new RecordItem("11", "user_11.m4a");
        if (check_user_voice_exist("user_11.m4a"))
            item11.setFileExist(true);
        recordList.add(item11);

        RecordItem item12 = new RecordItem("12", "user_12.m4a");
        if (check_user_voice_exist("user_12.m4a"))
            item12.setFileExist(true);
        recordList.add(item12);

        RecordItem item13 = new RecordItem("13", "user_13.m4a");
        if (check_user_voice_exist("user_13.m4a"))
            item13.setFileExist(true);
        recordList.add(item13);

        RecordItem item14 = new RecordItem("14", "user_14.m4a");
        if (check_user_voice_exist("user_14.m4a"))
            item14.setFileExist(true);
        recordList.add(item14);

        RecordItem item15 = new RecordItem("15", "user_15.m4a");
        if (check_user_voice_exist("user_15.m4a"))
            item15.setFileExist(true);
        recordList.add(item15);

        RecordItem item16 = new RecordItem("16", "user_16.m4a");
        if (check_user_voice_exist("user_16.m4a"))
            item16.setFileExist(true);
        recordList.add(item16);

        RecordItem item17 = new RecordItem("17", "user_17.m4a");
        if (check_user_voice_exist("user_17.m4a"))
            item17.setFileExist(true);
        recordList.add(item17);

        RecordItem item18 = new RecordItem("18", "user_18.m4a");
        if (check_user_voice_exist("user_18.m4a"))
            item18.setFileExist(true);
        recordList.add(item18);

        RecordItem item19 = new RecordItem("19", "user_19.m4a");
        if (check_user_voice_exist("user_19.m4a"))
            item19.setFileExist(true);
        recordList.add(item19);

        RecordItem item20 = new RecordItem("20", "user_20.m4a");
        if (check_user_voice_exist("user_20.m4a"))
            item20.setFileExist(true);
        recordList.add(item20);

        RecordItem item21 = new RecordItem("30", "user_30.m4a");
        if (check_user_voice_exist("user_30.m4a"))
            item21.setFileExist(true);
        recordList.add(item21);

        RecordItem item22 = new RecordItem("40", "user_40.m4a");
        if (check_user_voice_exist("user_40.m4a"))
            item22.setFileExist(true);
        recordList.add(item22);

        RecordItem item23 = new RecordItem("50", "user_50.m4a");
        if (check_user_voice_exist("user_50.m4a"))
            item23.setFileExist(true);
        recordList.add(item23);

        RecordItem item24 = new RecordItem("60", "user_60.m4a");
        if (check_user_voice_exist("user_60.m4a"))
            item24.setFileExist(true);
        recordList.add(item24);

        RecordItem item25 = new RecordItem("70", "user_70.m4a");
        if (check_user_voice_exist("user_70.m4a"))
            item25.setFileExist(true);
        recordList.add(item25);

        RecordItem item26 = new RecordItem("80", "user_80.m4a");
        if (check_user_voice_exist("user_80.m4a"))
            item26.setFileExist(true);
        recordList.add(item26);

        RecordItem item27 = new RecordItem("90", "user_90.m4a");
        if (check_user_voice_exist("user_90.m4a"))
            item27.setFileExist(true);
        recordList.add(item27);

        RecordItem item28 = new RecordItem("0 : 15", "user_0_15.m4a");
        if (check_user_voice_exist("user_0_15.m4a"))
            item28.setFileExist(true);
        recordList.add(item28);

        RecordItem item29 = new RecordItem("0 : 30", "user_0_30.m4a");
        if (check_user_voice_exist("user_0_30.m4a"))
            item29.setFileExist(true);
        recordList.add(item29);

        RecordItem item30 = new RecordItem("0 : 40", "user_0_40.m4a");
        if (check_user_voice_exist("user_0_40.m4a"))
            item30.setFileExist(true);
        recordList.add(item30);

        RecordItem item31 = new RecordItem("15 : 0", "user_15_0.m4a");
        if (check_user_voice_exist("user_15_0.m4a"))
            item31.setFileExist(true);
        recordList.add(item31);

        RecordItem item32 = new RecordItem("15 : 15", "user_15_15.m4a");
        if (check_user_voice_exist("user_15_15.m4a"))
            item32.setFileExist(true);
        recordList.add(item32);

        RecordItem item33 = new RecordItem("15 : 30", "user_15_30.m4a");
        if (check_user_voice_exist("user_15_30.m4a"))
            item33.setFileExist(true);
        recordList.add(item33);

        RecordItem item34 = new RecordItem("15 : 40", "user_15_40.m4a");
        if (check_user_voice_exist("user_15_40.m4a"))
            item34.setFileExist(true);
        recordList.add(item34);

        RecordItem item35 = new RecordItem("30 : 0", "user_30_0.m4a");
        if (check_user_voice_exist("user_30_0.m4a"))
            item35.setFileExist(true);
        recordList.add(item35);

        RecordItem item36 = new RecordItem("30 : 15", "user_30_15.m4a");
        if (check_user_voice_exist("user_30_15.m4a"))
            item36.setFileExist(true);
        recordList.add(item36);

        RecordItem item37 = new RecordItem("30 : 30", "user_30_30.m4a");
        if (check_user_voice_exist("user_30_30.m4a"))
            item37.setFileExist(true);
        recordList.add(item37);

        RecordItem item38 = new RecordItem("30 : 40", "user_30_40.m4a");
        if (check_user_voice_exist("user_30_40.m4a"))
            item38.setFileExist(true);
        recordList.add(item38);

        RecordItem item39 = new RecordItem("40 : 0", "user_40_0.m4a");
        if (check_user_voice_exist("user_40_0.m4a"))
            item39.setFileExist(true);
        recordList.add(item39);

        RecordItem item40 = new RecordItem("40 : 15", "user_40_15.m4a");
        if (check_user_voice_exist("user_40_15.m4a"))
            item40.setFileExist(true);
        recordList.add(item40);

        RecordItem item41 = new RecordItem("40 : 30", "user_40_30.m4a");
        if (check_user_voice_exist("user_40_30.m4a"))
            item41.setFileExist(true);
        recordList.add(item41);

        RecordItem item42 = new RecordItem("40 : 40 (Deuce)", "user_40_40.m4a");
        if (check_user_voice_exist("user_40_40.m4a"))
            item42.setFileExist(true);
        recordList.add(item42);

        RecordItem item43 = new RecordItem("Advantage", "user_ad.m4a");
        if (check_user_voice_exist("user_ad.m4a"))
            item43.setFileExist(true);
        recordList.add(item43);

        RecordItem item44 = new RecordItem("All", "user_all.m4a");
        if (check_user_voice_exist("user_all.m4a"))
            item44.setFileExist(true);
        recordList.add(item44);

        RecordItem item45 = new RecordItem("Deciding Point", "user_deciding_point.m4a");
        if (check_user_voice_exist("user_deciding_point.m4a"))
            item45.setFileExist(true);
        recordList.add(item45);

        RecordItem item46 = new RecordItem("First Set", "user_first_set.m4a");
        if (check_user_voice_exist("user_first_set.m4a"))
            item46.setFileExist(true);
        recordList.add(item46);

        RecordItem item47 = new RecordItem("Second Set", "user_second_set.m4a");
        if (check_user_voice_exist("user_second_set.m4a"))
            item47.setFileExist(true);
        recordList.add(item47);

        RecordItem item48 = new RecordItem("Third Set", "user_third_set.m4a");
        if (check_user_voice_exist("user_third_set.m4a"))
            item48.setFileExist(true);
        recordList.add(item48);

        RecordItem item49 = new RecordItem("Forth Set", "user_forth_set.m4a");
        if (check_user_voice_exist("user_forth_set.m4a"))
            item49.setFileExist(true);
        recordList.add(item49);

        RecordItem item50 = new RecordItem("Game", "user_game.m4a");
        if (check_user_voice_exist("user_game.m4a"))
            item50.setFileExist(true);
        recordList.add(item50);

        RecordItem item51 = new RecordItem("Set", "user_set.m4a");
        if (check_user_voice_exist("user_set.m4a"))
            item51.setFileExist(true);
        recordList.add(item51);

        RecordItem item52 = new RecordItem("Match", "user_match.m4a");
        if (check_user_voice_exist("user_match.m4a"))
            item52.setFileExist(true);
        recordList.add(item52);

        RecordItem item53 = new RecordItem("Tiebreak", "user_tiebreak.m4a");
        if (check_user_voice_exist("user_tiebreak.m4a"))
            item53.setFileExist(true);
        recordList.add(item53);

        RecordItem item54 = new RecordItem("Game to", "user_game_to.m4a");
        if (check_user_voice_exist("user_game_to.m4a"))
            item54.setFileExist(true);
        recordList.add(item54);

        RecordItem item55 = new RecordItem("Games to", "user_games_to.m4a");
        if (check_user_voice_exist("user_games_to.m4a"))
            item55.setFileExist(true);
        recordList.add(item55);

        RecordItem item56 = new RecordItem("Game all", "user_game_all.m4a");
        if (check_user_voice_exist("user_game_all.m4a"))
            item56.setFileExist(true);
        recordList.add(item56);

        RecordItem item57 = new RecordItem("Games all", "user_games_all.m4a");
        if (check_user_voice_exist("user_games_all.m4a"))
            item57.setFileExist(true);
        recordList.add(item57);

        RecordItem item58 = new RecordItem("To", "user_to.m4a");
        if (check_user_voice_exist("user_to.m4a"))
            item58.setFileExist(true);
        recordList.add(item58);

        RecordItem item59 = new RecordItem("(Player1's Name)", "user_player_up.m4a");
        if (check_user_voice_exist("user_player_up.m4a"))
            item59.setFileExist(true);
        recordList.add(item59);

        RecordItem item60= new RecordItem("(Player2's Name)", "user_player_down.m4a");
        if (check_user_voice_exist("user_player_down.m4a"))
            item60.setFileExist(true);
        recordList.add(item60);

        recordArrayAdapter = new RecordArrayAdapter(VoiceRecordActivity.this, R.layout.voice_record_item, recordList);
        listView.setAdapter(recordArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < listView.getCount(); i++) {
                    RecordItem item = (RecordItem) parent.getItemAtPosition(i);

                    if (i == position) {
                        item.setSelected(true);
                        Log.e(TAG, "set select: "+item.getFilename());
                        currentSelectedName = item.getFilename();
                        record_select = position;
                        recordPlayList.clear();
                        if (check_user_voice_exist(currentSelectedName)) { //if file exist, add
                            recordPlayList.add(currentSelectedName);
                        }
                    } else {
                        item.setSelected(false);
                    }

                }

                listView.invalidateViews();



                layoutRecord.setVisibility(View.VISIBLE);

            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(VoiceRecordActivity.this, FileImportActivity.class);
                intent.putExtra("filename", currentSelectedName);
                startActivity(intent);
            }
        });

        imgPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_playing) { //stop->play

                    if (!is_recording) {

                        if (recordPlayList.size() > 0) {
                            imgPlayStop.setImageResource(R.drawable.ic_stop_white_48dp);
                            is_playing = true;
                            startVoicePlaying();
                        } else {
                            toast(getResources().getString(R.string.record_file_not_exist));
                        }

                    } else { //recording ->
                        if (mRecorder != null) {
                            stopRecording();
                            recordPlayList.clear();
                            if (check_user_voice_exist(currentSelectedName)) { //if file exist, add
                                recordPlayList.add(currentSelectedName);
                                recordList.get(record_select).setFileExist(true);
                                recordArrayAdapter.notifyDataSetChanged();
                            }
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

        /*lauoutPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_playing) { //stop->play

                    if (!is_recording) {

                        if (recordPlayList.size() > 0) {
                            imgPlayStop.setImageResource(R.drawable.ic_stop_white_48dp);
                            is_playing = true;
                            startVoicePlaying();
                        } else {
                            toast("File not exist!");
                        }
                    } else { //recording -> stop
                        if (mRecorder != null) {
                            stopRecording(true);
                            recordPlayList.clear();
                            if (check_user_voice_exist(currentSelectedName)) { //if file exist, add
                                recordPlayList.add(currentSelectedName);
                            }
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
        });*/

        imgRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_recording) {
                    if (!is_playing) { //not record->recording

                        if (check_user_voice_exist(currentSelectedName)) { //if file exist, add
                            AlertDialog.Builder confirmdialog = new AlertDialog.Builder(VoiceRecordActivity.this);
                            confirmdialog.setTitle(getResources().getString(R.string.record_file_is_exist_over_write));
                            confirmdialog.setIcon(R.drawable.ball_icon);
                            confirmdialog.setCancelable(false);
                            confirmdialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    imgPlayStop.setImageResource(R.drawable.ic_stop_white_48dp);
                                    imgRecord.setImageResource(R.drawable.ic_fiber_manual_record_red_48dp);
                                    is_recording = true;

                                    Log.d(TAG, "output: " + getOutputFile());
                                    startRecording();


                                }
                            });
                            confirmdialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            });

                            confirmdialog.show();
                        } else {
                            imgPlayStop.setImageResource(R.drawable.ic_stop_white_48dp);
                            imgRecord.setImageResource(R.drawable.ic_fiber_manual_record_red_48dp);
                            is_recording = true;

                            Log.d(TAG, "output: " + getOutputFile());
                            startRecording();
                        }




                    } else {
                        imgPlayStop.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                        imgRecord.setImageResource(R.drawable.ic_fiber_manual_record_white_48dp);
                        is_recording = false;
                    }
                }
            }
        });

        /*layoutRecord.setOnClickListener(new View.OnClickListener() {
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
        });*/

        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction() != null) {
                    if (intent.getAction().equalsIgnoreCase(Constants.ACTION.PLAY_MULTIFILES_COMPLETE)) {
                        Log.e(TAG, "receive PLAY_MULTIFILES_COMPLETE");
                        imgPlayStop.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                        is_playing = false;
                        stopVoicePlaying();
                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.IMPORT_FILE_COMPLETE)) {
                        if (check_user_voice_exist(currentSelectedName)) { //if file exist, add
                            recordPlayList.clear();
                            recordPlayList.add(currentSelectedName);
                            recordList.get(record_select).setFileExist(true);
                            recordArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }


            }
        };


        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.PLAY_MULTIFILES_COMPLETE);
            filter.addAction(Constants.ACTION.IMPORT_FILE_COMPLETE);
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

    private void startVoicePlaying() {
        if (voicePlay != null) {
            voicePlay.doStopAudioPlayMulti();
            voicePlay.audioPlayMultiFile(recordPlayList);

        } else {
            recordPlay.doStopAudioPlayMulti();
            recordPlay.audioPlayMultiFile(recordPlayList);
        }

        //handler
        mStartTime = SystemClock.elapsedRealtime();
        mHandler.postDelayed(mTickExecutor, 100);
    }

    private void stopVoicePlaying() {
        mStartTime = 0;
        mHandler.removeCallbacks(mTickExecutor);
    }

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
        mRecorder.setAudioSamplingRate(44100);
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

    protected  void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        mStartTime = 0;
        mHandler.removeCallbacks(mTickExecutor);
        if (mOutputFile != null) {
            mOutputFile.delete();
        }
    }

    private File getOutputFile() {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.TAIWAN);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/.tennisScoredBoard/user/"
                + currentSelectedName);
    }

    private void tick() {
        long time = (mStartTime < 0) ? 0 : (SystemClock.elapsedRealtime() - mStartTime);
        int minutes = (int) (time / 60000);
        int seconds = (int) (time / 1000) % 60;
        int milliseconds = (int) (time / 100) % 10;
        String time_string = minutes+":"+(seconds < 10 ? "0"+seconds : seconds)+"."+milliseconds;
        textViewTime.setText(time_string);
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
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.record_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_clear_all:
                AlertDialog.Builder confirmdialog = new AlertDialog.Builder(VoiceRecordActivity.this);
                confirmdialog.setTitle(getResources().getString(R.string.record_clear_all_msg));
                confirmdialog.setIcon(R.drawable.ball_icon);
                confirmdialog.setCancelable(false);
                confirmdialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clear_all_voice();
                        for (int i=0; i<recordList.size(); i++) {
                            recordList.get(i).setFileExist(false);
                        }
                        if (recordArrayAdapter != null)
                            recordArrayAdapter.notifyDataSetChanged();

                    }
                });
                confirmdialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                confirmdialog.show();

                break;

            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }


}
