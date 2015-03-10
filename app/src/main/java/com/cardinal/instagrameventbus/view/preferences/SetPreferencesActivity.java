package com.cardinal.instagrameventbus.view.preferences;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.cardinal.instagrameventbus.R;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         14:05
 */
public class SetPreferencesActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings_prefs);

        initToolbar();

        getFragmentManager().beginTransaction().replace(R.id.settings_frame_container,
                new PrefsFragment()).commit();

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.custom_prefs_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setTitle(getResources().getString(R.string.instagram_pref_activity_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
