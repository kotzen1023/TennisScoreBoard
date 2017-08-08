package com.seventhmoon.tennisscoreboard.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.R;

import java.io.File;
import java.io.IOException;

import static com.seventhmoon.tennisscoreboard.Data.FileOperation.check_file_exist;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.copy_file;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.read_out_file;
import static com.seventhmoon.tennisscoreboard.Data.FileOperation.read_record;
import static com.seventhmoon.tennisscoreboard.FileImportActivity.searchList;


public class SearchFileService extends IntentService {
    private static final String TAG = SearchFileService.class.getName();

    private NotificationCompat.Builder mBuilder = null;
    //private Notification notification;
    private NotificationManager mNotifyManager;
    int id = 1;
    private boolean is_searching = false;
    private int current_dir_files = 0;
    private int checked_files = 0;
    private String current_file_name;

    public SearchFileService() {
        super("SearchFileService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");



    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Handle");

        //String filename = intent.getStringExtra("FILENAME");


        if (intent.getAction().equals(Constants.ACTION.GET_SEARCHLIST_ACTION)) {
            Log.i(TAG, "GET_SEARCHLIST_ACTION");

            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle("Searching");
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            //mBuilder.setContentIntent(pendingIntent);
            mBuilder.setOngoing(true);
        }

        is_searching = true;
        new Thread() {
            public void run() {
                searchServicetask task = new searchServicetask();
                task.execute(10);
            }
        }.start();

        searchFiles();
        is_searching = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        Intent intent = new Intent(Constants.ACTION.ADD_FILE_LIST_COMPLETE);
        sendBroadcast(intent);

    }

    public String checkFileFormat(String filePath) {
        Log.e(TAG, "<checkFileFormat>");
        String infoMsg = null;

        //File file = new File(filePath);
        //Log.d(TAG, "file name: "+file.getName());

        String message = read_out_file(filePath);
        Log.d(TAG, "message = "+ message);
        String msg[] = message.split("\\|");

        Log.e(TAG, "msg.length = "+msg.length);

        if (msg.length > 0 && msg.length <= 2) {

            Log.d(TAG, "msg[0] = "+ msg[0]);

            String info[] = msg[0].split(";");
            Log.d(TAG, "info.length = "+info.length);
            if (info.length == 8) {
                if ((info[2].toString().equals("true") || info[2].toString().equals("false")) &&
                        (info[3].toString().equals("true") || info[3].toString().equals("false")) &&
                        (info[4].toString().equals("true") || info[4].toString().equals("false")) &&
                        (info[5].toString().equals("0") || info[5].toString().equals("1") || info[5].toString().equals("2")) &&
                        (info[6].toString().equals("0") || info[6].toString().equals("1")) &&
                        (info[7].toString().equals("0") || info[7].toString().equals("1"))) {
                    Log.e(TAG, "===>match header");
                    if (msg.length == 1) {
                        Log.e(TAG, "length = 1");
                        String filename = copy_file(filePath);
                        Log.e(TAG, "<Import>" + filename + "</Import>");
                    } else { // msg.length == 2
                        Log.e(TAG, "length = 2");
                        String stat[] = msg[1].split("&");
                        if (stat.length > 0) {
                            String data[] = stat[0].split(";");
                            if (data.length > 72 && data.length <= 75) {
                                String filename = copy_file(filePath);
                                Log.e(TAG, "<Import>" + filename + "</Import>");
                            }
                        }

                    }
                }
            }

        } else {

        }


        Log.e(TAG, "</checkFileFormat>");




        return infoMsg;
    }

    /*public void checkFileAndDuration(File file) {

        getAudioInfo(file.getAbsolutePath());

    }*/

