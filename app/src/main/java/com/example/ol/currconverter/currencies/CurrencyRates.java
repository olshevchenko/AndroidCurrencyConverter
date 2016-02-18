package com.example.ol.currconverter.currencies;

import android.util.Log;

import com.example.ol.currconverter.Constants;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by oshevchenk on 30.07.2015.
 */
public class CurrencyRates implements Serializable {
  private static final String LOG_TAG = CurrencyRates.class.getName();
  private static final long serialVersionUID = Constants.CurrencyRates.SERIAL_FILE_VERSION;
  private Locale defltlocale = Locale.getDefault();
  private Date quotesDate;
  private Map<String, Double> quotesMap = new LinkedHashMap<String, Double>();

  public CurrencyRates() {
    quotesDate = new Date(0L); //zero date as a just-created sign
    quotesMap.put("USD", 1.0d); //prevent empty currencies list
  }

/*
  public CurrencyRates(Date date) {
    quotesDate = date;
    quotesMap.put("CAD", 1.3d);
    quotesMap.put("EUR", 0.9d);
    quotesMap.put("RUB", 60.0d);
    quotesMap.put("JPY", 120.0d);
    quotesMap.put("CNY", 6.0d);
    quotesMap.put("USD", 1.0d);
  }
*/

  public void setQuotesDate(Date quotesDate) {
    this.quotesDate = quotesDate;
  }

  public Date getQuotesDate() {
    return quotesDate;
  }

  public void setQuotesDate(long timestamp) {
    this.quotesDate = new Date (timestamp*1000);
  }

  public Map<String, Double> getQuotesMap() {
    return quotesMap;
  }

  // set all currency quotes with long currency pairs name pretrancating (to corresponding ISO code view)
  public void setQuotesMap(Map<String, Double> map) {
    if (null != map)
      quotesMap.clear();
    else
      return;
    for (Map.Entry entry: map.entrySet()) {
      String currCode = (String) entry.getKey();
      try {
        this.quotesMap.put(currCode.substring(3), (Double) entry.getValue());
      } catch (Exception e) {
        Log.w(LOG_TAG, "While processing currency codes list:" + e);
//        System.out.println("W: While processing currency codes list:" + e);
      } //just skip this element & continue adding loop
    }
  }

  //for listView with currency codes
  public Set<String> getQuotesCurrencies() {
    return quotesMap.keySet();
  }

  public String toString() {
    SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.OperationData.DATE_FORMAT, defltlocale);
    return "<" + dateFormatter.format(quotesDate) + ">: [" +
           quotesMap + "]";
  }

  /**
   * gets quotes value (to USD) by its ISO code
   *
   * @param usedISOCode
   * @return double-typed value or 0.0d if input currency not found
   */
  private Double getVal(String usedISOCode) {
    Double val = quotesMap.get(usedISOCode);
    if (val == null ) {
      Log.w(LOG_TAG, "Can not get value rate value for '" + usedISOCode + "' currency.");
//      System.out.println("W: Can not get value rate value for '" + usedISOCode + "' currency.");
      val = 0.0d;
    }
    return val;
  }

  /**
   * gets cross-rate value (by USD)
   *
   * @param fromCurr - currency "From"
   * @param toCurr - currency "To"
   * @return double-typed cross-rate value or 0.0d if currencies not found
   */
  public Double getCrossRate(String fromCurr, String toCurr) {
    Double valFrom2To = 0.0d;
    Double valUSD2From = getVal(fromCurr);
    Double valUSD2To = getVal(toCurr);

    if (0.0d == valUSD2From) {
      ; //stay 0.0d unchanged
    } else
      valFrom2To = valUSD2To / valUSD2From;
    return valFrom2To;
  }

}



