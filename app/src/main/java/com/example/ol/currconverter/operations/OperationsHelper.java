package com.example.ol.currconverter.operations;

import android.util.Log;

import com.example.ol.currconverter.Constants;
import com.example.ol.currconverter.db.DBHelper;
import com.example.ol.currconverter.db.OperationData;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by oshevchenk on 13.08.2015.
 */
public class OperationsHelper {
  private static final String LOG_TAG = OperationsHelper.class.getName();
  private Operations opsCurrentSession; //list for saving all CURRENT session operations
  private Operations opsDBUser; //list of (initially full but later still remaining) DB operation records for the user
  private Operations opsDeleted; //list of deleted operation records for the user
  private Dao<OperationData, Integer> operationDataDao = null; //DB DAO interface for OperationData

  static class Operations {
    private static final String LOG_TAG = Operations.class.getName();

    private List<OperationData> opsList = new ArrayList<OperationData>(Constants.Operations.OPS_CAPACITY);

    public void addItem(OperationData op) {
      try {
        opsList.add(op);
      } catch (Exception e) {
        Log.w(LOG_TAG, "While adding operation to the operations list:" + e);
      } //just skip this element
      return;
    }

    public void deleteItem(int position) {
      try {
        opsList.remove(position);
      } catch (Exception e) {
        Log.w(LOG_TAG, "While removing operation from the operations list:" + e);
      } //just skip this element
      return;
    }

    public List<OperationData> getList() {
      return opsList;
    }

    public void clearList() {
      opsList.clear();
    }

    public void setList(List<OperationData> opsList) { this.opsList = opsList; }
  }


  public OperationsHelper(String userName) {
    opsCurrentSession = new Operations();

    //get DAO for operation with OperationData objects (records)
    try {
      operationDataDao = DBHelper.getHelper().getOperationDataDao();
    } catch (RuntimeException e) {} //ignoring DB failure
    //got DAO pointer or null if failed

    opsDBUser = selectDBOperationsByUserName(userName); //at start, we have FULL user's operations list

    opsDeleted = new Operations();
  }

  /**
   * gets all operation records for the user specified
   *
   * @return list of operations
   */
  public List<OperationData> getUserOperationsHistory() {
    return opsDBUser.getList();
  }

  /**
   * gets currencies from the last session operations
   *
   * @return lastSessionOps - set of currencies ISO codes
   */
  public Set<String> getLastSessionCurrencies() {

    Set<String> currencies = new LinkedHashSet<String>();

    for (OperationData op: opsDBUser.getList()) {
      if (op.isLastSession() == true) {
        //add currencies from the LAST session operations only
        currencies.add(op.getFromCurrency());
        currencies.add(op.getToCurrency());
      }
    }
    Log.i(LOG_TAG, "Prepared[" + currencies.size() + "] LAST SESSION operations currencies: " + currencies);
    return currencies;
  }

  /**
   * internal method for getting operation DB records by <username> field
   * gets list and save it into created 'Operations' object
   *
   * @param userName
   * @return Operations instance
   */
  private Operations selectDBOperationsByUserName(String userName) {
    List<OperationData> opsList;

    Operations ops = new Operations();
    // query for data objects in the database
    try {
      if (null != operationDataDao)
        opsList = operationDataDao.queryForEq(Constants.OperationData.USER_NAME_FIELD_NAME, userName);
      else
        opsList = new ArrayList<OperationData>(0); //ok, let it be EMPTY list
    } catch (SQLException e) {
      opsList = new ArrayList<OperationData>(0); //anyway!
    }

    Log.i(LOG_TAG, "Found [" + opsList.size() + "] entries in DB by user: " + userName);
    ops.setList(opsList);
    return ops;
  }

  /**
   * Create new OperationData record based on successful currency exchange
   * add it to the session operations list
   */
  public void onConvert(OperationData op) {
    opsCurrentSession.addItem(op);
  }


  /**
   * Delete [position] operation record from user's all operations DB list
   * also either adds it into deleted operations list or removes it directly from DB right here
   *
   * @param position record's position in the corresponding user's ops list
   * @param isDBDeleteNow delete the DB record right now or delay to the session end
   */
  public void onDelete(int position, boolean isDBDeleteNow) {

    if ((position < 0) || (position >= opsDBUser.getList().size())) {
      Log.w(LOG_TAG, "Incorrect DB item number[" + position + "] for remove => ignore it");
      return; //just skip removing
    }

    OperationData item = opsDBUser.getList().get(position);
    if (true == isDBDeleteNow) {
      // delete data objects in the database
      try {
        if (null != operationDataDao)
          operationDataDao.delete(item); //delete corresponding record from DB - RIGHT NOW
        else
          ; //just ignore deletion
      } catch (SQLException e) {
        Log.w(LOG_TAG, "Failed to remove operation DB item[" + position + "]");
      }
    } else
      opsDeleted.addItem(item); //move deleting item for removing from DB - FURTHER

    //remove item from the list
    opsDBUser.deleteItem(position);
    return;
  }

  /**
   * FULL Clear user's all operations DB list
   * Always executes DELAYED (until session end) DB deletion
   */
  public void onClear() {

    for (OperationData item : opsDBUser.getList()) {
      //realize delayed removing
      opsDeleted.addItem(item); //mark item for deletion
    }
    opsDBUser.clearList();
  }

  /**
   * execute all preparation operations with lists before finish
   * delete 'deleted operations' list records from DB (if delayed removing has been used)
   * erase 'last-session' sign in existed DB records
   * save all new 'last-session' records into DB from 'current session operations' list
   */
  public void onExit() {

    //execute delayed DB records remove (from deleted operations list if any)
    for (OperationData item : opsDeleted.getList()) {
      try {
        if (null != operationDataDao)
          operationDataDao.delete(item); //delete corresponding record from DB
        else
          ; //ignore deletion
      } catch (SQLException e) {
        Log.w(LOG_TAG, "Failed to remove operation DB item: " + item);
      }
    }
    opsDeleted.clearList();

    //disable 'isLastSession' sign from remaining user's operation DB records
    for (OperationData item : opsDBUser.getList()) {
      if (item.isLastSession() == false)
        continue;
      //will update 'last-session' records only
      item.setIsLastSession(false);
      try {
        if (null != operationDataDao)
          operationDataDao.update(item); //update corresponding DB records
        else
          ; //ignore update
      } catch (SQLException e) {
        Log.w(LOG_TAG, "Failed to update operation DB item: " + item);
      }
    }
    opsDBUser.clearList();

    //store all current session operations into DB
    for (OperationData item : opsCurrentSession.getList()) {
      try {
        if (null != operationDataDao)
          operationDataDao.create(item); //add new operation DB record
        else
          ; //ignore adding
      } catch (SQLException e) {
        Log.w(LOG_TAG, "Failed to add new operation DB item: " + item);
      }
    }
    opsCurrentSession.clearList();
  }

}
