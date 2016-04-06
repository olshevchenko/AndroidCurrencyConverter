package com.example.ol.currconverter;
/**
 * Created by oshevchenk on 25.06.2015.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ol.currconverter.db.OperationData;

import java.util.Date;
import java.util.List;

import retrofit.RetrofitError;


public class ConverterFragment extends Fragment {

  //for logging
  private static final String LOG_TAG = ConverterFragment.class.getName();

  //for interaction w. Activity & Session
  private MainActivity myActivity;
  private Session mySession;
  private CurrListAdapter currListAdapter;

  public static class NetworkFailedDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.dlgNetworkFailedTitle)
        .setMessage(R.string.dlgNetworkFailedMessage)
        .setIcon(R.drawable.ic_sync_problem_white_36dp)
        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });
      return builder.create();
    }
  }

  //definition class 4 async task ('GetCurrRatesTask') params
  class Params4CRTask {
    private TextView tvRatesRefreshDate;
    private Session session;
    private CurrListAdapter currListAdapter;

    public Params4CRTask(TextView tv, Session ses, CurrListAdapter adapter) {
      tvRatesRefreshDate = tv;
      session = ses;
      currListAdapter = adapter;
    }

    public TextView getTvRatesRefreshDate() {
      return tvRatesRefreshDate;
    }

    public Session getSession() {
      return session;
    }

    public CurrListAdapter getCurrListAdapter() {
      return currListAdapter;
    }
  }

  //asynchronously get currencies rates here
  class GetCurrRatesTask extends AsyncTask<Params4CRTask, Void, Params4CRTask> {

    ProgressDialog pleaseWaitDialog;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pleaseWaitDialog = ProgressDialog.show(getActivity(), "",
        getString(R.string.dlgRefreshingExchangeRates), false, false);
    }

    @Override
    protected Params4CRTask doInBackground(Params4CRTask... params) {
      Params4CRTask result = params[0];
      try {
        params[0].getSession().getCrHelper().refreshRates();
      } catch (RetrofitError e) {
        result = null; //ignore exchange rates from the response
      }
      return result;
    }

    @Override
    protected void onPostExecute(Params4CRTask result) {
      super.onPostExecute(result);
      pleaseWaitDialog.dismiss();
      if (null != result) {
        Date refreshQuotesDate = result.getSession().getCrHelper().getRates().getQuotesDate();
        result.getTvRatesRefreshDate().setText(getString(R.string.tvRatesRefreshDate) +
            TimeHelper.datesDiff2Str(refreshQuotesDate, getActivity()));
        result.getSession().refreshCurrenciesList();
        result.getCurrListAdapter().notifyDataSetChanged();
      }
      else {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        NetworkFailedDialogFragment nfDialogFragment = new NetworkFailedDialogFragment();
        nfDialogFragment.show(fm, "dialog");
      }
    }
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    myActivity = (MainActivity) getActivity();
    mySession = myActivity.getUserSession();

    // currencies list adapter
    List<String> currList = mySession.getCurrenciesList();
    currListAdapter = new CurrListAdapter(myActivity, R.layout.currencies_item, currList);

//    Toast.makeText(getActivity(), LOG_TAG + "onCreate()", Toast.LENGTH_SHORT).show();
//    Log.i(LOG_TAG, "onCreate()");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_converter, container, false);

//    Toast.makeText(myActivity, LOG_TAG + "onCreateView()", Toast.LENGTH_SHORT).show();
//    Log.i(LOG_TAG, "onCreateView()");

  //-------------------
  //Currencies UI block
    final Spinner spCurrencyFrom = (Spinner) rootView.findViewById(R.id.spCurrencyFrom);
    spCurrencyFrom.setAdapter(currListAdapter);

    spCurrencyFrom.setSelection(0);
    spCurrencyFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view,
                                 int position, long id) {
        String currFrom = new String(mySession.getCurrenciesList().get(position));
        mySession.getOpConvertion().setFromCurrency(currFrom);
      }
      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });

    final Spinner spCurrencyTo = (Spinner) rootView.findViewById(R.id.spCurrencyTo);
    spCurrencyTo.setAdapter(currListAdapter);

    spCurrencyTo.setSelection(0);
    spCurrencyTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view,
                                 int position, long id) {
        String currTo = new String(mySession.getCurrenciesList().get(position));
        mySession.getOpConvertion().setToCurrency(currTo);
      }
      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });

    /// Currencies swap
    Button btInvert = (Button) rootView.findViewById(R.id.btInvert);
    btInvert.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
//        Log.d(LOG_TAG, "Swapping currencies...");
        int posFrom = spCurrencyFrom.getSelectedItemPosition();
        int posTo = spCurrencyTo.getSelectedItemPosition();
        spCurrencyFrom.setSelection(posTo);
        spCurrencyTo.setSelection(posFrom);
      }
    });

  //----------------
  //Amounts UI block
    final EditText etAmountFrom = (EditText) rootView.findViewById(R.id.etAmountFrom);
    final EditText etAmountTo = (EditText) rootView.findViewById(R.id.etAmountTo);

    /// Currencies amounts conversion
    Button btConvert = (Button) rootView.findViewById(R.id.btConvert);
    btConvert.setEnabled(false); /// hide it at 1'st
    btConvert.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (TextUtils.isEmpty(etAmountFrom.getText().toString())) {
          return;
        }
//        Log.d(LOG_TAG, "Converting currencies...");
        double amountTo = mySession.onConvert();
        etAmountTo.setText(OperationData.getAmountDecimalFormatter().format(amountTo));
      }
    });

    /// textWatcher to hide/show btConvert + to format etAmountFrom
    NumberTextWatcher textWatcher = new NumberTextWatcher(etAmountFrom, btConvert, mySession);
    etAmountFrom.addTextChangedListener(textWatcher);

  //-----------------------------
  //"Rates refresh Date" UI block
    final TextView tvRatesRefreshDate = (TextView) rootView.findViewById(R.id.tvRatesRefreshDate);
    Date refreshQuotesDate = mySession.getCrHelper().getRates().getQuotesDate();
    tvRatesRefreshDate.setText(getString(R.string.tvRatesRefreshDate) +
        TimeHelper.datesDiff2Str(refreshQuotesDate, getActivity()));

    final Params4CRTask params4CRT = new Params4CRTask(tvRatesRefreshDate, mySession, currListAdapter);

    Button btRefresh = (Button) rootView.findViewById(R.id.btRefresh);
    btRefresh.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Log.d(LOG_TAG, "Refreshing currency rates...");
        new GetCurrRatesTask().execute(params4CRT);
      }
    });

    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

//    Toast.makeText(getActivity(), LOG_TAG + "onActivityCreated()", Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "onActivityCreated()");
  }

  @Override
  public void onStart() {
    super.onStart();

//    Toast.makeText(getActivity(), LOG_TAG + "onStart()", Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "onStart()");
  }

  @Override
  public void onResume() {
    super.onResume();

//    Toast.makeText(getActivity(), LOG_TAG + "onResume()", Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "onResume()");
  }

  @Override
  public void onPause() {

//    Toast.makeText(getActivity(), LOG_TAG + "onPause()", Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "onPause()");
    super.onPause();
  }

  @Override
  public void onStop() {

//    Toast.makeText(getActivity(), LOG_TAG + "onStop()", Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "onStop()");
    super.onStop();
  }

  @Override
  public void onDestroyView() {

//    Toast.makeText(getActivity(), LOG_TAG + "onDestroyView()", Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "onDestroyView()");
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {

//    Toast.makeText(getActivity(), LOG_TAG + "onDestroy()", Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "onDestroy()");
    super.onDestroy();
  }

  @Override
  public void onDetach() {

//    Toast.makeText(getActivity(), LOG_TAG + "onDetach()", Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "onDetach()");
    super.onDetach();
  }
}
