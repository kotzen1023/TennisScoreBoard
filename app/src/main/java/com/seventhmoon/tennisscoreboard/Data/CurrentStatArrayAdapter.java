package com.seventhmoon.tennisscoreboard.Data;


import android.content.Context;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.seventhmoon.tennisscoreboard.R;

import java.util.ArrayList;

public class CurrentStatArrayAdapter extends ArrayAdapter<CurrentStatItem> {
    private static final String TAG = CurrentStatArrayAdapter.class.getName();

    private LayoutInflater inflater = null;
    //SparseBooleanArray mSparseBooleanArray;
    private int layoutResourceId;

    private ArrayList<CurrentStatItem> items = new ArrayList<>();

    public CurrentStatArrayAdapter(Context context, int textViewResourceId,
                                  ArrayList<CurrentStatItem> objects) {
        super(context, textViewResourceId, objects);
        this.layoutResourceId = textViewResourceId;

        this.items = objects;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //mSparseBooleanArray = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return items.size();

    }

    public CurrentStatItem getItem(int position)
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

        CurrentStatItem currentStatItem = items.get(position);
        if (currentStatItem != null) {
            holder.title.setText(currentStatItem.getTitle());
            holder.statUp.setTextColor(Color.rgb(0x0, 0x99, 0xcc));
            holder.statUp.setText(currentStatItem.getStatUp());

            holder.statDown.setTextColor(Color.rgb(0xf1, 0x7a, 0x0a));
            holder.statDown.setText(currentStatItem.getStatDown());

            if (position == 0) {
                holder.barUp.setProgress(0);
                holder.barDown.setProgress(0);
            } else {

                if (currentStatItem.getValueUp() <= 100 &&
                        currentStatItem.getValueDown() <= 100) {
                    holder.barUp.setProgress(currentStatItem.getValueUp());

                    holder.barDown.setProgress(currentStatItem.getValueDown());
                } else if (currentStatItem.getValueUp() > 100 && currentStatItem.getValueUp() <= 200 &&
                        currentStatItem.getValueDown() > 100 && currentStatItem.getValueDown() <= 200) {
                    holder.barUp.setProgress(currentStatItem.getValueUp()/2);
                    holder.barDown.setProgress(currentStatItem.getValueDown()/2);
                } else if (currentStatItem.getValueUp() > 200 && currentStatItem.getValueUp() <= 300 &&
                        currentStatItem.getValueDown() > 200 && currentStatItem.getValueDown() <= 300) {
                    holder.barUp.setProgress(currentStatItem.getValueUp()/3);
                    holder.barDown.setProgress(currentStatItem.getValueDown()/3);
                }

            }
        }



        return view;
    }

    /*CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.i(TAG, "switch " + buttonView.getTag() + " checked = " + isChecked);
            //int idx = (Integer) buttonView.getTag();

            //if(isChecked == true) {
            FileChooseItem fileChooseItem = items.get((Integer) buttonView.getTag());

            if (fileChooseItem.getCheckBox() != null) {

                if (!fileChooseItem.getName().equals("..")) {
                    mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
                }
                else {
                    fileChooseItem.getCheckBox().setChecked(false);
                    fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                    mSparseBooleanArray.put((Integer) buttonView.getTag(), false);
                }
            }
            //}
        }
    };*/

    class ViewHolder {
        TextView title;
        TextView statUp;
        TextView statDown;
        ProgressBar barUp;
        ProgressBar barDown;


        public ViewHolder(View view) {
            this.title = (TextView) view.findViewById(R.id.textViewStatTitle);
            this.statUp = (TextView) view.findViewById(R.id.textViewStatUp);
            this.statDown = (TextView) view.findViewById(R.id.textViewStatDown);
            this.barUp = (ProgressBar) view.findViewById(R.id.textViewStatUpBar);
            this.barDown = (ProgressBar) view.findViewById(R.id.textViewStatDownBar);

        }
    }
}
