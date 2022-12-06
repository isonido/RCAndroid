package com.andrey_sonido.russiancoins;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.andrey_sonido.russiancoins.helpers.NetworkHelper;
import com.andrey_sonido.russiancoins.helpers.XmlParserHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;

//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sonido on 15.09.15.
 */
public class Utils {
    private static final String PREFS_NAME = "Coins";
    private static final String PREF_CACHE_INSTALL = "pref_cache_type";
    private static final String PREF_COLLECTION_COINS = "Coins";
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    private static final String ALLOWED_URI_CHARS = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz@#&=*+-_.,:!?()/~'%";
    private static Context mContext;
    private static SharedPreferences mPrefs = null;
    private static ArrayList<Coin> coinsDBOrig = null;
    private static ArrayList<Coin> coinsDBPrefs = null;
    private static ArrayList<Coin> coinsDBWithPrices = null;
    private static HashMap<String, ArrayList<Coin>> filteredData = new HashMap<>();
    public static String groupBy = MainActivity.GROUP_BY_YEAR;
    private static String mPatternFilter = MainActivity.FILTER_ALL;
    private static int mCountryFilter = MainActivity.STILL_ALL;
    private static int mTransmissionFilter = MainActivity.TRANS_ALL;
    private static String mVisibleFragment = MainActivity.FRAGMENT_CATALOG;

    private static ArrayList<Coin> mcoinsDBFiltered = null;
    private static String mCoinNominalFilter = MainActivity.FILTER_ALL;
    private static String mCoinYearFilter = MainActivity.FILTER_ALL;


    public Utils(Context context) {
        mContext = context;
        mPrefs = context.getSharedPreferences("Coins", 0);
    }

    private static ArrayList<Coin> getOriginalDataByFragment(String fragmentTag){
        ArrayList<Coin> origData;
        switch (fragmentTag) {
            case MainActivity.FRAGMENT_CATALOG:
                origData = new ArrayList<>(getCoinsDBOrig());
                break;
            case MainActivity.FRAGMENT_COLLECTION:
                origData = new ArrayList<>(getCoinsDBPrefs());
                break;
            case MainActivity.FRAGMENT_STORE:
                origData = new ArrayList<>(getCoinsDBWithPrices());
                break;
            default:
                origData = new ArrayList<>();
                break;
        }
        return origData;
    }

    public static ArrayList<Coin> getFilteredData(String listId) {
        ArrayList<Coin> filteredCoins;
        if (filteredData.size() > 0) {
            filteredCoins = filteredData.get(listId);
        } else {
            filteredCoins = getOriginalDataByFragment(listId);
        }
        return filteredCoins;
    }

    public static void setFilteredData(int stillType, int transmissionType) {
        mCountryFilter = stillType;
        mTransmissionFilter = transmissionType;
        try {
        // Сохраняем фильтрованные данные для Каталога
        filteredData.put(MainActivity.FRAGMENT_CATALOG, FilterData(new ArrayList<>(getCoinsDBOrig())));
        // Сохраняем фильтрованные данные для Коллекции
        filteredData.put(MainActivity.FRAGMENT_COLLECTION, FilterData(new ArrayList<>(getCoinsDBPrefs())));
        // Сохраняем фильтрованные данные для Магазина
        filteredData.put(MainActivity.FRAGMENT_STORE, FilterData(new ArrayList<>(getCoinsDBWithPrices())));
        } catch (Exception ex) {
        //Toast.makeText(ActivityMain.this, "setFilteredData error:\n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        ex.printStackTrace();
        }
    }

    private static ArrayList<Coin> FilterData(ArrayList<Coin> coinsOrig) {
        ArrayList<Coin> filteredCoins = new ArrayList<>(coinsOrig);
        //Фильтр по металлу
        filteredCoins = getFilteredDataByNominal(filteredCoins, mCoinNominalFilter);
        //Фильтр по годам
        filteredCoins = getFilteredDataByYear(filteredCoins, mCoinYearFilter);
        //Фильтр по номиналу
        filteredCoins = filterCoinsByCountry(filteredCoins, mCountryFilter);
        //Фильтр по двору
        filteredCoins = filterCoinsByTransmission(filteredCoins, mTransmissionFilter);
        //Фильтр по серии
        filteredCoins = getFilteredDataByListPattern(mPatternFilter, filteredCoins);
        return filteredCoins;
    }

