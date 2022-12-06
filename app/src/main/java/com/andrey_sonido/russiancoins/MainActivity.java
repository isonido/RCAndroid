package com.andrey_sonido.russiancoins;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.andrey_sonido.russiancoins.activity.ActivityInfo;
import com.andrey_sonido.russiancoins.activity.ActivitySendEmail;
import com.andrey_sonido.russiancoins.fragments.FragmentList;
import com.andrey_sonido.russiancoins.fragments.FragmentProgressBarInfinite;
import com.andrey_sonido.russiancoins.fragments.FragmentStatic;
import com.andrey_sonido.russiancoins.helpers.XmlParserHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sonido on 15.09.2015.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Logger log = LoggerFactory.getLogger(MainActivity.class);
    public static final java.lang.String ARG_FRAGMENT_TYPE = "fragment_type";
    int mPressed, mUnPressed, mPressedText, mUnPressedText;
    private ViewFlipper vfSpinnerTitleChng;
    private DrawerLayout mDrawer;
    private CharSequence mTitle;
    private ListView mDrawerList;
    private ListView mYearList;
    private ListView mNominalList;
    private Spinner spnTBCat;
    private Button ivRefresh;
    //private ImageView ivChangeView;
    private Handler mHandler;
    private Button mDrawerBtnResetFilters, mDrawerBtnYear, mDrawerBtnTirag, mDrawerBtnNominal;
    private Button mDrawerBtnDvorAll, mDrawerBtnDvorMmd, mDrawerBtnDvorSpmd;
    private Button mDrawerBtnStillAll, mDrawerBtnDrag, mDrawerBtnNedrag;
    private Button mDrawerbtnOnsite;

    private ImageView ivBannerPic;
    private TextView tvPhoneNumber;
    private ImageButton ibCall;

    //public static boolean mListUserView;
    //LinearLayout mDrawerLayoutMain;

    public final static String FRAGMENT_CATALOG = "fragment_catalog";
    public final static String FRAGMENT_COLLECTION = "fragment_collection";
    public final static String FRAGMENT_STORE = "fragment_store";
    public final static String FRAGMENT_STATIC = "fragment_static";
    public final static String FRAGMENT_WAIT = "fragment_wait";
    public final static String GROUP_BY_NOMINAL = "ByNominal";
    public final static String GROUP_BY_TIRAG = "ByTirag";
    public final static String GROUP_BY_YEAR = "ByYear";
    public final static String FILTER_ALL = "Все";
    public final static int TRANS_ALL = 0;
    public final static int TRANS_MMD = 1;
    public final static int TRANS_SPMD = 2;
    public final static int STILL_ALL = 0;
    public final static int STILL_DRAG = 1;
    public final static int STILL_NEDRAG = 2;
    public final static String VALYUTA = " р.";

    String groupBy = GROUP_BY_YEAR;
    //String country = FILTER_ALL;
    int transmission = TRANS_ALL;
    int still = STILL_ALL;

    public ArrayList<String> mDATA = new ArrayList<String>();
    public ArrayList<String> mDATA2 = new ArrayList<String>();
    public ArrayList<String> mDATA3 = new ArrayList<String>();
    private Timer mTimer = null;

    public static Parcelable mScrollState = null;
    public static int mScrollPositionSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mTitle = "";
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }

        vfSpinnerTitleChng = (ViewFlipper) findViewById(R.id.vf_spinner_title);
        spnTBCat = (Spinner) findViewById(R.id.toolbar_spinner_cat);
        mDrawerList = (ListView) findViewById(R.id.lv_fragment_drawer);
        ArrayAdapter<String> SpinnerAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_item, new String[]{"Каталог", "Моя коллекция", "Магазин", "Статистика"});
        SpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        if (spnTBCat != null) {
            spnTBCat.setAdapter(SpinnerAdapter);
            spnTBCat.setVisibility(View.VISIBLE);
        }
        spnTBCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resetScrollPosition();
                if (Utils.getCoinsDBOrig().size() > 0) {
                    switch (position) {
                        case 0:
                            beforeChangeFragment(FRAGMENT_CATALOG);
                            break;
                        case 1:
                            beforeChangeFragment(FRAGMENT_COLLECTION);
                            break;
                        case 2:
                            beforeChangeFragment(FRAGMENT_STORE);
                            break;
                        case 3:
                            beforeChangeFragment(FRAGMENT_STATIC);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ivRefresh = (Button) findViewById(R.id.iv_refresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //refresh(ivRefresh, ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_refresh_new));
                //Toast.makeText(MainActivity.this, "Parsing DONE", Toast.LENGTH_SHORT).show();
                try {
                    resetScrollPosition();
                    Utils.loadPrices();
                    Utils.setFilteredData(still, transmission);
                    checkFragment();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        /*
        ivChangeView = (ImageView) findViewById(R.id.iv_change_view);
        ivChangeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mListUserView) {
                    mListUserView = true;
                    refresh(ivChangeView, getResources().getDrawable(R.drawable.ic_view_module_white_24dp));
                    //Toast.makeText(ActivityMain.this, "Changed to: List", Toast.LENGTH_SHORT).show();
                } else {
                    mListUserView = false;
                    refresh(ivChangeView, getResources().getDrawable(R.drawable.ic_view_list_white_24dp));
                    //Toast.makeText(ActivityMain.this, "Changed to: Grid", Toast.LENGTH_SHORT).show();
                }
                checkFragment();
            }
        }); */

        mPressed = ContextCompat.getColor(MainActivity.this, R.color.btn_bg_color_pressed);
        mPressedText = ContextCompat.getColor(MainActivity.this, R.color.btn_bg_color);
        mUnPressed = ContextCompat.getColor(MainActivity.this, R.color.btn_bg_color);
        mUnPressedText = ContextCompat.getColor(MainActivity.this, R.color.btn_bg_color_pressed);

        mDrawerBtnYear = (Button) findViewById(R.id.btnYear);
        mDrawerBtnNominal = (Button) findViewById(R.id.btnNominal);
        mDrawerBtnTirag = (Button) findViewById(R.id.btnTirag);
        mDrawerBtnNominal.setBackgroundColor(mUnPressed);
        mDrawerBtnNominal.setTextColor(mUnPressedText);
        mDrawerBtnTirag.setBackgroundColor(mUnPressed);
        mDrawerBtnTirag.setTextColor(mUnPressedText);

        mDrawerBtnDvorAll = (Button) findViewById(R.id.btnDvorAll);
        mDrawerBtnDvorMmd = (Button) findViewById(R.id.btnDvorMmd);
        mDrawerBtnDvorSpmd = (Button) findViewById(R.id.btnDvorSpmd);
        mDrawerBtnDvorMmd.setBackgroundColor(mUnPressed);
        mDrawerBtnDvorMmd.setTextColor(mUnPressedText);
        mDrawerBtnDvorSpmd.setBackgroundColor(mUnPressed);
        mDrawerBtnDvorSpmd.setTextColor(mUnPressedText);

        mDrawerBtnStillAll = (Button) findViewById(R.id.btnStillAll);
        mDrawerBtnDrag = (Button) findViewById(R.id.btnDrag);
        mDrawerBtnNedrag = (Button) findViewById(R.id.btnNedrag);
        mDrawerBtnDrag.setBackgroundColor(mUnPressed);
        mDrawerBtnDrag.setTextColor(mUnPressedText);
        mDrawerBtnNedrag.setBackgroundColor(mUnPressed);
        mDrawerBtnNedrag.setTextColor(mUnPressedText);

        mDrawerBtnResetFilters = (Button) findViewById(R.id.btnResetFilters);
        mDrawerbtnOnsite = (Button) findViewById(R.id.btnOnsite);


        mDrawerBtnYear.setOnClickListener(this);
        mDrawerBtnNominal.setOnClickListener(this);
        mDrawerBtnTirag.setOnClickListener(this);
        mDrawerBtnDvorAll.setOnClickListener(this);
        mDrawerBtnDvorMmd.setOnClickListener(this);
        mDrawerBtnDvorSpmd.setOnClickListener(this);
        mDrawerBtnStillAll.setOnClickListener(this);
        mDrawerBtnResetFilters.setOnClickListener(this);
        mDrawerbtnOnsite.setOnClickListener(this);
        mDrawerBtnDrag.setOnClickListener(this);
        mDrawerBtnNedrag.setOnClickListener(this);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawer != null) {
            mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            mDrawer.setFocusableInTouchMode(false);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                vfSpinnerTitleChng.setDisplayedChild(0);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                vfSpinnerTitleChng.setDisplayedChild(0);
                invalidateOptionsMenu();
                if (mDATA.size() == 0) {
                    setListOfDrawer();
                }
            }
        };
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.setmCoinYearFilter(FILTER_ALL);
                Utils.setmCoinNominalFilter(FILTER_ALL);

                Utils.setmPatternFilter(parent.getAdapter().getItem(position).toString());
                mDrawerList.setItemChecked(position, true);
                Utils.setFilteredData(still, transmission);
                checkFragment();

                String pattern = parent.getAdapter().getItem(position).toString();
                mDATA2 = Utils.getListOfMarkByCountry(pattern);
                mDATA3 = Utils.getListOfNominalByCountry(pattern);
                setAdapter2();

                mYearList.setItemChecked(0, true);
                mNominalList.setItemChecked(0, true);
            }
        });
        mYearList = (ListView) findViewById(R.id.lv_year);

        mYearList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.setmCoinYearFilter(parent.getAdapter().getItem(position).toString());
                mYearList.setItemChecked(position, true);
                Utils.setFilteredData(still, transmission);
                checkFragment();
                resetScrollPosition();
            }
        });

        mNominalList = (ListView) findViewById(R.id.lv_nominal);

        mNominalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.setmCoinNominalFilter(parent.getAdapter().getItem(position).toString());
                mNominalList.setItemChecked(position, true);
                Utils.setFilteredData(still, transmission);
                checkFragment();
                resetScrollPosition();
            }
        });

        mHandler = new Handler();
        getOrigData();
    }

    @Override
    public void onClick(View v) {
        //String pattern = FILTER_ALL;
        switch (v.getId()) {
            case R.id.btnOnsite:
                String bkv = "http://www.ricgold.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(bkv));
                startActivity(i);
                break;
            case R.id.btnResetFilters:
                groupBy = GROUP_BY_YEAR;
                transmission = TRANS_ALL;
                still = STILL_ALL;
                Utils.setmPatternFilter(FILTER_ALL);
                Utils.setmCoinYearFilter(FILTER_ALL);
                Utils.setmCoinNominalFilter(FILTER_ALL);
                mDrawerBtnYear.performClick();
                mDrawerBtnDvorAll.performClick();
                mDrawerBtnStillAll.performClick();

                mDATA2 = Utils.getListOfMarkByCountry(FILTER_ALL);
                mDATA3 = Utils.getListOfNominalByCountry(FILTER_ALL);
                setAdapter2();

                mDrawerList.setItemChecked(0, true);
                mYearList.setItemChecked(0, true);
                mNominalList.setItemChecked(0, true);
                break;
            case R.id.btnYear:
                //Utils.setmPatternFilter(FILTER_ALL);
                groupBy = GROUP_BY_YEAR;
                mDrawerBtnYear.setBackgroundColor(mPressed);
                mDrawerBtnYear.setTextColor(mPressedText);
                mDrawerBtnNominal.setBackgroundColor(mUnPressed);
                mDrawerBtnNominal.setTextColor(mUnPressedText);
                mDrawerBtnTirag.setBackgroundColor(mUnPressed);
                mDrawerBtnTirag.setTextColor(mUnPressedText);
                break;
            case R.id.btnNominal:
                //Utils.setmPatternFilter(FILTER_ALL);
                groupBy = GROUP_BY_NOMINAL;
                mDrawerBtnYear.setBackgroundColor(mUnPressed);
                mDrawerBtnYear.setTextColor(mUnPressedText);
                mDrawerBtnNominal.setBackgroundColor(mPressed);
                mDrawerBtnNominal.setTextColor(mPressedText);
                mDrawerBtnTirag.setBackgroundColor(mUnPressed);
                mDrawerBtnTirag.setTextColor(mUnPressedText);
                break;
            case R.id.btnTirag:
                //Utils.setmPatternFilter(FILTER_ALL);
                groupBy = GROUP_BY_TIRAG;
                mDrawerBtnYear.setBackgroundColor(mUnPressed);
                mDrawerBtnYear.setTextColor(mUnPressedText);
                mDrawerBtnNominal.setBackgroundColor(mUnPressed);
                mDrawerBtnNominal.setTextColor(mUnPressedText);
                mDrawerBtnTirag.setBackgroundColor(mPressed);
                mDrawerBtnTirag.setTextColor(mPressedText);
                break;
            case R.id.btnDvorAll:
                transmission = TRANS_ALL;
                mDrawerBtnDvorAll.setBackgroundColor(mPressed);
                mDrawerBtnDvorAll.setTextColor(mPressedText);
                mDrawerBtnDvorMmd.setBackgroundColor(mUnPressed);
                mDrawerBtnDvorMmd.setTextColor(mUnPressedText);
                mDrawerBtnDvorSpmd.setBackgroundColor(mUnPressed);
                mDrawerBtnDvorSpmd.setTextColor(mUnPressedText);
                break;
            case R.id.btnDvorMmd:
                transmission = TRANS_MMD;
                mDrawerBtnDvorAll.setBackgroundColor(mUnPressed);
                mDrawerBtnDvorAll.setTextColor(mUnPressedText);
                mDrawerBtnDvorMmd.setBackgroundColor(mPressed);
                mDrawerBtnDvorMmd.setTextColor(mPressedText);
                mDrawerBtnDvorSpmd.setBackgroundColor(mUnPressed);
                mDrawerBtnDvorSpmd.setTextColor(mUnPressedText);
                break;
            case R.id.btnDvorSpmd:
                transmission = TRANS_SPMD;
                mDrawerBtnDvorAll.setBackgroundColor(mUnPressed);
                mDrawerBtnDvorAll.setTextColor(mUnPressedText);
                mDrawerBtnDvorMmd.setBackgroundColor(mUnPressed);
                mDrawerBtnDvorMmd.setTextColor(mUnPressedText);
                mDrawerBtnDvorSpmd.setBackgroundColor(mPressed);
                mDrawerBtnDvorSpmd.setTextColor(mPressedText);
                break;
            case R.id.btnStillAll:
                still = STILL_ALL;
                mDrawerBtnStillAll.setBackgroundColor(mPressed);
                mDrawerBtnStillAll.setTextColor(mPressedText);
                mDrawerBtnDrag.setBackgroundColor(mUnPressed);
                mDrawerBtnDrag.setTextColor(mUnPressedText);
                mDrawerBtnNedrag.setBackgroundColor(mUnPressed);
                mDrawerBtnNedrag.setTextColor(mUnPressedText);
                break;
            case R.id.btnDrag:
                still = STILL_DRAG;
                mDrawerBtnStillAll.setBackgroundColor(mUnPressed);
                mDrawerBtnStillAll.setTextColor(mUnPressedText);
                mDrawerBtnDrag.setBackgroundColor(mPressed);
                mDrawerBtnDrag.setTextColor(mPressedText);
                mDrawerBtnNedrag.setBackgroundColor(mUnPressed);
                mDrawerBtnNedrag.setTextColor(mUnPressedText);
                break;
            case R.id.btnNedrag:
                still = STILL_NEDRAG;
                mDrawerBtnStillAll.setBackgroundColor(mUnPressed);
                mDrawerBtnStillAll.setTextColor(mUnPressedText);
                mDrawerBtnDrag.setBackgroundColor(mUnPressed);
                mDrawerBtnDrag.setTextColor(mUnPressedText);
                mDrawerBtnNedrag.setBackgroundColor(mPressed);
                mDrawerBtnNedrag.setTextColor(mPressedText);
                break;
        }
        try {
            resetScrollPosition();
            Utils.setGroupBy(groupBy);
            //setListOfDrawer();
            checkFragment();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setListOfDrawer() {
        mDATA = Utils.getListOfSerial(FILTER_ALL);
        mDATA2 = Utils.getListOfMarkByCountry(FILTER_ALL);
        mDATA3 = Utils.getListOfNominalByCountry(FILTER_ALL);

        setAdapter();
        setAdapter2();
    }

    private void resetScrollPosition() {
        mScrollPositionSelected = 0;
        mScrollState = null;
    }

    private void checkFragment() {
        if (checkVisibleFragment(FRAGMENT_CATALOG)) {
            beforeChangeFragment(FRAGMENT_CATALOG);
        } else if (checkVisibleFragment(FRAGMENT_COLLECTION)) {
            beforeChangeFragment(FRAGMENT_COLLECTION);
        } else if (checkVisibleFragment(FRAGMENT_STORE)) {
            beforeChangeFragment(FRAGMENT_STORE);
        } else if (checkVisibleFragment(FRAGMENT_STATIC)) {
            beforeChangeFragment(FRAGMENT_STATIC);
        }
    }

    private void beforeChangeFragment(final String fragmentTag) {
        Utils.setmVisibleFragment(fragmentTag);
        Utils.setFilteredData(still, transmission);
        setFragment(fragmentTag);
    }

    private void setProgressBar() {
        changeFragment(new FragmentProgressBarInfinite(), FRAGMENT_WAIT);
    }

    private void setFragment(String fragmentTag) {

        switch (fragmentTag) {
            case FRAGMENT_CATALOG:
                changeFragment(new FragmentList(), fragmentTag);
                break;
            case FRAGMENT_COLLECTION:
                changeFragment(new FragmentList(), fragmentTag);
                break;
            case FRAGMENT_STORE:
                changeFragment(new FragmentList(), fragmentTag);
                break;
            case FRAGMENT_STATIC:
                changeFragment(new FragmentStatic(), FRAGMENT_STATIC);
                break;
        }
        /**
         changeFragment(new FragmentList(), fragmentTag);
         */
    }

    public void callAd(View view) {
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        if (tvPhoneNumber != null) {
            final int REQUEST_PHONE_CALL = 1;
            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPhoneNumber.getText()));
            try {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    startActivity(in);
                }
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void emailAd(View view) {
        Intent myIntent = new Intent(this, ActivitySendEmail.class);
        startActivityForResult(myIntent, 1);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void info(View view) {
        Intent myIntent = new Intent(MainActivity.this, ActivityInfo.class);
        startActivity(myIntent);
    }

    private void getOrigData() {
        if (Utils.getCoinsDBOrig().size() == 0) {
            setProgressBar();
            parseXML(getResources().getXml(R.xml.test));
            if (mTimer != null) {
                mTimer.cancel();
            }
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {

                }
            }, 10000);
        }
        if (Utils.getCoinsDBPrefs().size() == 0) {
            Utils.loadData(MainActivity.this);
        }
        if (Utils.getCoinsDBWithPrices().size() == 0) {
            Utils.loadPrices();
        }
    }

    private void parseXML(XmlPullParser xpp) {
        XmlParserHelper.parseXMLbyStack(new XmlParserHelper.LoadListener() {
            @Override
            public void OnParseComplete(final Object result) {
                Utils.setCoinsDBOrig((ArrayList<Coin>) result);
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
                changeFragment(new FragmentList(), FRAGMENT_CATALOG);
            }

            @Override
            public void OnParseError(final Exception error) {
                Log.d("OnParseError: ", error.getMessage());
            }
        }, xpp);
    }

    private void setAdapter() {
        mDrawerList.setAdapter(new AppSectionAdapter());
    }

    private void setAdapter2() {
        mYearList.setAdapter(new AppSectionAdapter2());
        mNominalList.setAdapter(new AppSectionAdapter3());
    }

    class AppSectionAdapter extends BaseAdapter {
        private class MyRow {
            TextView tvMenuItem;
            LinearLayout llRowDrawer;
        }

        public ArrayList<String> menu = mDATA;

        @Override
        public int getCount() {
            return menu.size();
        }

        @Override
        public Object getItem(int i) {
            return menu.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyRow myRow;
            if (view == null) {
                myRow = new MyRow();
                view = getLayoutInflater().inflate(R.layout.row_fragment_drawer, viewGroup, false);
                myRow.tvMenuItem = (TextView) view.findViewById(R.id.tv_menu_item);
                myRow.llRowDrawer = (LinearLayout) view.findViewById(R.id.ll_row_drawer);
                view.setTag(myRow);
            } else {
                myRow = (MyRow) view.getTag();
            }
            myRow.tvMenuItem.setText(menu.get(i));
            return view;
        }
    }

    class AppSectionAdapter2 extends BaseAdapter {
        private class MyRow {
            TextView tvMenuItem;
            LinearLayout llRowDrawer;
        }

        public ArrayList<String> menu = mDATA2;

        @Override
        public int getCount() {
            return menu.size();
        }

        @Override
        public Object getItem(int i) {
            return menu.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyRow myRow;
            if (view == null) {
                myRow = new MyRow();
                view = getLayoutInflater().inflate(R.layout.row_fragment_drawer, viewGroup, false);
                myRow.tvMenuItem = (TextView) view.findViewById(R.id.tv_menu_item);
                myRow.llRowDrawer = (LinearLayout) view.findViewById(R.id.ll_row_drawer);
                view.setTag(myRow);
            } else {
                myRow = (MyRow) view.getTag();
            }
            myRow.tvMenuItem.setText(menu.get(i));
            return view;
        }
    }

    class AppSectionAdapter3 extends BaseAdapter {
        private class MyRow {
            TextView tvMenuItem;
            LinearLayout llRowDrawer;
        }

        public ArrayList<String> menu = mDATA3;

        @Override
        public int getCount() {
            return menu.size();
        }

        @Override
        public Object getItem(int i) {
            return menu.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyRow myRow;
            if (view == null) {
                myRow = new MyRow();
                view = getLayoutInflater().inflate(R.layout.row_fragment_drawer, viewGroup, false);
                myRow.tvMenuItem = (TextView) view.findViewById(R.id.tv_menu_item);
                myRow.llRowDrawer = (LinearLayout) view.findViewById(R.id.ll_row_drawer);
                view.setTag(myRow);
            } else {
                myRow = (MyRow) view.getTag();
            }
            myRow.tvMenuItem.setText(menu.get(i));
            return view;
        }
    }

    public void changeFragmentBack(Fragment fragment) {
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.content_frame, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);
        trans.commit();
        //ivChangeView.setVisibility(View.INVISIBLE);
    }

    public void changeFragment(Fragment fragment, String fragmentTag) {
        mHandler.post(new CommitFragmentRunnable(fragment, fragmentTag));
        //ivChangeView.setVisibility(View.VISIBLE);
    }

    private class CommitFragmentRunnable implements Runnable {

        private Fragment fragment;
        private String fragmentTag;
        private Bundle args = new Bundle();

        CommitFragmentRunnable(Fragment fragment, String fragmentTag) {
            this.fragment = fragment;
            this.fragmentTag = fragmentTag;
        }

        @Override
        public void run() {
            try {
                args.putString(ARG_FRAGMENT_TYPE, fragmentTag);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.content_frame, fragment, fragmentTag)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    boolean checkVisibleFragment(String tag) {
        Fragment myFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (myFragment != null) {
            boolean isVisible = myFragment.isVisible();
            if (isVisible) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkFragment();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0 && !mDrawer.isDrawerOpen(GravityCompat.START)) {
            fm.popBackStack();
            //ivChangeView.setVisibility(View.VISIBLE);
        } else {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                log.info("exit app");
                appExit();
            } else {
                log.info("open menu");
                mDrawer.openDrawer(GravityCompat.START);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
/**
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
*/

    public void appExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.app_exit_dialog)).setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    //System.exit(0);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };
}
