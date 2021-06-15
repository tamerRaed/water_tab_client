package com.tamer.alna99.watertabclient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.tamer.alna99.watertabclient.fragments.AboutUsFragment;
import com.tamer.alna99.watertabclient.fragments.ConcatUsFragment;
import com.tamer.alna99.watertabclient.fragments.HomepageFragment;
import com.tamer.alna99.watertabclient.fragments.OldOrdersFragment;
import com.tamer.alna99.watertabclient.model.SharedPrefs;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        HomepageFragment homepageFragment = new HomepageFragment();
        moveFragment(homepageFragment);
        toolbar.setTitle(getString(R.string.homepage));

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView tv = header.findViewById(R.id.name);
        tv.setText(SharedPrefs.getUserName(getApplicationContext()));
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homepage:
                HomepageFragment homepageFragment = new HomepageFragment();
                moveFragment(homepageFragment);
                toolbar.setTitle(getString(R.string.homepage));
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                break;

            case R.id.old_orders:
                OldOrdersFragment oldOrdersFragment = new OldOrdersFragment();
                moveFragment(oldOrdersFragment);
                toolbar.setTitle(getString(R.string.old_orders));
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                break;

            case R.id.settings:
                // TODO: Settings activity
                break;
            case R.id.about_us:
                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                moveFragment(aboutUsFragment);
                toolbar.setTitle(getString(R.string.about_us));
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                break;
            case R.id.concat_us:
                ConcatUsFragment concatUsFragment = new ConcatUsFragment();
                moveFragment(concatUsFragment);
                toolbar.setTitle(getString(R.string.concat_us));
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                break;

            case R.id.logout:
                onBackPressed();
                break;
        }
        return false;
    }


    public void moveFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out).replace(R.id.patient_frameLayout, fragment).commit();
    }
}
