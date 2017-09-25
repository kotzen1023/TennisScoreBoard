package com.seventhmoon.tennisscoreboard;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;

import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.seventhmoon.tennisscoreboard.Audio.VoicePlay;
import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.State;
import com.seventhmoon.tennisscoreboard.Data.StateAction;
import com.seventhmoon.tennisscoreboard.Service.SearchFileService;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.TimeZone;

import static com.seventhmoon.tennisscoreboard.Data.Constants.VOICE_TYPE;
import static com.seventhmoon.tennisscoreboard.Data.Constants.VOICE_TYPE.*;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.append_record;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.check_file_exist;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.clear_record;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.copy_file;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.get_absolute_path;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.read_record;
import static com.seventhmoon.tennisscoreboard.Data.StateAction.OPPT_RETIRE;
import static com.seventhmoon.tennisscoreboard.Data.StateAction.OPPT_SCORE;
import static com.seventhmoon.tennisscoreboard.Data.StateAction.OPPT_SERVE;
import static com.seventhmoon.tennisscoreboard.Data.StateAction.YOU_RETIRE;
import static com.seventhmoon.tennisscoreboard.Data.StateAction.YOU_SCORE;
import static com.seventhmoon.tennisscoreboard.Data.StateAction.YOU_SERVE;


public class GameActivity extends AppCompatActivity{
    private static final String TAG = GameActivity.class.getName();

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";

    private Context context;


    private TextView gameUp;
    private TextView gameDown;
    private TextView pointUp;
    private TextView pointDown;
    private ImageView imgServeUp;
    private ImageView imgServeDown;
    private LinearLayout setLayout;
    //private LinearLayout nameLayout;
    private TextView setUp;
    private TextView setDown;
    private ImageView imgWinCheckUp;
    private ImageView imgWinCheckDown;

    private ImageView imgPlayOrPause;

    //private TextView mClockView;


    private TextView textCurrentTime;
    private TextView textGameTime;

    private static String set;
    private static String games;
    private static String tiebreak;
    private static String deuce;
    private static String serve;
    private static String is_retire = "0";
    //private static String duration;

    private static String filename;
    private static String playerUp;
    private static String playerDown;

    //private static long startTime;
    private static Handler handler;
    private static long time_use = 0;

    public static Deque<State> stack = new ArrayDeque<>();

    //public static File RootDirectory = new File("/");

    private static boolean is_pause = false;

    ProgressDialog loadDialog = null;

    //for state
    //private static boolean is_ace = false;
    //private static boolean is_double_fault = false;
    private static boolean is_second_serve = false;
    private static boolean is_break_point = false;
    //private static boolean is_unforced_error = false;
    //private static boolean is_forehand_winner = false;
    //private static boolean is_backhand_winner = false;
    //private static boolean is_forehand_volley = false;
    //private static boolean is_backhand_volley = false;
    private static byte ace_count = 0;
    private static byte double_faults_count = 0;
    private static short forced_errors_count = 0;
    private static short unforced_errors_count = 0;
    private static short forehand_winner_count = 0;
    private static short backhand_winner_count = 0;
    private static short forehand_volley_count = 0;
    private static short backhand_volley_count = 0;
    private static byte foul_to_lose_count = 0;
    private static short first_serve_count = 0;
    private static short first_serve_miss = 0;
    private static short second_serve_count = 0;

    private static byte first_serve_won = 0;
    private static byte first_serve_lost = 0;
    private static byte second_serve_won = 0;
    private static byte second_serve_lost = 0;

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    public static VoicePlay voicePlay;

    private static ArrayList<Integer> voiceList = new ArrayList<>();
    private static ArrayList<String> voiceUserList = new ArrayList<>();
    private static boolean voiceOn = false;
    private MenuItem voice_item, voice_support_item;

    //private static int current_voice_select = 0;
    private static boolean is_current_game_over = false;

    private static boolean is_saving_state = false;
    private static int total_state = 0;
    private static int state_num_saved = 0;

    public static VOICE_TYPE current_voice_type = GBR_MAN;

