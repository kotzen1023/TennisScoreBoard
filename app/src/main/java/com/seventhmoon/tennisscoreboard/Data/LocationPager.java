package com.seventhmoon.tennisscoreboard.Data;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.seventhmoon.tennisscoreboard.FullScreenView;
import com.seventhmoon.tennisscoreboard.MainActivity;
import com.seventhmoon.tennisscoreboard.MainMenu;
import com.seventhmoon.tennisscoreboard.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.seventhmoon.tennisscoreboard.FindCourtActivity.currentPage;
import static com.seventhmoon.tennisscoreboard.FindCourtActivity.is_init;

import static com.seventhmoon.tennisscoreboard.FindCourtActivity.is_markOther;
import static com.seventhmoon.tennisscoreboard.FindCourtActivity.is_setFirst;
import static com.seventhmoon.tennisscoreboard.FindCourtActivity.is_setLast;

import static com.seventhmoon.tennisscoreboard.FindCourtActivity.mark_select;
import static com.seventhmoon.tennisscoreboard.FindCourtActivity.set_count;



public class LocationPager extends PagerAdapter {
    private static final String TAG = LocationPager.class.getName();

    Context context;
    //LayoutInflater inflater;

    private LayoutInflater inflater = null;

    private int layoutResourceId;
    private ArrayList<PageItem> items = new ArrayList<>();
    ArrayList<ShowItem> showItemArrayList = new ArrayList<>();
    ArrayList<ShowItem> currentArrayList = new ArrayList<>();
    ArrayList<ShowItem> preArrayList = new ArrayList<>();
    ArrayList<ShowItem> nextArrayList = new ArrayList<>();

    ArrayList<ShowItem> tempArrayList = new ArrayList<>();

    ShowItemAdapter prevAdapter;
    ShowItemAdapter currentAdapter;
    ShowItemAdapter nextAdapter;

    ShowItemAdapter tempAdapter;


    private ListView currentListView;
    private ListView preListView;
    private ListView nextListView;

    private ListView tempListView;

    private boolean init = false;
    private int prev_position = -1;


    public LocationPager(Context context,
                         ArrayList<PageItem> objects) {
        //super(context, textViewResourceId, objects);
        this.context = context;

        this.items = objects;
        //inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.init = true;
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
    public Object instantiateItem(ViewGroup container, final int position) {
        // Declare Variables
        ImageView imgPic;
        TextView textViewName;
        TextView textViewCharge;
        TextView textViewMaintain;
        //ListView listView;

        Log.d(TAG, "=======================================================");
        Log.i(TAG, "position = "+position+", prev_position = "+prev_position);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container, false);



        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "position " + position);

