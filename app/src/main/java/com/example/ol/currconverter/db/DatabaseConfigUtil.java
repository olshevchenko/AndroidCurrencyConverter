package com.example.ol.currconverter.db;
/**
 * Created by ol on 17.05.15.
 *
 * for successful compilation - use console (javac + java) in following way:
 * For Win:
 * (being in D:\Work\Android\CurrConverter\app\src\main\java) - do the followings:
 * `javac -cp D:\Work\Android\CurrConverter\app\libs\* com\example\ol\currconverter\Constants.java com\example\ol\currconverter\db\DatabaseConfigUtil.java com\example\ol\currconverter\db\OperationData.java`
 * (then)
 * `mkdir /../res/raw/`
 * `java -cp D:\Work\Android\CurrConverter\app\libs\*;. com.example.ol.currconverter.db.DatabaseConfigUtil`
 *
 * for Linux:
 * (being in app/src/main/java) - do the followings:
 * javac com/example/ol/currconverter/Constants.java com/example/ol/currconverter/db/DatabaseConfigUtil.java com/example/ol/currconverter/db/OperationData.java
 * mkdir ../res/raw
 * java com.example.ol.currconverter.db.DatabaseConfigUtil
 *
 */

import com.example.ol.currconverter.Constants;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
  private static final Class<?>[] classes = new Class[] { OperationData.class };

  public static void main(String[] args) throws SQLException, IOException {
//    writeConfigFile(new File("D:\\Work\\Java\\Android\\CurrencyConverter\\app\\src\\main\\res\\raw\\ormlite_config.txt"), classes);
//  writeConfigFile("ormlite_config.txt", classes);

    /**
     * Gets the project root directory
     */
    String projectRoot = System.getProperty(Constants.DatabaseConfigUtil.CURRENT_DIRECTORY);

    /**
     * Full configuration path includes the project root path, and the location
     * of the ormlite_config.txt file appended to it
     */
    String fullConfigPath = projectRoot + Constants.DatabaseConfigUtil.CONFIG_PATH;

    File configFile = new File(fullConfigPath);

    /**
     * In the a scenario where we run this program serveral times, it will recreate the
     * configuration file each time with the updated configurations.
     */
    if(configFile.exists()) {
      configFile.delete();
      configFile = new File(fullConfigPath);
    }

    /**
     * writeConfigFile is a util method used to write the necessary configurations
     * to the ormlite_config.txt file.
     */
    writeConfigFile(configFile, classes);
    }
}