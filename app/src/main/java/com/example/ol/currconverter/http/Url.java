package com.example.ol.currconverter.http;

/*
 * URL example:
 * http://apilayer.net/api/live?access_key=e2c9410aabead14100bc12068ecc394b&format=1&currencies=EUR
 */

import com.example.ol.currconverter.Constants;

public class Url {
  private String currencies;
  private String params;
  private String fullurl;

  Url(String currencies) {
    this.currencies = currencies;
    this.params = Constants.Url.ACCESS_PARAM + Constants.Url.ACCESS_VALUE + Constants.Url.PARAMS_DIVIDER +
        Constants.Url.FORMAT_PARAM + Constants.Url.FORMAT_VALUE + Constants.Url.PARAMS_DIVIDER +
        Constants.Url.CURRENCIES_PARAM + currencies;
    this.fullurl = Constants.Url.ENDPOINT_URL + Constants.Url.ENDPOINT_ACTION + Constants.Url.PARAMS_START + this.params;
  }

  public String getCurrencies() {
    return currencies;
  }

  public String getParams() {
    return params;
  }

  public String getFullurl() {
    return fullurl;
  }
}

