package com.seventhmoon.tennisscoreboard;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.seventhmoon.tennisscoreboard.Data.GridViewVoiceAdapter;
import com.seventhmoon.tennisscoreboard.Data.ImageBuyItem;
import com.seventhmoon.tennisscoreboard.util.IabBroadcastReceiver;
import com.seventhmoon.tennisscoreboard.util.IabHelper;
import com.seventhmoon.tennisscoreboard.util.IabResult;
import com.seventhmoon.tennisscoreboard.util.Inventory;
import com.seventhmoon.tennisscoreboard.util.Purchase;

import java.io.File;
import java.util.ArrayList;

public class VoiceSupport extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private static final String TAG = VoiceSupport.class.getName();

    public ArrayList<ImageBuyItem> imageBuyItems = new ArrayList<>();

    private GridViewVoiceAdapter gridViewVoiceAdapter;
    private GridView gridView;

    private boolean [] selected;

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";
    private static int CurrentVoiceSupport = 0; //default

    //in-app billing
    IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;
    private static boolean debug = true;

    private static int previous_voice = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        editor = pref.edit();
        CurrentVoiceSupport = pref.getInt("CurrentVoice", 0);

        previous_voice = CurrentVoiceSupport;

        setContentView(R.layout.voice_support);

        //in-app billing

        //1.prepare string

        String encryptKey = "PyAqKiAYKC0qDhkYCwMANVAUWCszOCYuKDMmICk4SiguISAwKgQjKjM4JikbJD4EICQhDCpDW0BG" +
                "MSQYB1gFJAEIOhs6LSIsFQwjWVokACILPioyBysMNlwtSh0UOls/DhQHGCYQJVACMSUTBTAwWg8q" +
                "EDgCOSsAFSQWHw8DOBAPWxQFJ0cCGz8CCzg7Ww4DPzEbJzAFChA5AiwKJTY/LhseNwIgNBkHWgVC" +
                "BxEtUEQDJyMaPTAxHjkdGRUmDCA6EwImCkYrJVoqOS4tXTpQESkhMSI6WEIdKDVfUUQoORoxFwAS" +
                "HxErHylcDEQsVVsPIzBSOCwhLlEhOz85CCcYHFwgPBwiWFYpDUoeBT4tE1kXBA8VHioFIyUQFgEl" +
                "IBg2CwAwJTUPByU/URsMOFsEBA5EKCAdLhAvJh0cEys7O15GGFseXQotSFAQBwsaOUImHiEsJyE8" +
                "SBAsRwYiLig+HlBYGTcfJBwZRDMOIj4rAVsdXBseDz0vOwMuHDBLOS0PDgUgJyk4Mys=";
        //let's encrypt this string
        String base64EncodedPublicKey = xorDecrypt(encryptKey, "richie");
        //Log.d(TAG, "base64EncodedPublicKey = "+base64EncodedPublicKey);
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

                mBroadcastReceiver = new IabBroadcastReceiver(VoiceSupport.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");

                ArrayList<String> additionalSkuList = new ArrayList<>();
                additionalSkuList.add("sku_voice_support_gbr_man_180days");
                additionalSkuList.add("sku_voice_support_gbr_man_30days");
                additionalSkuList.add("sku_voice_support_gbr_man_365days");
                additionalSkuList.add("sku_voice_support_gbr_man_90days");

                //mHelper.queryInventoryAsync(mGotInventoryListener);
                mHelper.queryInventoryAsync(false, additionalSkuList, mGotInventoryListener);
            }
        });

        gridViewVoiceAdapter = new GridViewVoiceAdapter(this, R.layout.grid_item_voice_layout, getData());

        gridView = (GridView) findViewById(R.id.gridViewVoice);
        gridView.setAdapter(gridViewVoiceAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //ImageBuyItem item = (ImageBuyItem) parent.getItemAtPosition(position);
                Log.i(TAG, "item" + position + " was select");

                /*for (int i = 0; i < gridView.getCount(); i++) {
                    ImageBuyItem item = (ImageBuyItem) parent.getItemAtPosition(i);

                    if (i == position) {
                        selected[position] = true;
                        item.setSelected(true);

                        editor.putInt("CurrentTheme", position);
                        editor.apply();
                        Data.current_theme = position;
                    } else {
                        selected[position] = false;
                        item.setSelected(false);
                    }

                }

                switch (Data.current_theme) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            linearLayout.setBackgroundColor(getColor(R.color.inside_main_background_simple));
                        } else {
                            linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_background_simple)));
                        } else
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_background_simple, getTheme())));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_simple));
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_simple, getTheme()));
                        }
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            linearLayout.setBackgroundColor(getColor(R.color.inside_main_background_bear));
                        } else {
                            linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                        //btnBack.setBackgroundColor(Color.parseColor("#727171"));
                        //btnBack.setTextColor(Color.parseColor("#FFFFFF"));
                        //btnBack.setBackgroundResource(R.drawable.buttonbear);
                        //btnBack.setShadowLayer(5, 0, 0, Color.parseColor("#ABABAB"));
                        //btnConfirm.setBackgroundColor(Color.parseColor("#727171"));
                        //btnConfirm.setTextColor(Color.parseColor("#FFFFFF"));
                        //btnConfirm.setBackgroundResource(R.drawable.buttonbear);
                        //btnConfirm.setShadowLayer(5, 0, 0, Color.parseColor("#ABABAB"));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_background_bear)));
                        } else
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_background_bear, getTheme())));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_bear));
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_bear, getTheme()));
                        }
                        break;

                    case 2: //cat
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            linearLayout.setBackgroundColor(getColor(R.color.inside_main_background_cat));
                        } else {
                            linearLayout.setBackgroundColor(Color.parseColor("#efefef"));
                        }
                        //btnBack.setBackgroundColor(Color.parseColor("#75babc"));
                        //btnBack.setTextColor(Color.parseColor("#FFFFFF"));
                        //btnConfirm.setBackgroundColor(Color.parseColor("#75babc"));
                        //btnConfirm.setTextColor(Color.parseColor("#FFFFFF"));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_background_cat)));
                        } else
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_background_cat, getTheme())));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_cat));
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_cat, getTheme()));
                        }
                        break;
                    case 3: //bear
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            linearLayout.setBackgroundColor(getColor(R.color.inside_main_background_classic));
                        } else {
                            linearLayout.setBackgroundColor(Color.parseColor("#333333"));
                        }
                        //btnBack.setTextColor(Color.parseColor("#FFFFFF"));
                        //btnBack.setBackgroundResource(R.drawable.buttonshape);
                        //btnBack.setShadowLayer(5, 0, 0, Color.parseColor("#ABABAB"));
                        //btnConfirm.setBackgroundColor(Color.parseColor("#727171"));
                        //btnConfirm.setTextColor(Color.parseColor("#FFFFFF"));
                        //btnConfirm.setBackgroundResource(R.drawable.buttonshape);
                        //btnConfirm.setShadowLayer(5, 0, 0, Color.parseColor("#ABABAB"));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.menu_for_classic)));
                        } else
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.menu_for_classic, getTheme())));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_classic));
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_classic, getTheme()));
                        }
                        break;
                    default:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            linearLayout.setBackgroundColor(getColor(R.color.inside_main_background_simple));
                        } else {
                            linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_background_simple)));
                        } else
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_background_simple, getTheme())));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_simple));
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_inside_simple, getTheme()));
                        }
                        break;
                }



                gridViewThemeAdapter.notifyDataSetChanged();

                if (!debug) {

                    if (position > 0 && !imageBuyItems.get(position).getPurchased()) //buy items
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ThemeChange.this);
                        dialog.setTitle(getResources().getString(R.string.theme_change_ask_to_buy));
                        dialog.setIcon(R.drawable.ic_error_black_48dp);
                        dialog.setCancelable(false);

                        dialog.setPositiveButton(getResources().getString(R.string.theme_change_confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //buy it
                                do_buy_theme(position);
                            }
                        });

                        dialog.setNegativeButton(getResources().getString(R.string.theme_change_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //don't buy it
                            }
                        });
                        dialog.show();
                    }
                }*/
            }
        });

        selected = new boolean[gridView.getCount()];
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
    public void onBackPressed() {

        finish();
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

    public static String xorEncrypt(byte [] input, String key) {
        //byte[] inputBytes = Base64.encode(input, Base64.DEFAULT);

        int inputSize = input.length;

        byte[] keyBytes = key.getBytes();
        int keySize = keyBytes.length - 1;

        byte[] outBytes = new byte[inputSize];
        for (int i = 0; i < inputSize; i++) {
            outBytes[i] = (byte) (input[i] ^ keyBytes[i % keySize]);
        }

        byte[] encryptBytes = Base64.encode(outBytes, Base64.DEFAULT);

        return new String(encryptBytes);
    }

    @Override
    public void receivedBroadcast() {
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

            //String theme_cat_price = inventory.getSkuDetails("sku_theme_cat").getPrice();
            //String theme_bear_price = inventory.getSkuDetails("sku_theme_bear").getPrice();

            /*Log.i(TAG, "30 days = "+inventory.getSkuDetails("sku_voice_support_gbr_woman").getPriceCurrencyCode()+ " "+
                    inventory.getSkuDetails("sku_voice_support_gbr_woman").getPrice()+ "purchase "+inventory.getPurchase("sku_voice_support_gbr_woman"));

            Log.i(TAG, "90 days = " + inventory.getSkuDetails("sku_voice_support_gbr_man_90days").getPriceCurrencyCode() + " " +
                    inventory.getSkuDetails("sku_voice_support_gbr_man_90days").getPrice() + "purchase " + inventory.getPurchase("sku_voice_support_gbr_man_90days"));

            Log.i(TAG, "180 days = " + inventory.getSkuDetails("sku_voice_support_gbr_man_180days").getPriceCurrencyCode() + " " +
                    inventory.getSkuDetails("sku_voice_support_gbr_man_180days").getPrice() + "purchase " + inventory.getPurchase("sku_voice_support_gbr_man_180days"));

            Log.i(TAG, "1 year = " + inventory.getSkuDetails("sku_voice_support_gbr_man_365days").getPriceCurrencyCode() + " " +
                    inventory.getSkuDetails("sku_voice_support_gbr_man_365days").getPrice() + "purchase " + inventory.getPurchase("sku_voice_support_gbr_man_365days"));


            //30days
            if(inventory.getPurchase("sku_voice_support_gbr_man_30days") == null) { //not buy yet
                if (debug)
                    imageBuyItems.get(0).setTitle("30 days");
                else
                    imageBuyItems.get(0).setTitle("30 days\n" + inventory.getSkuDetails("sku_voice_support_gbr_man_30days").getPrice());
                imageBuyItems.get(0).setPurchased(false);
            } else {
                if (debug)
                    imageBuyItems.get(0).setTitle("30 days");
                else
                    imageBuyItems.get(0).setTitle("30 days\n" + getResources().getString(R.string.voice_change_purchased));
                imageBuyItems.get(0).setPurchased(true);
            }
            //90days
            if(inventory.getPurchase("sku_voice_support_gbr_man_90days") == null) { //not buy yet
                if (debug)
                    imageBuyItems.get(1).setTitle("90 days");
                else
                    imageBuyItems.get(1).setTitle("90 days\n" + inventory.getSkuDetails("sku_voice_support_gbr_man_90days").getPrice());
                imageBuyItems.get(1).setPurchased(false);
            }
            else { //bought
                if (debug)
                    imageBuyItems.get(1).setTitle("90 days");
                else
                    imageBuyItems.get(1).setTitle("90 days\n" + getResources().getString(R.string.voice_change_purchased));
                imageBuyItems.get(1).setPurchased(true);
            }
            //180 days
            if(inventory.getPurchase("sku_voice_support_gbr_man_180days") == null) {
                if (debug)
                    imageBuyItems.get(2).setTitle("180 days");
                else
                    imageBuyItems.get(2).setTitle("180 days\n" + inventory.getSkuDetails("sku_voice_support_gbr_man_180days").getPrice());
                imageBuyItems.get(2).setPurchased(false);
            } else {
                if (debug)
                    imageBuyItems.get(2).setTitle("180 days");
                else
                    imageBuyItems.get(2).setTitle("180 days\n" + getResources().getString(R.string.voice_change_purchased));
                imageBuyItems.get(2).setPurchased(true);
            }

            //1 year
            if(inventory.getPurchase("sku_voice_support_gbr_man_365days") == null) {
                if (debug)
                    imageBuyItems.get(3).setTitle("1 year");
                else
                    imageBuyItems.get(3).setTitle("1 year\n" + inventory.getSkuDetails("sku_voice_support_gbr_man_365days").getPrice());
                imageBuyItems.get(3).setPurchased(false);
            } else {
                if (debug)
                    imageBuyItems.get(3).setTitle("1 year");
                else
                    imageBuyItems.get(3).setTitle("1 year\n" + getResources().getString(R.string.voice_change_purchased));
                imageBuyItems.get(3).setPurchased(true);
            }*/

            gridViewVoiceAdapter.notifyDataSetChanged();
            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            /*
            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // First find out which subscription is auto renewing
            Purchase gasMonthly = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);
            Purchase gasYearly = inventory.getPurchase(SKU_INFINITE_GAS_YEARLY);
            if (gasMonthly != null && gasMonthly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
                mAutoRenewEnabled = true;
            } else if (gasYearly != null && gasYearly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_YEARLY;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteGasSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly))
                    || (gasYearly != null && verifyDeveloperPayload(gasYearly));
            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");
            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                Log.d(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
                return;
            }

            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
            */
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);

                editor.putInt("CurrentVoice", previous_voice);
                editor.apply();
            }
            /*else if (purchase.getSku().equals("sku_theme_bear")) {
                imageBuyItems.get(2).setPurchased(true);
                File bear_dir = new File(Data.RootDirectory.getAbsolutePath() + "/.hkey/theme/bear");
                if (!bear_dir.exists()) {
                    Intent bear_intent = new Intent(ThemeChange.this, DownloadService.class);
                    bear_intent.putExtra(DownloadService.FILENAME, "bear.zip");
                    bear_intent.putExtra(DownloadService.URL, "http://211.20.170.52:8080/hkey_theme/bear.zip");
                    startService(bear_intent);
                }
            }*/
            else if (purchase.getSku().equals("sku_voice_support_gbr_man_30days")) {
                imageBuyItems.get(0).setPurchased(true);
                /*File cat_dir = new File(Data.RootDirectory.getAbsolutePath() + "/.hkey/theme/cat");
                if (!cat_dir.exists()) {
                    Intent cat_intent = new Intent(ThemeChange.this, DownloadService.class);
                    cat_intent.putExtra(DownloadService.FILENAME, "cat.zip");
                    cat_intent.putExtra(DownloadService.URL, "http://211.20.170.52:8080/hkey_theme/cat.zip");
                    startService(cat_intent);
                }*/
            }
            else if (purchase.getSku().equals("sku_voice_support_gbr_man_90days")) {
                imageBuyItems.get(1).setPurchased(true);
                /*File classic_dir = new File(Data.RootDirectory.getAbsolutePath() + "/.hkey/theme/classic");
                if (!classic_dir.exists()) {
                    Intent classic_intent = new Intent(ThemeChange.this, DownloadService.class);
                    classic_intent.putExtra(DownloadService.FILENAME, "classic.zip");
                    classic_intent.putExtra(DownloadService.URL, "http://211.20.170.52:8080/hkey_theme/classic.zip");
                    startService(classic_intent);
                }*/
            }
            else if (purchase.getSku().equals("sku_voice_support_gbr_man_180days")) {
                imageBuyItems.get(2).setPurchased(true);
                /*File classic_dir = new File(Data.RootDirectory.getAbsolutePath() + "/.hkey/theme/classic");
                if (!classic_dir.exists()) {
                    Intent classic_intent = new Intent(ThemeChange.this, DownloadService.class);
                    classic_intent.putExtra(DownloadService.FILENAME, "classic.zip");
                    classic_intent.putExtra(DownloadService.URL, "http://211.20.170.52:8080/hkey_theme/classic.zip");
                    startService(classic_intent);
                }*/
            }
            else if (purchase.getSku().equals("sku_voice_support_gbr_man_365days")) {
                imageBuyItems.get(3).setPurchased(true);
                /*File classic_dir = new File(Data.RootDirectory.getAbsolutePath() + "/.hkey/theme/classic");
                if (!classic_dir.exists()) {
                    Intent classic_intent = new Intent(ThemeChange.this, DownloadService.class);
                    classic_intent.putExtra(DownloadService.FILENAME, "classic.zip");
                    classic_intent.putExtra(DownloadService.URL, "http://211.20.170.52:8080/hkey_theme/classic.zip");
                    startService(classic_intent);
                }*/
            }
        }
    };

    protected void do_buy_voice(int position)
    {
        switch (position)
        {
            case 0: //30days
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSupport.this, "sku_voice_support_gbr_man_30days", 10001, mPurchaseFinishedListener, null);
                break;
            case 1: //90days
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSupport.this, "sku_voice_support_gbr_man_90days", 10001, mPurchaseFinishedListener, null);
                break;
            case 2: //180days
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSupport.this, "sku_voice_support_gbr_man_180days", 10001, mPurchaseFinishedListener, null);
                break;
            case 3: //1 year
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSupport.this, "sku_voice_support_gbr_man_365days", 10001, mPurchaseFinishedListener, null);
                break;
            default:
                break;
        }
    }

    private ArrayList<ImageBuyItem> getData() {
        //clear
        imageBuyItems.clear();
        Bitmap bitmap_30days = BitmapFactory.decodeResource(getResources(), R.drawable.uk_flag);
        Bitmap bitmap_90days = BitmapFactory.decodeResource(getResources(), R.drawable.us_flag);
        Bitmap bitmap_180days = BitmapFactory.decodeResource(getResources(), R.drawable.tw_flag);
        Bitmap bitmap_1year = BitmapFactory.decodeResource(getResources(), R.drawable.uk_flag);

        imageBuyItems.add(new ImageBuyItem(bitmap_30days, "30 days"));
        imageBuyItems.add(new ImageBuyItem(bitmap_90days, "90 days"));
        imageBuyItems.add(new ImageBuyItem(bitmap_180days, "180 days"));
        imageBuyItems.add(new ImageBuyItem(bitmap_1year, "1 year"));

        return imageBuyItems;
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
}
