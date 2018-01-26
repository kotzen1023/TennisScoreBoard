package com.seventhmoon.tennisscoreboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seventhmoon.tennisscoreboard.Audio.VoicePlay;
import com.seventhmoon.tennisscoreboard.Data.ListenChooseArrayAdapter;
import com.seventhmoon.tennisscoreboard.Data.ListenChooseItem;

import java.util.ArrayList;
import java.util.Random;

import static com.seventhmoon.tennisscoreboard.GameActivity.voicePlay;


public class VoiceListenActivity extends AppCompatActivity {
    private static final String TAG = VoiceListenActivity.class.getName();

    public ArrayList<ListenChooseItem> listenList = new ArrayList<>();
    //private ListenChooseArrayAdapter listenChooseArrayAdapter;
    //private ListView listView;
    //private Context context;
    private VoicePlay listenPlay;
    private static ArrayList<Integer> myPlayList = new ArrayList<>();
    private ArrayList<Integer> gbr_man_list = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.voice_listen);

        Context context = getBaseContext();

        if (voicePlay == null) {
            listenPlay = new VoicePlay(context);
        } else {
            Log.e(TAG, "voicePlay is running");
        }



        initVoiceArray();

        ListView listView = findViewById(R.id.listViewListen);

        listenList.clear();

        ListenChooseItem item0 = new ListenChooseItem(getResources().getString(R.string.voice_gbr_man));
        listenList.add(item0);

        ListenChooseItem item1 = new ListenChooseItem(getResources().getString(R.string.voice_gbr_woman));
        listenList.add(item1);

        //ListenChooseItem item2 = new ListenChooseItem(getResources().getString(R.string.voice_user_record));
        //listenList.add(item2);

        ListenChooseArrayAdapter listenChooseArrayAdapter = new ListenChooseArrayAdapter(VoiceListenActivity.this, R.layout.voice_listen_choose_item, listenList);
        listView.setAdapter(listenChooseArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doRandomPlay(position);

            }
        });
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

        if (listenPlay != null) {
            listenPlay.doExit();
            listenPlay = null;
        }

        super.onDestroy();
    }

    private void initVoiceArray() {
        gbr_man_list.clear();
        gbr_man_list.add(R.raw.gbr_man_0_15);
        gbr_man_list.add(R.raw.gbr_man_0_30);
        gbr_man_list.add(R.raw.gbr_man_0_40);
        gbr_man_list.add(R.raw.gbr_man_1);
        gbr_man_list.add(R.raw.gbr_man_10);
        gbr_man_list.add(R.raw.gbr_man_11);
        gbr_man_list.add(R.raw.gbr_man_12);
        gbr_man_list.add(R.raw.gbr_man_13);
        gbr_man_list.add(R.raw.gbr_man_14);
        gbr_man_list.add(R.raw.gbr_man_15);
        gbr_man_list.add(R.raw.gbr_man_15_0);
        gbr_man_list.add(R.raw.gbr_man_15_15);
        gbr_man_list.add(R.raw.gbr_man_15_30);
        gbr_man_list.add(R.raw.gbr_man_15_40);
        gbr_man_list.add(R.raw.gbr_man_16);
        gbr_man_list.add(R.raw.gbr_man_17);
        gbr_man_list.add(R.raw.gbr_man_18);
        gbr_man_list.add(R.raw.gbr_man_19);
        gbr_man_list.add(R.raw.gbr_man_2);
        gbr_man_list.add(R.raw.gbr_man_20);
        gbr_man_list.add(R.raw.gbr_man_3);
        gbr_man_list.add(R.raw.gbr_man_30);
        gbr_man_list.add(R.raw.gbr_man_30_0);
        gbr_man_list.add(R.raw.gbr_man_30_30);
        gbr_man_list.add(R.raw.gbr_man_30_40);
        gbr_man_list.add(R.raw.gbr_man_4);
        gbr_man_list.add(R.raw.gbr_man_40);
        gbr_man_list.add(R.raw.gbr_man_40_0);
        gbr_man_list.add(R.raw.gbr_man_40_15);
        gbr_man_list.add(R.raw.gbr_man_40_30);
        gbr_man_list.add(R.raw.gbr_man_40_40);
        gbr_man_list.add(R.raw.gbr_man_5);
        gbr_man_list.add(R.raw.gbr_man_50);
        gbr_man_list.add(R.raw.gbr_man_6);
        gbr_man_list.add(R.raw.gbr_man_60);
        gbr_man_list.add(R.raw.gbr_man_7);
        gbr_man_list.add(R.raw.gbr_man_70);
        gbr_man_list.add(R.raw.gbr_man_8);
        gbr_man_list.add(R.raw.gbr_man_80);
        gbr_man_list.add(R.raw.gbr_man_9);
        gbr_man_list.add(R.raw.gbr_man_90);
        gbr_man_list.add(R.raw.gbr_man_ad_recv);
        gbr_man_list.add(R.raw.gbr_man_ad_serve);
        gbr_man_list.add(R.raw.gbr_man_all);
        gbr_man_list.add(R.raw.gbr_man_deciding_point);
        gbr_man_list.add(R.raw.gbr_man_first_set);
        gbr_man_list.add(R.raw.gbr_man_forth_set);
        gbr_man_list.add(R.raw.gbr_man_game);
        gbr_man_list.add(R.raw.gbr_man_love);
        gbr_man_list.add(R.raw.gbr_man_match);
        gbr_man_list.add(R.raw.gbr_man_second_set);
        gbr_man_list.add(R.raw.gbr_man_set);
        gbr_man_list.add(R.raw.gbr_man_third_set);
        gbr_man_list.add(R.raw.gbr_man_tiebreak);
    }

    private void doRandomPlay(int listenChoose) {
        Log.d(TAG, "doRandomPlay");
        Random r = new Random();
        int call = r.nextInt(gbr_man_list.size());
        Log.d(TAG, "call = "+call);
        myPlayList.clear();
        switch (listenChoose) {
            case 0://gbr man
                myPlayList.add(gbr_man_list.get(call));
                if (voicePlay == null) {
                    listenPlay.doStopAudioPlayMulti();
                    listenPlay.audioPlayMulti(myPlayList);
                } else {
                    voicePlay.doStopAudioPlayMulti();
                    voicePlay.audioPlayMulti(myPlayList);
                }

                break;
        }
        Log.d(TAG, "doRandomPlay");
    }

    public void onBackPressed() {

        finish();
    }
}
