package com.example.ol.currconverter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ol.currconverter.Constants;
import com.example.ol.currconverter.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


/**
 * Created by ol on 16.05.15.
 */
public class OperationDataDBHelper extends OrmLiteSqliteOpenHelper {

  //for logging
  private static final String LOG_TAG = OperationDataDBHelper.class.getName();

  // the DAO object we use to access the OperationData table
  private static Dao<OperationData, Integer> operationDataDao; //singleton DAO for OperationData class

  public OperationDataDBHelper(Context context) {
    super(context, Constants.OperationDataDBHelper.DATABASE_NAME, null,
          Constants.OperationDataDBHelper.DATABASE_VERSION, R.raw.ormlite_config);
  }

  /**
   * @return real DAO for OperationData class or null if DAO init was failed
   */
  public static Dao<OperationData, Integer> getOperationDataDao() {
    return operationDataDao;
  }

  /**
   * Creates the singleton Database Access Object (DAO) instance for OperationData class, if it's possible.
   * Otherwise, remains null pointer.
   */
  public void initOperationDataDao() {
    if (null == operationDataDao) {
      try {
        operationDataDao = getDao(OperationData.class);
      } catch (SQLException e) {
        Log.e(LOG_TAG, "Can't get OperationData DAO", e);
        operationDataDao = null; //stay unassigned
      }
    }
  }

  /**
   * This is called when the database is first created. Usually you should call createTable statements here to create
   * the tables that will store your data.
   */
  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)  throws RuntimeException {
    try {
      Log.i(LOG_TAG, "onCreate");
      TableUtils.createTable(connectionSource, OperationData.class);
    } catch (SQLException e) {
      Log.e(LOG_TAG, "Can't create database " + Constants.OperationDataDBHelper.DATABASE_NAME, e);
      throw new RuntimeException(e);
    }
  }

  /**
   * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
   * the various data to match the new version number.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    try {
//      Log.i(LOG_TAG, "onUpgrade");
      TableUtils.dropTable(connectionSource, OperationData.class, true);
      // after we drop the old databases, we create the new ones
      onCreate(db, connectionSource);
    } catch (SQLException e) {
      Log.e(LOG_TAG, "Can't drop databases", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Close the database connections and clear any cached DAOs.
   */
  @Override
  public void close() {
    super.close();
    operationDataDao = null;
  }
}