    public static void setCoinsDBOrig(ArrayList<Coin> coins) {
        coinsDBOrig = coins;
    }

    public static ArrayList<Coin> getCoinsDBOrig() {
        if (coinsDBOrig != null) {
            return hashById(coinsDBOrig);
        } else {
            return new ArrayList<>();
        }
    }

    public static void setCoinsDBFiltered(ArrayList<Coin> coins, int typeStill, int typeTransmission) {
        //coinsDBFiltered = getFilteredDataByListPattern(mPatternFilter, filterCoinsByTransmission(filterCoinsByCountry(coins, typeStill), typeTransmission));
        mcoinsDBFiltered = getFilteredDataByListPattern(mPatternFilter,
                filterCoinsByTransmission(filterCoinsByCountry(
                        getFilteredDataByYear(getFilteredDataByNominal(coins, mCoinNominalFilter), mCoinYearFilter), typeStill), typeTransmission));
    }

    public static ArrayList<Coin> getmCoinsDBFiltered() {
        if (mcoinsDBFiltered != null) {
            if (mcoinsDBFiltered.size() > 0) {
                return mcoinsDBFiltered;
            }
        }
        return getFilteredDataByListPattern(mPatternFilter, new ArrayList<>(getOriginalDataByFragment(mVisibleFragment)));
    }

    public static ArrayList<Coin> getCoinsDBPrefs() {
        if (coinsDBPrefs == null) {
            return  new ArrayList<>();
        }
        return coinsDBPrefs;
    }

    public static void setCoinsDBPrefs(ArrayList<Coin> coinsDBPrefs) {
        Utils.coinsDBPrefs = coinsDBPrefs;
    }

    public static void setGroupBy(String groupBy) {
        Utils.groupBy = groupBy;
    }

    public static ArrayList<Coin> getCoinsDBWithPrices() {
        if (mcoinsDBFiltered == null){
            return new ArrayList<>();
        }
        return coinsDBWithPrices;
    }

    public static void setCoinsDBWithPrices(ArrayList<Coin> coinsWithPrices) {
        Utils.coinsDBWithPrices = filterCoinsWithPrices(coinsWithPrices);
    }

    public static void setmPatternFilter(String mPatternFilter) {
        Utils.mPatternFilter = mPatternFilter;
    }

    public static void setmCoinYearFilter(String mCoinYearFilter) {
        Utils.mCoinYearFilter = mCoinYearFilter;
    }

    public static void setmCoinNominalFilter(String mCoinMarkFilter) {
        Utils.mCoinNominalFilter = mCoinMarkFilter;
    }

