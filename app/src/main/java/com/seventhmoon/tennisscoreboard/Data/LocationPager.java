package com.seventhmoon.tennisscoreboard.Data;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tennisscoreboard.R;

import java.util.ArrayList;


public class LocationPager extends PagerAdapter {
    private static final String TAG = LocationPager.class.getName();

    Context context;
    //LayoutInflater inflater;

    private LayoutInflater inflater = null;

    private int layoutResourceId;
    private ArrayList<PageItem> items = new ArrayList<>();

    public LocationPager(Context context,
                         ArrayList<PageItem> objects) {
        //super(context, textViewResourceId, objects);
        this.context = context;

        this.items = objects;
        //inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return items.size()+2;
    }

    public PageItem getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Declare Variables
        ImageView imgPic;
        TextView textViewName;
        TextView textViewCharge;
        TextView textViewMaintain;


        Log.i(TAG, "get position = "+position);

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        // Locate the TextViews in viewpager_item.xml
        textViewName = (TextView) itemView.findViewById(R.id.textView1);
        textViewCharge = (TextView) itemView.findViewById(R.id.textView2);
        textViewMaintain = (TextView) itemView.findViewById(R.id.textView3);
        imgPic = (ImageView) itemView.findViewById(R.id.imgViewPic);

        // Capture position and set to the TextViews

        if (items.size() > 0) {

            if (position == getCount() - 1) {
                Log.d(TAG, "<last>");
                imgPic.setImageBitmap(items.get(0).getPic());
                textViewName.setText(items.get(0).getName());
                textViewCharge.setText(items.get(0).getCharge());
                //textViewMaintain.setText(items.get(0).getCourt_usage());
            } else if (position == 0) {
                Log.d(TAG, "<first>");
                imgPic.setImageBitmap(items.get(items.size() - 1).getPic());
                textViewName.setText(items.get(items.size() - 1).getName());
                textViewCharge.setText(items.get(items.size() - 1).getCharge());
                //textViewMaintain.setText(items.get(items.size() - 1).getCourt_usage());
            } else {
                Log.d(TAG, "<normal>");
                imgPic.setImageBitmap(items.get(position - 1).getPic());
                textViewName.setText(items.get(position - 1).getName());
                textViewCharge.setText(items.get(position - 1).getCharge());
                //textViewMaintain.setText(items.get(position - 1).getCourt_usage());
            }
        }



        // Locate the ImageView in viewpager_item.xml
        //imgPic = (ImageView) itemView.findViewById(R.id.flag);
        // Capture position and set to the ImageView
        //imgPic.setImageResource(flag[position]);

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
