package com.cardinal.instagrameventbus.view.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.cardinal.instagrameventbus.R;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         14:05
 */
public class SetPreferencesActivity extends ActionBarActivity {

    private String mInstagamHashTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mInstagamHashTag = mySharedPreferences.getString("instagram_preference", "Apple");
        setTitle(getResources().getString(R.string.instagram_pref_activity_title));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();

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
