package ru.app.autocat.helpers.downloader.core;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Map;

import ru.app.autocat.helpers.downloader.DownloadInfo;
import ru.app.autocat.helpers.downloader.architecture.DownloadTask;
import ru.app.autocat.helpers.downloader.db.ThreadInfo;


/**
 * Created by Aspsine on 2015/7/22.
 */
public class SingleDownloadTask extends DownloadTaskImpl {

    public SingleDownloadTask(DownloadInfo mDownloadInfo, ThreadInfo mThreadInfo, DownloadTask.OnDownloadListener mOnDownloadListener) {
        super(mDownloadInfo, mThreadInfo, mOnDownloadListener);
    }

    @Override
    protected void insertIntoDB(ThreadInfo info) {
        // don't support
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_OK;
    }

    @Override
    protected void updateDB(ThreadInfo info) {
        // needn't Override this
    }

    @Override
    protected Map<String, String> getHttpHeaders(ThreadInfo info) {
        // simply return null
        return null;
    }

    @Override
    protected RandomAccessFile getFile(File dir, String name, long offset) throws IOException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(0);
        return raf;
    }

    @Override
    protected String getTag() {
        return this.getClass().getSimpleName();
    }
}