    public static void saveItem(final SaveListener listener, Context context, final Coin coinDetails) {
        mPrefs = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        //mPrefs = context.getSharedPreferences("Coins", 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    savePref(coinDetails);
                    listener.OnSaveComplete(true);
                } catch (Exception e) {
                    listener.OnSaveError(e.getMessage());
                }
            }
        });
    }

    public static void deleteItem(final DeleteListener listener, Context context, final Coin coinDetails) {
        mPrefs = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        //mPrefs = context.getSharedPreferences("Coins", 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    deleteSelectPref(coinDetails);
                    listener.OnDeleteComplete(true);
                } catch (Exception e) {
                    listener.OnDeleteError(e.getMessage());
                }
            }
        });

    }

    public static void loadItem(final LoadListener listener, Context context, final Coin coinDetails) {
        mPrefs = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        //mPrefs = context.getSharedPreferences("Coins", 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.OnLoadComplete(loadSelectPref(coinDetails));
                } catch (Exception e) {
                    listener.OnLoadError(e.getMessage());
                }
            }
        });
    }

    public static ArrayList<Coin> hashById(ArrayList<Coin> coins) {
        if (coins != null) {
            HashMap<String, Coin> coinsId = new HashMap<>();
            for (int i = 0; i < coins.size(); i++) {
                coinsId.put(coins.get(i).getId(), coins.get(i));
            }
            coins.clear();
            coins.addAll(coinsId.values());
        }
        return coins;
    }

    public static ArrayList<Coin> compareCoins(ArrayList<Coin> coins) {
        if (coins == null || coins.size() <= 0) {
            return coins;
        }
        loadData(mContext);
        ArrayList<Coin> loadCoins = new ArrayList<>(getCoinsDBPrefs());
        HashMap<String, Coin> coinsHash = new HashMap<>();
        for (int i = 0; i < coins.size(); i++) {

                coinsHash.put(coins.get(i).getId(), coins.get(i));

                //coinsHash.get(coins.get(i).getId()).setPriceRic(0);
                coinsHash.get(coins.get(i).getId()).setAmountKppAt(0);
                coinsHash.get(coins.get(i).getId()).setAmountKppMt(0);

        }
        if (loadCoins != null & loadCoins.size() > 0) {
            for (int i = 0; i < loadCoins.size(); i++) {
                if (coinsHash.containsKey(loadCoins.get(i).getId())) {

                    //coinsHash.get(loadCoins.get(i).getId()).setPriceRic(loadCoins.get(i).getPriceRic());
                    coinsHash.get(loadCoins.get(i).getId()).setAmountKppAt(loadCoins.get(i).getAmountKppAt());
                    coinsHash.get(loadCoins.get(i).getId()).setAmountKppMt(loadCoins.get(i).getAmountKppMt());
                }
            }
            coins.clear();
            coins.addAll(coinsHash.values());
        }
        return coins;
    }

    public static ArrayList<Coin> compareCoins2(ArrayList<Coin> coins) {
        if (coins == null || coins.size() <= 0) {
            return coins;
        }
        loadData(mContext);
        ArrayList<Coin> loadCoins = new ArrayList<>(getCoinsDBPrefs());
        HashMap<String, Coin> coinsHash = new HashMap<>();
        for (int i = 0; i < coins.size(); i++) {

            coinsHash.put(coins.get(i).getId(), coins.get(i));

            coinsHash.get(coins.get(i).getId()).setPriceRic(0);

        }
        if (loadCoins != null & loadCoins.size() > 0) {
            for (int i = 0; i < loadCoins.size(); i++) {
                if (coinsHash.containsKey(loadCoins.get(i).getId())) {

                    coinsHash.get(loadCoins.get(i).getId()).setPriceRic(loadCoins.get(i).getPriceRic());

                }
            }
            coins.clear();
            coins.addAll(coinsHash.values());
        }
        return coins;
    }

    public static ArrayList<Coin> setCoinPrices(ArrayList<Coin> coinPrices) {
        ArrayList<Coin> coins = new ArrayList<>(getOriginalDataByFragment(mVisibleFragment));
        for (Coin coin : coins) {
            for (int i = 0; i < coinPrices.size(); i++) {
                if (coin.getId().equalsIgnoreCase(coinPrices.get(i).getId())) {
                    coin.setPriceRic(coinPrices.get(i).getPriceRic());
                }
            }
        }
        setCoinsDBFiltered(coins, MainActivity.STILL_ALL, MainActivity.TRANS_ALL);
        return coins;
    }

    public static ArrayList<Coin> filterCoinsWithPrices(ArrayList<Coin> coins) {
        Iterator<Coin> iterator = coins.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPriceRic() <= 0) {
                iterator.remove();
            }
        }
        return coins;
    }
//фильтрует данные по двору
    public static ArrayList<Coin> filterCoinsByTransmission(ArrayList<Coin> coins, int typeTransmission) {
        if (typeTransmission != MainActivity.TRANS_ALL) {
            Iterator<Coin> iterator = coins.iterator();
            while (iterator.hasNext()) {
                Coin coin = iterator.next();
                if (coin.getTypeKpp() != typeTransmission && coin.getTypeKpp() != MainActivity.TRANS_ALL) {
                    iterator.remove();
                }
            }
        }
        return coins;
    }
