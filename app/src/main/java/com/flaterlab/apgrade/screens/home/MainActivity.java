package com.flaterlab.apgrade.screens.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.flaterlab.apgrade.model.Test;
import com.flaterlab.apgrade.model.User;
import com.flaterlab.apgrade.screens.ApgradeApp;
import com.flaterlab.apgrade.screens.introduction.IntroActivity;
import com.flaterlab.apgrade.screens.login.LoginActivity;

import com.flaterlab.apgrade.utils.BaseActivity;
import com.flaterlab.apgrade.utils.CommonUtils;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flaterlab.apgrade.BuildConfig;
import com.flaterlab.apgrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainViewModel mViewModel;
    private final int LOGIN_REQUEST_CODE = 111;
    private User currentUser;
    private TextView tvUser;
    private TextView tvUserAttempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_tests);
        navigationView.getMenu().performIdentifierAction(R.id.nav_tests, 0);
        View view = navigationView.getHeaderView(0);
        tvUser = view.findViewById(R.id.tv_user_full_name);
        tvUserAttempts = view.findViewById(R.id.tv_left_attempts);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.init();

        checkAuthentication();
        checkFirstRun();
    }

    private void fetchAndSetCurrentUser() {
        mViewModel.fetchCurrentUser();
        mViewModel.getCurrentUser().observe(this, user -> {
            currentUser = user;
            ((ApgradeApp) getApplication()).setCurrentUser(user);

            //Log.d("MylogCurrUser", user.toString());

            setObservers();
        });
    }

    public void setObservers() {
        mViewModel.getTestsSnap().observe(this, testsSnap -> {
            ArrayList<Test> tests = new ArrayList<>();

            for (DataSnapshot snapshot : testsSnap.getChildren()) {
                Test test = snapshot.getValue(Test.class);
                if (test != null && currentUser.getLeftAttemptions() == 0) {
                    test.setStatus(Test.TestStatus.CLOSED);
                }
                tests.add(test);
            }
            mViewModel.setTests(tests);
        });

        mViewModel.getCurrentUser(user -> {
            String fullName = user.getFirstname() + " " + user.getSurname();
            tvUser.setText(fullName);
            String leftAttempts = String.format((String) getResources().getText(R.string.nav_header_left_attempts_msg),
                    (int) user.getLeftAttemptions());
            tvUserAttempts.setText(leftAttempts);
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            CommonUtils.showChooseLanguageDialog(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchAndSetCurrentUser();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_tests) {
            transaction.replace(R.id.cl_container, new TestsFragment());

        } else if (id == R.id.nav_rating) {
            transaction.replace(R.id.cl_container, new RatingsFragment());

        } else if (id == R.id.nav_exit) {
            CommonUtils.closeApp(this);
        }
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            Intent intent = new Intent(this, IntroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        } else if (currentVersionCode > savedVersionCode) {
            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private void checkAuthentication() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        } else {
            fetchAndSetCurrentUser();
        }
    }
}
