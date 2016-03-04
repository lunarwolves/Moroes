package rui.com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import rui.com.inst.Constant;

/**
 * Only one helper is to create, or db will be locked
 */
public class LocationTimeDBHelper extends SQLiteOpenHelper {
	private static LocationTimeDBHelper HELPER;

	private LocationTimeDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public static LocationTimeDBHelper instance(Context context) {
		if (HELPER == null) {
			HELPER = new LocationTimeDBHelper(context, Constant.DB_NAME, null, 1);
		}

		return HELPER;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + Constant.TABLE_NAME_LOCATION + "(id INTEGER PRIMARY KEY, status INTEGER, changedTime INTEGER)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
