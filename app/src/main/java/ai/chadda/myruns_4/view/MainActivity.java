package ai.chadda.myruns_4.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import ai.chadda.myruns_4.R;
import ai.chadda.myruns_4.view.fragments.HistoryFragment;
import ai.chadda.myruns_4.view.fragments.StartFragment;

public class MainActivity extends AppCompatActivity {

    // Constants
    public final static String PROFILE_ORIGIN_KEY = "origin";
    public final static String PROFILE_ORIGIN_VALUE = "main";

    // Globals
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private MenuItem mMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up layout
        setContentView(R.layout.activity_main);
        mNavigationView = findViewById(R.id.main_bottom_nav);
        mViewPager = findViewById(R.id.main_view_pager);

        // Set up fragments and adapter
        fragments.add(new StartFragment());
        fragments.add(new HistoryFragment());
        MainActivityAdapter mAdapter = new MainActivityAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);

        // Set up navigation and viewpage listeners
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_start:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_history:
                        mViewPager.setCurrentItem(1);
                        break;
                }
                return false;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                // If on first selection, uncheck start fragment
                // Otherwise, uncheck current fragment
                if (mMenuItem != null) mMenuItem.setChecked(false);
                else mNavigationView.getMenu().getItem(0).setChecked(false);

                // Check new fragment and update current item selection
                mNavigationView.getMenu().getItem(position).setChecked(true);
                mMenuItem = mNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.edit_profile:
                intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra(PROFILE_ORIGIN_KEY, PROFILE_ORIGIN_VALUE);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
