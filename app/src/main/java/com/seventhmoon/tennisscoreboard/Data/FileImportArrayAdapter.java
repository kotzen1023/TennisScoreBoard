package com.seventhmoon.tennisscoreboard.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.tennisscoreboard.R;

import java.io.File;
import java.util.ArrayList;


import static com.seventhmoon.tennisscoreboard.FileImportActivity.confirm;


public class FileImportArrayAdapter extends ArrayAdapter<FileImportItem> {
    private static final String TAG = FileImportArrayAdapter.class.getName();

    private LayoutInflater inflater = null;
    public SparseBooleanArray mSparseBooleanArray;
    private int layoutResourceId;
    private ArrayList<FileImportItem> items = new ArrayList<>();
    //private int count = 0;
    private static int previous_check;

    public FileImportArrayAdapter(Context context, int textViewResourceId,
                                  ArrayList<FileImportItem> objects) {
        super(context, textViewResourceId, objects);
        this.layoutResourceId = textViewResourceId;
        this.items = objects;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSparseBooleanArray = new SparseBooleanArray();


        mSparseBooleanArray.clear();
        for (int i=0; i<items.size(); i++) {
            mSparseBooleanArray.put(i, false);
        }

        //Log.e(TAG, "mSparseBooleanArray.size = "+mSparseBooleanArray.size());
        previous_check = 0;
    }

    @Override
    public int getCount() {
        return items.size();

    }

    public FileImportItem getItem(int position)
    {
        return items.get(position);
    }
    @Override

    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {

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

        FileImportItem fileChooseItem = items.get(position);
        if (fileChooseItem != null) {

            holder.filename.setText(fileChooseItem.getName());
            holder.checkbox.setTag(position);

            TextView t1 = view.findViewById(R.id.fileChooseFileName);
            CheckBox ck = view.findViewById(R.id.checkBoxInRow);
            //TextView t2 = (TextView) v.findViewById(R.id.TextView02);
            //TextView t3 = (TextView) v.findViewById(R.id.TextViewDate);
                       /* Take the ImageView from layout and set the city's image */

            fileChooseItem.setCheckBox(ck);
            ck.setVisibility(View.VISIBLE);
            //ck.setVisibility(View.INVISIBLE);

            //if (FileChooseLongClick) {
            //    ck.setVisibility(View.VISIBLE);
            //}
            //Log.d(TAG, "getview "+position+ " mSparseBooleanArray = "+mSparseBooleanArray.get(position));

            if (mSparseBooleanArray.get(position))
            {
                ck.setChecked(true);
            } else {
                ck.setChecked(false);
            }
            /*if (holder.checkbox != null) {
                holder.checkbox.setVisibility(View.INVISIBLE);
                holder.checkbox.setChecked(false);
                if (Data.FileChooseLongClick == true) {
                    holder.checkbox.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e(TAG, "checkbox = null");
            }*/
            if(t1!=null)
                t1.setText(fileChooseItem.getName());
            //if(t2!=null)
            //    t2.setText(o.getData());
            //if(t3!=null)
            //    t3.setText(o.getDate());


            Bitmap bitmap, bm;
            if (fileChooseItem.getPath() != null) {

                File file = new File(fileChooseItem.getPath());

                //Log.i(TAG, "file: abs path"+file.getAbsolutePath()+ "name = "+fileChooseItem.getName()+" data = "+fileChooseItem.getData());

                if (file.isDirectory()) {
                    if (fileChooseItem.getName().equals("..")) {
                        bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.up);
                        //ck.setVisibility(View.INVISIBLE);
                    } else {
                        bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.folder);
                    }
                    ck.setVisibility(View.INVISIBLE);
                } else if (file.isFile()) {
                    bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.file);
                    //ck.setVisibility(View.VISIBLE);
                } else {
                    bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.folder);
                    ck.setVisibility(View.INVISIBLE);
                }
                bm = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                ImageView imageCity = view.findViewById(R.id.fd_Icon1);
                //String uri = "drawable/" + o.getImage();
                //int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
                //Drawable image = c.getResources().getDrawable(imageResource);
                imageCity.setImageBitmap(bm);
                //imageCity.setImageDrawable(image);

                holder.checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
            }

        }
        return view;
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int)buttonView.getTag();
            Log.i(TAG, "switch " + buttonView.getTag() + " checked = " + isChecked);
            //int idx = (Integer) buttonView.getTag();

            //if(isChecked == true) {
            FileImportItem fileImportItem = items.get((Integer) buttonView.getTag());



            if (fileImportItem.getCheckBox() != null) {

                if (!fileImportItem.getName().equals("..")) {


                    /*for (int i= 0; i<mSparseBooleanArray.size(); i++) {
                        if (isChecked) {
                            if (i == position) {
                                mSparseBooleanArray.put(position, true);
                                items.get(position).getCheckBox().setChecked(true);
                            } else {

                                mSparseBooleanArray.put(i, false);
                                if (items.get(i).getCheckBox() != null)
                                    items.get(i).getCheckBox().setChecked(false);
                            }
                        } else {
                            if (i == position) {
                                mSparseBooleanArray.put(position, false);
                                items.get(position).getCheckBox().setChecked(false);
                            } else {

                                mSparseBooleanArray.put(i, false);
                                if (items.get(i).getCheckBox() != null)
                                    items.get(i).getCheckBox().setChecked(false);
                            }
                        }



                    }*/

                    if (position != previous_check) {
                        //Log.e(TAG, "position != previous_check");

                        if (isChecked) {
                            mSparseBooleanArray.put(position, true);
                            items.get(position).getCheckBox().setChecked(true);
                            //set previous false
                            mSparseBooleanArray.put(previous_check, false);
                            if (items.get(previous_check).getCheckBox() != null)
                                items.get(previous_check).getCheckBox().setChecked(false);

                            previous_check = position;
                        }
                    } else { //position == previous_check
                        //Log.e(TAG, "position == previous_check, ischeck = "+isChecked);
                        mSparseBooleanArray.put(position, isChecked);
                        items.get(position).getCheckBox().setChecked(isChecked);

                        previous_check = position;
                    }





                    boolean found = false;
                    for (int j = 0; j<mSparseBooleanArray.size(); j++) {
                        if (mSparseBooleanArray.get(j)) {
                            //Log.e(TAG, "found =====> "+j);
                            found = true;
                            break;
                        }
                    }

                    if (found)
                        confirm.setVisibility(View.VISIBLE);
                    else
                        confirm.setVisibility(View.GONE);
                }
                else {
                    Log.e(TAG, "item = ..");
                }
            }


            //}
            /*int count = 0;

            for (int i=0; i<fileChooseArrayAdapter.getCount(); i++) {
                if (mSparseBooleanArray.get(i)) {
                    count++;
                }
            }*/

            //Log.e(TAG, "Count = "+count);

            /*if (count > 0) {
                confirm.setVisibility(View.VISIBLE);
            } else  {
                confirm.setVisibility(View.GONE);
            }*/
        }
    };

    private class ViewHolder {
        ImageView fileicon;
        TextView filename;
        CheckBox checkbox;


        private ViewHolder(View view) {
            this.fileicon = view.findViewById(R.id.fd_Icon1);
            this.filename = view.findViewById(R.id.fileChooseFileName);
            this.checkbox = view.findViewById(R.id.checkBoxInRow);
        }
    }
}
