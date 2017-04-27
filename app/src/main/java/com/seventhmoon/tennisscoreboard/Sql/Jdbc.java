package com.seventhmoon.tennisscoreboard.Sql;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.PageItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.seventhmoon.tennisscoreboard.FindCourtActivity.myCourtList;
import static com.seventhmoon.tennisscoreboard.FindCourtActivity.pageAdapter;
import static com.seventhmoon.tennisscoreboard.MainMenu.initData;

public class Jdbc {
    private static final String TAG = Jdbc.class.getName();
    private final static String URL = "jdbc:mysql://35.185.153.232/tennis_score_board_db";
    public final static String USERNAME = "tennis_user";
    public final static String PASSWORD = "rk19791023";
    public final static String DRIVER = "com.mysql.jdbc.Driver";

    private static Statement stat = null;
    private static ResultSet rs = null;
    private static Connection con = null;
    private static PreparedStatement pst = null;

    private static String insertdbSQLCourt = "insert into court(name, longitude ,latitude, type, court_usage, light, court_num, if_charge, charge, maintenance, traffic, parking, pic) " +
            "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static String insertdbSQLUserId = "insert into user_id(user_mac, uploaded) " +
            "values(?, ?)";

    private static String updatedbSQLUserId = "UPDATE user_id SET uploaded = ? where user_mac = ?";

    private static String querydbSQLCourt = "select * from court";
    private static String querydbSQLUserId = "select * from user_id";


    private static Context myContext;

    private static double longitude = 0.0;
    private static double latitude = 0.0;

    private String macAddress;
    public static boolean is_query = false;
    public static boolean is_update = false;

    public Jdbc() {
        Log.d(TAG, "Jdbc create");
        //this.myContext = context;
        //this.longitude = longitude;
        //this.latitude = latitude;
        //this.macAddress = macAddress;
        /*new Thread() {
            public void run() {
                Connect();
            }
        };

        sqlTask conTask;
        conTask = new sqlTask();
        conTask.execute(10);*/


    }

    public void setMacAddress(String macAddress) {
        Log.d(TAG, "setMacAddress "+macAddress);
        this.macAddress = macAddress;
    }

    private boolean Connect() {

        Log.d(TAG, "=== Connect start ===");
        boolean ret = false;

        if (con == null) {

            try {
                Class.forName(DRIVER);
                //註冊driver
                con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                if (con != null) {
                    Log.d(TAG, "con = "+con.getClass().getName());
                } else {
                    Log.e(TAG, "con = null");
                }
            }
            catch(ClassNotFoundException e)
            {
                System.out.println("DriverClassNotFound :"+e.toString());
            }//有可能會產生sqlexception
            catch(SQLException x) {
                System.out.println("Exception :"+x.toString());
            }


        } else {
            Log.e(TAG, "Already connected!");
            ret = true;
        }

        Log.d(TAG, "=== Connect end ===");

        return ret;
    }

    private void Close()
    {
        try
        {
            if(rs!=null)
            {
                rs.close();
                rs = null;
            }
            if(stat!=null)
            {
                stat.close();
                stat = null;
            }
            if(pst!=null)
            {
                pst.close();
                pst = null;
            }
        }
        catch(SQLException e)
        {
            System.out.println("Close Exception :" + e.toString());
        }
    }

