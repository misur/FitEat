package me.fiteat.activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.fiteat.R;
import me.fiteat.DAO.SettingsDAO;
import me.fiteat.adapters.MealsListViewAdapter;
import me.fiteat.database.Connection;
import me.fiteat.logic.RangeSeekBar;
import me.fiteat.logic.RangeSeekBar.OnRangeSeekBarChangeListener;
import me.fiteat.shells.Meal;
import me.fiteat.shells.Restaurants;
import me.fiteat.statics.CustomDialogs;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MealsActivity extends Activity {

	private Typeface typeRegular;

	private DisplayMetrics displaymetrics;
	private CustomDialogs customDialogs;

	private List<Meal> mealsList;
	private ArrayList<Restaurants> restaurantsList;
	private Context context;

	private ListView mealsListView;
	private TextView minCaloriesTextView;
	private TextView maxCaloriesTextView;
	private TextView caloriesRangeTextView;

	int minSelectedCalories = 0;
	int maxSelectedCalories = 0;

	private SettingsDAO settingsDAO;

	boolean isStartedFromMainMenu = false;

	double height;
	double weight;
	double age;
	String gender;
	
	private int resPos = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meals);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		context = this;
		settingsDAO = new SettingsDAO(this);
		mealsListView = (ListView) findViewById(R.id.mealslistView);

		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		customDialogs = new CustomDialogs(this, displaymetrics);

		typeRegular = Typeface.createFromAsset(getAssets(), "font-regular.ttf");

		initMinCaloriesTextView();
		initMaxCaloriesTextView();
		initCaloriesRangeTextView();

		try {
			isStartedFromMainMenu = getIntent().getExtras().getBoolean("main");
		} catch (Exception e) {

		}

		try {
			restaurantsList = (ArrayList<Restaurants>) getIntent().getExtras().getSerializable("restList");
		} catch (Exception e) {

		}
		resPos = getIntent().getExtras().getInt("resPos");
		initData();
		try {
			mealsList = (ArrayList<Meal>) getIntent().getExtras().getSerializable("mealList");
			if (mealsList != null) {
				initMealsListView();
				for (int i = 0; i < mealsList.size(); i++) {
					int calories = Integer.parseInt(mealsList.get(i).getCalories());
					if (calories < minSelectedCalories || minSelectedCalories == 0) {
						minSelectedCalories = calories;
					}
					if (calories > maxSelectedCalories) {
						maxSelectedCalories = calories;
					}
				}

				initRangeSeekBar(minSelectedCalories, maxSelectedCalories);
				minCaloriesTextView.setText(minSelectedCalories + " kcal");
				maxCaloriesTextView.setText(maxSelectedCalories + " kcal");
				caloriesRangeTextView.setText(minSelectedCalories + "-" + maxSelectedCalories + " kcal");
			}
		} catch (Exception e) {

		}
		
		if (mealsList == null) {
			downloadMeals(getIntent().getExtras().getString("id"));
		}

	}

	private void initData() {
		height = Double.parseDouble(settingsDAO.getHeight());
		weight = Double.parseDouble(settingsDAO.getWeight());
		age = getAge(settingsDAO.getBirthday());
		gender = settingsDAO.getGender();
	}

	private void initMealsListView() {
		mealsListView = (ListView) findViewById(R.id.mealslistView);
		MealsListViewAdapter listAdapter = new MealsListViewAdapter(context, mealsList, height, weight, age, gender);
		mealsListView.setAdapter(listAdapter);
	}

	private void initMinCaloriesTextView() {
		minCaloriesTextView = (TextView) findViewById(R.id.minCaloriesTextView);
		minCaloriesTextView.setTypeface(typeRegular);
	}

	private void initMaxCaloriesTextView() {
		maxCaloriesTextView = (TextView) findViewById(R.id.maxCaloriesTextView);
		maxCaloriesTextView.setTypeface(typeRegular);
	}

	private void initCaloriesRangeTextView() {
		caloriesRangeTextView = (TextView) findViewById(R.id.caloriesRangeTextView);
		caloriesRangeTextView.setTypeface(typeRegular);
	}

	private void initRangeSeekBar(int min, int max) {
		RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(min, max, context);
		seekBar.setNotifyWhileDragging(true);
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				minSelectedCalories = minValue;
				maxSelectedCalories = maxValue;

				caloriesRangeTextView.setText(minValue + "-" + maxValue + " kcal");

				MealsListViewAdapter listAdapter = new MealsListViewAdapter(context, filterListBasedOnCalories(), height, weight, age, gender);
				mealsListView.setAdapter(listAdapter);
				mealsListView.invalidateViews();
			}
		});

		// add RangeSeekBar to pre-defined layout
		LinearLayout layout = (LinearLayout) findViewById(R.id.mealSeekBarLayout);
		layout.addView(seekBar);
	}

	private List<Meal> filterListBasedOnCalories() {
		ArrayList<Meal> mealsFilteredList = new ArrayList<Meal>();
		for (int i = 0; i < mealsList.size(); i++) {
			int calories = Integer.parseInt(mealsList.get(i).getCalories());
			if (calories <= maxSelectedCalories && calories >= minSelectedCalories) {
				mealsFilteredList.add(mealsList.get(i));
			}
		}
		return mealsFilteredList;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private void downloadMeals(String restaurantId) {
		String url = Connection.urlXml;
		url += "return_menu.php?restaurant_id=" + restaurantId;
		new RetrieveFeedTask().execute(url);
	}

	class RetrieveFeedTask extends AsyncTask<String, Void, String> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(MealsActivity.this, getString(R.string.downloading_text), getString(R.string.meals_downloading_text), true);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... urls) {

			try {
				URL url = new URL(urls[0]);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				mealsList = readJsonStream(conn.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
				return "fail";
			}
			return "success";
		}

		protected void onPostExecute(String x) {
			dialog.dismiss();
			if (x.equals("success")) {
				initMealsListView();
				for (int i = 0; i < mealsList.size(); i++) {
					int calories = Integer.parseInt(mealsList.get(i).getCalories());
					if (calories < minSelectedCalories || minSelectedCalories == 0) {
						minSelectedCalories = calories;
					}
					if (calories > maxSelectedCalories) {
						maxSelectedCalories = calories;
					}
				}

				initRangeSeekBar(minSelectedCalories, maxSelectedCalories);
				minCaloriesTextView.setText(minSelectedCalories + " kcal");
				maxCaloriesTextView.setText(maxSelectedCalories + " kcal");
				caloriesRangeTextView.setText(minSelectedCalories + "-" + maxSelectedCalories + " kcal");
			} else {
				customDialogs.showOkDialog("Error downloading meals, please try again.");
			}
		}
	}

	public List readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readMessagesArray(reader);
		} finally {
			reader.close();
		}
	}

	public List readMessagesArray(JsonReader reader) throws IOException {
		List messages = new ArrayList();

		reader.beginArray();
		while (reader.hasNext()) {
			messages.add(readMessage(reader));
		}
		reader.endArray();
		return messages;
	}

	public Meal readMessage(JsonReader reader) throws IOException {
		String id = "-1";
		String nameM = "-1";
		String price = "-1";
		String calories = "-1";
		String ingredients = "-1";

		reader.beginObject();

		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				id = reader.nextString();

			} else if (name.equals("name")) {
				nameM = reader.nextString();

			} else if (name.equals("price")) {
				price = reader.nextString();

			} else if (name.equals("calories")) {
				calories = reader.nextString();

			} else if (name.equals("ingredients")) {
				ingredients = reader.nextString();
			} else {
				reader.skipValue();
			}
		}
		// } else if (name.equals("ingredients")) {

		reader.endObject();
		return new Meal(id, nameM, price, calories, ingredients);
	}

	public ArrayList<String> readIngredientsArray(JsonReader reader) throws IOException {
		ArrayList<String> messages = new ArrayList<String>();

		reader.beginArray();
		while (reader.hasNext()) {
			messages.add(readIngredient(reader));
		}
		reader.endArray();
		return messages;
	}

	public String readIngredient(JsonReader reader) throws IOException {
		String nameM = "-1";

		reader.beginObject();

		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("name")) {
				nameM = reader.nextString();

			} else {
				reader.skipValue();
			}
		}

		reader.endObject();
		return nameM;
	}

	public void startGame(String gameNumber, String username) {
		if (isNetworkAvailable()) {
			String url = Connection.urlXml;
			url += "ReturnGame.aspx?gameNumber=" + gameNumber + "&username=" + username;
			new RetrieveFeedTask().execute(url);
		} else {
			customDialogs.showOkDialog(getString(R.string.no_internet_connection_message));
		}
	}

	private double getAge(String birthday) {
		Calendar date1 = Calendar.getInstance();

		String[] parts = birthday.split("-");
		int year = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		int day = Integer.parseInt(parts[2]);

		date1.clear();
		date1.set(year, month, day);

		long diff = System.currentTimeMillis() - date1.getTimeInMillis();

		float dayCount = (float) diff / (24 * 60 * 60 * 1000);

		double age = ((double) dayCount) / ((double) 365);

		return age;
	}
	
	private ArrayList<Meal> convertFromList(List<Meal> list) {

		ArrayList<Meal> al = new ArrayList<Meal>();
		for (Meal meal : list) {
			al.add(meal);
		}
		return al;
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(MealsActivity.this, MainActivity.class);
		if (!isStartedFromMainMenu) {
			intent = new Intent(MealsActivity.this, RestaurantsActivity.class);
			intent.putExtra("restList", restaurantsList);
			intent.putExtra("mealList", convertFromList(mealsList));
			intent.putExtra("resPos", resPos);
		}

		startActivityForResult(intent, 0);
		finish();
	}
}
