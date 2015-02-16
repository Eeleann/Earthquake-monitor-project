package com.gp.elena.earthquakemonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class RowAdapter extends BaseAdapter {

    private ArrayList<?> inputs;
    private int R_layout_IdView;
    private Context context;

    public RowAdapter(Context context, int R_layout_IdView, ArrayList<?> inputs){
        super();
        this.context = context;
        this.inputs = inputs;
        this.R_layout_IdView = R_layout_IdView;
    }

    public View getView(int position, View view, ViewGroup parent){
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, null);
        }
        onInput (inputs.get(position), view);
        return view;
    }

    @Override
    public int getCount() {
        return inputs.size();
    }

    @Override
    public Object getItem(int position) {
        return inputs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract void onInput (Object input, View view);
}
