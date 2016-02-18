package com.example.ol.currconverter.db;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by oshevchenk on 17.08.2015.
 *
 * OperationDataDBHelper wrapper for get / release operations
 */
public class DBHelper {
  //for logging
  private static final String LOG_TAG = DBHelper.class.getName();

  private static OperationDataDBHelper dbHelper = null; //singleton DB helper

  public static OperationDataDBHelper getHelper() throws RuntimeException {
    if (null == dbHelper) {
      throw new RuntimeException("nulled OperationDataDBHelper");
    }
    return dbHelper;
  }

  /**
   * Creates the singleton DB helper instance for actions with OperationData, if it's possible.
   * Otherwise, remains null pointer.
   */
  public static void initHelper(Context context){
    if (null == dbHelper) {
      try {
        dbHelper = OpenHelperManager.getHelper(context, OperationDataDBHelper.class);
      } catch (IllegalStateException | IllegalArgumentException e) {
        Log.e(LOG_TAG, "FAILED to get DB Helper", e);
        dbHelper = null; //stay unassigned
      }
    }
  }

  public static void releaseHelper(){
    if (null != dbHelper) {
      OpenHelperManager.releaseHelper();
      dbHelper = null;
    }
  }
}
