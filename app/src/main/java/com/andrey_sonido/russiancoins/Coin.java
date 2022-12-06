package com.andrey_sonido.russiancoins;

import java.io.Serializable;

/**
 * Created by andreyandrosov on 15.09.15.
 */
public class Coin implements Serializable {
    private String id;
    private String rating;
    private String title;
    private String serial;
    private String mark;
    private String imageName1;
    private String imageName2;
    private String imageName3;
    private int price = 0;
    private int PriceRic = 0;
    private String created;
    private String createdOn;
    private String text1;
    private String text2;
    private String dvor;
    private String quality;
    private String still;
    private String weight;
    private String weightStill;
    private String diameter;
    private String length;
    private String thickness;
    private String pcs;
    private String pcs1;
    private String creator;
    private String sculptor;
    private String gurt;
    private String number;
    private String Dvor1 = null;
    private String Dvor2 = null;
    private String kpp;
    private int typeKpp;
    private int typeStill;
    private String country;
    private String KppAT = null;
    private String KppMT = null;
    private int AmountKppAt = 0;
    private int AmountKppMt = 0;

    public Coin(String mark) {this.mark = mark;}

    public Coin() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getStill() {
        return still;
    }

    public void setStill(String still) {
        this.still = still;

        if (still.contains("олото") || still.contains("еребро") || still.contains("алладий") || still.contains("латина")) {
            setTypeStill(MainActivity.STILL_DRAG);
        } else {
            setTypeStill(MainActivity.STILL_NEDRAG);
        }
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeightStill() {
        return weightStill;
    }

    public void setWeightStill(String weightStill) {
        this.weightStill = weightStill;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public String getPcs() {
        return pcs;
    }

    public void setPcs(String pcs) {
        this.pcs = pcs;
    }

    public String getPcs1() {
        return pcs1;
    }

    public void setPcs1(String pcs1) {
        this.pcs1 = pcs1;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSculptor() {
        return sculptor;
    }

    public void setSculptor(String sculptor) {
        this.sculptor = sculptor;
    }

    public String getGurt() {
        return gurt;
    }

    public void setGurt(String gurt) {
        this.gurt = gurt;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getImageName1() {
        return imageName1;
    }

    public void setImageName1(String imageName1) {
        this.imageName1 = imageName1;
    }

    public String getImageName2() {
        return imageName2;
    }

    public void setImageName2(String imageName2) {
        this.imageName2 = imageName2;
    }

    public String getImageName3() {
        return imageName3;
    }

    public void setImageName3(String imageName3) {
        this.imageName3 = imageName3;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDvor() {
        return dvor;
    }

    public String getDvor1() {
        if (dvor.equals("ММД и СПМД")) {
            return dvor.split(" ")[0];
        } else if (dvor.equals("ММД и СПМД(ЛМД)")) {
            return dvor.split(" ")[0];
        } else {
            return dvor;
        }
    }

    public String getDvor2() {
        if (dvor.equals("ММД и СПМД")) {
            return dvor.split(" ")[2];
        } else if (dvor.equals("ММД и СПМД(ЛМД)")) {
            return dvor.split(" ")[2];
        } else {
            return " ";
        }
    }

    public void setDvor(String dvor) {
        if (Dvor1 != null && Dvor2 != null) {
            this.dvor = Dvor1 + " и " + Dvor2;
        } else {
            this.dvor = dvor;
        }
        if (dvor.contains("ММД") && (dvor.contains("ЛМД") || dvor.contains("СПМД"))) {
            setTypeKpp(MainActivity.TRANS_ALL);
        } else if (!dvor.contains("ММД")) {
            setTypeKpp(MainActivity.TRANS_SPMD);
        } else if (!dvor.contains("ЛМД") || !dvor.contains("СПМД")) {
            setTypeKpp(MainActivity.TRANS_MMD);
        }
    }

    public void setDvor2(String dvor2) {
        this.Dvor2 = dvor2;
    }

    public void setDvor1(String dvor1) {
        this.Dvor1 = dvor1;
    }

    public String getKpp() {
        return kpp;
    }

    public String getKppAT() {
        if (kpp.equals("ММД и СПМД")) {
            return kpp.split(" ")[2];
        } else if (kpp.equals("ММД и СПМД(ЛМД)")) {
            return kpp.split(" ")[2];
        } else {
            return " ";
        }
    }

    public String getKppMT() {
        if (kpp.equals("ММД и СПМД")) {
            return kpp.split(" ")[0];
        } else if (kpp.equals("ММД и СПМД(ЛМД)")) {
            return kpp.split(" ")[0];
        } else {
            return kpp;
        }
    }

    public void setKpp(String kpp) {
        if (KppMT != null && KppAT != null) {
            this.kpp = KppMT + " и " + KppAT;
        } else {
            this.kpp = kpp;
        }
    }

    public void setKppAT(String kppAT) {
        this.KppAT = kppAT;
    }

    public void setKppMT(String kppMT) {
        this.KppMT = kppMT;
    }

    public int getAmountKppMt() {
        return AmountKppMt;
    }

    public void setAmountKppMt(int amountKppMt) {
        AmountKppMt = amountKppMt;
    }

    public int getAmountKppAt() {
        return AmountKppAt;
    }

    public void setAmountKppAt(int amountKppAt) {
        AmountKppAt = amountKppAt;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPriceRic() {
        return PriceRic;
    }

    public void setPriceRic(int PriceRic) {
        this.PriceRic = PriceRic;
    }

    public String getHeader() {
        if (Utils.groupBy.equalsIgnoreCase(MainActivity.GROUP_BY_YEAR)) {
            return getCreated();
        }
        if (Utils.groupBy.equalsIgnoreCase(MainActivity.GROUP_BY_NOMINAL)) {
            return getRating();
        }
        if (Utils.groupBy.equalsIgnoreCase(MainActivity.GROUP_BY_TIRAG)) {
            return getPcs1();
        }
        return getHeader();
    }

    public int getTypeKpp() {
        return typeKpp;
    }

    public void setTypeKpp(int typeKpp) {
        this.typeKpp = typeKpp;
    }

    public int getTypeStill() {
        return typeStill;
    }

    public void setTypeStill(int typeStill) {
        this.typeStill = typeStill;
    }
}
