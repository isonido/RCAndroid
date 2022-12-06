package com.andrey_sonido.russiancoins.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.andrey_sonido.russiancoins.BuildConfig;
import com.andrey_sonido.russiancoins.MainActivity;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.Utils;
import com.andrey_sonido.russiancoins.helpers.NetworkHelper;
//import com.andrey_sonido.russiancoins.services.DownloadService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sonido on 09.06.2016.
 */
public class ActivityCacheDownload extends AppCompatActivity implements View.OnClickListener {
    private static final int PI_REQUEST_CODE = 1;
    private static final int STATUS_COMPLETE = 1;
    private static final int STATUS_CANCEL = 2;
    private static final int STATUS_FAIL = 3;
    private String mCacheFileSize;
    private Logger log = LoggerFactory.getLogger(ActivityCacheDownload.class);
    public PendingIntent mServicePI;
    private ViewFlipper mDownloadFlipper;
    private TextView mBodyText;
    public static ProgressBar DownloadProgress;
    private Button btnDownload;
    private Button btnCancel;
    private String mCacheUrl;

    void setCacheFileSize(int cacheSize) {
        if (cacheSize != -1) {
            cacheSize = Math.round(cacheSize / 1024 / 1024);
        }
        mCacheFileSize = " " + ((cacheSize != -1) ? cacheSize : "--") + " Мб";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_download);
        mDownloadFlipper = (ViewFlipper) findViewById(R.id.vf_download);
        TextView progressHeader = (TextView) findViewById(R.id.tv_progress_header);
        if (progressHeader != null) {
            progressHeader.setText(getString(R.string.pb_download));
        }

        mCacheUrl = getString(R.string.url_cache);
        if (BuildConfig.DEBUG) {
            //mCacheUrl = "http://192.168.1.4/pack1.zip";
            mCacheUrl = getString(R.string.url_cache);
        }
        mBodyText = (TextView) findViewById(R.id.tv_message_text);
        DownloadProgress = (ProgressBar) findViewById(R.id.download_progress);
        btnDownload = (Button) findViewById(R.id.btn_download);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        setData();
    }

    private void setData() {
        btnDownload.setOnClickListener(this);
        btnDownload.setText("Продолжить");
        btnDownload.setVisibility(View.VISIBLE);
        btnCancel.setOnClickListener(this);


        if (BuildConfig.DEBUG) {
            startApp();
            //Utils.setCacheInstall(this, false);
        }
        if (!Utils.isCacheInstall(ActivityCacheDownload.this)) {
            if (!Utils.isInternetAvailable(this)) {
                //Toast.makeText(ActivityCacheDownload.this, "Internet connection not available", Toast.LENGTH_SHORT).show();
                log.error(getString(R.string.msg_no_internet));
                //showErrorMessage(getString(R.string.msg_no_internet), getString(R.string.btn_retry), getString(R.string.btn_exit));
                startApp();
            } else {
                //getCacheFileSize();
                startApp();
                createPendingIntent();
            }
        } else {
            startApp();
        }
    }

    private void createPendingIntent() {
        // Создаем PendingIntent для Task1
        mServicePI = createPendingResult(PI_REQUEST_CODE, new Intent(), 0);
    }

    private void getCacheFileSize() {
        NetworkHelper.getFileSizeFromURL(
                new NetworkHelper.LoadListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void OnLoadComplete(final Object result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ((int) result == -1) {
                                    log.error(getString(R.string.msg_no_file));
                                    showErrorMessage(getString(R.string.msg_no_file), getString(R.string.btn_retry), getString(R.string.btn_exit));
                                } else {
                                    setCacheFileSize((int) result);
                                    mBodyText.setText(getString(R.string.download_cache_body) + mCacheFileSize);
                                    mDownloadFlipper.setDisplayedChild(1);
                                }
                            }
                        });
                    }

                    @Override
                    public void OnLoadError(Exception error) {
                        //showErrorMessage(getString(R.string.msg_no_file), getString(R.string.btn_retry), getString(R.string.btn_exit));
                        //error.printStackTrace();
                        log.error("getFileSize", error);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showErrorMessage(getString(R.string.msg_no_file), getString(R.string.btn_retry), getString(R.string.btn_exit));
                            }
                        });
                    }
                }, mCacheUrl
        );
    }

    public void startApp() {
        startActivity(new Intent(ActivityCacheDownload.this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                btnDownload.setVisibility(View.INVISIBLE);
                //downloadCache();
                break;
            case R.id.btn_cancel:
                //exitApp();
                break;
        }
    }
/*
    private void exitApp() {
        if (Utils.isServiceRunning(DownloadService.class.getName(),
                ActivityCacheDownload.this)) {
            DownloadService.intentCancel(ActivityCacheDownload.this,
                    getString(R.string.cache_file_name));
        }
        finish();
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log.info("requestCode = " + requestCode + ", resultCode = "
                + resultCode);
        if (requestCode == PI_REQUEST_CODE) {
            switch (resultCode) {
                case STATUS_COMPLETE:
                    startApp();
                    break;
                case STATUS_CANCEL:
                    break;
                case STATUS_FAIL:
                    btnDownload.setText("Повторить");
                    btnDownload.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
/*
    private void downloadCache() {
        DownloadService.intentDownload(this,
                getString(R.string.cache_file_name),
                getString(R.string.url_cache),
                mServicePI);
    }
*/
    private void showErrorMessage(String message, String btnPositive, String btnNegative) {
        Utils.ErrorMessage(this, message, btnPositive, btnNegative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int btnId) {
                        switch (btnId) {
                            case DialogInterface.BUTTON_POSITIVE:
                                setData();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //exitApp();
                                break;
                        }
                    }
                }
        );
    }
}
