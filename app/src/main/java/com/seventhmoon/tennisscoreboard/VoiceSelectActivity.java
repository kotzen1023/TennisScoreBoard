package com.seventhmoon.tennisscoreboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.seventhmoon.tennisscoreboard.Data.GridViewVoiceAdapter;
import com.seventhmoon.tennisscoreboard.Data.ImageBuyItem;
import com.seventhmoon.tennisscoreboard.Data.RandomString;
import com.seventhmoon.tennisscoreboard.util.IabBroadcastReceiver;

import com.seventhmoon.tennisscoreboard.util.IabHelper;
import com.seventhmoon.tennisscoreboard.util.IabResult;
import com.seventhmoon.tennisscoreboard.util.Inventory;
import com.seventhmoon.tennisscoreboard.util.Purchase;

import java.util.ArrayList;


public class VoiceSelectActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private static final String TAG = VoiceSelectActivity.class.getName();

    public ArrayList<ImageBuyItem> imageBuyItems = new ArrayList<>();
    private LinearLayout linearLayout;

    private GridViewVoiceAdapter gridViewVoiceAdapter;
    private GridView gridView;

    private boolean [] selected;

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";

    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    private static boolean debug = false;
    private Window window;
    ArrayList<String> additionalSkuList = new ArrayList<>();
    private int previous_select = 0;
    private int current_voice;
    public static ActionBar actionBar;
    private MenuItem item_record, item_listen;
    //buying save
    private static boolean voice_support_gbr_woman = false;
    private static boolean voice_support_gbr_user_record = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.e(TAG, "onCreate");

        setContentView(R.layout.voice_select);

        actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        current_voice = pref.getInt("VOICE_SELECT", 0);

        //load buying
        voice_support_gbr_woman = pref.getBoolean("VOICE_SUPPORT_GBR_WOMAN", false);
        voice_support_gbr_user_record = pref.getBoolean("VOICE_SUPPORT_USER_RECORD", false);

        //in-app billing

        //1.prepare string
        String encryptedKey = "HyAqKiAPEiYrNQISAAIMFFEeYisiOSwjEikmESgyUCgoGiErEQ4oKyg0FikbBD4EICQ2NiFCYFtM" +
                "OiUUJlkPHgEZOxE3FzgsJA0pQ1oiOyMQBSA5BjAABlwtah0UOlsoNB8GIz0aLlEOECQZPzAhWwUn" +
                "KiICCCoKDyQQJA4YAxoEWg8JF0cCOz8CCzgsYQUCBCoRLDEJKxEzOCwbJDwyFAEeBgMqLhkBYQRZ" +
                "PBsmUV8PFyMaHTAxHjkKIx4nNzswGAMqK0chH1o7OCQgZyBQICgrKyI8Y0MGEz9UUF8kCRoxNwAS" +
                "HxE8JSJdN18mXloDAjFYAiwwL1ssASU5OSYSBlwmBx05Y1wiDFESNT4tM1kXBA8CJCEEGD4aHQAp" +
                "ARk8MQAhJD8CPT8/YBoGIlsCPw9fEyoWLwsjFh0cMys7O15RIlAfZhEnQ1EcJgoQA0I3HyshHTs8" +
                "eREmXQYkFSklJVpTGCwTFBwZZDMOIj48O1AcZwAUBDwjGgIkJjBaOCcCNB8gFigyKSs=";

        /*String original = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArVWgHMSeI+22/RLqu1fLhzSxRDP"+
                "EvdJ+3GhKyWIZnYeU4D8twR2MgwoqTyF8kCLpmYB3lByJkZCigMuwfqQsg2flD/kiVacQI2mkVCrDXlxy"+
                "ZjExLUWGiwTjIFpd2l0nrE96jDKsOYRvPopvNeRSpjOx/HM3XPME4H9rAHCKY0+oAV786AZrXeiqwxYvJ"+
                "4e6E63fQY1PESG2IRMPkOqn5CTuP15Ad8wfVDa0tlfgwImJWyuiLRqUciBLVgnWV2seJ2glg6ACuGbFEu"+
                "uaBXS74q8v4xD+8yubyQ+TwBDNSU+xE5oAFALw30pEvGtp6ZmJWYh8u5iwlUFIjMtY9PNggwIDAQAB";*/

        //Log.d(TAG, "original: "+original);


        //String encryped = xorEncrypt(original.getBytes(), "RichieShih");
        //Log.d(TAG, "encrypted: "+encryped);

        //let's decrypt this string
        String base64EncodedPublicKey = xorDecrypt(encryptedKey, "RichieShih");
        //Log.d(TAG, "decrypted: "+base64EncodedPublicKey);

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().

                mBroadcastReceiver = new IabBroadcastReceiver(VoiceSelectActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");

                //ArrayList<String> additionalSkuList = new ArrayList<>();
                additionalSkuList.clear();
                additionalSkuList.add("sku_voice_support_gbr_woman");
                additionalSkuList.add("sku_voice_support_user_record");
                //additionalSkuList.add("sku_theme_cat");
                //additionalSkuList.add("sku_theme_classic");

                //mHelper.queryInventoryAsync(mGotInventoryListener);
                mHelper.queryInventoryAsync(true, additionalSkuList, mGotInventoryListener);
            }
        });

        //btnBack = (Button) findViewById(R.id.btnThemeBack);
        //btnConfirm = (Button) findViewById(R.id.btnThemeConfirm);

        linearLayout = (LinearLayout) findViewById(R.id.voice_select_layout);

        window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);






        gridViewVoiceAdapter = new GridViewVoiceAdapter(this, R.layout.grid_item_voice_layout, getData());

        gridView = (GridView) findViewById(R.id.gridViewVoice);
        gridView.setAdapter(gridViewVoiceAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //ImageBuyItem item = (ImageBuyItem) parent.getItemAtPosition(position);
                Log.i(TAG, "item " + position + " was select");

                for (int i = 0; i < gridView.getCount(); i++) {
                    ImageBuyItem item = (ImageBuyItem) parent.getItemAtPosition(i);

                    if (i == position) {
                        selected[position] = true;
                        item.setSelected(true);
                    } else {
                        selected[position] = false;
                        item.setSelected(false);
                    }

                }

                gridView.invalidateViews();
                //gridViewVoiceAdapter.notifyDataSetChanged();

                if (!debug) {

                    if (position > 0) {

                        if (!getPurchasedWithoutInternet(position)) {

                            if (!imageBuyItems.get(position).getPurchased()) //buy items
                            {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(VoiceSelectActivity.this);
                                dialog.setTitle(getResources().getString(R.string.voice_change_ask_to_buy));
                                dialog.setIcon(R.drawable.ball_icon);
                                dialog.setCancelable(false);

                                dialog.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //buy it
                                        do_buy_theme(position);
                                    }
                                });

                                dialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //don't buy it

                                        for (int i = 0; i < imageBuyItems.size(); i++) {
                                            ImageBuyItem item = imageBuyItems.get(i);

                                            if (i == previous_select) {
                                                selected[previous_select] = true;
                                                item.setSelected(true);
                                            } else {
                                                selected[previous_select] = false;
                                                item.setSelected(false);
                                            }

                                        }
                                        //save current position
                                        editor = pref.edit();
                                        editor.putInt("VOICE_SELECT", previous_select);
                                        editor.apply();

                                        gridView.invalidateViews();
                                    }
                                });
                                dialog.show();
                            } else { //you have buy this one
                                //save current position
                                editor = pref.edit();
                                editor.putInt("VOICE_SELECT", position);
                                editor.apply();

                                previous_select = position;
                            }
                        } else {
                            Log.d(TAG, "You have bought this item, we don't have to check this with internet");
                        }
                    } else { //position == 0
                        //save current position
                        editor = pref.edit();
                        editor.putInt("VOICE_SELECT", position);
                        editor.apply();

                        previous_select = position;
                    }
                } else {
                    if (position == 2) { //user record
                        item_record.setVisible(true);
                        item_listen.setVisible(false);
                    } else {
                        item_record.setVisible(false);
                        item_listen.setVisible(true);
                    }

                    Log.e(TAG, "voice change to "+position);

                    //save current position
                    editor = pref.edit();
                    editor.putInt("VOICE_SELECT", position);
                    editor.apply();

                    previous_select = position;
                }
            }
        });

        selected = new boolean[gridView.getCount()];

    }

    private ArrayList<ImageBuyItem> getData() {
        //clear
        imageBuyItems.clear();
        Bitmap bitmap_simple = BitmapFactory.decodeResource(getResources(), R.drawable.uk_flag);
        imageBuyItems.add(new ImageBuyItem(bitmap_simple, getResources().getString(R.string.voice_gbr_man)));

        Bitmap bitmap_uk_woman = BitmapFactory.decodeResource(getResources(), R.drawable.uk_flag);
        imageBuyItems.add(new ImageBuyItem(bitmap_uk_woman, getResources().getString(R.string.voice_gbr_woman)));

        Bitmap bitmap_user_record = BitmapFactory.decodeResource(getResources(), R.drawable.ic_record_voice_over_white_48dp);
        imageBuyItems.add(new ImageBuyItem(bitmap_user_record, getResources().getString(R.string.voice_user_record)));

        for(int i=0; i<imageBuyItems.size(); i++) {
            if (i == current_voice) {
                imageBuyItems.get(i).setSelected(true);
            } else {
                imageBuyItems.get(i).setSelected(false);
            }
        }



        return imageBuyItems;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if (mHelper != null) mHelper.dispose();
        mHelper = null;

        unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();
    }

    public static String xorDecrypt(String input, String key) {
        byte[] inputBytes = Base64.decode(input, Base64.DEFAULT);
        //byte[] inputBytes = input.getBytes(Charset.forName("ISO-8859-1"));
        int inputSize = inputBytes.length;

        byte[] keyBytes = key.getBytes();
        int keySize = keyBytes.length - 1;

        byte[] outBytes = new byte[inputSize];
        for (int i = 0; i < inputSize; i++) {
            outBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keySize]);
        }
        return new String(outBytes);
    }

    public static String xorEncrypt(byte[] input, String key) {
        int inputSize = input.length;
        byte[] keyBytes = key.getBytes();
        int keySize = keyBytes.length - 1;

        byte[] outTempBytes = new byte[inputSize];
        for (int i = 0; i < inputSize; i++) {
            outTempBytes[i] = (byte) (input[i] ^ keyBytes[i % keySize]);
        }

        String output = Base64.encodeToString(outTempBytes, Base64.DEFAULT);

        return output;
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.e(TAG, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");


            if (inventory != null) {
                //gbr woman
                if (inventory.getPurchase("sku_voice_support_gbr_woman") != null) {
                    imageBuyItems.get(1).setPurchase(inventory.getPurchase("sku_voice_support_gbr_woman"));

                    Log.i(TAG, "sku_voice_support_gbr_woman = " + inventory.getSkuDetails("sku_voice_support_gbr_woman").getPriceCurrencyCode() + " " +
                            inventory.getSkuDetails("sku_voice_support_gbr_woman").getPrice() + "purchase " + inventory.getPurchase("sku_voice_support_gbr_woman"));


                    if (inventory.getPurchase("sku_voice_support_gbr_woman") == null) { //not buy yet
                        if (debug)
                            imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_gbr_woman));
                        else
                            imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_gbr_woman)+"\n" + inventory.getSkuDetails("sku_voice_support_gbr_woman").getPrice());
                        imageBuyItems.get(1).setPurchased(false);
                    } else {
                        if (debug)
                            imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_gbr_woman));
                        else
                            imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_gbr_woman)+"\n" + getResources().getString(R.string.voice_change_purchased));
                        imageBuyItems.get(1).setPurchased(true);

                        editor = pref.edit();
                        editor.putBoolean("VOICE_SUPPORT_GBR_WOMAN", true);
                        editor.apply();
                    }
                } else {
                    Log.e(TAG, "inventory.getPurchase = null");
                    if (debug)
                        imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_gbr_woman));
                    else
                        imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_gbr_woman)+"\n" + inventory.getSkuDetails("sku_voice_support_gbr_woman").getPrice());
                    imageBuyItems.get(1).setPurchased(false);
                }

                //user record
                if (inventory.getPurchase("sku_voice_support_user_record") != null) {
                    imageBuyItems.get(2).setPurchase(inventory.getPurchase("sku_voice_support_user_record"));

                    Log.i(TAG, "sku_voice_support_user_record = " + inventory.getSkuDetails("sku_voice_support_user_record").getPriceCurrencyCode() + " " +
                            inventory.getSkuDetails("sku_voice_support_user_record").getPrice() + "purchase " + inventory.getPurchase("sku_voice_support_user_record"));


                    if (inventory.getPurchase("sku_voice_support_user_record") == null) { //not buy yet
                        if (debug)
                            imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record));
                        else
                            imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record)+"\n" + inventory.getSkuDetails("sku_voice_support_user_record").getPrice());
                        imageBuyItems.get(2).setPurchased(false);
                    } else {
                        if (debug)
                            imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record));
                        else
                            imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record)+"\n" + getResources().getString(R.string.voice_change_purchased));
                        imageBuyItems.get(2).setPurchased(true);

                        editor = pref.edit();
                        editor.putBoolean("VOICE_SUPPORT_USER_RECORD", true);
                        editor.apply();
                    }
                } else {
                    Log.e(TAG, "inventory.getPurchase = null");
                    if (debug)
                        imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record));
                    else
                        imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record)+"\n" + inventory.getSkuDetails("sku_voice_support_user_record").getPrice());
                    imageBuyItems.get(2).setPurchased(false);
                }
            }

            gridViewVoiceAdapter.notifyDataSetChanged();

        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {

            int select = 0;

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);

                select = previous_select;
            }
            else if (purchase.getSku().equals("sku_voice_support_gbr_woman")) {
                imageBuyItems.get(1).setPurchased(true);

                select = 1;

            } else if (purchase.getSku().equals("sku_voice_support_user_record")) {
                imageBuyItems.get(2).setPurchased(true);
                select = 2;

            }

            for (int i = 0; i < imageBuyItems.size(); i++) {
                ImageBuyItem item = imageBuyItems.get(i);

                if (i == select) {
                    selected[select] = true;
                    item.setSelected(true);
                } else {
                    selected[select] = false;
                    item.setSelected(false);
                }

            }

            //save current position
            editor = pref.edit();
            editor.putInt("VOICE_SELECT", select);
            editor.apply();

            gridView.invalidateViews();
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        //clickButton.setEnabled(true);
                        Log.d(TAG, "buy Consume "+purchase.getSku()+" success!");
                    } else {
                        // handle error
                    }
                }
            };



    IabHelper.QueryInventoryFinishedListener mReceivedInventoryBuyGBRManListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase("sku_voice_support_gbr_woman"),
                        mConsumeFinishedListener);
            }
        }
    };

    protected void do_buy_theme(int position)
    {
        RandomString randomString = new RandomString(36);
        //Purchase purchase = imageBuyItems.get(0).getPurchase();
        switch (position)
        {
            case 1: //gbr woman
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSelectActivity.this, "sku_voice_support_gbr_woman", 10001, mPurchaseFinishedListener, null);
                    //mHelper.consumeAsync(imageBuyItems.get(0).getPurchase(), mConsumeFinishedListener);
                    /*if (purchase != null) {
                        Log.d(TAG, "purchase != null");
                    } else {
                        Log.d(TAG, "purchase == null");
                    }*/

                break;
            case 2: //user record
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSelectActivity.this, "sku_voice_support_user_record", 10001, mPurchaseFinishedListener, null);
                //mHelper.consumeAsync(imageBuyItems.get(0).getPurchase(), mConsumeFinishedListener);
                    /*if (purchase != null) {
                        Log.d(TAG, "purchase != null");
                    } else {
                        Log.d(TAG, "purchase == null");
                    }*/

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (mHelper != null && mHelper.handleActivityResult(requestCode, resultCode, data))
        {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
            return;
        }

        // not handled, so handle it ourselves (here's where you'd
        // perform any handling of activity results not related to in-app
        // billing...

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        //editor.putInt("CurrentTheme", previous_theme);
        //editor.apply();
        //Data.current_theme = previous_theme;

        //Intent intent = new Intent(VoiceSelectActivity.this, MainActivity.class);
        //startActivity(intent);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.e(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.voice_select_menu, menu);

        item_record = menu.findItem(R.id.action_record);
        item_listen = menu.findItem(R.id.action_listen);

        if (current_voice == 2) {//user
            item_record.setVisible(true);
            item_listen.setVisible(false);
        } else {
            item_record.setVisible(false);
            item_listen.setVisible(true);
        }



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_listen:
                intent = new Intent(VoiceSelectActivity.this, VoiceListenActivity.class);
                startActivity(intent);
                break;
            case R.id.action_record:
                intent = new Intent(VoiceSelectActivity.this, VoiceRecordActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private boolean getPurchasedWithoutInternet(int position) {
        boolean ret;

        switch (position) {
            case 0: //gbr man, always true
                ret = true;
                break;
            case 1: //gbr woman
                if (voice_support_gbr_woman)
                    ret = true;
                else
                    ret = false;
                break;
            case 2: //user record
                if (voice_support_gbr_user_record)
                    ret = true;
                else
                    ret = false;
                break;
            default:
                ret = false;
                break;


        }

        return ret;
    }
}