    public void queryCourtTable(final Context context, final double longitude, final double latitude) {


        if (!is_query) {
            myContext = context;
            myCourtList.clear();
            if (pageAdapter != null)
                pageAdapter.notifyDataSetChanged();

            is_query = true;
            //new Thread() {
            //    public void run() {
                    Log.d(TAG, "=== queryTable start ===");
                    //boolean ret = false;
                    if (con == null) {
                        Log.e(TAG, "Connection = null, we must connect first...");
                        Connect();
                    } else {
                        Log.e(TAG, "Connection = " + con.getClass().getName());
                    }

                    if (con != null) {
                        try {
                            stat = con.createStatement();
                            String query = querydbSQLCourt + " WHERE longitude BETWEEN " + String.valueOf(longitude - 1) + " AND " + String.valueOf(longitude + 1) +
                                    " AND latitude BETWEEN " + String.valueOf(latitude - 1) + " AND " + String.valueOf(latitude + 1);

                            Log.e(TAG, "query = " + query);

                            rs = stat.executeQuery(query);
                            Log.d(TAG, "=== Data Read ===");
                            //name, longitude ,latitude, type, court_num, maintenance, rate, night_play, charge
                            while (rs.next()) {

                                Log.d(TAG, "" + rs.getString("name") + ", " +
                                        rs.getDouble("longitude") + ", " +
                                        rs.getDouble("latitude") + ", " +
                                        rs.getInt("type") + ", " +
                                        rs.getInt("court_usage") + ", " +
                                        rs.getInt("light") + ", " +
                                        rs.getInt("court_num") + ", " +
                                        rs.getInt("if_charge") + ", " +
                                        rs.getString("charge") + ", " +
                                        rs.getFloat("maintenance") + ", " +
                                        rs.getFloat("traffic") + ", " +
                                        rs.getFloat("parking"));

                                PageItem item = new PageItem();
                                item.setName(rs.getString("name"));
                                item.setLongitude(rs.getDouble("longitude"));
                                item.setLatitude(rs.getDouble("latitude"));
                                item.setType(rs.getInt("type"));
                                item.setCourt_usage((byte) rs.getInt("court_usage"));
                                item.setLight((byte) rs.getInt("light"));
                                item.setCourt_num(rs.getInt("court_num"));
                                item.setIfCharge((byte) rs.getInt("if_charge"));
                                item.setCharge(rs.getString("charge"));
                                item.setMaintenance(rs.getFloat("maintenance"));
                                item.setTraffic(rs.getFloat("traffic"));
                                item.setParking(rs.getFloat("parking"));
                                Blob blob = rs.getBlob("pic");



                                Bitmap bp = BitmapFactory.decodeStream(blob.getBinaryStream());
                                //Log.e(TAG, "before compress = "+bp.getByteCount()+" width = "+bp.getWidth()+" height = "+bp.getHeight());

                                Bitmap scaled = Bitmap.createScaledBitmap(bp, bp.getWidth()/2, bp.getHeight()/2, true);

                                //ByteArrayOutputStream out = new ByteArrayOutputStream();
                                //bp.compress(Bitmap.CompressFormat.PNG, 50, out);
                                //Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                                //Log.e(TAG, "after compress = "+scaled.getByteCount());
                                item.setPic(scaled);

                                myCourtList.add(item);


                            }
                            Log.d(TAG, "=== Data Read ===");
                        } catch (SQLException e) {
                            System.out.println("DropDB Exception :" + e.toString());
                        } finally {
                            Close();
                        }
                    }

                    //Intent newNotifyIntent = new Intent(Constants.ACTION.GET_COURT_INFO_COMPLETE);
                    //context.sendBroadcast(newNotifyIntent);
                    Log.d(TAG, "=== queryTable end ===");
            is_query = false;
                //}
            //}.start();

            //sqlQueryCourtTask conTask;
            //conTask = new sqlQueryCourtTask();
            //conTask.execute(10);
        }


    }

