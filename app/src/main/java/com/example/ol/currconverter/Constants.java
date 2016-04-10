package com.example.ol.currconverter;

/**
 * Created by oshevchenk on 08.09.2015.
 */
public final class Constants {
  public class Languages {
    public static final String ENG = "en";
    public static final String RUS = "ru";
  }

  public class SocAppsIDs {
    public static final String VKAppIdPrefix = "vk.com/id";
    public static final String VKTokenKey = "VK_ACCESS_TOKEN";
  }



  public class CurrencyRates {
    //name of the file for currency rates last (before exiting) value storing
    public static final String SER_FNAME = "currconv_cr_ser.out";
    // any time you make changes to your database objects, you may have to increase the database version
    public static final long SERIAL_FILE_VERSION = 1L;
  }

  public class DatabaseConfigUtil {
    public static final String CURRENT_DIRECTORY = "user.dir";
    public static final String CONFIG_PATH = "/../res/raw/ormlite_config.txt";
  }

  public class OperationDataDBHelper {
    // name of the database file for your application -- change to something appropriate for your app
    // DB file location is: /data/data/<APPNAME>/<DATABASE_NAME>
    public static final String DATABASE_NAME = "operations.db";

    // any time you make changes to your database objects, you may have to increase the database version
    public static final int DATABASE_VERSION = 1;
  }

  public class OperationData {
    public static final String TABLE_NAME = "operations";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String DECIMAL_FORMAT = "#,###.##";
    public static final String DECIMAL_FORMAT_NON_FRACTIONAL = "#,###";
  }

  public class Operations {
    public static final int OPS_CAPACITY = 10; //it'll be enough to start
  }

  public class Url {
    public static final String ENDPOINT_URL = "http://apilayer.net/api/";
    public static final String ENDPOINT_ACTION = "live";
    public static final String PARAMS_START = "?";
    public static final String PARAMS_DIVIDER = "&";
    public static final String ACCESS_PARAM = "access_key=";
    public static final String ACCESS_VALUE = "e2c9410aabead14100bc12068ecc394b";
    public static final String FORMAT_PARAM = "format=";
    public static final String FORMAT_VALUE = "1";
    public static final String CURRENCIES_PARAM = "currencies=";
  }
} //class Constants





