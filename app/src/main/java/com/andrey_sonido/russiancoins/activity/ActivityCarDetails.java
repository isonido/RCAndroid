package com.andrey_sonido.russiancoins.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.andrey_sonido.russiancoins.Coin;
import com.andrey_sonido.russiancoins.MainActivity;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;

/**
 * Created by Sonido on 15.09.2015.
 */

public class ActivityCarDetails extends AppCompatActivity {
    Drawable mPressed;
    Drawable mUnPressed;

    private TextView mCreated;
    private ImageView mCarPic;
    private Coin coinDetails;
    private TextView mTbTitle;
    private TextView mTitle;
    private TextView mID;
    private TextView mSerial;
    private TextView mPriceRic;
    private TextView mText1;
    private TextView mQuality;
    private TextView mStill;
    private TextView mWeight;
    private TextView mWeightStill;
    private TextView mDiameter;
    private TextView mThickness;
    private TextView mPcs;
    private TextView mCreator;
    private TextView mSculptor;
    private TextView mDvor;
    private TextView mGurt;
    private TextView mAddChngBtn;
    private TextView mAddZakazBtn;
    private TextView mColCoins;
    private Button btnRevers;
    private Button btnAvers;
    private ImageButton plus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        getData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.str_car_details));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTbTitle = (TextView) findViewById(R.id.tb_title);
        mTitle = (TextView) findViewById(R.id.tvModel);
        mCarPic = (ImageView) findViewById(R.id.ivCarPic);
        mCreated = (TextView) findViewById(R.id.tv_create);
        mSerial = (TextView) findViewById(R.id.tv_serial);
        mPriceRic = (TextView) findViewById(R.id.priceRic);
        mID = (TextView) findViewById(R.id.tv_id);
        mText1 = (TextView) findViewById(R.id.tvText1);
        mQuality = (TextView) findViewById(R.id.tvQuality);
        mStill = (TextView) findViewById(R.id.tvStill);
        mWeight = (TextView) findViewById(R.id.tvWeight);
        mWeightStill = (TextView) findViewById(R.id.tvWeightStill);
        mDiameter = (TextView) findViewById(R.id.tvDiameter);
        mThickness = (TextView) findViewById(R.id.tvThickness);
        mPcs = (TextView) findViewById(R.id.tvPcs);
        mCreator = (TextView) findViewById(R.id.tvCreator);
        mSculptor = (TextView) findViewById(R.id.tvSculptor);
        mDvor = (TextView) findViewById(R.id.tvDvor);
        mGurt = (TextView) findViewById(R.id.tvGurt);
        mColCoins = (TextView) findViewById(R.id.colCoins);

        mAddChngBtn = (TextView) findViewById(R.id.btn_add_change);
        mAddChngBtn.setVisibility(View.VISIBLE);
        mAddChngBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //convert object to string
                Gson gson = new Gson();
                String json = gson.toJson(coinDetails);

                Intent myIntent = new Intent(ActivityCarDetails.this, ActivityCarDetailsAdd.class);
                myIntent.putExtra("CoinDetails", json);
                startActivityForResult(myIntent, 1);
            }
        });

        mAddZakazBtn = (TextView) findViewById(R.id.btn_zakaz);
        mAddZakazBtn.setVisibility(View.VISIBLE);
        mAddZakazBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //convert object to string
                Gson gson = new Gson();
                String json = gson.toJson(coinDetails);

                Intent myIntent = new Intent(ActivityCarDetails.this, ActivitySendEmailZakaz.class);
                myIntent.putExtra("CoinDetails", json);
                startActivityForResult(myIntent, 1);
            }
        });

        mPressed = getResources().getDrawable(R.color.btn_bg_color_pressed);
        mUnPressed = getResources().getDrawable(R.color.tb_text_color);
        btnRevers = (Button) findViewById(R.id.btn_revers);
        btnAvers = (Button) findViewById(R.id.btn_avers);
        btnRevers.setBackgroundDrawable(mPressed);
        btnAvers.setBackgroundDrawable(mUnPressed);

        View.OnClickListener oclBtn = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_revers:
                        btnRevers.setBackgroundDrawable(mPressed);
                        btnAvers.setBackgroundDrawable(mUnPressed);
                        //mCarPic.setImageResource(getResources().getIdentifier(coinDetails.getImageName2(), "drawable", getPackageName()));
                        Utils.setImageFromFile3(coinDetails, mCarPic, false);
                        break;
                    case R.id.btn_avers:
                        btnRevers.setBackgroundDrawable(mUnPressed);
                        btnAvers.setBackgroundDrawable(mPressed);
                        //mCarPic.setImageResource(getResources().getIdentifier(coinDetails.getImageName1(), "drawable", getPackageName()));
                        Utils.setImageFromFile2(coinDetails, mCarPic, false);
                        break;
                }
            }
        };

        btnRevers.setOnClickListener(oclBtn);
        btnAvers.setOnClickListener(oclBtn);

        if (coinDetails != null) {

            //mCarPic.setImageResource(getResources().getIdentifier(coinDetails.getImageName2(), "drawable", getPackageName()));
            Utils.setImageFromFile3(coinDetails, mCarPic, false);

            mTbTitle.setText(coinDetails.getRating());
            mTitle.setText(coinDetails.getTitle());
            if (coinDetails.getSerial() .equals("null")) {
                mSerial.setText(" ");
            } else {
                mSerial.setText(coinDetails.getSerial());
            }
            if (coinDetails.getPriceRic() != 0) {
                mPriceRic.setText(String.valueOf(coinDetails.getPriceRic()) + MainActivity.VALYUTA);
                mAddZakazBtn.setEnabled(true);
                mAddZakazBtn.setTextColor(getResources().getColor(R.color.btn_bg_color_pressed));
            } else {
                mPriceRic.setText("-");
                mAddZakazBtn.setEnabled(false);
                mAddZakazBtn.setTextColor(getResources().getColor(R.color.row_lv_background_color));
            }
            if (coinDetails.getAmountKppMt() + coinDetails.getAmountKppAt() != 0) {
                mColCoins.setText(String.valueOf(coinDetails.getAmountKppMt() + coinDetails.getAmountKppAt()) + " шт");
                mAddChngBtn.setText("Изменить");
            } else {
                mColCoins.setText("нет");
            }
            mCreated.setText(coinDetails.getCreatedOn());
            mID.setText(coinDetails.getId());
            mText1.setText(coinDetails.getText1());
            mQuality.setText(coinDetails.getQuality());
            mStill.setText(coinDetails.getStill());
            mWeightStill.setText(coinDetails.getWeightStill());
            mWeight.setText(coinDetails.getWeight());
            if (coinDetails.getDiameter() != null) {
                mDiameter.setText(coinDetails.getDiameter());
            } else {
                mDiameter.setText(coinDetails.getLength());
            }
            mThickness.setText(coinDetails.getThickness());
            mPcs.setText(coinDetails.getPcs());
            mCreator.setText(coinDetails.getCreator());
            mSculptor.setText(coinDetails.getSculptor());
            mDvor.setText(coinDetails.getDvor());
            mGurt.setText(coinDetails.getGurt());
            if (coinDetails.getText2() != null) {
                plus = (ImageButton) findViewById(R.id.plus);
                plus.setVisibility(View.VISIBLE);
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //convert object to string
                        Gson gson = new Gson();
                        String json = gson.toJson(coinDetails);

                        Intent myIntent = new Intent(ActivityCarDetails.this, ActivityInfo2.class);
                        myIntent.putExtra("CoinDetails", json);
                        startActivityForResult(myIntent, 1);
                    }
                });
            } else {
                plus = (ImageButton) findViewById(R.id.plus);
                plus.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void getData() {
        if (getIntent() != null) {
            Gson gson = new Gson();
            String json = getIntent().getExtras().getString("CoinDetails");
            coinDetails = gson.fromJson(json, new TypeToken<Coin>() {
            }.getType());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //etMenuInflater().inflate(R.menu.menu_activity_car_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(this, "Return to main", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
    }
}