    public void queryUserIdTable(final String macAddress) {

        if (!is_query) {
            is_query = true;
            initData.setMatch_mac(false);
            //myCourtList.clear();

            //new Thread() {
            //    public void run() {
                    Log.d(TAG, "=== queryUserIdTable start ===");
                    //boolean ret = false;
                    if (con == null) {
                        Log.e(TAG, "Connection = null, we must connect first...");
                        Connect();
                    } else {
                        Log.e(TAG, "Connection = " + con.getClass().getName());
                    }

                    if (con != null) {
                        try {
                            stat = con.createStatement();
                            String query = querydbSQLUserId + " WHERE user_mac = '" + macAddress + "'";

                            Log.e(TAG, "query = " + query);

                            rs = stat.executeQuery(query);
                            Log.d(TAG, "=== Data Read ===");
                            //name, longitude ,latitude, type, court_num, maintenance, rate, night_play, charge
                            while (rs.next()) {

                                Log.d(TAG, "" + rs.getString("user_mac") + ", " +
                                        rs.getInt("uploaded"));

                                initData.setUpload_remain(rs.getInt("uploaded"));
                                initData.setMatch_mac(true);
                            }
                            Log.d(TAG, "=== Data Read ===");
                        } catch (SQLException e) {
                            System.out.println("DropDB Exception :" + e.toString());
                        } finally {
                            Close();
                        }
                    }

                    //Intent newNotifyIntent = new Intent(Constants.ACTION.GET_COURT_INFO_COMPLETE);
                    //context.sendBroadcast(newNotifyIntent);
                    Log.d(TAG, "=== queryUserIdTable end ===");
                    is_query = false;
                //}
            //}.start();

            //sqlTask conTask;
            //conTask = new sqlTask();
            //conTask.execute(10);

        }
    }

    public PageItem queryCourtTableImage(final Context context, final double longitude, final double latitude) {

        PageItem item = null;

        if (!is_query) {
            myContext = context;

            is_query = true;
            //new Thread() {
            //    public void run() {
            Log.d(TAG, "=== queryTable start ===");
            //boolean ret = false;
            if (con == null) {
                Log.e(TAG, "Connection = null, we must connect first...");
                Connect();
            } else {
                Log.e(TAG, "Connection = " + con.getClass().getName());
            }

            if (con != null) {
                try {
                    stat = con.createStatement();
                    String query = querydbSQLCourt + " WHERE longitude = " + String.valueOf(longitude) +" AND latitude = " + String.valueOf(latitude);

                    Log.e(TAG, "query = " + query);

                    rs = stat.executeQuery(query);
                    Log.d(TAG, "=== Data Read ===");
                    //name, longitude ,latitude, type, court_num, maintenance, rate, night_play, charge
                    while (rs.next()) {

                        Log.d(TAG, "" + rs.getString("name") + ", " +
                                rs.getDouble("longitude") + ", " +
                                rs.getDouble("latitude") + ", " +
                                rs.getInt("type") + ", " +
                                rs.getInt("court_usage") + ", " +
                                rs.getInt("light") + ", " +
                                rs.getInt("court_num") + ", " +
                                rs.getInt("if_charge") + ", " +
                                rs.getString("charge") + ", " +
                                rs.getFloat("maintenance") + ", " +
                                rs.getFloat("traffic") + ", " +
                                rs.getFloat("parking"));

                        item = new PageItem();
                        item.setName(rs.getString("name"));
                        item.setLongitude(rs.getDouble("longitude"));
                        item.setLatitude(rs.getDouble("latitude"));
                        item.setType(rs.getInt("type"));
                        item.setCourt_usage((byte) rs.getInt("court_usage"));
                        item.setLight((byte) rs.getInt("light"));
                        item.setCourt_num(rs.getInt("court_num"));
                        item.setIfCharge((byte) rs.getInt("if_charge"));
                        item.setCharge(rs.getString("charge"));
                        item.setMaintenance(rs.getFloat("maintenance"));
                        item.setTraffic(rs.getFloat("traffic"));
                        item.setParking(rs.getFloat("parking"));
                        Blob blob = rs.getBlob("pic");



                        Bitmap bp = BitmapFactory.decodeStream(blob.getBinaryStream());
                        //Log.e(TAG, "before compress = "+bp.getByteCount()+" width = "+bp.getWidth()+" height = "+bp.getHeight());

                        //Bitmap scaled = Bitmap.createScaledBitmap(bp, bp.getWidth()/2, bp.getHeight()/2, true);

                        //ByteArrayOutputStream out = new ByteArrayOutputStream();
                        //bp.compress(Bitmap.CompressFormat.PNG, 50, out);
                        //Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                        //Log.e(TAG, "after compress = "+scaled.getByteCount());
                        item.setPic(bp);

                        //myCourtList.add(item);


                    }
                    Log.d(TAG, "=== Data Read ===");
                } catch (SQLException e) {
                    System.out.println("DropDB Exception :" + e.toString());
                } finally {
                    Close();
                }
            }

            //Intent newNotifyIntent = new Intent(Constants.ACTION.GET_COURT_INFO_COMPLETE);
            //context.sendBroadcast(newNotifyIntent);
            Log.d(TAG, "=== queryTable end ===");
            is_query = false;
            //}
            //}.start();

            //sqlQueryCourtTask conTask;
            //conTask = new sqlQueryCourtTask();
            //conTask.execute(10);
        }

        return item;
    }

