package com.seventhmoon.tennisscoreboard;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.seventhmoon.tennisscoreboard.Data.CurrentStatArrayAdapter;
import com.seventhmoon.tennisscoreboard.Data.CurrentStatItem;
import com.seventhmoon.tennisscoreboard.Data.State;

import java.util.ArrayList;
import java.util.Locale;

import static com.seventhmoon.tennisscoreboard.GameActivity.stack;

public class CurrentStatActivity extends AppCompatActivity{
    private static final String TAG = CurrentStatActivity.class.getName();

    private static ArrayList<CurrentStatItem> currentArray = new ArrayList<>();

    private ListView listView;
    private CurrentStatArrayAdapter currentStatArrayAdapter;

    private static State first_set_stat = null;
    private static State second_set_stat = null;
    private static State third_set_stat = null;
    private static State forth_set_stat = null;
    private static State fifth_set_stat = null;

    private static State prev_first_set_stat = null;
    private static State prev_second_set_stat = null;
    private static State prev_third_set_stat = null;
    private static State prev_forth_set_stat = null;
    //private static State prev_fifth_set_stat = null;

    //private Spinner setSpinner;
    public ArrayAdapter<String> setAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_current);

        //String set = "0";

        Intent intent = getIntent();
        final String playerUp = intent.getStringExtra("PLAYER_UP");
        final String playerDown = intent.getStringExtra("PLAYER_DOWN");
        String set = intent.getStringExtra("TOTAL_SETS");

        currentArray.clear();

        final Locale current_local;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            current_local = getResources().getConfiguration().locale;
        } else {
            current_local = getResources().getConfiguration().getLocales().get(0);
        }

        byte thisSet = 6;

        if (set != null) {

            switch (set) {
                case "0": // 1 set
                    first_set_stat = stack.peek();
                    break;
                case "1": //3 set
                    for (State s : stack) {

                        if (s.getCurrent_set() < thisSet) {
                            Log.d(TAG, "set " + thisSet + " -> set " + s.getCurrent_set());
                            if (s.getCurrent_set() == 3) {
                                third_set_stat = s;
                            } else if (s.getCurrent_set() == 2) {
                                second_set_stat = s;
                            } else {
                                first_set_stat = s;
                            }
                        } else {
                            Log.d(TAG, "current_set = " + s.getCurrent_set());
                            if (s.getCurrent_set() == 2) {
                                prev_first_set_stat = s;
                            } else if (s.getCurrent_set() == 3) {
                                prev_second_set_stat = s;
                            }
                        }
                        thisSet = s.getCurrent_set();
                    }
                    break;
                case "2": // 5 set
                    for (State s : stack) {

                        if (s.getCurrent_set() < thisSet) {
                            Log.d(TAG, "set " + thisSet + " -> set " + s.getCurrent_set());
                            if (s.getCurrent_set() == 5) {
                                fifth_set_stat = s;
                            } else if (s.getCurrent_set() == 4) {
                                forth_set_stat = s;
                            } else if (s.getCurrent_set() == 3) {
                                third_set_stat = s;
                            } else if (s.getCurrent_set() == 2) {
                                second_set_stat = s;
                            } else {
                                first_set_stat = s;
                            }
                        } else {
                            Log.d(TAG, "current_set = " + s.getCurrent_set());
                            if (s.getCurrent_set() == 2) {
                                prev_first_set_stat = s;
                            } else if (s.getCurrent_set() == 3) {
                                prev_second_set_stat = s;
                            } else if (s.getCurrent_set() == 4) {
                                prev_third_set_stat = s;
                            } else if (s.getCurrent_set() == 5) {
                                prev_forth_set_stat = s;
                            }
                        }
                        thisSet = s.getCurrent_set();
                    }
                    break;
                default:
                    first_set_stat = stack.peek();
                    break;
            }
        } else {
            first_set_stat = stack.peek();
        }


        final State current_state = stack.peek();

        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }

        Spinner setSpinner = (Spinner) findViewById(R.id.spinnerSets);
        listView = (ListView) findViewById(R.id.listViewStat);

        String[] setList = {getResources().getString(R.string.stat_all_set),
                getResources().getString(R.string.stat_first_set),
                getResources().getString(R.string.stat_second_set),
                getResources().getString(R.string.stat_third_set),
                getResources().getString(R.string.stat_forth_set),
                getResources().getString(R.string.stat_fifth_set)};

        setAdapter = new ArrayAdapter<>(CurrentStatActivity.this, android.R.layout.simple_spinner_dropdown_item, setList);
        setSpinner.setAdapter(setAdapter);

        setSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentArray.clear();
                if (position == 1) { //first set
                    if (first_set_stat != null) {
                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                String.valueOf(first_set_stat.getAceCountUp()), String.valueOf(first_set_stat.getAceCountDown()),
                                Integer.valueOf(first_set_stat.getAceCountUp()), Integer.valueOf(first_set_stat.getAceCountDown()));
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                String.valueOf(first_set_stat.getDoubleFaultUp()), String.valueOf(first_set_stat.getDoubleFaultDown()),
                                Integer.valueOf(first_set_stat.getDoubleFaultUp()), Integer.valueOf(first_set_stat.getDoubleFaultDown()));
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error),
                                String.valueOf(first_set_stat.getUnforceErrorUp()),
                                String.valueOf(first_set_stat.getUnforceErrorDown()),
                                Integer.valueOf(first_set_stat.getUnforceErrorUp()),
                                Integer.valueOf(first_set_stat.getUnforceErrorDown()));
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error),
                                String.valueOf(first_set_stat.getForceErrorUp()),
                                String.valueOf(first_set_stat.getForceErrorDown()),
                                Integer.valueOf(first_set_stat.getForceErrorUp()),
                                Integer.valueOf(first_set_stat.getForceErrorDown()));
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), first_set_stat.getFirstServeUp() == 0 ? "0%" :
                                String.format(current_local, "%.1f", ((float) (first_set_stat.getFirstServeUp() - first_set_stat.getFirstServeMissUp()) / (float) first_set_stat.getFirstServeUp()) * 100) + "%",
                                first_set_stat.getFirstServeDown() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (first_set_stat.getFirstServeDown() - first_set_stat.getFirstServeMissDown()) / (float) first_set_stat.getFirstServeDown()) * 100) + "%",

                                (int)(((float) (first_set_stat.getFirstServeUp() - first_set_stat.getFirstServeMissUp()) / (float) first_set_stat.getFirstServeUp()) * 100),
                                (int)(((float) (first_set_stat.getFirstServeDown() - first_set_stat.getFirstServeMissDown()) / (float) first_set_stat.getFirstServeDown())) *100);
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won),
                                first_set_stat.getFirstServeWonUp() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) first_set_stat.getFirstServeWonUp() / (float) (first_set_stat.getFirstServeWonUp() + first_set_stat.getFirstServeLostUp())) * 100) + "%",
                                first_set_stat.getFirstServeWonDown() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) first_set_stat.getFirstServeWonDown() / (float) (first_set_stat.getFirstServeWonDown() + first_set_stat.getFirstServeLostDown())) * 100) + "%",
                                (int)(((float) first_set_stat.getFirstServeWonUp() / (float) (first_set_stat.getFirstServeWonUp() + first_set_stat.getFirstServeLostUp()))  * 100),
                                (int)(((float) first_set_stat.getFirstServeWonDown() / (float) (first_set_stat.getFirstServeWonDown() + first_set_stat.getFirstServeLostDown())) * 100));
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), first_set_stat.getSecondServeUp() == 0 ? "0%" :
                                String.format(current_local, "%.1f", ((float) (first_set_stat.getSecondServeUp() - first_set_stat.getDoubleFaultUp()) / (float) first_set_stat.getSecondServeUp()) * 100) + "%",
                                first_set_stat.getSecondServeDown() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (first_set_stat.getSecondServeDown() - first_set_stat.getDoubleFaultDown()) / (float) first_set_stat.getSecondServeDown()) * 100) + "%",
                                (int)(((float) (first_set_stat.getSecondServeUp() - first_set_stat.getDoubleFaultUp()) / (float) first_set_stat.getSecondServeUp()) * 100),
                                (int)(((float) (first_set_stat.getSecondServeDown() - first_set_stat.getDoubleFaultDown()) / (float) first_set_stat.getSecondServeDown()) * 100));
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won),
                                first_set_stat.getSecondServeWonUp() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) first_set_stat.getSecondServeWonUp() / (float) (first_set_stat.getSecondServeWonUp() + first_set_stat.getSecondServeLostUp())) * 100) + "%",
                                first_set_stat.getSecondServeDown() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) first_set_stat.getSecondServeWonDown() / (float) (first_set_stat.getSecondServeWonDown() + first_set_stat.getSecondServeLostDown())) * 100) + "%",
                                (int)(((float) first_set_stat.getSecondServeWonUp() / (float) (first_set_stat.getSecondServeWonUp() + first_set_stat.getSecondServeLostUp())) * 100),
                                (int)(((float) first_set_stat.getSecondServeWonDown() / (float) (first_set_stat.getSecondServeWonDown() + first_set_stat.getSecondServeLostDown())) * 100));
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner),
                                String.valueOf(first_set_stat.getForehandWinnerUp() +
                                        first_set_stat.getBackhandWinnerUp() +
                                        first_set_stat.getForehandVolleyUp() +
                                        first_set_stat.getBackhandVolleyUp()),
                                String.valueOf(first_set_stat.getForehandWinnerDown() +
                                        first_set_stat.getBackhandWinnerDown() +
                                        first_set_stat.getForehandVolleyDown() +
                                        first_set_stat.getBackhandVolleyDown()),
                                (first_set_stat.getForehandWinnerUp() +
                                        first_set_stat.getBackhandWinnerUp() +
                                        first_set_stat.getForehandVolleyUp() +
                                        first_set_stat.getBackhandVolleyUp()),
                                (first_set_stat.getForehandWinnerDown() +
                                        first_set_stat.getBackhandWinnerDown() +
                                        first_set_stat.getForehandVolleyDown() +
                                        first_set_stat.getBackhandVolleyDown()));
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner),
                                String.valueOf(first_set_stat.getForehandWinnerUp() +
                                        first_set_stat.getForehandVolleyUp()),
                                String.valueOf(first_set_stat.getForehandWinnerDown() +
                                        first_set_stat.getForehandVolleyDown()),
                                (first_set_stat.getForehandWinnerUp() +
                                        first_set_stat.getForehandVolleyUp()),
                                (first_set_stat.getForehandWinnerDown() +
                                        first_set_stat.getForehandVolleyDown()));
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner),
                                String.valueOf(first_set_stat.getBackhandWinnerUp() +
                                        first_set_stat.getBackhandVolleyUp()),
                                String.valueOf(first_set_stat.getBackhandWinnerDown() +
                                        first_set_stat.getBackhandVolleyDown()),
                                (first_set_stat.getBackhandWinnerUp() +
                                        first_set_stat.getBackhandVolleyUp()),
                                (first_set_stat.getBackhandWinnerDown() +
                                        first_set_stat.getBackhandVolleyDown()));
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won),
                                String.valueOf(first_set_stat.getForehandVolleyUp() +
                                        first_set_stat.getBackhandVolleyUp()),
                                String.valueOf(first_set_stat.getForehandVolleyDown() +
                                        first_set_stat.getBackhandVolleyDown()),
                                (first_set_stat.getForehandVolleyUp() +
                                        first_set_stat.getBackhandVolleyUp()),
                                (first_set_stat.getForehandVolleyDown() +
                                        first_set_stat.getBackhandVolleyDown()));
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won),
                                first_set_stat.getBreakPointUp() == 0 ? "0%" : "("+
                                        String.valueOf(first_set_stat.getBreakPointUp() - first_set_stat.getBreakPointMissUp()) +"/"+String.valueOf(first_set_stat.getBreakPointUp())+") "+
                                        String.format(current_local, "%.1f", ((float) (first_set_stat.getBreakPointUp() - first_set_stat.getBreakPointMissUp()) / (float) first_set_stat.getBreakPointUp()) * 100) + "%",
                                first_set_stat.getBreakPointDown() == 0 ? "0%" : "("+
                                        String.valueOf(first_set_stat.getBreakPointDown() - first_set_stat.getBreakPointMissDown()) +"/"+String.valueOf(first_set_stat.getBreakPointDown())+") "+
                                        String.format(current_local, "%.1f", ((float) (first_set_stat.getBreakPointDown() - first_set_stat.getBreakPointMissDown()) / (float) first_set_stat.getBreakPointDown()) * 100) + "%",
                                (int)(((float) (first_set_stat.getBreakPointUp() - first_set_stat.getBreakPointMissUp()) / (float) first_set_stat.getBreakPointUp()) * 100),
                                (int)(((float) (first_set_stat.getBreakPointDown() - first_set_stat.getBreakPointMissDown()) / (float) first_set_stat.getBreakPointDown()) * 100));
                        currentArray.add(item11);
                        //total point
                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point),
                                String.valueOf(first_set_stat.getForehandWinnerUp() +
                                        first_set_stat.getBackhandWinnerUp() +
                                        first_set_stat.getForehandVolleyUp() +
                                        first_set_stat.getBackhandVolleyUp() +
                                        first_set_stat.getDoubleFaultDown() +
                                        first_set_stat.getUnforceErrorDown() +
                                        first_set_stat.getFoulToLoseDown()),
                                String.valueOf(first_set_stat.getForehandWinnerDown() +
                                        first_set_stat.getBackhandWinnerDown() +
                                        first_set_stat.getForehandVolleyDown() +
                                        first_set_stat.getBackhandVolleyDown() +
                                        first_set_stat.getDoubleFaultUp() +
                                        first_set_stat.getUnforceErrorUp() +
                                        first_set_stat.getFoulToLoseUp()),
                                (first_set_stat.getForehandWinnerUp() +
                                        first_set_stat.getBackhandWinnerUp() +
                                        first_set_stat.getForehandVolleyUp() +
                                        first_set_stat.getBackhandVolleyUp() +
                                        first_set_stat.getDoubleFaultDown() +
                                        first_set_stat.getUnforceErrorDown() +
                                        first_set_stat.getFoulToLoseDown()),
                                (first_set_stat.getForehandWinnerDown() +
                                        first_set_stat.getBackhandWinnerDown() +
                                        first_set_stat.getForehandVolleyDown() +
                                        first_set_stat.getBackhandVolleyDown() +
                                        first_set_stat.getDoubleFaultUp() +
                                        first_set_stat.getUnforceErrorUp() +
                                        first_set_stat.getFoulToLoseUp()));
                        currentArray.add(item12);
                    } else {
                        Log.d(TAG, "current_state = null");

                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                "0", "0", 0, 0);
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                "0", "0", 0, 0);
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error), "0", "0", 0, 0);
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error), "0", "0", 0, 0);
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), "0%", "0%", 0, 0);
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), "0%", "0%", 0, 0);
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner), "0", "0", 0, 0);
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner), "0", "0", 0, 0);
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner), "0", "0", 0, 0);
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won), "0", "0", 0, 0);
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won), "0%", "0%", 0, 0);
                        currentArray.add(item11);

                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point), "0", "0", 0, 0);
                        currentArray.add(item12);
                    }
                } else if (position == 2) { //second set
                    if (second_set_stat != null && prev_first_set_stat != null) {
                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                String.valueOf(second_set_stat.getAceCountUp()-prev_first_set_stat.getAceCountUp()), String.valueOf(second_set_stat.getAceCountDown()-prev_first_set_stat.getAceCountDown()),
                                (second_set_stat.getAceCountUp()-prev_first_set_stat.getAceCountUp()),
                                (second_set_stat.getAceCountDown()-prev_first_set_stat.getAceCountDown()));
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                String.valueOf(second_set_stat.getDoubleFaultUp()-prev_first_set_stat.getDoubleFaultUp()), String.valueOf(second_set_stat.getDoubleFaultDown()-prev_first_set_stat.getDoubleFaultDown()),
                                (second_set_stat.getDoubleFaultUp()-prev_first_set_stat.getDoubleFaultUp()),
                                (second_set_stat.getDoubleFaultDown()-prev_first_set_stat.getDoubleFaultDown()));
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error),
                                String.valueOf(second_set_stat.getUnforceErrorUp()-prev_first_set_stat.getUnforceErrorUp()),
                                String.valueOf(second_set_stat.getUnforceErrorDown()-prev_first_set_stat.getUnforceErrorDown()),
                                (second_set_stat.getUnforceErrorUp()-prev_first_set_stat.getUnforceErrorUp()),
                                (second_set_stat.getUnforceErrorDown()-prev_first_set_stat.getUnforceErrorDown()));
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error),
                                String.valueOf(second_set_stat.getForceErrorUp()-prev_first_set_stat.getForceErrorUp()),
                                String.valueOf(second_set_stat.getForceErrorDown()-prev_first_set_stat.getForceErrorDown()),
                                (second_set_stat.getForceErrorUp()-prev_first_set_stat.getForceErrorUp()),
                                (second_set_stat.getForceErrorDown()-prev_first_set_stat.getForceErrorDown()));
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve),
                                (second_set_stat.getFirstServeUp() - prev_first_set_stat.getFirstServeUp()) == 0 ? "0%" :
                                String.format(current_local, "%.1f", ((float) ((second_set_stat.getFirstServeUp() - prev_first_set_stat.getFirstServeUp()) - (second_set_stat.getFirstServeMissUp() - prev_first_set_stat.getFirstServeMissUp())) / (float) (second_set_stat.getFirstServeUp() - prev_first_set_stat.getFirstServeUp())) * 100) + "%",

                                (second_set_stat.getFirstServeDown() - prev_first_set_stat.getFirstServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((second_set_stat.getFirstServeDown() - prev_first_set_stat.getFirstServeDown()) - (second_set_stat.getFirstServeMissDown() - prev_first_set_stat.getFirstServeMissDown())) / (float) (second_set_stat.getFirstServeDown() - prev_first_set_stat.getFirstServeDown())) * 100) + "%",
                                (int)(((float) ((second_set_stat.getFirstServeUp() - prev_first_set_stat.getFirstServeUp()) - (second_set_stat.getFirstServeMissUp() - prev_first_set_stat.getFirstServeMissUp())) / (float) (second_set_stat.getFirstServeUp() - prev_first_set_stat.getFirstServeUp())) * 100),
                                (int)(((float) ((second_set_stat.getFirstServeDown() - prev_first_set_stat.getFirstServeDown()) - (second_set_stat.getFirstServeMissDown() - prev_first_set_stat.getFirstServeMissDown())) / (float) (second_set_stat.getFirstServeDown() - prev_first_set_stat.getFirstServeDown())) * 100));
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won),
                                (second_set_stat.getFirstServeWonUp() - prev_first_set_stat.getFirstServeWonUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (second_set_stat.getFirstServeWonUp() - prev_first_set_stat.getFirstServeWonUp()) / (float) ((second_set_stat.getFirstServeWonUp() - prev_first_set_stat.getFirstServeWonUp()) + (second_set_stat.getFirstServeLostUp() - prev_first_set_stat.getFirstServeLostUp()))) * 100) + "%",

                                (second_set_stat.getFirstServeWonDown() - prev_first_set_stat.getFirstServeWonDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (second_set_stat.getFirstServeWonDown() - prev_first_set_stat.getFirstServeWonDown()) / (float) ((second_set_stat.getFirstServeWonDown() - prev_first_set_stat.getFirstServeWonDown()) + (second_set_stat.getFirstServeLostDown() - prev_first_set_stat.getFirstServeLostDown()))) * 100) + "%",
                                (int)(((float) (second_set_stat.getFirstServeWonUp() - prev_first_set_stat.getFirstServeWonUp()) / (float) ((second_set_stat.getFirstServeWonUp() - prev_first_set_stat.getFirstServeWonUp()) + (second_set_stat.getFirstServeLostUp() - prev_first_set_stat.getFirstServeLostUp()))) * 100),
                                (int)(((float) (second_set_stat.getFirstServeWonDown() - prev_first_set_stat.getFirstServeWonDown()) / (float) ((second_set_stat.getFirstServeWonDown() - prev_first_set_stat.getFirstServeWonDown()) + (second_set_stat.getFirstServeLostDown() - prev_first_set_stat.getFirstServeLostDown()))) * 100));
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve),
                                (second_set_stat.getSecondServeUp() - prev_first_set_stat.getSecondServeUp()) == 0 ? "0%" :
                                String.format(current_local, "%.1f", ((float) ((second_set_stat.getSecondServeUp() - prev_first_set_stat.getSecondServeUp()) - (second_set_stat.getDoubleFaultUp() - prev_first_set_stat.getDoubleFaultUp())) / (float) (second_set_stat.getSecondServeUp() - prev_first_set_stat.getSecondServeUp())) * 100) + "%",
                                (second_set_stat.getSecondServeDown() - prev_first_set_stat.getSecondServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((second_set_stat.getSecondServeDown() - prev_first_set_stat.getSecondServeDown()) - (second_set_stat.getDoubleFaultDown() - prev_first_set_stat.getDoubleFaultDown())) / (float) (second_set_stat.getSecondServeDown() - prev_first_set_stat.getSecondServeDown())) * 100) + "%",
                                (int)(((float) ((second_set_stat.getSecondServeUp() - prev_first_set_stat.getSecondServeUp()) - (second_set_stat.getDoubleFaultUp() - prev_first_set_stat.getDoubleFaultUp())) / (float) (second_set_stat.getSecondServeUp() - prev_first_set_stat.getSecondServeUp())) * 100),
                                (int)(((float) ((second_set_stat.getSecondServeDown() - prev_first_set_stat.getSecondServeDown()) - (second_set_stat.getDoubleFaultDown() - prev_first_set_stat.getDoubleFaultDown())) / (float) (second_set_stat.getSecondServeDown() - prev_first_set_stat.getSecondServeDown())) * 100));
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won),
                                (second_set_stat.getSecondServeWonUp() - prev_first_set_stat.getSecondServeWonUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (second_set_stat.getSecondServeWonUp() - prev_first_set_stat.getSecondServeWonUp()) / (float) ((second_set_stat.getSecondServeWonUp() - prev_first_set_stat.getSecondServeWonUp()) + (second_set_stat.getSecondServeLostUp() - prev_first_set_stat.getSecondServeLostUp()))) * 100) + "%",
                                (second_set_stat.getSecondServeDown() - prev_first_set_stat.getSecondServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (second_set_stat.getSecondServeWonDown() - prev_first_set_stat.getSecondServeWonDown()) / (float) ((second_set_stat.getSecondServeWonDown() - prev_first_set_stat.getSecondServeWonDown()) + (second_set_stat.getSecondServeLostDown() - prev_first_set_stat.getSecondServeLostDown()))) * 100) + "%",
                                (int)(((float) (second_set_stat.getSecondServeWonUp() - prev_first_set_stat.getSecondServeWonUp()) / (float) ((second_set_stat.getSecondServeWonUp() - prev_first_set_stat.getSecondServeWonUp()) + (second_set_stat.getSecondServeLostUp() - prev_first_set_stat.getSecondServeLostUp()))) * 100),
                                (int)(((float) (second_set_stat.getSecondServeWonDown() - prev_first_set_stat.getSecondServeWonDown()) / (float) ((second_set_stat.getSecondServeWonDown() - prev_first_set_stat.getSecondServeWonDown()) + (second_set_stat.getSecondServeLostDown() - prev_first_set_stat.getSecondServeLostDown()))) * 100));
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner),
                                String.valueOf((second_set_stat.getForehandWinnerUp() - prev_first_set_stat.getForehandWinnerUp())+
                                        (second_set_stat.getBackhandWinnerUp() - prev_first_set_stat.getBackhandWinnerUp()) +
                                        (second_set_stat.getForehandVolleyUp() - prev_first_set_stat.getForehandVolleyUp()) +
                                        (second_set_stat.getBackhandVolleyUp() - prev_first_set_stat.getBackhandVolleyUp())),
                                String.valueOf((second_set_stat.getForehandWinnerDown() - prev_first_set_stat.getForehandWinnerDown()) +
                                        (second_set_stat.getBackhandWinnerDown() - prev_first_set_stat.getBackhandWinnerDown())+
                                        (second_set_stat.getForehandVolleyDown() - prev_first_set_stat.getForehandVolleyDown())+
                                        (second_set_stat.getBackhandVolleyDown() - prev_first_set_stat.getBackhandVolleyDown())),
                                ((second_set_stat.getForehandWinnerUp() - prev_first_set_stat.getForehandWinnerUp())+
                                        (second_set_stat.getBackhandWinnerUp() - prev_first_set_stat.getBackhandWinnerUp()) +
                                        (second_set_stat.getForehandVolleyUp() - prev_first_set_stat.getForehandVolleyUp()) +
                                        (second_set_stat.getBackhandVolleyUp() - prev_first_set_stat.getBackhandVolleyUp())),
                                ((second_set_stat.getForehandWinnerDown() - prev_first_set_stat.getForehandWinnerDown()) +
                                        (second_set_stat.getBackhandWinnerDown() - prev_first_set_stat.getBackhandWinnerDown())+
                                        (second_set_stat.getForehandVolleyDown() - prev_first_set_stat.getForehandVolleyDown())+
                                        (second_set_stat.getBackhandVolleyDown() - prev_first_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner),
                                String.valueOf((second_set_stat.getForehandWinnerUp() - prev_first_set_stat.getForehandWinnerUp())+
                                        (second_set_stat.getForehandVolleyUp() - prev_first_set_stat.getForehandVolleyUp())),
                                String.valueOf((second_set_stat.getForehandWinnerDown() - prev_first_set_stat.getForehandWinnerDown()) +
                                        (second_set_stat.getForehandVolleyDown() - prev_first_set_stat.getForehandVolleyDown())),
                                ((second_set_stat.getForehandWinnerUp() - prev_first_set_stat.getForehandWinnerUp())+
                                        (second_set_stat.getForehandVolleyUp() - prev_first_set_stat.getForehandVolleyUp())),
                                ((second_set_stat.getForehandWinnerDown() - prev_first_set_stat.getForehandWinnerDown()) +
                                        (second_set_stat.getForehandVolleyDown() - prev_first_set_stat.getForehandVolleyDown())));
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner),
                                String.valueOf((second_set_stat.getBackhandWinnerUp() - prev_first_set_stat.getBackhandWinnerUp()) +
                                        (second_set_stat.getBackhandVolleyUp() - prev_first_set_stat.getBackhandVolleyUp())),
                                String.valueOf((second_set_stat.getBackhandWinnerDown() - prev_first_set_stat.getBackhandWinnerDown())+
                                        (second_set_stat.getBackhandVolleyDown() - prev_first_set_stat.getBackhandVolleyDown())),
                                ((second_set_stat.getBackhandWinnerUp() - prev_first_set_stat.getBackhandWinnerUp()) +
                                        (second_set_stat.getBackhandVolleyUp() - prev_first_set_stat.getBackhandVolleyUp())),
                                ((second_set_stat.getBackhandWinnerDown() - prev_first_set_stat.getBackhandWinnerDown())+
                                        (second_set_stat.getBackhandVolleyDown() - prev_first_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won),
                                String.valueOf((second_set_stat.getForehandVolleyUp() - prev_first_set_stat.getForehandVolleyUp())+
                                        (second_set_stat.getBackhandVolleyUp() - prev_first_set_stat.getBackhandVolleyUp())),
                                String.valueOf((second_set_stat.getForehandVolleyDown() - prev_first_set_stat.getForehandVolleyDown()) +
                                        (second_set_stat.getBackhandVolleyDown() - prev_first_set_stat.getBackhandVolleyDown())),
                                ((second_set_stat.getForehandVolleyUp() - prev_first_set_stat.getForehandVolleyUp())+
                                        (second_set_stat.getBackhandVolleyUp() - prev_first_set_stat.getBackhandVolleyUp())),
                                ((second_set_stat.getForehandVolleyDown() - prev_first_set_stat.getForehandVolleyDown()) +
                                        (second_set_stat.getBackhandVolleyDown() - prev_first_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won),
                                (second_set_stat.getBreakPointUp() - prev_first_set_stat.getBreakPointUp()) == 0 ? "0%" : "("+
                                        String.valueOf((second_set_stat.getBreakPointUp() - prev_first_set_stat.getBreakPointUp()) - (second_set_stat.getBreakPointMissUp() - prev_first_set_stat.getBreakPointMissUp())) +"/"+String.valueOf((second_set_stat.getBreakPointUp() - prev_first_set_stat.getBreakPointUp()))+") "+
                                        String.format(current_local, "%.1f", ((float) ((second_set_stat.getBreakPointUp() - prev_first_set_stat.getBreakPointUp()) - (second_set_stat.getBreakPointMissUp() - prev_first_set_stat.getBreakPointMissUp())) / (float) (second_set_stat.getBreakPointUp() - prev_first_set_stat.getBreakPointUp())) * 100) + "%",
                                (second_set_stat.getBreakPointDown() - prev_first_set_stat.getBreakPointDown()) == 0 ? "0%" : "("+
                                        String.valueOf((second_set_stat.getBreakPointDown() - prev_first_set_stat.getBreakPointDown()) - (second_set_stat.getBreakPointMissDown() - prev_first_set_stat.getBreakPointMissDown())) +"/"+String.valueOf((second_set_stat.getBreakPointDown() - prev_first_set_stat.getBreakPointDown()))+") "+
                                        String.format(current_local, "%.1f", ((float) ((second_set_stat.getBreakPointDown() - prev_first_set_stat.getBreakPointDown()) - (second_set_stat.getBreakPointMissDown() - prev_first_set_stat.getBreakPointMissDown())) / (float) (second_set_stat.getBreakPointDown() - prev_first_set_stat.getBreakPointDown())) * 100) + "%",
                                (int)(((float) ((second_set_stat.getBreakPointUp() - prev_first_set_stat.getBreakPointUp()) - (second_set_stat.getBreakPointMissUp() - prev_first_set_stat.getBreakPointMissUp())) / (float) (second_set_stat.getBreakPointUp() - prev_first_set_stat.getBreakPointUp()))),
                                (int)(((float) ((second_set_stat.getBreakPointDown() - prev_first_set_stat.getBreakPointDown()) - (second_set_stat.getBreakPointMissDown() - prev_first_set_stat.getBreakPointMissDown())) / (float) (second_set_stat.getBreakPointDown() - prev_first_set_stat.getBreakPointDown()))));
                        currentArray.add(item11);
                        //total point
                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point),
                                String.valueOf((second_set_stat.getForehandWinnerUp() - prev_first_set_stat.getForehandWinnerUp()) +
                                        (second_set_stat.getBackhandWinnerUp() - prev_first_set_stat.getBackhandWinnerUp()) +
                                        (second_set_stat.getForehandVolleyUp() - prev_first_set_stat.getForehandVolleyUp()) +
                                        (second_set_stat.getBackhandVolleyUp() - prev_first_set_stat.getBackhandVolleyUp()) +
                                        (second_set_stat.getDoubleFaultDown() - prev_first_set_stat.getDoubleFaultDown()) +
                                        (second_set_stat.getUnforceErrorDown() - prev_first_set_stat.getUnforceErrorDown()) +
                                        (second_set_stat.getFoulToLoseDown() - prev_first_set_stat.getFoulToLoseDown())),
                                String.valueOf((second_set_stat.getForehandWinnerDown() - prev_first_set_stat.getForehandWinnerDown()) +
                                        (second_set_stat.getBackhandWinnerDown() - prev_first_set_stat.getBackhandWinnerDown()) +
                                        (second_set_stat.getForehandVolleyDown() - prev_first_set_stat.getForehandVolleyDown()) +
                                        (second_set_stat.getBackhandVolleyDown() - prev_first_set_stat.getBackhandVolleyDown()) +
                                        (second_set_stat.getDoubleFaultUp() - prev_first_set_stat.getDoubleFaultUp()) +
                                        (second_set_stat.getUnforceErrorUp() - prev_first_set_stat.getUnforceErrorUp()) +
                                        (second_set_stat.getFoulToLoseUp() - prev_first_set_stat.getFoulToLoseUp())),
                                ((second_set_stat.getForehandWinnerUp() - prev_first_set_stat.getForehandWinnerUp()) +
                                        (second_set_stat.getBackhandWinnerUp() - prev_first_set_stat.getBackhandWinnerUp()) +
                                        (second_set_stat.getForehandVolleyUp() - prev_first_set_stat.getForehandVolleyUp()) +
                                        (second_set_stat.getBackhandVolleyUp() - prev_first_set_stat.getBackhandVolleyUp()) +
                                        (second_set_stat.getDoubleFaultDown() - prev_first_set_stat.getDoubleFaultDown()) +
                                        (second_set_stat.getUnforceErrorDown() - prev_first_set_stat.getUnforceErrorDown()) +
                                        (second_set_stat.getFoulToLoseDown() - prev_first_set_stat.getFoulToLoseDown())),
                                ((second_set_stat.getForehandWinnerDown() - prev_first_set_stat.getForehandWinnerDown()) +
                                        (second_set_stat.getBackhandWinnerDown() - prev_first_set_stat.getBackhandWinnerDown()) +
                                        (second_set_stat.getForehandVolleyDown() - prev_first_set_stat.getForehandVolleyDown()) +
                                        (second_set_stat.getBackhandVolleyDown() - prev_first_set_stat.getBackhandVolleyDown()) +
                                        (second_set_stat.getDoubleFaultUp() - prev_first_set_stat.getDoubleFaultUp()) +
                                        (second_set_stat.getUnforceErrorUp() - prev_first_set_stat.getUnforceErrorUp()) +
                                        (second_set_stat.getFoulToLoseUp() - prev_first_set_stat.getFoulToLoseUp())));
                        currentArray.add(item12);
                    } else {
                        Log.d(TAG, "current_state = null");

                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                "0", "0", 0, 0);
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                "0", "0", 0, 0);
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error), "0", "0", 0, 0);
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error), "0", "0", 0, 0);
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), "0%", "0%", 0, 0);
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), "0%", "0%", 0, 0);
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner), "0", "0", 0, 0);
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner), "0", "0", 0, 0);
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner), "0", "0", 0, 0);
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won), "0", "0", 0, 0);
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won), "0%", "0%", 0, 0);
                        currentArray.add(item11);

                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point), "0", "0", 0, 0);
                        currentArray.add(item12);
                    }
                } else if (position == 3) {
                    if (third_set_stat != null && prev_second_set_stat != null) {
                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                String.valueOf(third_set_stat.getAceCountUp()-prev_second_set_stat.getAceCountUp()), String.valueOf(third_set_stat.getAceCountDown()-prev_second_set_stat.getAceCountDown()),
                                (third_set_stat.getAceCountUp()-prev_second_set_stat.getAceCountUp()),
                                (third_set_stat.getAceCountDown()-prev_second_set_stat.getAceCountDown()));
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                String.valueOf(third_set_stat.getDoubleFaultUp()-prev_second_set_stat.getDoubleFaultUp()), String.valueOf(third_set_stat.getDoubleFaultDown()-prev_second_set_stat.getDoubleFaultDown()),
                                (third_set_stat.getDoubleFaultUp()-prev_second_set_stat.getDoubleFaultUp()),
                                (third_set_stat.getDoubleFaultDown()-prev_second_set_stat.getDoubleFaultDown()));
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error),
                                String.valueOf(third_set_stat.getUnforceErrorUp()-prev_second_set_stat.getUnforceErrorUp()),
                                String.valueOf(third_set_stat.getUnforceErrorDown()-prev_second_set_stat.getUnforceErrorDown()),
                                (third_set_stat.getUnforceErrorUp()-prev_second_set_stat.getUnforceErrorUp()),
                                (third_set_stat.getUnforceErrorDown()-prev_second_set_stat.getUnforceErrorDown()));
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error),
                                String.valueOf(third_set_stat.getForceErrorUp()-prev_second_set_stat.getForceErrorUp()),
                                String.valueOf(third_set_stat.getForceErrorDown()-prev_second_set_stat.getForceErrorDown()),
                                (third_set_stat.getForceErrorUp()-prev_second_set_stat.getForceErrorUp()),
                                (third_set_stat.getForceErrorDown()-prev_second_set_stat.getForceErrorDown()));
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve),
                                (third_set_stat.getFirstServeUp() - prev_second_set_stat.getFirstServeUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((third_set_stat.getFirstServeUp() - prev_second_set_stat.getFirstServeUp()) - (third_set_stat.getFirstServeMissUp() - prev_second_set_stat.getFirstServeMissUp())) / (float) (third_set_stat.getFirstServeUp() - prev_second_set_stat.getFirstServeUp())) * 100) + "%",

                                (third_set_stat.getFirstServeDown() - prev_second_set_stat.getFirstServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((third_set_stat.getFirstServeDown() - prev_second_set_stat.getFirstServeDown()) - (third_set_stat.getFirstServeMissDown() - prev_second_set_stat.getFirstServeMissDown())) / (float) (third_set_stat.getFirstServeDown() - prev_second_set_stat.getFirstServeDown())) * 100) + "%",
                                (int)(((float) ((third_set_stat.getFirstServeUp() - prev_second_set_stat.getFirstServeUp()) - (third_set_stat.getFirstServeMissUp() - prev_second_set_stat.getFirstServeMissUp())) / (float) (third_set_stat.getFirstServeUp() - prev_second_set_stat.getFirstServeUp())) * 100),
                                (int)(((float) ((third_set_stat.getFirstServeDown() - prev_second_set_stat.getFirstServeDown()) - (third_set_stat.getFirstServeMissDown() - prev_second_set_stat.getFirstServeMissDown())) / (float) (third_set_stat.getFirstServeDown() - prev_second_set_stat.getFirstServeDown())) * 100));
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won),
                                (third_set_stat.getFirstServeWonUp() - prev_second_set_stat.getFirstServeWonUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (third_set_stat.getFirstServeWonUp() - prev_second_set_stat.getFirstServeWonUp()) / (float) ((third_set_stat.getFirstServeWonUp() - prev_second_set_stat.getFirstServeWonUp()) + (third_set_stat.getFirstServeLostUp() - prev_second_set_stat.getFirstServeLostUp()))) * 100) + "%",

                                (third_set_stat.getFirstServeWonDown() - prev_second_set_stat.getFirstServeWonDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (third_set_stat.getFirstServeWonDown() - prev_second_set_stat.getFirstServeWonDown()) / (float) ((third_set_stat.getFirstServeWonDown() - prev_second_set_stat.getFirstServeWonDown()) + (third_set_stat.getFirstServeLostDown() - prev_second_set_stat.getFirstServeLostDown()))) * 100) + "%",
                                (int)(((float) (third_set_stat.getFirstServeWonUp() - prev_second_set_stat.getFirstServeWonUp()) / (float) ((third_set_stat.getFirstServeWonUp() - prev_second_set_stat.getFirstServeWonUp()) + (third_set_stat.getFirstServeLostUp() - prev_second_set_stat.getFirstServeLostUp()))) * 100),
                                (int)(((float) (third_set_stat.getFirstServeWonDown() - prev_second_set_stat.getFirstServeWonDown()) / (float) ((third_set_stat.getFirstServeWonDown() - prev_second_set_stat.getFirstServeWonDown()) + (third_set_stat.getFirstServeLostDown() - prev_second_set_stat.getFirstServeLostDown()))) * 100));
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve),
                                (third_set_stat.getSecondServeUp() - prev_second_set_stat.getSecondServeUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((third_set_stat.getSecondServeUp() - prev_second_set_stat.getSecondServeUp()) - (third_set_stat.getDoubleFaultUp() - prev_second_set_stat.getDoubleFaultUp())) / (float) (third_set_stat.getSecondServeUp() - prev_second_set_stat.getSecondServeUp())) * 100) + "%",
                                (third_set_stat.getSecondServeDown() - prev_second_set_stat.getSecondServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((third_set_stat.getSecondServeDown() - prev_second_set_stat.getSecondServeDown()) - (third_set_stat.getDoubleFaultDown() - prev_second_set_stat.getDoubleFaultDown())) / (float) (third_set_stat.getSecondServeDown() - prev_second_set_stat.getSecondServeDown())) * 100) + "%",
                                (int)(((float) ((third_set_stat.getSecondServeUp() - prev_second_set_stat.getSecondServeUp()) - (third_set_stat.getDoubleFaultUp() - prev_second_set_stat.getDoubleFaultUp())) / (float) (third_set_stat.getSecondServeUp() - prev_second_set_stat.getSecondServeUp())) * 100),
                                (int)(((float) ((third_set_stat.getSecondServeDown() - prev_second_set_stat.getSecondServeDown()) - (third_set_stat.getDoubleFaultDown() - prev_second_set_stat.getDoubleFaultDown())) / (float) (third_set_stat.getSecondServeDown() - prev_second_set_stat.getSecondServeDown())) * 100));
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won),
                                (third_set_stat.getSecondServeWonUp() - prev_second_set_stat.getSecondServeWonUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (third_set_stat.getSecondServeWonUp() - prev_second_set_stat.getSecondServeWonUp()) / (float) ((third_set_stat.getSecondServeWonUp() - prev_second_set_stat.getSecondServeWonUp()) + (third_set_stat.getSecondServeLostUp() - prev_second_set_stat.getSecondServeLostUp()))) * 100) + "%",
                                (third_set_stat.getSecondServeDown() - prev_second_set_stat.getSecondServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (third_set_stat.getSecondServeWonDown() - prev_second_set_stat.getSecondServeWonDown()) / (float) ((third_set_stat.getSecondServeWonDown() - prev_second_set_stat.getSecondServeWonDown()) + (third_set_stat.getSecondServeLostDown() - prev_second_set_stat.getSecondServeLostDown()))) * 100) + "%",
                                (int)(((float) (third_set_stat.getSecondServeWonUp() - prev_second_set_stat.getSecondServeWonUp()) / (float) ((third_set_stat.getSecondServeWonUp() - prev_second_set_stat.getSecondServeWonUp()) + (third_set_stat.getSecondServeLostUp() - prev_second_set_stat.getSecondServeLostUp()))) * 100),
                                (int)(((float) (third_set_stat.getSecondServeWonDown() - prev_second_set_stat.getSecondServeWonDown()) / (float) ((third_set_stat.getSecondServeWonDown() - prev_second_set_stat.getSecondServeWonDown()) + (third_set_stat.getSecondServeLostDown() - prev_second_set_stat.getSecondServeLostDown()))) * 100));
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner),
                                String.valueOf((third_set_stat.getForehandWinnerUp() - prev_second_set_stat.getForehandWinnerUp())+
                                        (third_set_stat.getBackhandWinnerUp() - prev_second_set_stat.getBackhandWinnerUp()) +
                                        (third_set_stat.getForehandVolleyUp() - prev_second_set_stat.getForehandVolleyUp()) +
                                        (third_set_stat.getBackhandVolleyUp() - prev_second_set_stat.getBackhandVolleyUp())),
                                String.valueOf((third_set_stat.getForehandWinnerDown() - prev_second_set_stat.getForehandWinnerDown()) +
                                        (third_set_stat.getBackhandWinnerDown() - prev_second_set_stat.getBackhandWinnerDown())+
                                        (third_set_stat.getForehandVolleyDown() - prev_second_set_stat.getForehandVolleyDown())+
                                        (third_set_stat.getBackhandVolleyDown() - prev_second_set_stat.getBackhandVolleyDown())),
                                ((third_set_stat.getForehandWinnerUp() - prev_second_set_stat.getForehandWinnerUp())+
                                        (third_set_stat.getBackhandWinnerUp() - prev_second_set_stat.getBackhandWinnerUp()) +
                                        (third_set_stat.getForehandVolleyUp() - prev_second_set_stat.getForehandVolleyUp()) +
                                        (third_set_stat.getBackhandVolleyUp() - prev_second_set_stat.getBackhandVolleyUp())),
                                ((third_set_stat.getForehandWinnerDown() - prev_second_set_stat.getForehandWinnerDown()) +
                                        (third_set_stat.getBackhandWinnerDown() - prev_second_set_stat.getBackhandWinnerDown())+
                                        (third_set_stat.getForehandVolleyDown() - prev_second_set_stat.getForehandVolleyDown())+
                                        (third_set_stat.getBackhandVolleyDown() - prev_second_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner),
                                String.valueOf((third_set_stat.getForehandWinnerUp() - prev_second_set_stat.getForehandWinnerUp())+
                                        (third_set_stat.getForehandVolleyUp() - prev_second_set_stat.getForehandVolleyUp())),
                                String.valueOf((third_set_stat.getForehandWinnerDown() - prev_second_set_stat.getForehandWinnerDown()) +
                                        (third_set_stat.getForehandVolleyDown() - prev_second_set_stat.getForehandVolleyDown())),
                                ((third_set_stat.getForehandWinnerUp() - prev_second_set_stat.getForehandWinnerUp())+
                                        (third_set_stat.getForehandVolleyUp() - prev_second_set_stat.getForehandVolleyUp())),
                                ((third_set_stat.getForehandWinnerDown() - prev_second_set_stat.getForehandWinnerDown()) +
                                        (third_set_stat.getForehandVolleyDown() - prev_second_set_stat.getForehandVolleyDown())));
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner),
                                String.valueOf((third_set_stat.getBackhandWinnerUp() - prev_second_set_stat.getBackhandWinnerUp()) +
                                        (third_set_stat.getBackhandVolleyUp() - prev_second_set_stat.getBackhandVolleyUp())),
                                String.valueOf((third_set_stat.getBackhandWinnerDown() - prev_second_set_stat.getBackhandWinnerDown())+
                                        (third_set_stat.getBackhandVolleyDown() - prev_second_set_stat.getBackhandVolleyDown())),
                                ((third_set_stat.getBackhandWinnerUp() - prev_second_set_stat.getBackhandWinnerUp()) +
                                        (third_set_stat.getBackhandVolleyUp() - prev_second_set_stat.getBackhandVolleyUp())),
                                ((third_set_stat.getBackhandWinnerDown() - prev_second_set_stat.getBackhandWinnerDown())+
                                        (third_set_stat.getBackhandVolleyDown() - prev_second_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won),
                                String.valueOf((third_set_stat.getForehandVolleyUp() - prev_second_set_stat.getForehandVolleyUp())+
                                        (third_set_stat.getBackhandVolleyUp() - prev_second_set_stat.getBackhandVolleyUp())),
                                String.valueOf((third_set_stat.getForehandVolleyDown() - prev_second_set_stat.getForehandVolleyDown()) +
                                        (third_set_stat.getBackhandVolleyDown() - prev_second_set_stat.getBackhandVolleyDown())),
                                ((third_set_stat.getForehandVolleyUp() - prev_second_set_stat.getForehandVolleyUp())+
                                        (third_set_stat.getBackhandVolleyUp() - prev_second_set_stat.getBackhandVolleyUp())),
                                ((third_set_stat.getForehandVolleyDown() - prev_second_set_stat.getForehandVolleyDown()) +
                                        (third_set_stat.getBackhandVolleyDown() - prev_second_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won),
                                (third_set_stat.getBreakPointUp() - prev_second_set_stat.getBreakPointUp()) == 0 ? "0%" : "("+
                                        String.valueOf((third_set_stat.getBreakPointUp() - prev_second_set_stat.getBreakPointUp()) - (third_set_stat.getBreakPointMissUp() - prev_second_set_stat.getBreakPointMissUp())) +"/"+String.valueOf((third_set_stat.getBreakPointUp() - prev_second_set_stat.getBreakPointUp()))+") "+
                                        String.format(current_local, "%.1f", ((float) ((third_set_stat.getBreakPointUp() - prev_second_set_stat.getBreakPointUp()) - (third_set_stat.getBreakPointMissUp() - prev_second_set_stat.getBreakPointMissUp())) / (float) (third_set_stat.getBreakPointUp() - prev_second_set_stat.getBreakPointUp())) * 100) + "%",
                                (third_set_stat.getBreakPointDown() - prev_second_set_stat.getBreakPointDown()) == 0 ? "0%" : "("+
                                        String.valueOf((third_set_stat.getBreakPointDown() - prev_second_set_stat.getBreakPointDown()) - (third_set_stat.getBreakPointMissDown() - prev_second_set_stat.getBreakPointMissDown())) +"/"+String.valueOf((third_set_stat.getBreakPointDown() - prev_second_set_stat.getBreakPointDown()))+") "+
                                        String.format(current_local, "%.1f", ((float) ((third_set_stat.getBreakPointDown() - prev_second_set_stat.getBreakPointDown()) - (third_set_stat.getBreakPointMissDown() - prev_second_set_stat.getBreakPointMissDown())) / (float) (third_set_stat.getBreakPointDown() - prev_second_set_stat.getBreakPointDown())) * 100) + "%",
                                (int)(((float) ((third_set_stat.getBreakPointUp() - prev_second_set_stat.getBreakPointUp()) - (third_set_stat.getBreakPointMissUp() - prev_second_set_stat.getBreakPointMissUp())) / (float) (third_set_stat.getBreakPointUp() - prev_second_set_stat.getBreakPointUp())) * 100),
                                (int)(((float) ((third_set_stat.getBreakPointDown() - prev_second_set_stat.getBreakPointDown()) - (third_set_stat.getBreakPointMissDown() - prev_second_set_stat.getBreakPointMissDown())) / (float) (third_set_stat.getBreakPointDown() - prev_second_set_stat.getBreakPointDown())) * 100));
                        currentArray.add(item11);
                        //total point
                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point),
                                String.valueOf((third_set_stat.getForehandWinnerUp() - prev_second_set_stat.getForehandWinnerUp()) +
                                        (third_set_stat.getBackhandWinnerUp() - prev_second_set_stat.getBackhandWinnerUp()) +
                                        (third_set_stat.getForehandVolleyUp() - prev_second_set_stat.getForehandVolleyUp()) +
                                        (third_set_stat.getBackhandVolleyUp() - prev_second_set_stat.getBackhandVolleyUp()) +
                                        (third_set_stat.getDoubleFaultDown() - prev_second_set_stat.getDoubleFaultDown()) +
                                        (third_set_stat.getUnforceErrorDown() - prev_second_set_stat.getUnforceErrorDown()) +
                                        (third_set_stat.getFoulToLoseDown() - prev_second_set_stat.getFoulToLoseDown())),
                                String.valueOf((third_set_stat.getForehandWinnerDown() - prev_second_set_stat.getForehandWinnerDown()) +
                                        (third_set_stat.getBackhandWinnerDown() - prev_second_set_stat.getBackhandWinnerDown()) +
                                        (third_set_stat.getForehandVolleyDown() - prev_second_set_stat.getForehandVolleyDown()) +
                                        (third_set_stat.getBackhandVolleyDown() - prev_second_set_stat.getBackhandVolleyDown()) +
                                        (third_set_stat.getDoubleFaultUp() - prev_second_set_stat.getDoubleFaultUp()) +
                                        (third_set_stat.getUnforceErrorUp() - prev_second_set_stat.getUnforceErrorUp()) +
                                        (third_set_stat.getFoulToLoseUp() - prev_second_set_stat.getFoulToLoseUp())),
                                ((third_set_stat.getForehandWinnerUp() - prev_second_set_stat.getForehandWinnerUp()) +
                                        (third_set_stat.getBackhandWinnerUp() - prev_second_set_stat.getBackhandWinnerUp()) +
                                        (third_set_stat.getForehandVolleyUp() - prev_second_set_stat.getForehandVolleyUp()) +
                                        (third_set_stat.getBackhandVolleyUp() - prev_second_set_stat.getBackhandVolleyUp()) +
                                        (third_set_stat.getDoubleFaultDown() - prev_second_set_stat.getDoubleFaultDown()) +
                                        (third_set_stat.getUnforceErrorDown() - prev_second_set_stat.getUnforceErrorDown()) +
                                        (third_set_stat.getFoulToLoseDown() - prev_second_set_stat.getFoulToLoseDown())),
                                ((third_set_stat.getForehandWinnerDown() - prev_second_set_stat.getForehandWinnerDown()) +
                                        (third_set_stat.getBackhandWinnerDown() - prev_second_set_stat.getBackhandWinnerDown()) +
                                        (third_set_stat.getForehandVolleyDown() - prev_second_set_stat.getForehandVolleyDown()) +
                                        (third_set_stat.getBackhandVolleyDown() - prev_second_set_stat.getBackhandVolleyDown()) +
                                        (third_set_stat.getDoubleFaultUp() - prev_second_set_stat.getDoubleFaultUp()) +
                                        (third_set_stat.getUnforceErrorUp() - prev_second_set_stat.getUnforceErrorUp()) +
                                        (third_set_stat.getFoulToLoseUp() - prev_second_set_stat.getFoulToLoseUp())));
                        currentArray.add(item12);
                    } else {
                        Log.d(TAG, "current_state = null");

                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                "0", "0", 0, 0);
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                "0", "0", 0, 0);
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error), "0", "0", 0, 0);
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error), "0", "0", 0, 0);
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), "0%", "0%", 0, 0);
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), "0%", "0%", 0, 0);
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner), "0", "0", 0, 0);
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner), "0", "0", 0, 0);
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner), "0", "0", 0, 0);
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won), "0", "0", 0, 0);
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won), "0%", "0%", 0, 0);
                        currentArray.add(item11);

                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point), "0", "0", 0, 0);
                        currentArray.add(item12);
                    }
                } else if (position == 4) {
                    if (forth_set_stat != null && prev_third_set_stat != null) {
                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                String.valueOf(forth_set_stat.getAceCountUp()-prev_third_set_stat.getAceCountUp()), String.valueOf(forth_set_stat.getAceCountDown()-prev_third_set_stat.getAceCountDown()),
                                (forth_set_stat.getAceCountUp()-prev_third_set_stat.getAceCountUp()),
                                (forth_set_stat.getAceCountDown()-prev_third_set_stat.getAceCountDown()));
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                String.valueOf(forth_set_stat.getDoubleFaultUp()-prev_third_set_stat.getDoubleFaultUp()), String.valueOf(forth_set_stat.getDoubleFaultDown()-prev_third_set_stat.getDoubleFaultDown()),
                                (forth_set_stat.getDoubleFaultUp()-prev_third_set_stat.getDoubleFaultUp()),
                                (forth_set_stat.getDoubleFaultDown()-prev_third_set_stat.getDoubleFaultDown()));
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error),
                                String.valueOf(forth_set_stat.getUnforceErrorUp()-prev_third_set_stat.getUnforceErrorUp()),
                                String.valueOf(forth_set_stat.getUnforceErrorDown()-prev_third_set_stat.getUnforceErrorDown()),
                                (forth_set_stat.getUnforceErrorUp()-prev_third_set_stat.getUnforceErrorUp()),
                                (forth_set_stat.getUnforceErrorDown()-prev_third_set_stat.getUnforceErrorDown()));
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error),
                                String.valueOf(forth_set_stat.getForceErrorUp()-prev_third_set_stat.getForceErrorUp()),
                                String.valueOf(forth_set_stat.getForceErrorDown()-prev_third_set_stat.getForceErrorDown()),
                                (forth_set_stat.getForceErrorUp()-prev_third_set_stat.getForceErrorUp()),
                                (forth_set_stat.getForceErrorDown()-prev_third_set_stat.getForceErrorDown()));
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve),
                                (forth_set_stat.getFirstServeUp() - prev_third_set_stat.getFirstServeUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((forth_set_stat.getFirstServeUp() - prev_third_set_stat.getFirstServeUp()) - (forth_set_stat.getFirstServeMissUp() - prev_third_set_stat.getFirstServeMissUp())) / (float) (forth_set_stat.getFirstServeUp() - prev_third_set_stat.getFirstServeUp())) * 100) + "%",

                                (forth_set_stat.getFirstServeDown() - prev_third_set_stat.getFirstServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((forth_set_stat.getFirstServeDown() - prev_third_set_stat.getFirstServeDown()) - (forth_set_stat.getFirstServeMissDown() - prev_third_set_stat.getFirstServeMissDown())) / (float) (forth_set_stat.getFirstServeDown() - prev_third_set_stat.getFirstServeDown())) * 100) + "%",
                                (int)(((float) ((forth_set_stat.getFirstServeUp() - prev_third_set_stat.getFirstServeUp()) - (forth_set_stat.getFirstServeMissUp() - prev_third_set_stat.getFirstServeMissUp())) / (float) (forth_set_stat.getFirstServeUp() - prev_third_set_stat.getFirstServeUp())) * 100),
                                (int)(((float) ((forth_set_stat.getFirstServeDown() - prev_third_set_stat.getFirstServeDown()) - (forth_set_stat.getFirstServeMissDown() - prev_third_set_stat.getFirstServeMissDown())) / (float) (forth_set_stat.getFirstServeDown() - prev_third_set_stat.getFirstServeDown())) * 100));
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won),
                                (forth_set_stat.getFirstServeWonUp() - prev_third_set_stat.getFirstServeWonUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (forth_set_stat.getFirstServeWonUp() - prev_third_set_stat.getFirstServeWonUp()) / (float) ((forth_set_stat.getFirstServeWonUp() - prev_third_set_stat.getFirstServeWonUp()) + (forth_set_stat.getFirstServeLostUp() - prev_third_set_stat.getFirstServeLostUp()))) * 100) + "%",

                                (forth_set_stat.getFirstServeWonDown() - prev_third_set_stat.getFirstServeWonDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (forth_set_stat.getFirstServeWonDown() - prev_third_set_stat.getFirstServeWonDown()) / (float) ((forth_set_stat.getFirstServeWonDown() - prev_third_set_stat.getFirstServeWonDown()) + (forth_set_stat.getFirstServeLostDown() - prev_third_set_stat.getFirstServeLostDown()))) * 100) + "%",
                                (int)(((float) (forth_set_stat.getFirstServeWonUp() - prev_third_set_stat.getFirstServeWonUp()) / (float) ((forth_set_stat.getFirstServeWonUp() - prev_third_set_stat.getFirstServeWonUp()) + (forth_set_stat.getFirstServeLostUp() - prev_third_set_stat.getFirstServeLostUp()))) * 100),
                                (int)(((float) (forth_set_stat.getFirstServeWonDown() - prev_third_set_stat.getFirstServeWonDown()) / (float) ((forth_set_stat.getFirstServeWonDown() - prev_third_set_stat.getFirstServeWonDown()) + (forth_set_stat.getFirstServeLostDown() - prev_third_set_stat.getFirstServeLostDown()))) * 100));
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve),
                                (forth_set_stat.getSecondServeUp() - prev_third_set_stat.getSecondServeUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((forth_set_stat.getSecondServeUp() - prev_third_set_stat.getSecondServeUp()) - (forth_set_stat.getDoubleFaultUp() - prev_third_set_stat.getDoubleFaultUp())) / (float) (forth_set_stat.getSecondServeUp() - prev_third_set_stat.getSecondServeUp())) * 100) + "%",
                                (forth_set_stat.getSecondServeDown() - prev_third_set_stat.getSecondServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((forth_set_stat.getSecondServeDown() - prev_third_set_stat.getSecondServeDown()) - (forth_set_stat.getDoubleFaultDown() - prev_third_set_stat.getDoubleFaultDown())) / (float) (forth_set_stat.getSecondServeDown() - prev_third_set_stat.getSecondServeDown())) * 100) + "%",
                                (int)(((float) ((forth_set_stat.getSecondServeUp() - prev_third_set_stat.getSecondServeUp()) - (forth_set_stat.getDoubleFaultUp() - prev_third_set_stat.getDoubleFaultUp())) / (float) (forth_set_stat.getSecondServeUp() - prev_third_set_stat.getSecondServeUp())) * 100),
                                (int)(((float) ((forth_set_stat.getSecondServeDown() - prev_third_set_stat.getSecondServeDown()) - (forth_set_stat.getDoubleFaultDown() - prev_third_set_stat.getDoubleFaultDown())) / (float) (forth_set_stat.getSecondServeDown() - prev_third_set_stat.getSecondServeDown())) * 100));
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won),
                                (forth_set_stat.getSecondServeWonUp() - prev_third_set_stat.getSecondServeWonUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (forth_set_stat.getSecondServeWonUp() - prev_third_set_stat.getSecondServeWonUp()) / (float) ((forth_set_stat.getSecondServeWonUp() - prev_third_set_stat.getSecondServeWonUp()) + (forth_set_stat.getSecondServeLostUp() - prev_third_set_stat.getSecondServeLostUp()))) * 100) + "%",
                                (forth_set_stat.getSecondServeDown() - prev_third_set_stat.getSecondServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (forth_set_stat.getSecondServeWonDown() - prev_third_set_stat.getSecondServeWonDown()) / (float) ((forth_set_stat.getSecondServeWonDown() - prev_third_set_stat.getSecondServeWonDown()) + (forth_set_stat.getSecondServeLostDown() - prev_third_set_stat.getSecondServeLostDown()))) * 100) + "%",
                                (int)(((float) (forth_set_stat.getSecondServeWonUp() - prev_third_set_stat.getSecondServeWonUp()) / (float) ((forth_set_stat.getSecondServeWonUp() - prev_third_set_stat.getSecondServeWonUp()) + (forth_set_stat.getSecondServeLostUp() - prev_third_set_stat.getSecondServeLostUp()))) * 100),
                                (int)(((float) (forth_set_stat.getSecondServeWonDown() - prev_third_set_stat.getSecondServeWonDown()) / (float) ((forth_set_stat.getSecondServeWonDown() - prev_third_set_stat.getSecondServeWonDown()) + (forth_set_stat.getSecondServeLostDown() - prev_third_set_stat.getSecondServeLostDown()))) * 100));
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner),
                                String.valueOf((forth_set_stat.getForehandWinnerUp() - prev_third_set_stat.getForehandWinnerUp())+
                                        (forth_set_stat.getBackhandWinnerUp() - prev_third_set_stat.getBackhandWinnerUp()) +
                                        (forth_set_stat.getForehandVolleyUp() - prev_third_set_stat.getForehandVolleyUp()) +
                                        (forth_set_stat.getBackhandVolleyUp() - prev_third_set_stat.getBackhandVolleyUp())),
                                String.valueOf((forth_set_stat.getForehandWinnerDown() - prev_third_set_stat.getForehandWinnerDown()) +
                                        (forth_set_stat.getBackhandWinnerDown() - prev_third_set_stat.getBackhandWinnerDown())+
                                        (forth_set_stat.getForehandVolleyDown() - prev_third_set_stat.getForehandVolleyDown())+
                                        (forth_set_stat.getBackhandVolleyDown() - prev_third_set_stat.getBackhandVolleyDown())),
                                ((forth_set_stat.getForehandWinnerUp() - prev_third_set_stat.getForehandWinnerUp())+
                                        (forth_set_stat.getBackhandWinnerUp() - prev_third_set_stat.getBackhandWinnerUp()) +
                                        (forth_set_stat.getForehandVolleyUp() - prev_third_set_stat.getForehandVolleyUp()) +
                                        (forth_set_stat.getBackhandVolleyUp() - prev_third_set_stat.getBackhandVolleyUp())),
                                ((forth_set_stat.getForehandWinnerDown() - prev_third_set_stat.getForehandWinnerDown()) +
                                        (forth_set_stat.getBackhandWinnerDown() - prev_third_set_stat.getBackhandWinnerDown())+
                                        (forth_set_stat.getForehandVolleyDown() - prev_third_set_stat.getForehandVolleyDown())+
                                        (forth_set_stat.getBackhandVolleyDown() - prev_third_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner),
                                String.valueOf((forth_set_stat.getForehandWinnerUp() - prev_third_set_stat.getForehandWinnerUp())+
                                        (forth_set_stat.getForehandVolleyUp() - prev_third_set_stat.getForehandVolleyUp())),
                                String.valueOf((forth_set_stat.getForehandWinnerDown() - prev_third_set_stat.getForehandWinnerDown()) +
                                        (forth_set_stat.getForehandVolleyDown() - prev_third_set_stat.getForehandVolleyDown())),
                                ((forth_set_stat.getForehandWinnerUp() - prev_third_set_stat.getForehandWinnerUp())+
                                        (forth_set_stat.getForehandVolleyUp() - prev_third_set_stat.getForehandVolleyUp())),
                                ((forth_set_stat.getForehandWinnerDown() - prev_third_set_stat.getForehandWinnerDown()) +
                                        (forth_set_stat.getForehandVolleyDown() - prev_third_set_stat.getForehandVolleyDown())));
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner),
                                String.valueOf((forth_set_stat.getBackhandWinnerUp() - prev_third_set_stat.getBackhandWinnerUp()) +
                                        (forth_set_stat.getBackhandVolleyUp() - prev_third_set_stat.getBackhandVolleyUp())),
                                String.valueOf((forth_set_stat.getBackhandWinnerDown() - prev_third_set_stat.getBackhandWinnerDown())+
                                        (forth_set_stat.getBackhandVolleyDown() - prev_third_set_stat.getBackhandVolleyDown())),
                                ((forth_set_stat.getBackhandWinnerUp() - prev_third_set_stat.getBackhandWinnerUp()) +
                                        (forth_set_stat.getBackhandVolleyUp() - prev_third_set_stat.getBackhandVolleyUp())),
                                ((forth_set_stat.getBackhandWinnerDown() - prev_third_set_stat.getBackhandWinnerDown())+
                                        (forth_set_stat.getBackhandVolleyDown() - prev_third_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won),
                                String.valueOf((forth_set_stat.getForehandVolleyUp() - prev_third_set_stat.getForehandVolleyUp())+
                                        (forth_set_stat.getBackhandVolleyUp() - prev_third_set_stat.getBackhandVolleyUp())),
                                String.valueOf((forth_set_stat.getForehandVolleyDown() - prev_third_set_stat.getForehandVolleyDown()) +
                                        (forth_set_stat.getBackhandVolleyDown() - prev_third_set_stat.getBackhandVolleyDown())),
                                ((forth_set_stat.getForehandVolleyUp() - prev_third_set_stat.getForehandVolleyUp())+
                                        (forth_set_stat.getBackhandVolleyUp() - prev_third_set_stat.getBackhandVolleyUp())),
                                ((forth_set_stat.getForehandVolleyDown() - prev_third_set_stat.getForehandVolleyDown()) +
                                        (forth_set_stat.getBackhandVolleyDown() - prev_third_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won),
                                (forth_set_stat.getBreakPointUp() - prev_third_set_stat.getBreakPointUp()) == 0 ? "0%" : "("+
                                        String.valueOf((forth_set_stat.getBreakPointUp() - prev_third_set_stat.getBreakPointUp()) - (forth_set_stat.getBreakPointMissUp() - prev_third_set_stat.getBreakPointMissUp())) +"/"+String.valueOf((forth_set_stat.getBreakPointUp() - prev_third_set_stat.getBreakPointUp()))+") "+
                                        String.format(current_local, "%.1f", ((float) ((forth_set_stat.getBreakPointUp() - prev_third_set_stat.getBreakPointUp()) - (forth_set_stat.getBreakPointMissUp() - prev_third_set_stat.getBreakPointMissUp())) / (float) (forth_set_stat.getBreakPointUp() - prev_third_set_stat.getBreakPointUp())) * 100) + "%",
                                (forth_set_stat.getBreakPointDown() - prev_third_set_stat.getBreakPointDown()) == 0 ? "0%" : "("+
                                        String.valueOf((forth_set_stat.getBreakPointDown() - prev_third_set_stat.getBreakPointDown()) - (forth_set_stat.getBreakPointMissDown() - prev_third_set_stat.getBreakPointMissDown())) +"/"+String.valueOf((forth_set_stat.getBreakPointDown() - prev_third_set_stat.getBreakPointDown()))+") "+
                                        String.format(current_local, "%.1f", ((float) ((forth_set_stat.getBreakPointDown() - prev_third_set_stat.getBreakPointDown()) - (forth_set_stat.getBreakPointMissDown() - prev_third_set_stat.getBreakPointMissDown())) / (float) (forth_set_stat.getBreakPointDown() - prev_third_set_stat.getBreakPointDown())) * 100) + "%",
                                (int)(((float) ((forth_set_stat.getBreakPointUp() - prev_third_set_stat.getBreakPointUp()) - (forth_set_stat.getBreakPointMissUp() - prev_third_set_stat.getBreakPointMissUp())) / (float) (forth_set_stat.getBreakPointUp() - prev_third_set_stat.getBreakPointUp())) * 100),
                                (int)(((float) ((forth_set_stat.getBreakPointDown() - prev_third_set_stat.getBreakPointDown()) - (forth_set_stat.getBreakPointMissDown() - prev_third_set_stat.getBreakPointMissDown())) / (float) (forth_set_stat.getBreakPointDown() - prev_third_set_stat.getBreakPointDown())) * 100));
                        currentArray.add(item11);
                        //total point
                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point),
                                String.valueOf((forth_set_stat.getForehandWinnerUp() - prev_third_set_stat.getForehandWinnerUp()) +
                                        (forth_set_stat.getBackhandWinnerUp() - prev_third_set_stat.getBackhandWinnerUp()) +
                                        (forth_set_stat.getForehandVolleyUp() - prev_third_set_stat.getForehandVolleyUp()) +
                                        (forth_set_stat.getBackhandVolleyUp() - prev_third_set_stat.getBackhandVolleyUp()) +
                                        (forth_set_stat.getDoubleFaultDown() - prev_third_set_stat.getDoubleFaultDown()) +
                                        (forth_set_stat.getUnforceErrorDown() - prev_third_set_stat.getUnforceErrorDown()) +
                                        (forth_set_stat.getFoulToLoseDown() - prev_third_set_stat.getFoulToLoseDown())),
                                String.valueOf((forth_set_stat.getForehandWinnerDown() - prev_third_set_stat.getForehandWinnerDown()) +
                                        (forth_set_stat.getBackhandWinnerDown() - prev_third_set_stat.getBackhandWinnerDown()) +
                                        (forth_set_stat.getForehandVolleyDown() - prev_third_set_stat.getForehandVolleyDown()) +
                                        (forth_set_stat.getBackhandVolleyDown() - prev_third_set_stat.getBackhandVolleyDown()) +
                                        (forth_set_stat.getDoubleFaultUp() - prev_third_set_stat.getDoubleFaultUp()) +
                                        (forth_set_stat.getUnforceErrorUp() - prev_third_set_stat.getUnforceErrorUp()) +
                                        (forth_set_stat.getFoulToLoseUp() - prev_third_set_stat.getFoulToLoseUp())),
                                ((forth_set_stat.getForehandWinnerUp() - prev_third_set_stat.getForehandWinnerUp()) +
                                        (forth_set_stat.getBackhandWinnerUp() - prev_third_set_stat.getBackhandWinnerUp()) +
                                        (forth_set_stat.getForehandVolleyUp() - prev_third_set_stat.getForehandVolleyUp()) +
                                        (forth_set_stat.getBackhandVolleyUp() - prev_third_set_stat.getBackhandVolleyUp()) +
                                        (forth_set_stat.getDoubleFaultDown() - prev_third_set_stat.getDoubleFaultDown()) +
                                        (forth_set_stat.getUnforceErrorDown() - prev_third_set_stat.getUnforceErrorDown()) +
                                        (forth_set_stat.getFoulToLoseDown() - prev_third_set_stat.getFoulToLoseDown())),
                                ((forth_set_stat.getForehandWinnerDown() - prev_third_set_stat.getForehandWinnerDown()) +
                                        (forth_set_stat.getBackhandWinnerDown() - prev_third_set_stat.getBackhandWinnerDown()) +
                                        (forth_set_stat.getForehandVolleyDown() - prev_third_set_stat.getForehandVolleyDown()) +
                                        (forth_set_stat.getBackhandVolleyDown() - prev_third_set_stat.getBackhandVolleyDown()) +
                                        (forth_set_stat.getDoubleFaultUp() - prev_third_set_stat.getDoubleFaultUp()) +
                                        (forth_set_stat.getUnforceErrorUp() - prev_third_set_stat.getUnforceErrorUp()) +
                                        (forth_set_stat.getFoulToLoseUp() - prev_third_set_stat.getFoulToLoseUp())));
                        currentArray.add(item12);
                    } else {
                        Log.d(TAG, "current_state = null");

                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                "0", "0", 0, 0);
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                "0", "0", 0, 0);
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error), "0", "0", 0, 0);
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error), "0", "0", 0, 0);
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), "0%", "0%", 0, 0);
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), "0%", "0%", 0, 0);
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner), "0", "0", 0, 0);
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner), "0", "0", 0, 0);
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner), "0", "0", 0, 0);
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won), "0", "0", 0, 0);
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won), "0%", "0%", 0, 0);
                        currentArray.add(item11);

                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point), "0", "0", 0, 0);
                        currentArray.add(item12);
                    }
                } else if (position == 5) {
                    if (fifth_set_stat != null && prev_forth_set_stat != null) {
                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                String.valueOf(fifth_set_stat.getAceCountUp()-prev_forth_set_stat.getAceCountUp()), String.valueOf(fifth_set_stat.getAceCountDown()-prev_forth_set_stat.getAceCountDown()),
                                (fifth_set_stat.getAceCountUp()-prev_forth_set_stat.getAceCountUp()),
                                (fifth_set_stat.getAceCountDown()-prev_forth_set_stat.getAceCountDown()));
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                String.valueOf(fifth_set_stat.getDoubleFaultUp()-prev_forth_set_stat.getDoubleFaultUp()), String.valueOf(fifth_set_stat.getDoubleFaultDown()-prev_forth_set_stat.getDoubleFaultDown()),
                                (fifth_set_stat.getDoubleFaultUp()-prev_forth_set_stat.getDoubleFaultUp()),
                                (fifth_set_stat.getDoubleFaultDown()-prev_forth_set_stat.getDoubleFaultDown()));
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error),
                                String.valueOf(fifth_set_stat.getUnforceErrorUp()-prev_forth_set_stat.getUnforceErrorUp()),
                                String.valueOf(fifth_set_stat.getUnforceErrorDown()-prev_forth_set_stat.getUnforceErrorDown()),
                                (fifth_set_stat.getUnforceErrorUp()-prev_forth_set_stat.getUnforceErrorUp()),
                                (fifth_set_stat.getUnforceErrorDown()-prev_forth_set_stat.getUnforceErrorDown()));
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error),
                                String.valueOf(fifth_set_stat.getForceErrorUp()-prev_forth_set_stat.getForceErrorUp()),
                                String.valueOf(fifth_set_stat.getForceErrorDown()-prev_forth_set_stat.getForceErrorDown()),
                                (fifth_set_stat.getForceErrorUp()-prev_forth_set_stat.getForceErrorUp()),
                                (fifth_set_stat.getForceErrorDown()-prev_forth_set_stat.getForceErrorDown()));
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve),
                                (fifth_set_stat.getFirstServeUp() - prev_forth_set_stat.getFirstServeUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((fifth_set_stat.getFirstServeUp() - prev_forth_set_stat.getFirstServeUp()) - (fifth_set_stat.getFirstServeMissUp() - prev_forth_set_stat.getFirstServeMissUp())) / (float) (fifth_set_stat.getFirstServeUp() - prev_forth_set_stat.getFirstServeUp())) * 100) + "%",

                                (fifth_set_stat.getFirstServeDown() - prev_forth_set_stat.getFirstServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((fifth_set_stat.getFirstServeDown() - prev_forth_set_stat.getFirstServeDown()) - (fifth_set_stat.getFirstServeMissDown() - prev_forth_set_stat.getFirstServeMissDown())) / (float) (fifth_set_stat.getFirstServeDown() - prev_forth_set_stat.getFirstServeDown())) * 100) + "%",
                                (int)(((float) ((fifth_set_stat.getFirstServeUp() - prev_forth_set_stat.getFirstServeUp()) - (fifth_set_stat.getFirstServeMissUp() - prev_forth_set_stat.getFirstServeMissUp())) / (float) (fifth_set_stat.getFirstServeUp() - prev_forth_set_stat.getFirstServeUp())) * 100),
                                (int)(((float) ((fifth_set_stat.getFirstServeDown() - prev_forth_set_stat.getFirstServeDown()) - (fifth_set_stat.getFirstServeMissDown() - prev_forth_set_stat.getFirstServeMissDown())) / (float) (fifth_set_stat.getFirstServeDown() - prev_forth_set_stat.getFirstServeDown())) * 100));
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won),
                                (fifth_set_stat.getFirstServeWonUp() - prev_forth_set_stat.getFirstServeWonUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (fifth_set_stat.getFirstServeWonUp() - prev_forth_set_stat.getFirstServeWonUp()) / (float) ((fifth_set_stat.getFirstServeWonUp() - prev_forth_set_stat.getFirstServeWonUp()) + (fifth_set_stat.getFirstServeLostUp() - prev_forth_set_stat.getFirstServeLostUp()))) * 100) + "%",

                                (fifth_set_stat.getFirstServeWonDown() - prev_forth_set_stat.getFirstServeWonDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (fifth_set_stat.getFirstServeWonDown() - prev_forth_set_stat.getFirstServeWonDown()) / (float) ((fifth_set_stat.getFirstServeWonDown() - prev_forth_set_stat.getFirstServeWonDown()) + (fifth_set_stat.getFirstServeLostDown() - prev_forth_set_stat.getFirstServeLostDown()))) * 100) + "%",
                                (int)(((float) (fifth_set_stat.getFirstServeWonUp() - prev_forth_set_stat.getFirstServeWonUp()) / (float) ((fifth_set_stat.getFirstServeWonUp() - prev_forth_set_stat.getFirstServeWonUp()) + (fifth_set_stat.getFirstServeLostUp() - prev_forth_set_stat.getFirstServeLostUp()))) * 100),
                                (int)(((float) (fifth_set_stat.getFirstServeWonDown() - prev_forth_set_stat.getFirstServeWonDown()) / (float) ((fifth_set_stat.getFirstServeWonDown() - prev_forth_set_stat.getFirstServeWonDown()) + (fifth_set_stat.getFirstServeLostDown() - prev_forth_set_stat.getFirstServeLostDown()))) * 100));
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve),
                                (fifth_set_stat.getSecondServeUp() - prev_forth_set_stat.getSecondServeUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((fifth_set_stat.getSecondServeUp() - prev_forth_set_stat.getSecondServeUp()) - (fifth_set_stat.getDoubleFaultUp() - prev_forth_set_stat.getDoubleFaultUp())) / (float) (fifth_set_stat.getSecondServeUp() - prev_forth_set_stat.getSecondServeUp())) * 100) + "%",
                                (fifth_set_stat.getSecondServeDown() - prev_forth_set_stat.getSecondServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) ((fifth_set_stat.getSecondServeDown() - prev_forth_set_stat.getSecondServeDown()) - (fifth_set_stat.getDoubleFaultDown() - prev_forth_set_stat.getDoubleFaultDown())) / (float) (fifth_set_stat.getSecondServeDown() - prev_forth_set_stat.getSecondServeDown())) * 100) + "%",
                                (int)(((float) ((fifth_set_stat.getSecondServeUp() - prev_forth_set_stat.getSecondServeUp()) - (fifth_set_stat.getDoubleFaultUp() - prev_forth_set_stat.getDoubleFaultUp())) / (float) (fifth_set_stat.getSecondServeUp() - prev_forth_set_stat.getSecondServeUp())) * 100),
                                (int)(((float) ((fifth_set_stat.getSecondServeDown() - prev_forth_set_stat.getSecondServeDown()) - (fifth_set_stat.getDoubleFaultDown() - prev_forth_set_stat.getDoubleFaultDown())) / (float) (fifth_set_stat.getSecondServeDown() - prev_forth_set_stat.getSecondServeDown())) * 100));
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won),
                                (fifth_set_stat.getSecondServeWonUp() - prev_forth_set_stat.getSecondServeWonUp()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (fifth_set_stat.getSecondServeWonUp() - prev_forth_set_stat.getSecondServeWonUp()) / (float) ((fifth_set_stat.getSecondServeWonUp() - prev_forth_set_stat.getSecondServeWonUp()) + (fifth_set_stat.getSecondServeLostUp() - prev_forth_set_stat.getSecondServeLostUp()))) * 100) + "%",
                                (fifth_set_stat.getSecondServeDown() - prev_forth_set_stat.getSecondServeDown()) == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (fifth_set_stat.getSecondServeWonDown() - prev_forth_set_stat.getSecondServeWonDown()) / (float) ((fifth_set_stat.getSecondServeWonDown() - prev_forth_set_stat.getSecondServeWonDown()) + (fifth_set_stat.getSecondServeLostDown() - prev_forth_set_stat.getSecondServeLostDown()))) * 100) + "%",
                                (int)(((float) (fifth_set_stat.getSecondServeWonUp() - prev_forth_set_stat.getSecondServeWonUp()) / (float) ((fifth_set_stat.getSecondServeWonUp() - prev_forth_set_stat.getSecondServeWonUp()) + (fifth_set_stat.getSecondServeLostUp() - prev_forth_set_stat.getSecondServeLostUp()))) * 100),
                                (int)(((float) (fifth_set_stat.getSecondServeWonDown() - prev_forth_set_stat.getSecondServeWonDown()) / (float) ((fifth_set_stat.getSecondServeWonDown() - prev_forth_set_stat.getSecondServeWonDown()) + (fifth_set_stat.getSecondServeLostDown() - prev_forth_set_stat.getSecondServeLostDown()))) * 100));
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner),
                                String.valueOf((fifth_set_stat.getForehandWinnerUp() - prev_forth_set_stat.getForehandWinnerUp())+
                                        (fifth_set_stat.getBackhandWinnerUp() - prev_forth_set_stat.getBackhandWinnerUp()) +
                                        (fifth_set_stat.getForehandVolleyUp() - prev_forth_set_stat.getForehandVolleyUp()) +
                                        (fifth_set_stat.getBackhandVolleyUp() - prev_forth_set_stat.getBackhandVolleyUp())),
                                String.valueOf((fifth_set_stat.getForehandWinnerDown() - prev_forth_set_stat.getForehandWinnerDown()) +
                                        (fifth_set_stat.getBackhandWinnerDown() - prev_forth_set_stat.getBackhandWinnerDown())+
                                        (fifth_set_stat.getForehandVolleyDown() - prev_forth_set_stat.getForehandVolleyDown())+
                                        (fifth_set_stat.getBackhandVolleyDown() - prev_forth_set_stat.getBackhandVolleyDown())),
                                ((fifth_set_stat.getForehandWinnerUp() - prev_forth_set_stat.getForehandWinnerUp())+
                                        (fifth_set_stat.getBackhandWinnerUp() - prev_forth_set_stat.getBackhandWinnerUp()) +
                                        (fifth_set_stat.getForehandVolleyUp() - prev_forth_set_stat.getForehandVolleyUp()) +
                                        (fifth_set_stat.getBackhandVolleyUp() - prev_forth_set_stat.getBackhandVolleyUp())),
                                ((fifth_set_stat.getForehandWinnerDown() - prev_forth_set_stat.getForehandWinnerDown()) +
                                        (fifth_set_stat.getBackhandWinnerDown() - prev_forth_set_stat.getBackhandWinnerDown())+
                                        (fifth_set_stat.getForehandVolleyDown() - prev_forth_set_stat.getForehandVolleyDown())+
                                        (fifth_set_stat.getBackhandVolleyDown() - prev_forth_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner),
                                String.valueOf((fifth_set_stat.getForehandWinnerUp() - prev_forth_set_stat.getForehandWinnerUp())+
                                        (fifth_set_stat.getForehandVolleyUp() - prev_forth_set_stat.getForehandVolleyUp())),
                                String.valueOf((fifth_set_stat.getForehandWinnerDown() - prev_forth_set_stat.getForehandWinnerDown()) +
                                        (fifth_set_stat.getForehandVolleyDown() - prev_forth_set_stat.getForehandVolleyDown())),
                                ((fifth_set_stat.getForehandWinnerUp() - prev_forth_set_stat.getForehandWinnerUp())+
                                        (fifth_set_stat.getForehandVolleyUp() - prev_forth_set_stat.getForehandVolleyUp())),
                                ((fifth_set_stat.getForehandWinnerDown() - prev_forth_set_stat.getForehandWinnerDown()) +
                                        (fifth_set_stat.getForehandVolleyDown() - prev_forth_set_stat.getForehandVolleyDown())));
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner),
                                String.valueOf((fifth_set_stat.getBackhandWinnerUp() - prev_forth_set_stat.getBackhandWinnerUp()) +
                                        (fifth_set_stat.getBackhandVolleyUp() - prev_forth_set_stat.getBackhandVolleyUp())),
                                String.valueOf((fifth_set_stat.getBackhandWinnerDown() - prev_forth_set_stat.getBackhandWinnerDown())+
                                        (fifth_set_stat.getBackhandVolleyDown() - prev_forth_set_stat.getBackhandVolleyDown())),
                                ((fifth_set_stat.getBackhandWinnerUp() - prev_forth_set_stat.getBackhandWinnerUp()) +
                                        (fifth_set_stat.getBackhandVolleyUp() - prev_forth_set_stat.getBackhandVolleyUp())),
                                ((fifth_set_stat.getBackhandWinnerDown() - prev_forth_set_stat.getBackhandWinnerDown())+
                                        (fifth_set_stat.getBackhandVolleyDown() - prev_forth_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won),
                                String.valueOf((fifth_set_stat.getForehandVolleyUp() - prev_forth_set_stat.getForehandVolleyUp())+
                                        (fifth_set_stat.getBackhandVolleyUp() - prev_forth_set_stat.getBackhandVolleyUp())),
                                String.valueOf((fifth_set_stat.getForehandVolleyDown() - prev_forth_set_stat.getForehandVolleyDown()) +
                                        (fifth_set_stat.getBackhandVolleyDown() - prev_forth_set_stat.getBackhandVolleyDown())),
                                ((fifth_set_stat.getForehandVolleyUp() - prev_forth_set_stat.getForehandVolleyUp())+
                                        (fifth_set_stat.getBackhandVolleyUp() - prev_forth_set_stat.getBackhandVolleyUp())),
                                ((fifth_set_stat.getForehandVolleyDown() - prev_forth_set_stat.getForehandVolleyDown()) +
                                        (fifth_set_stat.getBackhandVolleyDown() - prev_forth_set_stat.getBackhandVolleyDown())));
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won),
                                (fifth_set_stat.getBreakPointUp() - prev_forth_set_stat.getBreakPointUp()) == 0 ? "0%" : "("+
                                        String.valueOf((fifth_set_stat.getBreakPointUp() - prev_forth_set_stat.getBreakPointUp()) - (fifth_set_stat.getBreakPointMissUp() - prev_forth_set_stat.getBreakPointMissUp())) +"/"+String.valueOf((fifth_set_stat.getBreakPointUp() - prev_forth_set_stat.getBreakPointUp()))+") "+
                                        String.format(current_local, "%.1f", ((float) ((fifth_set_stat.getBreakPointUp() - prev_forth_set_stat.getBreakPointUp()) - (fifth_set_stat.getBreakPointMissUp() - prev_forth_set_stat.getBreakPointMissUp())) / (float) (fifth_set_stat.getBreakPointUp() - prev_forth_set_stat.getBreakPointUp())) * 100) + "%",
                                (fifth_set_stat.getBreakPointDown() - prev_forth_set_stat.getBreakPointDown()) == 0 ? "0%" : "("+
                                        String.valueOf((fifth_set_stat.getBreakPointDown() - prev_forth_set_stat.getBreakPointDown()) - (fifth_set_stat.getBreakPointMissDown() - prev_forth_set_stat.getBreakPointMissDown())) +"/"+String.valueOf((fifth_set_stat.getBreakPointDown() - prev_forth_set_stat.getBreakPointDown()))+") "+
                                        String.format(current_local, "%.1f", ((float) ((fifth_set_stat.getBreakPointDown() - prev_forth_set_stat.getBreakPointDown()) - (fifth_set_stat.getBreakPointMissDown() - prev_forth_set_stat.getBreakPointMissDown())) / (float) (fifth_set_stat.getBreakPointDown() - prev_forth_set_stat.getBreakPointDown())) * 100) + "%",
                                (int)(((float) ((fifth_set_stat.getBreakPointUp() - prev_forth_set_stat.getBreakPointUp()) - (fifth_set_stat.getBreakPointMissUp() - prev_forth_set_stat.getBreakPointMissUp())) / (float) (fifth_set_stat.getBreakPointUp() - prev_forth_set_stat.getBreakPointUp())) * 100),
                                (int)(((float) ((fifth_set_stat.getBreakPointDown() - prev_forth_set_stat.getBreakPointDown()) - (fifth_set_stat.getBreakPointMissDown() - prev_forth_set_stat.getBreakPointMissDown())) / (float) (fifth_set_stat.getBreakPointDown() - prev_forth_set_stat.getBreakPointDown())) * 100));
                        currentArray.add(item11);
                        //total point
                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point),
                                String.valueOf((fifth_set_stat.getForehandWinnerUp() - prev_forth_set_stat.getForehandWinnerUp()) +
                                        (fifth_set_stat.getBackhandWinnerUp() - prev_forth_set_stat.getBackhandWinnerUp()) +
                                        (fifth_set_stat.getForehandVolleyUp() - prev_forth_set_stat.getForehandVolleyUp()) +
                                        (fifth_set_stat.getBackhandVolleyUp() - prev_forth_set_stat.getBackhandVolleyUp()) +
                                        (fifth_set_stat.getDoubleFaultDown() - prev_forth_set_stat.getDoubleFaultDown()) +
                                        (fifth_set_stat.getUnforceErrorDown() - prev_forth_set_stat.getUnforceErrorDown()) +
                                        (fifth_set_stat.getFoulToLoseDown() - prev_forth_set_stat.getFoulToLoseDown())),
                                String.valueOf((fifth_set_stat.getForehandWinnerDown() - prev_forth_set_stat.getForehandWinnerDown()) +
                                        (fifth_set_stat.getBackhandWinnerDown() - prev_forth_set_stat.getBackhandWinnerDown()) +
                                        (fifth_set_stat.getForehandVolleyDown() - prev_forth_set_stat.getForehandVolleyDown()) +
                                        (fifth_set_stat.getBackhandVolleyDown() - prev_forth_set_stat.getBackhandVolleyDown()) +
                                        (fifth_set_stat.getDoubleFaultUp() - prev_forth_set_stat.getDoubleFaultUp()) +
                                        (fifth_set_stat.getUnforceErrorUp() - prev_forth_set_stat.getUnforceErrorUp()) +
                                        (fifth_set_stat.getFoulToLoseUp() - prev_forth_set_stat.getFoulToLoseUp())),
                                ((fifth_set_stat.getForehandWinnerUp() - prev_forth_set_stat.getForehandWinnerUp()) +
                                        (fifth_set_stat.getBackhandWinnerUp() - prev_forth_set_stat.getBackhandWinnerUp()) +
                                        (fifth_set_stat.getForehandVolleyUp() - prev_forth_set_stat.getForehandVolleyUp()) +
                                        (fifth_set_stat.getBackhandVolleyUp() - prev_forth_set_stat.getBackhandVolleyUp()) +
                                        (fifth_set_stat.getDoubleFaultDown() - prev_forth_set_stat.getDoubleFaultDown()) +
                                        (fifth_set_stat.getUnforceErrorDown() - prev_forth_set_stat.getUnforceErrorDown()) +
                                        (fifth_set_stat.getFoulToLoseDown() - prev_forth_set_stat.getFoulToLoseDown())),
                                ((fifth_set_stat.getForehandWinnerDown() - prev_forth_set_stat.getForehandWinnerDown()) +
                                        (fifth_set_stat.getBackhandWinnerDown() - prev_forth_set_stat.getBackhandWinnerDown()) +
                                        (fifth_set_stat.getForehandVolleyDown() - prev_forth_set_stat.getForehandVolleyDown()) +
                                        (fifth_set_stat.getBackhandVolleyDown() - prev_forth_set_stat.getBackhandVolleyDown()) +
                                        (fifth_set_stat.getDoubleFaultUp() - prev_forth_set_stat.getDoubleFaultUp()) +
                                        (fifth_set_stat.getUnforceErrorUp() - prev_forth_set_stat.getUnforceErrorUp()) +
                                        (fifth_set_stat.getFoulToLoseUp() - prev_forth_set_stat.getFoulToLoseUp())));
                        currentArray.add(item12);
                    } else {
                        Log.d(TAG, "current_state = null");

                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                "0", "0", 0, 0);
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                "0", "0", 0, 0);
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error), "0", "0", 0, 0);
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error), "0", "0", 0, 0);
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), "0%", "0%", 0, 0);
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), "0%", "0%", 0, 0);
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner), "0", "0", 0, 0);
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner), "0", "0", 0, 0);
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner), "0", "0", 0, 0);
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won), "0", "0", 0, 0);
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won), "0%", "0%", 0, 0);
                        currentArray.add(item11);

                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point), "0", "0", 0, 0);
                        currentArray.add(item12);
                    }
                } else { //position = 0
                    if (current_state != null) {

                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                String.valueOf(current_state.getAceCountUp()), String.valueOf(current_state.getAceCountDown()),
                                (int)(current_state.getAceCountUp()),
                                (int)(current_state.getAceCountDown()));
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                String.valueOf(current_state.getDoubleFaultUp()), String.valueOf(current_state.getDoubleFaultDown()),
                                (int)(current_state.getDoubleFaultUp()),
                                (int)(current_state.getDoubleFaultDown()));
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error),
                                String.valueOf(current_state.getUnforceErrorUp()),
                                String.valueOf(current_state.getUnforceErrorDown()),
                                (int)(current_state.getUnforceErrorUp()),
                                (int)(current_state.getUnforceErrorDown()));
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error),
                                String.valueOf(current_state.getForceErrorUp()),
                                String.valueOf(current_state.getForceErrorDown()),
                                (int)(current_state.getForceErrorUp()),
                                (int)(current_state.getForceErrorDown()));
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), current_state.getFirstServeUp() == 0 ? "0%" :
                                String.format(current_local, "%.1f", ((float) (current_state.getFirstServeUp() - current_state.getFirstServeMissUp()) / (float) current_state.getFirstServeUp()) * 100) + "%",
                                current_state.getFirstServeDown() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (current_state.getFirstServeDown() - current_state.getFirstServeMissDown()) / (float) current_state.getFirstServeDown()) * 100) + "%",
                                (int)(((float) (current_state.getFirstServeUp() - current_state.getFirstServeMissUp()) / (float) current_state.getFirstServeUp()) * 100),
                                (int)(((float) (current_state.getFirstServeDown() - current_state.getFirstServeMissDown()) / (float) current_state.getFirstServeDown()) * 100));
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won),
                                current_state.getFirstServeWonUp() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) current_state.getFirstServeWonUp() / (float) (current_state.getFirstServeWonUp() + current_state.getFirstServeLostUp())) * 100) + "%",
                                current_state.getFirstServeWonDown() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) current_state.getFirstServeWonDown() / (float) (current_state.getFirstServeWonDown() + current_state.getFirstServeLostDown())) * 100) + "%",
                                (int)(((float) current_state.getFirstServeWonUp() / (float) (current_state.getFirstServeWonUp() + current_state.getFirstServeLostUp())) * 100),
                                (int)(((float) current_state.getFirstServeWonDown() / (float) (current_state.getFirstServeWonDown() + current_state.getFirstServeLostDown())) * 100));
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), current_state.getSecondServeUp() == 0 ? "0%" :
                                String.format(current_local, "%.1f", ((float) (current_state.getSecondServeUp() - current_state.getDoubleFaultUp()) / (float) current_state.getSecondServeUp()) * 100) + "%",
                                current_state.getSecondServeDown() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) (current_state.getSecondServeDown() - current_state.getDoubleFaultDown()) / (float) current_state.getSecondServeDown()) * 100) + "%",
                                (int)(((float) (current_state.getSecondServeUp() - current_state.getDoubleFaultUp()) / (float) current_state.getSecondServeUp()) * 100),
                                (int)(((float) (current_state.getSecondServeDown() - current_state.getDoubleFaultDown()) / (float) current_state.getSecondServeDown()) * 100));
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won),
                                current_state.getSecondServeWonUp() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) current_state.getSecondServeWonUp() / (float) (current_state.getSecondServeWonUp() + current_state.getSecondServeLostUp())) * 100) + "%",
                                current_state.getSecondServeDown() == 0 ? "0%" :
                                        String.format(current_local, "%.1f", ((float) current_state.getSecondServeWonDown() / (float) (current_state.getSecondServeWonDown() + current_state.getSecondServeLostDown())) * 100) + "%",
                                (int)(((float) current_state.getSecondServeWonUp() / (float) (current_state.getSecondServeWonUp() + current_state.getSecondServeLostUp())) * 100),
                                (int)(((float) current_state.getSecondServeWonDown() / (float) (current_state.getSecondServeWonDown() + current_state.getSecondServeLostDown())) * 100));
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner),
                                String.valueOf(current_state.getForehandWinnerUp() +
                                        current_state.getBackhandWinnerUp() +
                                        current_state.getForehandVolleyUp() +
                                        current_state.getBackhandVolleyUp()),
                                String.valueOf(current_state.getForehandWinnerDown() +
                                        current_state.getBackhandWinnerDown() +
                                        current_state.getForehandVolleyDown() +
                                        current_state.getBackhandVolleyDown()),
                                (current_state.getForehandWinnerUp() +
                                        current_state.getBackhandWinnerUp() +
                                        current_state.getForehandVolleyUp() +
                                        current_state.getBackhandVolleyUp()),
                                (current_state.getForehandWinnerDown() +
                                        current_state.getBackhandWinnerDown() +
                                        current_state.getForehandVolleyDown() +
                                        current_state.getBackhandVolleyDown()));
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner),
                                String.valueOf(current_state.getForehandWinnerUp() +
                                        current_state.getForehandVolleyUp()),
                                String.valueOf(current_state.getForehandWinnerDown() +
                                        current_state.getForehandVolleyDown()),
                                (current_state.getForehandWinnerUp() +
                                        current_state.getForehandVolleyUp()),
                                (current_state.getForehandWinnerDown() +
                                        current_state.getForehandVolleyDown()));
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner),
                                String.valueOf(current_state.getBackhandWinnerUp() +
                                        current_state.getBackhandVolleyUp()),
                                String.valueOf(current_state.getBackhandWinnerDown() +
                                        current_state.getBackhandVolleyDown()),
                                (current_state.getBackhandWinnerUp() +
                                        current_state.getBackhandVolleyUp()),
                                (current_state.getBackhandWinnerDown() +
                                        current_state.getBackhandVolleyDown()));
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won),
                                String.valueOf(current_state.getForehandVolleyUp() +
                                        current_state.getBackhandVolleyUp()),
                                String.valueOf(current_state.getForehandVolleyDown() +
                                        current_state.getBackhandVolleyDown()),
                                (current_state.getForehandVolleyUp() +
                                        current_state.getBackhandVolleyUp()),
                                (current_state.getForehandVolleyDown() +
                                        current_state.getBackhandVolleyDown()));
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won),
                                current_state.getBreakPointUp() == 0 ? "0%" : "("+
                                        String.valueOf(current_state.getBreakPointUp() - current_state.getBreakPointMissUp()) +"/"+String.valueOf(current_state.getBreakPointUp())+") "+
                                        String.format(current_local, "%.1f", ((float) (current_state.getBreakPointUp() - current_state.getBreakPointMissUp()) / (float) current_state.getBreakPointUp()) * 100) + "%",
                                current_state.getBreakPointDown() == 0 ? "0%" : "("+
                                        String.valueOf(current_state.getBreakPointDown() - current_state.getBreakPointMissDown()) +"/"+String.valueOf(current_state.getBreakPointDown())+") "+
                                        String.format(current_local, "%.1f", ((float) (current_state.getBreakPointDown() - current_state.getBreakPointMissDown()) / (float) current_state.getBreakPointDown()) * 100) + "%",
                                (int)(((float) (current_state.getBreakPointUp() - current_state.getBreakPointMissUp()) / (float) current_state.getBreakPointUp()) * 100),
                                (int)(((float) (current_state.getBreakPointDown() - current_state.getBreakPointMissDown()) / (float) current_state.getBreakPointDown()) * 100));
                        currentArray.add(item11);
                        //total point
                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point),
                                String.valueOf(current_state.getForehandWinnerUp() +
                                        current_state.getBackhandWinnerUp() +
                                        current_state.getForehandVolleyUp() +
                                        current_state.getBackhandVolleyUp() +
                                        current_state.getDoubleFaultDown() +
                                        current_state.getUnforceErrorDown() +
                                        current_state.getFoulToLoseDown()),
                                String.valueOf(current_state.getForehandWinnerDown() +
                                        current_state.getBackhandWinnerDown() +
                                        current_state.getForehandVolleyDown() +
                                        current_state.getBackhandVolleyDown() +
                                        current_state.getDoubleFaultUp() +
                                        current_state.getUnforceErrorUp() +
                                        current_state.getFoulToLoseUp()),
                                (current_state.getForehandWinnerUp() +
                                        current_state.getBackhandWinnerUp() +
                                        current_state.getForehandVolleyUp() +
                                        current_state.getBackhandVolleyUp() +
                                        current_state.getDoubleFaultDown() +
                                        current_state.getUnforceErrorDown() +
                                        current_state.getFoulToLoseDown()),
                                (current_state.getForehandWinnerDown() +
                                        current_state.getBackhandWinnerDown() +
                                        current_state.getForehandVolleyDown() +
                                        current_state.getBackhandVolleyDown() +
                                        current_state.getDoubleFaultUp() +
                                        current_state.getUnforceErrorUp() +
                                        current_state.getFoulToLoseUp()));
                        currentArray.add(item12);
                    } else {
                        Log.d(TAG, "current_state = null");

                        CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
                        currentArray.add(item1);

                        CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                                "0", "0", 0, 0);
                        currentArray.add(item2);

                        CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                                "0", "0", 0, 0);
                        currentArray.add(item3);

                        CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error), "0", "0", 0, 0);
                        currentArray.add(item4);

                        CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error), "0", "0", 0, 0);
                        currentArray.add(item13);

                        CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), "0%", "0%", 0, 0);
                        currentArray.add(item5);

                        CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item6);

                        CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), "0%", "0%", 0, 0);
                        currentArray.add(item7);

                        CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won), "0%", "0%", 0, 0);
                        currentArray.add(item8);

                        CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner), "0", "0", 0, 0);
                        currentArray.add(item9);

                        CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner), "0", "0", 0, 0);
                        currentArray.add(item14);

                        CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner), "0", "0", 0, 0);
                        currentArray.add(item15);

                        CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won), "0", "0", 0, 0);
                        currentArray.add(item10);

                        CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won), "0%", "0%", 0, 0);
                        currentArray.add(item11);

                        CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point), "0", "0", 0, 0);
                        currentArray.add(item12);
                    }

                }

                currentStatArrayAdapter = new CurrentStatArrayAdapter(CurrentStatActivity.this, R.layout.new_stat_current_item, currentArray);
                listView.setAdapter(currentStatArrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        if (current_state != null) {

            CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
            currentArray.add(item1);

            CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                    String.valueOf(current_state.getAceCountUp()), String.valueOf(current_state.getAceCountDown()),
                    (int)(current_state.getAceCountUp()),
                    (int)(current_state.getAceCountDown()));
            currentArray.add(item2);

            CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                    String.valueOf(current_state.getDoubleFaultUp()), String.valueOf(current_state.getDoubleFaultDown()),
                    (int)(current_state.getDoubleFaultUp()),
                    (int)(current_state.getDoubleFaultDown()));
            currentArray.add(item3);

            CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error),
                    String.valueOf(current_state.getUnforceErrorUp()),
                    String.valueOf(current_state.getUnforceErrorDown()),
                    (int)(current_state.getUnforceErrorUp()),
                    (int)(current_state.getUnforceErrorDown()));
            currentArray.add(item4);

            CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error),
                    String.valueOf(current_state.getForceErrorUp()),
                    String.valueOf(current_state.getForceErrorDown()),
                    (int)(current_state.getForceErrorUp()),
                    (int)(current_state.getForceErrorDown()));
            currentArray.add(item13);

            CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), current_state.getFirstServeUp() == 0 ? "0%" :
                    String.format(current_local, "%.1f", ((float) (current_state.getFirstServeUp() - current_state.getFirstServeMissUp()) / (float) current_state.getFirstServeUp()) * 100) + "%",
                    current_state.getFirstServeDown() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) (current_state.getFirstServeDown() - current_state.getFirstServeMissDown()) / (float) current_state.getFirstServeDown()) * 100) + "%",
                    (int)(((float) (current_state.getFirstServeUp() - current_state.getFirstServeMissUp()) / (float) current_state.getFirstServeUp()) * 100),
                    (int)(((float) (current_state.getFirstServeDown() - current_state.getFirstServeMissDown()) / (float) current_state.getFirstServeDown()) * 100));
            currentArray.add(item5);

            CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won),
                    current_state.getFirstServeWonUp() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) current_state.getFirstServeWonUp() / (float) (current_state.getFirstServeWonUp() + current_state.getFirstServeLostUp())) * 100) + "%",
                    current_state.getFirstServeWonDown() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) current_state.getFirstServeWonDown() / (float) (current_state.getFirstServeWonDown() + current_state.getFirstServeLostDown())) * 100) + "%",
                    (int)(((float) current_state.getFirstServeWonUp() / (float) (current_state.getFirstServeWonUp() + current_state.getFirstServeLostUp())) * 100),
                    (int)(((float) current_state.getFirstServeWonDown() / (float) (current_state.getFirstServeWonDown() + current_state.getFirstServeLostDown())) * 100));
            currentArray.add(item6);

            CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), current_state.getSecondServeUp() == 0 ? "0%" :
                    String.format(current_local, "%.1f", ((float) (current_state.getSecondServeUp() - current_state.getDoubleFaultUp()) / (float) current_state.getSecondServeUp()) * 100) + "%",
                    current_state.getSecondServeDown() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) (current_state.getSecondServeDown() - current_state.getDoubleFaultDown()) / (float) current_state.getSecondServeDown()) * 100) + "%",
                    (int)(((float) (current_state.getSecondServeUp() - current_state.getDoubleFaultUp()) / (float) current_state.getSecondServeUp()) * 100),
                    (int)(((float) (current_state.getSecondServeDown() - current_state.getDoubleFaultDown()) / (float) current_state.getSecondServeDown()) * 100));
            currentArray.add(item7);

            CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won),
                    current_state.getSecondServeWonUp() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) current_state.getSecondServeWonUp() / (float) (current_state.getSecondServeWonUp() + current_state.getSecondServeLostUp())) * 100) + "%",
                    current_state.getSecondServeDown() == 0 ? "0%" :
                            String.format(current_local, "%.1f", ((float) current_state.getSecondServeWonDown() / (float) (current_state.getSecondServeWonDown() + current_state.getSecondServeLostDown())) * 100) + "%",
                    (int)(((float) current_state.getSecondServeWonUp() / (float) (current_state.getSecondServeWonUp() + current_state.getSecondServeLostUp())) * 100),
                    (int)(((float) current_state.getSecondServeWonDown() / (float) (current_state.getSecondServeWonDown() + current_state.getSecondServeLostDown())) * 100));
            currentArray.add(item8);

            CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner),
                    String.valueOf(current_state.getForehandWinnerUp() +
                            current_state.getBackhandWinnerUp() +
                            current_state.getForehandVolleyUp() +
                            current_state.getBackhandVolleyUp()),
                    String.valueOf(current_state.getForehandWinnerDown() +
                            current_state.getBackhandWinnerDown() +
                            current_state.getForehandVolleyDown() +
                            current_state.getBackhandVolleyDown()),
                    (current_state.getForehandWinnerUp() +
                            current_state.getBackhandWinnerUp() +
                            current_state.getForehandVolleyUp() +
                            current_state.getBackhandVolleyUp()),
                    (current_state.getForehandWinnerDown() +
                            current_state.getBackhandWinnerDown() +
                            current_state.getForehandVolleyDown() +
                            current_state.getBackhandVolleyDown()));
            currentArray.add(item9);

            CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner),
                    String.valueOf(current_state.getForehandWinnerUp() +
                            current_state.getForehandVolleyUp()),
                    String.valueOf(current_state.getForehandWinnerDown() +
                            current_state.getForehandVolleyDown()),
                    (current_state.getForehandWinnerUp() +
                            current_state.getForehandVolleyUp()),
                    (current_state.getForehandWinnerDown() +
                            current_state.getForehandVolleyDown()));
            currentArray.add(item14);

            CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner),
                    String.valueOf(current_state.getBackhandWinnerUp() +
                            current_state.getBackhandVolleyUp()),
                    String.valueOf(current_state.getBackhandWinnerDown() +
                            current_state.getBackhandVolleyDown()),
                    (current_state.getBackhandWinnerUp() +
                            current_state.getBackhandVolleyUp()),
                    (current_state.getBackhandWinnerDown() +
                            current_state.getBackhandVolleyDown()));
            currentArray.add(item15);

            CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won),
                    String.valueOf(current_state.getForehandVolleyUp() +
                            current_state.getBackhandVolleyUp()),
                    String.valueOf(current_state.getForehandVolleyDown() +
                            current_state.getBackhandVolleyDown()),
                    (current_state.getForehandVolleyUp() +
                            current_state.getBackhandVolleyUp()),
                    (current_state.getForehandVolleyDown() +
                            current_state.getBackhandVolleyDown()));
            currentArray.add(item10);

            CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won),
                    current_state.getBreakPointUp() == 0 ? "0%" : "("+
                            String.valueOf(current_state.getBreakPointUp() - current_state.getBreakPointMissUp()) +"/"+String.valueOf(current_state.getBreakPointUp())+") "+
                            String.format(current_local, "%.1f", ((float) (current_state.getBreakPointUp() - current_state.getBreakPointMissUp()) / (float) current_state.getBreakPointUp()) * 100) + "%",
                    current_state.getBreakPointDown() == 0 ? "0%" : "("+
                            String.valueOf(current_state.getBreakPointDown() - current_state.getBreakPointMissDown()) +"/"+String.valueOf(current_state.getBreakPointDown())+") "+
                            String.format(current_local, "%.1f", ((float) (current_state.getBreakPointDown() - current_state.getBreakPointMissDown()) / (float) current_state.getBreakPointDown()) * 100) + "%",
                    (int)(((float) (current_state.getBreakPointUp() - current_state.getBreakPointMissUp()) / (float) current_state.getBreakPointUp()) * 100),
                    (int)(((float) (current_state.getBreakPointDown() - current_state.getBreakPointMissDown()) / (float) current_state.getBreakPointDown()) * 100));
            currentArray.add(item11);
            //total point
            CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point),
                    String.valueOf(current_state.getForehandWinnerUp() +
                            current_state.getBackhandWinnerUp() +
                            current_state.getForehandVolleyUp() +
                            current_state.getBackhandVolleyUp() +
                            current_state.getDoubleFaultDown() +
                            current_state.getUnforceErrorDown() +
                            current_state.getFoulToLoseDown()),
                    String.valueOf(current_state.getForehandWinnerDown() +
                            current_state.getBackhandWinnerDown() +
                            current_state.getForehandVolleyDown() +
                            current_state.getBackhandVolleyDown() +
                            current_state.getDoubleFaultUp() +
                            current_state.getUnforceErrorUp() +
                            current_state.getFoulToLoseUp()),
                    (current_state.getForehandWinnerUp() +
                            current_state.getBackhandWinnerUp() +
                            current_state.getForehandVolleyUp() +
                            current_state.getBackhandVolleyUp() +
                            current_state.getDoubleFaultDown() +
                            current_state.getUnforceErrorDown() +
                            current_state.getFoulToLoseDown()),
                    (current_state.getForehandWinnerDown() +
                            current_state.getBackhandWinnerDown() +
                            current_state.getForehandVolleyDown() +
                            current_state.getBackhandVolleyDown() +
                            current_state.getDoubleFaultUp() +
                            current_state.getUnforceErrorUp() +
                            current_state.getFoulToLoseUp()));
            currentArray.add(item12);
        } else {
            Log.d(TAG, "current_state = null");

            CurrentStatItem item1 = new CurrentStatItem("", playerUp, playerDown, 0, 0);
            currentArray.add(item1);

            CurrentStatItem item2 = new CurrentStatItem(getResources().getString(R.string.game_ace),
                    "0", "0", 0, 0);
            currentArray.add(item2);

            CurrentStatItem item3 = new CurrentStatItem(getResources().getString(R.string.game_double_faults),
                    "0", "0", 0, 0);
            currentArray.add(item3);

            CurrentStatItem item4 = new CurrentStatItem(getResources().getString(R.string.stat_unforced_error), "0", "0", 0, 0);
            currentArray.add(item4);

            CurrentStatItem item13 = new CurrentStatItem(getResources().getString(R.string.game_forced_error), "0", "0", 0, 0);
            currentArray.add(item13);

            CurrentStatItem item5 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve), "0%", "0%", 0, 0);
            currentArray.add(item5);

            CurrentStatItem item6 = new CurrentStatItem(getResources().getString(R.string.stat_first_serve_won), "0%", "0%", 0, 0);
            currentArray.add(item6);

            CurrentStatItem item7 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve), "0%", "0%", 0, 0);
            currentArray.add(item7);

            CurrentStatItem item8 = new CurrentStatItem(getResources().getString(R.string.stat_second_serve_won), "0%", "0%", 0, 0);
            currentArray.add(item8);

            CurrentStatItem item9 = new CurrentStatItem(getResources().getString(R.string.stat_winner), "0", "0", 0, 0);
            currentArray.add(item9);

            CurrentStatItem item14 = new CurrentStatItem(getResources().getString(R.string.game_forehand_winner), "0", "0", 0, 0);
            currentArray.add(item14);

            CurrentStatItem item15 = new CurrentStatItem(getResources().getString(R.string.game_backhand_winner), "0", "0", 0, 0);
            currentArray.add(item15);

            CurrentStatItem item10 = new CurrentStatItem(getResources().getString(R.string.stat_net_point_won), "0", "0", 0, 0);
            currentArray.add(item10);

            CurrentStatItem item11 = new CurrentStatItem(getResources().getString(R.string.stat_break_point_won), "0%", "0%", 0, 0);
            currentArray.add(item11);

            CurrentStatItem item12 = new CurrentStatItem(getResources().getString(R.string.stat_total_point), "0", "0", 0, 0);
            currentArray.add(item12);
        }

        currentStatArrayAdapter = new CurrentStatArrayAdapter(CurrentStatActivity.this, R.layout.new_stat_current_item, currentArray);
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
        super.onPause();

        Log.d(TAG, "onPause");

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
