package me.fiteat.activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import me.fiteat.R;
import me.fiteat.adapters.RestaurantsListViewAdapter;
import me.fiteat.database.Connection;
import me.fiteat.shells.Meal;
import me.fiteat.shells.Restaurants;
import me.fiteat.statics.CustomDialogs;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class RestaurantsActivity extends Activity {

	private TextView publicGamesTextView;
	private ListView restaurantsListView;

	private Context context;

	private Typeface typeBold;

	private DisplayMetrics displaymetrics;
	private CustomDialogs customDialogs;

	ArrayList<Restaurants[]> restaurantsList = null;
	private Spinner citySpinner;

	private ArrayList<String> cities;

	List<Restaurants> restaurants = null;
	ArrayList<Meal> mealsList = null;
	
	int resPos = -1;

	private String height, weight, day, month, year, sex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurants);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		customDialogs = new CustomDialogs(this, displaymetrics);

		typeBold = Typeface.createFromAsset(getAssets(), "font-bold.ttf");

		publicGamesTextView = (TextView) findViewById(R.id.publicGamesTextView);
		publicGamesTextView.setTypeface(typeBold);

		context = this;

		initCitySpinnerButton();

		restaurantsListView = (ListView) findViewById(R.id.restaurantsListView);
		try {
			restaurants = (ArrayList<Restaurants>) getIntent().getExtras().getSerializable("restList");
			restaurantsList = refactorList(restaurants);
			initRestaurantsListView();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, cities);
			adapter.setDropDownViewResource(R.layout.spinner_item);
			citySpinner.setAdapter(adapter);
		} catch (Exception e) {

		}

		try {
			mealsList = (ArrayList<Meal>) getIntent().getExtras().getSerializable("mealList");
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		try {
			resPos =  getIntent().getExtras().getInt("resPos");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (restaurantsList == null) {
			downloadRestaurants();
		}

	}

	private void initCitySpinnerButton() {
		citySpinner = (Spinner) findViewById(R.id.citySpinner);
		citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				List<Restaurants> pom = new ArrayList<Restaurants>();

				for (int i = 0; i < restaurants.size(); i++) {
					if (citySpinner.getSelectedItem().toString().equals("ALL")) {
						pom = restaurants;
					} else {
						if (restaurants.get(i).getCity().equals(citySpinner.getSelectedItem().toString())) {
							pom.add(restaurants.get(i));
						}
					}
				}
				ArrayList<Restaurants[]> pom1 = refactorList(pom);
				RestaurantsListViewAdapter listAdapter = new RestaurantsListViewAdapter(context, pom1, restaurants, mealsList, resPos);
				restaurantsListView.setAdapter(listAdapter);
				restaurantsListView.invalidate();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void downloadRestaurants() {
		String url = Connection.urlXml;
		url += "return_restaurants.php?restaurants";
		new RetrieveFeedTask().execute(url);
	}

	private void initRestaurantsListView() {
		RestaurantsListViewAdapter listAdapter = new RestaurantsListViewAdapter(context, restaurantsList, restaurants, mealsList,resPos);
		restaurantsListView.setAdapter(listAdapter);
	}

	class RetrieveFeedTask extends AsyncTask<String, Void, String> {

		ProgressDialog pdialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = ProgressDialog.show(RestaurantsActivity.this, getString(R.string.downloading_text), getString(R.string.restaurants_text), true);
			pdialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				URL url = new URL(urls[0]);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);

				restaurants = (List<Restaurants>) readJsonStream(conn.getInputStream());
				restaurantsList = refactorList(restaurants);

			} catch (Exception e) {
				e.printStackTrace();
				return "fail";
			}
			return "success";
		}

		protected void onPostExecute(String x) {
			pdialog.dismiss();
			if (x.equals("success")) {
				initRestaurantsListView();
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, cities);
				adapter.setDropDownViewResource(R.layout.spinner_item);
				citySpinner.setAdapter(adapter);
			} else {
				customDialogs.showOkDialog("Error downloading restaurants, please try again");
			}
		}
	}

	private ArrayList<Restaurants[]> refactorList(List<Restaurants> restaurants) {
		ArrayList<Restaurants[]> restorantsListDouble = new ArrayList<Restaurants[]>();
		cities = new ArrayList<String>();
		cities.add("ALL");
		for (int i = 0; i < restaurants.size(); i++) {
			Restaurants r = restaurants.get(i);
			if (!cities.contains(r.getCity())) {
				cities.add(r.getCity());
			}
			if (i % 2 == 0) {
				if (i + 1 < restaurants.size()) {
					restorantsListDouble.add(new Restaurants[] { r, restaurants.get(i + 1) });
				} else {
					restorantsListDouble.add(new Restaurants[] { restaurants.get(i), null });
				}
			}

		}

		return restorantsListDouble;
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

	public Restaurants readMessage(JsonReader reader) throws IOException {
		String id = "-1";
		String username = "-1";
		String password = "-1";
		String email = "-1";
		String nameR = "-1";
		String country = "-1";
		String city = "-1";
		String logo = "-1";
		String qrCode = "-1";

		reader.beginObject();

		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				id = reader.nextString();
			} else if (name.equals("username")) {
				username = reader.nextString();
			} else if (name.equals("password")) {
				password = reader.nextString();

			} else if (name.equals("email")) {
				email = reader.nextString();

			} else if (name.equals("name")) {
				nameR = reader.nextString();

			} else if (name.equals("country")) {
				country = reader.nextString();

			} else if (name.equals("city")) {
				city = reader.nextString();

			} else if (name.equals("logo")) {
				logo = reader.nextString();

			} else if (name.equals("qr_code")) {
				qrCode = reader.nextString();
			} else {
				reader.skipValue();
			}
		}

		reader.endObject();
		return new Restaurants(id, username, password, email, nameR, country, city, logo, qrCode);
	}

	@Override
	public void onBackPressed() {
		Intent gameActivity = new Intent(RestaurantsActivity.this, MainActivity.class);
		startActivityForResult(gameActivity, 0);
		finish();
	}
}
