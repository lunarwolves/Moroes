package rui.com.moroes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class WorkTimeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_time);

		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);

		toolbar.setLogo(R.drawable.hourglass32);

		setSupportActionBar(toolbar);
	}
}
