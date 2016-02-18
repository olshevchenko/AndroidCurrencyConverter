package com.example.ol.currconverter;

import com.vk.sdk.VKSdk;

/**
 * Created by ol on 28.01.16.
 */
public class Application extends android.app.Application {

  @Override
  public void onCreate() {
    super.onCreate();
    VKSdk.initialize(this);
  }
}