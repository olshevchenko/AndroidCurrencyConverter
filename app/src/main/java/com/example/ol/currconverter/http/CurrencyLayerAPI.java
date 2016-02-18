package com.example.ol.currconverter.http;

import android.util.Log;

import com.example.ol.currconverter.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

import java.util.Map;

public class CurrencyLayerAPI {
  //for logging
  private static final String LOG_TAG = CurrencyLayerAPI.class.getName();

  private static Api api = null;
  Url url = new Url("");


  private static RestAdapter.Builder providesRestAdapterBuilder(String baseUrl) {
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    return new RestAdapter.Builder()
        .setEndpoint(baseUrl)
        .setConverter(new GsonConverter(gson))
        .setLogLevel(RestAdapter.LogLevel.FULL);
  }

  public static void initApi(){
    if (null == api) {
      RestAdapter restAdapter = providesRestAdapterBuilder(Constants.Url.ENDPOINT_URL).build();
      api = restAdapter.create(Api.class);
    }
  }

  public static void releaseApi(){
    if (null != api) {
//      ;
      api = null;
    }
  }

  public Map<String, Double> getHTTPRates() throws RetrofitError {
    Map<String, Double> currencyRates;
    try {
      Response response = api.getResponse(Constants.Url.ENDPOINT_ACTION, Constants.Url.ACCESS_VALUE,
          Constants.Url.FORMAT_VALUE, url.getCurrencies());
//      Response response = api.getResponse(url.ENDPOINT_ACTION, url.ACCESS_VALUE);
//      Log.d(LOG_TAG, "Got response: " + response);
      currencyRates = response.getQuotes();
    } catch (RetrofitError error) {
        if (error.getResponse() != null) {
            int code = error.getResponse().getStatus();
            Log.e(LOG_TAG, "Http error, status : " + code);
        } else {
            Log.e(LOG_TAG, "Unknown error");
            error.printStackTrace();
        }
        throw error;
    }
    return currencyRates;
  }

} //public class CurrencyLayerAPI


