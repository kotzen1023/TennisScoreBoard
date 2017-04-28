package com.seventhmoon.tennisscoreboard;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.seventhmoon.tennisscoreboard.Data.Constants;
import com.seventhmoon.tennisscoreboard.Data.PageItem;

import com.seventhmoon.tennisscoreboard.Service.GetCourtImageService;


public class FullScreenView extends AppCompatActivity {
    private static final String TAG = FullScreenView.class.getName();

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    private ImageView imageView;
    //ProgressDialog loadDialog = null;
    public static PageItem pageItem;
    ProgressDialog loadDialog = null;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    int view_width;
    int view_height;
    int bitmap_width;
    int bitmap_height;

    public boolean modify = false;
    public boolean touch = false;
    public float touch_x = 0;
    public float touch_y = 0;

    private float matrix_center_x = 0;
    private float matrix_center_y = 0;

    private float current_degree = 0;
    private float current_width = 0;
    private float current_height = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_view);

        Intent intent = getIntent();

        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");

        imageView = (ImageView) findViewById(R.id.imageViewFullScreenView);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView view = (ImageView) v;
                //dumpEvent(event);

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        if (!touch) {

                            //view.measure(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                            view_width = view.getWidth();
                            view_height = view.getHeight();
                            Log.i("ImageAdapter", "display-width=" + view.getWidth() + ", display-height=" + view.getHeight()+", bitmap-width=" + bitmap_width + ", bitmap-height=" + bitmap_height);
                            RectF drawableRect = new RectF(0, 0, bitmap_width, bitmap_height);
                            RectF viewRect = new RectF(0, 0, view_width, view_height);
                            matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);


                            view.setScaleType(ImageView.ScaleType.MATRIX);

                            touch = true;
                        }
                        savedMatrix.set(matrix);

                        touch_x = event.getX();
                        touch_y = event.getY();


                        start.set(event.getX(), event.getY());

                        Log.i("ImageAdapter", "mode=DRAG");

                        mode = DRAG;

                        break;


                    case MotionEvent.ACTION_POINTER_DOWN:

                        oldDist = spacing(event);

                        Log.i("ImageAdapter", "oldDist=" + oldDist);

                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                            Log.i("ImageAdapter", "mode=ZOOM");
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                    case MotionEvent.ACTION_POINTER_UP:

                        mode = NONE;
                        Log.i("ImageAdapter", "mode=NONE");

                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (mode == DRAG) {
                            Log.i("ImageAdapter", "Drag Move(X="+start.x+",Y="+start.y+") to (X="+event.getX()+
                                    ", Y="+event.getY());
                            // ...
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - start.x, event.getY()
                                    - start.y);


                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            Log.i("ImageAdapter", "newDist=" + newDist);

                            //Log.e("ImageAdapter", "width="++", height="+view.getMeasuredHeight());

                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                        }

                        break;

                }

                getCurrentCenter(matrix);

                view.setImageMatrix(matrix);

                return true;
            }
        });

        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_COURT_IMAGE_COMPLETE)) {
                    Log.d(TAG, "receive brocast !");

                    if (pageItem != null) {

                        Bitmap bitmap = pageItem.getPic();
                        bitmap_width = bitmap.getWidth();
                        bitmap_height = bitmap.getHeight();
                        Log.e(TAG, "bitmap_width = "+bitmap_width+", bitmap_height = "+bitmap_height);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setImageBitmap(bitmap);

                        //imageView.setImageBitmap(pageItem.getPic());


                    } else {
                        toast("Can't read Image!");
                    }
                    loadDialog.dismiss();
                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.GET_COURT_IMAGE_COMPLETE);
            registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }

        if (Double.valueOf(longitude) != 0.0 && Double.valueOf(latitude) != 0.0) {
            //initData.jdbc.queryCourtTable(context, longitude, latitude);

            Intent checkIntent = new Intent(FullScreenView.this, GetCourtImageService.class);
            checkIntent.putExtra("longitude", longitude);
            checkIntent.putExtra("latitude", latitude);
            startService(checkIntent);
        }

        loadDialog = new ProgressDialog(FullScreenView.this);
        loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadDialog.setTitle(getResources().getString(R.string.loading));
        loadDialog.setIndeterminate(false);
        loadDialog.setCancelable(false);

        loadDialog.show();
    }

    @Override
    protected void onPause() {
        //Data.LockSerivce = true;

        super.onPause();

    }

    @Override
    protected void onResume() {

        /*if (Data.LockSerivce)
        {

            Intent intent = new Intent(FullscreenViewer.this, Nfc_read.class);
            startActivity(intent);
        }*/

        super.onResume();

    }

    @Override
    protected void onDestroy() {
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
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.fullscreen_menu, menu);

        //MenuItem item_left, item_right;

        //item_left = menu.findItem(R.id.action_rotate_left);
        //item_right = menu.findItem(R.id.action_rotate_right);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int currentItem = viewPager.getCurrentItem();
        //ImageView imageView;
        float degree;
        switch (item.getItemId()) {
            // action with ID action_refresh was selected

            case R.id.action_rotate_left:
                degree = -90;
                current_degree += degree;
                if (current_degree == -360.0)
                    current_degree = 0;

                Log.i(TAG, "onRotate click, degree = "+String.valueOf(current_degree));

                //int currentItem = viewPager.getCurrentItem();
                //Log.i(TAG, "current item[" + currentItem + "]");

                //imageView = (ImageView) viewPager.findViewWithTag("myView"+currentItem);


                Log.i(TAG, "image width = " + imageView.getDrawable().getBounds().width() + ", height = " + imageView.getDrawable().getBounds().height());
                //Log.i(TAG, "image display witdh = " + imageView.getDrawable().getIntrinsicWidth() + ", height = " + imageView.getDrawable().getIntrinsicHeight());
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                //Matrix matrix = new Matrix();

                Log.i(TAG, "width=" + imageView.getWidth() + ", Height=" + imageView.getHeight());


                if (touch) { //image was been touch
                    if (touch_x == 0 && touch_y ==0)
                    {
                        matrix.postRotate(degree, imageView.getWidth() / 2, imageView.getHeight() / 2);
                    }
                    else {
                        matrix.postRotate(degree, matrix_center_x, matrix_center_y);
                    }

                    getCurrentCenter(matrix);
                }
                else //image was not touch
                {
                    //if (adapter.touch == false) {

                    RectF drawableRect = new RectF(0, 0, imageView.getDrawable().getBounds().width(), imageView.getDrawable().getBounds().height());
                    RectF viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());
                    matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
                    savedMatrix.set(matrix);
                    touch = true;
                    //}

                    matrix.postRotate(degree, imageView.getWidth() / 2, imageView.getHeight() / 2);
                    //adapter.matrix.postTranslate(imageView.getWidth() / 2, imageView.getHeight() / 2);
                }

                //Log.i(TAG, "scale_x = " + String.valueOf(scale_x) + ", scale_y = " + String.valueOf(scale_y));

                imageView.setImageMatrix(matrix);
                break;
            case R.id.action_rotate_right:


                degree = 90;
                current_degree += degree;
                if (current_degree == 360.0)
                    current_degree = 0;

                Log.i(TAG, "onRotate click, degree = "+String.valueOf(current_degree));

                //int currentItem = viewPager.getCurrentItem();
                //Log.i(TAG, "current item[" + currentItem + "]");

                //imageView = (ImageView) viewPager.findViewWithTag("myView"+currentItem);


                Log.i(TAG, "image width = " + imageView.getDrawable().getBounds().width() + ", height = " + imageView.getDrawable().getBounds().height());
                //Log.i(TAG, "image display witdh = " + imageView.getDrawable().getIntrinsicWidth() + ", height = " + imageView.getDrawable().getIntrinsicHeight());
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                //Matrix matrix = new Matrix();

                Log.i(TAG, "width=" + imageView.getWidth() + ", Height=" + imageView.getHeight());


                if (touch) { //image was been touch
                    if (touch_x == 0 && touch_y ==0)
                    {
                        matrix.postRotate(degree, imageView.getWidth() / 2, imageView.getHeight() / 2);
                    }
                    else {
                        matrix.postRotate(degree, matrix_center_x, matrix_center_y);
                    }

                    getCurrentCenter(matrix);
                }
                else //image was not touch
                {
                    //if (adapter.touch == false) {

                    RectF drawableRect = new RectF(0, 0, imageView.getDrawable().getBounds().width(), imageView.getDrawable().getBounds().height());
                    RectF viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());
                    matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
                    savedMatrix.set(matrix);
                    touch = true;
                    //}
                    matrix.postRotate(degree, imageView.getWidth() / 2, imageView.getHeight() / 2);
                    //adapter.matrix.postTranslate(imageView.getWidth() / 2, imageView.getHeight() / 2);
                }

                //Log.i(TAG, "scale_x = " + String.valueOf(scale_x) + ", scale_y = " + String.valueOf(scale_y));

                imageView.setImageMatrix(matrix);
                break;
            case android.R.id.home:


                finish();
                break;
            default:
                break;
        }

        return true;
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void getCurrentCenter(Matrix matrix) {
        float[] values = new float[9];

        matrix.getValues(values);

        Log.e(TAG, "[0]="+values[0]+" [1]="+values[1]+" [2]="+values[2]+
                " [3]="+values[3]+" [4]="+values[4]+" [5]="+values[5]+" [6]="+values[6]+" [7]="+values[7]+" [8]"+values[8]);

        if (current_degree == 0.0) {
            current_width = values[0] * bitmap_width;
            current_height = values[4] * bitmap_height;

            matrix_center_x = values[2] + current_width / 2;
            matrix_center_y = values[5] + current_height / 2;
        } else if (current_degree == 90.0 || current_degree == -270.0) {
            current_width = values[3]* bitmap_width;
            current_height = values[3]* bitmap_height;

            matrix_center_x = values[2] - current_height / 2;
            matrix_center_y = values[5] + current_width / 2;
        } else if (current_degree == 180.0 || current_degree == -180.0) {
            current_width = -(values[0]* bitmap_width);
            current_height = -(values[4]* bitmap_height);

            matrix_center_x = values[2] - current_width / 2;
            matrix_center_y = values[5] - current_height / 2;
        } else if (current_degree == 270.0 || current_degree == -90.0) {
            current_width = values[1]* bitmap_width;
            current_height = values[1]* bitmap_height;

            matrix_center_x = values[2] + current_height / 2;
            matrix_center_y = values[5] - current_width / 2;
        }

        Log.e(TAG, "x="+values[2]+", y="+values[5]+" current_width="+current_width+", current_height="+current_height);
    }
}
