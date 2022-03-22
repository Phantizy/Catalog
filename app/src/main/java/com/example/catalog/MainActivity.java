package com.example.catalog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionPagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary section of the activity
        mSectionPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // setup the View Pager with the sections adapter
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionPagerAdapter);

        // displays the tabs
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public void onClickShowMap(View view) {
        // use URI + intent to pull information for geolocation (MAP)
        Uri intentUri = Uri.parse("geo:36.9775016, -76.42977?Z=20");

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void onClickCall(View view) {
        // uses intent to make Phone Call *phone # is in strings.xml*
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse("tel:" + getString(R.string.our_phone)));
        if(phoneIntent.resolveActivity(getPackageManager()) !=null){
            startActivity(phoneIntent);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate (start) the fragment for the given page.
            switch (position){
                case 0:
                    return CatalogFragment.newInstance();
                case 1:
                    return NewsFragment.newInstance();
                case 2:
                    return MapsFragment.newInstance();
                case 3:
                    return SalesFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show x total pages
            return 4;
        }
        
        // sets the order for the Page Titles
        public CharSequence getPageTitle(int position){
            switch (position) {
                case 0:
                    return getString(R.string.tab_catalog); // extracted Strings
                case 1:
                    return getString(R.string.tab_news); // extracted Strings
                case 2:
                    return getString(R.string.tab_maps); // extracted Strings
                case 3:
                    return getString(R.string.tab_sales); // extracted Strings
            }
            return null;
        }

    }

    // onCreateOptionsMenu created the menu -> getMenuInflater()
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // created methods by highlighting onclick method in menu_main.xml
    // does the on click method for Show Notes Tab
    public void onClickShowNotes(MenuItem item) {
        // takes us to the NotesTextActivity class *NotesTextActivity*
        Intent i = new Intent(this, NotesTextActivity.class);
        startActivity(i);
    }

    // does the on click Show Image Notes tab
    public void onClickShowImageNotes(MenuItem item) {
        // takes us to the NotesImageActivity class -> *NotesImageActivity*
        Intent i = new Intent(this, NotesImageActivity.class);
        startActivity(i);
    }

    // does the on click Capture Location tab
    public void onClickCaptureLocation(MenuItem item) {
        // takes us to the NotesLocationActivity class -> *NotesLocationActivity*
        Intent i = new Intent(this, NotesLocationActivity.class);
        startActivity(i);
    }

    // does the on click Show Settings tab
    public void onClickShowSettings(MenuItem item) {
        // takes us to the SettingsActivity class -> *SettingsActivity*
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

}