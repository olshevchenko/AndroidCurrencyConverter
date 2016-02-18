package com.example.ol.currconverter.currencies;

import android.content.Context;
import android.util.Log;

import com.example.ol.currconverter.Constants;
import com.example.ol.currconverter.http.CurrencyLayerAPI;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import retrofit.RetrofitError;

/**
 * Created by oshevchenk on 11.08.2015.
 */
public class CurrencyRatesHelper {
  private static final String LOG_TAG = CurrencyRatesHelper.class.getName();

  private CurrencyRates crs;
  private CurrencyLayerAPI api;

  public CurrencyRatesHelper() {

    crs = new CurrencyRates();
//    crs = new CurrencyRates(new Date(0L));
    api = new CurrencyLayerAPI();
  }

  public CurrencyRates getRates() {
    return crs;
  }

  /**
   * !! ASYNCHRONOUS !! currencies rates refreshing (via http get request)
   * @throws RetrofitError if there's networks or addresses problem
   */
  public void refreshRates() throws RetrofitError {
    crs.setQuotesMap(api.getHTTPRates());
    crs.setQuotesDate(new Date()); //set day current if succeeded
    return;
  }

  /**
   * serializes CurrencyRates object
   *
   * @param
   * @return  true if succeeded
   */
  public boolean serialaizeMe(Context context) {
    boolean result = true;
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    try {
      fos = context.openFileOutput(Constants.CurrencyRates.SER_FNAME, Context.MODE_PRIVATE);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(crs);
    } catch (Exception e) {
      Log.w(LOG_TAG, "While saving currency rates data:" + e);
      result = false;
    }
    finally {
      try {
        if (null != oos)
          oos.close();
        if (null != fos)
          fos.close();
      } catch (Exception e) {}
    }
    return result;
  }


  /**
   * unserializes CurrencyRates object form the disk record
   * if succeeded, replace the current crs to the unserialized one
   *
   * @param
   * @return  true if succeeded
   */
  public boolean unserialaizeMe(Context context) {
    CurrencyRates crs_new;
    boolean result = true;
    FileInputStream fis = null;
    ObjectInputStream ois = null;
    try {
      fis = context.openFileInput(Constants.CurrencyRates.SER_FNAME);
      ois = new ObjectInputStream(fis);
      crs_new =  (CurrencyRates) ois.readObject();
    } catch (Exception e) {
      Log.w(LOG_TAG, "While restoring currency rates data:" + e);
      crs_new = crs; //restore original rates
      result = false;
    }
    finally {
      try {
        if (null != ois)
          ois.close();
        if (null != fis)
          fis.close();
      } catch (Exception e) {}
    }
    crs = crs_new;
    return result;
  }

  /**
   * save currency rates to the disk
   */
  public void onExit(Context context) {
    serialaizeMe(context); //ignore result code
    return;
  }

} //class CurrencyRatesHelper
