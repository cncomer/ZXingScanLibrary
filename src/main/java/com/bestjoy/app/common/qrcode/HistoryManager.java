/*
 * Copyright (C) 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bestjoy.app.common.qrcode;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.bestjoy.app.common.qrcode.database.ScanHistoryDBHelper;
import com.bestjoy.app.common.qrcode.result.ResultHandler;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Manages functionality related to scan history.</p>
 *
 * @author Sean Owen
 */
public final class HistoryManager {

  private static final String TAG = HistoryManager.class.getSimpleName();

  private static final int MAX_ITEMS = 500;

  private static final String[] COLUMNS = {
	  ScanHistoryDBHelper.TEXT_COL,
      ScanHistoryDBHelper.DISPLAY_COL,
      ScanHistoryDBHelper.FORMAT_COL,
      ScanHistoryDBHelper.TIMESTAMP_COL,
      ScanHistoryDBHelper.DETAILS_COL,
  };

  private static final String[] COUNT_COLUMN = { "COUNT(1)" };

  private static final String[] ID_COL_PROJECTION = { ScanHistoryDBHelper.ID_COL };
  private static final String[] ID_DETAIL_COL_PROJECTION = { ScanHistoryDBHelper.ID_COL, ScanHistoryDBHelper.DETAILS_COL };
  private static final DateFormat EXPORT_DATE_TIME_FORMAT = DateFormat.getDateTimeInstance();

  private final Activity activity;
private Uri historyContentUri;
  public HistoryManager(Activity activity) {
    this.activity = activity;
  }


    private Uri getHistoryContentUri() {
        if (historyContentUri == null) {
            historyContentUri = Uri.parse("content://" + activity.getPackageName()+".provider.ScanHistoryBjnoteProvider");
            historyContentUri = Uri.withAppendedPath(historyContentUri, "scan_history");

        }
        return historyContentUri;
    }

  public boolean hasHistoryItems() {
	  Cursor cursor = activity.getContentResolver().query(getHistoryContentUri(), COUNT_COLUMN, null, null, null);
      if (cursor != null) {
    	  if (cursor.moveToFirst()) {
    		  return cursor.getInt(0) > 0;
    	  }
      }
     return false;
  }

  public List<HistoryItem> buildHistoryItems() {
      List<HistoryItem> items = new ArrayList<HistoryItem>();
      Cursor cursor = activity.getContentResolver().query(getHistoryContentUri(), COLUMNS, null, null, ScanHistoryDBHelper.TIMESTAMP_COL + " DESC");
      if (cursor != null) {
    	  while (cursor.moveToNext()) {
    	        String text = cursor.getString(0);
    	        String display = cursor.getString(1);
    	        String format = cursor.getString(2);
    	        long timestamp = cursor.getLong(3);
    	        String details = cursor.getString(4);
    	        Result result = new Result(text, null, null, BarcodeFormat.valueOf(format), timestamp);
    	        items.add(new HistoryItem(result, display, details));
    	      }
    	  cursor.close();
      }
      
    return items;
  }

  public HistoryItem buildHistoryItem(int number) {
	  Cursor cursor = activity.getContentResolver().query(getHistoryContentUri(), COLUMNS, null, null, ScanHistoryDBHelper.TIMESTAMP_COL + " DESC");
      if (cursor != null) {
    	  cursor.move(number + 1);
          String text = cursor.getString(0);
          String display = cursor.getString(1);
          String format = cursor.getString(2);
          long timestamp = cursor.getLong(3);
          String details = cursor.getString(4);
          Result result = new Result(text, null, null, BarcodeFormat.valueOf(format), timestamp);
          cursor.close();
          return new HistoryItem(result, display, details);
      }
      return null;
  }
  
  public void deleteHistoryItem(int number) {
      Cursor c = activity.getContentResolver().query(getHistoryContentUri(), ID_COL_PROJECTION, null, null, ScanHistoryDBHelper.TIMESTAMP_COL + " DESC");
      c.move(number + 1);
      activity.getContentResolver().delete(getHistoryContentUri(), ScanHistoryDBHelper.ID_COL + '=' + c.getString(0), null);
  }

  public void addHistoryItem(Result result, ResultHandler handler) {
    // Do not save this item to the history if the preference is turned off, or the contents are
    // considered secure.
    if (!activity.getIntent().getBooleanExtra(ScanIntent.Scan.SAVE_HISTORY, true) ) {
      return;
    }

      deletePrevious(result.getText());

    ContentValues values = new ContentValues();
    values.put(ScanHistoryDBHelper.TEXT_COL, result.getText());
    values.put(ScanHistoryDBHelper.FORMAT_COL, result.getBarcodeFormat().toString());
    values.put(ScanHistoryDBHelper.DISPLAY_COL, handler.getDisplayContents().toString());
    values.put(ScanHistoryDBHelper.TIMESTAMP_COL, System.currentTimeMillis());

      // Insert the new entry into the DB.
    activity.getContentResolver().insert(getHistoryContentUri(), values);
  }