    private static boolean am_I_Tiebreak_First_Serve = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //for android 7.0+ upload file
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }



        pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        voiceOn = pref.getBoolean("VOICE_ON", false);
        int current_voice_select = pref.getInt("VOICE_SELECT", 0);

        switch (current_voice_select) {
            case 0:
                current_voice_type = GBR_MAN;
                break;
            case 1:
                current_voice_type = GBR_WOMAN;
                break;
            case 2:
                current_voice_type = USER_RECORD;
                break;
            default:
                current_voice_type = GBR_MAN;
                break;
        }


        context = getBaseContext();

        voicePlay = new VoicePlay(context);

        final IntentFilter filter;

        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }
        //is_ace = false;
        //is_double_fault = false;
        is_second_serve = false;
        is_break_point = false;
        //is_unforced_error = false;
        //is_forehand_winner = false;
        //is_backhand_winner = false;
        //is_forehand_volley = false;
        //is_backhand_volley = false;
        first_serve_count = 0;
        first_serve_miss = 0;
        second_serve_count = 0;

        first_serve_won = 0;
        first_serve_lost = 0;
        second_serve_won = 0;
        second_serve_lost = 0;

        //Button btnYouScore;
        Button btnBack;
        //Button btnOpptScore;
        Button btnReset;
        Button btnSave;
        Button btnLoad;
        TextView btnOpptScore;
        TextView btnYouScore;

        LinearLayout nameLayout;

        handler = new Handler();

        //startTime = System.currentTimeMillis();

        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);

        Intent intent = getIntent();

        Log.d(TAG, "intent type = "+intent.getType());

        set = intent.getStringExtra("SETUP_SET");
        games = intent.getStringExtra("SETUP_GAME");
        tiebreak = intent.getStringExtra("SETUP_TIEBREAK");
        deuce = intent.getStringExtra("SETUP_DEUCE");
        serve = intent.getStringExtra("SETUP_SERVE");

        filename = intent.getStringExtra("FILE_NAME");
        playerUp = intent.getStringExtra("PLAYER_UP");
        playerDown = intent.getStringExtra("PLAYER_DOWN");
        //duration = intent.getStringExtra("GAME_DURATION");

        Log.e(TAG, "SET = "+set);
        Log.e(TAG, "GAMES = "+games);
        Log.e(TAG, "TIEBREAK = "+tiebreak);
        Log.e(TAG, "DEUCE = "+deuce);

        Log.e(TAG, "IS_RETIRE = "+is_retire);

        Log.e(TAG, "filename = "+filename);
        Log.e(TAG, "playerUp = "+playerUp);
        Log.e(TAG, "playerDown = "+playerDown);

        //mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        gameUp = (TextView) findViewById(R.id.textViewGameUp);
        gameDown = (TextView) findViewById(R.id.textViewGameDown);
        pointUp = (TextView) findViewById(R.id.textViewPointUp);
        pointDown = (TextView) findViewById(R.id.textViewPointDown);

        imgServeUp = (ImageView) findViewById(R.id.imageViewServeUp);
        imgServeDown = (ImageView) findViewById(R.id.imageViewServeDown);

        textCurrentTime = (TextView) findViewById(R.id.currentTime);
        textGameTime = (TextView) findViewById(R.id.gameTime);

        setLayout = (LinearLayout) findViewById(R.id.setLayout);
        nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        setUp = (TextView) findViewById(R.id.textViewSetUp);
        setDown = (TextView) findViewById(R.id.textViewSetDown);

        imgWinCheckUp = (ImageView) findViewById(R.id.imageWincheckUp);
        imgWinCheckDown = (ImageView) findViewById(R.id.imageWincheckDown);

        imgPlayOrPause = (ImageView) findViewById(R.id.imageViewPlayOrPause);

        //init score board
        gameUp.setText("0");
        gameDown.setText("0");
        pointUp.setText("0");
        pointDown.setText("0");

        if (serve != null) {
            if (serve.equals("0")) { //you serve first
                imgServeUp.setVisibility(View.INVISIBLE);
                imgServeDown.setVisibility(View.VISIBLE);
            } else {
                imgServeUp.setVisibility(View.VISIBLE);
                imgServeDown.setVisibility(View.INVISIBLE);
            }
        } else {
            serve = "0";
            imgServeUp.setVisibility(View.INVISIBLE);
            imgServeDown.setVisibility(View.VISIBLE);
        }

        if (playerUp != null && playerDown != null) {
            if (playerUp.equals(""))
                playerUp = "Player1";
            if (playerDown.equals(""))
                playerDown = "Player2";
            nameLayout.setVisibility(View.VISIBLE);
        } else {
            if (playerUp == null)
                playerUp = "Player1";
            if (playerDown == null)
                playerDown = "Player2";
            nameLayout.setVisibility(View.VISIBLE);
        }

        //load file from intent
        if (intent.getType() != null) {
            Log.i(TAG, "get -> " + intent.getType());
            //must use under api = 19
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (intent.getType().contains("text/")) { //text start


                    String downFile = intent.getData().getPath();
                    Log.e(TAG, "downFile path = "+downFile);
                    if (downFile != null) {
                        filename = copy_file(downFile);
                    }
                    Log.i(TAG, "text file, file name = "+ filename);
                }
            //} else {

            //}
        }


        //load file to stack

        stack.clear();
        if (check_file_exist(filename)) {
            Log.d(TAG, "load file success!");
            loadDialog = new ProgressDialog(this);
            loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadDialog.setTitle("Loading...");
            loadDialog.setIndeterminate(false);
            loadDialog.setCancelable(false);

            loadDialog.show();

            String message = read_record(filename);
            Log.d(TAG, "message = "+ message);
            String msg[] = message.split("\\|");

            Log.d(TAG, "msg[0] = "+ msg[0]);

            String info[] = msg[0].split(";");

            if (info.length > 1) {

                playerUp = info[0];
                playerDown = info[1];

                if (playerUp != null && playerDown != null) {
                    if (!playerUp.equals("") && !playerDown.equals(""))
                        nameLayout.setVisibility(View.VISIBLE);
                    else
                        nameLayout.setVisibility(View.GONE);
                }

                tiebreak = "0"; //init value
                try {
                    boolean ret = Boolean.valueOf(info[2]);
                    if (!ret) { //tiebreak
                        tiebreak = "1";
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


                deuce = "0";
                try {
                    boolean ret = Boolean.valueOf(info[3]);
                    if (!ret) { //deuce
                        deuce = "1";
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                serve = "0";
                imgServeUp.setVisibility(View.INVISIBLE);
                imgServeDown.setVisibility(View.VISIBLE);
                try {
                    boolean ret = Boolean.valueOf(info[4]);
                    if (!ret) { //first serve
                        serve = "1";
                        imgServeUp.setVisibility(View.VISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);
                    }
                } catch (NumberFormatException e) {

                    e.printStackTrace();
                }



                //set
                set = info[5];

                if (info.length > 6) {
                    is_retire = info[6];
                } else {
                    is_retire = "0";
                }

                if (info.length > 7) {
                    games = info[7];
                }

            } else {
                playerUp = "Player1";
                playerDown = "Player2";
                tiebreak = "0";
                deuce = "0";
                serve = "0";
                set = "0";
                is_retire = "0";
                games = "0";
            }

            if (msg.length > 1) {

                String stat[] = msg[1].split("&");


                for (String s : stat) {
                    String data[] = s.split(";");
                    State new_state = new State();

                    try {
                        new_state.setCurrent_set(Byte.valueOf(data[0]));
                        new_state.setServe(Boolean.valueOf(data[1]));
                        new_state.setInTiebreak(Boolean.valueOf(data[2]));
                        new_state.setFinish(Boolean.valueOf(data[3]));
                        new_state.setSecondServe(Boolean.valueOf(data[4]));
                        new_state.setInBreakPoint(Boolean.valueOf(data[5]));
                        new_state.setSetsUp(Byte.valueOf(data[6]));
                        new_state.setSetsDown(Byte.valueOf(data[7]));
                        new_state.setDuration(Long.valueOf(data[8]));

                        //ace
                        new_state.setAceCountUp(Byte.valueOf(data[9]));
                        new_state.setAceCountDown(Byte.valueOf(data[10]));
                        //first serve
                        new_state.setFirstServeUp(Short.valueOf(data[11]));
                        new_state.setFirstServeDown(Short.valueOf(data[12]));
                        //first serve miss
                        new_state.setFirstServeMissUp(Short.valueOf(data[13]));
                        new_state.setFirstServeMissDown(Short.valueOf(data[14]));
                        //second serve
                        new_state.setSecondServeUp(Short.valueOf(data[15]));
                        new_state.setSecondServeDown(Short.valueOf(data[16]));
                        //break point
                        new_state.setBreakPointUp(Byte.valueOf(data[17]));
                        new_state.setBreakPointDown(Byte.valueOf(data[18]));
                        //break point miss
                        new_state.setBreakPointMissUp(Byte.valueOf(data[19]));
                        new_state.setBreakPointMissDown(Byte.valueOf(data[20]));
                        //first serve won
                        new_state.setFirstServeWonUp(Short.valueOf(data[21]));
                        //first serve lost
                        new_state.setFirstServeLostUp(Short.valueOf(data[23]));
                        new_state.setFirstServeLostDown(Short.valueOf(data[24]));
                        //second serve won
                        new_state.setSecondServeWonUp(Short.valueOf(data[25]));
                        new_state.setSecondServeWonDown(Short.valueOf(data[26]));
                        //second serve lost
                        new_state.setSecondServeLostUp(Short.valueOf(data[27]));
                        new_state.setSecondServeLostDown(Short.valueOf(data[28]));
                        //double faults
                        new_state.setDoubleFaultUp(Byte.valueOf(data[29]));
                        new_state.setFirstServeWonDown(Short.valueOf(data[22]));
                        new_state.setDoubleFaultDown(Byte.valueOf(data[30]));
                        //unforced error
                        new_state.setUnforceErrorUp(Byte.valueOf(data[31]));
                        new_state.setUnforceErrorDown(Byte.valueOf(data[32]));
                        //forehand winner
                        new_state.setForehandWinnerUp(Byte.valueOf(data[33]));
                        new_state.setForehandWinnerDown(Byte.valueOf(data[34]));
                        //backhand winner
                        new_state.setBackhandWinnerUp(Byte.valueOf(data[35]));
                        new_state.setBackhandWinnerDown(Byte.valueOf(data[36]));
                        //forehand volley
                        new_state.setForehandVolleyUp(Byte.valueOf(data[37]));
                        new_state.setForehandVolleyDown(Byte.valueOf(data[38]));
                        new_state.setBackhandVolleyUp(Byte.valueOf(data[39]));
                        new_state.setBackhandVolleyDown(Byte.valueOf(data[40]));
                        //foul to lose
                        new_state.setFoulToLoseUp(Byte.valueOf(data[41]));
                        new_state.setFoulToLoseDown(Byte.valueOf(data[42]));

                        new_state.setSet_game_up((byte) 0x1, Byte.valueOf(data[43]));
                        new_state.setSet_game_down((byte) 0x1, Byte.valueOf(data[44]));
                        new_state.setSet_point_up((byte) 0x1, Byte.valueOf(data[45]));
                        new_state.setSet_point_down((byte) 0x1, Byte.valueOf(data[46]));
                        new_state.setSet_tiebreak_point_up((byte) 0x1, Byte.valueOf(data[47]));
                        new_state.setSet_tiebreak_point_down((byte) 0x1, Byte.valueOf(data[48]));

                        new_state.setSet_game_up((byte) 0x2, Byte.valueOf(data[49]));
                        new_state.setSet_game_down((byte) 0x2, Byte.valueOf(data[50]));
                        new_state.setSet_point_up((byte) 0x2, Byte.valueOf(data[51]));
                        new_state.setSet_point_down((byte) 0x2, Byte.valueOf(data[52]));
                        new_state.setSet_tiebreak_point_up((byte) 0x2, Byte.valueOf(data[53]));
                        new_state.setSet_tiebreak_point_down((byte) 0x2, Byte.valueOf(data[54]));

                        new_state.setSet_game_up((byte) 0x3, Byte.valueOf(data[55]));
                        new_state.setSet_game_down((byte) 0x3, Byte.valueOf(data[56]));
                        new_state.setSet_point_up((byte) 0x3, Byte.valueOf(data[57]));
                        new_state.setSet_point_down((byte) 0x3, Byte.valueOf(data[58]));
                        new_state.setSet_tiebreak_point_up((byte) 0x3, Byte.valueOf(data[59]));
                        new_state.setSet_tiebreak_point_down((byte) 0x3, Byte.valueOf(data[60]));

                        new_state.setSet_game_up((byte) 0x4, Byte.valueOf(data[61]));
                        new_state.setSet_game_down((byte) 0x4, Byte.valueOf(data[62]));
                        new_state.setSet_point_up((byte) 0x4, Byte.valueOf(data[63]));
                        new_state.setSet_point_down((byte) 0x4, Byte.valueOf(data[64]));
                        new_state.setSet_tiebreak_point_up((byte) 0x4, Byte.valueOf(data[65]));
                        new_state.setSet_tiebreak_point_down((byte) 0x4, Byte.valueOf(data[66]));

                        new_state.setSet_game_up((byte) 0x5, Byte.valueOf(data[67]));
                        new_state.setSet_game_down((byte) 0x5, Byte.valueOf(data[68]));
                        new_state.setSet_point_up((byte) 0x5, Byte.valueOf(data[69]));
                        new_state.setSet_point_down((byte) 0x5, Byte.valueOf(data[70]));
                        new_state.setSet_tiebreak_point_up((byte) 0x5, Byte.valueOf(data[71]));
                        new_state.setSet_tiebreak_point_down((byte) 0x5, Byte.valueOf(data[72]));

                        if (data.length > 73) {
                            new_state.setForceErrorUp(Short.valueOf(data[73]));
                            new_state.setForceErrorDown(Short.valueOf(data[74]));
                        }

                        stack.addLast(new_state);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        toast(getResources().getString(R.string.file_load_error));
                        break;
                    }


                }



                //get top

                //State top = new State();

                State top = stack.peek();
                if (top != null) {
                    byte current_set = top.getCurrent_set();


                    if (top.getSetsUp() > 0 || top.getSetsDown() > 0) {
                        setLayout.setVisibility(View.VISIBLE);
                        setUp.setText(String.valueOf(top.getSetsUp()));
                        setDown.setText(String.valueOf(top.getSetsDown()));
                    } else {
                        setLayout.setVisibility(View.GONE);
                        setUp.setText("0");
                        setDown.setText("0");
                    }

                    gameUp.setText(String.valueOf(top.getSet_game_up(current_set)));
                    gameDown.setText(String.valueOf(top.getSet_game_down(current_set)));

                    if (top.isFinish()) {
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);

                        if (is_retire.equals("1")) { //Oppt retire, you win
                            imgWinCheckUp.setVisibility(View.GONE);
                            imgWinCheckDown.setVisibility(View.VISIBLE);
                        } else if (is_retire.equals("2")) { //you retire, oppt win
                            imgWinCheckUp.setVisibility(View.VISIBLE);
                            imgWinCheckDown.setVisibility(View.GONE);
                        } else {

                            if (top.getSetsUp() > top.getSetsDown()) {
                                imgWinCheckUp.setVisibility(View.VISIBLE);
                                imgWinCheckDown.setVisibility(View.GONE);
                            } else {
                                imgWinCheckUp.setVisibility(View.GONE);
                                imgWinCheckDown.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (top.isServe()) {
                            imgServeUp.setVisibility(View.INVISIBLE);
                            imgServeDown.setVisibility(View.VISIBLE);
                        } else {
                            imgServeUp.setVisibility(View.VISIBLE);
                            imgServeDown.setVisibility(View.INVISIBLE);
                        }
                    }

                    if (top.isSecondServe()) {
                        is_second_serve = true;
                        imgServeUp.setImageResource(R.drawable.ball_icon_red);
                        imgServeDown.setImageResource(R.drawable.ball_icon_red);
                    }
                    else {
                        is_second_serve = false;
                        imgServeUp.setImageResource(R.drawable.ball_icon);
                        imgServeDown.setImageResource(R.drawable.ball_icon);
                    }

                    if (top.isInBreakPoint()) {
                        is_break_point = true;
                    } else {
                        is_break_point = false;
                    }


                    if (!top.isInTiebreak()) { //not in tiebreak
                        if (top.getSet_point_up(current_set) == 1) {
                            pointUp.setText(String.valueOf(15));
                        } else if (top.getSet_point_up(current_set) == 2) {
                            pointUp.setText(String.valueOf(30));
                        } else if (top.getSet_point_up(current_set) == 3) {
                            pointUp.setText(String.valueOf(40));
                        } else if (top.getSet_point_up(current_set) == 4) {
                            String score = "Ad";
                            pointUp.setText(score);
                        } else {
                            pointUp.setText("0");
                        }
                    } else { //tie break;
                        pointUp.setText(String.valueOf(top.getSet_point_up(current_set)));
                    }

                    if (!top.isInTiebreak()) { //not in tiebreak
                        if (top.getSet_point_down(current_set) == 1) {
                            pointDown.setText(String.valueOf(15));
                        } else if (top.getSet_point_down(current_set) == 2) {
                            pointDown.setText(String.valueOf(30));
                        } else if (top.getSet_point_down(current_set) == 3) {
                            pointDown.setText(String.valueOf(40));
                        } else if (top.getSet_point_down(current_set) == 4) {
                            String score = "Ad";
                            pointDown.setText(score);
                        } else {
                            pointDown.setText("0");
                        }
                    } else {
                        pointDown.setText(String.valueOf(top.getSet_point_down(current_set)));
                    }

                    if (top.isInTiebreak()) { //in tiebreak
                        Log.e(TAG, "<In tiebreak>");
                        int plus = top.getSet_point_up(current_set)+top.getSet_point_down(current_set);
                        Log.e(TAG, "plus = "+plus+"");
                        if (top.isServe()) { //I serve
                            Log.d(TAG, "===> I serve");
                            if (plus % 4 == 1 || plus % 4 == 2) {
                                am_I_Tiebreak_First_Serve = false;
                            } else {
                                am_I_Tiebreak_First_Serve = true;
                            }
                            Log.e(TAG, "am_I_Tiebreak_First_Serve = "+am_I_Tiebreak_First_Serve);
                        } else { //oppt serve
                            Log.d(TAG, "===> Oppt serve");
                            if (plus % 4 == 1 || plus % 4 == 2) {
                                am_I_Tiebreak_First_Serve = true;
                            } else {
                                am_I_Tiebreak_First_Serve = false;
                            }
                            Log.e(TAG, "am_I_Tiebreak_First_Serve = "+am_I_Tiebreak_First_Serve);
                        }

                        Log.e(TAG, "<In tiebreak>");
                    }

                    //get back duration
                    time_use = top.getDuration();
                    if (top.isFinish()) {
                        handler.removeCallbacks(updateTimer);
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);

                        is_pause = false;
                        imgPlayOrPause.setVisibility(View.GONE);
                    }

                    Log.d(TAG, "########## top state start ##########");
                    Log.d(TAG, "current set : " + top.getCurrent_set());
                    Log.d(TAG, "Serve : " + top.isServe());
                    Log.d(TAG, "In tiebreak : " + top.isInTiebreak());
                    Log.d(TAG, "Finish : " + top.isFinish());
                    Log.d(TAG, "second serve : " + top.isSecondServe());
                    Log.d(TAG, "In break point : "+ top.isInBreakPoint());
                    //Log.d(TAG, "deuce : " + top.isDeuce());
                    //Log.d(TAG, "First serve : "+ top.isFirstServe());
                    Log.d(TAG, "duration : " + top.getDuration());

                    int set_limit;
                    switch (set) {
                        case "0":
                            set_limit = 1;
                            break;
                        case "1":
                            set_limit = 3;
                            break;
                        case "2":
                            set_limit = 5;
                            break;
                        default:
                            set_limit = 1;
                            break;
                    }


                    for (int i = 1; i <= set_limit; i++) {
                        Log.d(TAG, "================================");
                        Log.d(TAG, "[set " + i + "]");
                        Log.d(TAG, "[Game : " + top.getSet_game_up((byte) i) + " / " + top.getSet_game_down((byte) i) + "]");
                        Log.d(TAG, "[Point : " + top.getSet_point_up((byte) i) + " / " + top.getSet_point_down((byte) i) + "]");
                        Log.d(TAG, "[tiebreak : " + top.getSet_tiebreak_point_up((byte) i) + " / " + top.getSet_tiebreak_point_down((byte) i) + "]");
                    }

                    Log.d(TAG, "########## top state end ##########");

                } else {
                    gameUp.setText("0");
                    gameDown.setText("0");

                    imgServeUp.setVisibility(View.INVISIBLE);
                    imgServeDown.setVisibility(View.INVISIBLE);

                    pointUp.setText("0");
                    pointDown.setText("0");

                    if (serve.equals("0")) { //you server first
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.VISIBLE);
                    } else {
                        imgServeUp.setVisibility(View.VISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);
                    }
                }
            }

            loadDialog.dismiss();
        }



        //mClockView = (TextView) findViewById(R.id.clock);

        btnYouScore = (TextView) findViewById(R.id.textYouScore);
        //btnOpptScore = (Button) findViewById(R.id.btnOpptScore);
        btnOpptScore = (TextView) findViewById(R.id.textOpptScore);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnLoad = (Button) findViewById(R.id.btnLoad);

        TextView textViewPlayerUp = (TextView) findViewById(R.id.textViewPlayerUp);
        final TextView textViewPlayerDown = (TextView) findViewById(R.id.textViewPlayerDown);

        textViewPlayerUp.setText(playerUp);
        textViewPlayerDown.setText(playerDown);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //stopvoice
                //do stop play

                voiceList.clear();
                voiceUserList.clear();

                if (voiceOn)
                    voicePlay.doStopAudioPlayMulti();

                is_retire = "0";
                imgWinCheckUp.setVisibility(View.GONE);
                imgWinCheckDown.setVisibility(View.GONE);

                is_pause = false;
                imgPlayOrPause.setVisibility(View.VISIBLE);
                imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);

                if (stack.isEmpty()) {
                    Log.d(TAG, "stack is empty!");

                } else {
                    Log.d(TAG, "stack is not empty, pop top state");
                    byte current_set;
                    //stack.pop();
                    State popState = stack.pop();
                    time_use = popState.getDuration();

                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);


                    if (popState != null) { //pop out current
                        State back_state = stack.peek();
                        if (back_state != null) {
                            Log.d(TAG, "back_state not null");
                            current_set = back_state.getCurrent_set();


                            if (back_state.getSetsUp() > 0 || back_state.getSetsDown() > 0) {
                                setLayout.setVisibility(View.VISIBLE);
                                setUp.setText(String.valueOf(back_state.getSetsUp()));
                                setDown.setText(String.valueOf(back_state.getSetsDown()));
                            } else {
                                setLayout.setVisibility(View.GONE);
                                setUp.setText("0");
                                setDown.setText("0");
                            }

                            gameUp.setText(String.valueOf(back_state.getSet_game_up(current_set)));
                            gameDown.setText(String.valueOf(back_state.getSet_game_down(current_set)));

                            if (back_state.isServe()) {
                                imgServeUp.setVisibility(View.INVISIBLE);
                                imgServeDown.setVisibility(View.VISIBLE);
                            } else {
                                imgServeUp.setVisibility(View.VISIBLE);
                                imgServeDown.setVisibility(View.INVISIBLE);
                            }

                            if (back_state.isInBreakPoint()) {
                                is_break_point = true;
                            } else {
                                is_break_point = false;
                            }

                            if (!back_state.isInTiebreak()) { //not in tiebreak
                                if (back_state.getSet_point_up(current_set) == 1) {
                                    pointUp.setText(String.valueOf(15));
                                } else if (back_state.getSet_point_up(current_set) == 2) {
                                    pointUp.setText(String.valueOf(30));
                                } else if (back_state.getSet_point_up(current_set) == 3) {
                                    pointUp.setText(String.valueOf(40));
                                } else if (back_state.getSet_point_up(current_set) == 4) {
                                    String msg = "Ad";
                                    pointUp.setText(msg);
                                } else {
                                    pointUp.setText("0");
                                }
                            } else { //tie break;
                                pointUp.setText(String.valueOf(back_state.getSet_point_up(current_set)));
                            }

                            if (!back_state.isInTiebreak()) { //not in tiebreak
                                if (back_state.getSet_point_down(current_set) == 1) {
                                    pointDown.setText(String.valueOf(15));
                                } else if (back_state.getSet_point_down(current_set) == 2) {
                                    pointDown.setText(String.valueOf(30));
                                } else if (back_state.getSet_point_down(current_set) == 3) {
                                    pointDown.setText(String.valueOf(40));
                                } else if (back_state.getSet_point_down(current_set) == 4) {
                                    String msg = "Ad";
                                    pointDown.setText(msg);
                                } else {
                                    pointDown.setText("0");
                                }
                            } else {
                                pointDown.setText(String.valueOf(back_state.getSet_point_down(current_set)));
                            }

                            if (back_state.isSecondServe()) {
                                is_second_serve = true;
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                            } else {
                                is_second_serve = false;
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                            }

                            Log.d(TAG, "########## back state start ##########");
                            Log.d(TAG, "current set : " + back_state.getCurrent_set());
                            Log.d(TAG, "Serve : " + back_state.isServe());
                            Log.d(TAG, "In tiebreak : " + back_state.isInTiebreak());
                            Log.d(TAG, "Finish : " + back_state.isFinish());
                            Log.d(TAG, "Second Serve : " + back_state.isSecondServe());
                            Log.d(TAG, "In break point : " + back_state.isInBreakPoint());

                            int set_limit;
                            switch (set)
                            {
                                case "0":
                                    set_limit = 1;
                                    break;
                                case "1":
                                    set_limit = 3;
                                    break;
                                case "2":
                                    set_limit = 5;
                                    break;
                                default:
                                    set_limit = 1;
                                    break;
                            }


                            for (int i = 1; i <= set_limit; i++) {
                                Log.d(TAG, "================================");
                                Log.d(TAG, "[set " + i + "]");
                                Log.d(TAG, "[Game : " + back_state.getSet_game_up((byte) i) + " / " + back_state.getSet_game_down((byte) i) + "]");
                                Log.d(TAG, "[Point : " + back_state.getSet_point_up((byte) i) + " / " + back_state.getSet_point_down((byte) i) + "]");
                                Log.d(TAG, "[tiebreak : " + back_state.getSet_tiebreak_point_up((byte) i) + " / " + back_state.getSet_tiebreak_point_down((byte) i) + "]");
                            }


                            Log.d(TAG, "########## back state end ##########");

                        } else {
                            Log.d(TAG, "back_state is null");

                            gameUp.setText("0");
                            gameDown.setText("0");

                            imgServeUp.setVisibility(View.INVISIBLE);
                            imgServeDown.setVisibility(View.INVISIBLE);

                            pointUp.setText("0");
                            pointDown.setText("0");

                            imgServeUp.setImageResource(R.drawable.ball_icon);
                            imgServeDown.setImageResource(R.drawable.ball_icon);

                            if (serve.equals("0")) { //you serve first
                                imgServeUp.setVisibility(View.INVISIBLE);
                                imgServeDown.setVisibility(View.VISIBLE);
                            } else {
                                imgServeUp.setVisibility(View.VISIBLE);
                                imgServeDown.setVisibility(View.INVISIBLE);
                            }

                            is_second_serve = false;
                        }
                    } else {
                        Log.d(TAG, "popState null");
                    }
                }
            }
        });

        btnYouScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calculateScore(true);
                if (imgWinCheckUp.getVisibility() == View.VISIBLE || imgWinCheckDown.getVisibility() == View.VISIBLE) {
                    Log.d(TAG, "Game is over!");
                    calculateScore(YOU_SCORE);
                } else {

                    ArrayList<String> items = new ArrayList<>();


                    if (imgServeDown.getVisibility() == View.VISIBLE) { //you serve
                        if (is_second_serve) {
                            items.add(getResources().getString(R.string.game_ace));
                            items.add(getResources().getString(R.string.game_double_faults));
                            items.add(getResources().getString(R.string.game_unforced_error));
                            items.add(getResources().getString(R.string.game_forced_error));
                            items.add(getResources().getString(R.string.game_forehand_winner));
                            items.add(getResources().getString(R.string.game_backhand_winner));
                            items.add(getResources().getString(R.string.game_forehand_volley));
                            items.add(getResources().getString(R.string.game_backhand_volley));
                            items.add(getResources().getString(R.string.game_foul_to_lose));
                            items.add(getResources().getString(R.string.game_other_winner));
                            items.add(getResources().getString(R.string.game_serve_net));
                            items.add(getResources().getString(R.string.game_retire));
                        } else {
                            items.add(getResources().getString(R.string.game_ace));
                            items.add(getResources().getString(R.string.game_second_serve));
                            items.add(getResources().getString(R.string.game_unforced_error));
                            items.add(getResources().getString(R.string.game_forced_error));
                            items.add(getResources().getString(R.string.game_forehand_winner));
                            items.add(getResources().getString(R.string.game_backhand_winner));
                            items.add(getResources().getString(R.string.game_forehand_volley));
                            items.add(getResources().getString(R.string.game_backhand_volley));
                            items.add(getResources().getString(R.string.game_foul_to_lose));
                            items.add(getResources().getString(R.string.game_other_winner));
                            items.add(getResources().getString(R.string.game_serve_net));
                            items.add(getResources().getString(R.string.game_retire));
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_item, items);

                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                        builder.setTitle(playerDown + " " + getResources().getString(R.string.game_select_action));
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) { //pick from gamer
                                if (is_second_serve) { //in second serve
                                    second_serve_count = 1;
                                    if (item == 0) { //ace
                                        ace_count = 1;
                                        second_serve_won = 1;
                                        forehand_winner_count = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 1) { //double fault
                                        //is_double_fault = true;
                                        double_faults_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 2) { //unforced error
                                        unforced_errors_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 3) { //forced error
                                        forced_errors_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 4) { //forehand winner
                                        forehand_winner_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 5) { //backhand winner
                                        backhand_winner_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 6) { //forehand volley
                                        forehand_volley_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 7) { //backhand volley
                                        backhand_volley_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 8) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 9) { //other winner
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 10) { //net in
                                        calculateScore(YOU_SERVE);
                                    } else if (item == 11) { //
                                        is_retire = "2";
                                        calculateScore(YOU_RETIRE);
                                    }
                                } else { //first serve
                                    first_serve_count = 1;
                                    if (item == 0) { //ace
                                        //is_ace = true;
                                        ace_count = 1;
                                        first_serve_won = 1;
                                        forehand_winner_count = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 1) {
                                        Log.d(TAG, "second serve");
                                        first_serve_miss = 1;
                                        is_second_serve = true;
                                        calculateScore(YOU_SERVE);
                                    } else if (item == 2) { //unforced error
                                        unforced_errors_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 3) { //forced error
                                        forced_errors_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 4) { //forehand winner
                                        forehand_winner_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 5) { //backhand winner
                                        backhand_winner_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 6) { //forehand volley
                                        forehand_volley_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 7) { //backhand volley
                                        backhand_volley_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 8) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 9) { //other winner
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 10) { //net in
                                        calculateScore(YOU_SERVE);
                                    } else if (item == 11) { //retire
                                        is_retire = "2";
                                        calculateScore(YOU_RETIRE);
                                    }
                                }
                            }
                        });
                        builder.show();
                    } else { //oppt serve
                        items.add(getResources().getString(R.string.game_unforced_error));
                        items.add(getResources().getString(R.string.game_forced_error));
                        items.add(getResources().getString(R.string.game_forehand_winner));
                        items.add(getResources().getString(R.string.game_backhand_winner));
                        items.add(getResources().getString(R.string.game_forehand_volley));
                        items.add(getResources().getString(R.string.game_backhand_volley));
                        items.add(getResources().getString(R.string.game_foul_to_lose));
                        items.add(getResources().getString(R.string.game_other_winner));
                        items.add(getResources().getString(R.string.game_retire));

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_item, items);

                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                        builder.setTitle(playerDown + " " + getResources().getString(R.string.game_select_action));
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (is_second_serve) {
                                    second_serve_count = 1;
                                    if (item == 0) { //unforced error
                                        unforced_errors_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 1) { //forced error
                                        forced_errors_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 2) { //forehand winner
                                        forehand_winner_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 3) { //backhand winner
                                        backhand_winner_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 4) { //forehand volley
                                        forehand_volley_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 5) { //backhand volley
                                        backhand_volley_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 6) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 7) { //other winner
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 8) { //retire
                                        is_retire = "2";
                                        calculateScore(YOU_RETIRE);
                                    }
                                } else { //first serve
                                    first_serve_count = 1;
                                    if (item == 0) { //unforced error
                                        unforced_errors_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 1) { //forced error
                                        forced_errors_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 2) { //forehand winner
                                        forehand_winner_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 3) { //backhand winner
                                        backhand_winner_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 4) { //forehand volley
                                        forehand_volley_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 5) { //backhand volley
                                        backhand_volley_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 6) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 7) { //other winner
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 8) { //retire
                                        is_retire = "2";
                                        calculateScore(YOU_RETIRE);
                                    }
                                }
                            }
                        });
                        builder.show();
                    }


                }
            }
        });

        btnOpptScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calculateScore(false);
                if (imgWinCheckUp.getVisibility() == View.VISIBLE || imgWinCheckDown.getVisibility() == View.VISIBLE) {
                    Log.d(TAG, "Game is over!");
                    calculateScore(OPPT_SCORE);
                } else {
                    ArrayList<String> items = new ArrayList<>();

                    if (imgServeUp.getVisibility() == View.VISIBLE) { //oppt serve
                        if (is_second_serve) { //second serve
                            items.add(getResources().getString(R.string.game_ace));
                            items.add(getResources().getString(R.string.game_double_faults));
                            items.add(getResources().getString(R.string.game_unforced_error));
                            items.add(getResources().getString(R.string.game_forced_error));
                            items.add(getResources().getString(R.string.game_forehand_winner));
                            items.add(getResources().getString(R.string.game_backhand_winner));
                            items.add(getResources().getString(R.string.game_forehand_volley));
                            items.add(getResources().getString(R.string.game_backhand_volley));
                            items.add(getResources().getString(R.string.game_foul_to_lose));
                            items.add(getResources().getString(R.string.game_other_winner));
                            items.add(getResources().getString(R.string.game_serve_net));
                            items.add(getResources().getString(R.string.game_retire));
                        } else { //first serve
                            items.add(getResources().getString(R.string.game_ace));
                            items.add(getResources().getString(R.string.game_second_serve));
                            items.add(getResources().getString(R.string.game_unforced_error));
                            items.add(getResources().getString(R.string.game_forced_error));
                            items.add(getResources().getString(R.string.game_forehand_winner));
                            items.add(getResources().getString(R.string.game_backhand_winner));
                            items.add(getResources().getString(R.string.game_forehand_volley));
                            items.add(getResources().getString(R.string.game_backhand_volley));
                            items.add(getResources().getString(R.string.game_foul_to_lose));
                            items.add(getResources().getString(R.string.game_other_winner));
                            items.add(getResources().getString(R.string.game_serve_net));
                            items.add(getResources().getString(R.string.game_retire));
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_item, items);

                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                        builder.setTitle(playerUp + " " + getResources().getString(R.string.game_select_action));
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) { //pick from gamer

                                if (is_second_serve) { //in second serve
                                    second_serve_count = 1;
                                    if (item == 0) { //ace
                                        ace_count = 1;
                                        second_serve_won = 1;
                                        forehand_winner_count = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 1) { //double fault
                                        //is_double_fault = true;
                                        double_faults_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 2) { //unforced error
                                        unforced_errors_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 3) { //forced error
                                        forced_errors_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 4) { //forehand winner
                                        forehand_winner_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 5) { //backhand winner
                                        backhand_winner_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 6) { //forehand volley
                                        forehand_volley_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 7) { //backhand volley
                                        backhand_volley_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 8) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 9) { //other winner
                                        second_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 10) { //net in
                                        calculateScore(OPPT_SERVE);
                                    } else if (item == 11) { //retire
                                        is_retire = "1";
                                        calculateScore(OPPT_RETIRE);
                                    }
                                } else { //first serve
                                    first_serve_count = 1;
                                    if (item == 0) { //ace
                                        //is_ace = true;
                                        ace_count = 1;
                                        first_serve_won = 1;
                                        forehand_winner_count = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 1) {
                                        Log.d(TAG, "second serve");
                                        first_serve_miss = 1;
                                        is_second_serve = true;
                                        calculateScore(OPPT_SERVE);
                                    } else if (item == 2) { //unforced error
                                        unforced_errors_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 3) { //forced error
                                        forced_errors_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 4) { //forehand winner
                                        forehand_winner_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 5) { //backhand winner
                                        backhand_winner_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 6) { //forehand volley
                                        forehand_volley_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 7) { //backhand volley
                                        backhand_volley_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 8) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 9) { //other winner
                                        first_serve_won = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 10) { //net in
                                        calculateScore(OPPT_SERVE);
                                    } else if (item == 11) { //retire
                                        is_retire = "1";
                                        calculateScore(OPPT_RETIRE);
                                    }
                                }
                            }
                        });
                        builder.show();
                    } else { //you serve
                        items.add(getResources().getString(R.string.game_unforced_error));
                        items.add(getResources().getString(R.string.game_forced_error));
                        items.add(getResources().getString(R.string.game_forehand_winner));
                        items.add(getResources().getString(R.string.game_backhand_winner));
                        items.add(getResources().getString(R.string.game_forehand_volley));
                        items.add(getResources().getString(R.string.game_backhand_volley));
                        items.add(getResources().getString(R.string.game_foul_to_lose));
                        items.add(getResources().getString(R.string.game_other_winner));
                        items.add(getResources().getString(R.string.game_retire));

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(GameActivity.this, android.R.layout.select_dialog_item, items);

                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                        builder.setTitle(playerUp + " " + getResources().getString(R.string.game_select_action));
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (is_second_serve) {
                                    second_serve_count = 1;
                                    if (item == 0) { //unforced error
                                        unforced_errors_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 1) { //forced error
                                        forced_errors_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 2) { //forehand winner
                                        forehand_winner_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 3) { //backhand winner
                                        backhand_winner_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 4) { //forehand volley
                                        forehand_volley_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 5) { //backhand volley
                                        backhand_volley_count = 1;
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 6) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        second_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 7) { //other winner
                                        second_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 8) { //other winner
                                        is_retire = "1";
                                        calculateScore(OPPT_RETIRE);
                                    }
                                } else {
                                    first_serve_count = 1;
                                    if (item == 0) { //unforced error
                                        unforced_errors_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 1) { //forced error
                                        forced_errors_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 2) { //forehand winner
                                        forehand_winner_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 3) { //backhand winner
                                        backhand_winner_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 4) { //forehand volley
                                        forehand_volley_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 5) { //backhand volley
                                        backhand_volley_count = 1;
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 6) { //Foul to lose
                                        foul_to_lose_count = 1;
                                        first_serve_won = 1;
                                        calculateScore(YOU_SCORE);
                                    } else if (item == 7) { //other winner
                                        first_serve_lost = 1;
                                        calculateScore(OPPT_SCORE);
                                    } else if (item == 8) { //other winner
                                        is_retire = "1";
                                        calculateScore(OPPT_RETIRE);
                                    }
                                }

                            }
                        });
                        builder.show();
                    }

                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_use = 0;
                stack.clear();
                handler.removeCallbacks(updateTimer);

                clear_record(filename);

                Intent intent = new Intent(GameActivity.this, SetupMain.class);
                intent.putExtra("FILE_NAME", filename);
                intent.putExtra("PLAYER_UP", playerUp);
                intent.putExtra("PLAYER_DOWN", playerDown);
                intent.putExtra("SETUP_SERVE", serve);
                //playerUp = intent.getStringExtra("PLAYER_UP");
                //playerDown = intent.getStringExtra("PLAYER_DOWN");
                startActivity(intent);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadDialog = new ProgressDialog(GameActivity.this);
                loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                loadDialog.setTitle(getResources().getString(R.string.game_saving));
                loadDialog.setIndeterminate(false);
                loadDialog.setCancelable(false);

                loadDialog.show();
                //clear



                new Thread() {
                    public void run() {
                        clear_record(filename);

                        boolean is_tiebreak;
                        boolean is_deuce;
                        boolean is_firstserve;

                        switch (tiebreak) {
                            case "0":
                                is_tiebreak = true;
                                break;
                            default:
                                is_tiebreak = false;
                                break;
                        }

                        switch (deuce) {
                            case "0":
                                is_deuce = true;
                                break;
                            default:
                                is_deuce = false;
                                break;
                        }

                        switch (serve) {
                            case "0":
                                is_firstserve = true;
                                break;
                            default:
                                is_firstserve = false;
                                break;
                        }

                        String msg = playerUp + ";" + playerDown + ";" + is_tiebreak + ";" + is_deuce + ";" + is_firstserve + ";" + set + ";" + is_retire+ ";" + games + "|";
                        append_record(msg, filename);

                        com.seventhmoon.tennisscoreboard.Data.State top = stack.peek();
                        if (top != null) {
                            top.setDuration(time_use);

                            int i = 0;
                            //load stack
                            for (com.seventhmoon.tennisscoreboard.Data.State s : stack) {

                                if (i >= 1) {
                                    append_record("&", filename);
                                }


                                String append_msg = s.getCurrent_set() + ";"
                                        + s.isServe() + ";"
                                        + s.isInTiebreak() + ";"
                                        + s.isFinish() + ";"
                                        + s.isSecondServe() + ";"
                                        + s.isInBreakPoint() + ";"
                                        + s.getSetsUp() + ";"
                                        + s.getSetsDown() + ";"
                                        + s.getDuration() + ";"
                                        + s.getAceCountUp() + ";"
                                        + s.getAceCountDown() + ";"
                                        + s.getFirstServeUp() + ";"
                                        + s.getFirstServeDown() + ";"
                                        + s.getFirstServeMissUp() + ";"
                                        + s.getFirstServeMissDown() + ";"
                                        + s.getSecondServeUp() + ";"
                                        + s.getSecondServeDown() + ";"
                                        + s.getBreakPointUp() + ";"
                                        + s.getBreakPointDown() + ";"
                                        + s.getBreakPointMissUp() + ";"
                                        + s.getBreakPointMissDown() + ";"
                                        + s.getFirstServeWonUp() + ";"
                                        + s.getFirstServeWonDown() + ";"
                                        + s.getFirstServeLostUp() + ";"
                                        + s.getFirstServeLostDown() + ";"
                                        + s.getSecondServeWonUp() + ";"
                                        + s.getSecondServeWonDown() + ";"
                                        + s.getSecondServeLostUp() + ";"
                                        + s.getSecondServeLostDown() + ";"
                                        + s.getDoubleFaultUp() + ";"
                                        + s.getDoubleFaultDown() + ";"
                                        + s.getUnforceErrorUp() + ";"
                                        + s.getUnforceErrorDown() + ";"
                                        + s.getForehandWinnerUp() + ";"
                                        + s.getForehandWinnerDown() + ";"
                                        + s.getBackhandWinnerUp() + ";"
                                        + s.getBackhandWinnerDown() + ";"
                                        + s.getForehandVolleyUp() + ";"
                                        + s.getForehandVolleyDown() + ";"
                                        + s.getBackhandVolleyUp() + ";"
                                        + s.getBackhandVolleyDown() + ";"
                                        + s.getFoulToLoseUp() + ";"
                                        + s.getFoulToLoseDown() + ";"
                                        + s.getSet_game_up((byte) 0x1) + ";"
                                        + s.getSet_game_down((byte) 0x1) + ";"
                                        + s.getSet_point_up((byte) 0x1) + ";"
                                        + s.getSet_point_down((byte) 0x1) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x1) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x1) + ";"
                                        + s.getSet_game_up((byte) 0x2) + ";"
                                        + s.getSet_game_down((byte) 0x2) + ";"
                                        + s.getSet_point_up((byte) 0x2) + ";"
                                        + s.getSet_point_down((byte) 0x2) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x2) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x2) + ";"
                                        + s.getSet_game_up((byte) 0x3) + ";"
                                        + s.getSet_game_down((byte) 0x3) + ";"
                                        + s.getSet_point_up((byte) 0x3) + ";"
                                        + s.getSet_point_down((byte) 0x3) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x3) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x3) + ";"
                                        + s.getSet_game_up((byte) 0x4) + ";"
                                        + s.getSet_game_down((byte) 0x4) + ";"
                                        + s.getSet_point_up((byte) 0x4) + ";"
                                        + s.getSet_point_down((byte) 0x4) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x4) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x4) + ";"
                                        + s.getSet_game_up((byte) 0x5) + ";"
                                        + s.getSet_game_down((byte) 0x5) + ";"
                                        + s.getSet_point_up((byte) 0x5) + ";"
                                        + s.getSet_point_down((byte) 0x5) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x5) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x5) + ";"
                                        + s.getForceErrorUp() + ";"
                                        + s.getForceErrorDown() + ";"
                                        ;
                                append_record(append_msg, filename);
                                i++;
                            }
                        } else {
                            Log.d(TAG, "Top null");
                        }

                        Intent intent = new Intent(Constants.ACTION.GAME_SAVE_COMPLETE);
                        sendBroadcast(intent);
                    }
                }.start();


                //loadDialog.dismiss();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, LoadGame.class);
                intent.putExtra("CALL_ACTIVITY", "Game");
                intent.putExtra("PREVIOUS_FILENAME", filename);
                startActivity(intent);
                finish();
            }
        });

        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_pause) {
                    is_pause = true;
                    imgPlayOrPause.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    handler.removeCallbacks(updateTimer);
                } else {
                    is_pause = false;
                    imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);
                }
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GAME_SAVE_COMPLETE)) {
                    Log.d(TAG, "receive GAME_SAVE_COMPLETE !");

                    loadDialog.dismiss();

                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.SAVE_CURRENT_STATE_COMPLETE)) {
                    Log.d(TAG, "receive SAVE_CURRENT_STATE_COMPLETE !");

                    loadDialog.dismiss();
                    finish();

                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.GAME_SAVE_COMPLETE);
            filter.addAction(Constants.ACTION.SAVE_CURRENT_STATE_COMPLETE);
            context.registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        int current_voice_select = pref.getInt("VOICE_SELECT", 0);

        switch (current_voice_select) {
            case 0:
                current_voice_type = GBR_MAN;
                break;
            case 1:
                current_voice_type = GBR_WOMAN;
                break;
            case 2:
                current_voice_type = USER_RECORD;
                break;
            default:
                current_voice_type = GBR_MAN;
                break;
        }

        Log.e(TAG, "current voice = "+current_voice_type);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        time_use = 0;
        stack.clear();
        handler.removeCallbacks(updateTimer);

        if (isRegister && mReceiver != null) {
            try {
                context.unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
        }

        if (loadDialog != null) {
            loadDialog.dismiss();
            loadDialog = null;
        }

        if (voicePlay != null) {
            voicePlay.doExit();
            voicePlay = null;
        }



        super.onDestroy();

    }

    private void calculateScore(StateAction action) {
        byte current_set;
        State new_state=null;
        //load top state first
        final State current_state = stack.peek();

        int set_limit;
        switch (set)
        {
            case "0":
                set_limit = 1;
                break;
            case "1":
                set_limit = 3;
                break;
            case "2":
                set_limit = 5;
                break;
            default:
                set_limit = 1;
                break;
        }

        if (current_state != null) {
            current_set = current_state.getCurrent_set();
            Log.d(TAG, "########## current state start ##########");
            Log.d(TAG, "default:");
            Log.d(TAG, "set = " + set);
            Log.d(TAG, "games = " + games);
            Log.d(TAG, "tiebreak = " + tiebreak);
            Log.d(TAG, "deuce = " + deuce);
            Log.d(TAG, "serve = " + serve);
            Log.d(TAG, "second serve = "+ is_second_serve);

            Log.d(TAG, "Ace : up = "+current_state.getAceCountUp()+" down = "+current_state.getAceCountDown());
            Log.d(TAG, "First serve miss/count : up = "+current_state.getFirstServeMissUp()+"/"+current_state.getFirstServeUp()+
                    " down = "+current_state.getFirstServeMissDown()+"/"+current_state.getFirstServeDown());
            Log.d(TAG, "Second serve miss/count : up = "+current_state.getDoubleFaultUp()+"/"+current_state.getSecondServeUp()+
                    " down = "+current_state.getDoubleFaultDown()+"/"+current_state.getSecondServeDown());
            Log.d(TAG, "======================");
            Log.d(TAG, "Unforced Error : up = "+current_state.getUnforceErrorUp()+ " down = "+current_state.getUnforceErrorDown());
            Log.d(TAG, "Forehand winner : up = "+current_state.getForehandWinnerUp()+ " down = "+current_state.getForehandWinnerDown());
            Log.d(TAG, "Backhand winner : up = "+current_state.getBackhandWinnerUp()+ " down = "+current_state.getBackhandWinnerDown());
            Log.d(TAG, "Forehand Volley : up = "+current_state.getForehandVolleyUp()+ " down = "+current_state.getForehandVolleyDown());
            Log.d(TAG, "Backhand Volley : up = "+current_state.getBackhandVolleyUp()+ " down = "+current_state.getBackhandVolleyDown());
            Log.d(TAG, "Foul to lose : up = "+current_state.getFoulToLoseUp()+ " down = "+current_state.getFoulToLoseDown());

            Log.d(TAG, "current set : " + current_state.getCurrent_set());
            Log.d(TAG, "Serve : " + current_state.isServe());
            Log.d(TAG, "In tiebreak : " + current_state.isInTiebreak());
            Log.d(TAG, "Finish : " + current_state.isFinish());

            //Log.d(TAG, "set 1:");
            Log.d(TAG, "Game : " + current_state.getSet_game_up(current_set) + " / " + current_state.getSet_game_down(current_set));
            Log.d(TAG, "Point : " + current_state.getSet_point_up(current_set) + " / " + current_state.getSet_point_down(current_set));
            Log.d(TAG, "tiebreak : " + current_state.getSet_tiebreak_point_up(current_set) + " / " + current_state.getSet_tiebreak_point_down(current_set));
            Log.d(TAG, "########## current state end ##########");

            if (current_state.isFinish()) {
                Log.d(TAG, "*** Game is Over ***");
                //handler.removeCallbacks(updateTimer);
                //

                AlertDialog.Builder confirmdialog = new AlertDialog.Builder(GameActivity.this);
                confirmdialog.setTitle(getResources().getString(R.string.game_show_result_dalog));
                confirmdialog.setIcon(R.drawable.ball_icon);
                //confirmdialog.setMessage(request_split[0]+" "+getResources().getString(R.string.macauto_chat_dialog_want_to)+" "+request_file);
                confirmdialog.setCancelable(false);
                confirmdialog.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                        intent.putExtra("SET1_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x01)));
                        intent.putExtra("SET1_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x01)));
                        intent.putExtra("SET2_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x02)));
                        intent.putExtra("SET2_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x02)));
                        intent.putExtra("SET3_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x03)));
                        intent.putExtra("SET3_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x03)));
                        intent.putExtra("SET4_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x04)));
                        intent.putExtra("SET4_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x04)));
                        intent.putExtra("SET5_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x05)));
                        intent.putExtra("SET5_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x05)));

                        intent.putExtra("SET1_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x01)));
                        intent.putExtra("SET1_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x01)));
                        intent.putExtra("SET2_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x02)));
                        intent.putExtra("SET2_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x02)));
                        intent.putExtra("SET3_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x03)));
                        intent.putExtra("SET3_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x03)));
                        intent.putExtra("SET4_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x04)));
                        intent.putExtra("SET4_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x04)));
                        intent.putExtra("SET5_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x05)));
                        intent.putExtra("SET5_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x05)));

                        intent.putExtra("GAME_DURATION", String.valueOf(String.valueOf(time_use)));

                        intent.putExtra("PLAYER_UP", playerUp);
                        intent.putExtra("PLAYER_DOWN", playerDown);
                        if (imgWinCheckUp.getVisibility() == View.VISIBLE
                                && imgWinCheckDown.getVisibility() == View.GONE) {
                            intent.putExtra("WIN_PLAYER", playerUp);
                            intent.putExtra("LOSE_PLAYER", playerDown);
                        } else {
                            intent.putExtra("WIN_PLAYER", playerDown);
                            intent.putExtra("LOSE_PLAYER", playerUp);
                        }

                        /*if (current_state.getSetsUp() > current_state.getSetsDown()) {
                            intent.putExtra("WIN_PLAYER", playerUp);
                            intent.putExtra("LOSE_PLAYER", playerDown);
                        } else {
                            intent.putExtra("WIN_PLAYER", playerDown);
                            intent.putExtra("LOSE_PLAYER", playerUp);
                        }*/

                        startActivity(intent);

                    }
                });
                confirmdialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                confirmdialog.show();


            } else { //not finish
                if (is_pause) { //
                    is_pause = false;
                    imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);
                }

                Log.d(TAG, "*** Game is running ***");
                new_state = new State();

                int first, first_miss, second;
                //Log.d(TAG, "==>[Stack empty]");
                Log.d(TAG, "first_serve_count = "+first_serve_count);
                Log.d(TAG, "first_serve_miss = "+first_serve_miss);
                Log.d(TAG, "second_serve_count = "+second_serve_count);
                Log.d(TAG, "ace_count = "+ace_count);
                Log.d(TAG, "unforced_errors_count = "+unforced_errors_count);
                Log.d(TAG, "forced_errors_count = "+forced_errors_count);
                Log.d(TAG, "foul_to_lose_count = "+foul_to_lose_count);
                Log.d(TAG, "double_faults_count = "+double_faults_count);
                Log.d(TAG, "forehand_winner_count = "+forehand_winner_count);
                Log.d(TAG, "backhand_winner_count = "+backhand_winner_count);
                Log.d(TAG, "forehand_volley_count = "+forehand_volley_count);
                Log.d(TAG, "backhand_volley_count = "+backhand_volley_count);

                Log.d(TAG, "first_serve_won = "+first_serve_won);
                Log.d(TAG, "first_serve_lost = "+first_serve_lost);
                Log.d(TAG, "second_serve_won = "+second_serve_won);
                Log.d(TAG, "second_serve_lost = "+second_serve_lost);

                switch (action) {
                    case YOU_RETIRE:
                        Log.d(TAG, "=== I retire start ===");
                        if (stack.isEmpty()) { //the state stack is empty

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);

                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setDuration(time_use);
                            new_state.setFinish(true);
                            handler.removeCallbacks(updateTimer);
                            is_pause = false;
                        } else {
                            Log.d(TAG, "==>[Stack not empty]");
                            first = current_state.getFirstServeDown()+first_serve_count;
                            first_miss = current_state.getFirstServeMissDown()+first_serve_miss;
                            second = current_state.getSecondServeDown()+second_serve_count;


                            new_state.setFirstServeDown((short) first);
                            new_state.setFirstServeMissDown((short) first_miss);
                            new_state.setSecondServeDown((short) second);

                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(true);
                            handler.removeCallbacks(updateTimer);
                            is_pause = false;
                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }


                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            new_state.setDuration(time_use);
                            new_state.setFinish(true);
                            handler.removeCallbacks(updateTimer);
                            is_pause = false;

                            imgWinCheckUp.setVisibility(View.VISIBLE);
                            imgWinCheckDown.setVisibility(View.GONE);

                            new_state.setAceCountUp(current_state.getAceCountUp());
                            new_state.setAceCountDown(current_state.getAceCountDown());
                            new_state.setFirstServeUp(current_state.getFirstServeUp());
                            //new_state.setFirstServeDown(current_state.getFirstServeDown());
                            new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                            //new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                            new_state.setSecondServeUp(current_state.getSecondServeUp());
                            //new_state.setSecondServeDown(current_state.getSecondServeDown());
                            new_state.setBreakPointUp(current_state.getBreakPointUp());
                            new_state.setBreakPointMissUp(current_state.getBreakPointMissUp());
                            new_state.setBreakPointDown(current_state.getBreakPointDown());
                            new_state.setBreakPointMissDown(current_state.getBreakPointMissDown());
                            new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                            new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                            new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                            new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());
                            new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                            new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                            new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                            new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());
                            new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                            new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                            new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                            new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                            new_state.setForceErrorUp(current_state.getForceErrorUp());
                            new_state.setForceErrorDown(current_state.getForceErrorDown());
                            new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                            new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                            new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                            new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                            new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                            new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                            new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                            new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                            new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                            new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }

                            //Log.d(TAG, "your first serve : miss/count = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                            //.d(TAG, "your second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());
                        }
                        Log.d(TAG, "=== I retire end ===");
                        break;
                    case OPPT_RETIRE:
                        Log.d(TAG, "=== oppt retire start ===");
                        if (stack.isEmpty()) { //the state stack is empty

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);

                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setDuration(time_use);
                            new_state.setFinish(true);
                            handler.removeCallbacks(updateTimer);
                            is_pause = false;
                        } else {
                            Log.d(TAG, "==>[Stack not empty]");
                            first = current_state.getFirstServeUp()+first_serve_count;
                            first_miss = current_state.getFirstServeMissUp()+first_serve_miss;
                            second = current_state.getSecondServeUp()+second_serve_count;

                            new_state.setFirstServeUp((short) first);
                            new_state.setFirstServeMissUp((short) first_miss);
                            new_state.setSecondServeUp((short) second);

                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }

                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            new_state.setDuration(time_use);
                            new_state.setFinish(true);
                            handler.removeCallbacks(updateTimer);
                            is_pause = false;

                            imgWinCheckUp.setVisibility(View.GONE);
                            imgWinCheckDown.setVisibility(View.VISIBLE);

                            new_state.setAceCountUp(current_state.getAceCountUp());
                            new_state.setAceCountDown(current_state.getAceCountDown());
                            //new_state.setFirstServeUp(current_state.getFirstServeUp());
                            new_state.setFirstServeDown(current_state.getFirstServeDown());
                            //new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                            new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                            //new_state.setSecondServeUp(current_state.getSecondServeUp());
                            new_state.setSecondServeDown(current_state.getSecondServeDown());
                            new_state.setBreakPointUp(current_state.getBreakPointUp());
                            new_state.setBreakPointMissUp(current_state.getBreakPointMissUp());
                            new_state.setBreakPointDown(current_state.getBreakPointDown());
                            new_state.setBreakPointMissDown(current_state.getBreakPointMissDown());
                            new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                            new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                            new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                            new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());
                            new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                            new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                            new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                            new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());
                            new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                            new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                            new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                            new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                            new_state.setForceErrorUp(current_state.getForceErrorUp());
                            new_state.setForceErrorDown(current_state.getForceErrorDown());
                            new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                            new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                            new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                            new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                            new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                            new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                            new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                            new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                            new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                            new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }
                        }
                        Log.d(TAG, "=== oppt retire end ===");
                        break;
                    case YOU_SERVE: //you serve
                        Log.d(TAG, "=== I serve start ===");
                        if (stack.isEmpty()) { //the state stack is empty
                            first = first_serve_count;
                            first_miss = first_serve_miss;
                            second = second_serve_count;

                            new_state.setFirstServeDown((short) first);
                            new_state.setFirstServeMissDown((short) first_miss);
                            new_state.setSecondServeDown((short) second);

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);

                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }

                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setDuration(time_use);

                            new_state.setFirstServeDown(first_serve_count);
                            new_state.setFirstServeMissDown(first_serve_miss);
                        } else {
                            Log.d(TAG, "==>[Stack not empty]");
                            first = current_state.getFirstServeDown()+first_serve_count;
                            first_miss = current_state.getFirstServeMissDown()+first_serve_miss;
                            second = current_state.getSecondServeDown()+second_serve_count;


                            new_state.setFirstServeDown((short) first);
                            new_state.setFirstServeMissDown((short) first_miss);
                            new_state.setSecondServeDown((short) second);

                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }


                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            new_state.setDuration(time_use);

                            new_state.setAceCountUp(current_state.getAceCountUp());
                            new_state.setAceCountDown(current_state.getAceCountDown());
                            new_state.setFirstServeUp(current_state.getFirstServeUp());
                            //new_state.setFirstServeDown(current_state.getFirstServeDown());
                            new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                            //new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                            new_state.setSecondServeUp(current_state.getSecondServeUp());
                            //new_state.setSecondServeDown(current_state.getSecondServeDown());
                            new_state.setBreakPointUp(current_state.getBreakPointUp());
                            new_state.setBreakPointMissUp(current_state.getBreakPointMissUp());
                            new_state.setBreakPointDown(current_state.getBreakPointDown());
                            new_state.setBreakPointMissDown(current_state.getBreakPointMissDown());
                            new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                            new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                            new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                            new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());
                            new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                            new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                            new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                            new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());
                            new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                            new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                            new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                            new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                            new_state.setForceErrorUp(current_state.getForceErrorUp());
                            new_state.setForceErrorDown(current_state.getForceErrorDown());
                            new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                            new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                            new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                            new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                            new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                            new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                            new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                            new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                            new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                            new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }

                            Log.d(TAG, "your first serve : miss/count = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                            Log.d(TAG, "your second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());
                        }

                        Log.d(TAG, "=== I serve end ===");
                        break;
                    case OPPT_SERVE: //oppt serve
                        Log.d(TAG, "=== oppt serve start ===");
                        if (stack.isEmpty()) { //the state stack is empty
                            Log.d(TAG, "==>[Stack empty]");
                            first = first_serve_count;
                            first_miss = first_serve_miss;
                            second = second_serve_count;

                            new_state.setFirstServeUp((short) first);
                            new_state.setFirstServeMissUp((short) first_miss);
                            new_state.setSecondServeUp((short) second);

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);
                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }

                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setDuration(time_use);

                            new_state.setFirstServeUp(first_serve_count);
                            new_state.setFirstServeMissUp(first_serve_miss);
                        } else {
                            Log.d(TAG, "==>[Stack not empty]");
                            first = current_state.getFirstServeUp()+first_serve_count;
                            first_miss = current_state.getFirstServeMissUp()+first_serve_miss;
                            second = current_state.getSecondServeUp()+second_serve_count;

                            new_state.setFirstServeUp((short) first);
                            new_state.setFirstServeMissUp((short) first_miss);
                            new_state.setSecondServeUp((short) second);

                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            if (is_second_serve) {
                                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                                imgServeDown.setImageResource(R.drawable.ball_icon_red);
                                new_state.setSecondServe(true);
                            } else {
                                imgServeUp.setImageResource(R.drawable.ball_icon);
                                imgServeDown.setImageResource(R.drawable.ball_icon);
                                new_state.setSecondServe(false);
                            }

                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            new_state.setDuration(time_use);

                            new_state.setAceCountUp(current_state.getAceCountUp());
                            new_state.setAceCountDown(current_state.getAceCountDown());
                            //new_state.setFirstServeUp(current_state.getFirstServeUp());
                            new_state.setFirstServeDown(current_state.getFirstServeDown());
                            //new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                            new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                            //new_state.setSecondServeUp(current_state.getSecondServeUp());
                            new_state.setSecondServeDown(current_state.getSecondServeDown());
                            new_state.setBreakPointUp(current_state.getBreakPointUp());
                            new_state.setBreakPointMissUp(current_state.getBreakPointMissUp());
                            new_state.setBreakPointDown(current_state.getBreakPointDown());
                            new_state.setBreakPointMissDown(current_state.getBreakPointMissDown());
                            new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                            new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                            new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                            new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());
                            new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                            new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                            new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                            new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());
                            new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                            new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                            new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                            new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                            new_state.setForceErrorUp(current_state.getForceErrorUp());
                            new_state.setForceErrorDown(current_state.getForceErrorDown());
                            new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                            new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                            new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                            new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                            new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                            new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                            new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                            new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                            new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                            new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }

                            Log.d(TAG, "oppt first serve : miss/count = "+new_state.getFirstServeMissUp()+"/"+new_state.getFirstServeUp());
                            Log.d(TAG, "oppt second serve : miss/count = "+new_state.getDoubleFaultUp()+"/"+new_state.getSecondServeUp());
                        }

                        Log.d(TAG, "=== oppt serve end ===");
                        break;
                    case YOU_SCORE: //you score
                        Log.d(TAG, "=== I score start ===");

                        if (stack.isEmpty()) { //the state stack is empty
                            Log.d(TAG, "==>[Stack empty]");

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);
                            if (is_second_serve)
                                new_state.setSecondServe(true);
                            else
                                new_state.setSecondServe(false);

                            first = first_serve_count;
                            if (new_state.isServe()) { //you serve
                                Log.d(TAG, "you serve");
                                new_state.setFirstServeDown((short) first);
                            } else {
                                Log.d(TAG, "oppt serve");
                                new_state.setFirstServeUp((short) first);
                            }



                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setSet_point_down((byte) 0x01, (byte) 0x01);

                            new_state.setDuration(time_use);

                            //win by your self
                            new_state.setAceCountDown(ace_count);
                            new_state.setForehandWinnerDown(forehand_winner_count);
                            new_state.setBackhandWinnerDown(backhand_winner_count);
                            new_state.setForehandVolleyDown(forehand_volley_count);
                            new_state.setBackhandVolleyDown(backhand_volley_count);
                            //oppt lose
                            new_state.setDoubleFaultUp(double_faults_count);
                            new_state.setUnforceErrorUp(unforced_errors_count);
                            new_state.setForceErrorUp(forced_errors_count);
                            new_state.setFoulToLoseUp(foul_to_lose_count);

                            if (new_state.isServe()) //you serve
                                new_state.setFirstServeWonDown(first_serve_won);
                            else //oppt serve
                                new_state.setFirstServeLostUp(first_serve_lost);

                            checkPoint(new_state);

                            checkGames(new_state);
                        } else {
                            Log.d(TAG, "==>[Stack not empty]");

                            if (current_state.isFinish()) {
                                Log.d(TAG, "**** Game Finish ****");
                            } else {
                                new_state.setCurrent_set(current_state.getCurrent_set());
                                new_state.setServe(current_state.isServe());
                                new_state.setInTiebreak(current_state.isInTiebreak());
                                new_state.setFinish(current_state.isFinish());
                                if (is_second_serve)
                                    new_state.setSecondServe(true);
                                else
                                    new_state.setSecondServe(false);

                                new_state.setSetsUp(current_state.getSetsUp());
                                new_state.setSetsDown(current_state.getSetsDown());

                                new_state.setDuration(time_use);

                                new_state.setAceCountUp(current_state.getAceCountUp());
                                new_state.setAceCountDown(current_state.getAceCountDown());
                                new_state.setFirstServeUp(current_state.getFirstServeUp());
                                new_state.setFirstServeDown(current_state.getFirstServeDown());
                                new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                                new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                                new_state.setSecondServeUp(current_state.getSecondServeUp());
                                new_state.setSecondServeDown(current_state.getSecondServeDown());
                                new_state.setBreakPointUp(current_state.getBreakPointUp());
                                new_state.setBreakPointMissUp(current_state.getBreakPointMissUp());
                                new_state.setBreakPointDown(current_state.getBreakPointDown());
                                new_state.setBreakPointMissDown(current_state.getBreakPointMissDown());
                                new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                                new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                                new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                                new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());
                                new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                                new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                                new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                                new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());
                                new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                                new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                                new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                                new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                                new_state.setForceErrorUp(current_state.getForceErrorUp());
                                new_state.setForceErrorDown(current_state.getForceErrorDown());
                                new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                                new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                                new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                                new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                                new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                                new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                                new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                                new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                                new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                                new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                                for (byte i=1; i<=set_limit; i++) {
                                    new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                    new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                    new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                    new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                    new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                    new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                                }

                                if (current_state.isServe()) { //you serve
                                    Log.d(TAG, "you serve");
                                    first = current_state.getFirstServeDown()+first_serve_count;
                                    first_miss = current_state.getFirstServeMissDown()+first_serve_miss;
                                    second = current_state.getSecondServeDown()+second_serve_count;

                                    new_state.setFirstServeDown((short) first);
                                    new_state.setFirstServeMissDown((short) first_miss);
                                    new_state.setSecondServeDown((short) second);
                                    //win on your own
                                    new_state.setAceCountDown((byte)(new_state.getAceCountDown()+ace_count));
                                    new_state.setForehandWinnerDown((short)(new_state.getForehandWinnerDown()+forehand_winner_count));
                                    new_state.setBackhandWinnerDown((short)(new_state.getBackhandWinnerDown()+backhand_winner_count));
                                    new_state.setForehandVolleyDown((short)(new_state.getForehandVolleyDown()+forehand_volley_count));
                                    new_state.setBackhandVolleyDown((short)(new_state.getBackhandVolleyDown()+backhand_volley_count));
                                    //win on oppt lose
                                    new_state.setUnforceErrorUp((short)(new_state.getUnforceErrorUp()+unforced_errors_count));
                                    new_state.setForceErrorUp((short)(new_state.getForceErrorUp()+forced_errors_count));
                                    new_state.setFoulToLoseUp((byte)(new_state.getFoulToLoseUp()+foul_to_lose_count));
                                    //score on first serve or second serve
                                    if (is_second_serve)
                                        new_state.setSecondServeWonDown((short)(new_state.getSecondServeWonDown()+second_serve_won));
                                    else //first serve
                                        new_state.setFirstServeWonDown((short)(new_state.getFirstServeWonDown()+first_serve_won));

                                } else {
                                    Log.d(TAG, "oppt serve");
                                    first = current_state.getFirstServeUp()+first_serve_count;
                                    first_miss = current_state.getFirstServeMissUp()+first_serve_miss;
                                    second = current_state.getSecondServeUp()+second_serve_count;

                                    new_state.setFirstServeUp((short) first);
                                    new_state.setFirstServeMissUp((short) first_miss);
                                    new_state.setSecondServeUp((short) second);

                                    //win on your own
                                    new_state.setForehandWinnerDown((short)(new_state.getForehandWinnerDown()+forehand_winner_count));
                                    new_state.setBackhandWinnerDown((short)(new_state.getBackhandWinnerDown()+backhand_winner_count));
                                    new_state.setForehandVolleyDown((short)(new_state.getForehandVolleyDown()+forehand_volley_count));
                                    new_state.setBackhandVolleyDown((short)(new_state.getBackhandVolleyDown()+backhand_volley_count));
                                    //win on oppt lose
                                    new_state.setDoubleFaultUp((byte)(new_state.getDoubleFaultUp()+double_faults_count));
                                    new_state.setUnforceErrorUp((short)(new_state.getUnforceErrorUp()+unforced_errors_count));
                                    new_state.setForceErrorUp((short)(new_state.getForceErrorUp()+forced_errors_count));
                                    new_state.setFoulToLoseUp((byte)(new_state.getFoulToLoseUp()+foul_to_lose_count));
                                    if (is_second_serve)
                                        new_state.setSecondServeLostUp((short)(new_state.getSecondServeLostUp()+second_serve_lost));
                                    else //first serve
                                        new_state.setFirstServeLostUp((short)(new_state.getFirstServeLostUp()+first_serve_lost));
                                }

                                Log.d(TAG, "your first serve : miss/count = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                                Log.d(TAG, "your second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());
                                Log.d(TAG, "oppt first serve : miss/count = "+new_state.getFirstServeMissUp()+"/"+new_state.getSecondServeUp());
                                Log.d(TAG, "oppt second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());

                                Log.d(TAG, "your first serve : lost/won = "+new_state.getFirstServeLostDown()+"/"+new_state.getFirstServeWonDown());
                                Log.d(TAG, "your second serve : lost/won = "+new_state.getSecondServeLostDown()+"/"+new_state.getSecondServeWonDown());
                                Log.d(TAG, "oppt first serve : lost/won = "+new_state.getFirstServeLostUp()+"/"+new_state.getFirstServeWonUp());
                                Log.d(TAG, "oppt second serve : lost/won = "+new_state.getSecondServeLostUp()+"/"+new_state.getSecondServeWonUp());

                                //you score!
                                byte point = current_state.getSet_point_down(current_set);
                                Log.d(TAG, "Your point " + point + " change to " + (++point));
                                new_state.setSet_point_down(current_set, point);

                                checkPoint(new_state);

                                checkGames(new_state);
                            }
                        }

                        //scored, reset serve
                        imgServeUp.setImageResource(R.drawable.ball_icon);
                        imgServeDown.setImageResource(R.drawable.ball_icon);
                        is_second_serve = false;
                        Log.d(TAG, "=== I score end ===");
                        break;
                    case OPPT_SCORE: //oppt score
                        Log.d(TAG, "=== Oppt score start ===");

                        if (stack.isEmpty()) { //the state stack is empty
                            Log.d(TAG, "==>[Stack empty]");

                            if (serve.equals("0"))
                                new_state.setServe(true);
                            else
                                new_state.setServe(false);
                            if (is_second_serve)
                                new_state.setSecondServe(true);
                            else
                                new_state.setSecondServe(false);

                            first = first_serve_count;
                            if (new_state.isServe()) { //you serve
                                Log.d(TAG, "you serve");
                                new_state.setFirstServeDown((short) first);
                            } else {
                                Log.d(TAG, "oppt serve");
                                new_state.setFirstServeUp((short) first);
                            }

                            //set current set = 1
                            new_state.setCurrent_set((byte) 0x01);

                            new_state.setSet_point_up((byte) 0x01, (byte) 0x01);

                            new_state.setDuration(time_use);

                            //oppt win on his own
                            new_state.setAceCountUp(ace_count);
                            new_state.setForehandWinnerUp(forehand_winner_count);
                            new_state.setBackhandWinnerUp(backhand_winner_count);
                            new_state.setForehandVolleyUp(forehand_volley_count);
                            new_state.setBackhandVolleyUp(backhand_volley_count);

                            //win by your lose
                            new_state.setDoubleFaultDown(double_faults_count);
                            new_state.setUnforceErrorDown(unforced_errors_count);
                            new_state.setForceErrorDown(forced_errors_count);
                            new_state.setFoulToLoseDown(foul_to_lose_count);

                            if (new_state.isServe()) //you serve
                                new_state.setFirstServeLostDown(first_serve_lost);
                            else //oppt serve
                                new_state.setFirstServeWonUp(first_serve_won);

                            checkPoint(new_state);

                            checkGames(new_state);
                        } else {
                            Log.d(TAG, "==>[Stack not empty]");
                            if (current_state.isFinish()) {
                                Log.d(TAG, "**** Game Finish ****");
                            } else {
                                new_state.setCurrent_set(current_state.getCurrent_set());
                                new_state.setServe(current_state.isServe());
                                new_state.setInTiebreak(current_state.isInTiebreak());
                                new_state.setFinish(current_state.isFinish());
                                if (is_second_serve)
                                    new_state.setSecondServe(true);
                                else
                                    new_state.setSecondServe(false);

                                new_state.setSetsUp(current_state.getSetsUp());
                                new_state.setSetsDown(current_state.getSetsDown());

                                new_state.setDuration(time_use);

                                new_state.setAceCountUp(current_state.getAceCountUp());
                                new_state.setAceCountDown(current_state.getAceCountDown());
                                new_state.setFirstServeUp(current_state.getFirstServeUp());
                                new_state.setFirstServeDown(current_state.getFirstServeDown());
                                new_state.setFirstServeMissUp(current_state.getFirstServeMissUp());
                                new_state.setFirstServeMissDown(current_state.getFirstServeMissDown());
                                new_state.setSecondServeUp(current_state.getSecondServeUp());
                                new_state.setSecondServeDown(current_state.getSecondServeDown());
                                new_state.setBreakPointUp(current_state.getBreakPointUp());
                                new_state.setBreakPointMissUp(current_state.getBreakPointMissUp());
                                new_state.setBreakPointDown(current_state.getBreakPointDown());
                                new_state.setBreakPointMissDown(current_state.getBreakPointMissDown());
                                new_state.setFirstServeWonUp(current_state.getFirstServeWonUp());
                                new_state.setFirstServeWonDown(current_state.getFirstServeWonDown());
                                new_state.setFirstServeLostUp(current_state.getFirstServeLostUp());
                                new_state.setFirstServeLostDown(current_state.getFirstServeLostDown());
                                new_state.setSecondServeWonUp(current_state.getSecondServeWonUp());
                                new_state.setSecondServeWonDown(current_state.getSecondServeWonDown());
                                new_state.setSecondServeLostUp(current_state.getSecondServeLostUp());
                                new_state.setSecondServeLostDown(current_state.getSecondServeLostDown());
                                new_state.setDoubleFaultUp(current_state.getDoubleFaultUp());
                                new_state.setDoubleFaultDown(current_state.getDoubleFaultDown());
                                new_state.setUnforceErrorUp(current_state.getUnforceErrorUp());
                                new_state.setUnforceErrorDown(current_state.getUnforceErrorDown());
                                new_state.setForceErrorUp(current_state.getForceErrorUp());
                                new_state.setForceErrorDown(current_state.getForceErrorDown());
                                new_state.setForehandWinnerUp(current_state.getForehandWinnerUp());
                                new_state.setForehandWinnerDown(current_state.getForehandWinnerDown());
                                new_state.setBackhandWinnerUp(current_state.getBackhandWinnerUp());
                                new_state.setBackhandWinnerDown(current_state.getBackhandWinnerDown());
                                new_state.setForehandVolleyUp(current_state.getForehandVolleyUp());
                                new_state.setForehandVolleyDown(current_state.getForehandVolleyDown());
                                new_state.setBackhandVolleyUp(current_state.getBackhandVolleyUp());
                                new_state.setBackhandVolleyDown(current_state.getBackhandVolleyDown());
                                new_state.setFoulToLoseUp(current_state.getFoulToLoseUp());
                                new_state.setFoulToLoseDown(current_state.getFoulToLoseDown());

                                for (byte i=1; i<=set_limit; i++) {
                                    new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                    new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                    new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                    new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                    new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                    new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                                }

                                if (current_state.isServe()) { //you serve
                                    Log.d(TAG, "you serve");
                                    first = current_state.getFirstServeDown()+first_serve_count;
                                    first_miss = current_state.getFirstServeMissDown()+first_serve_miss;
                                    second = current_state.getSecondServeDown()+second_serve_count;

                                    new_state.setFirstServeDown((short) first);
                                    new_state.setFirstServeMissDown((short) first_miss);
                                    new_state.setSecondServeDown((short) second);

                                    //win on oppt own
                                    new_state.setForehandWinnerUp((short)(new_state.getForehandWinnerUp()+forehand_winner_count));
                                    new_state.setBackhandWinnerUp((short)(new_state.getBackhandWinnerUp()+backhand_winner_count));
                                    new_state.setForehandVolleyUp((short)(new_state.getForehandVolleyUp()+forehand_volley_count));
                                    new_state.setBackhandVolleyUp((short)(new_state.getBackhandVolleyUp()+backhand_volley_count));
                                    //win on your lose
                                    new_state.setDoubleFaultDown((byte)(new_state.getDoubleFaultDown()+double_faults_count));
                                    new_state.setUnforceErrorDown((short) (new_state.getUnforceErrorDown()+unforced_errors_count));
                                    new_state.setForceErrorDown((short)(new_state.getForceErrorDown()+forced_errors_count));
                                    new_state.setFoulToLoseDown((byte)(new_state.getFoulToLoseDown()+foul_to_lose_count));

                                    //you serve, oppt scored
                                    if (is_second_serve) {
                                        new_state.setSecondServeLostDown((short)(new_state.getSecondServeLostDown()+second_serve_lost));
                                    } else {
                                        new_state.setFirstServeLostDown((short)(new_state.getFirstServeLostDown()+first_serve_lost));
                                    }
                                } else {
                                    Log.d(TAG, "oppt serve");
                                    first = current_state.getFirstServeUp()+first_serve_count;
                                    first_miss = current_state.getFirstServeMissUp()+first_serve_miss;
                                    second = current_state.getSecondServeUp()+second_serve_count;

                                    new_state.setFirstServeUp((short) first);
                                    new_state.setFirstServeMissUp((short) first_miss);
                                    new_state.setSecondServeUp((short) second);

                                    //win on oppt own
                                    new_state.setAceCountUp((byte)(new_state.getAceCountUp()+ace_count));
                                    new_state.setForehandWinnerUp((short)(new_state.getForehandWinnerUp()+forehand_winner_count));
                                    new_state.setBackhandWinnerUp((short)(new_state.getBackhandWinnerUp()+backhand_winner_count));
                                    new_state.setForehandVolleyUp((short)(new_state.getForehandVolleyUp()+forehand_volley_count));
                                    new_state.setBackhandVolleyUp((short)(new_state.getBackhandVolleyUp()+backhand_volley_count));
                                    //win on your lose
                                    new_state.setUnforceErrorDown((short)(new_state.getUnforceErrorDown()+unforced_errors_count));
                                    new_state.setForceErrorDown((short)(new_state.getForceErrorDown()+forced_errors_count));
                                    new_state.setFoulToLoseDown((byte)(new_state.getFoulToLoseDown()+foul_to_lose_count));

                                    //oppt serve, oppt scored
                                    if (is_second_serve) {
                                        new_state.setSecondServeWonUp((short)(new_state.getSecondServeWonUp()+second_serve_won));
                                    } else {
                                        new_state.setFirstServeWonUp((short)(new_state.getFirstServeWonUp()+first_serve_won));
                                    }
                                }

                                Log.d(TAG, "your first serve : miss/count = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                                Log.d(TAG, "your second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());
                                Log.d(TAG, "oppt first serve : miss/count = "+new_state.getFirstServeMissUp()+"/"+new_state.getSecondServeUp());
                                Log.d(TAG, "oppt second serve : miss/count = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());

                                Log.d(TAG, "your first serve : lost/won = "+new_state.getFirstServeLostDown()+"/"+new_state.getFirstServeWonDown());
                                Log.d(TAG, "your second serve : lost/won = "+new_state.getSecondServeLostDown()+"/"+new_state.getSecondServeWonDown());
                                Log.d(TAG, "oppt first serve : lost/won = "+new_state.getFirstServeLostUp()+"/"+new_state.getFirstServeWonUp());
                                Log.d(TAG, "oppt second serve : lost/won = "+new_state.getSecondServeLostUp()+"/"+new_state.getSecondServeWonUp());

                                //oppt score!
                                byte point = current_state.getSet_point_up(current_set);
                                Log.d(TAG, "Opponent point " + point + " change to " + (++point));
                                new_state.setSet_point_up(current_set, point);

                                checkPoint(new_state);

                                checkGames(new_state);
                            }
                        }

                        //scored, reset serve
                        imgServeUp.setImageResource(R.drawable.ball_icon);
                        imgServeDown.setImageResource(R.drawable.ball_icon);
                        is_second_serve = false;
                        Log.d(TAG, "=== Oppt score end ===");
                        break;
                } //switch end

                if (new_state != null) {

                    Log.d(TAG, "########## new state start ##########");
                    Log.d(TAG, "current set : " + new_state.getCurrent_set());
                    Log.d(TAG, "Serve : " + new_state.isServe());
                    Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                    Log.d(TAG, "Finish : " + new_state.isFinish());
                    Log.d(TAG, "Second serve : "+new_state.isSecondServe());
                    Log.d(TAG, "Ace : up = "+new_state.getAceCountUp()+" down = "+new_state.getAceCountDown());
                    Log.d(TAG, "Double Faults : up  = "+new_state.getDoubleFaultUp()+ " down = "+new_state.getDoubleFaultDown());
                    Log.d(TAG, "First serve miss/count : up = "+new_state.getFirstServeMissUp()+"/"+new_state.getFirstServeUp()+
                            " down = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                    Log.d(TAG, "Second serve miss/count : up = "+new_state.getDoubleFaultUp()+"/"+new_state.getSecondServeUp()+
                            " down = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());

                    Log.d(TAG, "First serve lost/won : up = "+new_state.getFirstServeLostUp()+"/"+new_state.getFirstServeWonUp()+" down = "
                            +new_state.getFirstServeLostDown()+"/"+new_state.getFirstServeWonDown());
                    Log.d(TAG, "Second serve lost/won : up = "+new_state.getSecondServeLostUp()+"/"+new_state.getSecondServeWonUp()+" down = "
                            +new_state.getSecondServeLostDown()+"/"+new_state.getSecondServeWonDown());

                    Log.d(TAG, "======================");

                    Log.d(TAG, "Unforced Error : up = "+new_state.getUnforceErrorUp()+ " down = "+new_state.getUnforceErrorDown());
                    Log.d(TAG, "Forehand winner : up = "+new_state.getForehandWinnerUp()+ " down = "+new_state.getForehandWinnerDown());
                    Log.d(TAG, "Backhand winner : up = "+new_state.getBackhandWinnerUp()+ " down = "+new_state.getBackhandWinnerDown());
                    Log.d(TAG, "Forehand Volley : up = "+new_state.getForehandVolleyUp()+ " down = "+new_state.getForehandVolleyDown());
                    Log.d(TAG, "Backhand Volley : up = "+new_state.getBackhandVolleyUp()+ " down = "+new_state.getBackhandVolleyDown());
                    Log.d(TAG, "Foul to lose : up = "+new_state.getFoulToLoseUp()+ " down = "+new_state.getFoulToLoseDown());
                    //Log.d(TAG, "deuce : " + new_state.isDeuce());
                    //Log.d(TAG, "set Limit : " + new_state.getSetLimit());
                    Log.d(TAG, "Set up : " + new_state.getSetsUp());
                    Log.d(TAG, "Set down : " + new_state.getSetsDown());

                    Log.d(TAG, "Duration : " + new_state.getDuration());

                    for (int i = 1; i <= set_limit; i++) {
                        Log.d(TAG, "================================");
                        Log.d(TAG, "[set " + i + "]");
                        Log.d(TAG, "[Game : " + new_state.getSet_game_up((byte) i) + " / " + new_state.getSet_game_down((byte) i) + "]");
                        Log.d(TAG, "[Point : " + new_state.getSet_point_up((byte) i) + " / " + new_state.getSet_point_down((byte) i) + "]");
                        Log.d(TAG, "[tiebreak : " + new_state.getSet_tiebreak_point_up((byte) i) + " / " + new_state.getSet_tiebreak_point_down((byte) i) + "]");
                    }

                    Log.d(TAG, "########## new state end ##########");

                    //then look up top state
                    //State new_current_state = stack.peek();
                    current_set = new_state.getCurrent_set();

                    if (new_state.getSetsUp() > 0 || new_state.getSetsDown() > 0) {
                        setLayout.setVisibility(View.VISIBLE);
                        setUp.setText(String.valueOf(new_state.getSetsUp()));
                        setDown.setText(String.valueOf(new_state.getSetsDown()));
                    } else {
                        setLayout.setVisibility(View.GONE);
                        setUp.setText("0");
                        setDown.setText("0");
                    }

                    gameUp.setText(String.valueOf(new_state.getSet_game_up(current_set)));
                    gameDown.setText(String.valueOf(new_state.getSet_game_down(current_set)));

                    if (new_state.isFinish()) {
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);

                        if (action == YOU_RETIRE) {
                            imgWinCheckUp.setVisibility(View.VISIBLE);
                            imgWinCheckDown.setVisibility(View.GONE);
                        } else if (action == OPPT_RETIRE){
                            imgWinCheckUp.setVisibility(View.GONE);
                            imgWinCheckDown.setVisibility(View.VISIBLE);
                        } else {
                            if (new_state.getSetsUp() > new_state.getSetsDown()) {
                                imgWinCheckUp.setVisibility(View.VISIBLE);
                                imgWinCheckDown.setVisibility(View.GONE);
                            } else {
                                imgWinCheckUp.setVisibility(View.GONE);
                                imgWinCheckDown.setVisibility(View.VISIBLE);
                            }
                        }


                    } else {
                        if (new_state.isServe()) {
                            imgServeUp.setVisibility(View.INVISIBLE);
                            imgServeDown.setVisibility(View.VISIBLE);
                        } else {
                            imgServeUp.setVisibility(View.VISIBLE);
                            imgServeDown.setVisibility(View.INVISIBLE);
                        }
                    }

                    if (!new_state.isInTiebreak()) { //not in tiebreak
                        if (new_state.getSet_point_up(current_set) == 1) {
                            pointUp.setText(String.valueOf(15));
                        } else if (new_state.getSet_point_up(current_set) == 2) {
                            pointUp.setText(String.valueOf(30));
                        } else if (new_state.getSet_point_up(current_set) == 3) {
                            pointUp.setText(String.valueOf(40));
                        } else if (new_state.getSet_point_up(current_set) == 4) {
                            //String msg = String.valueOf(40)+"A";
                            String msg = "Ad";
                            pointUp.setText(msg);
                        } else {
                            pointUp.setText("0");
                        }
                    } else { //tie break;
                        pointUp.setText(String.valueOf(new_state.getSet_point_up(current_set)));
                    }

                    if (!new_state.isInTiebreak()) { //not in tiebreak
                        if (new_state.getSet_point_down(current_set) == 1) {
                            pointDown.setText(String.valueOf(15));
                        } else if (new_state.getSet_point_down(current_set) == 2) {
                            pointDown.setText(String.valueOf(30));
                        } else if (new_state.getSet_point_down(current_set) == 3) {
                            pointDown.setText(String.valueOf(40));
                        } else if (new_state.getSet_point_down(current_set) == 4) {
                            //String msg = String.valueOf(40)+"A";
                            String msg = "Ad";
                            pointDown.setText(msg);
                        } else {
                            pointDown.setText("0");
                        }
                    } else {
                        pointDown.setText(String.valueOf(new_state.getSet_point_down(current_set)));
                    }

                    //push into stack
                    stack.push(new_state);

            /*Log.d(TAG, "@@@@@@ stack @@@@@@");
            for (State s : stack) {
                Log.d(TAG, "current set : " + s.getCurrent_set());
                Log.d(TAG, "Serve : " + s.isServe());
                Log.d(TAG, "In tiebreak : " + s.isInTiebreak());
                Log.d(TAG, "Finish : " + s.isFinish());

                for (int i = 1; i <= set_limit; i++) {

                    Log.d(TAG, "[set " + i + "]");
                    Log.d(TAG, "[Game : " + s.getSet_game_up((byte) i) + " / " + s.getSet_game_down((byte) i) + "]");
                    Log.d(TAG, "[Point : " + s.getSet_point_up((byte) i) + " / " + s.getSet_point_down((byte) i) + "]");
                    Log.d(TAG, "[tiebreak : " + s.getSet_tiebreak_point_up((byte) i) + " / " + s.getSet_tiebreak_point_down((byte) i) + "]");
                }
                Log.d(TAG, "================================");
            }
            Log.d(TAG, "@@@@@@ stack @@@@@@");*/
                }
            } //not finish end
        } else { //current_state null
            Log.d(TAG, "current_state not null ==>[Stack empty]");
            if (is_pause) { //
                is_pause = false;
                imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                handler.removeCallbacks(updateTimer);
                handler.postDelayed(updateTimer, 1000);
            }
            Log.d(TAG, "*** Game is running ***");

            new_state = new State();

            Log.d(TAG, "first_serve_count = "+first_serve_count);
            Log.d(TAG, "first_serve_miss = "+first_serve_miss);
            Log.d(TAG, "second_serve_count = "+second_serve_count);

            Log.d(TAG, "first_serve_won = "+first_serve_won);
            Log.d(TAG, "first_serve_lost = "+first_serve_lost);
            Log.d(TAG, "second_serve_won = "+second_serve_won);
            Log.d(TAG, "second_serve_lost = "+second_serve_lost);

            if (serve.equals("0"))
                new_state.setServe(true);
            else
                new_state.setServe(false);


            if (is_second_serve) {
                new_state.setSecondServe(true);
                imgServeUp.setImageResource(R.drawable.ball_icon_red);
                imgServeDown.setImageResource(R.drawable.ball_icon_red);
            } else {
                new_state.setSecondServe(false);
                imgServeUp.setImageResource(R.drawable.ball_icon);
                imgServeDown.setImageResource(R.drawable.ball_icon);
            }
            //set current set = 1
            new_state.setCurrent_set((byte) 0x01);

            new_state.setDuration(time_use);

            switch (action) {
                case YOU_RETIRE:
                    Log.d(TAG, "=== I retire start ===");
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;

                    imgWinCheckUp.setVisibility(View.VISIBLE);
                    imgWinCheckDown.setVisibility(View.GONE);
                    Log.d(TAG, "=== I retire end ===");
                    break;
                case OPPT_RETIRE:
                    Log.d(TAG, "=== oppt retire start ===");
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;

                    imgWinCheckUp.setVisibility(View.GONE);
                    imgWinCheckDown.setVisibility(View.VISIBLE);
                    Log.d(TAG, "=== oppt retire end ===");
                    break;
                case YOU_SERVE: //you serve
                    Log.d(TAG, "=== I serve start ===");
                    new_state.setFirstServeDown(first_serve_count);
                    new_state.setFirstServeMissDown(first_serve_miss);
                    first_serve_count = 0;
                    first_serve_miss = 0;
                    //new_state.setSecondServeDown(second_serve_count);
                    Log.d(TAG, "=== I serve end ===");
                    break;
                case OPPT_SERVE: //oppt serve
                    Log.d(TAG, "=== oppt serve start ===");
                    new_state.setFirstServeUp(first_serve_count);
                    new_state.setFirstServeMissUp(first_serve_miss);
                    first_serve_count = 0;
                    first_serve_miss = 0;
                    //new_state.setSecondServeUp(second_serve_count);
                    Log.d(TAG, "=== oppt serve end ===");
                    break;
                case YOU_SCORE: //you score
                    Log.d(TAG, "=== I score start ===");
                    new_state.setCurrent_set((byte) 0x01);

                    new_state.setDuration(time_use);
                    if (new_state.isServe()) //you serve
                    {
                        Log.d(TAG, "you serve");
                        new_state.setFirstServeDown(first_serve_count);
                        new_state.setFirstServeMissDown(first_serve_miss);
                        new_state.setSecondServeDown(second_serve_count);

                        //win on your own
                        new_state.setAceCountDown(ace_count);
                        new_state.setForehandWinnerDown(forehand_winner_count);
                        new_state.setBackhandWinnerDown(backhand_winner_count);
                        new_state.setForehandVolleyDown(forehand_volley_count);
                        new_state.setBackhandVolleyDown(backhand_volley_count);
                        //win on oppt lose
                        new_state.setUnforceErrorUp(unforced_errors_count);
                        new_state.setForceErrorUp(forced_errors_count);
                        new_state.setFoulToLoseUp(foul_to_lose_count);

                        new_state.setFirstServeWonDown(first_serve_won);
                        new_state.setFirstServeLostDown(first_serve_lost);
                        new_state.setSecondServeWonDown(second_serve_won);
                        new_state.setSecondServeLostDown(second_serve_lost);

                    } else { //oppt serve
                        Log.d(TAG, "oppt serve");
                        new_state.setFirstServeUp(first_serve_count);
                        new_state.setFirstServeMissUp(first_serve_miss);
                        new_state.setSecondServeUp(second_serve_count);

                        //win on your own
                        new_state.setForehandWinnerDown(forehand_winner_count);
                        new_state.setBackhandWinnerDown(backhand_winner_count);
                        new_state.setForehandVolleyDown(forehand_volley_count);
                        new_state.setBackhandVolleyDown(backhand_volley_count);
                        //win on oppt lose
                        new_state.setDoubleFaultUp(double_faults_count);
                        new_state.setUnforceErrorUp(unforced_errors_count);
                        new_state.setForceErrorUp(forced_errors_count);
                        new_state.setFoulToLoseUp(foul_to_lose_count);

                        new_state.setFirstServeWonUp(first_serve_won);
                        new_state.setFirstServeLostUp(first_serve_lost);
                        new_state.setSecondServeWonUp(second_serve_won);
                        new_state.setSecondServeLostUp(second_serve_lost);
                    }

                    new_state.setSet_point_down((byte) 0x01, (byte) 0x01);

                    checkPoint(new_state);

                    checkGames(new_state);

                    Log.d(TAG, "=== I score end ===");
                    break;
                case OPPT_SCORE: //oppt score
                    Log.d(TAG, "=== Oppt score start ===");

                    if (imgServeDown.getVisibility() == View.VISIBLE) //you serve
                    {
                        Log.d(TAG, "you serve");
                        new_state.setFirstServeDown(first_serve_count);
                        new_state.setFirstServeMissDown(first_serve_miss);
                        new_state.setSecondServeDown(second_serve_count);

                        //win on oppt own
                        new_state.setForehandWinnerUp(forehand_winner_count);
                        new_state.setBackhandWinnerUp(backhand_winner_count);
                        new_state.setForehandVolleyUp(forehand_volley_count);
                        new_state.setBackhandVolleyUp(backhand_volley_count);
                        //win on you lose
                        new_state.setDoubleFaultDown(double_faults_count);
                        new_state.setUnforceErrorDown(unforced_errors_count);
                        new_state.setForceErrorDown(forced_errors_count);
                        new_state.setFoulToLoseDown(foul_to_lose_count);

                        new_state.setFirstServeWonDown(first_serve_won);
                        new_state.setFirstServeLostDown(first_serve_lost);
                        new_state.setSecondServeWonDown(second_serve_won);
                        new_state.setSecondServeLostDown(second_serve_lost);

                    } else { //oppt serve
                        Log.d(TAG, "oppt serve");
                        new_state.setFirstServeUp(first_serve_count);
                        new_state.setFirstServeMissUp(first_serve_miss);
                        new_state.setSecondServeUp(second_serve_count);

                        //win on oppt own
                        new_state.setAceCountUp(ace_count);
                        new_state.setForehandWinnerUp(forehand_winner_count);
                        new_state.setBackhandWinnerUp(backhand_winner_count);
                        new_state.setForehandVolleyUp(forehand_volley_count);
                        new_state.setBackhandVolleyUp(backhand_volley_count);
                        //win on you lose
                        new_state.setUnforceErrorDown(unforced_errors_count);
                        new_state.setForceErrorDown(forced_errors_count);
                        new_state.setFoulToLoseDown(foul_to_lose_count);

                        new_state.setFirstServeWonUp(first_serve_won);
                        new_state.setFirstServeLostUp(first_serve_lost);
                        new_state.setSecondServeWonUp(second_serve_won);
                        new_state.setSecondServeLostUp(second_serve_lost);
                    }

                    new_state.setSet_point_up((byte) 0x01, (byte) 0x01);

                    checkPoint(new_state);

                    checkGames(new_state);

                    Log.d(TAG, "=== Oppt score end ===");
                    break;
            }

            if (new_state != null) {

                Log.d(TAG, "########## new state start ##########");
                Log.d(TAG, "current set : " + new_state.getCurrent_set());
                Log.d(TAG, "Serve : " + new_state.isServe());
                Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                Log.d(TAG, "Finish : " + new_state.isFinish());
                Log.d(TAG, "Second Serve : " + new_state.isSecondServe());
                //Log.d(TAG, "deuce : " + new_state.isDeuce());
                //Log.d(TAG, "Set Limit : "+ new_state.getSetLimit());
                Log.d(TAG, "Ace : up = "+new_state.getAceCountUp()+" down = "+new_state.getAceCountDown());
                Log.d(TAG, "First serve miss/count : up = "+new_state.getFirstServeMissUp()+"/"+new_state.getFirstServeUp()+
                        " down = "+new_state.getFirstServeMissDown()+"/"+new_state.getFirstServeDown());
                Log.d(TAG, "Second serve miss/count : up = "+new_state.getDoubleFaultUp()+"/"+new_state.getSecondServeUp()+
                        " down = "+new_state.getDoubleFaultDown()+"/"+new_state.getSecondServeDown());

                Log.d(TAG, "First serve lost/won : up = "+new_state.getFirstServeLostUp()+"/"+new_state.getFirstServeWonUp()+" down = "
                        +new_state.getFirstServeLostDown()+"/"+new_state.getFirstServeWonDown());
                Log.d(TAG, "Second serve lost/won : up = "+new_state.getSecondServeLostUp()+"/"+new_state.getSecondServeWonUp()+" down = "
                        +new_state.getSecondServeLostDown()+"/"+new_state.getSecondServeWonDown());
                Log.d(TAG, "======================");
                Log.d(TAG, "Unforced Error : up = "+new_state.getUnforceErrorUp()+ " down = "+new_state.getUnforceErrorDown());
                Log.d(TAG, "Forehand winner : up = "+new_state.getForehandWinnerUp()+ " down = "+new_state.getForehandWinnerDown());
                Log.d(TAG, "Backhand winner : up = "+new_state.getBackhandWinnerUp()+ " down = "+new_state.getBackhandWinnerDown());
                Log.d(TAG, "Forehand Volley : up = "+new_state.getForehandVolleyUp()+ " down = "+new_state.getForehandVolleyDown());
                Log.d(TAG, "Backhand Volley : up = "+new_state.getBackhandVolleyUp()+ " down = "+new_state.getBackhandVolleyDown());
                Log.d(TAG, "Foul to lose : up = "+new_state.getFoulToLoseUp()+ " down = "+new_state.getFoulToLoseDown());

                Log.d(TAG, "Set up : " + new_state.getSetsUp());
                Log.d(TAG, "set down : " + new_state.getSetsDown());

                for (int i = 1; i <= set_limit; i++) {
                    Log.d(TAG, "================================");
                    Log.d(TAG, "[set " + i + "]");
                    Log.d(TAG, "[Game : " + new_state.getSet_game_up((byte) i) + " / " + new_state.getSet_game_down((byte) i) + "]");
                    Log.d(TAG, "[Point : " + new_state.getSet_point_up((byte) i) + " / " + new_state.getSet_point_down((byte) i) + "]");
                    Log.d(TAG, "[tiebreak : " + new_state.getSet_tiebreak_point_up((byte) i) + " / " + new_state.getSet_tiebreak_point_down((byte) i) + "]");
                }

                Log.d(TAG, "########## new state end ##########");

                //then look up top state
                //State new_current_state = stack.peek();
                current_set = new_state.getCurrent_set();

                gameUp.setText(String.valueOf(new_state.getSet_game_up(current_set)));
                gameDown.setText(String.valueOf(new_state.getSet_game_down(current_set)));

                if (new_state.isServe()) {
                    imgServeUp.setVisibility(View.INVISIBLE);
                    imgServeDown.setVisibility(View.VISIBLE);
                } else {
                    imgServeUp.setVisibility(View.VISIBLE);
                    imgServeDown.setVisibility(View.INVISIBLE);
                }

                if (!new_state.isInTiebreak()) { //not in tiebreak
                    if (new_state.getSet_point_up(current_set) == 1) {
                        pointUp.setText(String.valueOf(15));
                    } else if (new_state.getSet_point_up(current_set) == 2) {
                        pointUp.setText(String.valueOf(30));
                    } else if (new_state.getSet_point_up(current_set) == 3) {
                        pointUp.setText(String.valueOf(40));
                    } else if (new_state.getSet_point_up(current_set) == 4) {
                        //String msg = String.valueOf(40)+"A";
                        String msg = "Ad";
                        pointUp.setText(msg);
                    } else {
                        pointUp.setText("0");
                    }
                } else { //tie break;
                    pointUp.setText(String.valueOf(new_state.getSet_point_up(current_set)));
                }

                if (!new_state.isInTiebreak()) { //not in tiebreak
                    if (new_state.getSet_point_down(current_set) == 1) {
                        pointDown.setText(String.valueOf(15));
                    } else if (new_state.getSet_point_down(current_set) == 2) {
                        pointDown.setText(String.valueOf(30));
                    } else if (new_state.getSet_point_down(current_set) == 3) {
                        pointDown.setText(String.valueOf(40));
                    } else if (new_state.getSet_point_down(current_set) == 4) {
                        //String msg = String.valueOf(40)+"A";
                        String msg = "Ad";
                        pointDown.setText(msg);
                    } else {
                        pointDown.setText("0");
                    }
                } else {
                    pointDown.setText(String.valueOf(new_state.getSet_point_down(current_set)));
                }



                //push into stack
                stack.push(new_state);
            }
        }

        //reset all zero
        ace_count = 0;
        double_faults_count = 0;
        forced_errors_count = 0;
        unforced_errors_count = 0;
        forehand_winner_count = 0;
        backhand_winner_count = 0;
        forehand_volley_count = 0;
        backhand_volley_count = 0;
        foul_to_lose_count = 0;
        first_serve_count = 0;
        first_serve_miss = 0;
        second_serve_count = 0;

        first_serve_won = 0;
        first_serve_lost = 0;
        second_serve_won = 0;
        second_serve_lost = 0;
    }

    private void checkPoint(State new_state) {
        Log.d(TAG, "[Check point Start]");
        //Integer call = 0;

        byte current_set = new_state.getCurrent_set();
        if (new_state.isInTiebreak()) { //in tiebreak
            Log.d(TAG, "[In Tiebreak]");
            byte game;

            if (games.equals("0")) { //6 game in a set
                Log.d(TAG, "[6 games in a set]"); //6:6 => tiebreak

                if (new_state.getSet_point_up(current_set) == 7 && new_state.getSet_point_down(current_set) <= 5) {
                    //7 : 0,1,2,3,4,5 => oppt win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) <= 5 && new_state.getSet_point_down(current_set) == 7) {
                    //0,1,2,3,4,5 : 7 => you win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) >= 6 &&
                        new_state.getSet_point_down(current_set) >= 6 &&
                        (new_state.getSet_point_up(current_set) - new_state.getSet_point_down(current_set)) == 2) {
                    //8:6, 9:7, 10:8.... => oppt win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) >= 6 &&
                        new_state.getSet_point_down(current_set) >= 6 &&
                        (new_state.getSet_point_down(current_set) - new_state.getSet_point_up(current_set)) == 2) {
                    //6:8, 7:9, 8:10.... => you win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else {
                    Log.d(TAG, "Other tie break, isServe = "+new_state.isServe());

                    byte plus = (byte) (new_state.getSet_point_up(current_set)+new_state.getSet_point_down(current_set));

                    if (plus%2 == 1) {
                        //change serve
                        Log.d(TAG, "==>Points plus become odd, change serve!");
                        if (new_state.isServe()) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }
                    }

                    is_current_game_over = false;

                    if (new_state.getSet_point_up(current_set) > 99 ||
                            new_state.getSet_point_down(current_set) > 99) { //point > 99, don't play voice
                        toast("The voice will not support while points more than 99");
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                    } else {

                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);
                    }
                }

            } else {
                Log.d(TAG, "[4 games in a set]"); //4:4 => tiebreak

                if (new_state.getSet_point_up(current_set) == 5 && new_state.getSet_point_down(current_set) <= 3) {
                    //7 : 0,1,2,3,4,5 => oppt win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) <= 3 && new_state.getSet_point_down(current_set) == 5) {
                    //0,1,2,3,4,5 : 7 => you win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) >= 4 &&
                        new_state.getSet_point_down(current_set) >= 4 &&
                        (new_state.getSet_point_up(current_set) - new_state.getSet_point_down(current_set)) == 2) {
                    //8:6, 9:7, 10:8.... => oppt win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) >= 4 &&
                        new_state.getSet_point_down(current_set) >= 4 &&
                        (new_state.getSet_point_down(current_set) - new_state.getSet_point_up(current_set)) == 2) {
                    //6:8, 7:9, 8:10.... => you win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else {
                    Log.d(TAG, "Other tie break, isServe = "+new_state.isServe());



                    byte plus = (byte) (new_state.getSet_point_up(current_set)+new_state.getSet_point_down(current_set));

                    if (plus%2 == 1) {
                        //change serve
                        Log.d(TAG, "==>Points plus become odd, change serve!");
                        if (new_state.isServe()) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }
                    }

                    is_current_game_over = false;

                    if (new_state.getSet_point_up(current_set) > 99 ||
                            new_state.getSet_point_down(current_set) > 99) { //point > 99, don't play voice
                        toast("The voice will not support while points more than 99");
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                    } else {

                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);
                    }
                }
            }




        } else { //not in tiebreak;
            Log.d(TAG, "[Not in Tiebreak]");
            if (deuce.equals("0")) { //use deuce
                Log.d(TAG, "[Game Using Deuce]");
                byte game;
                if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) ==4) { //40A:40A => 40:40
                    Log.d(TAG, "40A:40A => 40:40");

                    new_state.setSet_point_up(current_set, (byte)0x03);
                    new_state.setSet_point_down(current_set, (byte)0x03);

                    if (is_break_point) {
                        Log.d(TAG, "In break point");
                        if (new_state.isServe()) { //you serve
                            new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                            new_state.setBreakPointMissUp((byte)(new_state.getBreakPointMissUp()+1));
                        } else { //oppt serve
                            new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                            new_state.setBreakPointMissDown((byte)(new_state.getBreakPointMissDown()+1));
                        }
                    } else {
                        Log.d(TAG, "Not in break point");
                    }

                    is_break_point = false;
                    new_state.setInBreakPoint(false);
                    is_current_game_over = false;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);

                } else if (new_state.getSet_point_up(current_set) == 5 &&
                        new_state.getSet_point_down(current_set) == 3) { //40A+ : 40 => oppt win this game
                    //set point clean
                    Log.d(TAG, "40A+1 : 40, => oppt win this game");
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);

                    if (is_break_point) {
                        if (new_state.isServe()) { //you serve
                            Log.d(TAG, "You serve, oppt got this break point");
                            new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                        } else { //oppt serve
                            Log.d(TAG, "Oppt serve");
                        }
                    }

                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_break_point = false;
                    new_state.setInBreakPoint(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) == 3 &&
                        new_state.getSet_point_down(current_set) == 5) { //40 : 40A+ => you win this game
                    Log.d(TAG, "40 : 40A+ => you win this game");
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);

                    if (is_break_point) {
                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve");
                        } else {
                            Log.d(TAG, "Oppt serve, you got this break point");
                            new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                        }
                    }

                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_break_point = false;
                    new_state.setInBreakPoint(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) <= 2) { //40A : 0, 40A : 15, 40A : 30 => oppt win this game
                    Log.d(TAG, "40A : 0, 40A : 15, 40A : 30 => oppt win this game");
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);

                    if (is_break_point) {
                        if (new_state.isServe()) { //you serve
                            Log.d(TAG, "you serve, oppt got this break point");
                            new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                        } else {
                            Log.d(TAG, "Oppt serve");
                        }
                    }

                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_break_point = false;
                    new_state.setInBreakPoint(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) <=2 &&
                        new_state.getSet_point_down(current_set) == 4) { //0 : 40A, 15 : 40A, 30: 40A => you win this game
                    Log.d(TAG, "0 : 40A, 15 : 40A, 30: 40A => you win this game");
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);

                    if (is_break_point) {
                        if (new_state.isServe()) { //you serve
                            Log.d(TAG, "You serve");
                        } else {
                            Log.d(TAG, "Oppt serve, you got this break point");
                            new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                        }
                    }

                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_break_point = false;
                    new_state.setInBreakPoint(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                }
                else {
                    Log.d(TAG, "[points change without arrange]");
                    if (new_state.getSet_point_up(current_set) == 3 &&
                            new_state.getSet_point_down(current_set) <= 2 && !is_break_point) { // 40:0, 40:15, 40:30

                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve, Not int break point => In break point");
                            is_break_point = true;
                            new_state.setInBreakPoint(true);

                            byte num = new_state.getSet_point_down(current_set);
                            switch (num) {
                                case 0:
                                    toast("3 Break Points");
                                    break;
                                case 1:
                                    toast("2 Break Points");
                                    break;
                                case 2:
                                    toast("Break Point");
                                    break;
                            }

                        } else {
                            Log.d(TAG, "Oppt serve");

                        }
                        is_current_game_over = false;
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);
                    } else if (new_state.getSet_point_up(current_set) <= 2 &&
                            new_state.getSet_point_down(current_set) == 3 && !is_break_point) { // 0:40, 15:40, 30:40

                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve");

                        } else {
                            Log.d(TAG, "Oppt serve, Not int break point => In break point");
                            is_break_point = true;
                            new_state.setInBreakPoint(true);

                            byte num = new_state.getSet_point_up(current_set);
                            switch (num) {
                                case 0:
                                    toast("3 Break Points");
                                    break;
                                case 1:
                                    toast("2 Break Points");
                                    break;
                                case 2:
                                    toast("Break Point");
                                    break;
                            }

                        }
                        is_current_game_over = false;
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);
                    } else if (new_state.getSet_point_up(current_set) == 4 &&
                            new_state.getSet_point_down(current_set) == 3 && !is_break_point) { // 40A:40

                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve, Not int break point => In break point");
                            is_break_point = true;
                            new_state.setInBreakPoint(true);

                            toast("Break Point");

                        } else {
                            Log.d(TAG, "Oppt serve");
                        }
                        is_current_game_over = false;
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);

                    } else if (new_state.getSet_point_up(current_set) == 3 &&
                            new_state.getSet_point_down(current_set) == 4 && !is_break_point) { // 40:40A

                        if (new_state.isServe()) {
                            Log.d(TAG, "You serve");

                        } else {
                            Log.d(TAG, "Oppt serve, Not int break point => In break point");
                            is_break_point = true;
                            new_state.setInBreakPoint(true);

                            toast("Break Point");
                        }
                        is_current_game_over = false;
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);

                    } else if (new_state.getSet_point_up(current_set) == 3 &&
                            new_state.getSet_point_down(current_set) == 3) { //40:40
                        Log.d(TAG, "become deuce ");
                        if (is_break_point) { //in break point
                            if (new_state.isServe()) { //you serve
                                new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                                new_state.setBreakPointMissUp((byte)(new_state.getBreakPointMissUp()+1));
                            } else { //oppt serve
                                new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                                new_state.setBreakPointMissDown((byte)(new_state.getBreakPointMissDown()+1));
                            }
                        } else {
                            Log.d(TAG, "not in break point");
                        }

                        is_break_point = false;
                        new_state.setInBreakPoint(false);
                        is_current_game_over = false;
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);

                    } else { //other point 40:0 => 40:15, 40:15 => 40:30, 0:40=>15:40, 15:40=>30:40
                        if (is_break_point) { //in break point situation
                            Log.d(TAG, "In break point");
                            Log.d(TAG, "40:0 => 40:15, 40:15 => 40:30, 0:40=>15:40, 15:40=>30:40");
                            if (new_state.isServe()) { //you serve
                                new_state.setBreakPointUp((byte)(new_state.getBreakPointUp()+1));
                                new_state.setBreakPointMissUp((byte)(new_state.getBreakPointMissUp()+1));
                                Log.d(TAG, "miss/count ("+new_state.getBreakPointMissUp()+"/"+new_state.getBreakPointMissUp()+")");

                                byte num = new_state.getSet_point_down(current_set);
                                switch (num) {
                                    case 1:
                                        toast("2 Break Points");
                                        break;
                                    case 2:
                                        toast("Break Point");
                                        break;
                                }
                            } else { //oppt serve
                                new_state.setBreakPointDown((byte)(new_state.getBreakPointDown()+1));
                                new_state.setBreakPointMissDown((byte)(new_state.getBreakPointMissDown()+1));
                                Log.d(TAG, "miss/count ("+new_state.getBreakPointMissDown()+"/"+new_state.getBreakPointMissDown()+")");

                                byte num = new_state.getSet_point_up(current_set);
                                switch (num) {
                                    case 1:
                                        toast("2 Break Points");
                                        break;
                                    case 2:
                                        toast("Break Point");
                                        break;
                                }
                            }
                            new_state.setInBreakPoint(true);
                        } else {
                            Log.d(TAG, "Not In break point");

                        }
                        is_current_game_over = false;
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);

                    }
                }
            } else { //use deciding point
                Log.d(TAG, "[Deciding Point start]");
                byte game;
                if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) <= 3) { //40A : 40,30,15,0 => oppt win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) <= 3 &&
                        new_state.getSet_point_down(current_set) == 4) { //40,30,15,0 : 40A => you win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else {
                    Log.d(TAG, "[points change without arrange]");
                    is_current_game_over = false;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);

                }
                Log.d(TAG, "[Deciding Point end]");
            }
        }

        //point change
        new_state.setSecondServe(false);


        Log.d(TAG, "current_set = "+current_set);
        Log.d(TAG, "[Check point End]");
    }

    private void checkGames(State new_state) {
        Log.d(TAG, "[Check Games Start]");
        byte current_set = new_state.getCurrent_set();
        byte setsWinUp = new_state.getSetsUp();
        byte setsWinDown = new_state.getSetsDown();
        //Integer call = 0, all = 0, tiebreak = 0;
        if (tiebreak.equals("0")) { //use tibreak
            Log.d(TAG, "[Use Tiebreak start]");

            if (games.equals("0")) { //6 game in a set
                Log.d(TAG, "[6 game in a set start]");

                if (new_state.getSet_game_up(current_set) == 6 &&
                        new_state.getSet_game_down(current_set) == 6) {
                    new_state.setInTiebreak(true); //into tiebreak;

                    //am I(down) first serve?
                    if (new_state.isServe()) {
                        am_I_Tiebreak_First_Serve = true;
                    } else {
                        am_I_Tiebreak_First_Serve = false;
                    }

                    //add voice

                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    /*switch (current_voice_type) {
                        case GBR_MAN:
                            call = R.raw.gbr_man_6;
                            voiceList.add(call);
                            all = R.raw.gbr_man_all;
                            voiceList.add(all);
                            tiebreak = R.raw.gbr_man_tiebreak;
                            voiceList.add(tiebreak);
                            break;
                    }*/

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }

                } else if (new_state.getSet_game_up(current_set) == 7 &&
                        new_state.getSet_game_down(current_set) == 5) { // 7:5 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);
                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}


                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }

                } else if (new_state.getSet_game_up(current_set) == 5 &&
                        new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);
                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) == 7 &&
                        new_state.getSet_game_down(current_set) == 6) { // 7:6 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);
                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) == 6 &&
                        new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);
                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) == 6 &&
                        new_state.getSet_game_down(current_set) <=4 ) { // 6:0,1,2,3,4 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);
                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) <= 4 &&
                        new_state.getSet_game_down(current_set) == 6) { // 0,1,2,3,4:6 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);
                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else {
                    Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));
                    //add voice
                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                }

                Log.d(TAG, "[6 game in a set end]");
            } else {
                Log.d(TAG, "[4 game in a set start]");

                if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) == 4) {
                    new_state.setInTiebreak(true); //into tiebreak;

                    //am I(down) first serve?
                    if (new_state.isServe()) {
                        am_I_Tiebreak_First_Serve = true;
                    } else {
                        am_I_Tiebreak_First_Serve = false;
                    }

                    //add voice
                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) == 5 &&
                        new_state.getSet_game_down(current_set) == 3) { // 5:3 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) == 3 &&
                        new_state.getSet_game_down(current_set) == 5) { // 3:5 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) == 5 &&
                        new_state.getSet_game_down(current_set) == 4) { // 5:4 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) == 5) { // 4:5 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) <=2 ) { // 4:0,1,2 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) <= 2 &&
                        new_state.getSet_game_down(current_set) == 4) { // 0,1,2:6 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else {
                    Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));
                    //add voice
                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                }
                Log.d(TAG, "[4 game in a set end]");
            }

            Log.d(TAG, "[Use Tiebreak end]");
        } else {
            Log.d(TAG, "[Use deciding point start]");
            if (games.equals("0")) { //6 game in a set
                Log.d(TAG, "[6 game in a set start]");

                if (new_state.getSet_game_up(current_set) == 6 &&
                        new_state.getSet_game_down(current_set) <= 5) { // 6:5 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) <= 5 &&
                        new_state.getSet_game_down(current_set) == 6) { // 5:6 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}

                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else {
                    Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));
                    //add voice
                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                }
                Log.d(TAG, "[6 game in a set end]");
            } else {
                Log.d(TAG, "[4 game in a set start]");

                if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) <= 3) { // 4:3 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}
                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else if (new_state.getSet_game_up(current_set) <= 3 &&
                        new_state.getSet_game_down(current_set) == 4) { // 3:4 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                    //add voice
                    //if (!new_state.isFinish()) { //game is not finish, add game
                    //    if(is_current_game_over)
                    //        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //}
                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                } else {
                    Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));
                    //add voice
                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //play voice
                    if (voiceOn) {
                        if (current_voice_type == USER_RECORD) {
                            voicePlay.audioPlayMultiFile(voiceUserList);
                        } else {
                            voicePlay.audioPlayMulti(voiceList);
                        }
                    }
                }
                Log.d(TAG, "[4 game in a set end]");
            }

            Log.d(TAG, "[Use deciding point end]");
        }


        Log.d(TAG, "[Check Games End]");
    }

    private void checkSets(State new_state) {
        Log.d(TAG, "[Check sets Start]");
        //check if the game is over
        byte current_set = new_state.getCurrent_set();
        byte setsWinUp = new_state.getSetsUp();
        byte setsWinDown = new_state.getSetsDown();

        Integer match=0;
        String fileName;
        Integer gameSet;

        switch (set) {
            case "0":
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;
                    imgPlayOrPause.setVisibility(View.GONE);

                    //voice
                    switch (current_voice_type) {
                        case GBR_MAN:
                            match = R.raw.gbr_man_match;
                            voiceList.add(match);
                            break;
                        case GBR_WOMAN:
                            match = R.raw.gbr_woman_match;
                            voiceList.add(match);
                            break;
                        case USER_RECORD:
                            fileName = "user_match.m4a";
                            voiceUserList.add(fileName);
                            break;

                    }

                    //voiceList.add(match);
                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseSetVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseSetVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }
                } else {
                    Log.d(TAG, "Not finished");
                }
                break;
            case "1":
                if (setsWinUp == 2 || setsWinDown == 2) {
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;
                    imgPlayOrPause.setVisibility(View.GONE);

                    //voice
                    //voice
                    switch (current_voice_type) {
                        case GBR_MAN:
                            match = R.raw.gbr_man_match;
                            voiceList.add(match);
                            break;
                        case GBR_WOMAN:
                            match = R.raw.gbr_woman_match;
                            voiceList.add(match);
                            break;
                        case USER_RECORD:
                            fileName = "user_match.m4a";
                            voiceUserList.add(fileName);
                            break;

                    }
                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseSetVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseSetVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }
                } else { // new set
                    //voice
                    switch (current_set) {
                        case 1:
                            gameSet = R.raw.gbr_man_first_set;
                            voiceList.add(gameSet);

                            break;
                        case 2:
                            gameSet = R.raw.gbr_man_second_set;
                            voiceList.add(gameSet);
                            break;
                    }

                    //and play this set
                    if (new_state.getSet_game_up(current_set) > new_state.getSet_game_down(current_set)) {
                        chooseSetVoice(new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    } else {
                        chooseSetVoice(new_state.getSet_game_down(current_set), new_state.getSet_game_up(current_set));
                    }

                    current_set++;
                    new_state.setCurrent_set(current_set);
                }
                break;
            case "2":
                if (setsWinUp == 3 || setsWinDown == 3) {
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;
                    imgPlayOrPause.setVisibility(View.GONE);

                    //voice
                    //voice
                    switch (current_voice_type) {
                        case GBR_MAN:
                            match = R.raw.gbr_man_match;
                            voiceList.add(match);
                            break;
                        case GBR_WOMAN:
                            match = R.raw.gbr_woman_match;
                            voiceList.add(match);
                            break;
                        case USER_RECORD:
                            fileName = "user_match.m4a";
                            voiceUserList.add(fileName);
                            break;

                    }
                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseSetVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseSetVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }
                } else { // new set
                    current_set++;
                    new_state.setCurrent_set(current_set);
                }
                break;
            default:
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                    handler.removeCallbacks(updateTimer);
                    is_pause = false;
                    imgPlayOrPause.setVisibility(View.GONE);

                    //voice
                    //voice
                    switch (current_voice_type) {
                        case GBR_MAN:
                            match = R.raw.gbr_man_match;
                            voiceList.add(match);
                            break;
                        case GBR_WOMAN:
                            match = R.raw.gbr_woman_match;
                            voiceList.add(match);
                            break;
                        case USER_RECORD:
                            fileName = "user_match.m4a";
                            voiceUserList.add(fileName);
                            break;

                    }

                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseSetVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseSetVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }
                }
                break;
        }



        Log.d(TAG, "[Check sets End]");
    }

    private static int choosePointVoice(boolean down_serve, boolean is_tiebreak, byte up_point, byte down_point) {
        int call = 0;
        int call2 = 0;
        int call3 = 0;
        int call4 = 0;
        int call5 = 0;

        String fileName0;
        String fileName1;
        String fileName2;
        String fileName3;
        String fileName4;


        Log.d(TAG, "<choosePointVoice start>");

        if (is_current_game_over) { //current game over
            Log.d(TAG, "current game is over.");
            switch (current_voice_type) {
                case GBR_MAN:
                    call = R.raw.gbr_man_game;
                    voiceList.add(call);
                    break;
                case GBR_WOMAN:
                    call = R.raw.gbr_woman_game;
                    voiceList.add(call);
                    break;
                case USER_RECORD:
                    fileName0 = "user_game.m4a";
                    voiceUserList.add(fileName0);
                    break;
            }

        } else { //still in game

            if (!is_tiebreak) { //not in tiebreak

                if (up_point == 0 && down_point == 1) { //0:15
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_0;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_15_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_15;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_0_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 0 && down_point == 2) { //0:30
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_0;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_30_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_30;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_0_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 0 && down_point == 3) { //0:40
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_0;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_40_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_40;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_0_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 1 && down_point == 0) { //15:0
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_15;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_0_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_0;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_15_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_0.m4a";
                                voiceUserList.add(fileName0);
                        }
                    }
                } else if (up_point == 1 && down_point == 1) { //15:15
                    switch (current_voice_type) {
                        case GBR_MAN:
                            call = R.raw.gbr_man_15_15;
                            voiceList.add(call);
                            break;
                        case GBR_WOMAN:
                            call = R.raw.gbr_woman_15_15;
                            voiceList.add(call);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_15_15.m4a";
                            voiceUserList.add(fileName0);
                            break;
                    }
                } else if (up_point == 1 && down_point == 2) { //15:30
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_15;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_30_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_30;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_15_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 1 && down_point == 3) { //15:40
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_15;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_40_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_40;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_15_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 2 && down_point == 0) { //30:0
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_30;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_0_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_0;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_30_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 2 && down_point == 1) { //30:15
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_30;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_15_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_15;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_30_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 2 && down_point == 2) { //30:30
                    switch (current_voice_type) {
                        case GBR_MAN:
                            call = R.raw.gbr_man_30_30;
                            voiceList.add(call);
                            break;
                        case GBR_WOMAN:
                            call = R.raw.gbr_woman_30_30;
                            voiceList.add(call);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_30_30.m4a";
                            voiceUserList.add(fileName0);
                            break;
                    }
                } else if (up_point == 2 && down_point == 3) { //30:40
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_30;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_40_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_40;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_30_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 3 && down_point == 0) { //40:0
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_40;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_0_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_0;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_40_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 3 && down_point == 1) { //40:15
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_40;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_15_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_15;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_40_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 3 && down_point == 2) { //40:30
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_40;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_30_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_30;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_40_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 3 && down_point == 3) { //40:40

                    if (deuce.equals("0")) { //use deuce
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_40;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_40_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //use deciding point
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_deciding_point;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_deciding_point;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_deciding_point.m4a";
                                voiceUserList.add(fileName0);
                                break;

                        }
                    }


                } else if (up_point == 3 && down_point == 4) { //40:Ad
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_ad_serve;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_ad_serve;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_ad.m4a";
                                voiceUserList.add(fileName0);
                                fileName1 = "user_player_down.m4a";
                                voiceUserList.add(fileName1);
                                break;

                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_ad_recv;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_ad_recv;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_ad.m4a";
                                voiceUserList.add(fileName0);
                                fileName1 = "user_player_down.m4a";
                                voiceUserList.add(fileName1);
                                break;
                        }
                    }


                } else if (up_point == 4 && down_point == 3) { //Ad:40

                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_ad_recv;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_ad_recv;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_ad.m4a";
                                voiceUserList.add(fileName0);
                                fileName1 = "user_player_up.m4a";
                                voiceUserList.add(fileName1);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_ad_serve;
                                voiceList.add(call);
                                break;
                            case GBR_WOMAN:
                                call = R.raw.gbr_woman_ad_serve;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_ad.m4a";
                                voiceUserList.add(fileName0);
                                fileName1 = "user_player_up.m4a";
                                voiceUserList.add(fileName1);
                                break;
                        }
                    }


                }

                //voiceList.add(call);
            } else { // in tiebreak
                Log.e(TAG, "voice choose in tiebreak==>");

                if (up_point == 0 && down_point <= 6) { //0:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(down_point);
                                voiceList.add(call);

                                call2 = getPointByNumEnd((byte)0); //0
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                fileName1 = "user_to.m4a";
                                voiceUserList.add(fileName1);

                                fileName2 = getPointByNumString((byte)0);
                                voiceUserList.add(fileName2);
                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart((byte)0); //0
                                voiceList.add(call);

                                call2 = getPointByNumEnd(down_point);
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString((byte)0); //0
                                voiceUserList.add(fileName0);

                                fileName1 = "user_to.m4a";
                                voiceUserList.add(fileName1);

                                fileName2 = getPointByNumString(down_point);
                                voiceUserList.add(fileName2);
                                break;
                        }

                    }
                } else if (up_point == 1 && down_point <= 6) { //1:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(down_point);
                                voiceList.add(call);

                                if (down_point == 1)
                                    call2 = getPointByNumEnd((byte)100); //all
                                else
                                    call2 = getPointByNumEnd(up_point); //1
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 1) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2= getPointByNumString(up_point); //1
                                    voiceUserList.add(fileName2);
                                }


                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(up_point); //1
                                voiceList.add(call);

                                if (down_point == 1)  //1:1
                                    call2 = getPointByNumEnd((byte)100); //all
                                else
                                    call2 = getPointByNumEnd(down_point);

                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //1
                                voiceUserList.add(fileName0);

                                if (down_point == 1) { //1:1
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }


                    }
                } else if (up_point == 2 && down_point <= 6) { //2:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(down_point);
                                voiceList.add(call);

                                if (down_point == 2)
                                    call2 = getPointByNumEnd((byte)100); //all
                                else
                                    call2 = getPointByNumEnd(up_point); //2
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 2) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //2
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(up_point); //2
                                voiceList.add(call);

                                if (down_point == 2)  //2:2
                                    call2 = getPointByNumEnd((byte)100); //all
                                else
                                    call2 = getPointByNumEnd(down_point);

                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //2
                                voiceUserList.add(fileName0);

                                if (down_point == 2) {  //2:2
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    }
                } else if (up_point == 3 && down_point <= 6) { //3:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(down_point);
                                voiceList.add(call);

                                if (down_point == 3)
                                    call2 = getPointByNumEnd((byte)100); //all
                                else
                                    call2 = getPointByNumEnd(up_point); //3
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 3) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //3
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(up_point); //3
                                voiceList.add(call);

                                if (down_point == 3) { //3:3
                                    call2 = getPointByNumEnd((byte)100); //all
                                } else {
                                    call2 = getPointByNumEnd(down_point);
                                }
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //3
                                voiceUserList.add(fileName0);

                                if (down_point == 3) { //3:3
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    }
                } else if (up_point == 4 && down_point <= 6) { //4:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(down_point);
                                voiceList.add(call);

                                if (down_point == 4)
                                    call2 = getPointByNumEnd((byte)100); //all
                                else
                                    call2 = getPointByNumEnd(up_point); //4
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 4) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //4
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(up_point); //4
                                voiceList.add(call);

                                if (down_point == 4) { //4:4
                                    call2 = getPointByNumEnd((byte)100); //all
                                } else {
                                    call2 = getPointByNumEnd(down_point);
                                }
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //4
                                voiceUserList.add(fileName0);

                                if (down_point == 4) { //4:4
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    }
                } else if (up_point == 5 && down_point <= 6) { //5:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(down_point);
                                voiceList.add(call);

                                if (down_point == 5)
                                    call2 = getPointByNumEnd((byte)100); //all
                                else
                                    call2 = getPointByNumEnd(up_point); //5
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 5) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //5
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(up_point); //5
                                voiceList.add(call);

                                if (down_point == 5) { //5:5
                                    call2 = getPointByNumEnd((byte)100); //all
                                } else {
                                    call2 = getPointByNumEnd(down_point);
                                }
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //5
                                voiceUserList.add(fileName0);

                                if (down_point == 5) { //5:5
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    }
                } else if (up_point == 6 && down_point <= 6) { //6:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(down_point);
                                voiceList.add(call);

                                if (down_point == 6)
                                    call2 = getPointByNumEnd((byte)100); //all
                                else
                                    call2 = getPointByNumEnd(up_point); //6
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 6) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //6
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }


                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                            case GBR_WOMAN:
                                call = getPointByNumStart(up_point); //6
                                voiceList.add(call);

                                if (down_point == 6) { //6:6
                                    call2 = getPointByNumEnd((byte)100); //all
                                } else {
                                    call2 = getPointByNumEnd(down_point);
                                }
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //6
                                voiceUserList.add(fileName0);

                                if (down_point == 6) { //6:6
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2= getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }


                    }
                } else { //point more than 6
                    Log.e(TAG, "up_point = "+up_point+ ", down_point = "+down_point);

                    if (up_point == down_point) { // x all

                        if (up_point <= 20) {
                            switch (current_voice_type) {
                                case GBR_MAN:
                                case GBR_WOMAN:
                                    call = getPointByNumStart(up_point);
                                    voiceList.add(call);
                                    call2 = getPointByNumEnd((byte)100);
                                    voiceList.add(call2);
                                    break;
                                case USER_RECORD:
                                    fileName0 = getPointByNumString(up_point);
                                    voiceUserList.add(fileName0);
                                    fileName1 = "user_all.m4a";
                                    voiceUserList.add(fileName1);
                                    break;
                            }


                        } else { //up_point > 20
                            switch (current_voice_type) {
                                case GBR_MAN:
                                case GBR_WOMAN:
                                    if (up_point % 10 == 0) { //30, 40, 50, 60, 70, 80, 90
                                        call = getPointByNumStart(up_point);
                                        voiceList.add(call);
                                    } else {
                                        call = getPointByNumStart((byte)(up_point/10*10));
                                        voiceList.add(call);
                                        call2 = getPointByNumStart((byte)(up_point%10));
                                        voiceList.add(call2);
                                    }
                                    call3 = getPointByNumEnd((byte)100);
                                    voiceList.add(call3);
                                    break;
                                case USER_RECORD:
                                    if (up_point % 10 == 0) { //30, 40, 50, 60, 70, 80, 90
                                        fileName0 = getPointByNumString(up_point);
                                        voiceUserList.add(fileName0);
                                    } else {
                                        fileName1 = getPointByNumString((byte)(up_point/10*10));
                                        voiceUserList.add(fileName1);
                                        fileName2 = getPointByNumString((byte)(up_point%10));
                                        voiceUserList.add(fileName2);
                                    }
                                    fileName3 = "user_all.m4a";
                                    voiceUserList.add(fileName3);
                                    break;
                            }
                        }
                    } else {
                        if (up_point <= 20 && down_point <= 20) {
                            if (down_serve) { //you serve
                                switch (current_voice_type) {
                                    case GBR_MAN:
                                    case GBR_WOMAN:
                                        call = getPointByNumStart(down_point);
                                        voiceList.add(call);
                                        call2 = getPointByNumEnd(up_point);
                                        voiceList.add(call2);
                                        break;
                                    case USER_RECORD:
                                        fileName0 = getPointByNumString(down_point);
                                        voiceUserList.add(fileName0);
                                        fileName1 = "user_to.m4a";
                                        voiceUserList.add(fileName1);
                                        fileName2 = getPointByNumString(up_point);
                                        voiceUserList.add(fileName2);
                                        break;
                                }

                            } else { //oppt serve
                                switch (current_voice_type) {
                                    case GBR_MAN:
                                    case GBR_WOMAN:
                                        call = getPointByNumStart(up_point);
                                        voiceList.add(call);
                                        call2 = getPointByNumEnd(down_point);
                                        voiceList.add(call2);
                                        break;
                                    case USER_RECORD:
                                        fileName0 = getPointByNumString(up_point);
                                        voiceUserList.add(fileName0);
                                        fileName1 = "user_to.m4a";
                                        voiceUserList.add(fileName1);
                                        fileName2 = getPointByNumString(down_point);
                                        voiceUserList.add(fileName2);
                                        break;
                                }

                            }
                        } else { //up_point > 20
                            if (down_serve) { //you serve
                                switch (current_voice_type) {
                                    case GBR_MAN:
                                    case GBR_WOMAN:
                                        call = getPointByNumStart((byte)(down_point/10*10));
                                        voiceList.add(call);
                                        if (down_point%10 > 0) {
                                            call2 = getPointByNumStart((byte) (down_point % 10));
                                            voiceList.add(call2);
                                        }


                                        if (up_point%10 > 0) {
                                            call3 = getPointByNumStart((byte)(up_point/10*10));
                                            voiceList.add(call3);
                                            call4 = getPointByNumEnd((byte) (up_point % 10));
                                            voiceList.add(call4);
                                        } else {
                                            call3 = getPointByNumEnd((byte)(up_point/10*10));
                                            voiceList.add(call3);
                                        }
                                        break;
                                    case USER_RECORD:
                                        fileName0 = getPointByNumString((byte)(down_point/10*10));
                                        voiceUserList.add(fileName0);
                                        if (down_point%10 > 0) {
                                            fileName1 = getPointByNumString((byte) (down_point % 10));
                                            voiceUserList.add(fileName1);
                                        }
                                        //to
                                        fileName4 = "user_to.m4a";
                                        voiceUserList.add(fileName4);

                                        fileName2 = getPointByNumString((byte)(up_point/10*10));
                                        voiceUserList.add(fileName2);
                                        if (up_point%10 > 0) {
                                            fileName3 = getPointByNumString((byte) (up_point % 10));
                                            voiceUserList.add(fileName3);
                                        }
                                        break;
                                }

                            } else { //oppt serve
                                switch (current_voice_type) {
                                    case GBR_MAN:
                                    case GBR_WOMAN:
                                        call = getPointByNumStart((byte)(up_point/10*10));
                                        voiceList.add(call);
                                        if (up_point%10 > 0) {
                                            call2 = getPointByNumStart((byte) (up_point % 10));
                                            voiceList.add(call2);
                                        }

                                        if (down_point%10 > 0) {
                                            call3 = getPointByNumStart((byte)(down_point/10*10));
                                            voiceList.add(call3);
                                            call4 = getPointByNumEnd((byte) (down_point % 10));
                                            voiceList.add(call4);
                                        } else {
                                            call3 = getPointByNumEnd((byte)(down_point/10*10));
                                            voiceList.add(call3);
                                        }
                                        break;
                                    case USER_RECORD:
                                        fileName0 = getPointByNumString((byte)(up_point/10*10));
                                        voiceUserList.add(fileName0);
                                        if (up_point%10 > 0) {
                                            fileName1 = getPointByNumString((byte) (up_point % 10));
                                            voiceUserList.add(fileName1);
                                        }

                                        //to
                                        fileName4 = "user_to.m4a";
                                        voiceUserList.add(fileName4);

                                        fileName2 = getPointByNumString((byte)(down_point/10*10));
                                        voiceUserList.add(fileName2);
                                        if (down_point%10 > 0) {
                                            fileName3 = getPointByNumString((byte) (down_point % 10));
                                            voiceUserList.add(fileName3);
                                        }
                                        break;
                                }

                            }
                        }
                    }
                }

                Log.e(TAG, "<== voice choose in tiebreak");
            }

        }

        Log.d(TAG, "<choosePointVoice end>");

        return call;
    }

    private static int getPointByNumStart(byte num) {
        int call = 0;

        switch (num) {
            case 0:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_love;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_love;
                } else {
                    call = R.raw.gbr_man_start_love;
                }
                break;
            case 1:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_1;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_1;
                } else {
                    call = R.raw.gbr_man_start_1;
                }
                break;
            case 2:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_2;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_2;
                } else {
                    call = R.raw.gbr_man_start_2;
                }
                break;
            case 3:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_3;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_3;
                } else {
                    call = R.raw.gbr_man_start_3;
                }
                break;
            case 4:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_4;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_4;
                } else {
                    call = R.raw.gbr_man_start_4;
                }
                break;
            case 5:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_5;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_5;
                } else {
                    call = R.raw.gbr_man_start_5;
                }
                break;
            case 6:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_6;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_6;
                } else {
                    call = R.raw.gbr_man_start_6;
                }
                break;
            case 7:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_7;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_7;
                } else {
                    call = R.raw.gbr_man_start_7;
                }
                break;
            case 8:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_8;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_8;
                } else {
                    call = R.raw.gbr_man_start_8;
                }
                break;
            case 9:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_9;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_9;
                } else {
                    call = R.raw.gbr_man_start_9;
                }
                break;
            case 10:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_10;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_10;
                } else {
                    call = R.raw.gbr_man_start_10;
                }
                break;
            case 11:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_11;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_11;
                } else {
                    call = R.raw.gbr_man_start_11;
                }
                break;
            case 12:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_12;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_12;
                } else {
                    call = R.raw.gbr_man_start_12;
                }
                break;
            case 13:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_13;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_13;
                } else {
                    call = R.raw.gbr_man_start_13;
                }
                break;
            case 14:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_14;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_14;
                } else {
                    call = R.raw.gbr_man_start_14;
                }
                break;
            case 15:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_15;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_15;
                } else {
                    call = R.raw.gbr_man_start_15;
                }
                break;
            case 16:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_16;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_16;
                } else {
                    call = R.raw.gbr_man_start_16;
                }
                break;
            case 17:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_17;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_17;
                } else {
                    call = R.raw.gbr_man_start_17;
                }
                break;
            case 18:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_18;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_18;
                } else {
                    call = R.raw.gbr_man_start_18;
                }
                break;
            case 19:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_19;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_19;
                } else {
                    call = R.raw.gbr_man_start_19;
                }
                break;
            case 20:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_20;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_20;
                } else {
                    call = R.raw.gbr_man_start_20;
                }
                break;
            case 30:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_30;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_30;
                } else {
                    call = R.raw.gbr_man_start_30;
                }
                break;
            case 40:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_40;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_40;
                } else {
                    call = R.raw.gbr_man_start_40;
                }
                break;
            case 50:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_50;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_50;
                } else {
                    call = R.raw.gbr_man_start_50;
                }
                break;
            case 60:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_60;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_60;
                } else {
                    call = R.raw.gbr_man_start_60;
                }
                break;
            case 70:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_70;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_70;
                } else {
                    call = R.raw.gbr_man_start_70;
                }
                break;
            case 80:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_80;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_80;
                } else {
                    call = R.raw.gbr_man_start_80;
                }
                break;
            case 90:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_start_90;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_start_90;
                } else {
                    call = R.raw.gbr_man_start_90;
                }
                break;
            //case 100:
            //    call = R.raw.gbr_man_all;
            //    break;
        }

        return call;
    }

    private static int getPointByNumEnd(byte num) {
        int call = 0;

        switch (num) {
            case 0:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_love;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_love;
                } else {
                    call = R.raw.gbr_man_end_love;
                }
                break;
            case 1:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_1;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_1;
                } else {
                    call = R.raw.gbr_man_end_1;
                }
                break;
            case 2:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_2;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_2;
                } else {
                    call = R.raw.gbr_man_end_2;
                }
                break;
            case 3:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_3;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_3;
                } else {
                    call = R.raw.gbr_man_end_3;
                }
                break;
            case 4:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_4;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_4;
                } else {
                    call = R.raw.gbr_man_end_4;
                }
                break;
            case 5:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_5;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_5;
                } else {
                    call = R.raw.gbr_man_end_5;
                }
                break;
            case 6:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_6;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_6;
                } else {
                    call = R.raw.gbr_man_end_6;
                }
                break;
            case 7:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_7;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_7;
                } else {
                    call = R.raw.gbr_man_end_7;
                }
                break;
            case 8:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_8;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_8;
                } else {
                    call = R.raw.gbr_man_end_8;
                }
                break;
            case 9:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_9;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_9;
                } else {
                    call = R.raw.gbr_man_end_9;
                }
                break;
            case 10:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_10;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_10;
                } else {
                    call = R.raw.gbr_man_end_10;
                }
                break;
            case 11:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_11;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_11;
                } else {
                    call = R.raw.gbr_man_end_11;
                }
                break;
            case 12:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_12;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_12;
                } else {
                    call = R.raw.gbr_man_end_12;
                }
                break;
            case 13:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_13;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_13;
                } else {
                    call = R.raw.gbr_man_end_13;
                }
                break;
            case 14:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_14;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_14;
                } else {
                    call = R.raw.gbr_man_end_14;
                }
                break;
            case 15:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_15;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_15;
                } else {
                    call = R.raw.gbr_man_end_15;
                }
                break;
            case 16:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_16;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_16;
                } else {
                    call = R.raw.gbr_man_end_16;
                }
                break;
            case 17:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_17;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_17;
                } else {
                    call = R.raw.gbr_man_end_17;
                }
                break;
            case 18:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_18;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_18;
                } else {
                    call = R.raw.gbr_man_end_18;
                }
                break;
            case 19:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_19;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_19;
                } else {
                    call = R.raw.gbr_man_end_19;
                }
                break;
            case 20:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_20;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_20;
                } else {
                    call = R.raw.gbr_man_end_20;
                }
                break;
            case 30:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_30;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_30;
                } else {
                    call = R.raw.gbr_man_end_30;
                }
                break;
            case 40:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_40;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_40;
                } else {
                    call = R.raw.gbr_man_end_40;
                }
                break;
            case 50:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_50;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_50;
                } else {
                    call = R.raw.gbr_man_end_50;
                }
                break;
            case 60:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_60;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_60;
                } else {
                    call = R.raw.gbr_man_end_60;
                }
                break;
            case 70:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_70;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_70;
                } else {
                    call = R.raw.gbr_man_end_70;
                }
                break;
            case 80:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_80;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_80;
                } else {
                    call = R.raw.gbr_man_end_80;
                }
                break;
            case 90:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_end_90;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_end_90;
                } else {
                    call = R.raw.gbr_man_end_90;
                }
                break;
            case 100:
                if (current_voice_type == GBR_MAN) {
                    call = R.raw.gbr_man_all;
                } else if (current_voice_type == GBR_WOMAN) {
                    call = R.raw.gbr_woman_all;
                } else {
                    call = R.raw.gbr_man_all;
                }
                break;
        }

        return call;
    }

    private static String getPointByNumString(byte num) {
        String call = "";

        switch (num) {
            case 0:
                call = "user_love.m4a";
                break;
            case 1:
                call = "user_1.m4a";
                break;
            case 2:
                call = "user_2.m4a";
                break;
            case 3:
                call = "user_3.m4a";
                break;
            case 4:
                call = "user_4.m4a";
                break;
            case 5:
                call = "user_5.m4a";
                break;
            case 6:
                call = "user_6.m4a";
                break;
            case 7:
                call = "user_7.m4a";
                break;
            case 8:
                call = "user_8.m4a";
                break;
            case 9:
                call = "user_9.m4a";
                break;
            case 10:
                call = "user_10.m4a";
                break;
            case 11:
                call = "user_11.m4a";
                break;
            case 12:
                call = "user_12.m4a";
                break;
            case 13:
                call = "user_13.m4a";
                break;
            case 14:
                call = "user_14.m4a";
                break;
            case 15:
                call = "user_15.m4a";
                break;
            case 16:
                call = "user_16.m4a";
                break;
            case 17:
                call = "user_17.m4a";
                break;
            case 18:
                call = "user_18.m4a";
                break;
            case 19:
                call = "user_19.m4a";
                break;
            case 20:
                call = "user_20.m4a";
                break;
            case 30:
                call = "user_30.m4a";
                break;
            case 40:
                call = "user_40.m4a";
                break;
            case 50:
                call = "user_50.m4a";
                break;
            case 60:
                call = "user_60.m4a";
                break;
            case 70:
                call = "user_70.m4a";
                break;
            case 80:
                call = "user_80.m4a";
                break;
            case 90:
                call = "user_90.m4a";
                break;
            case 100:
                call = "user_all.m4a";
                break;
        }

        return call;
    }

    private void chooseGameVoice(boolean down_serve, boolean is_tiebreak, byte gameUp,  byte gameDown) {
        Integer gameCall, gameCall2, gameCall3;
        String fileName0, fileName1, fileName2, fileName3;
        Log.d(TAG, "[chooseGameVoice start]");
        if (is_tiebreak) { //enter tiebreak
            Log.d(TAG, "in tiebreak");
            if (games.equals("0")) { //6 game in a set
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_start_6;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        gameCall3 = R.raw.gbr_man_tiebreak;
                        voiceList.add(gameCall3);
                        break;
                    case GBR_WOMAN:
                        gameCall = R.raw.gbr_woman_start_6;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_woman_all;
                        voiceList.add(gameCall2);
                        gameCall3 = R.raw.gbr_woman_tiebreak;
                        voiceList.add(gameCall3);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_6.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        fileName2 = "user_tiebreak.m4a";
                        voiceUserList.add(fileName2);
                        break;
                }
            } else { //4 game in a set
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_start_4;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        gameCall3 = R.raw.gbr_man_tiebreak;
                        voiceList.add(gameCall3);
                        break;
                    case GBR_WOMAN:
                        gameCall = R.raw.gbr_woman_start_4;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_woman_all;
                        voiceList.add(gameCall2);
                        gameCall3 = R.raw.gbr_woman_tiebreak;
                        voiceList.add(gameCall3);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_4.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        fileName2 = "user_tiebreak.m4a";
                        voiceUserList.add(fileName2);
                        break;
                }
            }


        } else {
            Log.d(TAG, "Not in tiebreak, gameUp = "+gameUp+" : gameDown = "+gameDown);

            if (gameUp == 0 && gameDown == 1) { //0:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 2) { //0:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 3) { //0:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 4) { //0:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;

                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 5) { //0:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 6) { //0:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 0) { //1:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 1) { //1:1
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_start_1;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case GBR_WOMAN:
                        gameCall = R.raw.gbr_woman_start_1;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_woman_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_1.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_game_all.m4a";
                        voiceUserList.add(fileName1);
                        fileName2 = "user_all.m4a";
                        voiceUserList.add(fileName2);
                        break;
                }
            } else if (gameUp == 1 && gameDown == 2) { //1:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 3) { //1:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 4) { //1:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 5) { //1:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 6) { //1:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 0) { //2:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 1) { //2:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 2) { //2:2
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_start_2;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case GBR_WOMAN:
                        gameCall = R.raw.gbr_woman_start_2;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_woman_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_2.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;
                }
            } else if (gameUp == 2 && gameDown == 3) { //2:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 4) { //2:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 5) { //2:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 6) { //2:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 0) { //3:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 1) { //3:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 2) { //3:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 3) { //3:3
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_start_3;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case GBR_WOMAN:
                        gameCall = R.raw.gbr_woman_start_3;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_woman_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_3.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;
                }
            } else if (gameUp == 3 && gameDown == 4) { //3:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 5) { //3:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 6) { //3:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 0) { //4:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 1) { //4:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;

                    }

                }
            } else if (gameUp == 4 && gameDown == 2) { //4:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 3) { //4:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 4) { //4:4
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_start_4;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case GBR_WOMAN:
                        gameCall = R.raw.gbr_woman_start_4;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_woman_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_4.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;

                }
            } else if (gameUp == 4 && gameDown == 5) { //4:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;

                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 6) { //4:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 0) { //5:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 1) { //5:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 2) { //5:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 3) { //5:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 4) { //5:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 5) { //5:5
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_start_5;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case GBR_WOMAN:
                        gameCall = R.raw.gbr_woman_start_5;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_woman_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_5.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;
                }
            } else if (gameUp == 5 && gameDown == 6) { //5:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 7) { //5:7
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_start_7;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_7;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_7.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 0) { //6:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }


                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 1) { //6:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 2) { //6:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 3) { //6:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 4) { //6:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 5) { //6:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 6) { //6:6
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_start_6;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case GBR_WOMAN:
                        gameCall = R.raw.gbr_woman_start_6;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_woman_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_6.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;
                }
            } else if (gameUp == 6 && gameDown == 7) { //6:7
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_7;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_7;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 7 && gameDown == 5) { //7:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_7;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_7;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_7.m4a";
                            voiceUserList.add(fileName2);
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 7 && gameDown == 6) { //7:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_7;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_7;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_7.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_start_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case GBR_WOMAN:
                            gameCall = R.raw.gbr_woman_start_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_woman_end_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                    }

                }
            } else {
                Log.e(TAG, "unknown to choose voice");
            }
        }



        Log.d(TAG, "[chooseGameVoice end]");
    }

    private void chooseSetVoice(byte gameServe,  byte gameRecv) {
        Integer gameCall, gameCall2;
        String fileName0, fileName1;
        if (gameServe == 0 && gameRecv == 1) {

            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 0 && gameRecv == 2) {

            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 0 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }
        } else if (gameServe == 0 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }
        } else if (gameServe == 0 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }
        } else if (gameServe == 0 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }
        } else if (gameServe == 1 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }


        } else if (gameServe == 2 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2e.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }


        } else if (gameServe == 2 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 7) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_7;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_7;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_7.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 7) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_7;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_7;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_7.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 7 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_7;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_7;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_7.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 7 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_7;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case GBR_WOMAN:
                    gameCall = R.raw.gbr_woman_start_7;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_woman_end_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_7.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        }
    }

    private Runnable updateTimer = new Runnable() {
        public void run() {
            //final TextView time = (TextView) findViewById(R.id.currentTime);
            NumberFormat f = new DecimalFormat("00");
            //Long spentTime = System.currentTimeMillis() - startTime;
            //計算目前已過分鐘數

            //計算目前已過秒數
            //Long seconds = (time_use) % 60;
            //time.setText(minius+":"+seconds);

            handler.postDelayed(this, 1000);
            time_use++;

            //Log.d(TAG, "time_use = "+time_use);

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            sdf.setTimeZone(tz);//set time zone.
            Date netDate = new Date(System.currentTimeMillis());
            //Date gameDate = new Date(spentTime);
            Long hour = (time_use)/3600;
            Long min = (time_use)%3600/60;
            Long sec = (time_use)%60;
            textCurrentTime.setText(sdf.format(netDate));
            textGameTime.setText(f.format(hour)+":"+f.format(min)+":"+f.format(sec));

            //textGameTime.setText(sdf.format(gameDate));
        }
    };

    /*public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }*/

    @Override
    public void onBackPressed() {

        AlertDialog.Builder confirmdialog = new AlertDialog.Builder(GameActivity.this);
        confirmdialog.setTitle(getResources().getString(R.string.app_exit));
        confirmdialog.setIcon(R.drawable.ball_icon);
        confirmdialog.setCancelable(false);
        confirmdialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                new Thread() {
                    public void run() {
                        //clear
                        clear_record(filename);

                        boolean is_tiebreak;
                        boolean is_deuce;
                        boolean is_firstServe;

                        if (tiebreak != null) {

                            switch (tiebreak) {
                                case "0":
                                    is_tiebreak = true;
                                    break;
                                case "1":
                                    is_tiebreak = false;
                                    break;
                                default:
                                    is_tiebreak = true;
                                    break;
                            }
                        } else {
                            is_tiebreak = true;
                        }

                        if (deuce != null) {
                            switch (deuce) {
                                case "0":
                                    is_deuce = true;
                                    break;
                                case "1":
                                    is_deuce = false;
                                    break;
                                default:
                                    is_deuce = true;
                                    break;
                            }
                        } else {
                            is_deuce = true;
                        }

                        if (serve != null) {

                            switch (serve) {
                                case "0":
                                    is_firstServe = true;
                                    break;
                                case "1":
                                    is_firstServe = false;
                                    break;
                                default:
                                    is_firstServe = true;
                                    break;
                            }
                        } else {
                            is_firstServe = true;
                        }


                        String msg = playerUp + ";" + playerDown + ";" + is_tiebreak + ";" + is_deuce + ";" +is_firstServe+ ";" +set+ ";" +is_retire+ ";" +games+ "|";
                        append_record(msg, filename);

                        com.seventhmoon.tennisscoreboard.Data.State top = stack.peek();
                        if (top != null) {
                            top.setDuration(time_use);

                            int i = 0;
                            //load stack
                            total_state = stack.size();
                            state_num_saved = 0;
                            for (com.seventhmoon.tennisscoreboard.Data.State s : stack) {

                                if (i >= 1) {
                                    append_record("&", filename);
                                }


                                String append_msg = s.getCurrent_set() + ";"
                                        + s.isServe() + ";"
                                        + s.isInTiebreak() + ";"
                                        + s.isFinish() + ";"
                                        + s.isSecondServe() + ";"
                                        + s.isInBreakPoint() + ";"
                                        + s.getSetsUp() + ";"
                                        + s.getSetsDown() + ";"
                                        + s.getDuration() + ";"
                                        + s.getAceCountUp() + ";"
                                        + s.getAceCountDown() + ";"
                                        + s.getFirstServeUp() + ";"
                                        + s.getFirstServeDown() + ";"
                                        + s.getFirstServeMissUp() + ";"
                                        + s.getFirstServeMissDown() + ";"
                                        + s.getSecondServeUp() + ";"
                                        + s.getSecondServeDown() + ";"
                                        + s.getBreakPointUp() + ";"
                                        + s.getBreakPointDown() + ";"
                                        + s.getBreakPointMissUp() + ";"
                                        + s.getBreakPointMissDown() + ";"
                                        + s.getFirstServeWonUp() + ";"
                                        + s.getFirstServeWonDown() + ";"
                                        + s.getFirstServeLostUp() + ";"
                                        + s.getFirstServeLostDown() + ";"
                                        + s.getSecondServeWonUp() + ";"
                                        + s.getSecondServeWonDown() + ";"
                                        + s.getSecondServeLostUp() + ";"
                                        + s.getSecondServeLostDown() + ";"
                                        + s.getDoubleFaultUp() + ";"
                                        + s.getDoubleFaultDown() + ";"
                                        + s.getUnforceErrorUp() + ";"
                                        + s.getUnforceErrorDown() + ";"
                                        + s.getForehandWinnerUp() + ";"
                                        + s.getForehandWinnerDown() + ";"
                                        + s.getBackhandWinnerUp() + ";"
                                        + s.getBackhandWinnerDown() + ";"
                                        + s.getForehandVolleyUp() + ";"
                                        + s.getForehandVolleyDown() + ";"
                                        + s.getBackhandVolleyUp() + ";"
                                        + s.getBackhandVolleyDown() + ";"
                                        + s.getFoulToLoseUp() + ";"
                                        + s.getFoulToLoseDown() + ";"
                                        + s.getSet_game_up((byte) 0x1) + ";"
                                        + s.getSet_game_down((byte) 0x1) + ";"
                                        + s.getSet_point_up((byte) 0x1) + ";"
                                        + s.getSet_point_down((byte) 0x1) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x1) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x1) + ";"
                                        + s.getSet_game_up((byte) 0x2) + ";"
                                        + s.getSet_game_down((byte) 0x2) + ";"
                                        + s.getSet_point_up((byte) 0x2) + ";"
                                        + s.getSet_point_down((byte) 0x2) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x2) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x2) + ";"
                                        + s.getSet_game_up((byte) 0x3) + ";"
                                        + s.getSet_game_down((byte) 0x3) + ";"
                                        + s.getSet_point_up((byte) 0x3) + ";"
                                        + s.getSet_point_down((byte) 0x3) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x3) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x3) + ";"
                                        + s.getSet_game_up((byte) 0x4) + ";"
                                        + s.getSet_game_down((byte) 0x4) + ";"
                                        + s.getSet_point_up((byte) 0x4) + ";"
                                        + s.getSet_point_down((byte) 0x4) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x4) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x4) + ";"
                                        + s.getSet_game_up((byte) 0x5) + ";"
                                        + s.getSet_game_down((byte) 0x5) + ";"
                                        + s.getSet_point_up((byte) 0x5) + ";"
                                        + s.getSet_point_down((byte) 0x5) + ";"
                                        + s.getSet_tiebreak_point_up((byte) 0x5) + ";"
                                        + s.getSet_tiebreak_point_down((byte) 0x5) + ";"
                                        + s.getForceErrorUp() + ";"
                                        + s.getForceErrorDown() + ";"
                                        ;
                                append_record(append_msg, filename);
                                i++;
                                state_num_saved++;
                            }
                        }

                        Intent intent = new Intent(Constants.ACTION.SAVE_CURRENT_STATE_COMPLETE);
                        sendBroadcast(intent);
                    }
                }.start();


                saveTask task = new saveTask();
                task.execute(10);

            }
        });
        confirmdialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        confirmdialog.show();


    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.game_activity_menu, menu);

        //item_edit = menu.findItem(R.id.action_edit_group);
        voice_item = menu.findItem(R.id.action_voice_onOff);
        voice_support_item = menu.findItem(R.id.action_voice_support);

        voice_support_item.setVisible(true);

        if (voice_item != null) {
            if (voiceOn) {
                voice_item.setIcon(R.drawable.ic_keyboard_voice_white_48dp);
                voice_item.setTitle(R.string.game_voice_on);
            } else {
                voice_item.setIcon(R.drawable.ic_keyboard_voice_white_off_48dp);
                voice_item.setTitle(R.string.game_voice_off);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_voice_onOff:

                if (voiceOn) { //voice is on, set off
                    voice_item.setIcon(R.drawable.ic_keyboard_voice_white_off_48dp);
                    voice_item.setTitle(R.string.game_voice_off);
                    voiceOn = false;

                    toast(getResources().getString(R.string.game_voice_off_message));

                } else {
                    voice_item.setIcon(R.drawable.ic_keyboard_voice_white_48dp);
                    voice_item.setTitle(R.string.game_voice_on);
                    voiceOn = true;

                    toast(getResources().getString(R.string.game_voice_on_message));
                }
                editor = pref.edit();
                editor.putBoolean("VOICE_ON", voiceOn);
                editor.apply();
                break;
            case R.id.action_show_stat:
                intent = new Intent(GameActivity.this, CurrentStatActivity.class);
                intent.putExtra("PLAYER_UP", playerUp);
                intent.putExtra("PLAYER_DOWN", playerDown);
                intent.putExtra("TOTAL_SETS", set);
                startActivity(intent);
                break;
            case R.id.action_file_upload:
                File file = new File(get_absolute_path(filename));
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );
                startActivity(intent);
                break;
            case R.id.action_voice_support:
                intent = new Intent(GameActivity.this, VoiceSelectActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    public void toast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    class saveTask extends AsyncTask<Integer, Integer, String>
    {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        //int nowCount;
        @Override
        protected String doInBackground(Integer... countTo) {


            while(is_saving_state) {
                try {
                    long percent = 0;
                    if (total_state > 0)
                        percent = (state_num_saved * 100)/total_state;

                    publishProgress((int)percent, state_num_saved);
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            if (loadDialog == null) {
                loadDialog = new ProgressDialog(GameActivity.this);
                loadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                loadDialog.setTitle("Saving..");
                loadDialog.setProgress(0);
                loadDialog.setMax(100);
                loadDialog.setIndeterminate(false);


                loadDialog.show();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);

            if (loadDialog != null && loadDialog.isShowing()) {

                loadDialog.setTitle("Save state: " + "(" + values[1] + "/" + total_state + ") " + values[0]+"%");

                loadDialog.setProgress(values[0]);
            }

        }
        @Override
        protected void onPostExecute(String result) {


            super.onPostExecute(result);
            if (loadDialog != null && loadDialog.isShowing()) {
                loadDialog.dismiss();
            }


        }

        @Override
        protected void onCancelled() {

            super.onCancelled();
        }
    }
}