    private void search(File file) {


        if (file.exists() && file.isDirectory())
        {
            Log.e(TAG, " <Dir>");
            Log.d(TAG, ""+file.getName()+" is a directory");

            //String[] children = file.list();
            File[] dirs = file.listFiles();
            current_dir_files = current_dir_files + dirs.length;
            checked_files++;
            Log.d(TAG, "===>files("+current_dir_files+"), checked("+checked_files+")");

            for (File children : dirs)
            {
                Log.d(TAG, "["+children.getAbsolutePath()+"]");
                File chk = new File(children.getAbsolutePath());
                if (chk.exists() && chk.isDirectory()) {
                    Log.e(TAG, "Enter "+chk.getName()+" :");

                    //current_dir_files = current_dir_files + chk.listFiles().length;
                    //checked_files++;

                    search(chk);
                } else if (chk.isFile()){
                    Log.e(TAG, " <File>");
                    current_file_name = chk.getName();
                    checkFileFormat(chk.getAbsolutePath());
                    checked_files++;
                } else {
                    Log.e(TAG, "Unknown error(1)");
                }
            }
        } else if (file.isFile()) {
            Log.e(TAG, " <File>");
            current_file_name = file.getName();
            checkFileFormat(file.getAbsolutePath());
            checked_files++;
        } else {
            Log.e(TAG, "Unknown error(2)");
        }


        Log.e(TAG, " <search>");
    }


    public void searchFiles() {
        Log.e(TAG, "<searchFiles>");

        current_dir_files = searchList.size();
        checked_files = 0;
        Log.d(TAG, "===>files("+current_dir_files+")");
        for (int i=0; i<searchList.size(); i++) {
            File file = new File(searchList.get(i));
            search(file);
        }

        //Intent newNotifyIntent = new Intent(Constants.ACTION.ADD_SONG_LIST_COMPLETE);
        //sendBroadcast(newNotifyIntent);

        Log.e(TAG, "<searchFiles>");
    }

    class searchServicetask extends AsyncTask<Integer, Integer, String>
    {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        //int nowCount;
        @Override
        protected String doInBackground(Integer... countTo) {


            while(is_searching) {
                try {
                    long percent = 0;
                    if (current_dir_files > 0)
                        percent = (checked_files * 100)/current_dir_files;

                    publishProgress((int)percent, checked_files);
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

            /*if (loadDialog == null) {
                loadDialog = new ProgressDialog(EncryptService.this);
                loadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                loadDialog.setTitle(R.string.main_menu_loaddialog_encrypting);
                loadDialog.setProgress(0);
                loadDialog.setMax(100);
                loadDialog.setIndeterminate(false);
                //loadDialog.setCancelable(false);

                loadDialog.show();
            }*/
            if (mBuilder != null) {
                mBuilder.setContentTitle("Searching...");
                mBuilder.setProgress(100, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);

            /*if (loadDialog != null && loadDialog.isShowing()) {

                if (Data.OnCompressing == true) { //compressing
                    loadDialog.setTitle(getResources().getString(R.string.main_menu_files_being_compressed) + "(" + values[1] + "/" + Data.magic_selected.size() + ") " + Data.CompressingFileName);

                    loadDialog.setProgress(values[0]);
                } else if (Data.OnEncrypting == true) {
                    //loadDialog.setTitle(getResources().getString(R.string.main_menu_files_being_encrypted) + values[0] + "/" + Data.magic_selected.size() + ") "+ Data.magic_selected.get(values[0] - 1));
                    loadDialog.setTitle(getResources().getString(R.string.main_menu_files_being_encrypted) + "(" + values[1] + "/" + Data.magic_selected.size() + ") " + Data.EnryptingOrDecryptingFileName);
                    loadDialog.setProgress(values[0]);
                } else {
                    loadDialog.setMessage(getResources().getString(R.string.main_menu_files_being_encrypted));
                }
            }*/
            if (mBuilder != null) {
                mBuilder.setContentTitle("check" + "(" + values[1] + "/" + current_dir_files + ") " + current_file_name);
                mBuilder.setProgress(100, values[0], false);
                mBuilder.setContentText(values[0]+"%");
                mNotifyManager.notify(id, mBuilder.build());
            }
        }
        @Override
        protected void onPostExecute(String result) {


            super.onPostExecute(result);
            /*if (loadDialog != null && loadDialog.isShowing()) {
                loadDialog.dismiss();
            }*/
            if (mBuilder != null)
                mNotifyManager.cancel(id);

        }

        @Override
        protected void onCancelled() {

            super.onCancelled();
        }
    }
}
