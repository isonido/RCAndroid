package com.andrey_sonido.russiancoins.helpers;

import com.andrey_sonido.russiancoins.Coin;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andreyandrosov on 15.09.15.
 */
public class XmlParserHelper {
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    static String LOG_TAG = "RussianCoins_log";
    static String tmp;

    public static void parseXMLbyStack(final LoadListener listener, final XmlPullParser xpp) {
        mExecService.submit(new Runnable() {

            @Override
            public void run() {
                ArrayList<Coin> coins = new ArrayList<Coin>();
                try {
                    //xpp.setInput(new FileReader(XMLFILEPATH));
                    int eventType = xpp.getEventType();
                    String tagName = "";
                    String closeTag;
                    Coin coin = null;
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        closeTag = xpp.getName();
                /*
                 * Opening tag
                */
                        if (eventType == XmlPullParser.START_TAG) {
                    /*
                    * The name of the tag like: <coin> --> coin
                    */
                            tagName = xpp.getName();
                            //Log.d(LOG_TAG, "START.tagName = " + xpp.getName());
                            if (tagName.equals("coins")) {
                                // init your ArrayList
                                //Log.d(LOG_TAG, "Clear Array");
                                coins.clear();
                            } else if (tagName.equals("item")) {
                                // new coin tag opened
                                // new coin tag opened
                                coin = new Coin();
                                //Log.d(LOG_TAG, "Create Coin");
                            }
                        } else if (eventType == XmlPullParser.TEXT) {
                            //Log.d(LOG_TAG, tagName + " ?= " + xpp.getText());
                            switch (tagName) {
                                // fill CoinStruct
                                // do so for the other tags as well
                                case "id":
                                    if (coin != null) {
                                        coin.setId(xpp.getText());
                                    }
                                    break;
                                case "article":
                                    if (coin != null) {
                                        coin.setId(xpp.getText());
                                    }
                                    break;
                                case "rating":
                                    if (coin != null) {
                                        coin.setRating(xpp.getText());
                                    }
                                    break;
                                case "title":
                                    if (coin != null) {
                                        coin.setTitle(xpp.getText());
                                    }
                                    break;
                                case "serial":
                                    if (coin != null) {
                                        coin.setSerial(xpp.getText());
                                    }
                                    break;
                                case "priceCon":
                                    if (coin != null) {
                                        coin.setPrice(Integer.parseInt(xpp.getText()));
                                    }
                                    break;
                                case "price":
                                    if (coin != null) {
                                        coin.setPriceRic(Integer.parseInt(xpp.getText()));
                                    }
                                    break;
                                case "text1":
                                    if (coin != null) {
                                        coin.setText1(xpp.getText());
                                    }
                                    break;
                                case "text2":
                                    if (coin != null) {
                                        coin.setText2(xpp.getText());
                                    }
                                    break;
                                case "quality":
                                    if (coin != null) {
                                        coin.setQuality(xpp.getText());
                                    }
                                    break;
                                case "still":
                                    if (coin != null) {
                                        coin.setStill(xpp.getText());
                                    }
                                    break;
                                case "weight":
                                    if (coin != null) {
                                        coin.setWeight(xpp.getText());
                                    }
                                    break;
                                case "length":
                                    if (coin != null) {
                                        coin.setLength(xpp.getText());
                                    }
                                    break;
                                case "number":
                                    if (coin != null) {
                                        coin.setNumber(xpp.getText());
                                    }
                                    break;
                                case "weightStill":
                                    if (coin != null) {
                                        coin.setWeightStill(xpp.getText());
                                    }
                                    break;
                                case "diameter":
                                    if (coin != null) {
                                        coin.setDiameter(xpp.getText());
                                    }
                                    break;
                                case "thickness":
                                    if (coin != null) {
                                        coin.setThickness(xpp.getText());
                                    }
                                    break;
                                case "pcs":
                                    if (coin != null) {
                                        coin.setPcs(xpp.getText());
                                    }
                                    break;
                                case "pcs1":
                                    if (coin != null) {
                                        coin.setPcs1(xpp.getText());
                                    }
                                    break;
                                case "creator":
                                    if (coin != null) {
                                        coin.setCreator(xpp.getText());
                                    }
                                    break;
                                case "sculptor":
                                    if (coin != null) {
                                        coin.setSculptor(xpp.getText());
                                    }
                                    break;
                                case "gurt":
                                    if (coin != null) {
                                        coin.setGurt(xpp.getText());
                                    }
                                    break;
                                case "imageName1":
                                    if (coin != null) {
                                        coin.setImageName1(xpp.getText());
                                    }
                                    break;
                                case "imageName2":
                                    if (coin != null) {
                                        coin.setImageName2(xpp.getText());
                                    }
                                    break;
                                case "imageName3":
                                    if (coin != null) {
                                        coin.setImageName3(xpp.getText());
                                    }
                                    break;
                                case "created":
                                    if (coin != null) {
                                        coin.setCreated(xpp.getText());
                                    }
                                    break;
                                case "createdOn":
                                    if (coin != null) {
                                        coin.setCreatedOn(xpp.getText());
                                    }
                                    break;
                                case "dvor":
                                    if (coin != null) {
                                        coin.setDvor(xpp.getText());
                                    }
                                    break;
                                default:
                                    //Log.d(LOG_TAG, tagName + " d= " + xpp.getText());
                                    break;
                            }
                        /*
                        * Closing tag
                        */
                        } else if (eventType == XmlPullParser.END_TAG) {
                            //Log.d(LOG_TAG, "END.tagName = " + closeTag);
                            if (closeTag.equals("item")) {
                                coins.add(coin);
                                //Log.d(LOG_TAG, "Add Coin to Array");
                            }
                        }
                        eventType = xpp.next();
                    }
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    listener.OnParseError(e);
                }
                listener.OnParseComplete(coins);
            }
        });
    }


    public interface LoadListener {
        void OnParseComplete(Object result);

        void OnParseError(Exception error);
    }
}
