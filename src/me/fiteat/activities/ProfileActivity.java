package me.fiteat.activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import me.fiteat.R;
import me.fiteat.DAO.SettingsDAO;
import me.fiteat.database.Connection;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class ProfileActivity extends Activity {

	private EditText heightEditText;
	private EditText weightEditText;
	private EditText dayEditText;
	private EditText monthEditText;
	private EditText yearEditText;
	private String gender = "";

	private String height, weight, day, month, year, sex;

	private Button createAccountButton, maleButton, femaleButton;

	private SettingsDAO settingsDAO;

	private DisplayMetrics displaymetrics;
	private CustomDialogs customDialogs;

	private Typeface typeRegular;
	private Typeface typeBold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		customDialogs = new CustomDialogs(this, displaymetrics);

		typeRegular = Typeface.createFromAsset(getAssets(), "font-regular.ttf");
		typeBold = Typeface.createFromAsset(getAssets(), "font-bold.ttf");

		settingsDAO = new SettingsDAO(this);

		initCreateAccountButton();
		initHeightEditText();
		initWeightEditText();
		initDayEditText();
		initMonthEditText();
		initYearEditText();

		initFemaleButton();
		initMaleButton();
		setValue();
	}

	public void setValue() {
		heightEditText.setText(settingsDAO.getHeight());
		weightEditText.setText(settingsDAO.getWeight());

		if (settingsDAO.getGender().equals("m")) {
			maleButton.setBackgroundResource(R.drawable.maleactive);
			gender = "m";
			sex = "m";
		} else {
			femaleButton.setBackgroundResource(R.drawable.femaleactive);
			gender = "f";
			sex = "f";
		}
		dayEditText.setText(settingsDAO.getBirthday().split("-")[2]);
		monthEditText.setText(settingsDAO.getBirthday().split("-")[1]);
		yearEditText.setText(settingsDAO.getBirthday().split("-")[0]);

		height = settingsDAO.getHeight();
		weight = settingsDAO.getWeight();
		day = settingsDAO.getBirthday().split("-")[2];
		month = settingsDAO.getBirthday().split("-")[1];
		year = settingsDAO.getBirthday().split("-")[0];

	}

	private void initCreateAccountButton() {

		createAccountButton = (Button) findViewById(R.id.createAccountButton);
		createAccountButton.setTypeface(typeRegular);
		createAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String message = validate();
				if (isNetworkAvailable()) {
					if (message.equals("success")) {
						if (hasDataChanged()) {
							String url = Connection.urlXml;

							url += "update_user.php?id=" + settingsDAO.getUserID() + "&weight=" + weightEditText.getText().toString() + "&height="
									+ heightEditText.getText().toString() + "&gender=" + gender.toString() + "&birthday="
									+ yearEditText.getText().toString() + "-" + monthEditText.getText().toString() + "-"
									+ dayEditText.getText().toString();
							new RetrieveFeedTask().execute(url);
						} else {
							Intent mainActivity = new Intent(ProfileActivity.this, MainActivity.class);
							startActivityForResult(mainActivity, 0);
							finish();
						}
					} else {
						customDialogs.showOkDialog(message);
					}
				} else {
					customDialogs.showOkDialog(getResources().getString(R.string.no_internet_connection_message));
				}
			}
		});

	}

	private boolean hasDataChanged() {
		if (weight.equals(weightEditText.getText().toString()) && height.equals(heightEditText.getText().toString()) && sex.equals(gender)
				&& day.equals(dayEditText.getText().toString()) && month.equals(monthEditText.getText().toString())
				&& year.equals(yearEditText.getText().toString())) {
			return false;
		}
		return true;
	}

	public String validate() {

		int heightSize = heightEditText.getText().length();
		int weightSize = weightEditText.getText().length();
		int daySize = dayEditText.getText().length();
		int monthSize = monthEditText.getText().length();
		int yearSize = yearEditText.getText().length();
		int genderSize = gender.length();

		if (heightSize == 0) {
			return "You have to enter height!";
		}
		if (weightSize == 0) {
			return "You have to enter weight!";
		}
		if (genderSize == 0) {
			return "You have to enter gender!";
		}
		if (daySize == 0) {
			return "You have to enter birthday!";
		}
		if (monthSize == 0) {
			return "You have to enter birthday!";
		}
		if (yearSize == 0) {
			return "You have to enter birthday!";
		}
		return "success";
	}

	private void initHeightEditText() {
		heightEditText = (EditText) findViewById(R.id.heightSingUpEditText);
		heightEditText.setTypeface(typeBold);
		heightEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				heightEditText.setCursorVisible(true);
			}
		});
	}

	private void initWeightEditText() {
		weightEditText = (EditText) findViewById(R.id.weightSingUpEditText);
		weightEditText.setTypeface(typeBold);
		weightEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				weightEditText.setCursorVisible(true);
			}
		});
	}

	private void initDayEditText() {
		dayEditText = (EditText) findViewById(R.id.daySingUpEditText);
		dayEditText.setTypeface(typeBold);
		dayEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dayEditText.setCursorVisible(true);
			}
		});

		dayEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (dayEditText.getText().length() >= 2)
						dayEditText.setText("");

				}
			}
		});

		dayEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (dayEditText.getText().length() >= 2)
					monthEditText.requestFocus();
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (dayEditText.getText().length() >= 2) {

				}
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (dayEditText.getText().length() >= 2)
					monthEditText.requestFocus();
			}

		});
	}

	private void initMonthEditText() {
		monthEditText = (EditText) findViewById(R.id.monthSingUpEditText);
		monthEditText.setTypeface(typeBold);
		monthEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				monthEditText.setCursorVisible(true);
			}
		});

		monthEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (monthEditText.getText().length() >= 2)
						monthEditText.setText("");

				}
			}
		});

		monthEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (monthEditText.getText().length() >= 2)
					yearEditText.requestFocus();
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (monthEditText.getText().length() >= 2)
					yearEditText.requestFocus();
			}

		});
	}

	private void initYearEditText() {
		yearEditText = (EditText) findViewById(R.id.yearSingUpEditText);
		yearEditText.setTypeface(typeBold);
		yearEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				yearEditText.setCursorVisible(true);
			}
		});

		yearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (yearEditText.getText().length() >= 4)
						yearEditText.setText("");

				}
			}
		});

		yearEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (yearEditText.getText().length() >= 4) {
					InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm1.hideSoftInputFromWindow(yearEditText.getWindowToken(), 0);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (yearEditText.getText().length() >= 4) {
					InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm2.hideSoftInputFromWindow(yearEditText.getWindowToken(), 0);
				}
			}

		});
	}

	public void initMaleButton() {
		maleButton = (Button) findViewById(R.id.buttonMale);
		maleButton.setBackgroundResource(R.drawable.maleinactive);
		maleButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				maleButton.setBackgroundResource(R.drawable.maleactive);
				femaleButton.setBackgroundResource(R.drawable.femaleinactive);
				gender = "m";
			}
		});
	}

	public void initFemaleButton() {
		femaleButton = (Button) findViewById(R.id.buttonFemale);
		femaleButton.setBackgroundResource(R.drawable.femaleinactive);
		femaleButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				femaleButton.setBackgroundResource(R.drawable.femaleactive);
				maleButton.setBackgroundResource(R.drawable.maleinactive);
				gender = "f";
			}
		});

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	class RetrieveFeedTask extends AsyncTask<String, Void, String> {

		ProgressDialog dialog;
		private List resultatJSON;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(ProfileActivity.this, getString(R.string.registration_text), "Registering, please wait...", true);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... urls) {

			try {
				URL url = new URL(urls[0]);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);

				resultatJSON = readJsonStream(conn.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
				return "fail";
			}
			return "success";
		}

		protected void onPostExecute(String x) {
			dialog.dismiss();
			if (x.equals("success")) {

				String result = resultatJSON.get(0).toString();
				if (result.equals("update_success")) {

					settingsDAO.updateSettings(heightEditText.getText().toString(), weightEditText.getText().toString(), gender.toString(),
							yearEditText.getText().toString() + "-" + monthEditText.getText().toString() + "-" + dayEditText.getText().toString());

					Intent mainActivity = new Intent(ProfileActivity.this, MainActivity.class);
					startActivityForResult(mainActivity, 0);
					finish();
					return;
				} else if (resultatJSON.get(0).equals("invalide_parametars")) {
					customDialogs.showOkDialog("Invalid parameters");
					return;
				} else if (resultatJSON.get(0).equals("nothing_updated")) {
					customDialogs.showOkDialog("Nothing updated");
					return;
				}
			} else {
				customDialogs.showOkDialog("Update failed");
				return;
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
	} // private void initSignupTextView() {

	// signupTextView = (TextView) findViewById(R.id.signupTextView);
	// signupTextView.setTypeface(typeBold);
	// }

	public List readMessagesArray(JsonReader reader) throws IOException {
		List messages = new ArrayList();

		String text = null;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("message")) {
				text = reader.nextString();
				messages.add(text);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return messages;
	}

	public String readMessage(JsonReader reader) throws IOException {
		String text = null;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("message")) {
				text = reader.nextString();

			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return text;
	}

	@Override
	public void onBackPressed() {
		Intent mainActivity = new Intent(ProfileActivity.this, LoginSignupActivity.class);
		startActivityForResult(mainActivity, 0);
		finish();

	}
}