    public void insertTableCourt(final String name, final String longitude, final String latitude, final String type, final String usage, final String light,
                                 final String court_num, final String ifCharge, final String charge, final String maintenance, final String traffic, final String parking, final byte[] blob)
    {
        if (!is_update) {
            is_update = true;
            //new Thread() {
            //    public void run() {
            Log.d(TAG, "=== insertTable start ===");
            //boolean ret = false;
            if (con == null) {
                Log.e(TAG, "Connection = null, we must connect first...");
                Connect();
            } else {
                Log.e(TAG, "Connection = " + con.getClass().getName());
            }

            if (con != null) {

                try {
                    pst = con.prepareStatement(insertdbSQLCourt);

                    pst.setString(1, name); //court name
                    pst.setString(2, longitude);
                    pst.setString(3, latitude);
                    pst.setString(4, type); //hard, grass, clay
                    pst.setString(5, usage); //boolean
                    pst.setString(6, light); //boolean
                    pst.setString(7, court_num); //int
                    pst.setString(8, ifCharge); //if charge
                    pst.setString(9, charge); //string
                    pst.setString(10, maintenance); //
                    pst.setString(11, traffic);
                    pst.setString(12, parking);
                    pst.setBytes(13, blob);

                    pst.executeUpdate();

                } catch (SQLException e) {
                    System.out.println("InsertDB Exception :" + e.toString());
                } finally {
                    Close();
                }
            }
            Log.d(TAG, "=== insertTable end ===");

            //}
            //}.start();

            //sqlQueryCourtTask conTask;
            //conTask = new sqlQueryCourtTask();
            //conTask.execute(10);
            is_update = false;
        }
    }

    public void insertTableUserId(final String mac, final String upload)
    {
        new Thread() {
            public void run() {
                Log.d(TAG, "=== insertTable start ===");
                //boolean ret = false;
                if (con == null) {
                    Log.e(TAG, "Connection = null, we must connect first...");
                    Connect();
                } else {
                    Log.e(TAG, "Connection = "+con.getClass().getName());
                }

                if (con != null) {

                    try {
                        pst = con.prepareStatement(insertdbSQLUserId);

                        pst.setString(1, mac); //court name
                        pst.setString(2, upload);


                        pst.executeUpdate();

                    } catch (SQLException e) {
                        System.out.println("InsertDB Exception :" + e.toString());
                    } finally {
                        Close();
                    }
                }
                Log.d(TAG, "=== insertTable end ===");

            }
        }.start();

        //sqlTask conTask;
        //conTask = new sqlTask();
        //conTask.execute(10);


    }

    public void updateTableUserId(final String mac, final String upload)
    {
        Log.d(TAG, "=== updateTable start === mac = "+mac+" upload = "+upload);
        //boolean ret = false;

        if (!is_update) {
            is_update = true;
            if (con == null) {
                Log.e(TAG, "Connection = null, we must connect first...");
                Connect();
            } else {
                Log.e(TAG, "Connection = " + con.getClass().getName());
            }

            if (con != null) {

                try {
                    pst = con.prepareStatement(updatedbSQLUserId);

                    pst.setInt(1, Integer.valueOf(upload));
                    pst.setString(2, mac);


                    pst.executeUpdate();

                } catch (SQLException e) {
                    System.out.println("updateDB Exception :" + e.toString());
                } finally {
                    Close();
                }
            }
            is_update = false;
        }

        Log.d(TAG, "=== updateTable end ===");

        //sqlTask conTask;
        //conTask = new sqlTask();
        //conTask.execute(10);


    }
}
