package com.seventhmoon.tennisscoreboard.Sql;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.PageItem;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.seventhmoon.tennisscoreboard.FindCourtActivity.myCourtList;
import static com.seventhmoon.tennisscoreboard.FindCourtActivity.pageAdapter;

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

    private static String insertdbSQLCourt = "insert into court(name, longitude ,latitude, type, court_usage, light, court_num, charge, maintenance, traffic, parking, pic) " +
            "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static String insertdbSQLUserId = "insert into user_id(user_mac, uploaded) " +
            "values(?, ?)";

    private static String querydbSQLCourt = "select * from court";
    private static String querydbSQLUserId = "select * from user_id";
    private static Context context;

    private static double longitude = 0.0;
    private static double latitude = 0.0;

    private static String macAddress;

    public Jdbc(Context context, double longitude, double latitude, String macAddress) {
        Log.d(TAG, "Jdbc create");
        this.context = context;
        this.longitude = longitude;
        this.latitude = latitude;
        this.macAddress = macAddress;
        /*new Thread() {
            public void run() {
                Connect();
            }
        };

        sqlTask conTask;
        conTask = new sqlTask();
        conTask.execute(10);*/


    }

    private static boolean Connect() {

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

    private static void Close()
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

    public static void queryCourtTable() {

        myCourtList.clear();

        new Thread() {
            public void run() {
                Log.d(TAG, "=== queryTable start ===");
                //boolean ret = false;
                if (con == null) {
                    Log.e(TAG, "Connection = null, we must connect first...");
                    Connect();
                } else {
                    Log.e(TAG, "Connection = " + con.getClass().getName());
                }

                if (con != null) {
                    try
                    {
                        stat = con.createStatement();
                        String query = querydbSQLCourt + " WHERE longitude BETWEEN "+ String.valueOf(longitude-1)+" AND "+String.valueOf(longitude+1)+
                                " AND latitude BETWEEN "+ String.valueOf(latitude-1)+" AND "+String.valueOf(latitude+1);

                        Log.e(TAG, "query = "+query);

                        rs = stat.executeQuery(query);
                        Log.d(TAG, "=== Data Read ===");
                        //name, longitude ,latitude, type, court_num, maintenance, rate, night_play, charge
                        while(rs.next())
                        {

                            Log.d(TAG, ""+rs.getString("name")+", "+
                                    rs.getDouble("longitude")+", "+
                                    rs.getDouble("latitude")+", "+
                                    rs.getInt("type")+", "+
                                    rs.getInt("court_usage")+", "+
                                    rs.getInt("light")+", "+
                                    rs.getInt("court_num")+", "+
                                    rs.getString("charge")+", "+
                                    rs.getFloat("maintenance")+", "+
                                    rs.getFloat("traffic")+", "+
                                    rs.getFloat("parking"));

                            PageItem item = new PageItem();
                            item.setName(rs.getString("name"));
                            item.setLongitude(rs.getDouble("longitude"));
                            item.setLatitude(rs.getDouble("latitude"));
                            item.setType(rs.getInt("type"));
                            item.setCourt_usage((byte) rs.getInt("court_usage"));
                            item.setLight((byte) rs.getInt("light"));
                            item.setCourt_num(rs.getInt("court_num"));
                            item.setCharge(rs.getString("charge"));
                            item.setMaintenance(rs.getFloat("maintenance"));
                            item.setTraffic(rs.getFloat("traffic"));
                            item.setParking(rs.getFloat("parking"));
                            Blob blob = rs.getBlob("pic");
                            Bitmap bp = BitmapFactory.decodeStream(blob.getBinaryStream());
                            item.setPic(bp);

                            myCourtList.add(item);


                        }
                        Log.d(TAG, "=== Data Read ===");
                    }
                    catch(SQLException e)
                    {
                        System.out.println("DropDB Exception :" + e.toString());
                    }
                    finally
                    {
                        Close();
                    }
                }

                Intent newNotifyIntent = new Intent(Constants.ACTION.GET_COURT_INFO_COMPLETE);
                context.sendBroadcast(newNotifyIntent);
                Log.d(TAG, "=== queryTable end ===");
            }
        }.start();

        sqlTask conTask;
        conTask = new sqlTask();
        conTask.execute(10);


    }

    public static void queryUserIdTable() {

        myCourtList.clear();

        new Thread() {
            public void run() {
                Log.d(TAG, "=== queryTable start ===");
                //boolean ret = false;
                if (con == null) {
                    Log.e(TAG, "Connection = null, we must connect first...");
                    Connect();
                } else {
                    Log.e(TAG, "Connection = " + con.getClass().getName());
                }

                if (con != null) {
                    try
                    {
                        stat = con.createStatement();
                        String query = querydbSQLUserId + "WHERE user_mac = '"+macAddress+"'";

                        Log.e(TAG, "query = "+query);

                        rs = stat.executeQuery(query);
                        Log.d(TAG, "=== Data Read ===");
                        //name, longitude ,latitude, type, court_num, maintenance, rate, night_play, charge
                        while(rs.next())
                        {

                            Log.d(TAG, ""+rs.getString("user_mac")+", "+
                                    rs.getInt("uploaded"));

                            /*PageItem item = new PageItem();
                            item.setName(rs.getString("name"));
                            item.setLongitude(rs.getDouble("longitude"));
                            item.setLatitude(rs.getDouble("latitude"));
                            item.setType(rs.getInt("type"));
                            item.setCourt_usage((byte) rs.getInt("court_usage"));
                            item.setLight((byte) rs.getInt("light"));
                            item.setCourt_num(rs.getInt("court_num"));
                            item.setCharge(rs.getString("charge"));
                            item.setMaintenance(rs.getFloat("maintenance"));
                            item.setTraffic(rs.getFloat("traffic"));
                            item.setParking(rs.getFloat("parking"));
                            Blob blob = rs.getBlob("pic");
                            Bitmap bp = BitmapFactory.decodeStream(blob.getBinaryStream());
                            item.setPic(bp);

                            myCourtList.add(item);*/

                        }
                        Log.d(TAG, "=== Data Read ===");
                    }
                    catch(SQLException e)
                    {
                        System.out.println("DropDB Exception :" + e.toString());
                    }
                    finally
                    {
                        Close();
                    }
                }

                Intent newNotifyIntent = new Intent(Constants.ACTION.GET_COURT_INFO_COMPLETE);
                context.sendBroadcast(newNotifyIntent);
                Log.d(TAG, "=== queryTable end ===");
            }
        }.start();

        sqlTask conTask;
        conTask = new sqlTask();
        conTask.execute(10);


    }


    public static void insertTableCourt(final String name, final String longitude, final String latitude, final String type, final String usage, final String court_num,
                                   final String night_play, final String charge, final String maintenance, final String traffic, final String parking, final byte[] blob)
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
                        pst = con.prepareStatement(insertdbSQLCourt);

                        pst.setString(1, name); //court name
                        pst.setString(2, longitude);
                        pst.setString(3, latitude);
                        pst.setString(4, type); //hard, grass, clay
                        pst.setString(5, usage); //boolean
                        pst.setString(6, court_num); //int
                        pst.setString(7, night_play); //boolean
                        pst.setString(8, charge); //string
                        pst.setString(9, maintenance); //
                        pst.setString(10, traffic);
                        pst.setString(11, parking);
                        pst.setBytes(12, blob);

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

        sqlTask conTask;
        conTask = new sqlTask();
        conTask.execute(10);


    }



    private static class sqlTask extends AsyncTask<Integer, Integer, String>
    {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        //int nowCount;
        @Override
        protected String doInBackground(Integer... countTo) {

            // 再背景中處理的耗時工作
            /*try {
                while(Data.pass_count<selected_names.size()){

                    //nowCount = i + 1;
                    publishProgress(Data.pass_count+1);
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "10";*/
            /*while(decrypting) {
                try {

                    long percent = 0;
                    if (Data.current_file_size > 0)
                        percent = (Data.complete_file_size * 100)/Data.current_file_size;

                    publishProgress((int)percent);
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }*/

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*loadDialog = new ProgressDialog(PhotoList.this);
            loadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            loadDialog.setTitle(R.string.photolist_decrypting_title);
            loadDialog.setProgress(0);
            loadDialog.setMax(100);
            loadDialog.setIndeterminate(false);
            loadDialog.setCancelable(false);

            loadDialog.show();*/
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);
            // 背景工作處理"中"更新的事
            /*long percent = 0;
            if (Data.current_file_size > 0)
                percent = (Data.complete_file_size * 100)/Data.current_file_size;

            decryptDialog.setMessage(getResources().getString(R.string.photolist_decrypting_files) + "(" + values[0] + "/" + selected_names.size() + ") " + percent + "%\n" + selected_names.get(values[0] - 1));
            */
            /*if (Data.OnDecompressing) {
                loadDialog.setTitle(getResources().getString(R.string.decompressing_files_title) + " " + Data.CompressingFileName);
                loadDialog.setProgress(values[0]);
            } else if (Data.OnDecrypting) {
                loadDialog.setTitle(getResources().getString(R.string.decrypting_files_title) + " " + Data.EnryptingOrDecryptingFileName);
                loadDialog.setProgress(values[0]);
            } else {
                loadDialog.setMessage(getResources().getString(R.string.decrypting_files_title));
            }*/
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            //send broadcast

            //loadDialog.dismiss();
            /*btnDecrypt.setVisibility(View.INVISIBLE);
            btnShare.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.INVISIBLE);
            selected_count = 0;*/
        }

        @Override
        protected void onCancelled() {

            super.onCancelled();
        }
    }
}
