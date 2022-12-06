package com.andrey_sonido.russiancoins.adapters.grid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.andrey_sonido.russiancoins.Coin;
import com.andrey_sonido.russiancoins.MainActivity;
import com.andrey_sonido.russiancoins.R;
import com.andrey_sonido.russiancoins.Utils;
import com.andrey_sonido.russiancoins.adapters.list.StickyListHeadersAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

//import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class StickyListHeaderAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private ArrayList<Coin> mCoins;
    private int[] mSectionIndices;
    private String[] mSectionNames;
    private LayoutInflater mInflater;
    LinkedHashMap<String, String> mSections;

    public StickyListHeaderAdapter(Context context, ArrayList<Coin> coins) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCoins = coins;
        if (mCoins.size() != 0) {
            MarkComparator markComparator = new MarkComparator();
            MarkComparator2 markComparator2 = new MarkComparator2();
            MarkComparator3 markComparator3 = new MarkComparator3();
            if (Utils.groupBy.equalsIgnoreCase(MainActivity.GROUP_BY_YEAR)) {
                Collections.sort(coins, markComparator);
            }
            if (Utils.groupBy.equalsIgnoreCase(MainActivity.GROUP_BY_NOMINAL)) {
                Collections.sort(coins, markComparator2);
            }
            if (Utils.groupBy.equalsIgnoreCase(MainActivity.GROUP_BY_TIRAG)) {
                Collections.sort(coins, markComparator3);
            }
            mSections = findSections();
            mSectionIndices = getSectionIndices();
            mSectionNames = getSectionNames();
        }
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        String lastFirstMark = mCoins.get(0).getHeader();
        sectionIndices.add(0);
        for (int i = 1; i < mCoins.size(); i++) {
            if (!mCoins.get(i).getHeader().equalsIgnoreCase(lastFirstMark)) {
                lastFirstMark = mCoins.get(i).getHeader();
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        String[] sectionsStr = mSections.values().toArray(new String[mSections.size() - 1]);
        int[] numbers = new int[sectionsStr.length];
        for(int i = 0;i < sectionsStr.length;i++) {
            // Note that this is assuming valid input
            // If you want to check then add a try/catch
            // and another index for the numbers if to continue adding the others
            numbers[i] = Integer.parseInt(sectionsStr[i]);
        }
        return numbers;
    }


    private LinkedHashMap<String, String> findSections() {
        mSections = new LinkedHashMap<String, String>();
        int n = mCoins.size();
        int nSections = 0;
        for (int i = 0; i < n; i++) {
            String sectionName = mCoins.get(i).getHeader();

            if (!mSections.containsKey(sectionName)) {
                mSections.put(sectionName, String.valueOf(nSections));
                nSections++;
            }
        }

        //return mSections.keySet().toArray(new String[mSections.size()]);
        return mSections;
    }

    private String[] getSectionNames() {
        String[] letters = new String[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = mCoins.get(mSectionIndices[i]).getHeader();
        }
        return mSections.keySet().toArray(new String[mSections.size() - 1]);
    }

    @Override
    public int getCount() {
        return mCoins.size();
    }

    @Override
    public Coin getItem(int position) {
        return mCoins.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyRow myRow;
        if (convertView == null) {
            myRow = new MyRow();
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            myRow.ivCarPic = (ImageView) convertView.findViewById(R.id.ivCarPic);
            myRow.tvModel = (TextView) convertView.findViewById(R.id.tvModel);
            myRow.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            myRow.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            myRow.tvPriceRic = (TextView) convertView.findViewById(R.id.tvPriceRic);
            myRow.tvCreate = (TextView) convertView.findViewById(R.id.tv_create);
            myRow.tvMT = (TextView) convertView.findViewById(R.id.tv_mt_header);
            myRow.tvAT = (TextView) convertView.findViewById(R.id.tv_at_header);
            myRow.tvAmountMT = (TextView) convertView.findViewById(R.id.tv_mt);
            myRow.tvAmountAT = (TextView) convertView.findViewById(R.id.tv_at);
            convertView.setTag(myRow);
        } else {
            myRow = (MyRow) convertView.getTag();
        }
        //myRow.ivCarPic.setImageResource(mContext.getResources().getIdentifier("car_example", "drawable", mContext.getPackageName()));
        //myRow.ivCarPic.setImageResource(mContext.getResources().getIdentifier(getItem(position).getImageName3(), "drawable", mContext.getPackageName()));
        Utils.setImageFromFile1(getItem(position), myRow.ivCarPic, false);
        myRow.tvModel.setText(getItem(position).getRating());
        myRow.tvTitle.setText(getItem(position).getTitle());
        myRow.tvPrice.setText(String.valueOf("Тираж: " + getItem(position).getPcs()) + " шт");

        if (getItem(position).getPriceRic() != 0) {
            myRow.tvPriceRic.setText(String.valueOf("Купить сейчас: " + getItem(position).getPriceRic()) + MainActivity.VALYUTA);
        } else {
            myRow.tvPriceRic.setText("Купить сейчас: нет в наличии");
        }
        myRow.tvCreate.setText(getItem(position).getCreatedOn());
        myRow.tvMT.setText(getItem(position).getDvor1());
        myRow.tvAT.setText(getItem(position).getDvor2());
        myRow.tvAmountMT.setText(String.valueOf(getItem(position).getAmountKppMt()));
        if (getItem(position).getDvor2().equals(" ")) {
            myRow.tvAmountAT.setText(" ");
        } else {
            myRow.tvAmountAT.setText(String.valueOf(getItem(position).getAmountKppAt()));
        }
        return convertView;
    }

    private class MyRow {
        TextView tvModel;
        TextView tvTitle;
        TextView tvPrice;
        TextView tvPriceRic;
        TextView tvCreate;
        TextView tvMT;
        TextView tvAT;
        TextView tvAmountMT;
        TextView tvAmountAT;
        ImageView ivCarPic;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.item_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.item_header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        //CharSequence headerChar = mCountries[position].subSequence(0, 1);
        String headerString = mCoins.get(position).getHeader();
        holder.text.setText(headerString);

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public String getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        return mCoins.get(position).getHeader();
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionNames;
    }

    public void clear() {
        mSectionIndices = new int[0];
        mSectionNames = new String[0];
        notifyDataSetChanged();
    }

    public void restore() {
        mSectionIndices = getSectionIndices();
        mSectionNames = getSectionNames();
        notifyDataSetChanged();
    }

    class HeaderViewHolder {
        TextView text;
    }

    private class MarkComparator implements Comparator<Coin> {
        @Override
        public int compare(Coin coin1, Coin coin2){
            return coin2.getHeader().compareTo(coin1.getHeader());
        }
    }
    private class MarkComparator2 implements Comparator<Coin> {
        @Override
        public int compare(Coin a10, Coin a20){
            return a10.getHeader().compareTo(a20.getHeader());
        }
    }
    private class MarkComparator3 implements Comparator<Coin> {
        @Override
        public int compare(Coin a10, Coin a20){
            return a10.getHeader().compareTo(a20.getHeader());
        }
    }
}
