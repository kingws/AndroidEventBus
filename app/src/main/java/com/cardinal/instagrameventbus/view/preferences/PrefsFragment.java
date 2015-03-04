package com.cardinal.instagrameventbus.view.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.cardinal.instagrameventbus.R;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         14:00
 */
public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

    }
}
