package com.bestjoy.app.common.qrcode.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bestjoy.library.scan.utils.DebugUtils;
/**
 * @author Sean Owen
 * @author chenkai
 */
public final class ScanHistoryDBHelper extends SQLiteOpenHelper {
private static final String TAG = "ScanHistoryDBHelper";
  private static final int DB_VERSION = 1;//release version 1
  private static final String DB_NAME = "scanhistory.db";

  // Qrcode scan part begin
  public static final String TABLE_SCAN_NAME = "history";
  public static final String ID_COL = "_id";
  public static final String TEXT_COL = "text";
  public static final String FORMAT_COL = "format";
  public static final String DISPLAY_COL = "display";
  public static final String TIMESTAMP_COL = "timestamp";
  public static final String DETAILS_COL = "details";
  // Qrcode scan part end
  

  public ScanHistoryDBHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }
  
  private SQLiteDatabase mWritableDatabase;
  private SQLiteDatabase mReadableDatabase;
  
  public synchronized SQLiteDatabase openWritableDatabase() {
	  if (mWritableDatabase == null) {
		  mWritableDatabase = getWritableDatabase();
	  }
	  return mWritableDatabase;
  }
  
  public synchronized SQLiteDatabase openReadableDatabase() {
	  if (mReadableDatabase == null) {
		  mReadableDatabase = getReadableDatabase();
	  }
	  return mReadableDatabase;
  }
  
  public synchronized void closeReadableDatabase() {
	  if (mReadableDatabase != null && mReadableDatabase.isOpen()) {
		  mReadableDatabase.close();
		  mReadableDatabase = null;
	  }
  }
  
  public synchronized void closeWritableDatabase() {
	  if (mWritableDatabase != null && mWritableDatabase.isOpen()) {
		  mWritableDatabase.close();
		  mWritableDatabase = null;
	  }
  }
  
  public synchronized void closeDatabase() {
	  closeReadableDatabase();
	  closeWritableDatabase();
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
      DebugUtils.logD(TAG, "onCreate");
  		// Create scan history
  		createScanHistory(sqLiteDatabase);
  		
  }

  private void createScanHistory(SQLiteDatabase sqLiteDatabase) {
	  sqLiteDatabase.execSQL(
              "CREATE TABLE " + TABLE_SCAN_NAME + " (" +
                      ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                      TEXT_COL + " TEXT, " +
                      FORMAT_COL + " TEXT, " +
                      DISPLAY_COL + " TEXT, " +
                      TIMESTAMP_COL + " INTEGER, " +
                      DETAILS_COL + " TEXT);");
  }
  

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
	  DebugUtils.logD(TAG, "onUpgrade oldVersion " + oldVersion + " newVersion " + newVersion);
	  if (oldVersion < 1) {
		    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SCAN_NAME);
		    onCreate(sqLiteDatabase);
		    return;
		}

  }
}
