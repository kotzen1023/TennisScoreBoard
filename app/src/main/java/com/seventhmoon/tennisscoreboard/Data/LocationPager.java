package com.seventhmoon.tennisscoreboard.Data;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.seventhmoon.tennisscoreboard.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationPager extends PagerAdapter {
    private static final String TAG = LocationPager.class.getName();

    Context context;
    //LayoutInflater inflater;

    private LayoutInflater inflater = null;

    private int layoutResourceId;
    private ArrayList<PageItem> items = new ArrayList<>();
    ArrayList<ShowItem> showItemArrayList = new ArrayList<>();

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
        ListView listView;


        Log.i(TAG, "get position = "+position);

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        // Locate the TextViews in viewpager_item.xml
        //textViewName = (TextView) itemView.findViewById(R.id.textView1);
        //textViewCharge = (TextView) itemView.findViewById(R.id.textView2);
        //textViewMaintain = (TextView) itemView.findViewById(R.id.textView3);
        listView = (ListView) itemView.findViewById(R.id.listViewCourt);
        imgPic = (ImageView) itemView.findViewById(R.id.imgViewPic);

        // Capture position and set to the TextViews
        //Log.e(TAG, "image height = "+imgPic.getDrawable().getIntrinsicHeight()+" width = "+imgPic.getDrawable().getIntrinsicWidth());

        if (items.size() > 0) {

            if (position == getCount() - 1) {
                Log.d(TAG, "<last>");
                imgPic.setImageBitmap(items.get(0).getPic());
                //textViewName.setText(items.get(0).getName());
                //textViewCharge.setText(items.get(0).getCharge());
                //textViewMaintain.setText(items.get(0).getCourt_usage());

                showItemArrayList.clear();
                ShowItem courtName = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(0).getName(), 0);
                showItemArrayList.add(courtName);

                ShowItem courtType = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(0).getType()), 0);
                showItemArrayList.add(courtType);

                ShowItem courtUsage = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(0).getCourt_usage()), 0);
                showItemArrayList.add(courtUsage);

                ShowItem courtLight = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(0).getLight()), 0);
                showItemArrayList.add(courtLight);

                ShowItem courtNum = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(0).getCourt_num()), 0);
                showItemArrayList.add(courtNum);

                ShowItem courtIfCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(0).getIfCharge()), 0);
                showItemArrayList.add(courtIfCharge);

                ShowItem courtCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(0).getCharge()), 0);
                showItemArrayList.add(courtCharge);

                ShowItem courtMaintain = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(0).getMaintenance());
                showItemArrayList.add(courtMaintain);

                ShowItem courtTraffic = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(0).getTraffic());
                showItemArrayList.add(courtTraffic);

                ShowItem courtParking = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(0).getParking());
                showItemArrayList.add(courtParking);

                ShowItemAdapter showItemAdapter= new ShowItemAdapter(context,R.layout.court_show_item,showItemArrayList);
                listView.setAdapter(showItemAdapter);

            } else if (position == 0) {
                Log.d(TAG, "<first>");
                imgPic.setImageBitmap(items.get(items.size() - 1).getPic());
                //textViewName.setText(items.get(items.size() - 1).getName());
                //textViewCharge.setText(items.get(items.size() - 1).getCharge());
                //textViewMaintain.setText(items.get(items.size() - 1).getCourt_usage());

                showItemArrayList.clear();
                ShowItem courtName = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(items.size() - 1).getName(), 0);
                showItemArrayList.add(courtName);

                ShowItem courtType = new ShowItem(context.getResources().getString(R.string.add_court_header_type), String.valueOf(items.get(items.size() - 1).getType()), 0);
                showItemArrayList.add(courtType);

                ShowItem courtUsage = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(items.size() - 1).getCourt_usage()), 0);
                showItemArrayList.add(courtUsage);

                ShowItem courtLight = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(items.size() - 1).getLight()), 0);
                showItemArrayList.add(courtLight);

                ShowItem courtNum = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(items.size() - 1).getCourt_num()), 0);
                showItemArrayList.add(courtNum);

                ShowItem courtIfCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(items.size() - 1).getIfCharge()), 0);
                showItemArrayList.add(courtIfCharge);

                ShowItem courtCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(items.size() - 1).getCharge()), 0);
                showItemArrayList.add(courtCharge);

                ShowItem courtMaintain = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(items.size() - 1).getMaintenance());
                showItemArrayList.add(courtMaintain);

                ShowItem courtTraffic = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(items.size() - 1).getTraffic());
                showItemArrayList.add(courtTraffic);

                ShowItem courtParking = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(items.size() - 1).getParking());
                showItemArrayList.add(courtParking);

                ShowItemAdapter showItemAdapter= new ShowItemAdapter(context,R.layout.court_show_item,showItemArrayList);
                listView.setAdapter(showItemAdapter);
            } else {
                Log.d(TAG, "<normal>");
                imgPic.setImageBitmap(items.get(position - 1).getPic());
                //textViewName.setText(items.get(position - 1).getName());
                //textViewCharge.setText(items.get(position - 1).getCharge());
                //textViewMaintain.setText(items.get(position - 1).getCourt_usage());

                showItemArrayList.clear();
                ShowItem courtName = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(position - 1).getName(), 0);
                showItemArrayList.add(courtName);

                ShowItem courtType = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(position - 1).getType()), 0);
                showItemArrayList.add(courtType);

                ShowItem courtUsage = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(position - 1).getCourt_usage()), 0);
                showItemArrayList.add(courtUsage);

                ShowItem courtLight = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(position - 1).getLight()), 0);
                showItemArrayList.add(courtLight);

                ShowItem courtNum = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(position - 1).getCourt_num()), 0);
                showItemArrayList.add(courtNum);

                ShowItem courtIfCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(position - 1).getIfCharge()), 0);
                showItemArrayList.add(courtIfCharge);

                ShowItem courtCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(position - 1).getCharge()), 0);
                showItemArrayList.add(courtCharge);

                ShowItem courtMaintain = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(position - 1).getMaintenance());
                showItemArrayList.add(courtMaintain);

                ShowItem courtTraffic = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(position - 1).getTraffic());
                showItemArrayList.add(courtTraffic);

                ShowItem courtParking = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(position - 1).getParking());
                showItemArrayList.add(courtParking);

                ShowItemAdapter showItemAdapter= new ShowItemAdapter(context,R.layout.court_show_item,showItemArrayList);
                listView.setAdapter(showItemAdapter);
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

    private String getCourtType(int type) {
        String court_type;
        switch (type) {
            case 0:
                court_type = context.getResources().getString(R.string.court_type_hard);
                break;
            case 1:
                court_type = context.getResources().getString(R.string.court_type_grass);
                break;
            case 2:
                court_type = context.getResources().getString(R.string.court_type_clay);
                break;
            case 3:
                court_type = context.getResources().getString(R.string.court_type_hard) + ", " +context.getResources().getString(R.string.court_type_grass);
                break;
            case 4:
                court_type = context.getResources().getString(R.string.court_type_hard) + ", " +context.getResources().getString(R.string.court_type_clay);
                break;
            case 5:
                court_type = context.getResources().getString(R.string.court_type_grass) + ", " +context.getResources().getString(R.string.court_type_clay);
                break;
            case 6:
                court_type = context.getResources().getString(R.string.court_type_all);
                break;

            default:
                court_type = context.getResources().getString(R.string.court_type_hard);
                break;
        }

        return court_type;
    }

    private String getCourtUsage(int usage) {
        String court_usage;
        switch (usage) {
            case 0:
                court_usage = context.getResources().getString(R.string.court_usage_public);
                break;
            case 1:
                court_usage = context.getResources().getString(R.string.court_usage_private);
                break;


            default:
                court_usage = context.getResources().getString(R.string.court_usage_public);
                break;
        }

        return court_usage;
    }

    private String getCourtLight(int light) {
        String court_light;
        switch (light) {
            case 0:
                court_light = context.getResources().getString(R.string.court_light_all);
                break;
            case 1:
                court_light = context.getResources().getString(R.string.court_light_some);
                break;
            case 2:
                court_light = context.getResources().getString(R.string.court_light_no);
                break;

            default:
                court_light = context.getResources().getString(R.string.court_light_all);
                break;
        }

        return court_light;
    }

    private String getCourtCharge(int charge) {
        String court_charge;
        switch (charge) {
            case 0:
                court_charge = context.getResources().getString(R.string.court_charge_free);
                break;
            case 1:
                court_charge = context.getResources().getString(R.string.court_charge_charge);
                break;


            default:
                court_charge = context.getResources().getString(R.string.court_charge_free);
                break;
        }

        return court_charge;
    }
}
