package com.amiablecore.warehouse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amiablecore.warehouse.beans.Trader;

import java.util.List;

public class TraderSearchAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Trader> listItemStorage;

    public TraderSearchAdapter(Context context, List<Trader> customizedListView) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listItemStorage = customizedListView;
    }

    @Override
    public int getCount() {
        Log.i("TraderSearchAdapter :", "getCount() "+String.valueOf(listItemStorage.size()));
        return listItemStorage.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i("TraderSearchAdapter :", "getItem() "+String.valueOf(position));
        return listItemStorage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listViewHolder;
        if (convertView == null) {
            Log.i("TraderSearchAdapter :", "Null");
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
            listViewHolder.itemView = (TextView) convertView.findViewById(R.id.list_item_search);
            convertView.setTag(listViewHolder);
        } else {
            Log.i("TraderSearchAdapter :", "Not Null");
            listViewHolder = (ViewHolder) convertView.getTag();
        }
        listViewHolder.itemView.setText(listItemStorage.get(position).getTraderName());
        return convertView;
    }

    static class ViewHolder {
        TextView itemView;
    }
}
