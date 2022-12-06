package com.andrey_sonido.russiancoins.adapters.grid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrey_sonido.russiancoins.Coin;
import com.andrey_sonido.russiancoins.R;

import java.util.ArrayList;

/**
 * Created by Codex on 23.06.2015.
 */

public class StickyGridHeadersSimpleArrayAdapter<T> extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {
    protected static final String TAG = StickyGridHeadersSimpleArrayAdapter.class.getSimpleName();

    private int mHeaderResId;

    private LayoutInflater mInflater;

    private int mItemResId;

    private ArrayList<Coin> mItems;

    public StickyGridHeadersSimpleArrayAdapter(Context context, ArrayList<Coin> items, int headerResId,
                                               int itemResId) {
        init(context, items, headerResId, itemResId);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public String getHeader(int position) {
        Coin item = getItem(position);
        CharSequence value;
        if (item instanceof CharSequence) {
            value = (CharSequence) item;
        } else {
            value = item.toString();
        }

        //return value.subSequence(0, 1).charAt(0);
        return item.getMark();
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mHeaderResId, parent, false);
            holder = new HeaderViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.item_header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        Coin item = getItem(position);
        CharSequence string;
        if (item instanceof CharSequence) {
            string = (CharSequence) item;
        } else {
            string = item.toString();
        }

        // set header text as mark of coin
        holder.textView.setText(item.getMark());

        return convertView;
    }

    @Override
    public Coin getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mItemResId, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.data_item_text);
            holder.imageView = (ImageView) convertView.findViewById(R.id.grid_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Coin item = getItem(position);
        holder.textView.setText(item.getRating());
        /**
         if (item instanceof CharSequence) {
         holder.textView.setText((CharSequence)item);
         } else {
         holder.textView.setText(item.toString());
         }
         */
        return convertView;
    }

    private void init(Context context, ArrayList<Coin> items, int headerResId, int itemResId) {
        this.mItems = items;
        this.mHeaderResId = headerResId;
        this.mItemResId = itemResId;
        mInflater = LayoutInflater.from(context);
    }

    protected class HeaderViewHolder {
        public TextView textView;
    }

    protected class ViewHolder {
        public TextView textView;
        public ImageView imageView;
    }
}
