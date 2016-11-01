package com.ravisravan.capstone.UI.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravisravan.capstone.Constants.SPConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.utils.SharedPreferenceUtils;

/**
 * Created by ravisravankumar on 15/10/16.
 */
public class AppIntroductionFragment extends Fragment {
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    public static AppIntroductionFragment newInstance(int layoutResId) {
        AppIntroductionFragment appIntroductionFragment = new AppIntroductionFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        appIntroductionFragment.setArguments(args);

        return appIntroductionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutResId, container, false);
    }
}
