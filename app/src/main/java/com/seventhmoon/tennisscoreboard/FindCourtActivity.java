package com.seventhmoon.tennisscoreboard;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.LocationPager;
import com.seventhmoon.tennisscoreboard.Data.PageItem;
import com.seventhmoon.tennisscoreboard.Sql.Jdbc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.seventhmoon.tennisscoreboard.MainMenu.initData;

public class FindCourtActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {


    private static final String TAG = FindCourtActivity.class.getName();

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private static GoogleMap mGoogleMap;
    private static GoogleApiClient mGoogleApiClient;

    private static LocationManager mLocationManager;
    private Context context;

    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;

    Location mLastLocation;
    Marker mCurrLocationMarker;


    LatLng latLng;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    LocationListener locationListener;

    private SearchView searchView;

    List<Marker> mMarkers = new ArrayList<>();

    //LocationPager adapter;
    public static LocationPager pageAdapter = null;
    private ViewPager viewPager;

    public static String[] rank;
    public static String[] country;
    public static String[] population;

    private static int currentPage;
    private View viewDrawer;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private static boolean is_close = false;

    private static double longitude = 0.0;
    private static double latitude = 0.0;

    public static ArrayList<PageItem> myCourtList = new ArrayList<>();

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;
    //private Jdbc jdbc;
    private boolean is_permmision = false;

    //private MarkerOptions options = new MarkerOptions();
    //private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ArrayList<Marker> markerList = new ArrayList<>();

