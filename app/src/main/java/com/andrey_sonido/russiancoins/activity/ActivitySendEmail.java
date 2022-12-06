package com.andrey_sonido.russiancoins.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.andrey_sonido.russiancoins.MainActivity;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.helpers.GMailSender;

//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;

public class ActivitySendEmail extends AppCompatActivity {
    EditText etEmailBody;
    EditText etEmailAddr;
    TextView btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        btnSend = (TextView) findViewById(R.id.ibMailCarDetails);
        etEmailBody = (EditText) findViewById(R.id.etEmailBody);
        etEmailAddr = (EditText) findViewById(R.id.ibEMailAddr);
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(etEmailBody.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(getString(R.string.email_activity_header));
        TextView tvToolbarTitle = (TextView) findViewById(R.id.tb_title);
        tvToolbarTitle.setText(getString(R.string.email_activity_header));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //etEmailBody.requestFocus();
    }

    public void mailAdSend(View view) {
        GMailSender sender = new GMailSender(getString(R.string.email_login), getString(R.string.email_password));
        try {
            btnSend.setEnabled(false);
            String emailBody = String.valueOf("email user: " + etEmailAddr.getText() + "\n\n" + etEmailBody.getText());
            sender.sendMail(new GMailSender.SendListener() {
                                @Override
                                public void OnSendComplete(final Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //startMainActivity ();
                                            Toast.makeText(ActivitySendEmail.this, (String) result, Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    });

                                }

                                @Override
                                public void OnSendError(final String error) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ActivitySendEmail.this, error, Toast.LENGTH_LONG).show();
                                            btnSend.setEnabled(true);
                                        }
                                    });
                                }
                            }, getString(R.string.email_subject_general),
                    emailBody,
                    getString(R.string.email_from),
                    getString(R.string.email_to));
            btnSend.setTextColor(getResources().getColor(R.color.lv_background_color));
        } catch (Exception e) {
            Toast.makeText(ActivitySendEmail.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void startMainActivity() {
        Intent myIntent = new Intent(ActivitySendEmail.this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
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