//фильтрует данные по металлу
    public static ArrayList<Coin> filterCoinsByCountry(ArrayList<Coin> coins, int typeStill) {
        if (typeStill != MainActivity.STILL_ALL) {
            Iterator<Coin> iterator = coins.iterator();
            while (iterator.hasNext()) {
                Coin coin = iterator.next();
                if (coin.getTypeStill() != typeStill && coin.getTypeStill() != MainActivity.STILL_ALL) {
                    iterator.remove();
                }
            }
        }
        return coins;
    }

    //фильтрует данные по годам
    public static ArrayList<Coin> getFilteredDataByYear(ArrayList<Coin> coins, String pattern) {
        if (!pattern.equalsIgnoreCase(MainActivity.FILTER_ALL)) {
            ArrayList<Coin> filteredData = new ArrayList<>();
            for (Coin coin : coins) {
                if (coin.getCreated().equalsIgnoreCase(pattern)) {
                    filteredData.add(coin);
                }
            }
            return filteredData;
        }
        return coins;
    }

    //фильтрует данные по номиналу
    public static ArrayList<Coin> getFilteredDataByNominal(ArrayList<Coin> coins, String pattern) {
        if (!pattern.equalsIgnoreCase(MainActivity.FILTER_ALL)) {
            ArrayList<Coin> filteredData = new ArrayList<>();
            for (Coin coin : coins) {
                if (coin.getRating().equalsIgnoreCase(pattern)) {
                    filteredData.add(coin);
                }
            }
            return filteredData;
        }
        return coins;
    }

    //совмещенный вывод серии по году или номиналу или тиражу
    public static ArrayList<Coin> getFilteredDataByListPattern(String pattern, ArrayList<Coin> coinsForFilter) {
        if (coinsForFilter != null & coinsForFilter.size() > 0) {
            if (!pattern.equalsIgnoreCase(MainActivity.FILTER_ALL)) {
                ArrayList<Coin> filteredData = new ArrayList<>();
                for (Coin coin : coinsForFilter) {
                    if (groupBy.equalsIgnoreCase(MainActivity.GROUP_BY_NOMINAL) & coin.getSerial().equalsIgnoreCase(pattern)) {
                        filteredData.add(coin);
                    }
                    if (groupBy.equalsIgnoreCase(MainActivity.GROUP_BY_YEAR) & coin.getSerial().equalsIgnoreCase(pattern)) {
                        filteredData.add(coin);
                    }
                    if (groupBy.equalsIgnoreCase(MainActivity.GROUP_BY_TIRAG) & coin.getSerial().equalsIgnoreCase(pattern)) {
                        filteredData.add(coin);
                    }
                }
                return filteredData;
            }
        }
        return coinsForFilter;
    }

//вывод списка года
    public static ArrayList<String> getListOfMarkByCountry(String filterCountry) {
        ArrayList<String> mArrayListYear = new ArrayList<>();
        ArrayList<Coin> coins = getFilteredDataByCountry(filterCountry);
        HashMap<String, String> years = new HashMap<>();
        if (coins != null) {
            for (Coin coin : coins) {
                if (!years.containsKey(coin.getCreated())) {
                    years.put(coin.getCreated(), "");
                }
            }
            mArrayListYear.addAll(years.keySet());
        }
        Collections.sort(mArrayListYear, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.compareToIgnoreCase(s1);
            }
        });
        mArrayListYear.add(0, MainActivity.FILTER_ALL);

        return mArrayListYear;
    }
//вывод списка номинала
    public static ArrayList<String> getListOfNominalByCountry(String filterCountry) {
        ArrayList<String> mArrayListNominal = new ArrayList<>();
        ArrayList<Coin> coins = getFilteredDataByCountry(filterCountry);
        HashMap<String, String> nominals = new HashMap<>();
        if (coins != null) {
            for (Coin coin : coins) {
                if (!nominals.containsKey(coin.getRating())) {
                    nominals.put(coin.getRating(), "");
                }
            }
            mArrayListNominal.addAll(nominals.keySet());
        }
        Collections.sort(mArrayListNominal, new Comparator<String>() {
            @Override
            public int compare(String a10, String a20) {
                return a10.compareToIgnoreCase(a20);
            }
        });

        mArrayListNominal.add(0, MainActivity.FILTER_ALL);

        return mArrayListNominal;
    }
//вывод списка серий
    public static ArrayList<String> getListOfSerial(String filterSerial) {
        ArrayList<String> mArrayListSerial = new ArrayList<>();
        HashMap<String, String> serials = new HashMap<>();

        for (Coin coin : getCoinsDBOrig()) {
            if (!serials.containsKey(coin.getSerial())) {
                serials.put(coin.getSerial(), "");
            }
        }
        mArrayListSerial.addAll(serials.keySet());
        mArrayListSerial.remove("null");
        Collections.sort(mArrayListSerial);
        mArrayListSerial.add(0, MainActivity.FILTER_ALL);

        return mArrayListSerial;
    }
