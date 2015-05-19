package me.fiteat.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import me.fiteat.DAO.SettingsDAO;

import me.fiteat.statics.CustomDialogs;
import me.fiteat.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.FeatureInfo;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import me.fiteat.logic.Shaker;

public class MainActivity extends Activity implements Shaker.Callback {

	private Button restaurantsButton;
	private Button profileButton;
	private Button logoutButton;

	private SettingsDAO settingsDAO;

	private boolean hasGameNumber = false;

	private boolean doubleBackToExitPressedOnce = false;
	private String gameNumber = "";

	private DisplayMetrics displaymetrics;
	private CustomDialogs customDialogs;

	private Typeface typeRegular;

	private Shaker shaker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		customDialogs = new CustomDialogs(this, displaymetrics);

		typeRegular = Typeface.createFromAsset(getAssets(), "font-regular.ttf");

		try {
			backupDb();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initRestaurantsButton();
		initProfileButton();
		initLogoutButton();

		TextView homeTextView = (TextView) findViewById(R.id.homeTextView);
		homeTextView.setTypeface(typeRegular);

		initMainActivity();

		shaker = new Shaker(this, 2.5d, 500l, this);

	}

	private void backupDb() throws IOException {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();

		if (sd.canWrite()) {

			String currentDBPath = "/data/me.fiteat/databases/fiteat.db";
			String backupDBPath = "/FitEat/db/fiteat.db";

			File currentDB = new File(data, currentDBPath);
			File backupDB = new File(sd, backupDBPath);

			if (backupDB.exists())
				backupDB.delete();

			if (currentDB.exists()) {
				makeLogsFolder();

				copy(currentDB, backupDB);
			}

		}
	}

	private void copy(File from, File to) throws FileNotFoundException, IOException {
		FileChannel src = null;
		FileChannel dst = null;
		try {
			src = new FileInputStream(from).getChannel();
			dst = new FileOutputStream(to).getChannel();
			dst.transferFrom(src, 0, src.size());
		} finally {
			if (src != null)
				src.close();
			if (dst != null)
				dst.close();
		}
	}

	private void makeLogsFolder() {
		try {
			File sdFolder = new File(Environment.getExternalStorageDirectory(), "/FitEat/db/");
			sdFolder.mkdirs();
		} catch (Exception e) {
		}
	}

	private void initMainActivity() {
		settingsDAO = new SettingsDAO(this);
	}

	private void initRestaurantsButton() {
		restaurantsButton = (Button) findViewById(R.id.historyButton);
		restaurantsButton.setTypeface(typeRegular);
		restaurantsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent restaurantsActivity = new Intent(MainActivity.this, RestaurantsActivity.class);
				startActivityForResult(restaurantsActivity, 0);
				finish();
			}
		});

	}

	private void initProfileButton() {
		profileButton = (Button) findViewById(R.id.settingsButton);
		profileButton.setTypeface(typeRegular);
		profileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent gameActivity = new Intent(MainActivity.this, ProfileActivity.class);
				startActivityForResult(gameActivity, 0);
				finish();
			}
		});
	}

	private void initLogoutButton() {
		logoutButton = (Button) findViewById(R.id.logoutButton);
		logoutButton.setTypeface(typeRegular);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (settingsDAO.deleteAllData()) {
					Intent loginSignupActivity = new Intent(MainActivity.this, LoginSignupActivity.class);
					startActivityForResult(loginSignupActivity, 0);
					finish();
				}
			}
		});

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private void showLocationDisabledAlertToUser() {
		customDialogs.showYesNoDialog("Network location is disabled on your device. Would you like to enable it?", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				customDialogs.dialog.dismiss();
				Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(callGPSSettingIntent);
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				customDialogs.dialog.dismiss();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		shaker.close();
	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		shaker = new Shaker(this, 2.5d, 500l, this);
	
	}

	public void shakingStarted() {
		System.out.println("shake it baybe");
	}

	public void shakingStopped() {
		Intent scanActivity = new Intent(this, ScanActivity.class);
		startActivityForResult(scanActivity, 0);
		shaker = null;
		shaker.close();
		
		return;
	}

	// qr code scan result
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("result");

				String id = "";
				String fiteat = "";
				try {
					fiteat = contents.split("-")[0];
					id = contents.split("-")[1];
				} catch (Exception e) {

				}

				if (!id.equals("") & fiteat.equals("FitEat")) {
					Intent mealsActivity = new Intent(this, MealsActivity.class);
					mealsActivity.putExtra("id", id);
					mealsActivity.putExtra("main", true);
					startActivityForResult(mealsActivity, 0);
					finish();
				} else {
					customDialogs.showOkDialog("You must scan code that belongs to FitEat restaurant!");
				}
			}else {
				//ressume
			}

		}
	}

}
