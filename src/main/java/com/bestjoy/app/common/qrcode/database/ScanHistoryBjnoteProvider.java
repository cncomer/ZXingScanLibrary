package com.bestjoy.app.common.qrcode.database;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.bestjoy.library.scan.utils.DebugUtils;

public class ScanHistoryBjnoteProvider extends ContentProvider{
	private static final String TAG = "BjnoteProvider";
	private SQLiteDatabase mContactDatabase;
	private String[] mTables = new String[]{
			ScanHistoryDBHelper.TABLE_SCAN_NAME,
	};
	private static final int BASE = 8;
	
	private static final int SCAN_HISTORY = 0x0000;
	private static final int SCAN_HISTORY_ID = 0x0001;


	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	
	synchronized SQLiteDatabase getDatabase(Context context) {
        // Always return the cached database, if we've got one
        if (mContactDatabase != null) {
            return mContactDatabase;
        }


		ScanHistoryDBHelper helper = new ScanHistoryDBHelper(context);
        mContactDatabase = helper.getWritableDatabase();
        mContactDatabase.setLockingEnabled(true);
        return mContactDatabase;
	}
	
	@Override
	public boolean onCreate() {
		// URI matching table
		UriMatcher matcher = sURIMatcher;
		matcher.addURI(this.getContext().getPackageName()+".provider.ScanHistoryBjnoteProvider", "scan_history", SCAN_HISTORY);
		matcher.addURI(this.getContext().getPackageName()+".provider.ScanHistoryBjnoteProvider", "scan_history/#", SCAN_HISTORY_ID);
		return true;
	}
	
	/**
     * Wrap the UriMatcher call so we can throw a runtime exception if an unknown Uri is passed in
     * @param uri the Uri to match
     * @return the match value
     */
    private int findMatch(Uri uri, String methodName) {
        int match = sURIMatcher.match(uri);
        if (match < 0) {
            throw new IllegalArgumentException("Unknown uri: " + uri);
        } 
        DebugUtils.logD(TAG, methodName + ": uri=" + uri + ", match is " + match);
        return match;
    }
    
    private void notifyChange(int match) {
    	Context context = getContext();
    	Uri notify = Uri.parse("content://" + this.getContext().getPackageName() + ".provider.ScanHistoryBjnoteContent");
    	switch(match) {
		case SCAN_HISTORY:
		case SCAN_HISTORY_ID:
			notify = Uri.withAppendedPath(notify, "scan_history");
			break;
    	}
    	ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(notify, null);
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = findMatch(uri, "delete");
        Context context = getContext();

        // See the comment at delete(), above
        SQLiteDatabase db = getDatabase(context);
        String table = mTables[match>>BASE];
        DebugUtils.logProvider(TAG, "delete data from table " + table);
        int count = 0;
        switch(match) {
        default:
        	count = db.delete(table, buildSelection(match, uri, selection), selectionArgs);
        }
        if (count >0) notifyChange(match);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		 int match = findMatch(uri, "insert");
         Context context = getContext();

         // See the comment at delete(), above
         SQLiteDatabase db = getDatabase(context);
         String table = mTables[match>>BASE];
         DebugUtils.logProvider(TAG, "insert values into table " + table);
         //Insert 操作不允许设置_id字段，如果有的话，我们需要移除
         if (values.containsKey(ScanHistoryDBHelper.ID_COL)) {
      		values.remove(ScanHistoryDBHelper.ID_COL);
      	 }
     	 long id = db.insert(table, null, values);
     	 if (id > 0) {
     		notifyChange(match);
   		    return ContentUris.withAppendedId(uri, id);
     	 }
		 return null;
	}
	private static final String[] COLUMN_NAME = { MediaStore.Images.ImageColumns.DATA};
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		 int match = findMatch(uri, "query");
         Context context = getContext();
         // See the comment at delete(), above
         SQLiteDatabase db = getDatabase(context);
         int i =match>>BASE;
         String table = mTables[i];
         DebugUtils.logProvider(TAG, "query table " + table);
         Cursor result = null;
         switch(match) {
         default:
        	     result = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
         }
         if(result != null)result.setNotificationUri(getContext().getContentResolver(), uri);
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int match = findMatch(uri, "update");
        Context context = getContext();
        
        SQLiteDatabase db = getDatabase(context);
        String table = mTables[match>>BASE];
        DebugUtils.logProvider(TAG, "update data for table " + table);
        int count = 0;
        switch(match) {
        default:
        	    count = db.update(table, values, buildSelection(match, uri, selection), selectionArgs);
        }
        if (count >0) notifyChange(match);
		return count;
	}
	
	private String buildSelection(int match, Uri uri, String selection) {
		long id = -1;
		switch(match) {
			case SCAN_HISTORY_ID:
			try {
				id = ContentUris.parseId(uri);
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
			break;
		}
		
		if (id == -1) {
			return selection;
		}
		DebugUtils.logProvider(TAG, "find id from Uri#" + id);
		StringBuilder sb = new StringBuilder();
		sb.append(ScanHistoryDBHelper.ID_COL);
		sb.append("=").append(id);
		if (!TextUtils.isEmpty(selection)) {
			sb.append(" and ");
			sb.append(selection);
		}
		DebugUtils.logProvider(TAG, "rebuild selection#" + sb.toString());
		return sb.toString();
	}

}
