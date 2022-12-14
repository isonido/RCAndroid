package ru.app.autocat.helpers.downloader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.app.autocat.helpers.downloader.architecture.DownloadResponse;
import ru.app.autocat.helpers.downloader.architecture.DownloadStatusDelivery;
import ru.app.autocat.helpers.downloader.architecture.Downloader;
import ru.app.autocat.helpers.downloader.core.DownloadResponseImpl;
import ru.app.autocat.helpers.downloader.core.DownloadStatusDeliveryImpl;
import ru.app.autocat.helpers.downloader.core.DownloaderImpl;
import ru.app.autocat.helpers.downloader.db.DataBaseManager;
import ru.app.autocat.helpers.downloader.db.ThreadInfo;

//import android.support.annotation.NonNull;


/**
 * Created by Aspsine on 2015/7/14.
 */
public class DownloadManager implements Downloader.OnDownloaderDestroyedListener {

    public static final String TAG = DownloadManager.class.getSimpleName();

    /**
     * singleton of DownloadManager
     */
    private static DownloadManager sDownloadManager;

    private DataBaseManager mDBManager;

    private Map<String, Downloader> mDownloaderMap;

    private ru.app.autocat.helpers.downloader.DownloadConfiguration mConfig;

    private ExecutorService mExecutorService;

    private DownloadStatusDelivery mDelivery;

    public static DownloadManager getInstance() {
        if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                sDownloadManager = new DownloadManager();
            }
        }
        return sDownloadManager;
    }

    /**
     * private construction
     */
    private DownloadManager() {
        mDownloaderMap = new LinkedHashMap<String, Downloader>();
    }

    public void init(Context context) {
        init(context, new ru.app.autocat.helpers.downloader.DownloadConfiguration());
    }

    public void init(Context context, @NonNull ru.app.autocat.helpers.downloader.DownloadConfiguration config) {
        if (config.getThreadNum() > config.getMaxThreadNum()) {
            throw new IllegalArgumentException("thread num must < max thread num");
        }
        mConfig = config;
        mDBManager = DataBaseManager.getInstance(context);
        mExecutorService = Executors.newFixedThreadPool(mConfig.getMaxThreadNum());
        mDelivery = new DownloadStatusDeliveryImpl(new Handler(Looper.getMainLooper()));
    }

    @Override
    public void onDestroyed(String key, Downloader downloader) {
        if (mDownloaderMap.containsKey(key)) {
            mDownloaderMap.remove(key);
        }
    }

    public void download(ru.app.autocat.helpers.downloader.DownloadRequest request, String tag, ru.app.autocat.helpers.downloader.CallBack callBack) {
        final String key = createKey(tag);
        if (check(key)) {
            DownloadResponse response = new DownloadResponseImpl(mDelivery, callBack);
            Downloader downloader = new DownloaderImpl(request, response, mExecutorService, mDBManager, key, mConfig, this);
            mDownloaderMap.put(key, downloader);
            downloader.start();
        }
    }

    public void pause(String tag) {
        String key = createKey(tag);
        if (mDownloaderMap.containsKey(key)) {
            Downloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.pause();
                }
            }
            mDownloaderMap.remove(key);
        }
    }

    public void cancel(String tag) {
        String key = createKey(tag);
        if (mDownloaderMap.containsKey(key)) {
            Downloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                downloader.cancel();
            }
            mDownloaderMap.remove(key);
        }
    }

    public void pauseAll() {
        for (Downloader downloader : mDownloaderMap.values()) {
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.pause();
                }
            }
        }
    }

    public void cancelAll() {
        for (Downloader downloader : mDownloaderMap.values()) {
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.cancel();
                }
            }
        }
    }

//    public void delete(String tag) {
//        String key = createKey(tag);
//        if (mDownloaderMap.containsKey(key)) {
//            Downloader downloader = mDownloaderMap.get(key);
//            downloader.cancel();
//        } else {
//            List<DownloadInfo> infoList = mDBManager.getDownloadInfos(tag);
//            for (DownloadInfo info : infoList) {
//                FileUtils.delete(info.getD);
//            }
//        }
//    }
//
//    public void deleteAll() {
//
//    }

    public ru.app.autocat.helpers.downloader.DownloadInfo getDownloadProgress(String tag) {
        String key = createKey(tag);
        List<ThreadInfo> threadInfos = mDBManager.getThreadInfos(key);
        ru.app.autocat.helpers.downloader.DownloadInfo downloadInfo = null;
        if (!threadInfos.isEmpty()) {
            int finished = 0;
            int progress = 0;
            int total = 0;
            for (ThreadInfo info : threadInfos) {
                finished += info.getFinished();
                total += (info.getEnd() - info.getStart());
            }
            progress = (int) ((long) finished * 100 / total);
            downloadInfo = new ru.app.autocat.helpers.downloader.DownloadInfo();
            downloadInfo.setFinished(finished);
            downloadInfo.setLength(total);
            downloadInfo.setProgress(progress);
        }
        return downloadInfo;
    }

    private boolean check(String key) {
        if (mDownloaderMap.containsKey(key)) {
            Downloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    ru.app.autocat.helpers.downloader.utility.L.w("Task has been started!");
                    return false;
                } else {
                    throw new IllegalStateException("Downloader instance with same tag has not been destroyed!");
                }
            }
        }
        return true;
    }

    private static String createKey(String tag) {
        if (tag == null) {
            throw new NullPointerException("Tag can't be null!");
        }
        return String.valueOf(tag.hashCode());
    }


}
