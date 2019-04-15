package com.example.apgrate.screens.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.apgrate.R;
import com.example.apgrate.helper.Common;
import com.example.apgrate.model.MiniTest;
import com.example.apgrate.model.Test;
import com.example.apgrate.utils.CommonUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TestActivity extends AppCompatActivity {

    private TestViewModel mViewModel;
    private FragmentManager mFragmentManager;
    private TestFragment mTestFragment;
    private TextView tvCategory;
    private TextView tvTimer;
    private FloatingActionButton fabNext;
    private Test actualTest;

    public static final String ACTUAL_TEST = "ACTUAL_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        setSupportActionBar(findViewById(R.id.tb_test));

        mViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        mViewModel.init();

        tvCategory = findViewById(R.id.tv_test_category);
        tvTimer = findViewById(R.id.tv_timer);
        fabNext = findViewById(R.id.fab_next);

        mFragmentManager = getSupportFragmentManager();
        mTestFragment = TestFragment.getInstance();
        mFragmentManager.beginTransaction().add(R.id.fl_test_container, mTestFragment).commit();

        actualTest = (Test) getIntent().getSerializableExtra(ACTUAL_TEST);
        Log.d("MylogTest", actualTest.toString());
        mViewModel.setActualTest(actualTest);

        setObservers();
        setOnClickListeners();
    }

    private void setObservers() {
        mViewModel.getCurrentTestCategory().observe(this, this::setTitleCategory);

        mViewModel.getIsTestFinished().observe(this, isTestFinished -> {
            if (isTestFinished) {
                mFragmentManager.beginTransaction().replace(R.id.fl_test_container, new ResultFragment()).commit();
                fabNext.setImageResource(R.drawable.hand_okay);
                fabNext.setOnClickListener(v -> finish());
            }
        });

        mViewModel.getTimer().observe(this, time -> {
            tvTimer.setText(Common.getTimeFormat(time));
        });

        mViewModel.getIsBreakTime().observe(this, isBreakTime -> {
            if (isBreakTime) {
                tvCategory.setText(getResources().getString(R.string.test_category_break_time));
                mFragmentManager.beginTransaction().replace(R.id.fl_test_container, BreakTimeFragment.getInstance()).commit();
            } else {
                mTestFragment = TestFragment.getInstance();
                mFragmentManager.beginTransaction().replace(R.id.fl_test_container, mTestFragment).commit();
            }
            mViewModel.startNextSection();
        });
    }

    private void setTitleCategory(MiniTest.Category category) {
        switch (category) {
            case MATH_1:
                tvCategory.setText(getResources().getString(R.string.test_category_math1));
                break;
            case MATH_2:
                tvCategory.setText(getResources().getString(R.string.test_category_math2));
                break;
            case LANGUAGE_1:
                tvCategory.setText(getResources().getString(R.string.test_category_language1));
                break;
            case LANGUAGE_2:
                tvCategory.setText(getResources().getString(R.string.test_category_language2));
                break;
            case LANGUAGE_3:
                tvCategory.setText(getResources().getString(R.string.test_category_language3));
                break;
        }
    }

    private void setOnClickListeners() {
        fabNext.setOnClickListener(view -> {
            askUserToGoOn();
        });
    }

    private void askUserToGoOn() {
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    mViewModel.forceToStartNextSection();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    // TODO: stay in this activity
                    break;
            }
        };
        String title = getResources().getString(R.string.dialog_title_warning);
        String positiveBtn = getResources().getString(R.string.dialog_yes_btn);
        String negativeBtn = getResources().getString(R.string.dialog_no_btn);
        String msg = mViewModel.getIsBreakTime().getValue() ?
                getResources().getString(R.string.dialog_start_next_test_msg) :
                getResources().getString(R.string.dialog_finish_current_mini_test_msg);

        CommonUtils.showYesNoDialog(this, title, msg, positiveBtn, negativeBtn, listener);
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    super.onBackPressed();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    // TODO: stay in this activity
                    break;
            }
        };
        String title = getResources().getString(R.string.dialog_title_warning);
        String msg = getResources().getString(R.string.dialog_leave_test_msg);
        String positiveBtn = getResources().getString(R.string.dialog_yes_btn);
        String negativeBtn = getResources().getString(R.string.dialog_no_btn);

        CommonUtils.showYesNoDialog(this, title, msg, positiveBtn, negativeBtn, listener);
    }
}