//фильтрует список под гол и номинал
    public static ArrayList<Coin> getFilteredDataByCountry(String pattern) {
        ArrayList<Coin> filteredData = new ArrayList<>();
        for (Coin coin : getOriginalDataByFragment(mVisibleFragment)) {
            if (coin.getSerial().equalsIgnoreCase(pattern) || pattern.equals(MainActivity.FILTER_ALL)) {
                filteredData.add(coin);
            }
        }
        return filteredData;
    }

    public static ArrayList<Coin> findItemsByMask(String findMask) {
        ArrayList<Coin> allCoins = new ArrayList<>(getOriginalDataByFragment(mVisibleFragment));
        ArrayList<Coin> coins = new ArrayList<>();
        for (Coin coin : allCoins) {
            if (coin.getTitle().toLowerCase().contains(findMask.toLowerCase())) {
                coins.add(coin);
            }
            if (coin.getRating().toLowerCase().contains(findMask.toLowerCase())) {
                coins.add(coin);
            }
            if (coin.getSerial().toLowerCase().contains(findMask.toLowerCase())) {
                coins.add(coin);
            }
            if (coin.getId().toLowerCase().contains(findMask.toLowerCase())) {
                coins.add(coin);
            }
        }
        return new ArrayList<>(new HashSet(coins));
    }

    private static Coin loadSelectPref(Coin coinDetails) {
        //loadPref();
        ArrayList<Coin> coins = new ArrayList<>(getCoinsDBPrefs());
        if (coins != null && coins.size() > 0) {
            for (int i = 0; i < coins.size(); i++) {
                if (coins.get(i).getId().equalsIgnoreCase(coinDetails.getId())) {
                    return coins.get(i);
                }
            }
        }
        return null;
    }

    private static void deleteSelectPref(Coin coinDetails) {
        loadData(mContext);
        ArrayList<Coin> coins = new ArrayList<>(getCoinsDBPrefs());
        if (coins.size() > 1) {
            for (int i = 0; i <= coins.size() - 1; i++) {
                if (coins.get(i).getId().equalsIgnoreCase(coinDetails.getId())) {
                    coins.remove(i);
                }
            }
        } else if (coins.size() <= 1) {
            coins = null;
        }
        savePrefFull(coins);
    }

    private static void savePref(Coin coinDetails) {
        loadData(mContext);
        ArrayList<Coin> coinsSave = new ArrayList<>();
        ArrayList<Coin> coins = getCoinsDBPrefs();
        if (coins != null) {
            HashMap<String, Coin> coinsId = new HashMap<>();
            for (int i = 0; i < coins.size(); i++) {
                if (!coinsId.containsKey(coins.get(i).getId())) {
                    coinsId.put(coins.get(i).getId(), coins.get(i));
                }
            }
            coinsId.put(coinDetails.getId(), coinDetails);
            coinsSave.clear();
            coinsSave.addAll(coinsId.values());
        } else {
            coinsSave.add(coinDetails);
        }
        savePrefFull(coinsSave);
    }

    public static void setCacheInstall(Context context, boolean result) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_CACHE_INSTALL, result);
        editor.apply();
    }

    public static Boolean isCacheInstall(Context context) {
        File cacheFolder = new File(Utils.getAppCacheDir(context));
        if (Utils.isFolderExist(cacheFolder) && Utils.getFolderSize(cacheFolder) > 0) {
            Utils.setCacheInstall(context, true);
        } else {
            Utils.setCacheInstall(context, false);
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        return prefs.getBoolean(PREF_CACHE_INSTALL, false);
    }

    private static void savePrefFull(ArrayList<Coin> coins) {
        if (mPrefs == null) {
            return;
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.clear();
        if (coins != null) {
            Gson gson = new Gson();
            String json = gson.toJson(coins);
            //prefsEditor.putString("Coins", json);
            prefsEditor.putString(PREF_COLLECTION_COINS, json);
        }
        prefsEditor.apply();
        loadData(mContext);
    }

    private static void loadPref(final LoadListener listener) {
        if (mPrefs == null) {
            return;
        }
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Coin> coinsLoad;
                    Gson gson = new Gson();
                    String json = mPrefs.getString(PREF_COLLECTION_COINS, null);

                    coinsLoad = gson.fromJson(json, new TypeToken<ArrayList<Coin>>() {
                    }.getType());
                    listener.OnLoadComplete(coinsLoad);
                } catch (Exception e) {
                    listener.OnLoadError(e.getMessage());
                }
            }
        });
    }

    public static void loadData(final Context context) {
        mContext = context;
        //mPrefs = context.getSharedPreferences("Coins", 0);
        mPrefs = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        loadPref(new LoadListener() {
            @Override
            public void OnLoadComplete(Object result) {
                setCoinsDBPrefs((ArrayList<Coin>) result);
            }

            @Override
            public void OnLoadError(String error) {
                Log.d("OnLoadPrefsError: ", error);
            }
        });
    }

    public static void loadPrices() {
        // Get price of coins from server
        NetworkHelper.getPrices(new NetworkHelper.LoadListener() {
            @Override
            public void OnLoadComplete(Object result) {
                XmlParserHelper.parseXMLbyStack(new XmlParserHelper.LoadListener() {
                    @Override
                    public void OnParseComplete(Object result) {
                        ArrayList<Coin> coinsWithPrices = new ArrayList<>((ArrayList<Coin>) result);
                        setCoinsDBWithPrices(setCoinPrices(coinsWithPrices));
                    }

                    @Override
                    public void OnParseError(final Exception error) {
                        Log.d("OnParsePricesError: ", error.getMessage());
                    }
                }, (XmlPullParser) result);
            }

            @Override
            public void OnLoadError(final Exception error) {
                Log.d("OnLoadPricesError: ", error.getMessage());
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void EmptyMessage(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Нет монет в коллекции")
                .setMessage("Вы можете добавить монеты через каталог")
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void EmptyMessage2(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Информация:")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void ErrorMessage(Context context, String body, String btnPositiveCaption, String btnNegativeCaption, DialogInterface.OnClickListener dialogAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Упс.. Что-то пошло не так")
                .setMessage(body)
                .setCancelable(false)
                .setPositiveButton(btnPositiveCaption, dialogAction)
                .setNegativeButton(btnNegativeCaption, dialogAction);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String fixCyrillicHostInUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            return stringUrl;
        }
        String punycodeHost = url.getHost();
        String unicodeHost = IDN.toASCII(punycodeHost);
        return stringUrl.replaceFirst(punycodeHost, unicodeHost);
    }

    public static String fixCyrillicAddressInUrl(String stringUrl) {
        return Uri.encode(stringUrl, ALLOWED_URI_CHARS);
    }

    public static ArrayList<Coin> normalizeCoinsMark(ArrayList<Coin> coins) {
        if (coins != null) {
            for (Coin coin : coins) {
                coin.setRating(Normalizer.normalize(coin.getRating(), Normalizer.Form.NFD));
            }
            return coins;
        } else {
            return null;
        }
    }

    public static void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static String getAppCacheDir(Context context) {
        return context.getExternalCacheDir() + "/pictures/";
    }

    public static void saveToCache(Context context, File srcDirectory, String fileName) {
        final File zipArchive = new File(srcDirectory + "/" + fileName);
        final File dir = new File(getAppCacheDir(context));
        Executors.newCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                if (!dir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    dir.mkdir();
                }
                try {
                    unzip(zipArchive, dir);
                    //noinspection ResultOfMethodCallIgnored
                    zipArchive.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean isFolderExist(File folderPath) {
        boolean exist = folderPath.exists();
        boolean dir = folderPath.isDirectory();
        return exist && dir;
    }

    public static long getFolderSize(File folderPath) {
        long size = 0;
        for (File file : folderPath.listFiles()) {
            if (file.isFile()) {
                //System.out.println(file.getName() + " " + file.length());
                size += file.length();
            } else
                size += getFolderSize(file);
        }
        //System.out.println(folderPath.getAbsolutePath() + " size = " + size);
        return size;
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        //noinspection TryFinallyCanBeTryWithResources
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        return netInfo != null;
    }

    public static void setmVisibleFragment(String mVisibleFragment) {
        Utils.mVisibleFragment = mVisibleFragment;
    }

    public static void setImageFromFile1(Coin coin, ImageView imageView, boolean isDetails) {
        if (imageView == null) {
            return;
        }
        if (coin == null) {
            imageView.setImageResource(mContext.getResources().getIdentifier("car_example",
                    "drawable", mContext.getPackageName()));
            return;
        }
        File file = new File(Utils.getAppCacheDir(mContext) + (coin.getImageName3() + ".png"));
        String imageName3 = getFileNameWithoutExtension((coin.getImageName3() + ".png"));
        Bitmap coinImage = null;
        if (file.exists() && !file.isDirectory() && isFileImage(file.getAbsolutePath())) {
            try {
                coinImage = decodeImageFromFile(file);
            } catch (Exception ex) {
            //    ex.printStackTrace();
            }
        } else if (isDrawableExist(mContext, imageName3, "drawable")) {
            coinImage = decodeImageFromRes(mContext.getResources().getIdentifier(imageName3,
                    "drawable", mContext.getPackageName()));
        } else {
            coinImage = decodeImageFromRes(mContext.getResources().getIdentifier("unavailable",
                    "drawable", mContext.getPackageName()));
        }
        imageView.setImageBitmap(coinImage);
    }

    public static void setImageFromFile2(Coin coin, ImageView imageView, boolean isDetails) {
        if (imageView == null) {
            return;
        }
        if (coin == null) {
            imageView.setImageResource(mContext.getResources().getIdentifier("car_example",
                    "drawable", mContext.getPackageName()));
            return;
        }
        File file = new File(Utils.getAppCacheDir(mContext) + (coin.getImageName1() + ".png"));
        String imageName3 = getFileNameWithoutExtension((coin.getImageName1() + ".png"));
        Bitmap coinImage = null;
        if (file.exists() && !file.isDirectory() && isFileImage(file.getAbsolutePath())) {
            try {
                coinImage = decodeImageFromFile(file);
            } catch (Exception ex) {
                //    ex.printStackTrace();
            }
        } else if (isDrawableExist(mContext, imageName3, "drawable")) {
            coinImage = decodeImageFromRes(mContext.getResources().getIdentifier(imageName3,
                    "drawable", mContext.getPackageName()));
        } else {
            coinImage = decodeImageFromRes(mContext.getResources().getIdentifier("unavailable",
                    "drawable", mContext.getPackageName()));
        }
        imageView.setImageBitmap(coinImage);
    }

    public static void setImageFromFile3(Coin coin, ImageView imageView, boolean isDetails) {
        if (imageView == null) {
            return;
        }
        if (coin == null) {
            imageView.setImageResource(mContext.getResources().getIdentifier("car_example",
                    "drawable", mContext.getPackageName()));
            return;
        }
        File file = new File(Utils.getAppCacheDir(mContext) + (coin.getImageName2() + ".png"));
        String imageName3 = getFileNameWithoutExtension((coin.getImageName2() + ".png"));
        Bitmap coinImage = null;
        if (file.exists() && !file.isDirectory() && isFileImage(file.getAbsolutePath())) {
            try {
                coinImage = decodeImageFromFile(file);
            } catch (Exception ex) {
                //    ex.printStackTrace();
            }
        } else if (isDrawableExist(mContext, imageName3, "drawable")) {
            coinImage = decodeImageFromRes(mContext.getResources().getIdentifier(imageName3,
                    "drawable", mContext.getPackageName()));
        } else {
            coinImage = decodeImageFromRes(mContext.getResources().getIdentifier("unavailable",
                    "drawable", mContext.getPackageName()));
        }
        imageView.setImageBitmap(coinImage);
    }

    public static Bitmap decodeImageFromFile(File picture) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 2;
        return BitmapFactory.decodeFile(picture.getAbsolutePath(), options);
    }

    public static Bitmap decodeImageFromRes(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferQualityOverSpeed = true;
        return BitmapFactory.decodeResource(mContext.getResources(), resId, options);
    }


    public static String getFileNameWithoutExtension(String fullFileName) {
        return fullFileName.substring(0, fullFileName.indexOf("."));
    }

    public static boolean isFileImage(String filePath) {
        File f = new File(filePath);
        String mimeType = new MimetypesFileTypeMap().getContentType(f);
        String type = mimeType.split("/")[0];
        //System.out.println("It's an image");
        //System.out.println("It's NOT an image");
        return type.equals("image");
    }

    public static void restartActivity(Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        Intent intent = activity.getIntent();
        activity.finish();
        activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static boolean isServiceRunning(String serviceClassName, Context context) {
        final ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services =
                activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDrawableExist(Context context, String resourceName, String resFolderName) {
        resFolderName = "drawable";
        int checkExistence = context.getResources().getIdentifier(resourceName, resFolderName,
                context.getPackageName());

        // checkExistence == 0  // the resource does NOT exist!!
        return checkExistence != 0;
    }

    public interface LoadListener {
        void OnLoadComplete(Object result);

        void OnLoadError(String error);
    }
    public interface SaveListener {
        void OnSaveComplete(boolean result);

        void OnSaveError(String error);
    }
    public interface DeleteListener {
        void OnDeleteComplete(boolean result);

        void OnDeleteError(String error);
    }
}
