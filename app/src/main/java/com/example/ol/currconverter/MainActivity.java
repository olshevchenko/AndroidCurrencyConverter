package com.example.ol.currconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

  //for logging
  private static final String LOG_TAG = MainActivity.class.getName();

  /**
   * drawer navigation among fragments
   */
  String[] drawerListViewItems; /// fragment titles
  private DrawerLayout drawerLayout; /// fragment navigation
  private ListView drawerListView; /// fragment navigation
  private ActionBarDrawerToggle drawerToggle; /// actionbar toggle
  private int curDrawerListPosition = -1; /// fragment item position in the drawer list

  private ActionBar actionBar;

  private Session userSession; //integrator with currency quotes and operations history engines

  private SharedPreferences sPref;

  /**
   * item click listener 4 the nav.drawer
   */
  private class DrawerItemClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      selectItem(position);
    }
  }

  /**
   * create & init drawer for navidation among fragments
   * @return navigation drawer list
   */
  private ListView createDrawerList(Bundle savedInstanceState) {
    ListView drawerListView = (ListView)findViewById(R.id.drawer);
    drawerListView.setAdapter(new ArrayAdapter<>(this,
        android.R.layout.simple_list_item_activated_1, drawerListViewItems));
    drawerListView.setOnItemClickListener(new DrawerItemClickListener());
    return drawerListView;
  }

  /**
   * create the ActionBar toggle for the drawer
   */
  private ActionBarDrawerToggle createDrawerToggle() {
    final CharSequence title = getTitle();
    ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
        this,
        drawerLayout,
        R.string.drawer_open,
        R.string.drawer_close) {
      @Override
      public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        invalidateOptionsMenu();
      }
      @Override
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        invalidateOptionsMenu();
      }
    };
    return drawerToggle;
  }

  private void selectItem(int position) {
    if (curDrawerListPosition == position) {
      /// nothing to do
      drawerLayout.closeDrawer(drawerListView);
      return;
    }
    curDrawerListPosition = position;
    Fragment fragment;
    switch (position) {
      case 1:
        fragment = new OperationsFragment();
        break;
      case 0:
      default:
        fragment = new ConverterFragment();
        break;
    }
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content_fragment, fragment);
    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    ft.commit();

    drawerListView.setItemChecked(position, true);
    setActionBarTitle(position);
    drawerLayout.closeDrawer(drawerListView);
  }


  private void setActionBarTitle(int position) {
      String title = drawerListViewItems[position].toUpperCase();
      actionBar.setTitle(title);
  }

  public Session getUserSession() {
    return userSession;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(LOG_TAG, "onCreate()");
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    actionBar = getSupportActionBar();

    drawerListViewItems = getResources().getStringArray(R.array.fragment_titles);

    /// set up drawer for navigation among two primary fragments
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    drawerListView = createDrawerList(savedInstanceState);
    drawerToggle = createDrawerToggle();
    drawerLayout.setDrawerListener(drawerToggle);

    String userName = loadUserData(); //user login name
//    Toast.makeText(this, "username: " + userName, Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG,"OL-DBG: MainActivity.onCreate() - user " + userName);

    userSession = new Session(getApplicationContext(), new String(userName));

    if (null != savedInstanceState) {
      curDrawerListPosition = savedInstanceState.getInt("position", 0);
      setActionBarTitle(curDrawerListPosition);
    } else {
      selectItem(0);
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
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
    outState.putInt("position", curDrawerListPosition);
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
  public boolean onPrepareOptionsMenu(Menu menu) {
    // If the nav drawer is open, hide action items related to the content view
    boolean drawerOpen = drawerLayout.isDrawerOpen(drawerListView);
    menu.findItem(R.id.action_about_main).setVisible(!drawerOpen);
    if (drawerOpen) {
      /// hide the virtual keyboard
      ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
          hideSoftInputFromWindow(drawerLayout.getWindowToken(), 0);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    switch (item.getItemId()) {
      case R.id.action_about_main:
        startActivity(new Intent(this, AboutActivity.class));
        return true;

      case R.id.action_logout:
        finish();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    } //switch
  }

} //public class MainActivity
