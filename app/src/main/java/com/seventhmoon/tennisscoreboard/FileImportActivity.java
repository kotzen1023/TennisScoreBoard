package com.seventhmoon.tennisscoreboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.FileImportArrayAdapter;
import com.seventhmoon.tennisscoreboard.Data.FileImportItem;
import com.seventhmoon.tennisscoreboard.Service.FileImportService;


import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class FileImportActivity extends AppCompatActivity {
    private static final String TAG = FileImportActivity.class.getName();

    public static boolean FileChooseLongClick = false;
    public static boolean FileChooseSelectAll = false;

    //private Context context;

    public static FileImportArrayAdapter fileImportArrayAdapter;
    public ListView listView;
    public Button confirm;
    private File currentDir;
    //private Menu actionmenu;

    public static ArrayList<String> searchList = new ArrayList<>();

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";

    private String lastImportPath;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.file_import_list);

        pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        lastImportPath = pref.getString("LAST_IMPORT_PATH", "");

        Intent intent = getIntent();
        final String destFilename = intent.getStringExtra("filename");

        Log.e(TAG, "Dest filename = "+destFilename);
        //Context context = getBaseContext();

        //audioOperation = new AudioOperation(context);

        listView = findViewById(R.id.listViewFileImport);
        confirm = findViewById(R.id.btnFileImportConfirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //searchList.clear();
                FileImportItem fileImportItem = null;

                for (int i = 0; i < listView.getCount(); i++) {
                    if (fileImportArrayAdapter.mSparseBooleanArray.get(i)) {
                        fileImportItem = fileImportArrayAdapter.getItem(i);

                        if (fileImportItem != null) {

                            Log.e(TAG, "select : " + fileImportItem.getPath());
                            //searchList.add(fileImportItem.getPath());
                        }
                    }

                }


                if (fileImportItem != null) {
                    Intent importIntent = new Intent(FileImportActivity.this, FileImportService.class);
                    importIntent.setAction(Constants.ACTION.IMPORT_FILE_ACTION);
                    importIntent.putExtra("FILEPATH", fileImportItem.getPath());
                    importIntent.putExtra("DEST_FILE_PATH", destFilename);
                    startService(importIntent);
                }

                //searchFiles();
                finish();
            }
        });

        if (!lastImportPath.equals("")) {
            currentDir = new File(lastImportPath);
        } else {
            currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        }

        Log.e(TAG, "currentDir = "+Environment.getExternalStorageDirectory().getPath());
        fill(currentDir);
    }

    @Override
    protected void onDestroy() {
        FileChooseLongClick = false;
        FileChooseSelectAll = false;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }
    @Override
    public void onResume() {

        Log.i(TAG, "onResume");
        super.onResume();
    }

    public void onBackPressed() {
        /*if (FileChooseLongClick) {
            MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

            FileChooseLongClick = false;
            FileChooseSelectAll = false;
            menuItem.setTitle(getResources().getString(R.string.import_select_all));


            for(int i=0;i<listView.getCount(); i++) {
                FileImportItem fileImportItem = fileImportArrayAdapter.getItem(i);

                if (fileImportItem != null) {

                    if (fileImportItem.getCheckBox() != null) {
                        fileImportItem.getCheckBox().setVisibility(View.INVISIBLE);
                        fileImportItem.getCheckBox().setChecked(false);
                    }
                    fileImportArrayAdapter.mSparseBooleanArray.put(i, false);
                }
            }

            confirm.setVisibility(View.GONE);
        } else {
            //Log.e(TAG, "currentDir = "+currentDir+" root = "+Environment.getExternalStorageDirectory().getPath());
            if (!currentDir.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getPath())) {
                File parent = new File(currentDir.getParent());

                fill(parent);

                currentDir = new File(parent.getAbsolutePath());

                MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

                FileChooseLongClick = false;
                FileChooseSelectAll = false;
                menuItem.setTitle(getResources().getString(R.string.import_select_all));


                for (int i = 0; i < listView.getCount(); i++) {
                    FileImportItem fileImportItem = fileImportArrayAdapter.getItem(i);

                    if (fileImportItem != null) {

                        if (fileImportItem.getCheckBox() != null) {
                            fileImportItem.getCheckBox().setVisibility(View.INVISIBLE);
                            fileImportItem.getCheckBox().setChecked(false);
                        }
                        fileImportArrayAdapter.mSparseBooleanArray.put(i, false);
                    }

                }
            } else {
                finish();
            }

        }*/
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.file_import_menu, menu);

        //MenuItem item_all;

        //item_all = menu.findItem(R.id.action_selectall);

        //item_all.setVisible(true);

        //actionmenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_help:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FileImportActivity.this);

                alertDialog.setTitle(R.string.file_import_help_title);
                alertDialog.setMessage(getResources().getString(R.string.file_import_help_msg));
                alertDialog.setIcon(android.R.drawable.ic_menu_help);
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton(getResources().getString(R.string.find_court_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
                break;

            /*case R.id.action_selectall:
                if (!FileChooseLongClick) {
                    FileChooseLongClick = true;

                    if (!FileChooseSelectAll) {
                        FileChooseSelectAll = true;
                        item.setTitle(getResources().getString(R.string.import_unselect_all));
                        Log.d(TAG, "listView.getCount = "+listView.getCount());
                        for (int i = 0; i < listView.getCount(); i++) {
                            FileImportItem fileImportItem = fileImportArrayAdapter.getItem(i);

                            if (fileImportItem != null) {

                                if (fileImportItem.getCheckBox() != null) {
                                    //Log.e(TAG, "set item[" + i + "] visible");
                                    if (!fileImportItem.getName().equals("..")) {
                                        fileImportItem.getCheckBox().setVisibility(View.VISIBLE);
                                        fileImportItem.getCheckBox().setChecked(true);
                                        fileImportArrayAdapter.mSparseBooleanArray.put(i, true);
                                    } else {
                                        fileImportItem.getCheckBox().setVisibility(View.INVISIBLE);
                                        fileImportItem.getCheckBox().setChecked(false);
                                        fileImportArrayAdapter.mSparseBooleanArray.put(i, false);
                                    }

                                } else {
                                    fileImportArrayAdapter.mSparseBooleanArray.put(i, true);
                                }
                            }
                            //fileChooseArrayAdapter.mSparseBooleanArray.put(i, true);
                        }

                        confirm.setVisibility(View.VISIBLE);
                    } else { //Data.FileChooseSelectAll == true
                        FileChooseSelectAll = false;
                        item.setTitle(getResources().getString(R.string.import_select_all));

                        for (int i = 0; i < listView.getCount(); i++) {
                            FileImportItem fileImportItem = fileImportArrayAdapter.getItem(i);

                            if (fileImportItem != null) {

                                if (fileImportItem.getCheckBox() != null) {
                                    //Log.e(TAG, "set item[" + i + "] visible");
                                    if (!fileImportItem.getName().equals("..")) {
                                        fileImportItem.getCheckBox().setVisibility(View.VISIBLE);
                                        fileImportItem.getCheckBox().setChecked(false);
                                    } else {
                                        fileImportItem.getCheckBox().setVisibility(View.INVISIBLE);
                                        fileImportItem.getCheckBox().setChecked(false);
                                    }

                                }
                                fileImportArrayAdapter.mSparseBooleanArray.put(i, false);
                            }
                        }
                        confirm.setVisibility(View.GONE);
                    }

                } else { //long click == true
                    if (!FileChooseSelectAll) {
                        FileChooseSelectAll = true;
                        item.setTitle(getResources().getString(R.string.import_unselect_all));
                        Log.d(TAG, "listView.getCount = "+listView.getCount());
                        for (int i = 0; i < listView.getCount(); i++) {
                            FileImportItem fileImportItem = fileImportArrayAdapter.getItem(i);

                            if (fileImportItem != null) {

                                if (fileImportItem.getCheckBox() != null) {
                                    //Log.e(TAG, "set item[" + i + "] visible");
                                    if (!fileImportItem.getName().equals("..")) {
                                        //Log.e(TAG, "item["+i+"]="+fileChooseItem.getName());
                                        //fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                                        fileImportItem.getCheckBox().setChecked(true);
                                        fileImportArrayAdapter.mSparseBooleanArray.put(i, true);
                                    } else {
                                        fileImportItem.getCheckBox().setChecked(false);
                                        fileImportArrayAdapter.mSparseBooleanArray.put(i, false);
                                    }

                                } else {
                                    //Log.e(TAG, "item["+i+"]="+fileChooseItem.getName());
                                    fileImportArrayAdapter.mSparseBooleanArray.put(i, true);
                                }
                            }
                            //fileChooseArrayAdapter.mSparseBooleanArray.put(i, true);
                        }
                        confirm.setVisibility(View.VISIBLE);
                    } else { //Data.FileChooseSelectAll == true
                        FileChooseSelectAll = false;
                        item.setTitle(getResources().getString(R.string.import_select_all));

                        for (int i = 0; i < listView.getCount(); i++) {
                            FileImportItem fileImportItem = fileImportArrayAdapter.getItem(i);

                            if (fileImportItem != null) {

                                if (fileImportItem.getCheckBox() != null) {
                                    //Log.e(TAG, "set item[" + i + "] visible");
                                    if (!fileImportItem.getName().equals("..")) {
                                        //fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                                        fileImportItem.getCheckBox().setChecked(false);
                                    } else {
                                        fileImportItem.getCheckBox().setChecked(false);
                                    }

                                }
                                fileImportArrayAdapter.mSparseBooleanArray.put(i, false);
                            }
                        }

                        confirm.setVisibility(View.GONE);
                    }


                }



                break;
                */

            case android.R.id.home:

                break;

            default:
                break;
        }

        return true;
    }

    private void fill(File f)
    {
        final File[]dirs = f.listFiles();
        //this.setTitle("Current Dir: "+f.getName());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(f.getAbsolutePath());
            lastImportPath = f.getAbsolutePath();

            editor = pref.edit();
            editor.putString("LAST_IMPORT_PATH", lastImportPath);
            editor.apply();
        }
        //txtCurrentDir.setText(f.getAbsolutePath());

        ArrayList<FileImportItem> dir = new ArrayList<>();
        ArrayList<FileImportItem> fls = new ArrayList<>();
        try{
            for(File ff: dirs)
            {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                //CheckBox checkBox = new CheckBox(getApplicationContext());
                if(ff.isDirectory()){


                    File[] fbuf = ff.listFiles();
                    int buf;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    if(buf == 0) num_item = num_item + " "+"file";
                    else num_item = num_item + " "+"files";

                    //String formated = lastModDate.toString();
                    char first = ff.getName().charAt(0);
                    if (first != '.')
                        dir.add(new FileImportItem(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon"));
                }
                else
                {
                    char first = ff.getName().charAt(0);
                    if (first != '.')
                        fls.add(new FileImportItem(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(//!f.getName().equalsIgnoreCase("sdcard") ||
                !f.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getPath())) {
            //CheckBox checkBox = new CheckBox(this);
            dir.add(0, new FileImportItem("..", "Parent Directory", "", f.getParent(), "directory_up"));
        }
        fileImportArrayAdapter = new FileImportArrayAdapter(FileImportActivity.this,R.layout.file_import_in_row,dir);
        listView.setAdapter(fileImportArrayAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileImportItem o = fileImportArrayAdapter.getItem(position);

                if (o != null) {

                    if (o.getPath() != null && (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up"))) {
                        currentDir = new File(o.getPath());
                        fill(currentDir);

                        //MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

                        FileChooseLongClick = false;
                        FileChooseSelectAll = false;
                        //menuItem.setTitle("Select all");


                        for (int i = 0; i < listView.getCount(); i++) {
                            FileImportItem fileImportItem = fileImportArrayAdapter.getItem(i);

                            if (fileImportItem != null) {

                                if (fileImportItem.getCheckBox() != null) {
                                    fileImportItem.getCheckBox().setVisibility(View.INVISIBLE);
                                    fileImportItem.getCheckBox().setChecked(false);
                                }
                                fileImportArrayAdapter.mSparseBooleanArray.put(i, false);
                            }

                        }
                    } else {
                        //onFileClick(o);
                        Log.d(TAG, "click " + o.getName());
                    }
                }
            }
        });

        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e(TAG, "position = " + position + ", size = " + listView.getCount());
                FileChooseLongClick = true;
                //FileChooseItem fileChooseItem = (FileChooseItem) fileChooseArrayAdapter.getItem(position);
                //Log.i(TAG, "name = "+fileChooseItem.getName());
                //Log.e(TAG, "ck = " + fileChooseItem.getCheckBox());
                //fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);

                for(int i=0;i<listView.getCount(); i++) {
                    FileImportItem fileImportItem = fileImportArrayAdapter.getItem(i);

                    if (fileImportItem != null) {

                        if (fileImportItem.getCheckBox() != null) {
                            //Log.e(TAG, "set item[" + i + "] visible");
                            if (!fileImportItem.getName().equals(".."))
                                fileImportItem.getCheckBox().setVisibility(View.VISIBLE);
                            else
                                fileImportItem.getCheckBox().setVisibility(View.INVISIBLE);
                        }
                    }
                }


                return false;
            }
        });*/
    }
}
