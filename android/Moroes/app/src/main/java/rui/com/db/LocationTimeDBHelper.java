package rui.com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import rui.com.inst.Constant;

/**
 *
 */
public class LocationTimeDBHelper extends SQLiteOpenHelper {

	public LocationTimeDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + Constant.TABLE_NAME_LOCATION + "(id INTEGER PRIMARY KEY, status INTEGER, changedTime DATETIME DEFAULT " +
				"CURRENT_TIMESTAMP)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
