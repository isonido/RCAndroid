package com.andrey_sonido.russiancoins.adapters.grid;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sonido on 15.09.2015.
 */

public class StickyGridHeadersSimpleAdapterWrapper extends BaseAdapter implements
        StickyGridHeadersBaseAdapter {
    private StickyGridHeadersSimpleAdapter mDelegate;

    private HeaderData[] mHeaders;

    public StickyGridHeadersSimpleAdapterWrapper(StickyGridHeadersSimpleAdapter adapter) {
        mDelegate = adapter;
        adapter.registerDataSetObserver(new DataSetObserverExtension());
        mHeaders = generateHeaderList(adapter);
    }

    @Override
    public int getCount() {
        return mDelegate.getCount();
    }

    @Override
    public int getCountForHeader(int position) {
        return mHeaders[position].getCount();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        return mDelegate.getHeaderView(mHeaders[position].getRefPosition(), convertView, parent);
    }

    @Override
    public Object getItem(int position) {
        return mDelegate.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mDelegate.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mDelegate.getItemViewType(position);
    }

    @Override
    public int getNumHeaders() {
        return mHeaders.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mDelegate.getView(position, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return mDelegate.getViewTypeCount();
    }

    @Override
    public boolean hasStableIds() {
        return mDelegate.hasStableIds();
    }

    protected HeaderData[] generateHeaderList(StickyGridHeadersSimpleAdapter adapter) {
        Map<String, HeaderData> mapping = new HashMap<String, HeaderData>();
        List<HeaderData> headers = new ArrayList<HeaderData>();

        for (int i = 0; i < adapter.getCount(); i++) {
            String header = adapter.getHeader(i);
            HeaderData headerData = mapping.get(header);
            if (headerData == null) {
                headerData = new HeaderData(i);
                headers.add(headerData);
            }
            headerData.incrementCount();
            mapping.put(header, headerData);
        }

        return headers.toArray(new HeaderData[headers.size()]);
    }

    private final class DataSetObserverExtension extends DataSetObserver {
        @Override
        public void onChanged() {
            mHeaders = generateHeaderList(mDelegate);
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mHeaders = generateHeaderList(mDelegate);
            notifyDataSetInvalidated();
        }
    }

    private class HeaderData {
        private int mCount;

        private int mRefPosition;

        public HeaderData(int refPosition) {
            mRefPosition = refPosition;
            mCount = 0;
        }

        public int getCount() {
            return mCount;
        }

        public int getRefPosition() {
            return mRefPosition;
        }

        public void incrementCount() {
            mCount++;
        }
    }

}
