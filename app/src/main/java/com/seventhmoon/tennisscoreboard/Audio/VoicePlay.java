package com.seventhmoon.tennisscoreboard.Audio;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.seventhmoon.tennisscoreboard.Data.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static com.seventhmoon.tennisscoreboard.Data.Constants.STATE;



public class VoicePlay {
    private static final String TAG = VoicePlay.class.getName();

    private static Context context;
    public static File RootDirectory = new File("/");

    private static MediaPlayer mediaPlayer;

    public static boolean is_playing = false;

    private static STATE current_state = STATE.Created;
    private static float speed = 1;
    private static float current_volume = 0.5f;
    private static int current_position = 0;
    private final static int MAX_VOLUME = 100;

    public VoicePlay (Context context){
        this.context = context;
    }

    public static void audioPlayer(Context context, int res_id) {
        //String fileName){
        //Log.e(TAG, "audioPlayer start");
        /*if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            //Log.d(TAG, "playing!");


        } else {
            //set up MediaPlayer
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
            }

            try {

                //mediaPlayer.setDataSource(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+fileName);
                mediaPlayer = MediaPlayer.create(context, res_id);

                //mediaPlayer.prepare();

                mediaPlayer.start();


                //mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            //is_playing = false;

        }*/
        //Log.e(TAG, "audioPlayer end");
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
            }

            try {

                //mediaPlayer.setDataSource(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+fileName);
                mediaPlayer = MediaPlayer.create(context, res_id);

                //mediaPlayer.prepare();

                mediaPlayer.start();


                //mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void doStop() {
        Log.d(TAG, "<doStop>");
        if (mediaPlayer != null) {

            if (current_state == STATE.Prepared ||
                    current_state == STATE.Started ||
                    current_state == STATE.Paused ||
                    current_state == STATE.PlaybackCompleted) {

                //pause = false;
                mediaPlayer.stop();
                current_state = STATE.Stopped;
            }
        }

        /*if (goodTask != null) {
            Log.e(TAG, "cancel task");
            if (!goodTask.isCancelled()) {
                goodTask.cancel(true);
                goodTask = null;
            }
        }*/

        //taskDone = true;

        //Intent newNotifyIntent = new Intent(Constants.ACTION.MEDIAPLAYER_STATE_PAUSED);
        //context.sendBroadcast(newNotifyIntent);

        Log.d(TAG, "</doStop>");
    }

    private Handler mIncomingHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.e(TAG, "mIncomingHandler: play finished!");

            current_state = STATE.PlaybackCompleted;
            current_position = 0;

            /*if (isPlayPress) { //if still play, do next step

                if (current_mode == 2) { //single repeat
                    Log.e(TAG, "Looping, do nothing!");


                } else {

                    //taskDone = true;
                    //set state
                    current_state = STATE.PlaybackCompleted;


                    current_position = 0; //play complete, set position = 0


                    Intent newNotifyIntent = new Intent(Constants.ACTION.GET_PLAY_COMPLETE);
                    context.sendBroadcast(newNotifyIntent);

                    switch (current_play_mode) {
                        case 0: //play all
                            doNext();
                            break;
                        case 1: //play shuffle
                            doShuffle();
                            break;
                        case 2: //single repeat
                            doSingleRepeat();
                            break;
                        case 3: //an loop
                            doABLoop();
                            break;
                    }

                }
            } else { //isPlayPress is set as false, stop proceed
                //taskDone = true;
                //set state

                current_state = STATE.PlaybackCompleted;


                current_position = 0; //play complete, set position = 0



                Intent newNotifyIntent = new Intent(Constants.ACTION.GET_PLAY_COMPLETE);
                context.sendBroadcast(newNotifyIntent);

            }*/

