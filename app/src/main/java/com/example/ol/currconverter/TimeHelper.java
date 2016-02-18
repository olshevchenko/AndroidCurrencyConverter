package com.example.ol.currconverter;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by ol on 23.11.15.
 */
public class TimeHelper {
  //for logging
  private static final String LOG_TAG = TimeHelper.class.getName();

  public static String datesDiff2Str(Date oldDT, FragmentActivity activity) {
    long oldT = oldDT.getTime()/1000;
    if (0 == oldT)
      //Unix epoch time consider as the initial never-update-yet sign
      return activity.getString(R.string.tvRatesRefreshDateNeverValue);

    long curT = System.currentTimeMillis()/1000;

    long duration = Math.abs(curT - oldT);

    long diffInMinutes, diffInHours, diffInDays;
    String errResult = new String("...");
    try {
      diffInDays = TimeUnit.SECONDS.toDays(duration);
      duration -= TimeUnit.SECONDS.convert(diffInDays, TimeUnit.DAYS);
      diffInHours = TimeUnit.SECONDS.toHours(duration);
      duration -= TimeUnit.SECONDS.convert(diffInHours, TimeUnit.HOURS);
      diffInMinutes = TimeUnit.SECONDS.toMinutes(duration);
    } catch (Exception e) {
      Log.w(LOG_TAG, "Failed to compute time duration");
      return errResult;
    }

    StringBuilder resBuilder = new StringBuilder();
    if (diffInDays != 0)
      resBuilder.append(diffInDays + activity.getString(R.string.tvRatesRefreshDateAbbrevD));
    if (diffInHours != 0)
      resBuilder.append(diffInHours + activity.getString(R.string.tvRatesRefreshDateAbbrevH));
    resBuilder.append(diffInMinutes + activity.getString(R.string.tvRatesRefreshDateAbbrevM)); //use minutes - always even if 0.

    resBuilder.append(activity.getString(R.string.tvRatesRefreshDateEnding));

    return resBuilder.toString();
    }
}