  public void addHistoryItemDetails(String itemID, String itemDetails) {
    // As we're going to do an update only we don't need need to worry
    // about the preferences; if the item wasn't saved it won't be udpated
	 Cursor cursor = activity.getContentResolver().query(getHistoryContentUri(),
                    ID_DETAIL_COL_PROJECTION,
                    ScanHistoryDBHelper.TEXT_COL + "=?",
                    new String[] { itemID },
                    ScanHistoryDBHelper.TIMESTAMP_COL + " DESC");
      String oldID = null;
      String oldDetails = null;
      if (cursor.moveToNext()) {
          oldID = cursor.getString(0);
          oldDetails = cursor.getString(1);
      }

      String newDetails = oldDetails == null ? itemDetails : oldDetails + " : " + itemDetails;
      ContentValues values = new ContentValues();
      values.put(ScanHistoryDBHelper.DETAILS_COL, newDetails);

      activity.getContentResolver().update(getHistoryContentUri(), values, ScanHistoryDBHelper.ID_COL + "=?", new String[] { oldID });
  }

  private void deletePrevious(String text) {
      activity.getContentResolver().delete(getHistoryContentUri(), ScanHistoryDBHelper.TEXT_COL + "=?", new String[] { text });
  }

  public void trimHistory() {
	  ContentResolver cr = activity.getContentResolver();
      Cursor cursor = cr.query(getHistoryContentUri(),
                        ID_COL_PROJECTION,
                        null, null,
                        ScanHistoryDBHelper.TIMESTAMP_COL + " DESC");
      if (cursor != null) {
    	  if (cursor.getCount() > MAX_ITEMS) {
    		  cursor.move(MAX_ITEMS);
    		  while (cursor.moveToNext()) {
      	        cr.delete(getHistoryContentUri(), ScanHistoryDBHelper.ID_COL + '=' + cursor.getString(0), null);
      	   }
    	 }
    	  cursor.close();
    	  
      }
     
      
  }

  /**
   * <p>Builds a text representation of the scanning history. Each scan is encoded on one
   * line, terminated by a line break (\r\n). The values in each line are comma-separated,
   * and double-quoted. Double-quotes within values are escaped with a sequence of two
   * double-quotes. The fields output are:</p>
   *
   * <ul>
   *  <li>Raw text</li>
   *  <li>Display text</li>
   *  <li>Format (e.g. QR_CODE)</li>
   *  <li>Timestamp</li>
   *  <li>Formatted version of timestamp</li>
   * </ul>
   */
  CharSequence buildHistory() {
	  StringBuilder historyText = new StringBuilder(1000);
	  ContentResolver cr = activity.getContentResolver();
	  Cursor cursor = cr.query(getHistoryContentUri(),
                        COLUMNS,
                        null, null,
                        ScanHistoryDBHelper.TIMESTAMP_COL + " DESC");

      while (cursor.moveToNext()) {
        historyText.append('"').append(massageHistoryField(cursor.getString(0))).append("\",");
        historyText.append('"').append(massageHistoryField(cursor.getString(1))).append("\",");
        historyText.append('"').append(massageHistoryField(cursor.getString(2))).append("\",");
        historyText.append('"').append(massageHistoryField(cursor.getString(3))).append("\",");

        // Add timestamp again, formatted
        long timestamp = cursor.getLong(3);
        historyText.append('"').append(massageHistoryField(
            EXPORT_DATE_TIME_FORMAT.format(new Date(timestamp)))).append("\",");

        // Above we're preserving the old ordering of columns which had formatted data in position 5

        historyText.append('"').append(massageHistoryField(cursor.getString(4))).append("\"\r\n");
      }
      return historyText;
  }
  
  void clearHistory() {
      activity.getContentResolver().delete(getHistoryContentUri(), null, null);
  }

  static Uri saveHistory(String history) {
    File bsRoot = new File(Environment.getExternalStorageDirectory(), "BarcodeScanner");
    File historyRoot = new File(bsRoot, "History");
    if (!historyRoot.exists() && !historyRoot.mkdirs()) {
      Log.w(TAG, "Couldn't make dir " + historyRoot);
      return null;
    }
    File historyFile = new File(historyRoot, "history-" + System.currentTimeMillis() + ".csv");
    OutputStreamWriter out = null;
    try {
      out = new OutputStreamWriter(new FileOutputStream(historyFile), Charset.forName("UTF-8"));
      out.write(history);
      return Uri.parse("file://" + historyFile.getAbsolutePath());
    } catch (IOException ioe) {
      Log.w(TAG, "Couldn't access file " + historyFile + " due to " + ioe);
      return null;
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException ioe) {
          // do nothing
        }
      }
    }
  }

  private static String massageHistoryField(String value) {
    return value == null ? "" : value.replace("\"","\"\"");
  }
  
  private static void close(Cursor cursor, SQLiteDatabase database) {
    if (cursor != null) {
      cursor.close();
    }
    if (database != null) {
      database.close();
    }
  }

}
