package com.andrey_sonido.russiancoins.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.andrey_sonido.russiancoins.Coin;
import com.andrey_sonido.russiancoins.MainActivity;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.Utils;
import com.andrey_sonido.russiancoins.adapters.grid.StickyGridHeadersGridView;
import com.andrey_sonido.russiancoins.adapters.grid.StickyGridHeadersSimpleArrayAdapter;

import static com.andrey_sonido.russiancoins.MainActivity.mScrollPositionSelected;

//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentActivity;
//import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sonido on 23.06.2015.
 */

public class FragmentGrid extends AbstractFragment {
    //private Logger log = LoggerFactory.getLogger(FragmentGrid.class);
    private StickyGridHeadersGridView mGridView;
    //private StickyGridHeadersSimpleArrayAdapter<Coin> mAdapter;
    private FragmentActivity mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.init();
        super.sortData(null);
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        mGridView = (StickyGridHeadersGridView) view.findViewById(R.id.asset_grid);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Save view state
                        MainActivity.mScrollState = mGridView.onSaveInstanceState();

                        itemClickAction(position);
                    }
                });
            }
        });

        mGridView.setOnScrollListener(mScrollListener);

        try {
            setAdapter();
        } catch (Exception ex) {
            ex.printStackTrace();
            //log.error(Utils.getErrorLogHeader(), ex);
        }
        return view;
    }

    void setAdapter() {
        // 3. Set adapter
        if (super.coins != null && super.coins.size() > 0) {
            StickyGridHeadersSimpleArrayAdapter<Coin> mAdapter = new StickyGridHeadersSimpleArrayAdapter<Coin>(
                    mContext, super.coins, R.layout.item_header, R.layout.grid_item);
            mGridView.setAdapter(mAdapter);
            mGridView.setSelection(mScrollPositionSelected);
            if (MainActivity.mScrollState != null) {
                mGridView.onRestoreInstanceState(MainActivity.mScrollState);
            }
        } else {
            if (mContext != null)
                Utils.EmptyMessage2(((AppCompatActivity) mContext).getSupportActionBar().getThemedContext(), "Ничего не найдено.");
            if (mTimer != null) {
                mTimer.cancel();
            }
        }
    }
}
