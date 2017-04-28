package com.seventhmoon.tennisscoreboard.Data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tennisscoreboard.R;

import java.util.ArrayList;


public class GridViewVoiceAdapter extends ArrayAdapter<ImageBuyItem> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageBuyItem> data = new ArrayList<>();
    //private boolean[] selection;

    public GridViewVoiceAdapter(Context context, int layoutResourceId, ArrayList<ImageBuyItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        //selection = new boolean[data.size()];
    }

    @Override
    public int getCount() {
        return data.size();

    }

    @Override
    public ImageBuyItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View row = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) convertView.findViewById(R.id.themeText);
            //holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
            holder.image = (ImageView) convertView.findViewById(R.id.themeImage);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageBuyItem item = data.get(position);
        holder.imageTitle.setText(item.getTitle());
        holder.image.setImageBitmap(item.getImage());

        holder.id = position;
        /*if (item.getSelected())
        {
            holder.setState(true);


            switch (Data.current_theme) {
                case 0:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.image.setBackgroundColor(context.getColor(R.color.inside_select_background_simple));
                        holder.imageTitle.setBackgroundColor(context.getColor(R.color.inside_select_background_simple));
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_simple));
                    } else {
                        holder.image.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setTextColor(Color.parseColor("#161616"));
                    }
                    break;
                case 1:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.image.setBackgroundColor(context.getColor(R.color.inside_select_background_bear));
                        holder.imageTitle.setBackgroundColor(context.getColor(R.color.inside_select_background_bear));
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_bear));
                    } else {
                        holder.image.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setTextColor(Color.parseColor("#333333"));
                    }
                    break;
                case 2:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.image.setBackgroundColor(context.getColor(R.color.inside_select_background_cat));
                        holder.imageTitle.setBackgroundColor(context.getColor(R.color.inside_select_background_cat));
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_cat));
                    } else {
                        holder.image.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setTextColor(Color.parseColor("#333333"));
                    }
                    break;
                case 3:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.image.setBackgroundColor(context.getColor(R.color.inside_select_background_classic));
                        holder.imageTitle.setBackgroundColor(context.getColor(R.color.inside_select_background_classic));
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_selected_classic));
                    } else {
                        holder.image.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setTextColor(Color.parseColor("#333333"));
                    }
                    break;
                default:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.image.setBackgroundColor(context.getColor(R.color.inside_select_background_bear));
                        holder.imageTitle.setBackgroundColor(context.getColor(R.color.inside_select_background_bear));
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_bear));
                    } else {
                        holder.image.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setBackgroundColor(Color.parseColor("#90aadc"));
                        holder.imageTitle.setTextColor(Color.parseColor("#161616"));
                    }
                    break;
            }

            //holder.image.setBackgroundColor(Color.argb(255, 0x46,0x6e,0x9b));
            //holder.imageTitle.setBackgroundColor(Color.argb(255, 0x46,0x6e,0x9b));
            selection[holder.id] = true;
        }
        else {
            holder.setState(false);
            holder.image.setBackgroundColor(0x00000000);
            holder.imageTitle.setBackgroundColor(0x00000000);

            switch (Data.current_theme) {
                case 0:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_simple));
                    } else {
                        holder.imageTitle.setTextColor(Color.parseColor("#161616"));
                    }
                    break;
                case 1:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_bear));
                    } else {
                        holder.imageTitle.setTextColor(Color.parseColor("#333333"));
                    }
                    break;
                case 2:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_cat));
                    } else {
                        holder.imageTitle.setTextColor(Color.parseColor("#333333"));
                    }
                    break;
                case 3:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_unselected_classic));
                    } else {
                        holder.imageTitle.setTextColor(Color.parseColor("#efefef"));
                    }
                    break;
                default:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.imageTitle.setTextColor(context.getColor(R.color.inside_item_text_simple));
                    } else {
                        holder.imageTitle.setTextColor(Color.parseColor("#161616"));
                    }
                    break;
            }
            selection[holder.id] = false;
        }*/

        return convertView;
    }

    private class ViewHolder {
        TextView imageTitle;
        ImageView image;
        //CheckBox checkbox;
        int id;
        boolean state;

        //public boolean getState()
        //{
        //    return state;
        //}

        //public void setState(boolean state)
        {
            this.state = state;
        }
    }
}
