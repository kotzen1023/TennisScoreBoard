package com.seventhmoon.tennisscoreboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.FileOperation;
import com.seventhmoon.tennisscoreboard.Data.LocationPager;
import com.seventhmoon.tennisscoreboard.Service.CheckCourtTableService;
import com.seventhmoon.tennisscoreboard.Service.CheckMacExistsService;
import com.seventhmoon.tennisscoreboard.Service.InertCourtService;
import com.seventhmoon.tennisscoreboard.Service.UpdateUploadRemainService;
import com.seventhmoon.tennisscoreboard.Sql.Jdbc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import static android.R.drawable.sym_def_app_icon;
import static com.seventhmoon.tennisscoreboard.FindCourtActivity.is_reload;
import static com.seventhmoon.tennisscoreboard.MainMenu.initData;


public class AddCourt extends AppCompatActivity {
    private static final String TAG = AddCourt.class.getName();

    private Context context;

    private ImageView imageView;

    private Spinner courtTypeSpinner;
    private Spinner courtUsageSpinner;
    private Spinner lightSpinner;
    private Spinner ifChargeSpinner;

    public ArrayAdapter<String> courtTypeAdapter;
    public ArrayAdapter<String> courtUsageAdapter;
    public ArrayAdapter<String> lightAdapter;
    public ArrayAdapter<String> courtIfChargeAdapter;

    private EditText editTextCourtName;
    private EditText editTextCourtNum;
    private EditText editTextCharge;
    private RatingBar ratingBarMaintenance;
    private RatingBar ratingBarTraffic;
    private RatingBar ratingBarParking;
    private Button btnConfirm;
    private LinearLayout linearLayoutCharge;

