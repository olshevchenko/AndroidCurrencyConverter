package com.example.ol.currconverter;

import android.app.Activity;
import android.content.Context;
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
  //tag for logging
  private static final String LOG_TAG = SocNetHelper.class.getName();

  private static SocNetHelper snHelper = null;

  /// boolean initialization mask
  private static short snInitStatus = 0;
  private static short bitVKInited = 1; /// mask for VK soc.network init succeeded sign
  private static short bitFBInited = 2; /// for FB one
  private static short bitGPInited = 4; /// for Google+

  private static Activity activity;
  private static String[] vkScope = new String[] {};

  private SocNetHelper() {
  }

  public static SocNetHelper getHelper() throws RuntimeException {
    if (null == snHelper) {
      throw new RuntimeException("nulled SocNetHelper");
    }
    return snHelper;
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
//      snInitStatus |= bitVKInited | bitFBInited | bitGPInited; //let all succeeded
      snInitStatus |= bitVKInited;
      try {
        ;
//        VKSdk.initialize(activity.getApplicationContext());
//ToDo !remove artificial exception!
//        throw new RuntimeException("VKSdk.initialize failed");
      } catch (RuntimeException re) {
        Log.w(LOG_TAG, "FAILED to initialize VK SDK", re);
        snInitStatus &= ~bitVKInited; //set VK bit back to 0
      }
    }
  }

  public boolean loginVK() {
    if (1== (snInitStatus & bitVKInited)) { //VK has been initialized
      VKSdk.login(activity, vkScope);
      return true;
    }
    else {
      Log.w(LOG_TAG, "Trying to login VK SDK NOT initialized");
      Toast toast = Toast.makeText(activity, R.string.vk_init_failed, Toast.LENGTH_SHORT);
      toast.setGravity(Gravity.CENTER, 0, 0);
      toast.show();
      return false;
    }
  }


  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

    boolean res = VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
      @Override
      public void onResult(VKAccessToken res) {
// Пользователь успешно авторизовался
        Log.i(LOG_TAG, "VK login succeeded");
        Toast toast = Toast.makeText(activity, R.string.vk_login_succeeded, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
      }
      @Override
      public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
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
