package rui.com.moroes;


import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import rui.com.db.LocationTimeDBHelper;
import rui.com.inst.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkTimeFragment extends Fragment implements Constant {
	private String startWorkTime;
	private String finishWorkTime;
	private String workTimeToday;

	private TextView startTextView;
	private TextView finishTextView;
	private TextView durableTextView;

	public WorkTimeFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment


		View view =  inflater.inflate(R.layout.fragment_work_time, container, false);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		startTextView = (TextView)getView().findViewById(R.id.show_start_work_time);
		finishTextView = (TextView)getView().findViewById(R.id.show_stop_work_time);
		durableTextView = (TextView)getView().findViewById(R.id.show_work_time_today);

		getWorkTimeData();
	}

	private void getWorkTimeData() {
		LocationTimeDBHelper helper = LocationTimeDBHelper.instance(getActivity());
		SQLiteDatabase db = helper.getReadableDatabase();

		LocalDate localDate = LocalDate.now();
		long startOfDay = localDate.toDateTimeAtStartOfDay().getMillis();
		long endOfDay = localDate.toDateTimeAtStartOfDay().plusDays(1).getMillis();

		String sql = "select * from " + TABLE_NAME_LOCATION + " where changedTime<" + endOfDay + " and changedTime>" + startOfDay + " order by changedTime";

		Cursor cursor = db.rawQuery(sql, null);
		List<MoveRecord> allTimes = new ArrayList<>();
		while (cursor.moveToNext()) {
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			long date = cursor.getLong(cursor.getColumnIndex("changedTime"));

			allTimes.add(new MoveRecord(status, date));
			Log.i("WorkTime", "Record: status = " + status + " , date = " + date);
		}

		cursor.close();

		if (allTimes.isEmpty()) return;

		MoveRecord firstTime = allTimes.get(0);
		LocalTime startLocalTime;
		if (firstTime.status == IN_SCOPE_RIGHT_BEFORE_YES) {
			startLocalTime = LocalTime.fromDateFields(new Date(firstTime.changedTime));
			startWorkTime = startLocalTime.toString("HH时mm分");
		} else {
			//无上班时间记录，默认按工时上班时间计算
			return;
		}

		int workTimeHour = 0;
		int workTimeMinute = 0;
		//只有上班时间记录
		if (allTimes.size() == 1) {
			//工时计算按当前时间，超过13点时工时减去午饭时间1小时
			LocalTime localTime = LocalTime.now();
			workTimeHour = Hours.hoursBetween(startLocalTime, localTime).getHours();
			workTimeMinute = Minutes.minutesBetween(startLocalTime, localTime).getMinutes();
			if (localTime.getHourOfDay() >= 13) {
				workTimeHour = workTimeHour - 1;
			}
		} else {
			MoveRecord lastTime = allTimes.get(allTimes.size() - 1);
			//有下班工时
			if (lastTime.status == IN_SCOPE_RIGHT_BEFORE_NO) {
				LocalTime endLocalTime = LocalTime.fromDateFields(new Date(lastTime.changedTime));
				workTimeHour = Hours.hoursBetween(startLocalTime, endLocalTime).getHours();
				workTimeMinute = Minutes.minutesBetween(startLocalTime, endLocalTime).getMinutes();
				if (endLocalTime.getHourOfDay() >= 13) {
					workTimeHour = workTimeHour - 1;
				}

				finishWorkTime = endLocalTime.toString("HH时mm分");
			} else {
				//最后一条记录为上班状态，按当前时间计算
				LocalTime localTime = LocalTime.now();
				workTimeHour = Hours.hoursBetween(startLocalTime, localTime).getHours();
				workTimeMinute = Minutes.minutesBetween(startLocalTime, localTime).getMinutes() % 60;
				if (localTime.getHourOfDay() >= 13) {
					workTimeHour = workTimeHour - 1;
				}
			}
		}

		workTimeToday = String.format("%d小时%d分钟", workTimeHour, workTimeMinute);

		showWorkTime();
	}

	public void showWorkTime() {
		startTextView.setText(startWorkTime);
		finishTextView.setText(finishWorkTime);
		durableTextView.setText(workTimeToday);
	}

	class MoveRecord {
		int status;
		long changedTime;

		public MoveRecord(int status, long changedTime) {
			this.status = status;
			this.changedTime = changedTime;
		}
	}
}
