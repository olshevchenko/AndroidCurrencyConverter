package com.example.ol.currconverter.db;

import com.example.ol.currconverter.Constants;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by ol on 16.05.15.
 * Model class for reflection operations info into DB, w/annotations, etc..
 */
@DatabaseTable(tableName = Constants.OperationData.TABLE_NAME)
public class OperationData implements Cloneable {

  private Locale defltlocale = Locale.getDefault();

  @DatabaseField(generatedId = true)
  int id;

  // Empty field for ops due anonymous session
  @DatabaseField(index = true, dataType = DataType.STRING, columnName = Constants.OperationData.USER_NAME_FIELD_NAME)
  String userName;

  @DatabaseField(canBeNull = false, dataType = DataType.STRING)
  String fromCurrency;

  @DatabaseField(canBeNull = false, dataType = DataType.STRING)
  String toCurrency;

  @DatabaseField()
  double fromAmount;

  @DatabaseField()
  double toAmount;

  @DatabaseField(canBeNull = false, dataType = DataType.DATE)
  Date opDate;

  @DatabaseField()
  boolean isLastSession;

  public static DecimalFormat getAmountDecimalFormatter() {
    return new DecimalFormat(Constants.OperationData.DECIMAL_FORMAT);
  };

  public OperationData() {
    userName = ""; //anonymous
    fromCurrency = "";
    toCurrency = "";
    fromAmount = 0.0d;
    toAmount = 0.0d;
    opDate = new Date(0L);  //zero date as a just-created sign
    isLastSession = true; //it's NEW operation - so, it's really last-session operation
  }

  //cloning
  public OperationData clone() throws CloneNotSupportedException {
    OperationData cloned = (OperationData) super.clone();
    return cloned;
  }

  public OperationData( String userName, String fromCurrency, String toCurrency,
                        double fromAmount, double toAmount, Date opDate, boolean isLastSession ) {
    this.userName = userName;
    this.fromCurrency = fromCurrency;
    this.toCurrency = toCurrency;
    this.fromAmount = fromAmount;
    this.toAmount = toAmount;
    this.opDate = opDate;
    this.isLastSession = isLastSession;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getFromCurrency() {
    return fromCurrency;
  }

  public String getToCurrency() {
    return toCurrency;
  }

  public void setFromCurrency(String fromCurrency) {
    this.fromCurrency = fromCurrency;
  }

  public void setToCurrency(String toCurrency) {
    this.toCurrency = toCurrency;
  }

  public double getFromAmount() {
    return fromAmount;
  }

  public void setFromAmount(double fromAmount) {
    this.fromAmount = fromAmount;
  }

  public double getToAmount() {
    return toAmount;
  }

  public void setToAmount(double toAmount) {
    this.toAmount = toAmount;
  }

  public Date getOpDate() {
    return opDate;
  }

  public String getOpFormattedDate() {
    Format formatter = new SimpleDateFormat("[yyyy.MM.dd]");
    return formatter.format(opDate);
  }

  public String getOpFormattedTime() {
    Format formatter = new SimpleDateFormat("[HH:mm]");
    return formatter.format(opDate);
  }

  public String getOpFormattedDateTime() {
    Format formatter = new SimpleDateFormat("[yyyy-MM-dd HH:mm]");
    return formatter.format(opDate);
  }

  public void setOpDate(Date opDate) {
    this.opDate = opDate;
  }

  public boolean isLastSession() {
    return isLastSession;
  }

  public void setIsLastSession(boolean isLastSession) {
    this.isLastSession = isLastSession;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    DecimalFormat  decFormatter = this.getAmountDecimalFormatter();

    sb.append(fromCurrency);
    sb.append(decFormatter.format(fromAmount));
    sb.append(" \u00BB\u00BB ");
    sb.append(toCurrency);
    sb.append(decFormatter.format(toAmount));
    SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.OperationData.DATE_FORMAT, defltlocale);
    sb.append(" <").append(dateFormatter.format(opDate)).append(">");
    return sb.toString();
  }
}
