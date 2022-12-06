package com.andrey_sonido.russiancoins.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.andrey_sonido.russiancoins.R;

//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;

/**
 * Created by Sonido on 26.02.2016.
 */

public class FragmentProgressBarInfinite extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_progress_bar_infinite, container, false);
        //ProgressBarCircularIndeterminate pbar = (ProgressBarCircularIndeterminate) view.findViewById(R.id.pbCircularIndeterminate);
        return inflater.inflate(R.layout.fragment_progress_bar_infinite, container, false);
    }
}