                if (position >= 1) {

                    Log.d(TAG, "position " + position + " image onClick, longitude = " + items.get(position - 1).getLongitude() + " latitude = " + items.get(position - 1).getLatitude());


                    Intent intent = new Intent(context, FullScreenView.class);
                    intent.putExtra("longitude", String.valueOf(items.get(position - 1).getLongitude()));
                    intent.putExtra("latitude", String.valueOf(items.get(position - 1).getLatitude()));
                    context.startActivity(intent);
                }
            }
        });*/

        // Locate the TextViews in viewpager_item.xml
        //textViewName = (TextView) itemView.findViewById(R.id.textView1);
        //textViewCharge = (TextView) itemView.findViewById(R.id.textView2);
        //textViewMaintain = (TextView) itemView.findViewById(R.id.textView3);
        final ListView listView = (ListView) itemView.findViewById(R.id.listViewCourt);
        imgPic = (ImageView) itemView.findViewById(R.id.imgViewPic);

        imgPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "position " + position);

                if (position >= 1) {

                    Log.d(TAG, "position " + position + " image onClick, longitude = " + items.get(position - 1).getLongitude() + " latitude = " + items.get(position - 1).getLatitude());


                    Intent intent = new Intent(context, FullScreenView.class);
                    intent.putExtra("longitude", String.valueOf(items.get(position - 1).getLongitude()));
                    intent.putExtra("latitude", String.valueOf(items.get(position - 1).getLatitude()));
                    context.startActivity(intent);
                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e(TAG, "onScrollStateChanged position = "+position);
                Log.e(TAG, "current = "+currentArrayList.get(0).getTextShow());
                Log.e(TAG, "prev = "+preArrayList.get(0).getTextShow());
                Log.e(TAG, "next = "+nextArrayList.get(0).getTextShow());



            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        // Capture position and set to the TextViews
        //Log.e(TAG, "image height = "+imgPic.getDrawable().getIntrinsicHeight()+" width = "+imgPic.getDrawable().getIntrinsicWidth());



        if (items.size() > 0) {


            showItemArrayList.clear();
            //currentArrayList.clear();
            //preArrayList.clear();
            //nextArrayList.clear();
            if (position == getCount() - 1) {
                Log.d(TAG, "<last>");
                imgPic.setImageBitmap(items.get(0).getPic());
                //textViewName.setText(items.get(0).getName());
                //textViewCharge.setText(items.get(0).getCharge());
                //textViewMaintain.setText(items.get(0).getCourt_usage());



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

                ShowItemAdapter showItemAdapter = new ShowItemAdapter(context,R.layout.court_show_item,showItemArrayList);
                listView.setAdapter(showItemAdapter);

                if (is_setLast) {
                    if (set_count == 2) {

                        nextArrayList.clear();
                        nextArrayList = new ArrayList<>(showItemArrayList);
                        //Collections.copy(nextArrayList, showItemArrayList);

                        nextListView = listView;
                        nextAdapter = showItemAdapter;
                    }
                    set_count++;
                } else {

                    if (position > prev_position) { //left slide

                        if (currentArrayList.size() > 0) { //copy current to prev

                            preArrayList.clear();
                            preArrayList = new ArrayList<>(currentArrayList);

                            Log.e(TAG, "preArrayList " + preArrayList.get(0).getTextShow());

                            preListView = currentListView;
                            prevAdapter = currentAdapter;

                            prevAdapter = new ShowItemAdapter(context, R.layout.court_show_item, preArrayList);
                            preListView.setAdapter(prevAdapter);

                        }

                        if (nextArrayList.size() > 0) { //copy next to currrent

                            currentArrayList.clear();
                            currentArrayList = new ArrayList<>(nextArrayList);

                            Log.e(TAG, "currentArrayList " + currentArrayList.get(0).getTextShow());

                            currentListView = nextListView;
                            currentAdapter = nextAdapter;

                            currentAdapter = new ShowItemAdapter(context, R.layout.court_show_item, currentArrayList);
                            currentListView.setAdapter(currentAdapter);

                        }
                    } else if (position < prev_position) {

                        if (currentArrayList.size() > 0) { //copy current to temp

                            tempArrayList.clear();
                            tempArrayList = new ArrayList<>(currentArrayList);

                            Log.e(TAG, "tempArrayList " + tempArrayList.get(0).getTextShow());

                            tempListView = currentListView;
                            tempAdapter = currentAdapter;

                        }

                        if (preArrayList.size() > 0) { //copy prev to currrent

                            currentArrayList.clear();
                            currentArrayList = new ArrayList<>(preArrayList);

                            Log.e(TAG, "currentArrayList " + currentArrayList.get(0).getTextShow());

                            currentListView = preListView;
                            currentAdapter = prevAdapter;

                            currentAdapter = new ShowItemAdapter(context, R.layout.court_show_item, currentArrayList);
                            currentListView.setAdapter(currentAdapter);

                        }

                        if (tempArrayList.size() > 0) { //copy temp to prev
                            preArrayList.clear();
                            preArrayList = new ArrayList<>(tempArrayList);

                            Log.e(TAG, "preArrayList " + preArrayList.get(0).getTextShow());

                            preListView = tempListView;
                            prevAdapter = tempAdapter;

                            prevAdapter = new ShowItemAdapter(context, R.layout.court_show_item, preArrayList);
                            preListView.setAdapter(prevAdapter);
                        }
                    }

                    nextArrayList.clear();
                    nextArrayList = new ArrayList<>(showItemArrayList);

                    Log.e(TAG, "nextArrayList " + nextArrayList.get(0).getTextShow());
                    //Collections.copy(nextArrayList, showItemArrayList);

                    nextListView = listView;
                    nextAdapter = showItemAdapter;
                }
                //nextArrayList.clear();

                Log.d(TAG, "</last>");
            } else if (position == 0) {
                Log.d(TAG, "<first>");

                imgPic.setImageBitmap(items.get(items.size() - 1).getPic());


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

                ShowItemAdapter showItemAdapter = new ShowItemAdapter(context,R.layout.court_show_item,showItemArrayList);
                listView.setAdapter(showItemAdapter);

                Log.d(TAG, "</first>");

                if (is_init) {

                    Log.e(TAG, "=== is_init start ===");
                    preArrayList.clear();
                    preArrayList = new ArrayList<>(showItemArrayList);
                    //Collections.copy(preArrayList, showItemArrayList);

                    preListView = listView;
                    prevAdapter = showItemAdapter;
                } else if (is_setFirst) {
                    if (set_count == 1) {
                        preArrayList.clear();
                        //Collections.copy(preArrayList, showItemArrayList);
                        preArrayList = new ArrayList<>(showItemArrayList);

                        preListView = listView;
                        prevAdapter = showItemAdapter;
                    }
                    set_count++;
                } else {

                    if (position > prev_position) { //left slide

                        if (currentArrayList.size() > 0) { //copy current to prev

                            preArrayList.clear();
                            preArrayList = new ArrayList<>(currentArrayList);

                            Log.e(TAG, "preArrayList " + preArrayList.get(0).getTextShow());

                            preListView = currentListView;
                            prevAdapter = currentAdapter;

                            prevAdapter = new ShowItemAdapter(context, R.layout.court_show_item, preArrayList);
                            preListView.setAdapter(prevAdapter);

                        }

                        if (nextArrayList.size() > 0) { //copy next to currrent

                            currentArrayList.clear();
                            currentArrayList = new ArrayList<>(nextArrayList);

                            Log.e(TAG, "currentArrayList " + currentArrayList.get(0).getTextShow());

                            currentListView = nextListView;
                            currentAdapter = nextAdapter;

                            currentAdapter = new ShowItemAdapter(context, R.layout.court_show_item, currentArrayList);
                            currentListView.setAdapter(currentAdapter);

                        }
                    } else if (position < prev_position) {

                        if (currentArrayList.size() > 0) { //copy current to temp

                            tempArrayList.clear();
                            tempArrayList = new ArrayList<>(currentArrayList);

                            Log.e(TAG, "tempArrayList " + tempArrayList.get(0).getTextShow());

                            tempListView = currentListView;
                            tempAdapter = currentAdapter;

                        }

                        if (preArrayList.size() > 0) { //copy prev to currrent

                            currentArrayList.clear();
                            currentArrayList = new ArrayList<>(preArrayList);

                            Log.e(TAG, "currentArrayList " + currentArrayList.get(0).getTextShow());

                            currentListView = preListView;
                            currentAdapter = prevAdapter;

                            currentAdapter = new ShowItemAdapter(context, R.layout.court_show_item, currentArrayList);
                            currentListView.setAdapter(currentAdapter);
                        }

                        if (tempArrayList.size() > 0) { //copy temp to prev
                            preArrayList.clear();
                            preArrayList = new ArrayList<>(tempArrayList);

                            Log.e(TAG, "preArrayList " + preArrayList.get(0).getTextShow());

                            preListView = tempListView;
                            prevAdapter = tempAdapter;

                            prevAdapter = new ShowItemAdapter(context, R.layout.court_show_item, preArrayList);
                            preListView.setAdapter(prevAdapter);
                        }
                    }

                    nextArrayList.clear();
                    nextArrayList = new ArrayList<>(showItemArrayList);
                    //Collections.copy(nextArrayList, showItemArrayList);
                    Log.e(TAG, "nextArrayList " + nextArrayList.get(0).getTextShow());

                    nextListView = listView;
                    nextAdapter = showItemAdapter;

                }
            } else {
                Log.d(TAG, "<normal>");



                imgPic.setImageBitmap(items.get(position - 1).getPic());
                Log.e(TAG, "===> Get name "+items.get(position - 1).getName());


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

                ShowItemAdapter showItemAdapter = new ShowItemAdapter(context, R.layout.court_show_item, showItemArrayList);
                listView.setAdapter(showItemAdapter);

                Log.d(TAG, "</normal>");

                if (is_init) {
                    if (position == 1) {
                        currentArrayList.clear();
                        currentArrayList = new ArrayList<>(showItemArrayList);
                        //Collections.copy(currentArrayList, showItemArrayList);

                        currentListView = listView;
                        currentAdapter = showItemAdapter;

                    } else if (position == 2) {
                        nextArrayList.clear();
                        nextArrayList = new ArrayList<>(showItemArrayList);


                        //Collections.copy(nextArrayList, showItemArrayList);

                        nextListView = listView;
                        nextAdapter = showItemAdapter;

                        currentAdapter = new ShowItemAdapter(context, R.layout.court_show_item, currentArrayList);
                        currentListView.setAdapter(currentAdapter);

                        prevAdapter = new ShowItemAdapter(context, R.layout.court_show_item, preArrayList);
                        preListView.setAdapter(prevAdapter);
                        //show_current_page_init();
                        Log.e(TAG, "=== is_init end ===");
                        is_init = false;
                    }
                } else if (is_setFirst) {
                    if (set_count == 0) { //first, set current
                        currentArrayList.clear();
                        currentArrayList = new ArrayList<>(showItemArrayList);
                        //Collections.copy(currentArrayList, showItemArrayList);

                        currentListView = listView;
                        currentAdapter = showItemAdapter;

                    } else if (set_count == 2) {
                        nextArrayList.clear();
                        nextArrayList = new ArrayList<>(showItemArrayList);
                        //Collections.copy(nextArrayList, showItemArrayList);

                        nextListView = listView;
                        nextAdapter = showItemAdapter;
                    }
                    set_count++;
                } else if (is_setLast) {
                    if (set_count == 0) { //first, set current
                        currentArrayList.clear();
                        currentArrayList = new ArrayList<>(showItemArrayList);
                        //Collections.copy(currentArrayList, showItemArrayList);

                        currentListView = listView;
                        currentAdapter = showItemAdapter;
                    } else if (set_count == 1) {
                        preArrayList.clear();
                        preArrayList = new ArrayList<>(showItemArrayList);
                        //Collections.copy(preArrayList, showItemArrayList);

                        preListView = listView;
                        prevAdapter = showItemAdapter;
                    }

                    set_count++;
                } else if (is_markOther) {
                    Log.e(TAG, "<position = "+position+" mark_select = "+mark_select+">");
                    if (position == mark_select + 1) {
                        currentArrayList.clear();
                        currentArrayList = new ArrayList<>(showItemArrayList);
                        //Collections.copy(currentArrayList, showItemArrayList);

                        currentListView = listView;
                        currentAdapter = showItemAdapter;

                    } else if (position == mark_select ) {
                        preArrayList.clear();
                        preArrayList = new ArrayList<>(showItemArrayList);
                        //Collections.copy(preArrayList, showItemArrayList);

                        preListView = listView;
                        prevAdapter = showItemAdapter;
                    } else if (position == mark_select + 2) {
                        nextArrayList.clear();
                        nextArrayList = new ArrayList<>(showItemArrayList);
                        //Collections.copy(nextArrayList, showItemArrayList);

                        nextListView = listView;
                        nextAdapter = showItemAdapter;

                        is_markOther = false;
                    }

                } else {

                    if (position > prev_position) { //left slide

                        if (currentArrayList.size() > 0) { //copy current to prev

                            preArrayList.clear();
                            preArrayList = new ArrayList<>(currentArrayList);

                            Log.e(TAG, "preArrayList " + preArrayList.get(0).getTextShow());

                            preListView = currentListView;
                            prevAdapter = currentAdapter;

                            prevAdapter = new ShowItemAdapter(context, R.layout.court_show_item, preArrayList);
                            preListView.setAdapter(prevAdapter);

                        }

                        if (nextArrayList.size() > 0) { //copy next to currrent

                            currentArrayList.clear();
                            currentArrayList = new ArrayList<>(nextArrayList);

                            Log.e(TAG, "currentArrayList " + currentArrayList.get(0).getTextShow());

                            currentListView = nextListView;
                            currentAdapter = nextAdapter;

                            currentAdapter = new ShowItemAdapter(context, R.layout.court_show_item, currentArrayList);
                            currentListView.setAdapter(currentAdapter);

                        }
                    } else if (position < prev_position) {

                        if (currentArrayList.size() > 0) { //copy current to temp

                            tempArrayList.clear();
                            tempArrayList = new ArrayList<>(currentArrayList);

                            Log.e(TAG, "tempArrayList " + tempArrayList.get(0).getTextShow());

                            tempListView = currentListView;
                            tempAdapter = currentAdapter;

                        }

                        if (preArrayList.size() > 0) { //copy prev to currrent

                            currentArrayList.clear();
                            currentArrayList = new ArrayList<>(preArrayList);

                            Log.e(TAG, "currentArrayList " + currentArrayList.get(0).getTextShow());

                            currentListView = preListView;
                            currentAdapter = prevAdapter;

                            currentAdapter = new ShowItemAdapter(context, R.layout.court_show_item, currentArrayList);
                            currentListView.setAdapter(currentAdapter);

                        }

                        if (tempArrayList.size() > 0) { //copy temp to prev
                            preArrayList.clear();
                            preArrayList = new ArrayList<>(tempArrayList);

                            Log.e(TAG, "preArrayList " + preArrayList.get(0).getTextShow());

                            preListView = tempListView;
                            prevAdapter = tempAdapter;

                            prevAdapter = new ShowItemAdapter(context, R.layout.court_show_item, preArrayList);
                            preListView.setAdapter(prevAdapter);
                        }
                    }



                    nextArrayList.clear();
                    nextArrayList = new ArrayList<>(showItemArrayList);
                    //Collections.copy(nextArrayList, showItemArrayList);
                    Log.e(TAG, "nextArrayList " + nextArrayList.get(0).getTextShow());

                    nextListView = listView;
                    nextAdapter = showItemAdapter;


                }
            }


            prev_position = position;
        }

        Log.e(TAG, "set_count = "+set_count);

        if (is_setFirst && set_count == 3) {
            Log.e(TAG, "is_setFirst set current!");

            //listView.setAdapter(currentAdapter);
            set_count = 0;
        }

        if (is_setLast && set_count == 3) {
            Log.e(TAG, "is_setLast set current!");

            //listView.setAdapter(currentAdapter);
            set_count = 0;
        }


        // Locate the ImageView in viewpager_item.xml
        //imgPic = (ImageView) itemView.findViewById(R.id.flag);
        // Capture position and set to the ImageView
        //imgPic.setImageResource(flag[position]);

        // Add viewpager_item.xml to ViewPager


        container.addView(itemView);
        Log.e(TAG, "==> addItem "+position+"");



        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.e(TAG, "==> destroyItem "+position);



        container.removeView((View) object);
    }

    public void show_current_page_init() {
        Log.d(TAG, "show_current_page_init");

        Log.e(TAG, "name = "+items.get(0).getName());
        currentArrayList.clear();

        ShowItem courtName = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(0).getName(), 0);
        currentArrayList.add(courtName);

        ShowItem courtType = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(0).getType()), 0);
        currentArrayList.add(courtType);

        ShowItem courtUsage = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(0).getCourt_usage()), 0);
        currentArrayList.add(courtUsage);

        ShowItem courtLight = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(0).getLight()), 0);
        currentArrayList.add(courtLight);

        ShowItem courtNum = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(0).getCourt_num()), 0);
        currentArrayList.add(courtNum);

        ShowItem courtIfCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(0).getIfCharge()), 0);
        currentArrayList.add(courtIfCharge);

        ShowItem courtCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(0).getCharge()), 0);
        currentArrayList.add(courtCharge);

        ShowItem courtMaintain = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(0).getMaintenance());
        currentArrayList.add(courtMaintain);

        ShowItem courtTraffic = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(0).getTraffic());
        currentArrayList.add(courtTraffic);

        ShowItem courtParking = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(0).getParking());
        currentArrayList.add(courtParking);

        currentAdapter = new ShowItemAdapter(context,R.layout.court_show_item,currentArrayList);
        currentListView.setAdapter(currentAdapter);

        preArrayList.clear();
        ShowItem courtName1 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(items.size() - 1).getName(), 0);
        preArrayList.add(courtName1);

        ShowItem courtType1 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), String.valueOf(items.get(items.size() - 1).getType()), 0);
        preArrayList.add(courtType1);

        ShowItem courtUsage1 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(items.size() - 1).getCourt_usage()), 0);
        preArrayList.add(courtUsage1);

        ShowItem courtLight1 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(items.size() - 1).getLight()), 0);
        preArrayList.add(courtLight1);

        ShowItem courtNum1 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(items.size() - 1).getCourt_num()), 0);
        preArrayList.add(courtNum1);

        ShowItem courtIfCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(items.size() - 1).getIfCharge()), 0);
        preArrayList.add(courtIfCharge1);

        ShowItem courtCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(items.size() - 1).getCharge()), 0);
        preArrayList.add(courtCharge1);

        ShowItem courtMaintain1 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(items.size() - 1).getMaintenance());
        preArrayList.add(courtMaintain1);

        ShowItem courtTraffic1 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(items.size() - 1).getTraffic());
        preArrayList.add(courtTraffic1);

        ShowItem courtParking1 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(items.size() - 1).getParking());
        preArrayList.add(courtParking1);

        prevAdapter = new ShowItemAdapter(context,R.layout.court_show_item,preArrayList);
        preListView.setAdapter(prevAdapter);

        nextArrayList.clear();
        ShowItem courtName2 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(1).getName(), 0);
        nextArrayList.add(courtName2);

        ShowItem courtType2 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(1).getType()), 0);
        nextArrayList.add(courtType2);

        ShowItem courtUsage2 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(1).getCourt_usage()), 0);
        nextArrayList.add(courtUsage2);

        ShowItem courtLight2 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(1).getLight()), 0);
        nextArrayList.add(courtLight2);

        ShowItem courtNum2 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(1).getCourt_num()), 0);
        nextArrayList.add(courtNum2);

        ShowItem courtIfCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(1).getIfCharge()), 0);
        nextArrayList.add(courtIfCharge2);

        ShowItem courtCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(1).getCharge()), 0);
        nextArrayList.add(courtCharge2);

        ShowItem courtMaintain2 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(1).getMaintenance());
        nextArrayList.add(courtMaintain2);

        ShowItem courtTraffic2 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(1).getTraffic());
        nextArrayList.add(courtTraffic2);

        ShowItem courtParking2 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(1).getParking());
        nextArrayList.add(courtParking2);

        nextAdapter = new ShowItemAdapter(context,R.layout.court_show_item,nextArrayList);
        nextListView.setAdapter(nextAdapter);
    }

    public void show_current_page_first() {
        Log.d(TAG, "show_current_page_first");

        Log.e(TAG, "name = "+items.get(0).getName());
        currentArrayList.clear();

        ShowItem courtName = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(0).getName(), 0);
        currentArrayList.add(courtName);

        ShowItem courtType = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(0).getType()), 0);
        currentArrayList.add(courtType);

        ShowItem courtUsage = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(0).getCourt_usage()), 0);
        currentArrayList.add(courtUsage);

        ShowItem courtLight = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(0).getLight()), 0);
        currentArrayList.add(courtLight);

        ShowItem courtNum = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(0).getCourt_num()), 0);
        currentArrayList.add(courtNum);

        ShowItem courtIfCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(0).getIfCharge()), 0);
        currentArrayList.add(courtIfCharge);

        ShowItem courtCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(0).getCharge()), 0);
        currentArrayList.add(courtCharge);

        ShowItem courtMaintain = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(0).getMaintenance());
        currentArrayList.add(courtMaintain);

        ShowItem courtTraffic = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(0).getTraffic());
        currentArrayList.add(courtTraffic);

        ShowItem courtParking = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(0).getParking());
        currentArrayList.add(courtParking);

        currentAdapter = new ShowItemAdapter(context,R.layout.court_show_item,currentArrayList);
        currentListView.setAdapter(currentAdapter);

        preArrayList.clear();
        ShowItem courtName1 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(items.size() - 1).getName(), 0);
        preArrayList.add(courtName1);

        ShowItem courtType1 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), String.valueOf(items.get(items.size() - 1).getType()), 0);
        preArrayList.add(courtType1);

        ShowItem courtUsage1 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(items.size() - 1).getCourt_usage()), 0);
        preArrayList.add(courtUsage1);

        ShowItem courtLight1 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(items.size() - 1).getLight()), 0);
        preArrayList.add(courtLight1);

        ShowItem courtNum1 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(items.size() - 1).getCourt_num()), 0);
        preArrayList.add(courtNum1);

        ShowItem courtIfCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(items.size() - 1).getIfCharge()), 0);
        preArrayList.add(courtIfCharge1);

        ShowItem courtCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(items.size() - 1).getCharge()), 0);
        preArrayList.add(courtCharge1);

        ShowItem courtMaintain1 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(items.size() - 1).getMaintenance());
        preArrayList.add(courtMaintain1);

        ShowItem courtTraffic1 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(items.size() - 1).getTraffic());
        preArrayList.add(courtTraffic1);

        ShowItem courtParking1 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(items.size() - 1).getParking());
        preArrayList.add(courtParking1);

        prevAdapter = new ShowItemAdapter(context,R.layout.court_show_item,preArrayList);
        preListView.setAdapter(prevAdapter);

        nextArrayList.clear();
        ShowItem courtName2 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(1).getName(), 0);
        nextArrayList.add(courtName2);

        ShowItem courtType2 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(1).getType()), 0);
        nextArrayList.add(courtType2);

        ShowItem courtUsage2 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(1).getCourt_usage()), 0);
        nextArrayList.add(courtUsage2);

        ShowItem courtLight2 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(1).getLight()), 0);
        nextArrayList.add(courtLight2);

        ShowItem courtNum2 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(1).getCourt_num()), 0);
        nextArrayList.add(courtNum2);

        ShowItem courtIfCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(1).getIfCharge()), 0);
        nextArrayList.add(courtIfCharge2);

        ShowItem courtCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(1).getCharge()), 0);
        nextArrayList.add(courtCharge2);

        ShowItem courtMaintain2 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(1).getMaintenance());
        nextArrayList.add(courtMaintain2);

        ShowItem courtTraffic2 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(1).getTraffic());
        nextArrayList.add(courtTraffic2);

        ShowItem courtParking2 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(1).getParking());
        nextArrayList.add(courtParking2);

        nextAdapter = new ShowItemAdapter(context,R.layout.court_show_item,nextArrayList);
        nextListView.setAdapter(nextAdapter);
    }

    public void show_current_page_last() {
        Log.d(TAG, "show_current_page_last");

        Log.e(TAG, "name = "+items.get(items.size()-1).getName());
        currentArrayList.clear();

        ShowItem courtName = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(items.size()-1).getName(), 0);
        currentArrayList.add(courtName);

        ShowItem courtType = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(items.size()-1).getType()), 0);
        currentArrayList.add(courtType);

        ShowItem courtUsage = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(items.size()-1).getCourt_usage()), 0);
        currentArrayList.add(courtUsage);

        ShowItem courtLight = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(items.size()-1).getLight()), 0);
        currentArrayList.add(courtLight);

        ShowItem courtNum = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(items.size()-1).getCourt_num()), 0);
        currentArrayList.add(courtNum);

        ShowItem courtIfCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(items.size()-1).getIfCharge()), 0);
        currentArrayList.add(courtIfCharge);

        ShowItem courtCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(items.size()-1).getCharge()), 0);
        currentArrayList.add(courtCharge);

        ShowItem courtMaintain = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(items.size()-1).getMaintenance());
        currentArrayList.add(courtMaintain);

        ShowItem courtTraffic = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(items.size()-1).getTraffic());
        currentArrayList.add(courtTraffic);

        ShowItem courtParking = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(items.size()-1).getParking());
        currentArrayList.add(courtParking);

        currentAdapter = new ShowItemAdapter(context,R.layout.court_show_item,currentArrayList);
        currentListView.setAdapter(currentAdapter);

        preArrayList.clear();
        ShowItem courtName1 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(items.size() - 2).getName(), 0);
        preArrayList.add(courtName1);

        ShowItem courtType1 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), String.valueOf(items.get(items.size() - 2).getType()), 0);
        preArrayList.add(courtType1);

        ShowItem courtUsage1 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(items.size() - 2).getCourt_usage()), 0);
        preArrayList.add(courtUsage1);

        ShowItem courtLight1 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(items.size() - 2).getLight()), 0);
        preArrayList.add(courtLight1);

        ShowItem courtNum1 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(items.size() - 2).getCourt_num()), 0);
        preArrayList.add(courtNum1);

        ShowItem courtIfCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(items.size() - 2).getIfCharge()), 0);
        preArrayList.add(courtIfCharge1);

        ShowItem courtCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(items.size() - 2).getCharge()), 0);
        preArrayList.add(courtCharge1);

        ShowItem courtMaintain1 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(items.size() - 2).getMaintenance());
        preArrayList.add(courtMaintain1);

        ShowItem courtTraffic1 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(items.size() - 2).getTraffic());
        preArrayList.add(courtTraffic1);

        ShowItem courtParking1 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(items.size() - 2).getParking());
        preArrayList.add(courtParking1);

        prevAdapter = new ShowItemAdapter(context,R.layout.court_show_item,preArrayList);
        preListView.setAdapter(prevAdapter);

        nextArrayList.clear();
        ShowItem courtName2 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(0).getName(), 0);
        nextArrayList.add(courtName2);

        ShowItem courtType2 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(0).getType()), 0);
        nextArrayList.add(courtType2);

        ShowItem courtUsage2 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(0).getCourt_usage()), 0);
        nextArrayList.add(courtUsage2);

        ShowItem courtLight2 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(0).getLight()), 0);
        nextArrayList.add(courtLight2);

        ShowItem courtNum2 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(0).getCourt_num()), 0);
        nextArrayList.add(courtNum2);

        ShowItem courtIfCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(0).getIfCharge()), 0);
        nextArrayList.add(courtIfCharge2);

        ShowItem courtCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(0).getCharge()), 0);
        nextArrayList.add(courtCharge2);

        ShowItem courtMaintain2 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(0).getMaintenance());
        nextArrayList.add(courtMaintain2);

        ShowItem courtTraffic2 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(0).getTraffic());
        nextArrayList.add(courtTraffic2);

        ShowItem courtParking2 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(0).getParking());
        nextArrayList.add(courtParking2);

        nextAdapter = new ShowItemAdapter(context,R.layout.court_show_item,nextArrayList);
        nextListView.setAdapter(nextAdapter);
    }

    /*public void mark_current_page_first() {
        Log.d(TAG, "mark_current_page_first");

        Log.e(TAG, "name = "+items.get(0).getName());
        currentArrayList.clear();

        ShowItem courtName = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(0).getName(), 0);
        currentArrayList.add(courtName);

        ShowItem courtType = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(0).getType()), 0);
        currentArrayList.add(courtType);

        ShowItem courtUsage = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(0).getCourt_usage()), 0);
        currentArrayList.add(courtUsage);

        ShowItem courtLight = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(0).getLight()), 0);
        currentArrayList.add(courtLight);

        ShowItem courtNum = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(0).getCourt_num()), 0);
        currentArrayList.add(courtNum);

        ShowItem courtIfCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(0).getIfCharge()), 0);
        currentArrayList.add(courtIfCharge);

        ShowItem courtCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(0).getCharge()), 0);
        currentArrayList.add(courtCharge);

        ShowItem courtMaintain = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(0).getMaintenance());
        currentArrayList.add(courtMaintain);

        ShowItem courtTraffic = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(0).getTraffic());
        currentArrayList.add(courtTraffic);

        ShowItem courtParking = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(0).getParking());
        currentArrayList.add(courtParking);

        currentAdapter = new ShowItemAdapter(context,R.layout.court_show_item,currentArrayList);
        currentListView.setAdapter(currentAdapter);



        nextArrayList.clear();
        ShowItem courtName2 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(1).getName(), 0);
        nextArrayList.add(courtName2);

        ShowItem courtType2 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(1).getType()), 0);
        nextArrayList.add(courtType2);

        ShowItem courtUsage2 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(1).getCourt_usage()), 0);
        nextArrayList.add(courtUsage2);

        ShowItem courtLight2 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(1).getLight()), 0);
        nextArrayList.add(courtLight2);

        ShowItem courtNum2 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(1).getCourt_num()), 0);
        nextArrayList.add(courtNum2);

        ShowItem courtIfCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(1).getIfCharge()), 0);
        nextArrayList.add(courtIfCharge2);

        ShowItem courtCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(1).getCharge()), 0);
        nextArrayList.add(courtCharge2);

        ShowItem courtMaintain2 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(1).getMaintenance());
        nextArrayList.add(courtMaintain2);

        ShowItem courtTraffic2 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(1).getTraffic());
        nextArrayList.add(courtTraffic2);

        ShowItem courtParking2 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(1).getParking());
        nextArrayList.add(courtParking2);

        nextAdapter = new ShowItemAdapter(context,R.layout.court_show_item,nextArrayList);
        nextListView.setAdapter(nextAdapter);
    }*/

    public void mark_current_page_last() {
        Log.d(TAG, "mark_current_page_last");



        Log.e(TAG, "name = "+items.get(items.size() - 1).getName());
        currentArrayList.clear();

        ShowItem courtName = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(items.size() - 1).getName(), 0);
        currentArrayList.add(courtName);

        ShowItem courtType = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(items.size() - 1).getType()), 0);
        currentArrayList.add(courtType);

        ShowItem courtUsage = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(items.size() - 1).getCourt_usage()), 0);
        currentArrayList.add(courtUsage);

        ShowItem courtLight = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(items.size() - 1).getLight()), 0);
        currentArrayList.add(courtLight);

        ShowItem courtNum = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(items.size() - 1).getCourt_num()), 0);
        currentArrayList.add(courtNum);

        ShowItem courtIfCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(items.size() - 1).getIfCharge()), 0);
        currentArrayList.add(courtIfCharge);

        ShowItem courtCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(items.size() - 1).getCharge()), 0);
        currentArrayList.add(courtCharge);

        ShowItem courtMaintain = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(items.size() - 1).getMaintenance());
        currentArrayList.add(courtMaintain);

        ShowItem courtTraffic = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(items.size() - 1).getTraffic());
        currentArrayList.add(courtTraffic);

        ShowItem courtParking = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(items.size() - 1).getParking());
        currentArrayList.add(courtParking);

        currentAdapter = new ShowItemAdapter(context,R.layout.court_show_item,currentArrayList);
        currentListView.setAdapter(currentAdapter);

        preArrayList.clear();
        ShowItem courtName1 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(items.size() - 2).getName(), 0);
        preArrayList.add(courtName1);

        ShowItem courtType1 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), String.valueOf(items.get(items.size() - 2).getType()), 0);
        preArrayList.add(courtType1);

        ShowItem courtUsage1 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(items.size() - 2).getCourt_usage()), 0);
        preArrayList.add(courtUsage1);

        ShowItem courtLight1 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(items.size() - 2).getLight()), 0);
        preArrayList.add(courtLight1);

        ShowItem courtNum1 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(items.size() - 2).getCourt_num()), 0);
        preArrayList.add(courtNum1);

        ShowItem courtIfCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(items.size() - 2).getIfCharge()), 0);
        preArrayList.add(courtIfCharge1);

        ShowItem courtCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(items.size() - 2).getCharge()), 0);
        preArrayList.add(courtCharge1);

        ShowItem courtMaintain1 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(items.size() - 2).getMaintenance());
        preArrayList.add(courtMaintain1);

        ShowItem courtTraffic1 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(items.size() - 2).getTraffic());
        preArrayList.add(courtTraffic1);

        ShowItem courtParking1 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(items.size() - 2).getParking());
        preArrayList.add(courtParking1);

        prevAdapter = new ShowItemAdapter(context,R.layout.court_show_item,preArrayList);
        preListView.setAdapter(prevAdapter);

        nextArrayList.clear();
        ShowItem courtName2 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(0).getName(), 0);
        nextArrayList.add(courtName2);

        ShowItem courtType2 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(0).getType()), 0);
        nextArrayList.add(courtType2);

        ShowItem courtUsage2 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(0).getCourt_usage()), 0);
        nextArrayList.add(courtUsage2);

        ShowItem courtLight2 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(0).getLight()), 0);
        nextArrayList.add(courtLight2);

        ShowItem courtNum2 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(0).getCourt_num()), 0);
        nextArrayList.add(courtNum2);

        ShowItem courtIfCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(0).getIfCharge()), 0);
        nextArrayList.add(courtIfCharge2);

        ShowItem courtCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(0).getCharge()), 0);
        nextArrayList.add(courtCharge2);

        ShowItem courtMaintain2 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(0).getMaintenance());
        nextArrayList.add(courtMaintain2);

        ShowItem courtTraffic2 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(0).getTraffic());
        nextArrayList.add(courtTraffic2);

        ShowItem courtParking2 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(0).getParking());
        nextArrayList.add(courtParking2);

        nextAdapter = new ShowItemAdapter(context,R.layout.court_show_item,nextArrayList);
        nextListView.setAdapter(nextAdapter);
    }

    public void mark_current_page_other(int select) {
        Log.d(TAG, "mark_current_page_other");

        Log.e(TAG, "name = "+items.get(select-1).getName());
        currentArrayList.clear();

        ShowItem courtName = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(select-1).getName(), 0);
        currentArrayList.add(courtName);

        ShowItem courtType = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(select-1).getType()), 0);
        currentArrayList.add(courtType);

        ShowItem courtUsage = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(select-1).getCourt_usage()), 0);
        currentArrayList.add(courtUsage);

        ShowItem courtLight = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(select-1).getLight()), 0);
        currentArrayList.add(courtLight);

        ShowItem courtNum = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(select-1).getCourt_num()), 0);
        currentArrayList.add(courtNum);

        ShowItem courtIfCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(select-1).getIfCharge()), 0);
        currentArrayList.add(courtIfCharge);

        ShowItem courtCharge = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(select-1).getCharge()), 0);
        currentArrayList.add(courtCharge);

        ShowItem courtMaintain = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(select-1).getMaintenance());
        currentArrayList.add(courtMaintain);

        ShowItem courtTraffic = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(select-1).getTraffic());
        currentArrayList.add(courtTraffic);

        ShowItem courtParking = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(select-1).getParking());
        currentArrayList.add(courtParking);

        currentAdapter = new ShowItemAdapter(context,R.layout.court_show_item,currentArrayList);
        currentListView.setAdapter(currentAdapter);

        preArrayList.clear();
        ShowItem courtName1 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(select - 2).getName(), 0);
        preArrayList.add(courtName1);

        ShowItem courtType1 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), String.valueOf(items.get(select - 2).getType()), 0);
        preArrayList.add(courtType1);

        ShowItem courtUsage1 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(select - 2).getCourt_usage()), 0);
        preArrayList.add(courtUsage1);

        ShowItem courtLight1 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(select - 2).getLight()), 0);
        preArrayList.add(courtLight1);

        ShowItem courtNum1 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(select - 2).getCourt_num()), 0);
        preArrayList.add(courtNum1);

        ShowItem courtIfCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(select - 2).getIfCharge()), 0);
        preArrayList.add(courtIfCharge1);

        ShowItem courtCharge1 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(select - 2).getCharge()), 0);
        preArrayList.add(courtCharge1);

        ShowItem courtMaintain1 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(select - 2).getMaintenance());
        preArrayList.add(courtMaintain1);

        ShowItem courtTraffic1 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(select - 2).getTraffic());
        preArrayList.add(courtTraffic1);

        ShowItem courtParking1 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(select - 2).getParking());
        preArrayList.add(courtParking1);

        prevAdapter = new ShowItemAdapter(context,R.layout.court_show_item,preArrayList);
        preListView.setAdapter(prevAdapter);

        nextArrayList.clear();
        ShowItem courtName2 = new ShowItem(context.getResources().getString(R.string.add_court_header_name), items.get(select).getName(), 0);
        nextArrayList.add(courtName2);

        ShowItem courtType2 = new ShowItem(context.getResources().getString(R.string.add_court_header_type), getCourtType(items.get(select).getType()), 0);
        nextArrayList.add(courtType2);

        ShowItem courtUsage2 = new ShowItem(context.getResources().getString(R.string.add_court_header_usage), getCourtUsage(items.get(select).getCourt_usage()), 0);
        nextArrayList.add(courtUsage2);

        ShowItem courtLight2 = new ShowItem(context.getResources().getString(R.string.add_court_header_light), getCourtLight(items.get(select).getLight()), 0);
        nextArrayList.add(courtLight2);

        ShowItem courtNum2 = new ShowItem(context.getResources().getString(R.string.add_court_header_courts), String.valueOf(items.get(select).getCourt_num()), 0);
        nextArrayList.add(courtNum2);

        ShowItem courtIfCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_if_charge), getCourtCharge(items.get(select).getIfCharge()), 0);
        nextArrayList.add(courtIfCharge2);

        ShowItem courtCharge2 = new ShowItem(context.getResources().getString(R.string.add_court_header_charge), String.valueOf(items.get(select).getCharge()), 0);
        nextArrayList.add(courtCharge2);

        ShowItem courtMaintain2 = new ShowItem(context.getResources().getString(R.string.add_court_header_maintenance), "", items.get(select).getMaintenance());
        nextArrayList.add(courtMaintain2);

        ShowItem courtTraffic2 = new ShowItem(context.getResources().getString(R.string.add_court_header_traffic), "", items.get(select).getTraffic());
        nextArrayList.add(courtTraffic2);

        ShowItem courtParking2 = new ShowItem(context.getResources().getString(R.string.add_court_header_parking), "", items.get(select).getParking());
        nextArrayList.add(courtParking2);

        nextAdapter = new ShowItemAdapter(context,R.layout.court_show_item,nextArrayList);
        nextListView.setAdapter(nextAdapter);
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
