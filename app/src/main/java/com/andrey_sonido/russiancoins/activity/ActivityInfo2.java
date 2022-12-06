package com.andrey_sonido.russiancoins.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.andrey_sonido.russiancoins.Coin;
import com.andrey_sonido.russiancoins.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;

/**
 * Created by andreyandrosov on 14.06.18.
 */
public class ActivityInfo2 extends AppCompatActivity {

    private TextView textView39;
    private Coin coinDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_info);

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

        if (coinDetails != null) {
            textView39 = (TextView) findViewById(R.id.textView39);
            textView39.setText(coinDetails.getText2());
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

    private void getData() {
        if (getIntent() != null) {
            Gson gson = new Gson();
            String json = getIntent().getExtras().getString("CoinDetails");
            coinDetails = gson.fromJson(json, new TypeToken<Coin>() {
            }.getType());
        }
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
