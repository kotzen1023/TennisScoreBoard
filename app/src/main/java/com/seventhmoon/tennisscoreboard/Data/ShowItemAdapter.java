package com.seventhmoon.tennisscoreboard.Data;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.seventhmoon.tennisscoreboard.R;

import java.util.ArrayList;


public class ShowItemAdapter extends ArrayAdapter<ShowItem> {
    private static final String TAG = ShowItemAdapter.class.getName();

    private LayoutInflater inflater = null;
    private int layoutResourceId;
    private ArrayList<ShowItem> items = new ArrayList<>();

    public ShowItemAdapter(Context context, int textViewResourceId,
                                  ArrayList<ShowItem> objects) {
        super(context, textViewResourceId, objects);
        //this.context = context;
        this.layoutResourceId = textViewResourceId;
        this.items = objects;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return items.size();

    }

    public ShowItem getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.e(TAG, "getView = "+ position);
        View view;
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            //Log.e(TAG, "convertView = null");
            /*view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            view.setTag(holder);*/

            //LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            //holder.checkbox.setVisibility(View.INVISIBLE);
            view.setTag(holder);
        }
        else {
            view = convertView ;
            holder = (ViewHolder) view.getTag();
        }

        //holder.fileicon = (ImageView) view.findViewById(R.id.fd_Icon1);
        //holder.filename = (TextView) view.findViewById(R.id.fileChooseFileName);
        //holder.checkbox = (CheckBox) view.findViewById(R.id.checkBoxInRow);

        ShowItem showItem = items.get(position);
        if (showItem != null) {

            holder.header.setText(showItem.getTextTitle());
            if (!showItem.getTextShow().equals("")) {
                holder.content.setVisibility(View.VISIBLE);
                holder.rating.setVisibility(View.GONE);
                holder.content.setText(showItem.getTextShow());
                //Log.e(TAG, "getView = "+ position+ " "+showItem.getTextShow());
            } else {
                holder.content.setVisibility(View.GONE);
                holder.rating.setVisibility(View.VISIBLE);
                holder.rating.setRating(showItem.getFloatShow());
                //Log.e(TAG, "getView = "+ position+ " "+showItem.getFloatShow());
            }

            /*if (position == 0) {
                holder.header.setText(showItem.getTextTitle());
                //holder.name.setText(statisticsItem.getName());
                //holder.num.setText(getContext().getResources().getString(R.string.statistics_header_count));
                //holder.size.setText(getContext().getResources().getString(R.string.statistics_header_size));
            } else {

                holder.name.setText(statisticsItem.getName());
                if (position == 7) //free space
                    holder.num.setText("");
                else
                    holder.num.setText(String.valueOf(statisticsItem.getCount()));

                String output_size;
                double file_size = ((statisticsItem.getSize() * 100) / 1024) / 100.00;
                Log.e(TAG, "file_size = "+String.valueOf(file_size));
                if (file_size > 1024) { //mb
                    file_size = ((statisticsItem.getSize() * 100) / (1024 * 1024 )) / 100.00;
                    if (file_size > 1024) {
                        file_size = ((statisticsItem.getSize() * 100) / (1024 * 1024 * 1024)) / 100.00;
                        output_size = file_size + " GB";
                    } else {
                        output_size = file_size + " MB";
                    }
                } else {
                    output_size = file_size + " KB";
                }


                holder.size.setText(output_size);

            }*/

        }
        return view;
    }

    private class ViewHolder {
        TextView header;
        TextView content;
        RatingBar rating;


        private ViewHolder(View view) {
            this.header = (TextView) view.findViewById(R.id.court_show_header);
            this.content = (TextView) view.findViewById(R.id.court_show_msg);
            this.rating = (RatingBar) view.findViewById(R.id.court_show_rating);
        }
    }
}
