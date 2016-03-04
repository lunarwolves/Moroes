package rui.com.moroes;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import org.joda.time.LocalDateTime;
import rui.com.db.LocationTimeDBHelper;
import rui.com.inst.Constant;
import rui.com.services.CheckInScopeService;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
	private Intent serviceIntent;
	private WorkTimeFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initDB();
		serviceIntent = new Intent(this, CheckInScopeService.class);
		startService(serviceIntent);

		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);

		setSupportActionBar(toolbar);
		toolbar.setOnMenuItemClickListener(onMenuItemClick);
	}

	@Override
	protected void onResume() {
		super.onResume();

		TextView inScopeView = (TextView) findViewById(R.id.text_is_in_scope);
		SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
		int isInScope = sharedPreferences.getInt(Constant.DATA_IN_SCOPE_RIGHT_BEFORE, Constant.IN_SCOPE_RIGHT_BEFORE_UNKNOWN);
		inScopeView.setText("Current position is :" + isInScope);

		TextView lastInScopeView = (TextView) findViewById(R.id.text_last_in_scope_time);
		long lastInScopeTime = sharedPreferences.getLong(Constant.DATA_LAST_IN_SCOPE, -1);
		if (lastInScopeTime != -1) {
			lastInScopeView.setText("Last in is :" + new Date(lastInScopeTime).toString());
		}

		TextView lastOutScopeView = (TextView) findViewById(R.id.text_last_out_scope_time);
		long lastOutScopeTime = sharedPreferences.getLong(Constant.DATA_LAST_OUT_SCOPE, -1);
		if (lastOutScopeTime != -1) {
			lastOutScopeView.setText("Last out is :" + new Date(lastOutScopeTime).toString());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(serviceIntent);
	}

	private void toSettings() {
		Intent intent = new Intent(this, MySettingActivity.class);
		startActivity(intent);
	}

	private void showWorkTime() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		fragment = new WorkTimeFragment();
		transaction.replace(R.id.id_content, fragment);
		transaction.commit();
	}

	public void initDB() {
		LocationTimeDBHelper helper = LocationTimeDBHelper.instance(getApplicationContext());

		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();

		ContentValues values = new ContentValues();
		values.put("status", 0);

		LocalDateTime localDateTime = new LocalDateTime(2016, 3, 2, 7, 0, 0, 0);

		values.put("changedTime", localDateTime.toDateTime().getMillis());
		db.insert(Constant.TABLE_NAME_LOCATION, null, values);

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_actions, menu);
		return true;
	}

	private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem menuItem) {
			switch (menuItem.getItemId()) {
				case R.id.action_favorite:
					toSettings();
					break;
				case R.id.action_work_time:
					showWorkTime();
					break;
				default:
					break;
			}

			return true;
		}
	};
}
