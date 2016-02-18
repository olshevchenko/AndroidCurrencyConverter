package com.example.ol.currconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ol on 28.11.15.
 */
public class CurrListAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final int layoutID;
  private final List<String> currs;

  public CurrListAdapter(Context context, int layoutID, List<String> currs) {
    super(context, layoutID, currs);
    this.context = context;
    this.layoutID = layoutID;
    this.currs = currs;
  }

  static class ViewHolder {
    ImageView ivFlag;
    TextView tvCurrency;
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent);
  }

  public View getCustomView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    int imgResID;
    View rowView = convertView;

    if (rowView == null) {
      LayoutInflater inflater = LayoutInflater.from(context);
      rowView = inflater.inflate(layoutID, parent, false);
      holder = new ViewHolder();
      holder.ivFlag = (ImageView) rowView.findViewById(R.id.ivFlag);
      holder.tvCurrency = (TextView) rowView.findViewById(R.id.tvCurrency);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    String curr = currs.get(position);
    holder.tvCurrency.setText(curr);
    imgResID = context.getResources().getIdentifier("_"+curr.toLowerCase(), "drawable", context.getPackageName());
    if (0 == imgResID)
      holder.ivFlag.setImageResource(R.drawable.___); ///unknown currency icon
    else
      holder.ivFlag.setImageResource(imgResID);

    return rowView;
  }
}


