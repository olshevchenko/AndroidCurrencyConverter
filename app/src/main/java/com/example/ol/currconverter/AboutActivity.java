package com.example.ol.currconverter;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);

    setTitle(R.string.title_about); ///repeat naming for possible locale change
  }
}
