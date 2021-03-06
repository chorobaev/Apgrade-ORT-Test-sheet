package com.flaterlab.apgrade.screens.introduction;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.flaterlab.apgrade.R;
import com.flaterlab.apgrade.utils.CommonUtils;
import com.github.paolorotolo.appintro.AppIntro;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.flaterlab.apgrade.utils.BaseActivity.CONFIG_LANGUAGE;
import static com.flaterlab.apgrade.utils.BaseActivity.CURRENT_LANGUAGE;

public class IntroActivity extends AppIntro {

    private static boolean isFirstCreate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpLanguage();
        super.onCreate(savedInstanceState);
        initUI();

        if (!isFirstCreate) {
            CommonUtils.showChooseLanguageDialog(this);
            isFirstCreate = true;
        }
    }

    private void setUpLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG_LANGUAGE, Context.MODE_PRIVATE);
        String lang = sharedPreferences.getString(CURRENT_LANGUAGE, "ky");

        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(lang.toLowerCase())); // API 17+ only.
        } else {
            conf.locale = new Locale(lang.toLowerCase());
        }
        res.updateConfiguration(conf, dm);
    }

    private void initUI() {
        setSkipText(getResources().getString(R.string.intro_btn_skip));
        setDoneText(getResources().getString(R.string.intro_btn_done));
        showStatusBar(false);
        addSlide(IntroFragment.getInstance(R.drawable.apgrade_intro_logo, R.string.intro_first_message));
        addSlide(IntroFragment.getInstance(R.drawable.apgrade_intro_logo, R.string.intro_second_message));
        addSlide(IntroFragment.getInstance(R.drawable.apgrade_intro_logo, R.string.intro_third_message));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        closeActivity();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        closeActivity();
    }

    @Override
    public void onBackPressed() {
        CommonUtils.closeApp(this);
    }

    private void closeActivity() {
        overridePendingTransition(0, 0);
        finish();
    }
}
