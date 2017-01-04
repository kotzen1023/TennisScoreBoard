package com.seventhmoon.tennisscoreboard;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.seventhmoon.tennisscoreboard.Data.CurrentStatArrayAdapter;
import com.seventhmoon.tennisscoreboard.Data.CurrentStatItem;
import com.seventhmoon.tennisscoreboard.Data.State;

import java.util.ArrayList;

import static com.seventhmoon.tennisscoreboard.GameActivity.stack;

public class CurrentStatActivity extends AppCompatActivity{
    private static final String TAG = CurrentStatActivity.class.getName();

    private static ArrayList<CurrentStatItem> currrentArray = new ArrayList<>();

    private ListView listView;
    private CurrentStatArrayAdapter currentStatArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_current);

        listView = (ListView) findViewById(R.id.listViewStat);

        currrentArray.clear();

        State current_state = stack.peek();

        Intent intent = getIntent();
        String playerUp = intent.getStringExtra("PLAYER_UP");
        String playerDown = intent.getStringExtra("PLAYER_DOWN");

        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown);
        currrentArray.add(item1);

        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                String.valueOf(current_state.getAceCountUp()), String.valueOf(current_state.getAceCountDown()));
        currrentArray.add(item2);

        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                String.valueOf(current_state.getDoubleFaultUp()), String.valueOf(current_state.getDoubleFaultDown()));
        currrentArray.add(item3);

        CurrentStatItem item4 = new CurrentStatItem("1st Serve",
                String.valueOf(((float)(current_state.getFirstServeUp()-current_state.getFirstServeMissUp())/(float)current_state.getFirstServeUp())),
                String.valueOf(((float)(current_state.getFirstServeDown()-current_state.getFirstServeMissDown())/(float)current_state.getFirstServeDown())));
        currrentArray.add(item4);

        CurrentStatItem item5 = new CurrentStatItem("2nd Serve",
                String.valueOf(((float)(current_state.getSecondServeUp()-current_state.getDoubleFaultUp())/(float)current_state.getSecondServeUp())),
                String.valueOf(((float)(current_state.getSecondServeDown()-current_state.getDoubleFaultDown())/(float)current_state.getSecondServeDown())));
        currrentArray.add(item5);

        CurrentStatItem item6 = new CurrentStatItem("Winner",
                String.valueOf(current_state.getForehandWinnerUp()+current_state.getForehandVolleyUp()),
                String.valueOf(current_state.getForehandWinnerDown()+current_state.getForehandVolleyDown()));
        currrentArray.add(item6);

        CurrentStatItem item7 = new CurrentStatItem("Unforced Error",
                String.valueOf(current_state.getUnforceErrorUp()),
                String.valueOf(current_state.getUnforceErrorDown()));
        currrentArray.add(item7);

        currentStatArrayAdapter = new CurrentStatArrayAdapter(CurrentStatActivity.this, R.layout.stat_current_item, currrentArray);
        listView.setAdapter(currentStatArrayAdapter);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onResume();

        Log.d(TAG, "onPause");

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
