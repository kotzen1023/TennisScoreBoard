package com.seventhmoon.tennisscoreboard.Service;

import android.app.IntentService;

import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import android.util.Log;

import com.seventhmoon.tennisscoreboard.Data.Constants;


import java.io.File;
import java.io.IOException;


import static com.seventhmoon.tennisscoreboard.Data.FileOperation.importFileToSelect;



public class FileImportService extends IntentService {
    private static final String TAG = FileImportService.class.getName();


    private String dest_file_name;

    public FileImportService() {
        super("FileImportService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");



    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Handle");

        String filePath = intent.getStringExtra("FILEPATH");
        dest_file_name = intent.getStringExtra("DEST_FILE_PATH");


        Log.e(TAG, "filePath = "+filePath+" dest = "+dest_file_name);

        if (intent.getAction().equals(Constants.ACTION.IMPORT_FILE_ACTION)) {
            Log.i(TAG, "GET_SEARCHLIST_ACTION");
        }

        //clear add list
        //addSongList.clear();

        check(filePath);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        Intent intent = new Intent(Constants.ACTION.IMPORT_FILE_COMPLETE);
        sendBroadcast(intent);
    }

    public String getAudioInfo(String filePath) {
        Log.e(TAG, "<getAudioInfo>");
        String infoMsg = null;
        boolean hasFrameRate = false;

        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(filePath);// the adresss location of the sound on sdcard.
        } catch (IOException e) {

            e.printStackTrace();
        }



        File file = new File(filePath);
        Log.d(TAG, "file name: "+file.getName());

        if (mex != null) {

            try {
                MediaFormat mf = mex.getTrackFormat(0);
                Log.d(TAG, "file: "+file.getName()+" mf = "+mf.toString());
                infoMsg = mf.getString(MediaFormat.KEY_MIME);
                Log.d(TAG, "type: "+infoMsg);

                if (infoMsg.contains("audio")) {

                    Log.d(TAG, "duration(us): " + mf.getLong(MediaFormat.KEY_DURATION));
                    Log.d(TAG, "channel: " + mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
                    if (mf.toString().contains("channel-mask")) {
                        Log.d(TAG, "channel mask: " + mf.getInteger(MediaFormat.KEY_CHANNEL_MASK));
                    }
                    if (mf.toString().contains("aac-profile")) {
                        Log.d(TAG, "aac profile: " + mf.getInteger(MediaFormat.KEY_AAC_PROFILE));
                    }

                    Log.d(TAG, "sample rate: " + mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));

                    if (infoMsg != null && infoMsg.contains("mp4a")) {
                        Log.e(TAG, "match m4a file, import this file!");
                        importFileToSelect(filePath, dest_file_name);
                        /*Song song = new Song();
                        song.setName(file.getName());
                        song.setPath(file.getAbsolutePath());
                        //song.setDuration((int)(mf.getLong(MediaFormat.KEY_DURATION)/1000));
                        song.setDuration_u(mf.getLong(MediaFormat.KEY_DURATION));
                        song.setChannel((byte) mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
                        song.setSample_rate(mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                        song.setMark_a(0);
                        song.setMark_b((int) (mf.getLong(MediaFormat.KEY_DURATION) / 1000));
                        addSongList.add(song);*/

                    }
                } else {
                    Log.e(TAG, "Unknown type");
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "file: "+file.getName()+" not support");
        }

        Log.e(TAG, "</getAudioInfo>");




        return infoMsg;
    }

    /*public void checkFileAndDuration(File file) {

        getAudioInfo(file.getAbsolutePath());

    }*/

    private void check(String filePath) {
        File file = new File(filePath);

        if (file.exists() && file.isFile()) {
            Log.e(TAG, " <File>");
            getAudioInfo(file.getAbsolutePath());
        } else {
            Log.e(TAG, "Unknown error(2)");
        }


        Log.e(TAG, " <search>");
    }


    /*public void checkFiles() {
        Log.e(TAG, "<searchFiles>");

        for (int i=0; i<searchList.size(); i++) {
            File file = new File(searchList.get(i));
            search(file);
        }

        //Intent newNotifyIntent = new Intent(Constants.ACTION.ADD_SONG_LIST_COMPLETE);
        //sendBroadcast(newNotifyIntent);

        Log.e(TAG, "<searchFiles>");
    }*/
}
