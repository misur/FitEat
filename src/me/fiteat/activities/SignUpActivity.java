package me.fiteat.activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.fiteat.DAO.SettingsDAO;
import me.fiteat.database.Connection;
import me.fiteat.statics.CustomDialogs;
import me.fiteat.R;

import org.w3c.dom.Document;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.widget.TextView;

public class SignUpActivity extends Activity {
	private EditText emailEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private EditText heightEditText;
	private EditText weightEditText;
	private EditText dayEditText;
	private EditText monthEditText;
	private EditText yearEditText;
	private String gender = "";
	private TextView signupTextView, genderTextView;
	private Button createAccountButton, femaleButton, maleButton;

	private SettingsDAO settingsDAO;

	private DisplayMetrics displaymetrics;
	private CustomDialogs customDialogs;

	private Typeface typeRegular;
	private Typeface typeBold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		customDialogs = new CustomDialogs(this, displaymetrics);

		typeRegular = Typeface.createFromAsset(getAssets(), "font-regular.ttf");
		typeBold = Typeface.createFromAsset(getAssets(), "font-bold.ttf");

		settingsDAO = new SettingsDAO(this);

		initUsernameEditText();
		initPasswordEditText();
		initEmailEditText();
		initCreateAccountButton();
		initSignupTextView();
		initHeightEditText();
		initWeightEditText();
		initDayEditText();
		initMonthEditText();
		initYearEditText();
		initMaleButton();
		initFemaleButton();
		initGenderTextView();
	}

	public void initGenderTextView() {
		genderTextView = (TextView) findViewById(R.id.textViewGender);
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
				genderTextView.setText("male");
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
				genderTextView.setText("female");
			}
		});

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
						String url = Connection.urlXml;
						url += "registration.php?username=" + usernameEditText.getText().toString().toLowerCase(Locale.getDefault()) + "&password="
								+ passwordEditText.getText().toString() + "&email=" + emailEditText.getText().toString() + "&weight="
								+ weightEditText.getText().toString() + "&height=" + heightEditText.getText().toString() + "&gender="
								+ gender.toString() + "&birthday=" + yearEditText.getText().toString() + "-" + monthEditText.getText().toString()
								+ "-" + dayEditText.getText().toString();

						new RetrieveFeedTask().execute(url);
					} else {
						customDialogs.showOkDialog(message);
					}
				} else {
					customDialogs.showOkDialog(getResources().getString(R.string.no_internet_connection_message));
				}
			}
		});

	}

	private void initUsernameEditText() {
		usernameEditText = (EditText) findViewById(R.id.usernameSingUpEditText);
		usernameEditText.setTypeface(typeBold);
		usernameEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				usernameEditText.setCursorVisible(true);
			}
		});
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
					if (yearEditText.getText().length() >= 2)
						yearEditText.setText("");

				}
			}
		});

		yearEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (yearEditText.getText().length() >= 4) {
					InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm2.hideSoftInputFromWindow(yearEditText.getWindowToken(), 0);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

		});
	}

	private void initPasswordEditText() {
		passwordEditText = (EditText) findViewById(R.id.passwordSingUpEditText);
		passwordEditText.setTypeface(typeBold);
		passwordEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				passwordEditText.setCursorVisible(true);
			}
		});
	}

	private void initEmailEditText() {
		emailEditText = (EditText) findViewById(R.id.emailSingUpEditText);
		emailEditText.setTypeface(typeBold);
		String email = getEmail();
		if (email != null) {
			emailEditText.setText(email);
		}
		emailEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				emailEditText.setCursorVisible(true);
			}
		});
	}

	private void initSignupTextView() {
		signupTextView = (TextView) findViewById(R.id.signupTextView);
		signupTextView.setTypeface(typeBold);
	}

	private String getEmail() {
		AccountManager accountManager = AccountManager.get(getApplicationContext());
		Account account = getAccount(accountManager);

		if (account == null) {
			return null;
		} else {
			return account.name;
		}
	}

	private Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		if (accounts.length > 0) {
			return accounts[0];
		}
		return null;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static final String md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	class RetrieveFeedTask extends AsyncTask<String, Void, String> {

		ProgressDialog dialog;
		private List resultatJSON;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(SignUpActivity.this, getString(R.string.registration_text), "Registering, please wait...", true);
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
				if (result.startsWith("ID:")) {

					String id = result.split(":")[1];
					String username = usernameEditText.getText().toString().toLowerCase(Locale.getDefault());
					String email = emailEditText.getText().toString();
					String height = heightEditText.getText().toString();
					String weight = weightEditText.getText().toString();
					String birthday = yearEditText.getText().toString() + "-" + monthEditText.getText().toString() + "-"
							+ dayEditText.getText().toString();

					boolean success = false;
					success = settingsDAO.saveToDatabase(Integer.parseInt(id), username, email, height, weight, gender, birthday);

					if (success) {
						Intent gameActivity = new Intent(SignUpActivity.this, MainActivity.class);
						startActivityForResult(gameActivity, 0);
						finish();
						return;
					} else {
						customDialogs.showOkDialog("ERROR");
						return;
					}
				}
				if (resultatJSON.get(0).equals("user_exists")) {
					customDialogs.showOkDialog("Username already exists!");
					return;
				} else {
					if (resultatJSON.get(0).equals("email_exists")) {
						customDialogs.showOkDialog(getString(R.string.email_exists_message));
					} else if (resultatJSON.get(0).equals("post_empty")) {
						customDialogs.showOkDialog("Error, post empty!");
					} else {
						customDialogs.showOkDialog("Error!");
					}
					return;
				}
			} else {
				customDialogs.showOkDialog(getString(R.string.registering_failed_message));
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
	}

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
		long id = -1;
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

	public String validate() {
		int emailSize = emailEditText.getText().length();
		int usernameSize = usernameEditText.getText().length();
		int passwordSize = passwordEditText.getText().length();
		int heightSize = heightEditText.getText().length();
		int weightSize = weightEditText.getText().length();
		int daySize = dayEditText.getText().length();
		int monthSize = monthEditText.getText().length();
		int yearSize = yearEditText.getText().length();
		int genderSize = gender.length();

		if (emailSize == 0) {
			return "You have to enter email!";
		}
		if (usernameSize == 0) {
			return getResources().getString(R.string.no_username_message);
		}
		if (passwordSize == 0) {
			return getResources().getString(R.string.no_password_message);
		}
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

	@Override
	public void onBackPressed() {
		Intent gameActivity = new Intent(SignUpActivity.this, LoginSignupActivity.class);
		startActivityForResult(gameActivity, 0);
		finish();

	}
}
