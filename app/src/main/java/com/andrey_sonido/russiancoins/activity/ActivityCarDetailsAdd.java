package com.andrey_sonido.russiancoins.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by Sonido on 02.12.2015.
 */
public class ActivityCarDetailsAdd extends AppCompatActivity {

    private Button mMinusBtnMT;
    private Button mPlusBtnMT;
    private EditText mAmountMT;
    private TextView mDvor1;
    private Button mMinusBtnAT;
    private Button mPlusBtnAT;
    private EditText mAmountAT;
    private TextView mDvor2;
    private TextView mModel;
    private TextView mTitle;
    private ImageView mCarPic;
    private TextView mSaveBtn;
    private Coin coinDetails;
    private TextView mTbTitle;
    private LinearLayout mLlAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details_add);
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

        mLlAuto = (LinearLayout) findViewById(R.id.llAuto);
        mModel = (TextView) findViewById(R.id.tvModel);
        mTbTitle = (TextView) findViewById(R.id.tb_title);
        mCarPic = (ImageView) findViewById(R.id.ivCarPic);
        mTitle = (TextView) findViewById(R.id.tvTitle);
        mSaveBtn = (TextView) findViewById(R.id.btn_add_return);
        //mSaveBtn.setVisibility(View.INVISIBLE);

        mMinusBtnMT = (Button) findViewById(R.id.btn_minus_mt);
        mPlusBtnMT = (Button) findViewById(R.id.btn_plus_mt);
        mAmountMT = (EditText) findViewById((R.id.tvAmount_mt));
        mDvor1 = (TextView) findViewById(R.id.tv_mt);
        mMinusBtnAT = (Button) findViewById(R.id.btn_minus_at);
        mPlusBtnAT = (Button) findViewById(R.id.btn_plus_at);
        mAmountAT = (EditText) findViewById((R.id.tvAmount_at));
        mDvor2 = (TextView) findViewById(R.id.tv_at);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBeforeSave();
                Intent myIntent = new Intent(ActivityCarDetailsAdd.this, MainActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
            }
        });

        mMinusBtnMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (Integer.valueOf((String) mAmountMT.getText()) > 0) {
                if (Integer.valueOf(mAmountMT.getText().toString()) > 0) {
                    //mAmountMT.setText(String.valueOf(Integer.valueOf((String) mAmountMT.getText()) - 1));
                    mAmountMT.setText(String.valueOf(Integer.valueOf(mAmountMT.getText().toString()) - 1));
                }
            }
        });

        mPlusBtnMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mAmountMT.setText(String.valueOf(Integer.valueOf((String) mAmountMT.getText()) + 1));
                mAmountMT.setText(String.valueOf(Integer.valueOf(mAmountMT.getText().toString()) + 1));
            }
        });

        mMinusBtnAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (Integer.valueOf((String) mAmountAT.getText()) > 0) {
                if (Integer.valueOf(mAmountAT.getText().toString()) > 0) {
                    //mAmountAT.setText(String.valueOf(Integer.valueOf((String) mAmountAT.getText()) - 1));
                    mAmountAT.setText(String.valueOf(Integer.valueOf(mAmountAT.getText().toString()) - 1));
                }
            }
        });

        mPlusBtnAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mAmountAT.setText(String.valueOf(Integer.valueOf((String) mAmountAT.getText()) + 1));
                mAmountAT.setText(String.valueOf(Integer.valueOf(mAmountAT.getText().toString()) + 1));
            }
        });

        if (coinDetails != null) {
            //uncomment for get name of image file from xml tag <imageName1> </imageName1>
            //mCarPic.setImageResource(getResources().getIdentifier(coinDetails.getImageName3(), "drawable", getPackageName()));
            Utils.setImageFromFile1(coinDetails, mCarPic, false);

            mTbTitle.setText("Добавить/Изменить");
            mModel.setText(coinDetails.getRating());
            mTitle.setText(coinDetails.getTitle());

            mDvor1.setText(coinDetails.getDvor1());
            mDvor2.setText(coinDetails.getDvor2());
            mAmountMT.setText(String.valueOf(coinDetails.getAmountKppMt()));
            if (coinDetails.getDvor2().equals(" ")) {
                mLlAuto.setVisibility(View.GONE);
            } else {
                mAmountAT.setText(String.valueOf(coinDetails.getAmountKppAt()));
                mLlAuto.setVisibility(View.VISIBLE);
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

    private void deleteItem() {
        coinDetails.setAmountKppMt(Integer.parseInt(mAmountMT.getText().toString()));
        coinDetails.setAmountKppAt(Integer.parseInt(mAmountAT.getText().toString()));
        Utils.deleteItem(new Utils.DeleteListener() {
            @Override
            public void OnDeleteComplete(boolean result) {

            }

            @Override
            public void OnDeleteError(String error) {
                Log.d("Delete ERR", error);
            }
        }, ActivityCarDetailsAdd.this, coinDetails);
    }

    private void saveItem() {
        coinDetails.setAmountKppMt(Integer.parseInt(mAmountMT.getText().toString()));
        coinDetails.setAmountKppAt(Integer.parseInt(mAmountAT.getText().toString()));
        Utils.saveItem(new Utils.SaveListener() {
            @Override
            public void OnSaveComplete(boolean result) {
                Toast.makeText(ActivityCarDetailsAdd.this, "Save car in garage", Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnSaveError(String error) {
                Log.d("Save ERR", error);
            }
        }, ActivityCarDetailsAdd.this, coinDetails);
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
        //Toast.makeText(this, "BackArrowPressed", Toast.LENGTH_LONG).show();
        checkBeforeSave();

        Gson gson = new Gson();
        String json = gson.toJson(coinDetails);

        Intent myIntent2 = new Intent(ActivityCarDetailsAdd.this, ActivityCarDetails.class);
        myIntent2.putExtra("CoinDetails", json);
        myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(myIntent2, 1);

        super.onBackPressed();
    }

    private void checkBeforeSave() {
        if (Integer.parseInt(mAmountMT.getText().toString()) > 0
                | Integer.parseInt(mAmountAT.getText().toString()) > 0) {
            saveItem();
        } else {
            deleteItem();
        }
        setResult(RESULT_OK);
    }

}