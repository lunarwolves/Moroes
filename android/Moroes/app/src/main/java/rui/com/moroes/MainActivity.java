package rui.com.moroes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import rui.com.location.LocationService;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent serviceIntent = new Intent(this, LocationService.class);
		startService(serviceIntent);

		Intent intent = new Intent(this, WorkTimeActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
