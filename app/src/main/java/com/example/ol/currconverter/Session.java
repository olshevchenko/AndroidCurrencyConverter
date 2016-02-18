package com.example.ol.currconverter;

import android.content.Context;

import com.example.ol.currconverter.currencies.CurrencyRatesHelper;
import com.example.ol.currconverter.db.OperationData;
import com.example.ol.currconverter.operations.OperationsHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by oshevchenk on 14.08.2015.
 */
public class Session {
  private Context context;
  private String userName;
  private OperationData opConvertion; //actual conversion operation
  private CurrencyRatesHelper crHelper; //currencies rates interface
  private OperationsHelper opHelper; //operations lists interface
  private List<String> currenciesList; //list constructed with currencies names


  public Session(Context context, String userName) {
    this.context = context;
    this.userName = userName;
    if (userName.equalsIgnoreCase("user2"))
//ToDo Remove Hardcoded TESTING exception later!!!
//      opConvertion = null;
      ;
    else
      opConvertion = new OperationData();
    opConvertion.setUserName(new String(userName));
    crHelper = new CurrencyRatesHelper();
    crHelper.unserialaizeMe(context);
    opHelper = new OperationsHelper(userName);
    buildCurrenciesList(true); //create new list
  }

  public String getUserName() {
    return userName;
  }

  public OperationData getOpConvertion() {
    return opConvertion;
  }

  public void setOpConvertion(OperationData opConvertion) {
    this.opConvertion = opConvertion;
  }

  public CurrencyRatesHelper getCrHelper() {
    return crHelper;
  }

  public OperationsHelper getOpHelper() {
    return opHelper;
  }

  public List<String> getCurrenciesList() {
    return currenciesList;
  }

  /**
   * convert List of currencies names from operation history list (at the beginning)   *
   * then, add quotes currencies list (to the lists tail)
   * the result list is either created here or used given by param
   * @param toCreateNew sign for list creation or using the existed one
   */
  private void buildCurrenciesList(boolean toCreateNew) {
    Set<String> currsFromLastSession = opHelper.getLastSessionCurrencies();
    currsFromLastSession.addAll(crHelper.getRates().getQuotesCurrencies()); //skip the currencies repeated in BOTH sets
    if (true == toCreateNew)
      currenciesList = new ArrayList<String>(currsFromLastSession);
    else {
      currenciesList.clear();
      currenciesList.addAll(currsFromLastSession);
    }
  }

  public void refreshCurrenciesList() {
    buildCurrenciesList(false); //use existed cleared list
  }


  /**
   * Executes conversion operation during the current session
   * Calculations are based on cross-rates respected to base (USD)
   * After storing, create NEW 'OperationData' instance for further use
   *
   * @return converted amount of "To" currency
   */
  public double onConvert() {
    double rate1 = crHelper.getRates().getCrossRate("USD", opConvertion.getFromCurrency());
    double rate2 = crHelper.getRates().getCrossRate("USD", opConvertion.getToCurrency());
    Double result = opConvertion.getFromAmount() / rate1 * rate2;
    opConvertion.setToAmount(result);
    opConvertion.setOpDate(new Date()); //current operation datetime
    opHelper.onConvert(opConvertion); //store op for future use
    OperationData newopConvertion = opConvertion;
    try {
      newopConvertion = opConvertion.clone();
    } catch (CloneNotSupportedException e) {}
    opConvertion = newopConvertion;
    return result;
  }

  /**
   * for session close & exit
   */
  public void onExit() {
    crHelper.onExit(context); //save session operations into file system
    opHelper.onExit(); //save session operations into DB
  }

}
