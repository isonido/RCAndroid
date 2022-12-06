package com.andrey_sonido.russiancoins.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.andrey_sonido.russiancoins.Coin;
import com.andrey_sonido.russiancoins.MainActivity;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.helpers.GMailSender;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;

public class ActivitySendEmailZakaz extends AppCompatActivity {

    private EditText etEmailTel;
    TextView etEmailBody;
    TextView btnSend;
    private Coin coinDetails;
    private TextView tvPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snd_zakaz);
        btnSend = (TextView) findViewById(R.id.ibMailZakaz);
        etEmailBody = (TextView) findViewById(R.id.etEmailBdZakaz);

        etEmailTel = (EditText) findViewById(R.id.ibEMailTel);
        PhoneNumberUtils.formatNumber(String.valueOf(etEmailTel));
        etEmailTel.setSelection(2);
        getData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvToolbarTitle = (TextView) findViewById(R.id.tb_title);
        tvToolbarTitle.setText(getString(R.string.email_activity_header));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //etEmailBody.requestFocus();
        if (coinDetails != null) {
            etEmailBody.setText(coinDetails.getRating() + "\n" +
                    coinDetails.getTitle() + "\n" + coinDetails.getId() + "\n" +
                    "Кол-во: 1" + "\n" + "Сумма заказа: " + coinDetails.getPriceRic() + MainActivity.VALYUTA);
        }
    }

    public void mailAdSend(View view) {
        if (etEmailTel.length() > 11) {
            GMailSender sender = new GMailSender(getString(R.string.email_login), getString(R.string.email_password));
            try {
                btnSend.setEnabled(false);
                String emailBody = String.valueOf("Телефон: " + etEmailTel.getText() + "\n\n" +
                        etEmailBody.getText());
                sender.sendMail(new GMailSender.SendListener() {
                                    @Override
                                    public void OnSendComplete(final Object result) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //startMainActivity ();
                                                Toast.makeText(ActivitySendEmailZakaz.this, (String) result, Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }
                                        });

                                    }

                                    @Override
                                    public void OnSendError(final String error) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ActivitySendEmailZakaz.this, error, Toast.LENGTH_LONG).show();
                                                btnSend.setEnabled(true);
                                            }
                                        });
                                    }
                                }, "Монеты Роccии Андроид Заказ",
                        emailBody,
                        getString(R.string.email_from),
                        getString(R.string.email_to));
                btnSend.setTextColor(getResources().getColor(R.color.lv_background_color));
            } catch (Exception e) {
                Toast.makeText(ActivitySendEmailZakaz.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void callAd2(View view) {
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        final int REQUEST_PHONE_CALL = 1;
        Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+7 (926) 306-74-74"));

        try {
            if (ContextCompat.checkSelfPermission(ActivitySendEmailZakaz.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivitySendEmailZakaz.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
            }
            else
            {
                startActivity(in);
            }
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void startMainActivity() {
        Intent myIntent = new Intent(ActivitySendEmailZakaz.this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
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
        try {
            super.onBackPressed();
        } catch (Exception ex) {
            ex.printStackTrace();
            finish();
        }
    }
}
