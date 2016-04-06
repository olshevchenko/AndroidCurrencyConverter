package com.example.ol.currconverter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.ol.currconverter.db.OperationData;

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Created by ol on 04.04.16.
 */

/**
 * hide/show btConvert Button if entered any digits into etAmountFrom
 * format entering amountFrom number value
 */
public class NumberTextWatcher implements TextWatcher {

  //for logging
  private static final String LOG_TAG = ConverterFragment.class.getName();

  private DecimalFormat df; /// w. fraction part
  private DecimalFormat dfnf; /// w/o. one
  private boolean hasFractional; /// sign for fraction
  private EditText etAmountFrom;
  private Button btConvert;
  private Session mySession;
  private double amountFrom = 0.0d;
  private String srcString;

  /**
   * 4 cursor positioning in formatted text field
   */
  private int oldlen;
  private int newlen;
  private int oldpos;
  private int newpos;

  public NumberTextWatcher (EditText etAmountFrom, Button btConvert, Session mySession) {
    df = OperationData.getAmountDecimalFormatter();
    df.setDecimalSeparatorAlwaysShown(true);
    dfnf = OperationData.getAmountDecimalFormatterNF();
    this.etAmountFrom = etAmountFrom;
    this.btConvert = btConvert;
    this.mySession = mySession;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

  @Override
  public void onTextChanged (CharSequence s, int start, int before, int count) {
    updateConvertButton(); /// might convert now
    if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())))
      hasFractional = true;
    else
      hasFractional = false;
  }

  @Override
  public void afterTextChanged(Editable s) {
    /**
     * original number length & cursor position
     */
    int oldlen = etAmountFrom.getText().length();
    if (0 == oldlen)
      /// nothing to parse & format & show
      return;
    int oldpos = etAmountFrom.getSelectionStart();

    srcString = s.toString().replaceAll(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");

    etAmountFrom.removeTextChangedListener(this);
    try {
//      amountFrom = Double.parseDouble(srcString);
      amountFrom = df.parse(srcString).doubleValue();
      Log.d(LOG_TAG, "double amountFrom=" + amountFrom);
      mySession.getOpConvertion().setFromAmount(amountFrom);
      if (hasFractional)
        /// must show decimal separator ('.')
        etAmountFrom.setText(df.format(amountFrom));
      else
        etAmountFrom.setText(dfnf.format(amountFrom));

      /**
       * new, after-formatting number length & cursor position
       */
      int newlen = etAmountFrom.getText().length();
      int newpos = (oldpos + (newlen - oldlen));
      if (newpos > 0 && newpos <= newlen) {
        etAmountFrom.setSelection(newpos);
      } else {
        /// place cursor at the end
        etAmountFrom.setSelection(newlen - 1);
      }
    }
    catch (NumberFormatException ex) {
      Log.w(LOG_TAG, "Can't parse number from etAmountFrom", ex);
      ; ///just ignoring symbol NEWLY added
    }
    catch (ParseException ex) {
      Log.w(LOG_TAG, "Can't parse number from etAmountFrom", ex);
      ; ///just ignoring symbol NEWLY added
    }
    etAmountFrom.addTextChangedListener(this);
  }

  private void updateConvertButton() {
  // check if there is input in both EditTexts
    if (etAmountFrom.getText().toString().isEmpty())
      btConvert.setEnabled(false);
    else
      btConvert.setEnabled(true);
  }
}
