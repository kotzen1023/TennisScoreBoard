package com.seventhmoon.tennisscoreboard;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
//import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
//import android.widget.Toast;


import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.FileChooseArrayAdapter;
import com.seventhmoon.tennisscoreboard.Data.FileChooseItem;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


import static com.seventhmoon.tennisscoreboard.Data.FileOperation.remove_file;


public class LoadGame extends AppCompatActivity {
    private static final String TAG = LoadGame.class.getName();

    //private Context context;

    private ListView listView;

    private FileChooseArrayAdapter fileChooseArrayAdapter;

    public static File RootDirectory = new File("/");
    private static String call_activity;
    private static String previous_filename;

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    private static ArrayList<FileChooseItem> dir = new ArrayList<>();
    private static ArrayList<FileChooseItem> fls = new ArrayList<>();

    //private MenuItem import_menu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Context context = getBaseContext();

        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }

        Intent intent = getIntent();

        IntentFilter filter;

        call_activity = intent.getStringExtra("CALL_ACTIVITY");
        previous_filename = intent.getStringExtra("PREVIOUS_FILENAME");

        Log.d(TAG, "call_activity = " +call_activity);

        setContentView(R.layout.load_game);

        listView = findViewById(R.id.listViewFileChoose);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }
        //check folder
        final File folder = new File(RootDirectory.getAbsolutePath() + "/.tennisScoredBoard");

        dir.clear();
        fls.clear();

        File[] dirs = folder.listFiles();

        try{
            for(File ff: dirs)
            {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                //CheckBox checkBox = new CheckBox(getApplicationContext());
                if(!ff.isDirectory()){


                    char first = ff.getName().charAt(0);
                    if (first != '.')
                        fls.add(new FileChooseItem(ff.getName(), date_modify));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        //Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);


        fileChooseArrayAdapter = new FileChooseArrayAdapter(LoadGame.this,R.layout.file_choose_item,dir);
        listView.setAdapter(fileChooseArrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final FileChooseItem o = fileChooseArrayAdapter.getItem(position);

                if (o != null) {
                    Log.d(TAG, "select file " + o.getFileName());

                    AlertDialog.Builder confirmdialog = new AlertDialog.Builder(LoadGame.this);
                    confirmdialog.setTitle(getResources().getString(R.string.game_load_or_delete, o.getFileName()));
                    confirmdialog.setIcon(R.drawable.ball_icon);
                    confirmdialog.setCancelable(false);
                    confirmdialog.setPositiveButton(getResources().getString(R.string.game_load), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(LoadGame.this, GameActivity.class);
                            intent.putExtra("FILE_NAME", o.getFileName());
                            startActivity(intent);
                            finish();

                        }
                    });
                    confirmdialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });
                    confirmdialog.setNeutralButton(getResources().getString(R.string.game_delete), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            remove_file(o.getFileName());
                            //send broadcast
                            Intent intent = new Intent(Constants.ACTION.GAME_DELETE_COMPLETE);
                            intent.putExtra("REMOVE_FILE", o.getFileName());
                            sendBroadcast(intent);
                        }
                    });
                    confirmdialog.show();
                }


            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GAME_DELETE_COMPLETE)) {
                        Log.d(TAG, "receive brocast !");

                        String file_name = intent.getStringExtra("REMOVE_FILE");

                        dir.clear();
                        fls.clear();

                        File[] dirs = folder.listFiles();

                        try{
                            for(File ff: dirs)
                            {
                                Date lastModDate = new Date(ff.lastModified());
                                DateFormat formater = DateFormat.getDateTimeInstance();
                                String date_modify = formater.format(lastModDate);
                                //CheckBox checkBox = new CheckBox(getApplicationContext());
                                if(!ff.isDirectory()){


                                    char first = ff.getName().charAt(0);
                                    if (first != '.')
                                        fls.add(new FileChooseItem(ff.getName(), date_modify));
                                }
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        //Collections.sort(dir);
                        Collections.sort(fls);
                        dir.addAll(fls);


                        fileChooseArrayAdapter = new FileChooseArrayAdapter(LoadGame.this,R.layout.file_choose_item,dir);
                        listView.setAdapter(fileChooseArrayAdapter);

                        if (dir.size() == 0) {
                            call_activity = "Main";
                        } else {
                            if (file_name.equals(previous_filename)) {
                                call_activity = "Main";
                            }
                        }
                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.ADD_FILE_LIST_COMPLETE)) {
                        Log.d(TAG, "receive brocast ADD_FILE_LIST_COMPLETE!");

                        dir.clear();
                        fls.clear();

                        File[] dirs = folder.listFiles();

                        try{
                            for(File ff: dirs)
                            {
                                Date lastModDate = new Date(ff.lastModified());
                                DateFormat formater = DateFormat.getDateTimeInstance();
                                String date_modify = formater.format(lastModDate);
                                //CheckBox checkBox = new CheckBox(getApplicationContext());
                                if(!ff.isDirectory()){


                                    char first = ff.getName().charAt(0);
                                    if (first != '.')
                                        fls.add(new FileChooseItem(ff.getName(), date_modify));
                                }
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        //Collections.sort(dir);
                        Collections.sort(fls);
                        dir.addAll(fls);

                        if (fileChooseArrayAdapter == null) {
                            fileChooseArrayAdapter = new FileChooseArrayAdapter(context, R.layout.file_choose_item, dir);
                            listView.setAdapter(fileChooseArrayAdapter);
                        } else {
                            Log.e(TAG, "notifyDataSetChanged");
                            fileChooseArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.GAME_DELETE_COMPLETE);
            filter.addAction(Constants.ACTION.ADD_FILE_LIST_COMPLETE);
            registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

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
        Intent intent;
        switch (call_activity) {
            case "Main":
                intent = new Intent(LoadGame.this, PlayMainActivity.class);
                startActivity(intent);
                break;
            case "Game":
                intent = new Intent(LoadGame.this, GameActivity.class);
                intent.putExtra("FILE_NAME", previous_filename);
                startActivity(intent);
        }


        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.load_menu, menu);

        //item_edit = menu.findItem(R.id.action_edit_group);
        //MenuItem import_menu = menu.findItem(R.id.action_import);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_import:

                intent = new Intent(LoadGame.this, FileImportActivity.class);
                startActivity(intent);
                break;
            /*case R.id.action_show_stat:
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
                break;*/
            /*case R.id.action_voice_support:
                intent = new Intent(GameActivity.this, VoiceSupport.class);
                startActivity(intent);
                break;*/
            default:
                break;
        }
        return true;
    }

    /*public void toast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }*/
}
