package com.example.ol.currconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ol.currconverter.db.DBHelper;
import com.example.ol.currconverter.http.CurrencyLayerAPI;

import java.util.Locale;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

  private static boolean isMainActivityStarted = false; //helps to find proper app lifecycle positions during termination
  private TextView tvRegLogin, tvUnregLogin;

  private Button btnVKLogin, btnFBLogin, btnGooglePlusLogin;
  private Button btnGuestLogin;
  private ActionBar actionBar;

  //for logging
  private static final String LOG_TAG = LoginActivity.class.getName();

  //for preferences
  private SharedPreferences sPref;
  private Editor sPrefEd;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(LOG_TAG, "onCreate()");
    super.onCreate(savedInstanceState);

    sPref = getSharedPreferences(SharedData.SHARED_PREF_FNAME_TAG, MODE_PRIVATE);
    sPrefEd = sPref.edit();

    setContentView(R.layout.activity_login);

    //DB initializing
    DBHelper.initHelper(getApplicationContext());
    try {
      DBHelper.getHelper().initOperationDataDao();
    } catch (RuntimeException e) {} //ignoring DB failure
    isMainActivityStarted = false;

    //HTTP initializing
    CurrencyLayerAPI.initApi();

    //Soc.Networks initializing
    SocNetHelper.init(this);

    actionBar = getSupportActionBar();
    actionBar.setTitle(R.string.title_activity_login); ///repeat naming for possible locale change

    tvRegLogin = (TextView) findViewById(R.id.tvRegLogin);
    tvUnregLogin = (TextView) findViewById(R.id.tvUnregLogin);

    btnVKLogin = (Button) findViewById(R.id.btVKsignin);
    btnVKLogin.setOnClickListener(this);

    btnFBLogin = (Button) findViewById(R.id.btFBsignin);
    btnFBLogin.setEnabled(false);
    btnFBLogin.setOnClickListener(this);

    btnGooglePlusLogin = (Button) findViewById(R.id.btGooglePlussignin);
    btnGooglePlusLogin.setEnabled(false);
    btnGooglePlusLogin.setOnClickListener(this);


//ToDo Remove Users buttons after social networks authority implementations
    btnGuestLogin = (Button) findViewById(R.id.btGuestlogin);
    btnGuestLogin.setOnClickListener(this);

    setLocale(loadLangData());
    updateLanguage(); ///ToDo - remove it after fixing the problem of non-calling onConfigurationChanged()
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
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    Log.i(LOG_TAG, "onDestroy()");
    DBHelper.getHelper().close();
    DBHelper.releaseHelper();
    CurrencyLayerAPI.releaseApi();
    SocNetHelper.getHelper().done();
    super.onDestroy();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    updateLanguage();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (SocNetHelper.getHelper().onActivityResult(requestCode, resultCode, data)) {
      String userID = SocNetHelper.getHelper().getUserID();
      if (userID.isEmpty())
        ; /// try auth. again ...
      else {
        saveUserData(userID);
        startMain();
      }
    }
    else
      super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btVKsignin:
        if (SocNetHelper.getHelper().loginVK()) {
          saveUserData(SocNetHelper.getHelper().getUserID());
          startMain();
        }
        else
          ; /// do nothing - on init error or on waiting for the result of VK login (see onActivityResult())...
        break;
      case R.id.btFBsignin:
        saveUserData("Guest");
        break;
      case R.id.btGooglePlussignin:
        saveUserData("Guest");
        break;
      case R.id.btGuestlogin:
      default:
        saveUserData("Guest");
        startMain();
        break;
    }
  }

  private void startMain() {
    Intent intent = new Intent(this, MainActivity.class);
    isMainActivityStarted = true; //trust in next activity using the DB
    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_login, menu);

    ///update corresponding 'set' item state
    String lang = loadLangData();
    if (lang.equals(Constants.Languages.ENG)) {
      MenuItem item = menu.findItem(R.id.action_settings_login_en);
      if (null != item)
        item.setChecked(true);
      else
        ; //just ignore 'en' menu language setting
    }
    if (lang.equals(Constants.Languages.RUS)) {
      MenuItem item = menu.findItem(R.id.action_settings_login_ru);
      if (null != item)
        item.setChecked(true);
      else
        ; //just ignore 'ru' menu language setting
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    android.content.res.Configuration config;
    Locale myLocale;

    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    switch (item.getItemId()) {
      case R.id.action_about_login:
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
        return true;
      case R.id.action_settings_login_en:
        if (true == item.isChecked())
          return true; ///skip second setting
        item.setChecked(true);
        saveLangData(Constants.Languages.ENG);
//        Toast.makeText(this, "Gonna change language!", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "OL-DBG: LoginActivity.onOptionsItemSelected() - changing lang to " + Constants.Languages.ENG);
        setLocale(Constants.Languages.ENG);
        updateLanguage(); ///ToDo - remove it after fixing the problem of non-calling onConfigurationChanged()
        return true;
      case R.id.action_settings_login_ru:
        if (true == item.isChecked())
          return true; ///skip second setting
        item.setChecked(true);
        saveLangData(Constants.Languages.RUS);
//        Toast.makeText(this, "Gonna change language!", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "OL-DBG: LoginActivity.onOptionsItemSelected() - changing lang to " + Constants.Languages.RUS);
        setLocale(Constants.Languages.RUS);
        updateLanguage(); ///ToDo - remove it after fixing the problem of non-calling onConfigurationChanged()
        return true;
      case R.id.action_logout:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    } //switch
  }

  void saveUserData(String userName) {
    sPrefEd.putString(SharedData.USER_NAME_TAG, userName);
    sPrefEd.commit();
  }

  void saveLangData(String language) {
    sPrefEd.putString(SharedData.LANGUAGE_TAG, language);
    sPrefEd.commit();
  }

  private String loadLangData() {
    String str;
    try {
      str = sPref.getString(SharedData.LANGUAGE_TAG, Constants.Languages.ENG);
    } catch (Exception ex) {
      str = new String(Constants.Languages.ENG); ///'en' is default language anyway
    }
    return str;
  }

  private void setLocale(String loc) {
    ///compare new locale with the default one - may be no needs to reset language
    if (loc.equals(Locale.getDefault().toString()))
      return;
    Log.i(LOG_TAG, "OL-DBG: LoginActivity.setLocale() - changing locale to " + loc);
    Locale myLocale = new Locale(loc);
    Locale.setDefault(myLocale);
    Resources resources = getResources();
//    Configuration config = resources.getConfiguration();
    Configuration config = new Configuration();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      config.setLocale(myLocale);
    } else {
      config.locale = myLocale;
    }
    resources.updateConfiguration(config, resources.getDisplayMetrics());
//    resources.updateConfiguration(config, null);
  }

  private void updateLanguage() {
    actionBar.setTitle(R.string.title_activity_login);
    invalidateOptionsMenu();
    tvRegLogin.setText(R.string.regLoginText);
    tvUnregLogin.setText(R.string.unregLoginText);

    btnVKLogin.setText(R.string.vksignin);
    btnFBLogin.setText(R.string.fbsignin);
    btnGooglePlusLogin.setText(R.string.gooplussignin);
    btnGuestLogin.setText(R.string.guestlogin);
  }
}
