package com.example.gs.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gs.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> arrayMenu;
    private LayoutInflater mLayoutInflater = null;

    public ListViewAdapter(Context mContext, ArrayList<String> arrayMenu) {
        this.mContext = mContext;
        this.arrayMenu = arrayMenu;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return arrayMenu.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayMenu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.listview_item,null);
        TextView menuName = (TextView)view.findViewById(R.id.menuName);
        menuName.setText(arrayMenu.get(position));

        return view;
    }}

