package com.andrey_sonido.russiancoins.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.andrey_sonido.russiancoins.Coin;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;

/**
 * Created by andreyandrosov on 20.09.15.
 */
public class FragmentStatic extends Fragment {
    private ArrayList<Coin> coinsAll;
    private ArrayList<Coin> coins;
    private static ArrayList<Coin> coinsDBPrefs = null;
    private Coin coinDetails;
    private TextView sColAll;
    private TextView sColUser;
    private TextView sColPovUser;
    private TextView sNabAll;
    private TextView sNabUser;
    private TextView sGPlay;
    private TextView sRazrab;
    private TextView sVk;
    LinkedHashMap<String, String> mSections;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_static, container, false);

        sColAll = (TextView) view.findViewById(R.id.colAll);
        sColUser = (TextView) view.findViewById(R.id.colUser);
        sColPovUser = (TextView) view.findViewById(R.id.colPovUser);
        sNabAll = (TextView) view.findViewById(R.id.nabAll);
        sNabUser = (TextView) view.findViewById(R.id.nabUser);

        sGPlay = (TextView) view.findViewById(R.id.GPlay);
        sRazrab = (TextView) view.findViewById(R.id.razrab);
        sVk = (TextView) view.findViewById(R.id.vk);

        View.OnClickListener oclBtn = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.GPlay:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.andrey_sonido.russiancoins")));
                        break;
                    case R.id.razrab:
                        sendEmail();
                        break;
                    case R.id.vk:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://vk.com/club89320889")));
                        break;
                }
            }
        };
        sGPlay.setOnClickListener(oclBtn);
        sRazrab.setOnClickListener(oclBtn);
        sVk.setOnClickListener(oclBtn);

        coinsAll = new ArrayList<>(Utils.getCoinsDBOrig());
        coinsDBPrefs = new ArrayList<>(Utils.getCoinsDBPrefs());

        sColAll.setText(String.valueOf(coinsAll.size()));
        sColUser.setText(String.valueOf(coinsDBPrefs.size()));
/*
        mSections = new LinkedHashMap<String, String>();
        int n = coinsDBPrefs.size();
        int nSections = 0;
        for (int i = 0; i < n; i++) {
            if (coinDetails.getAmountKppMt()>1) {
                String sectionName = String.valueOf(coinsAll.get(i).getAmountKppMt());
                coins.add(sectionName);
                //nSections++;
            }
        } */
        sColPovUser.setText("0");
        sNabAll.setText("44");
        sNabUser.setText("0");

        return view;
    }

    protected void sendEmail() {
        Log.i("Send email", "");

        String[] TO = {"andrey.sonido@gmail.com"};
        String[] CC = {"bkv@ricgold.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Монеты России");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Ваше сообщение:");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

            //Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            //Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
    /*
    private void getData() {
        if (getIntent() != null) {
            Gson gson = new Gson();
            String json = getIntent().getExtras().getString("CoinDetails");
            coinDetails = gson.fromJson(json, new TypeToken<Coin>() {
            }.getType());
        }
    }
    */

}