    private final int PICK_FROM_CAMERA = 600;
    private final int PICK_FROM_FILE = 800;
    //private static Uri mImageCaptureUri;
    private static String picFromCamera;
    private static ByteArrayOutputStream stream;
    private static Bitmap resized = null;
    //private static Blob blob;
    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_court);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
            actionBar.setTitle("Upload remain "+initData.getUpload_remain());
        }


        IntentFilter filter;

        Intent intent = getIntent();
        final double longitude = intent.getDoubleExtra("longitude", 0.0);
        final double latitude = intent.getDoubleExtra("latitude", 0.0);

        Log.d(TAG, "longitude = "+longitude+", latitude = "+latitude);

        context = getBaseContext();

        imageView = (ImageView) findViewById(R.id.imageViewCourt);
        editTextCourtName = (EditText) findViewById(R.id.courtName);
        courtTypeSpinner = (Spinner) findViewById(R.id.spinnerCourtType);
        courtUsageSpinner = (Spinner) findViewById(R.id.spinnerCourtUsage);
        lightSpinner = (Spinner) findViewById(R.id.spinnerLight);
        ifChargeSpinner = (Spinner) findViewById(R.id.spinnerIfCharge);
        editTextCourtNum = (EditText) findViewById(R.id.courtNumber);

        editTextCharge = (EditText) findViewById(R.id.courtCharge);
        ratingBarMaintenance = (RatingBar) findViewById(R.id.ratingMaintain);
        ratingBarTraffic = (RatingBar) findViewById(R.id.ratingTraffic);
        ratingBarParking = (RatingBar) findViewById(R.id.ratingParking);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        linearLayoutCharge = (LinearLayout) findViewById(R.id.layoutCharge);

        String[] courtTypeList = {getResources().getString(R.string.court_type_hard),
                getResources().getString(R.string.court_type_grass),
                getResources().getString(R.string.court_type_clay),
                getResources().getString(R.string.court_type_hard)+","+getResources().getString(R.string.court_type_grass),
                getResources().getString(R.string.court_type_hard)+","+getResources().getString(R.string.court_type_clay),
                getResources().getString(R.string.court_type_grass)+","+getResources().getString(R.string.court_type_clay),
                getResources().getString(R.string.court_type_all)};
        String[] courtUsageList = {getResources().getString(R.string.court_usage_public), getResources().getString(R.string.court_usage_private)};
        String[] lightList = {getResources().getString(R.string.court_light_no),
                getResources().getString(R.string.court_light_some),
                getResources().getString(R.string.court_light_all)};
        String[] ifChargeList = {getResources().getString(R.string.court_charge_free), getResources().getString(R.string.court_charge_charge)};

        courtTypeAdapter = new ArrayAdapter<>(AddCourt.this, R.layout.myspinner, courtTypeList);
        courtTypeSpinner.setAdapter(courtTypeAdapter);

        courtUsageAdapter = new ArrayAdapter<>(AddCourt.this, R.layout.myspinner, courtUsageList);
        courtUsageSpinner.setAdapter(courtUsageAdapter);

        lightAdapter = new ArrayAdapter<>(AddCourt.this, R.layout.myspinner, lightList);
        lightSpinner.setAdapter(lightAdapter);

        courtIfChargeAdapter = new ArrayAdapter<>(AddCourt.this, R.layout.myspinner, ifChargeList);
        ifChargeSpinner.setAdapter(courtIfChargeAdapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String [] items        = new String [] {"Camera",
                        "Gallery",
                        "Remove"};
                ArrayAdapter<String> adapter = new ArrayAdapter<> (AddCourt.this, android.R.layout.select_dialog_item,items);

                AlertDialog.Builder builder  = new AlertDialog.Builder(AddCourt.this);

                builder.setTitle("Select:");
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) { //pick from camer
                        if (item == 0) {
                            if (FileOperation.init_camera_folder()) {

                                Calendar c = Calendar.getInstance();
                                NumberFormat f = new DecimalFormat("00");


                                //File camera_folder = new File(FileOperation.RootDirectory + "DCIM/Camera");
                                String file_name = "IMG_"+c.get(Calendar.YEAR)+
                                        f.format(c.get(Calendar.MONTH)+1)+
                                        f.format(c.get(Calendar.DAY_OF_MONTH))+"_"+
                                        f.format(c.get(Calendar.HOUR_OF_DAY))+
                                        f.format(c.get(Calendar.MINUTE))+
                                        f.format(c.get(Calendar.SECOND))+".jpg";

                                Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);



                                File tmpFile = new File(FileOperation.RootDirectory + "/DCIM/Camera/"+file_name);
                                picFromCamera = tmpFile.getAbsolutePath();


                                Uri outputFileUri = Uri.fromFile(tmpFile);
                                Log.e(TAG, "save filename = "+picFromCamera+" uri="+outputFileUri.getPath());
                                picIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
                                startActivityForResult(picIntent, PICK_FROM_CAMERA);
                            }
                        } else
                        if (item == 1) { //pick from file
                            /*if (Build.VERSION.SDK_INT <19){
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                //intent.setAction(Intent.ACTION_GET_CONTENT, );
                                intent.putExtra("return_data",true);
                                startActivityForResult(intent, PICK_FROM_FILE);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                intent.putExtra("return_data",true);
                                startActivityForResult(intent, PICK_FROM_FILE);
                            }*/

                            Intent intent = new Intent();

                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.putExtra("return_data",true);
                            startActivityForResult(intent, PICK_FROM_FILE);
                        } else { //remove
                            //Connection.myCard.removeAvatar();
                            //Connection.saveMyInfo();

                            //imageView.setImageResource(R.drawable.personal);
                            imageView.setImageResource(sym_def_app_icon);
                        }
                    }
                } );
                builder.show();
            }
        });

        ifChargeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Log.e(TAG, "charge");
                    linearLayoutCharge.setVisibility(View.VISIBLE);
                } else {
                    Log.e(TAG, "free");
                    linearLayoutCharge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextCourtName.getText().toString().equals("")) {
                    toast("Name can not be null!");
                } else if (editTextCourtNum.equals("")) {
                    toast("Court number can not be null!");
                } else if (ifChargeSpinner.getSelectedItemPosition() == 1 && editTextCharge.equals("")) {
                    toast("Charge can not be null!");
                } else if (resized == null) {
                    toast("Please add a pic of court!");
                } else {
                    Log.d(TAG, "court name = "+editTextCourtName.getText().toString());
                    Log.d(TAG, "longitude = "+longitude);
                    Log.d(TAG, "latitude = "+latitude);
                    Log.d(TAG, "type = "+courtTypeSpinner.getSelectedItemPosition());
                    Log.d(TAG, "usage = "+courtUsageSpinner.getSelectedItemPosition());
                    Log.d(TAG, "light = "+lightSpinner.getSelectedItemPosition());
                    Log.d(TAG, "ifCharge = "+ifChargeSpinner.getSelectedItemPosition());
                    Log.d(TAG, "courts = "+editTextCourtNum.getText().toString());
                    Log.d(TAG, "charge = "+editTextCharge.getText().toString());
                    Log.d(TAG, "maintenance = "+ratingBarMaintenance.getRating());
                    Log.d(TAG, "traffic = "+ratingBarTraffic.getRating());
                    Log.d(TAG, "parking = "+ratingBarParking.getRating());

                    //Jdbc jdbc = new Jdbc();
                    /*initData.jdbc.insertTableCourt(editTextCourtName.getText().toString(),
                            String.valueOf(longitude),
                            String.valueOf(latitude),
                            String.valueOf(courtTypeSpinner.getSelectedItemPosition()),
                            String.valueOf(courtUsageSpinner.getSelectedItemPosition()),
                            String.valueOf(lightSpinner.getSelectedItemPosition()),
                            editTextCourtNum.getText().toString(),
                            String.valueOf(ifChargeSpinner.getSelectedItemPosition()),
                            editTextCharge.getText().toString(),
                            String.valueOf(ratingBarMaintenance.getRating()),
                            String.valueOf(ratingBarTraffic.getRating()),
                            String.valueOf(ratingBarParking.getRating()),
                            stream.toByteArray()
                    );*/

                    Intent insertIntent = new Intent(AddCourt.this, InertCourtService.class);
                    insertIntent.putExtra("name", editTextCourtName.getText().toString());
                    insertIntent.putExtra("longitude", String.valueOf(longitude));
                    insertIntent.putExtra("latitude", String.valueOf(latitude));
                    insertIntent.putExtra("type", String.valueOf(courtTypeSpinner.getSelectedItemPosition()));
                    insertIntent.putExtra("usage", String.valueOf(courtUsageSpinner.getSelectedItemPosition()));
                    insertIntent.putExtra("light", String.valueOf(lightSpinner.getSelectedItemPosition()));
                    insertIntent.putExtra("courts", String.valueOf(editTextCourtNum.getText().toString()));
                    insertIntent.putExtra("ifCharge", String.valueOf(ifChargeSpinner.getSelectedItemPosition()));
                    insertIntent.putExtra("charge", editTextCharge.getText().toString());
                    insertIntent.putExtra("maintenance", String.valueOf(ratingBarMaintenance.getRating()));
                    insertIntent.putExtra("traffic", String.valueOf(ratingBarTraffic.getRating()));
                    insertIntent.putExtra("parking", String.valueOf(ratingBarParking.getRating()));
                    insertIntent.putExtra("blob", stream.toByteArray());
                    startService(insertIntent);

                }
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.INSERT_COURT_INFO_COMPLETE)) {
                    Log.d(TAG, "receive brocast !");

                    initData.setUpload_remain(initData.getUpload_remain()-1);

                    Intent checkIntent = new Intent(AddCourt.this, UpdateUploadRemainService.class);
                    //checkIntent.putExtra("my_id", my_id);
                    startService(checkIntent);

                    is_reload = true;

                    finish();


                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.INSERT_COURT_INFO_COMPLETE);
            registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

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

    public void toast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String filePath = "";

        switch (requestCode) {
            case PICK_FROM_FILE:
                Log.e(TAG, "PICK_FROM_FILE");

                if (resultCode == Activity.RESULT_OK){
                    if (data!=null) {
                        Uri uri = data.getData();
                        Log.e(TAG, "real path "+uri);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Log.i(TAG, "image data -> " + data.getData().getPath());

                            // DocumentProvider
                            if (DocumentsContract.isDocumentUri(context, data.getData())) {
                                Log.i(TAG, "<DocumentProvider>");
                                if (isExternalStorageDocument(data.getData())) { // ExternalStorageProvider
                                    Log.i(TAG, "=> isExternalStorageDocument ");
                                    String docId = DocumentsContract.getDocumentId(data.getData());
                                    String[] split = docId.split(":");
                                    String type = split[0];

                                    if ("primary".equalsIgnoreCase(type)) {
                                        String path = Environment.getExternalStorageDirectory() + "/" + split[1];
                                        Log.i(TAG, "[" + path + "]");
                                        //if (path != null)
                                    }
                                } // DownloadsProvider
                                else if (isDownloadsDocument(data.getData())) {
                                    Log.i(TAG, "=> DownloadsProvider ");
                                    String id = DocumentsContract.getDocumentId(data.getData());
                                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                                    String path = getDataColumn(context, contentUri, null, null);
                                    Log.i(TAG, "[" + path + "]");
                                    //if (path != null) {
                                    //
                                    //Data.content_uri_list.add(contentUri);
                                    //}
                                }  // MediaProvider
                                else if (isMediaDocument(data.getData())) {
                                    Log.i(TAG, "=> isMediaDocument ");
                                    final String docId = DocumentsContract.getDocumentId(data.getData());
                                    final String[] split = docId.split(":");
                                    final String type = split[0];

                                    Uri contentUri = null;
                                    if ("image".equals(type)) {
                                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    } else if ("video".equals(type)) {
                                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                    } else if ("audio".equals(type)) {
                                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                    }

                                    final String selection = "_id=?";
                                    final String[] selectionArgs = new String[]{
                                            split[1]
                                    };

                                    String path = getDataColumn(context, contentUri, selection, selectionArgs);
                                    Log.i(TAG, "[" + path + "]");
                                    filePath = path;
                                    //if (path != null)
                                    //    Data.magic_selected.add(path);
                                } //unknown
                                else {
                                    Log.e(TAG, "=> UnknownDocument ");
                                }
                            } // MediaStore (and general)
                            else if ("content".equalsIgnoreCase(data.getScheme())) {
                                Log.i(TAG, "<MediaStore>");
                                // Return the remote address
                                if (isGooglePhotosUri(data.getData())) {
                                    Log.i(TAG, "=> remote address = " + data.getData().getLastPathSegment());
                                }
                                String path = getDataColumn(context, data.getData(), null, null);
                                Log.i(TAG, "=> MediaStore path = " + path);
                                //if (path != null)
                                //    Data.magic_selected.add(path);
                            }// File
                            else if ("file".equalsIgnoreCase(data.getScheme())) {
                                Log.i(TAG, "<file>");
                                Log.i(TAG, "File = " + data.getData().getPath());
                                //Data.magic_selected.add(data.getData()getPath());
                            } else {
                                Log.e(TAG, "<Unknown> = " + data.getData().getPath());
                            }
                        }
                        Bitmap bmImg;
                        if (filePath != null && filePath.length() > 0) {
                            bmImg = BitmapFactory.decodeFile(filePath);
                            //imageView.setImageBitmap(bmImg);
                            int new_width, new_height;
                            if (bmImg.getWidth() > bmImg.getHeight()) {
                                new_width = 512;
                                new_height =  (bmImg.getHeight() * 512) / bmImg.getWidth();
                            } else if (bmImg.getWidth() < bmImg.getHeight()){
                                new_height = 512;
                                new_width = (bmImg.getWidth() * 512) /bmImg.getHeight();
                            } else {
                                new_width = 512;
                                new_height = 512;
                            }
                            resized = Bitmap.createScaledBitmap(bmImg, new_width, new_height, true);
                            stream = new ByteArrayOutputStream();
                            resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            imageView.setImageBitmap(resized);


                            //byte[] byteArray = stream.toByteArray();
                            //Connection.myCard.setAvatar(byteArray);
                            //Connection.saveMyInfo();
                            //Connection.reloadMyVCard = true;

                        } else {
                            imageView.setImageResource(R.drawable.googleg_standard_color_18);
                        }




                        /*BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        try {
                            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
                            options.inSampleSize = calculateInSampleSize(options, 256, 256);
                            options.inJustDecodeBounds = false;
                            Bitmap image = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();

                            imageView.setImageBitmap(image);

                            //

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }*/
                    }else {
                        toast("Cancelled");
                    }
                }else if (resultCode == Activity.RESULT_CANCELED) {
                    toast("Cancelled");
                }
                break;
            case PICK_FROM_CAMERA:
                Log.e(TAG, "PICK_FROM_CAMERA");
                if (resultCode == Activity.RESULT_OK) {

                    File tmpFile = new File(picFromCamera);

                    if (tmpFile.exists()) {

                        Bitmap bmImg = BitmapFactory.decodeFile(picFromCamera);
                        //imageView.setImageBitmap(bmImg);
                        int new_width, new_height;
                        if (bmImg.getWidth() > bmImg.getHeight()) {
                            new_width = 512;
                            new_height =  (bmImg.getHeight() * 512) / bmImg.getWidth();
                        } else if (bmImg.getWidth() < bmImg.getHeight()){
                            new_height = 512;
                            new_width = (bmImg.getWidth() * 512) /bmImg.getHeight();
                        } else {
                            new_width = 512;
                            new_height = 512;
                        }
                        resized = Bitmap.createScaledBitmap(bmImg, new_width, new_height, true);
                        stream = new ByteArrayOutputStream();
                        resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        imageView.setImageBitmap(resized);
                        //byte[] byteArray = stream.toByteArray();
                        //Connection.myCard.setAvatar(byteArray);
                        //Connection.saveMyInfo();
                        //Connection.reloadMyVCard = true;


                    } else if (data.getExtras() == null) {

                        toast("No extras to retrieve!");


                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    toast("Cancelled");
                }
                break;
        }
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
}