            return true;
        }
    });

    private static void playing(int res_id){
        Log.d(TAG, "<playing "+res_id+">");

        AssetFileDescriptor afd = context.getResources().openRawResourceFd(res_id);
        //int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
        //int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        //Log.d(TAG, "bitRate = "+bitRate+", sampleRate = "+sampleRate);

        if (mediaPlayer != null) {
            Log.e(TAG, "mediaPlayer != null");

            if (current_state == Constants.STATE.Paused) { // if current state is paused,
                Log.d(TAG, "State: "+STATE.Paused);

                //set looping
                //mediaPlayer.setLooping(looping);

                //set speed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.e(TAG, "set setPlaybackParams");
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                }

                //set volume
                mediaPlayer.setVolume(current_volume, current_volume);

                mediaPlayer.start();
                //set state
                current_state = STATE.Started;



                /*if (taskDone) {
                    taskDone = false;
                    goodTask = new playtask();
                    goodTask.execute(10);

                }*/

                //Intent newNotifyIntent = new Intent(Constants.ACTION.MEDIAPLAYER_STATE_PLAYED);
                //context.sendBroadcast(newNotifyIntent);
            } else {
                /*mediaPlayer.release();
                //set state
                current_state = STATE.End;
                mediaPlayer = null;*/

                mediaPlayer.reset();
                //set state
                current_state = STATE.Idle;
                Log.d(TAG, "===>Idle");
            }
        }

        if (mediaPlayer == null) {
            Log.e(TAG, "*** mediaPlayer == null (start)****");

            mediaPlayer = new MediaPlayer();
            //set state
            current_state = STATE.Created;
            Log.d(TAG, "===>Created");

            mediaPlayer.reset();
            //set state
            current_state = STATE.Idle;
            Log.d(TAG, "===>Idle");

            Log.e(TAG, "*** mediaPlayer == null (end)****");
        }


        if (current_state == STATE.Idle) {
            try {

                //mediaPlayer.setDataSource(songPath);
                mediaPlayer.setDataSource(afd.getFileDescriptor());
                //set state
                current_state = STATE.Initialized;
                Log.d(TAG, "===>Initialized");

                while (true) {
                    try {
                        Log.d(TAG, "--->set Prepare");
                        mediaPlayer.prepare();
                        break;
                    } catch (IllegalStateException e) {
                        //Log.e(TAG, "==== IllegalStateException start ====");
                        e.printStackTrace();
                        //Log.e(TAG, "==== IllegalStateException end====");
                    }
                }



                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        Log.e(TAG, "===>onPrepared");

                        //set state
                        current_state = STATE.Prepared;

                        //set looping
                        //mediaPlayer.setLooping(looping);

                        mediaPlayer.seekTo(current_position);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Log.e(TAG, "set setPlaybackParams");
                            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                        }

                        //set volume
                        mediaPlayer.setVolume(current_volume, current_volume);

                        mediaPlayer.start();
                        //set state
                        current_state = STATE.Started;


                        //Intent newNotifyIntent = new Intent(Constants.ACTION.MEDIAPLAYER_STATE_PLAYED);
                        //context.sendBroadcast(newNotifyIntent);


                    }
                });


                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d(TAG, "setOnCompletionListener");
                        //Message msg = new Message();

                        //mIncomingHandler.sendMessage(msg);
                        current_state = STATE.PlaybackCompleted;
                        current_position = 0;

                    }
                });



            } catch (IOException e) {
                e.printStackTrace();
                //Intent newNotifyIntent = new Intent(Constants.ACTION.GET_PLAY_COMPLETE);
                //context.sendBroadcast(newNotifyIntent);
            }
        }


        Log.d(TAG, "</playing>");
    }

    public static void audioPlayMulti(ArrayList<Integer> res_id) {
        //String fileName){
        //Log.e(TAG, "audioPlayer start");
        /*if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            //Log.d(TAG, "playing!");


        } else {
            //set up MediaPlayer
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
            }

            try {

                //mediaPlayer.setDataSource(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+fileName);
                mediaPlayer = MediaPlayer.create(context, res_id);

                //mediaPlayer.prepare();

                mediaPlayer.start();


                //mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            //is_playing = false;

        }*/
        //Log.e(TAG, "audioPlayer end");
        for (int i=0;i<res_id.size(); i++) {

            while (mediaPlayer.isPlaying()); //wait for play end

            playing(res_id.get(i));

            /*if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
            }

            try {

                //mediaPlayer.setDataSource(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+fileName);
                mediaPlayer = MediaPlayer.create(context, res_id.get(i));

                //mediaPlayer.prepare();

                mediaPlayer.start();


                //mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }



    }

    public static boolean checkPlay(Context context) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

}
