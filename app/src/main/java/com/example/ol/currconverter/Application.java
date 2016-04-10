package com.example.ol.currconverter;

import android.content.Intent;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Created by ol on 28.01.16.
 */
public class Application extends android.app.Application {
  /// tag for logging
  private static final String LOG_TAG = Application.class.getName();
  private boolean isVKInited = false;


    public boolean getIsVKInited() {
    return isVKInited;
  }

  VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
    @Override
    public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
      if (newToken == null) {
        /// VKAccessToken is invalid - then break all we do & restart login
        Intent intent = new Intent(Application.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      }
    }
  };

  @Override
  public void onCreate() {
    super.onCreate();
    vkAccessTokenTracker.startTracking();
    try {
      VKSdk.initialize(this);
      isVKInited = true;
    } catch (RuntimeException rex) {
      Log.w(LOG_TAG, "FAILED to initialize VK SDK", rex);
    }
  }

}