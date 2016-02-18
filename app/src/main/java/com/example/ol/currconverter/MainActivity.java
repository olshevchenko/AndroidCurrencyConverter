package com.example.ol.currconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

  //for logging
  private static final String LOG_TAG = MainActivity.class.getName();

  private Session userSession; //integrator with currency quotes and operations history engines

  private SharedPreferences sPref;

  SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link android.support.v4.view.ViewPager} that will host the section contents.
   */
  NonSwipeableViewPager mViewPager;

  //for interaction w. fragments
  public Session getUserSession() {
    return userSession;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(LOG_TAG, "onCreate()");
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    // Set up the action bar
    final ActionBar actionBar = getSupportActionBar();
    actionBar.setTitle(R.string.title_activity_main); ///repeat naming for possible locale change

    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    actionBar.setDisplayShowHomeEnabled(false);

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (NonSwipeableViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    // When swiping between different sections, select the corresponding
    // tab. We can also use ActionBar.Tab#select() to do this if we have
    // a reference to the Tab.
    mViewPager.setOnPageChangeListener(new NonSwipeableViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
      }
    });

    // For each of the sections in the app, add a tab to the action bar.
    for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
      // Create a tab with text corresponding to the page title defined by
      // the adapter. Also specify this Activity object, which implements
      // the TabListener interface, as the callback (listener) for when
      // this tab is selected.
      actionBar.addTab(
        actionBar.newTab()
          .setText(mSectionsPagerAdapter.getPageTitle(i))
          .setTabListener(this));
    }

    if (savedInstanceState != null) {
      actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
    }

    String userName = loadUserData(); //user login name
//    Toast.makeText(this, "username: " + userName, Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "OL-DBG: MainActivity.onCreate() - user " + userName);

    userSession = new Session(getApplicationContext(), new String(userName));
  }

  @Override
  protected void onResume() {
    Log.i(LOG_TAG, "onResume()");
    super.onResume();
  }

  @Override
  protected void onStart() {
    Log.i(LOG_TAG, "onStart()");
    super.onStart();
  }

  @Override
  protected void onPause() {
    Log.i(LOG_TAG, "onPause()");
    super.onPause();
  }

  @Override
  protected void onStop() {
    Log.i(LOG_TAG, "onStop()");
//    userSession.onExit();
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    Log.i(LOG_TAG, "onDestroy()");
    userSession.onExit();
    super.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
  }

  private String loadUserData() {
    sPref = getSharedPreferences(SharedData.SHARED_PREF_FNAME_TAG, MODE_PRIVATE);
    String str;
    try {
      str = sPref.getString(SharedData.USER_NAME_TAG, "");
    } catch (Exception ex) {
      str = new String(""); ///default user anyway
    }
    return str;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_about_main) {
      Intent intent = new Intent(this, AboutActivity.class);
      int res = 0;
      startActivityForResult(intent, res);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    // When the given tab is selected, switch to the corresponding page in
    // the ViewPager.
    mViewPager.setCurrentItem(tab.getPosition());
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  /**
   * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class below).
      return PlaceholderFragment.newInstance(position);
    }

    @Override
    public int getCount() {
      // Show 2 total pages.
      return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      Locale l = Locale.getDefault();
      switch (position) {
        case 0:
          return getString(R.string.title_fragment_converter).toUpperCase(l);
        case 1:
          return getString(R.string.title_fragment_operations).toUpperCase(l);
      }
      return null;
    }
  } //class SectionsPagerAdapter

  public static class PlaceholderFragment extends Fragment {
    public static PlaceholderFragment newInstance(int position) {
      PlaceholderFragment fragment;
      switch (position) {
        case 0:
          fragment = new ConverterFragment();
          break;
        case 1:
        default:
          fragment = new OperationsFragment();
          break;
      }
      return fragment;
    }

    public PlaceholderFragment() {
    }

  } //public static class PlaceholderFragment

} //public class MainActivity
