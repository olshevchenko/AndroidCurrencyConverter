package com.example.ol.currconverter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

/**
 * Created by ol on 27.01.16.
 */
public class SocNetHelper {
  /// tag for logging
  private static final String LOG_TAG = SocNetHelper.class.getName();

  private static SocNetHelper snHelper = null;

  /// boolean initialization mask
  private static short snInitStatus = 0;
  private static short bitVKInited = 1; /// mask for VK soc.network init succeeded sign
  private static short bitFBInited = 2; /// for FB one
  private static short bitGPInited = 4; /// for Google+

  private static String[] vkScope = new String[] {}; /// no permissions for VK - just signing through

  private static Activity activity;
  private String userID; /// ID for using as User's one for application session

  private SocNetHelper() {
  }

  public static SocNetHelper getHelper() throws RuntimeException {
    if (null == snHelper) {
      throw new RuntimeException("nulled SocNetHelper");
    }
    return snHelper;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  /**
   * allocates SocNetHelper singleton instance
   * initializes all soc. networks with setting of the corresponding InitStatus field bits
   * @param _activity reference to the parent activity
   */
  public static void init(Activity _activity) {
    if (null == snHelper) {
      snHelper = new SocNetHelper();
      activity = _activity;
//      snInitStatus |= bitVKInited | bitFBInited | bitGPInited; /// let all succeeded
      snInitStatus |= bitVKInited;

      Application myApplication = (Application) activity.getApplicationContext();
      if (! myApplication.getIsVKInited()) /// believe docs, have to init VK above in Application, not here...
        snInitStatus &= ~bitVKInited; /// set VK init bit back to 0

      ; /// try to init FB here...
      ; /// same as G+
    }
  }

  /**
   * executes VK authorization explicitly or hidden
   * @return true - if we have userID right now (from saved VK access token) and ready to start session
   *         false - if VK not initialized or if will get userID soon - through activity
   *         result callbacks
   */
  public boolean loginVK() {
    if ((snInitStatus & bitVKInited) == 0) {
      Log.w(LOG_TAG, "Trying to login VK SDK NOT initialized");
      Toast toast = Toast.makeText(activity, R.string.vk_init_failed, Toast.LENGTH_SHORT);
      toast.setGravity(Gravity.CENTER, 0, 0);
      toast.show();
      return false;
    }
    /// VK has been initialized
    if (VKSdk.wakeUpSession(activity)) {
      VKAccessToken token = VKAccessToken.currentToken();
/*
      VKAccessToken token = VKAccessToken.tokenFromSharedPreferences(activity,
          Constants.SocAppsIDs.VKTokenKey);
*/
      if (null != token) {
        /// it's ALL ok - use actual stored VK access token & get ID from there
        userID = Constants.SocAppsIDs.VKAppIdPrefix + token.userId;
        getHelper().setUserID(userID);
        return true;
      }
    }
    /// VK access token doesn't exist or expired or nulled somehow
    VKSdk.login(activity, vkScope);
    return false;
  }


  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

    boolean res = VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
      @Override
      public void onResult(VKAccessToken res) {
        /// Пользователь успешно авторизовался
        userID = Constants.SocAppsIDs.VKAppIdPrefix + res.userId;
        Log.i(LOG_TAG, "VK login succeeded, userID:" + userID);
      }
      @Override
      public void onError(VKError error) {
        /// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
        userID = "";
        Log.w(LOG_TAG, "VK login failed");
        Toast toast = Toast.makeText(activity, R.string.vk_login_failed, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
      }
    });
    return res;
  }

  public void done() {
    if (null != snHelper) {
      ;
      snHelper = null;
    }
  }
}
