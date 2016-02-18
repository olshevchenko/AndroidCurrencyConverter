package com.example.ol.currconverter.http;
/*
{
  "success":true,
  "terms":"https:\/\/currencylayer.com\/terms",
  "privacy":"https:\/\/currencylayer.com\/privacy",
  "timestamp":1435572555,
  "source":"USD",
  "quotes":{
    "USDRUB":55.584499
  }
}
*/

import java.util.Map;

public class Response {
  private boolean success;
  private String terms;
  private String privacy;
  private long timestamp;
  private String source;
  private Map<String, Double> quotes;

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getTerms() {
    return terms;
  }

  public void setTerms(String terms) {
    this.terms = terms;
  }

  public String getPrivacy() {
    return privacy;
  }

  public void setPrivacy(String privacy) {
    this.privacy = privacy;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setQuotes(Map<String, Double> quotes) {
    this.quotes = quotes;
  }

  public Map<String, Double> getQuotes() {
    return quotes;
  }

  public String toString() {
    return "{ success: " + (success == true? "true" : "false") +
      ", timestamp: " + timestamp + ", source: " + source + ", quotes: " + quotes.toString() + " }";
  }

  private static class Quotes {
    private double quota;

    private Quotes() {
      quota = 0.0;
    }

    private Quotes(double quota) {
      this.quota = quota;
    }

    public double getQuota() {
      return quota;
    }

    public void setQuota(double quota) {
      this.quota = quota;
    }

    public String toString() {
      return "" + quota;
    }

  } //private class Quotes

} //public class Response
