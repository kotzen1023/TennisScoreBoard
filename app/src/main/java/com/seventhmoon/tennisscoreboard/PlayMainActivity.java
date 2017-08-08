package com.seventhmoon.tennisscoreboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import static com.seventhmoon.tennisscoreboard.Data.FileOperation.check_file_exist;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.clear_record;



public class PlayMainActivity extends AppCompatActivity {
    private static final String TAG = PlayMainActivity.class.getName();

    //public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    //private SensorManager mSensorManager;
    //private final int REQUEST_ENABLE_BT = 200;
    //private Context context;
    //BluetoothAdapter mBluetoothAdapter;
    //BroadcastReceiver mReceiver;
    //private boolean isRegister;

    //private static MenuItem voiceItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_main);

        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }

        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            init_folder_and_files();
        } else {
            if(checkAndRequestPermissions()) {
                // carry on the normal flow, as the case of  permissions  granted.

                init_folder_and_files();
            }
        }*/





        //Button btnNewGame = (Button) findViewById(R.id.btnNewGame);
        //Button btnContinue = (Button) findViewById(R.id.btnContinue);

        ImageView imgNewGame = (ImageView) findViewById(R.id.imgNewGame);
        ImageView imgContinue = (ImageView) findViewById(R.id.imgContinue);

        TextView textNewGame = (TextView) findViewById(R.id.textNewGame);
        TextView textContinue = (TextView) findViewById(R.id.textContinue);

        imgNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(MainActivity.this, SetupMain.class);
                startActivity(intent);
                finish();*/
                showInputDialog();
            }
        });

        textNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

        imgContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayMainActivity.this, LoadGame.class);
                intent.putExtra("CALL_ACTIVITY", "Main");
                startActivity(intent);
                finish();
            }
        });

        textContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayMainActivity.this, LoadGame.class);
                intent.putExtra("CALL_ACTIVITY", "Main");
                startActivity(intent);
                finish();
            }
        });




    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();

    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");


        /*if (isRegister && mReceiver != null) {

            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
            Log.d(TAG, "unregisterReceiver mReceiver");

        }*/

        super.onDestroy();
    }

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    protected void showInputDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        View promptView = View.inflate(PlayMainActivity.this, R.layout.input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayMainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        final EditText editPlayerUp = (EditText) promptView.findViewById(R.id.editPlayerUp);
        final EditText editPlayerDown = (EditText) promptView.findViewById(R.id.editPlayerDown);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //resultText.setText("Hello, " + editText.getText());
                //Log.e(TAG, "input password = " + editText.getText());

                if (editFileName.getText().toString().equals("")) {
                    toast(getResources().getString(R.string.file_name_enpty));

                } else {
                    //check same file name
                    if (check_file_exist(editFileName.getText().toString()))
                    {
                        AlertDialog.Builder confirmdialog = new AlertDialog.Builder(PlayMainActivity.this);
                        confirmdialog.setTitle(getResources().getString(R.string.file_is_exist_overwrite, editFileName.getText().toString()));
                        confirmdialog.setIcon(R.drawable.ball_icon);

                        confirmdialog.setCancelable(false);
                        confirmdialog.setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //overwrite
                                //clear
                                clear_record(editFileName.getText().toString());

                                //String msg = editPlayerUp.getText().toString() + ";" + editPlayerDown.getText().toString() + "|";
                                //append_record(msg, editFileName.getText().toString());

                                Intent intent = new Intent(PlayMainActivity.this, SetupMain.class);
                                intent.putExtra("FILE_NAME", editFileName.getText().toString());
                                if (!editPlayerUp.getText().toString().equals(""))
                                    intent.putExtra("PLAYER_UP", editPlayerUp.getText().toString());
                                else
                                    intent.putExtra("PLAYER_UP", "");
                                if (!editPlayerDown.getText().toString().equals(""))
                                    intent.putExtra("PLAYER_DOWN", editPlayerDown.getText().toString());
                                else
                                    intent.putExtra("PLAYER_DOWN", "");
                                startActivity(intent);
                                finish();
                            }
                        });
                        confirmdialog.setNegativeButton(getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                        confirmdialog.show();
                    } else {

                        //add new file
                        //String msg = editPlayerUp.getText().toString() + ";" + editPlayerDown.getText().toString() + "|";
                        //append_record(msg, editFileName.getText().toString());


                        Intent intent = new Intent(PlayMainActivity.this, SetupMain.class);
                        intent.putExtra("FILE_NAME", editFileName.getText().toString());
                        if (!editPlayerUp.getText().toString().equals(""))
                            intent.putExtra("PLAYER_UP", editPlayerUp.getText().toString());
                        else
                            intent.putExtra("PLAYER_UP", "Player1");
                        if (!editPlayerDown.getText().toString().equals(""))
                            intent.putExtra("PLAYER_DOWN", editPlayerDown.getText().toString());
                        else
                            intent.putExtra("PLAYER_DOWN", "Player2");
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem voiceItem = menu.findItem(R.id.action_lang_support);

        voiceItem.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_lang_support:
                intent = new Intent(PlayMainActivity.this, VoiceSelectActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
