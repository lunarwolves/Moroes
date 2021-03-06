package rui.com.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import org.joda.time.Instant;
import rui.com.db.LocationTimeDBHelper;
import rui.com.inst.Constant;
import rui.com.location.LocationService;
import rui.com.moroes.MoroesApplication;

/**
 * 收集位置信息，记录到SharePerReference和SQLite中
 */
public class CheckInScopeService extends Service implements Constant {
	private LocationService locationService;

	private int isInScope;
	private long lastInScopeTime;
	private long lastOutScopeTime;

	public CheckInScopeService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences sharedPreferences = this.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
		isInScope = sharedPreferences.getInt(DATA_IN_SCOPE_RIGHT_BEFORE, IN_SCOPE_RIGHT_BEFORE_UNKNOWN);
		lastInScopeTime = sharedPreferences.getLong(DATA_LAST_IN_SCOPE, -1);
		lastOutScopeTime = sharedPreferences.getLong(DATA_LAST_OUT_SCOPE, -1);

		locationService = ((MoroesApplication) getApplication()).locationService;
		LocationClientOption mOption = locationService.getDefaultLocationClientOption();
		mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		mOption.setCoorType("bd09ll");
		locationService.setLocationOption(mOption);
		locationService.registerListener(mListener);
		locationService.start();
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		locationService.stop();
		//保存定位信息到SharedPreferences
		recordScopeInfo();
	}

	private BDLocationListener mListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {

			Log.i("CheckInScopeService", "Receive location data.");

			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				float[] distance = new float[1];
				Location.distanceBetween(SCOPE_LATITUDE, SCOPE_LONGTITUDE, location.getLatitude(), location.getLongitude(),
						distance);
				//进入了目标范围
				if (distance[0] <= DEFAULT_DETECT_DISTANCE) {
					if (isInScope != IN_SCOPE_RIGHT_BEFORE_YES) {
						lastInScopeTime = System.currentTimeMillis();

						if (isInScope == IN_SCOPE_RIGHT_BEFORE_NO) {
							//刚刚移动到范围内
							recordMoveInScope();
						}

						isInScope = IN_SCOPE_RIGHT_BEFORE_YES;
					}

				} else {
					if (isInScope != IN_SCOPE_RIGHT_BEFORE_NO) {

						lastOutScopeTime = System.currentTimeMillis();

						if (isInScope == IN_SCOPE_RIGHT_BEFORE_YES) {
							//刚刚移动出范围
							recordMoveOutScope();
						}

						isInScope = IN_SCOPE_RIGHT_BEFORE_NO;
					}
				}

			}
		}

	};

	private void recordMoveInScope() {
		writeToSQLite(IN_SCOPE_RIGHT_BEFORE_YES);
	}

	private void recordMoveOutScope() {
		writeToSQLite(IN_SCOPE_RIGHT_BEFORE_NO);
	}

	private void writeToSQLite(int status) {
		LocationTimeDBHelper helper = LocationTimeDBHelper.instance(getApplicationContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();

		ContentValues values = new ContentValues();
		values.put("status", status);
		values.put("changedTime", Instant.now().getMillis());
		db.insert(TABLE_NAME_LOCATION, null, values);

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();

		((MoroesApplication) getApplication()).mVibrator.vibrate(1000L);
	}

	private void recordScopeInfo() {
		SharedPreferences sharedPreferences = this.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putInt(DATA_IN_SCOPE_RIGHT_BEFORE, isInScope);
		editor.putLong(DATA_LAST_IN_SCOPE, lastInScopeTime);
		editor.putLong(DATA_LAST_OUT_SCOPE, lastOutScopeTime);

		editor.apply();
	}
}
