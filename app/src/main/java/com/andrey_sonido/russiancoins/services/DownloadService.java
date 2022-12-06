package com.andrey_sonido.russiancoins.services;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.andrey_sonido.russiancoins.MainActivity;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.Utils;
import com.andrey_sonido.russiancoins.activity.ActivityCacheDownload;
//import com.aspsine.multithreaddownload.CallBack;
//import com.aspsine.multithreaddownload.DownloadException;
//import com.aspsine.multithreaddownload.DownloadManager;
//import com.aspsine.multithreaddownload.DownloadRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import android.support.annotation.Nullable;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by Sonido on 15/7/28.
 */
/*
public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName() + " {}";

    public static final String ACTION_DOWNLOAD_BROADCAST = "com.andrey_sonido.russiancoins:action_download_broadcast";
    private static final String ACTION_NOTIFICATION_BROADCAST = "com.andrey_sonido.russiancoins:action_download_pause_broadcast";

    public static final String ACTION_DOWNLOAD = "com.andrey_sonido.russiancoins:action_download";
    public static final String ACTION_PAUSE = "com.andrey_sonido.russiancoins:action_pause";
    public static final String ACTION_CANCEL = "com.andrey_sonido.russiancoins:action_cancel";
    public static final String ACTION_PAUSE_ALL = "com.andrey_sonido.russiancoins:action_pause_all";
    public static final String ACTION_CANCEL_ALL = "com.andrey_sonido.russiancoins:action_cancel_all";
    private static final String ACTION_COMPLETE = "com.andrey_sonido.russiancoins:action_complete";
    private static final String ACTION_FAIL = "com.andrey_sonido.russiancoins:action_fail";

    private static final String EXTRA_FILE_NAME = "extra_file_name";
    private static final String EXTRA_FILE_URL = "extra_file_url";
    public static final String EXTRA_PINTENT = "extra_pintent";


    private PendingIntent mContentIntent;
    DownloadCallBack mDownloadCallBack;


    private Logger log = LoggerFactory.getLogger(DownloadService.class);
    private java.io.File mDownloadDirectory;

    private DownloadManager mDownloadManager;
    private PendingIntent mPendingIntent;
    private BroadcastReceiver mReceiver;

    enum actionButton {
        DEFAULT,
        REMOVE,
        ADD
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager = DownloadManager.getInstance();
        setNotificationActionReceiver();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent pIntent = new Intent(ACTION_NOTIFICATION_BROADCAST);
        mPendingIntent = PendingIntent.getBroadcast(this, 1, pIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mDownloadManager.pauseAll();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            PendingIntent pi = intent.getParcelableExtra(EXTRA_PINTENT);
            String fileName = intent.getStringExtra(EXTRA_FILE_NAME);
            String fileUrl = intent.getStringExtra(EXTRA_FILE_URL);
            String action = intent.getAction();
            try {
                switch (action) {
                    case ACTION_DOWNLOAD:
                        download(fileName, fileUrl, pi);
                        break;
                    case ACTION_PAUSE:
                        pause(fileName);
                        break;
                    case ACTION_CANCEL:
                        cancel(fileName);
                        break;
                    case ACTION_PAUSE_ALL:
                        pauseAll();
                        break;
                    case ACTION_CANCEL_ALL:
                        cancelAll();
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("OnStartCommand, /n {}", ex.toString());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setNotificationActionReceiver() {
        // создаем BroadcastReceiver
        mReceiver = new BroadcastReceiver() {
            // действия при получении сообщений
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action == null || !action.equals(DownloadService.ACTION_NOTIFICATION_BROADCAST)) {
                    return;
                }
                log.info("receive intent from notification");
                mDownloadManager.cancelAll();
            }
        };
        // создаем фильтр для BroadcastReceiver
        log.warn("Notification action receiver registered");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_NOTIFICATION_BROADCAST);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(mReceiver, intentFilter);
    }

    private void download(final String fileName, String fileUrl, PendingIntent pi) {
        mDownloadDirectory = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (mDownloadDirectory != null) {
            //noinspection ResultOfMethodCallIgnored
            mDownloadDirectory.mkdirs();
        }
        final DownloadRequest request = new DownloadRequest.Builder()
                .setTitle(fileName)
                .setUri(fileUrl)
                .setFolder(mDownloadDirectory)
                .build();
        log.info("url: {}", fileUrl);
        mDownloadCallBack = new DownloadCallBack(fileName, pi);
        try {
            mDownloadManager.download(request, fileName, mDownloadCallBack);
        } catch (NullPointerException ex) {
            log.warn("check MultiThreadDownload Config in your Application class");
        }
        log.info("download.setUri {} , filename {}", fileUrl, fileName);
    }

    private void pause(String fileName) {
        log.debug("onDownloadPaused() pause file {}", fileName);
        mDownloadManager.pause(fileName);
    }

    private void cancel(String fileName) {
        log.debug("onDownloadCanceled() cancel file {}", fileName);
        mDownloadManager.cancel(fileName);
        mDownloadCallBack.onDownloadCanceled();
    }

    private void pauseAll() {
        mDownloadManager.pauseAll();
    }

    private void cancelAll() {
        mDownloadManager.cancelAll();
    }

    public static void intentDownload(Context context, String fileName, String fileUrl, PendingIntent pi) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(EXTRA_PINTENT, pi);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        intent.putExtra(EXTRA_FILE_URL, fileUrl);
        context.startService(intent);
    }

    public static void intentPause(Context context, String fileName) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_PAUSE);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        context.startService(intent);
    }

    public static void intentCancel(Context context, String fileName) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_CANCEL);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        context.startService(intent);
    }

    public class DownloadCallBack implements CallBack {
        private String mFileName;
        private long mLastUpdateTime;
        private NotificationCompat.Builder mBuilder;
        private NotificationManagerCompat mNotificationManager;
        private Resources mResources;
        private PendingIntent mPi;

        public DownloadCallBack(String fileName, PendingIntent pi) {
            mFileName = fileName;
            mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
            mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mResources = getApplicationContext().getResources();
            mPi = pi;
        }

        @Override
        public void onStarted() {
            log.info("onStarted() file with name: {}", mFileName);
        }

        @Override
        public void onConnecting() {
            log.info("onConnecting() file with name: {}", mFileName);
//            updateNotification(R.mipmap.ic_launcher,
//                    mFileName,
//                    mResources.getString(R.string.download_connecting),
//                    new Progress(100, 0, true),
//                    mFileName + " " + mResources.getString(R.string.download_connecting),
//                    mContentIntent, true, false, actionButton.ADD);
        }

        @Override
        public void onConnected(long l, boolean b) {
            log.info("onConnected() file with name: {}", mFileName);
            ActivityCacheDownload.DownloadProgress.setIndeterminate(false);
        }

        @Override
        public void onProgress(long finished, long total, int progress) {
            long currentTime = System.currentTimeMillis() / 1000;
            if (mLastUpdateTime == 0) {
                mLastUpdateTime = currentTime;
            }

            //Update progress every ~1 sec
            if (currentTime - mLastUpdateTime > 1) {
                ActivityCacheDownload.DownloadProgress.setProgress(progress);
//                updateNotification(-1, null, mResources.getString(R.string.download_downloading),
//                        new Progress(100, progress, false),
//                        null, null, true, false, actionButton.DEFAULT);
//                mLastUpdateTime = currentTime;
            }
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public void onCompleted() {
            log.info("onCompleted() file with name: {}", mFileName);

//            mBuilder.setOnlyAlertOnce(true);
//            updateNotification(-1, null, mResources.getString(R.string.download_complete),
//                    new Progress(0, 0, false),
//                    mFileName + " " + mResources.getString(R.string.notice_download_complete), null, false, true, actionButton.REMOVE);

            Utils.saveToCache(DownloadService.this, mDownloadDirectory, mFileName);
            Utils.setCacheInstall(DownloadService.this, true);

            sendCallback(ACTION_COMPLETE);
        }

        @Override
        public void onDownloadPaused() {
            log.info("onDownloadPaused() file with name: {}", mFileName);
//            updateNotification(-1, null, mResources.getString(R.string.download_pause),
//                    null, mFileName + " " + mResources.getString(R.string.notice_download_pause), null, true, false, actionButton.DEFAULT);
        }

        @Override
        public void onDownloadCanceled() {
            log.info("onDownloadCanceled() file with name: {}", mFileName);
            sendCallback(ACTION_CANCEL);

//            mNotificationManager.cancel(mFileName);

//            updateNotification(-1, null, mResources.getString(R.string.download_canceled),
//                    new Progress(0, 0, false),
//                    mFileName + " " + mResources.getString(R.string.notice_download_canceled), null, false, true, actionButton.REMOVE);
        }

        @Override
        public void onFailed(DownloadException ex) {
            log.debug("onFailed() file name: {} errorMessage:\n {} \n ErrorCode {}",
                    mFileName, ex.getErrorMessage(), ex.getErrorCode());
            ex.printStackTrace();
            log.error("Download file failed!", ex);
            String errorNotify = getHumanReadableMessage(ex);

//            Toast.makeText(DownloadCallBack.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            Utils.setCacheInstall(DownloadService.this, false);
            sendCallback(ACTION_FAIL);
//            updateNotification(R.mipmap.ic_launcher, mFileName, errorNotify,
//                    new Progress(0, 0, false),
//                    errorNotify, null, false, true, actionButton.REMOVE);

        }

        private void updateNotification(int smallIcon, String contentTitle, String contentText, Progress progress, String ticker, PendingIntent contentIntent, boolean onGoing, boolean autoCancel, actionButton addAction) {

            if (smallIcon != -1) {
                mBuilder.setSmallIcon(smallIcon);
            }
            if (ticker != null) {
                mBuilder.setTicker(ticker);
            }
            if (contentIntent != null) {
                mBuilder.setContentIntent(contentIntent);
            }
            mBuilder.setOngoing(onGoing);
            mBuilder.setAutoCancel(autoCancel);

            if (contentTitle != null) {
                mBuilder.setContentTitle(contentTitle);
            }
            if (contentText != null) {
                mBuilder.setContentText(contentText);
            }

//             Locate and set the Progress into notification_download.xml Progressbar
            if (progress == null) {
                progress = new Progress(100, 0, true);
            }
            mBuilder.setProgress(progress.maxProgress, progress.currentProgress, progress.indeterminate);
            if (progress.maxProgress == progress.currentProgress) {
                mBuilder.setProgress(0, 0, false);
            }
            log.debug("Notification {} file:{}", contentText, mFileName);
            mBuilder.setShowWhen(true);
            switch (addAction) {
                case ADD:
                    mBuilder.addAction(android.R.drawable.ic_delete, "Отменить", mPendingIntent);
                    break;
                case REMOVE:
                    mBuilder.mActions.clear();
                    break;
                case DEFAULT:
                    break;
            }

            mNotificationManager.notify(0, mBuilder.build());
        }

        private void sendCallback(String action) {
            try {

                int resultCode = 0;
                switch (action) {
                    case ACTION_COMPLETE:
                        resultCode = 1;
                        break;
                    case ACTION_FAIL:
                        resultCode = 2;
                        break;
                    case ACTION_CANCEL:
                        resultCode = 3;
                        break;
                }
                mPi.send(resultCode);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }

        private class Progress {
            int maxProgress;
            int currentProgress;
            boolean indeterminate;

            Progress(int maxProgress, int currentProgress, boolean indeterminate) {
                this.maxProgress = maxProgress;
                this.currentProgress = currentProgress;
                this.indeterminate = indeterminate;
            }
        }
    }


    private String getHumanReadableMessage(DownloadException ex) {
        switch (ex.getErrorCode()) {
            case 108:
                return getResources().getString(R.string.download_ex_connection);
            default:
                return ex.getErrorMessage() + "(code: " + ex.getErrorCode() + ")";
        }
    }
}

 */