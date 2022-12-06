package com.andrey_sonido.russiancoins.fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.andrey_sonido.russiancoins.MainActivity;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.Utils;
import com.andrey_sonido.russiancoins.adapters.grid.StickyListHeaderAdapter;
import com.andrey_sonido.russiancoins.adapters.list.ExpandableStickyListHeadersListView;

import java.util.WeakHashMap;

//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentActivity;
//import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sonido on 21.06.2015.
 */

public class FragmentList extends AbstractFragment {
    //private Logger log = LoggerFactory.getLogger(FragmentList.class);
    private ExpandableStickyListHeadersListView mListView;
    StickyListHeaderAdapter mAdapter;
    WeakHashMap<View, Integer> mOriginalViewHeightPool = new WeakHashMap<>();
    private FragmentActivity mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.init();
        super.sortData(null);
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mListView = (ExpandableStickyListHeadersListView) view.findViewById(R.id.list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // Save view state
                MainActivity.mScrollState = mListView.onSaveInstanceState();

                itemClickAction(position);
            }
        });

        mListView.setOnScrollListener(mScrollListener);

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
        if (coins != null && coins.size() > 0) {
            mListView.setAnimExecutor(new AnimationExecutor());
            mAdapter = new StickyListHeaderAdapter(mContext, coins);
            mListView.setAdapter(mAdapter);
            mListView.setSelection(MainActivity.mScrollPositionSelected);
            if (MainActivity.mScrollState != null) {
                mListView.onRestoreInstanceState(MainActivity.mScrollState);
            }
            if (mListView.getAdapter() != null) {
                ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();

            } else {
                if (mContext != null)
                    Utils.EmptyMessage2(((AppCompatActivity) mContext).getSupportActionBar().getThemedContext(), "Ничего не найдено.");
                if (mTimer != null) {
                    mTimer.cancel();
                }
            }
        }
    }

    //animation executor
    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            if (ExpandableStickyListHeadersListView.ANIMATION_EXPAND == animType && target.getVisibility() == View.VISIBLE) {
                return;
            }
            if (ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE == animType && target.getVisibility() != View.VISIBLE) {
                return;
            }
            if (mOriginalViewHeightPool.get(target) == null) {
                mOriginalViewHeightPool.put(target, target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();
        }
    }
}
