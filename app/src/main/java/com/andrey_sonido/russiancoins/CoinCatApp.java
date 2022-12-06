package com.andrey_sonido.russiancoins;

import android.app.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Sonido on 04.12.2015.
 */
public class CoinCatApp extends Application {
    private static Logger log = LoggerFactory.getLogger(CoinCatApp.class);
    @Override
    public void onCreate() {

        setDefaultUncaughtExceptionHandler();
//        initDownloader();
        super.onCreate();

        // Инициализация AppMetrica SDK
//        YandexMetrica.activate(getApplicationContext(), "f012acec-8305-4c8e-8af8-763387e2c778");
        // Отслеживание активности пользователей
//        YandexMetrica.enableActivityAutoTracking(this);
    }

    private static void setDefaultUncaughtExceptionHandler() {
        try {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    log.error("Uncaught Exception " + t.getId(), e);
                    e.printStackTrace();
                }
            });
        } catch (SecurityException e) {
            log.error("Could not set ExHandler", e);
            e.printStackTrace();
        }
    }
/*
    private void initDownloader() {
        DownloadConfiguration configuration = new DownloadConfiguration();
        configuration.setMaxThreadNum(10);
        configuration.setThreadNum(3);
        DownloadManager.getInstance().init(getApplicationContext(), configuration);
    }
*/
}
