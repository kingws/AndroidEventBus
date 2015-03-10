package com.cardinal.instagrameventbus.common;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cardinal.instagrameventbus.R;
import com.cardinal.instagrameventbus.utils.AppConstants;
import com.cardinal.instagrameventbus.utils.Utils;
import com.cardinal.instagrameventbus.view.instagram.InstagramFragment;
import com.cardinal.instagrameventbus.view.instagram.InstagramImageDisplayFragment;
import com.cardinal.instagrameventbus.view.preferences.SetPreferencesActivity;

/**
 * Instagram Event Bus
 *
 * @author Shane King
 *         2 March 2015
 *         08:00
 */
public class MainActivity extends ActionBarActivity implements
        InstagramFragment.OnInstagramFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private Boolean shouldShowOptionsMenu = true;

    public SharedPreferences mySharedPreferences;

    public Toolbar mToolbar;

    private String mInstagramHashtag;

    private AlertDialog mAlertDialog;

    public boolean toolbarShowing = true;

    @Override
    protected void onCreate(Bundle savedInstanceStateIn) {
        super.onCreate(savedInstanceStateIn);

        setContentView(R.layout.activity_main);

        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initToolbar();

        loadPref();

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    private void showFragment() {

        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment fragment = new InstagramFragment();

        Bundle args = new Bundle();
        args.putString(AppConstants.HASHTAG, mInstagramHashtag);
        fragment.setArguments(args);

        String fragmentName = InstagramFragment.class.getName();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment, fragmentName)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(fragmentName)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_preferences:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SetPreferencesActivity.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.action_instagram:
                startInstagramIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!shouldShowOptionsMenu) {
            hideMenuItems(menu, shouldShowOptionsMenu);
            invalidateOptionsMenu();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void hideMenuItems(Menu menu, boolean visible) {
        for(int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(visible);
        }
    }

    private void menuValidation(boolean invalidate) {
        shouldShowOptionsMenu = invalidate;
        invalidateOptionsMenu();
    }

    @Override
    public void setTitle(CharSequence textIn) {
        mToolbar.setTitle(textIn);
    }

    @Override
    public void onInstagramFragmentInteraction(String imageUrl, String userName, String linkURL) {
        menuValidation(false);

//        if (!toolbarShowing) {
//            showViews();
//        }

        Fragment mFragment = InstagramImageDisplayFragment.newInstance(getApplicationContext(),
                imageUrl, userName, linkURL);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_back);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.frame_container, mFragment, mFragment.getClass().getName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(mFragment.getClass().getName())
                .commit();

    }

    @Override
    public void onBackPressed() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        menuValidation(true);

        getFragmentManager().executePendingTransactions();

        int backStackIndex = getFragmentManager().getBackStackEntryCount() - 1;
        String name = getFragmentManager().getBackStackEntryAt(backStackIndex).getName();
        Fragment fragment = getFragmentManager().findFragmentByTag(name);

        getFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        setTitle(mInstagramHashtag);

        //If the fragment is the agenda fragment, exit
        if (fragment instanceof InstagramFragment) {
            finish();
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mInstagramHashtag.equalsIgnoreCase(getHashtagFromPrefs())) {
            loadPref();
        }
    }

    private void loadPref(){
        mInstagramHashtag = getHashtagFromPrefs();
        setTitle(mInstagramHashtag);
        showFragment();
    }

    private String getHashtagFromPrefs() {
        String name = mySharedPreferences.getString("instagram_preference", "Apple");
        return name;
    }

    private void startInstagramIntent() {

        Intent instagram = getPackageManager().getLaunchIntentForPackage(
                "com.instagram.android");
        if (Utils.isIntentAvailable(getApplicationContext(), instagram)){
            startActivity(instagram);
        } else{
            showDialog();
        }
    }

    private void showDialog() {

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View layout = inflater.inflate(R.layout.dialog_custom_double_button,
                null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(layout);

        TextView alertTitle = (TextView) layout
                .findViewById(R.id.dialog_double_button_title_text_view);
        alertTitle.setText(R.string.instagram_view_app_store_title);

        TextView alertText = (TextView) layout
                .findViewById(R.id.dialog_double_button_text_view);
        alertText.setText(R.string.instagram_view_app_store_message);

        Button alertYesButton = (Button) layout
                .findViewById(R.id.dialog_double_button_ok_button);

        alertYesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View vIn) {
                mAlertDialog.cancel();
                Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW);
                googlePlayIntent.setData(Uri.parse("market://details?id=com.instagram.android"));

                if (Utils.isIntentAvailable(getApplicationContext(), googlePlayIntent)) {
                    startActivity(googlePlayIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No supported viewer...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button alertNoButton = (Button) layout
                .findViewById(R.id.dialog_double_button_no_button);

        alertNoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View vIn) {
                mAlertDialog.cancel();
            }
        });

        this.mAlertDialog = alertDialogBuilder.create();
        this.mAlertDialog.show();

    }

    public void hideViews() {
        mToolbar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.translate_up_off));
        setToolbarShowing(false);
    }

    public void showViews() {
        mToolbar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.translate_up_on));
        setToolbarShowing(true);
    }

    public boolean isToolbarShowing() {
        return toolbarShowing;
    }

    public void setToolbarShowing(boolean toolbarShowing) {
        this.toolbarShowing = toolbarShowing;
    }
}