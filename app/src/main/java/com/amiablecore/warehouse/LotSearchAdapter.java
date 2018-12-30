package com.amiablecore.warehouse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LotSearchAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<ItemObject> listItemStorage;

    public LotSearchAdapter(Context context, List<ItemObject> customizedListView) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listItemStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listItemStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return listItemStorage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("LotSearchAdapter :", "getView");
        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);
            listViewHolder.itemView = (TextView) convertView.findViewById(R.id.list_item_search);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }
        listViewHolder.itemView.setText(listItemStorage.get(position).getTitle());
        return convertView;
    }

    static class ViewHolder {
        TextView itemView;
    }
}
