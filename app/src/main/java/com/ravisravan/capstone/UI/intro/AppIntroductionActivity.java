package com.ravisravan.capstone.UI.intro;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.ravisravan.capstone.Constants.SPConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.utils.SharedPreferenceUtils;

public class AppIntroductionActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_app_introduction);
        showStatusBar(false);
        addSlide(AppIntroductionFragment.newInstance(R.layout.intro_slide1));
        addSlide(AppIntroductionFragment.newInstance(R.layout.intro_slide2));
        addSlide(AppIntroductionFragment.newInstance(R.layout.intro_slide3));
        addSlide(AppIntroductionFragment.newInstance(R.layout.intro_slide4));
        addSlide(AppIntroductionFragment.newInstance(R.layout.intro_slide5));
        setCustomTransformer(new ZoomOutPageTransformer());
        setIndicatorColor(Color.WHITE, Color.BLACK);
        setProgressIndicator();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferenceUtils.getInstance(this).setBooleanPreference(SPConstants.KEY_SHOW_APP_INTRO,false);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        SharedPreferenceUtils.getInstance(this).setBooleanPreference(SPConstants.KEY_SHOW_APP_INTRO,false);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferenceUtils.getInstance(this).setBooleanPreference(SPConstants.KEY_SHOW_APP_INTRO,false);
        finish();
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
