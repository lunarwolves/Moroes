package rui.com.moroes;


import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import rui.com.db.LocationTimeDBHelper;
import rui.com.inst.Constant;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkTimeFragment extends Fragment implements Constant {


	public WorkTimeFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		return inflater.inflate(R.layout.fragment_work_time, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		getWorkTimeData();
	}

	private void getWorkTimeData() {
		LocationTimeDBHelper helper = new LocationTimeDBHelper(this.getActivity(), DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();

		Date date11 = new Date(System.currentTimeMillis());


		String sql = "select * from " + TABLE_NAME_LOCATION + " where day(changedTime)=day(Now()) and month(changedTime)=month(Now()) and year(changedTime)=year(Now())";
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()) {
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			String date = cursor.getString(cursor.getColumnIndex("changedTime"));

			Log.i("WorkTime", "Record: status = " + status + " , date = " + date);
		}
	}
}
