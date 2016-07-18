package com.example.ol.currconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ol.currconverter.db.OperationData;

import java.util.List;

/**
 * Created by ol on 28.11.15.
 */
public class OpListAdapter extends ArrayAdapter<OperationData> {
  private final Context context;
  private final List<OperationData> ops;

  public OpListAdapter(Context context, List<OperationData> ops) {
    super(context, R.layout.operations_item, ops);
    this.context = context;
    this.ops = ops;
  }

  static class ViewHolder{
    TextView tvCurrencyFrom;
    ImageView ivFlagFrom;
    TextView tvAmountFrom;
    TextView tvCurrencyTo;
    ImageView ivFlagTo;
    TextView tvAmountTo;
    TextView tvDateTime;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    ViewHolder holder;
    int imgResID;
    View rowView = convertView;

    if (rowView == null) {
      LayoutInflater inflater = LayoutInflater.from(context);
      rowView = inflater.inflate(R.layout.operations_item, parent, false);
      holder = new ViewHolder();
      holder.tvCurrencyFrom = (TextView) rowView.findViewById(R.id.tvCurrencyFrom);
      holder.ivFlagFrom = (ImageView) rowView.findViewById(R.id.ivFlagFrom);
      holder.tvAmountFrom = (TextView) rowView.findViewById(R.id.tvAmountFrom);
      holder.tvCurrencyTo = (TextView) rowView.findViewById(R.id.tvCurrencyTo);
      holder.ivFlagTo = (ImageView) rowView.findViewById(R.id.ivFlagTo);
      holder.tvAmountTo = (TextView) rowView.findViewById(R.id.tvAmountTo);
      holder.tvDateTime = (TextView) rowView.findViewById(R.id.tvDateTime);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    OperationData op = ops.get(position);

    String currFrom = op.getFromCurrency();
    holder.tvCurrencyFrom.setText(currFrom);
    imgResID = context.getResources().getIdentifier("_"+currFrom.toLowerCase(), "drawable", context.getPackageName());
    if (0 == imgResID)
      holder.ivFlagFrom.setImageResource(R.drawable.___); ///unknown currency icon
    else
      holder.ivFlagFrom.setImageResource(imgResID);
    holder.tvAmountFrom.setText(OperationData.getAmountDecimalFormatter().format(op.getFromAmount()));

    String currTo = op.getToCurrency();
    holder.tvCurrencyTo.setText(currTo);
    imgResID = context.getResources().getIdentifier("_"+currTo.toLowerCase(), "drawable", context.getPackageName());
    if (0 == imgResID)
      holder.ivFlagFrom.setImageResource(R.drawable.___); ///unknown currency icon
    else
      holder.ivFlagTo.setImageResource(imgResID);
    holder.tvAmountTo.setText(OperationData.getAmountDecimalFormatter().format(op.getToAmount()));
    holder.tvDateTime.setText(op.getOpFormattedDateTime());

    return rowView;
  }

  public View getViewByPosition(int pos, ListView listView) {
    final int firstListItemPosition = listView.getFirstVisiblePosition();
    final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

    if (pos < firstListItemPosition || pos > lastListItemPosition ) {
      return getView(pos, null, listView);
    } else {
      final int childIndex = pos - firstListItemPosition;
      return listView.getChildAt(childIndex);
    }
  }
}


