package com.andrey_sonido.russiancoins.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import com.andrey_sonido.russiancoins.Utils;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Sonido on 30.09.2015.
 */

public class NetworkHelper {
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    private static Gson mGson = new Gson();
    private static Context mContext;
    private static XmlPullParser carPrices = Xml.newPullParser();


    public static void getPrices(final LoadListener listener) {
        mExecService.submit(new Runnable() {

            @Override
            public void run() {
                String urlXML = "https://www.ricgold.com/_xml/";
                //String urlXML = "http://andrey-sonido.com/test1.xml";
                try {
                    BufferedReader in = openConnection(Utils.fixCyrillicHostInUrl(urlXML));
                    String jRequest = "", line;

                    while ((line = in.readLine()) != null) {
                        jRequest += line;
                    }
                    in.close();

                    //carPrices.setInput(new StringReader(jRequest.replace(" ", "").replace("cardatabase", "car")));
                    //carPrices.setInput(new StringReader(jRequest.replace("\t", "").replace("\t\t\t\t", "").replace("coins", "item")));
                    carPrices.setInput(new StringReader(jRequest.replace("\t", "").replace(" ", "").replace("coins", "item")));
                    listener.OnLoadComplete(carPrices);
                } catch (Exception ex) {
                    listener.OnLoadError(ex);
                    ex.printStackTrace();
                    //Log.d("Error Connection ", ex.getMessage());
                }
            }
        });
    }

    public static void getImageFromUrl(final LoadListener listener, final String get_avatar_api) {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    String temp;
                    if (get_avatar_api.substring(0, 6).equalsIgnoreCase("https:")) {
                        temp = get_avatar_api;
                    } else {
                        temp = "https://joby.su" + get_avatar_api;
                    }
                    String urlEncoded = Uri.encode(temp, "UTF-8");
                    url = new URL(urlEncoded);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    int responseCode = connection.getResponseCode();
                    Log.d("Response Code ", String.valueOf(responseCode));
                    if (responseCode != HttpsURLConnection.HTTP_OK) {
                        throw new Exception(responseCode + " Bad Response Code");
                    }
                    Bitmap bmp = BitmapFactory.decodeStream(connection.getInputStream());
                    listener.OnLoadComplete(bmp);
                } catch (Exception ex) {
                    listener.OnLoadError(ex);
                    Log.e("Error Connection, url: " + url, ex.getMessage());
                }
            }
        });
    }

    public static void getFileSizeFromURL(final LoadListener listener, final String urlString) {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                int fileSize = 0;
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.connect();
                    fileSize = connection.getContentLength();
                    connection.disconnect();
                    listener.OnLoadComplete(fileSize);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.OnLoadError(e);
                }
            }
        });
    }

    public static void isWebFileExists(final LoadListener listener, final String URLName) {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection.setFollowRedirects(false);
                    // note : you may also need
                    //        HttpURLConnection.setInstanceFollowRedirects(false)
                    HttpURLConnection con =
                            (HttpURLConnection) new URL(URLName).openConnection();
                    con.setRequestMethod("HEAD");
                    listener.OnLoadComplete(con.getResponseCode() == HttpURLConnection.HTTP_OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    //log.info("no such file {} on the server", URLName);
                    listener.OnLoadError(e);
                }
            }
        });
    }

    private static BufferedReader openConnection(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(5000);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        int responseCode = connection.getResponseCode();
        //Log.d("Response Code {}", String.valueOf(responseCode));
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception(responseCode + " Bad Response Code");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return in;
    }

    public interface LoadListener {
        void OnLoadComplete(Object result);

        void OnLoadError(Exception error);
    }
}