    static SharedPreferences pref ;
    //static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";
    private static String macAddress;
    ProgressDialog loadDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_court);

        pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        macAddress = pref.getString("WIFIMAC", "");

        context = getBaseContext();

        IntentFilter filter;

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        viewDrawer = findViewById(R.id.view1);
        linearLayout = (LinearLayout) findViewById(R.id.viewPagerLaylout);
        imageView = (ImageView) findViewById(R.id.imgDraw);

        viewDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_close) {
                    linearLayout.setVisibility(View.GONE);
                    is_close = true;
                    imageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    is_close = false;
                    imageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
                }
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "No need to ask permmision");

            is_permmision = true;
            init_mapFragment();

        } else {
            if(checkAndRequestPermissions()) {
                is_permmision = true;
                // carry on the normal flow, as the case of  permissions  granted.
                init_mapFragment();
            }
        }

        //testDB();




        //rank = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

        //country = new String[] { "China", "India", "United States",
        //        "Indonesia", "Brazil", "Pakistan", "Nigeria", "Bangladesh",
        //        "Russia", "Japan" };

        //population = new String[] { "1,354,040,000", "1,210,193,422",
        //        "315,761,000", "237,641,326", "193,946,886", "182,912,000",
        //        "170,901,000", "152,518,015", "143,369,806", "127,360,000" };

        //viewPager = (ViewPager) findViewById(R.id.view_pager);






        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_COURT_INFO_COMPLETE)) {
                    Log.d(TAG, "receive brocast !");

                    if (myCourtList.size() > 0) {

                        if (pageAdapter == null) {
                            Log.d(TAG, "pageAdapter = null");
                            pageAdapter = new LocationPager(context, myCourtList);
                            viewPager.setAdapter(pageAdapter);

                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageSelected(int position) {
                                    Log.i(TAG, "onPageSelected = " + position);
                                    currentPage = position;
                                    Marker marker;


                                    if (myCourtList.size() > 0) {
                                        LatLng location;
                                        if (position > myCourtList.size()) {
                                            location = new LatLng(myCourtList.get(0).getLatitude(), myCourtList.get(0).getLongitude());
                                            marker = markerList.get(0);
                                        } else if (position == 0) {
                                            location = new LatLng(myCourtList.get(0).getLatitude(), myCourtList.get(0).getLongitude());
                                            marker = markerList.get(0);
                                        } else {
                                            location = new LatLng(myCourtList.get(currentPage - 1).getLatitude(), myCourtList.get(currentPage - 1).getLongitude());
                                            marker = markerList.get(currentPage -1);
                                        }

                                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                        marker.showInfoWindow();




                                    }
                                }

                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    // not needed
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {
                                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                                        int pageCount = myCourtList.size() + 2;

                                        if (currentPage == pageCount - 1) {
                                            viewPager.setCurrentItem(1, false);
                                        } else if (currentPage == 0) {
                                            viewPager.setCurrentItem(pageCount - 2, false);
                                        }
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "pageAdapter not null");
                            pageAdapter.notifyDataSetChanged();
                        }

                        markerList.clear();

                        for (int i = 0; i<myCourtList.size(); i++) {
                            Log.d(TAG, "Add marker: "+myCourtList.get(i).getLongitude()+" "+myCourtList.get(i).getLatitude());
                            LatLng location = new LatLng(myCourtList.get(i).getLatitude(), myCourtList.get(i).getLongitude());
                            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(myCourtList.get(i).getName()));



                            marker.setTag(0);
                            markerList.add(marker);

                            //latlngs.add(new LatLng(myCourtList.get(i).getLongitude(), myCourtList.get(i).getLatitude()));
                            //mGoogleMap.addMarker(new MarkerOptions()
                            //        .position(new LatLng(myCourtList.get(i).getLongitude(), myCourtList.get(i).getLatitude()))
                            //        .title(myCourtList.get(i).getName()));

                        }

                    }
                    loadDialog.dismiss();

                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.GET_COURT_INFO_COMPLETE);
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
            pageAdapter = null;

            isRegister = false;
            mReceiver = null;
        }

        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "onPause");

        myCourtList.clear();
        pageAdapter.notifyDataSetChanged();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "onResume");

        if (is_permmision) {
            loadDialog = new ProgressDialog(FindCourtActivity.this);
            loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadDialog.setTitle("Loading...");
            loadDialog.setIndeterminate(false);
            loadDialog.setCancelable(false);

            loadDialog.show();

            if (longitude != 0.0 && latitude != 0.0) {
                initData.jdbc.queryCourtTable(context, longitude, latitude);
            }
        }



    }

    public void init_mapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void init_view_pager() {
        //jdbc = new Jdbc(context, longitude, latitude, macAddress);
        //jdbc = new Jdbc();

        //jdbc.queryUserIdTable;
        initData.jdbc.queryCourtTable(context, longitude, latitude);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady");

        mGoogleMap = googleMap;

        //Initialize Google Play Services
        buildGoogleApiClient();
        try {
            mGoogleMap.setMyLocationEnabled(true);

            Location myLocation = getLastKnownLocation();

            if (myLocation != null) {
                longitude = myLocation.getLongitude();
                latitude = myLocation.getLatitude();
                Log.d(TAG, "longitude = "+longitude+" latitude = "+latitude);

                init_view_pager();
            } else {
                Log.d(TAG, "location = null");
            }

            //LatLng sydney = new LatLng(22.631392, 120.301803);
            //mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("捷運美麗島站"));

            /*mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {

                    Log.d(TAG, "onMyLocationButtonClick!");

                    Location myLocation = getLastKnownLocation();

                    if (myLocation != null) {
                        double longitude = myLocation.getLongitude();
                        double latitude = myLocation.getLatitude();
                        Log.d(TAG, "longitude = "+longitude+" latitude = "+latitude);
                    } else {
                        Log.d(TAG, "location = null");
                    }


                    return false;
                }
            });*/



        } catch (SecurityException e) {
            e.printStackTrace();
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Log.e(TAG, "onMarkerClick");
                if (markerList.size() > 0) {
                    for (int i=0; i<markerList.size(); i++) {
                        if (marker.getTitle().equals(markerList.get(i).getTitle())) {
                            viewPager.setCurrentItem(i+1, false);
                        }
                    }
                }


                return false;
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

        //optionally, stop location updates if only current location is needed
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }



    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            try {
                l = mLocationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }





    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private  boolean checkAndRequestPermissions() {
        //int permissionSendMessage = ContextCompat.checkSelfPermission(this,
        //        android.Manifest.permission.WRITE_CALENDAR);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Log.e(TAG, "result size = "+grantResults.length+ "result[0] = "+grantResults[0]+", result[1] = "+grantResults[1]);


        /*switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    Log.i(TAG, "WRITE_CALENDAR permissions granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "READ_CONTACTS permissions denied");

                    RetryDialog();
                }
            }
            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }*/
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        Log.d(TAG, "all permission granted");
                        is_permmision = true;
                        init_mapFragment();
                        //init_view_pager();



                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (//ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_CALENDAR) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            //|| ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                                ) {
                            showDialogOK("Need location access",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.find_court_menu, menu);



        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItem addCourtItem = menu.findItem(R.id.action_locate);

        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchMenuItem.setVisible(false);

        if (initData.getUpload_remain() > 0){
            addCourtItem.setVisible(true);
        } else {
            addCourtItem.setVisible(false);
        }

        try {
            //SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search_keeper));
            searchView.setOnQueryTextListener(queryListener);
        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:

                break;
            case R.id.action_locate:
                Intent intent = new Intent(FindCourtActivity.this, AddCourt.class);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    final private android.support.v7.widget.SearchView.OnQueryTextListener queryListener = new android.support.v7.widget.SearchView.OnQueryTextListener() {
        //searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            List<Address> addressList = null;
            //String location = newText;
            for (Marker marker: mMarkers) {
                marker.remove();
            }
            mMarkers.clear();
            mGoogleMap.clear();

            if (newText != null || !newText.equals("")) {
                Geocoder geocoder = new Geocoder(FindCourtActivity.this, Locale.getDefault());
                try {
                    addressList = geocoder.getFromLocationName(newText, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    marker.setTitle("Marker");
                    //marker.setSnippet("this is snippet");
                    //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_search_white_24dp));

                    mMarkers.add(marker);

                    //mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                    //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }


            return false;
        }
    };

    @Override
    public void onBackPressed() {

        finish();
    }
}
