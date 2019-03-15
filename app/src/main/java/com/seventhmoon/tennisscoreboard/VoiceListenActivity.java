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
    private ArrayList<Integer> gbr_woman_list = new ArrayList<>();

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
        gbr_woman_list.clear();

        gbr_man_list.add(R.raw.gbr_man_0_15);
        gbr_man_list.add(R.raw.gbr_man_0_30);
        gbr_man_list.add(R.raw.gbr_man_0_40);
        gbr_man_list.add(R.raw.gbr_man_15_0);
        gbr_man_list.add(R.raw.gbr_man_15_15);
        gbr_man_list.add(R.raw.gbr_man_15_30);
        gbr_man_list.add(R.raw.gbr_man_15_40);
        gbr_man_list.add(R.raw.gbr_man_30_0);
        gbr_man_list.add(R.raw.gbr_man_30_30);
        gbr_man_list.add(R.raw.gbr_man_30_40);
        gbr_man_list.add(R.raw.gbr_man_40_0);
        gbr_man_list.add(R.raw.gbr_man_40_15);
        gbr_man_list.add(R.raw.gbr_man_40_30);
        gbr_man_list.add(R.raw.gbr_man_40_40);
        gbr_man_list.add(R.raw.gbr_man_game);

        //gbr_woman
        gbr_woman_list.add(R.raw.gbr_woman_0_15);
        gbr_woman_list.add(R.raw.gbr_woman_0_30);
        gbr_woman_list.add(R.raw.gbr_woman_0_40);
        gbr_woman_list.add(R.raw.gbr_woman_15_0);
        gbr_woman_list.add(R.raw.gbr_woman_15_15);
        gbr_woman_list.add(R.raw.gbr_woman_15_30);
        gbr_woman_list.add(R.raw.gbr_woman_15_40);
        gbr_woman_list.add(R.raw.gbr_woman_30_0);
        gbr_woman_list.add(R.raw.gbr_woman_30_30);
        gbr_woman_list.add(R.raw.gbr_woman_30_40);
        gbr_woman_list.add(R.raw.gbr_woman_40_0);
        gbr_woman_list.add(R.raw.gbr_woman_40_15);
        gbr_woman_list.add(R.raw.gbr_woman_40_30);
        gbr_woman_list.add(R.raw.gbr_woman_40_40);
        gbr_woman_list.add(R.raw.gbr_woman_game);
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
            case 1://gbr_woman
                myPlayList.add(gbr_woman_list.get(call));
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
