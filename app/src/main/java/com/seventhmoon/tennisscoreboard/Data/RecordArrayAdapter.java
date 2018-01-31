package com.seventhmoon.tennisscoreboard.Data;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tennisscoreboard.R;

import java.util.ArrayList;


public class RecordArrayAdapter extends ArrayAdapter<RecordItem> {
    private static final String TAG = RecordArrayAdapter.class.getName();

    private LayoutInflater inflater = null;
    private int layoutResourceId;
    private ArrayList<RecordItem> items = new ArrayList<>();

    public RecordArrayAdapter(Context context, int textViewResourceId,
                           ArrayList<RecordItem> objects) {
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

    public RecordItem getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

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

        RecordItem recordItem = items.get(position);
        if (recordItem != null) {

            holder.imgRecord.setImageResource(R.drawable.ic_album_white_48dp);
            holder.title.setText(recordItem.getTitle());

            if (recordItem.isFileExist()) {
                holder.imgCheck.setImageResource(R.drawable.ic_check_white_48dp);
            } else {
                holder.imgCheck.setImageResource(R.drawable.ic_clear_white_48dp);
            }

            if (recordItem.isSelected()) {
                view.setBackgroundColor(Color.rgb(0x4d, 0x90, 0xfe));
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }


            //holder.play.setImageResource(R.drawable.ic_play_arrow_white_48dp);
            //holder.stop.setImageResource(R.drawable.ic_stop_white_48dp);
            //holder.record.setImageResource(R.drawable.ic_fiber_manual_record_white_48dp);

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
        ImageView imgRecord;
        TextView title;
        ImageView imgCheck;
        //ImageView play;
        //ImageView stop;
        //ImageView record;


        private ViewHolder(View view) {
            this.imgRecord = view.findViewById(R.id.imgRecord);
            this.title = view.findViewById(R.id.recordFileName);
            this.imgCheck = view.findViewById(R.id.imgCheck);
            //this.play = (ImageView) view.findViewById(R.id.imgPlay);
            //this.stop = (ImageView) view.findViewById(R.id.imgStop);
            //this.record = (ImageView) view.findViewById(R.id.imgRecord);
        }
    }
}
