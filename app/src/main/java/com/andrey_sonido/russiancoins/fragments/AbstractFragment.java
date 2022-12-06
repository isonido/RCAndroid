package com.andrey_sonido.russiancoins.fragments;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.andrey_sonido.russiancoins.BuildConfig;
import com.andrey_sonido.russiancoins.Coin;
import com.andrey_sonido.russiancoins.MainActivity;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.Utils;
import com.andrey_sonido.russiancoins.activity.ActivityCarDetails;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import static com.andrey_sonido.russiancoins.MainActivity.FRAGMENT_CATALOG;
import static com.andrey_sonido.russiancoins.MainActivity.FRAGMENT_COLLECTION;
import static com.andrey_sonido.russiancoins.MainActivity.FRAGMENT_STORE;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;

/**
 * Created by Sonido on 22.09.2016.
 */

public class AbstractFragment extends Fragment {
    //private Logger log = LoggerFactory.getLogger(FragmentGrid.class);
    EditText etFind;
    Button btnCancelFind;
    Timer mTimer;
    ArrayList<Coin> coins = null;
    private FragmentActivity mContext;
    private String mFragmentTag;


    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                    || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                Utils.hideKeyboard(mContext, getView());
                actionSearch(etFind.getText());
            }
            return false;
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        int delayTimer = 1300;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (String.valueOf(s).contains(" ")) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                actionSearch(etFind.getText());
            }
            if (String.valueOf(s).contains(" ") && s.length() > 3) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.hideKeyboard(mContext, getView());
                                actionSearch(etFind.getText());
                            }
                        });
                    }
                }, delayTimer);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 2 && !String.valueOf(s).contains(" ")) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.hideKeyboard(mContext, getView());
                                actionSearch(etFind.getText());
                            }
                        });
                    }
                }, delayTimer);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (mTimer != null) {
                mTimer.cancel();
            }
        }
    };

    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                hideSearchBox();
                return true;
            }
            return false;
        }
    };

    void init() {
        mContext = getActivity();
        mFragmentTag = getTag();
        etFind = (EditText) mContext.findViewById(R.id.et_find);
        btnCancelFind = (Button) mContext.findViewById(R.id.btn_find);
        etFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btnCancelFind.setVisibility(View.VISIBLE);
                }
            }
        });
        etFind.setOnEditorActionListener(editorActionListener);

        etFind.addTextChangedListener(textWatcher);

        etFind.setOnKeyListener(onKeyListener);

        btnCancelFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                hideSearchBox();
                sortData(null);
                updateFragmentAdapter();
            }
        });
    }

    private void actionSearch(Editable text) {
        String findMask = String.valueOf(text)
                .replace(" ", " ")
                //.replace("\n", "")
                //.replace("\r", "")
                ;
        //etFind.setText(findMask);
        //log.info("search: \'{}\'", findMask);
        sortData(Utils.findItemsByMask(findMask));
        updateFragmentAdapter();
    }

    private void updateFragmentAdapter() {
        Fragment visibleFragment = mContext.getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (visibleFragment == null) {
            if (BuildConfig.DEBUG) {
                //log.error("visibleFragment is null");
            } else {
                //log.error("{} visibleFragment is null", Utils.getErrorLogHeader());
            }
            return;
        }
        if (visibleFragment instanceof FragmentGrid) {
            ((FragmentGrid) visibleFragment).setAdapter();
        } else if (visibleFragment instanceof FragmentList) {
            ((FragmentList) visibleFragment).setAdapter();
        }
    }

    void itemClickAction(int position){
        // Save scroll position
        MainActivity.mScrollPositionSelected = position;

        hideSearchBox();

        //convert object to string
        Gson gson = new Gson();
        String json = gson.toJson(coins.get(position));

        Intent myIntent = new Intent(getActivity(), ActivityCarDetails.class);
        myIntent.putExtra("CoinDetails", json);
        getActivity().startActivityForResult(myIntent, 1);
    }

    protected void hideSearchBox() {
        Utils.hideKeyboard(getActivity(), getView());
        etFind.setVisibility(View.GONE);
        etFind.setText("");
        btnCancelFind.setVisibility(View.GONE);
    }

    void sortData(ArrayList<Coin> CoinsLoc) {
        try {
            // 1. Your data source
            //if (CoinsLoc == null || CoinsLoc.size() == 0) {
            if (CoinsLoc == null) {
                setDefaultDataForFragment(mFragmentTag);
            } else if (CoinsLoc.size() == 0) {
                //coins = CoinsLoc;
            } else {
                coins = CoinsLoc;
            }

            switch (getTag()) {
                case FRAGMENT_CATALOG:
                    coins = Utils.compareCoins(coins);
                    break;
                case FRAGMENT_COLLECTION:
                    break;
                case FRAGMENT_STORE:
                    //coins = Utils.filterCoinsWithPrices(coins);
                    //coins = Utils.compareCoins(coins);
                    break;
            }

            // Sort them using the Mark of the current coin
            MarkComparator markComparator = new MarkComparator();
            Collections.sort(coins, markComparator);

            // Sort using the Model of the current coin
            ModelComparator modelComparator = new ModelComparator();
            Collections.sort(coins, modelComparator);

        } catch (Exception ex) {
            ex.printStackTrace();
            //log.error(Utils.getErrorLogHeader(), ex);
        }
    }

    private void setDefaultDataForFragment(String fragmentTag) {
        switch (fragmentTag){
            case FRAGMENT_CATALOG:
                coins = Utils.getFilteredData(MainActivity.FRAGMENT_CATALOG);
                if (coins == null) {
                    coins = new ArrayList<>(Utils.getCoinsDBOrig());
                }
                break;
            case FRAGMENT_COLLECTION:
                coins = Utils.getFilteredData(MainActivity.FRAGMENT_COLLECTION);
                if (coins == null) {
                    coins = new ArrayList<>(Utils.getCoinsDBPrefs());
                }
                break;
            case FRAGMENT_STORE:
                coins = Utils.getFilteredData(MainActivity.FRAGMENT_STORE);
                if (coins == null) {
                    coins = new ArrayList<>(Utils.getCoinsDBWithPrices());
                }
                break;
        }
    }

    AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
        int currentTotalItemCount;
        int currentFirstVisibleItem;
        int currentVisibleItemCount;
        int currentScrollState;
        int mLastFirstVisibleItem = 1;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            this.currentScrollState = scrollState;
            this.isScrollEndOfList();

            if (scrollState == SCROLL_STATE_IDLE) {
                //log.info("scrolling stopped...");
            }
            if (currentFirstVisibleItem == 0) {
                mLastFirstVisibleItem = 1;
            }
            if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                //log.info("scrolling down...");
                hideSearchBox();
            } else if (currentFirstVisibleItem < mLastFirstVisibleItem && currentFirstVisibleItem == 0) {
                etFind.setVisibility(View.VISIBLE);
                btnCancelFind.setVisibility(View.VISIBLE);
            }
            mLastFirstVisibleItem = currentFirstVisibleItem;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            currentFirstVisibleItem = firstVisibleItem;
            currentVisibleItemCount = visibleItemCount;
            currentTotalItemCount = totalItemCount;
        }

        private void isScrollEndOfList() {
            if (currentFirstVisibleItem + currentVisibleItemCount == currentTotalItemCount
                    && currentTotalItemCount > 0
                    && currentScrollState == SCROLL_STATE_IDLE) {
                /*** In this way I detect if there's been a scroll which has completed ***/
                /*** do the work for load more date! ***/
                //log.info("get end of list...");
            }
        }
    };



    class MarkComparator implements Comparator<Coin> {
        @Override
        public int compare(Coin o1, Coin o2) {
            return o2.getHeader().compareTo(o1.getHeader());
        }
    }
    class ModelComparator implements Comparator<Coin> {
        @Override
        public int compare(Coin Coin1, Coin Coin2) {
            return Coin2.getNumber().compareTo(Coin1.getNumber());
        }
    }
}
