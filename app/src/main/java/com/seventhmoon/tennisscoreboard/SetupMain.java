package com.seventhmoon.tennisscoreboard;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import static com.seventhmoon.tennisscoreboard.Data.FileOperation.append_record;


public class SetupMain extends AppCompatActivity{
    private static final String TAG = SetupMain.class.getName();

    private TextView textUpPlayer;
    private TextView textDownPlayer;
    private Spinner setSpinner;
    private Spinner gameSpinner;
    private Spinner tiebreakSpinner;
    private Spinner deuceSpinner;
    private Spinner serveSpinner;

    public ArrayAdapter<String> setAdapter;
    public ArrayAdapter<String> gameAdapter;
    public ArrayAdapter<String> tiebreakAdapter;
    public ArrayAdapter<String> deuceAdapter;
    public ArrayAdapter<String> serveAdapter;

    private String fileName;
    private String playerUp;
    private String playerDown;
    private String serve;

    //private MenuItem item_edit;
    private static ArrayList<String> serveList = new ArrayList<>();
    private static ArrayList<String> tiebreakList = new ArrayList<>();

    //private int set_choose = 0;
    //private int game_choose = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_menu);

        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }

        Intent intent = getIntent();

        fileName = intent.getStringExtra("FILE_NAME");
        playerUp = intent.getStringExtra("PLAYER_UP");
        playerDown = intent.getStringExtra("PLAYER_DOWN");
        serve = intent.getStringExtra("SETUP_SERVE");

        Button confirm;

        textUpPlayer = findViewById(R.id.textViewUpPlayer);
        textDownPlayer = findViewById(R.id.textViewDownPlayer);
        setSpinner = findViewById(R.id.spinnerSets);
        gameSpinner = findViewById(R.id.spinnerGames);
        tiebreakSpinner = findViewById(R.id.spinnerTieBreak);
        deuceSpinner = findViewById(R.id.spinnerDeuce);
        serveSpinner = findViewById(R.id.spinnerServe);
        confirm = findViewById(R.id.btnConfirm);

        String[] setList = {getResources().getString(R.string.setup_one_set),
                getResources().getString(R.string.setup_three_sets),
                getResources().getString(R.string.setup_five_set)};

        String[] gameList = {getResources().getString(R.string.setup_six_games),
                getResources().getString(R.string.setup_four_games)};

        //final String[] tiebreakList = {getResources().getString(R.string.setup_tiebreak), getResources().getString(R.string.setup_deciding_game)};

        tiebreakList.clear();

        tiebreakList.add(getResources().getString(R.string.setup_tiebreak));
        tiebreakList.add(getResources().getString(R.string.setup_deciding_game));

        String[] deuceList = {getResources().getString(R.string.setup_deuce), getResources().getString(R.string.setup_deciding_point)};

        if (playerUp != null) {
            if (playerUp.equals("")) {
                playerUp = "Player1";
            }
        } else {
            playerUp = "Player1";
        }

        if (playerDown != null) {
            if (playerDown.equals("")) {
                playerDown = "Player2";
            }
        } else {
            playerDown = "Player2";
        }

        textUpPlayer.setText(playerUp);
        textDownPlayer.setText(playerDown);

        String serveUp = playerUp+" "+getResources().getString(R.string.setup_first_serve);
        String serveDown = playerDown+" "+getResources().getString(R.string.setup_first_serve);

        serveList.clear();
        serveList.add(serveDown);
        serveList.add(serveUp);

        //String[] serveList = {serveUp, serveDown};


        setAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, setList);
        setSpinner.setAdapter(setAdapter);

        setSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "select "+position);

                tiebreakList.clear();
                tiebreakList.add(getResources().getString(R.string.setup_tiebreak));
                tiebreakList.add(getResources().getString(R.string.setup_deciding_game));
                if (gameSpinner.getSelectedItemPosition() == 0) { //only 6 games in a set

                    if (position >= 1) { //3,5 sets
                        //String item = getResources().getString(R.string.setup_supertiebreak);

                        tiebreakList.add(getResources().getString(R.string.setup_supertiebreak));
                        tiebreakList.add(getResources().getString(R.string.setup_long_game));


                        //tiebreakAdapter.notifyDataSetChanged();
                    }
                }
                tiebreakAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gameAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, gameList);
        gameSpinner.setAdapter(gameAdapter);

        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                tiebreakList.clear();
                tiebreakList.add(getResources().getString(R.string.setup_tiebreak));
                tiebreakList.add(getResources().getString(R.string.setup_deciding_game));
                if (setSpinner.getSelectedItemPosition() >= 1) { //3,5 sets

                    if (position == 0) { //6 games in a set
                        //String item = getResources().getString(R.string.setup_supertiebreak);

                        tiebreakList.add(getResources().getString(R.string.setup_supertiebreak));
                        tiebreakList.add(getResources().getString(R.string.setup_long_game));

                    }
                }
                tiebreakAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tiebreakAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, tiebreakList);
        tiebreakSpinner.setAdapter(tiebreakAdapter);

        deuceAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, deuceList);
        deuceSpinner.setAdapter(deuceAdapter);

        serveAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, serveList);
        serveSpinner.setAdapter(serveAdapter);

        if (serve != null) {

            if (serve.equals("1")) {
                serveSpinner.setSelection(1);
            } else {
                serveSpinner.setSelection(0);
            }
        } else {
            serve = "0";
            serveSpinner.setSelection(0);
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean is_tiebreak;
                boolean is_deuce;
                boolean is_firstServe;
                //boolean is_6game;

                //int is_setLimit;

                switch (tiebreakSpinner.getSelectedItemPosition()) {
                    case 0:
                        is_tiebreak = true;
                        break;
                    default:
                        is_tiebreak = false;
                        break;
                }

                /*switch (gameSpinner.getSelectedItemPosition()) {
                    case 0:
                        is_6game = true;
                        break;
                    default:
                        is_6game = false;
                        break;
                }*/

                switch (deuceSpinner.getSelectedItemPosition()) {
                    case 0:
                        is_deuce = true;
                        break;
                    default:
                        is_deuce = false;
                        break;
                }

                /*
                if (deuceSpinner.getSelectedItemPosition() == 0) {
                    is_deuce = true;
                } else {
                    is_deuce = false;
                }*/

                switch (serveSpinner.getSelectedItemPosition()) {
                    case 0:
                        is_firstServe = true;
                        break;
                    default:
                        is_firstServe = false;
                        break;
                }

                /*
                if (serveSpinner.getSelectedItemPosition() == 0) {
                    is_firstServe = true;
                } else {
                    is_firstServe = false;
                }*/




                String msg = playerUp + ";" + playerDown + ";" + is_tiebreak +";"+ is_deuce+ ";" +is_firstServe+ ";"+setSpinner.getSelectedItemPosition()+ ";false;"+ gameSpinner.getSelectedItemPosition() +"|";
                append_record(msg, fileName);

                Intent intent = new Intent(SetupMain.this, GameActivity.class);
                intent.putExtra("SETUP_SET", String.valueOf(setSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_GAME", String.valueOf(gameSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_TIEBREAK", String.valueOf(tiebreakSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_DEUCE", String.valueOf(deuceSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_SERVE", String.valueOf(serveSpinner.getSelectedItemPosition()));
                intent.putExtra("FILE_NAME", fileName);
                intent.putExtra("PLAYER_UP", playerUp);
                intent.putExtra("PLAYER_DOWN", playerDown);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");


        super.onDestroy();

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.setup_menu, menu);

        //item_edit = menu.findItem(R.id.action_edit_group);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_help:
                AlertDialog.Builder helpdialog = new AlertDialog.Builder(this);
                helpdialog.setIcon(R.mipmap.ic_launcher);
                helpdialog.setTitle(getResources().getString(R.string.setup_help));
                helpdialog.setMessage(getResources().getString(R.string.setup_help_msg_deciding_game)+"\n"+
                        getResources().getString(R.string.setup_help_msg_super_tiebreak)+"\n"+
                        getResources().getString(R.string.setup_help_msg_deciding_point)+"\n"+
                        getResources().getString(R.string.setup_help_msg_long_game));
                helpdialog.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                helpdialog.show();
                break;

            case R.id.action_edit_group:

                showInputDialog();
                break;

            default:
                break;
        }
        return true;
    }

    protected void showInputDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        View promptView = View.inflate(SetupMain.this, R.layout.setup_input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SetupMain.this);
        alertDialogBuilder.setView(promptView);

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        final EditText editPlayerUp = promptView.findViewById(R.id.editResetPlayerUp);
        final EditText editPlayerDown = promptView.findViewById(R.id.editResetPlayerDown);
        if (playerUp != null)
            editPlayerUp.setText(playerUp);
        if (playerDown != null)
            editPlayerDown.setText(playerDown);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //resultText.setText("Hello, " + editText.getText());
                //Log.e(TAG, "input password = " + editText.getText());

                if (editPlayerUp.getText().toString().equals("")) {
                    playerUp = "Player1";


                } else {
                    playerUp = editPlayerUp.getText().toString();
                }

                if (editPlayerDown.getText().toString().equals("")) {
                    playerDown = "Player2";
                } else {
                    playerDown = editPlayerDown.getText().toString();
                }

                textUpPlayer.setText(playerUp);
                textDownPlayer.setText(playerDown);

                String serveUp = playerUp+" "+getResources().getString(R.string.setup_first_serve);
                String serveDown = playerDown+" "+getResources().getString(R.string.setup_first_serve);

                serveList.clear();
                serveList.add(serveDown);
                serveList.add(serveUp);


                //serveAdapter = new ArrayAdapter<>(SetupMain.this, R.layout.myspinner, serveList);
                //serveSpinner.setAdapter(serveAdapter);
                serveAdapter.notifyDataSetChanged();

                if (serve.equals("1")) {
                    serveSpinner.setSelection(1);
                } else {
                    serveSpinner.setSelection(0);
                }
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.show();
    }
}
