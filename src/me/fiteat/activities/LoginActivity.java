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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private EditText usernameEditText;
	private EditText passwordEditText;
	private TextView loginTextView;
	private Button loginButton;

	private SettingsDAO settingsDAO;

	private DisplayMetrics displaymetrics;
	private CustomDialogs customDialogs;

	private Typeface typeRegular;
	private Typeface typeBold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		customDialogs = new CustomDialogs(this, displaymetrics);

		typeRegular = Typeface.createFromAsset(getAssets(), "font-regular.ttf");
		typeBold = Typeface.createFromAsset(getAssets(), "font-bold.ttf");

		settingsDAO = new SettingsDAO(this);

		try {
			String username = settingsDAO.getUsername();
			if (!username.equals("empty")) {
				Intent gameActivity = new Intent(LoginActivity.this, MainActivity.class);
				startActivityForResult(gameActivity, 0);
				finish();
			}
		} catch (Exception e) {

		}

		initUsernameEditText();
		initPasswordEditText();
		initLoginButton();
		initLoginTextView();
	}

	private void initUsernameEditText() {
		usernameEditText = (EditText) findViewById(R.id.usernameLogInEditText);
		usernameEditText.setTypeface(typeBold);
		usernameEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				usernameEditText.setCursorVisible(true);
			}
		});
	}

	private void initPasswordEditText() {
		passwordEditText = (EditText) findViewById(R.id.passwordLogInEditText);
		passwordEditText.setTypeface(typeBold);
		passwordEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				passwordEditText.setCursorVisible(true);
			}
		});
	}

	private void initLoginTextView() {
		loginTextView = (TextView) findViewById(R.id.loginTextView);
		loginTextView.setTypeface(typeBold);
	}

	private void initLoginButton() {
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setTypeface(typeRegular);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String message = validate();
				if (isNetworkAvailable()) {
					if (message.equals("success")) {
						String url = Connection.urlXml;
						url += "login.php?username=" + usernameEditText.getText().toString().toLowerCase(Locale.getDefault()) + "&password="
								+ passwordEditText.getText().toString();
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
		private List resultatJSON;
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(LoginActivity.this, getResources().getString(R.string.login_text),
					getResources().getString(R.string.login_progress_message), true);
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
				if (resultatJSON.get(0).equals("bad_login")) {
					customDialogs.showOkDialog(getResources().getString(R.string.bad_username_message));
				} else if (resultatJSON.get(0).equals("login_success")) {
					String username = resultatJSON.get(3).toString();
					String id = resultatJSON.get(1).toString();
					String email = resultatJSON.get(2).toString();
					String height = resultatJSON.get(4).toString();
					String weight = resultatJSON.get(5).toString();
					String gender = resultatJSON.get(6).toString();
					String birthday = resultatJSON.get(7).toString();

					if (settingsDAO.saveToDatabase(Integer.parseInt(id), username, email, height, weight, gender, birthday)) {
						Intent gameActiviy = new Intent(LoginActivity.this, MainActivity.class);
						startActivityForResult(gameActiviy, 0);
						finish();
						return;
					}
					else {
						customDialogs.showOkDialog("ERROR");
					}
				}

			} else {
				customDialogs.showOkDialog(getResources().getString(R.string.login_failed_message));
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
			} else if (name.equals("id")) {
				text = reader.nextString();
				messages.add(text);
			} else if (name.equals("email")) {
				text = reader.nextString();
				messages.add(text);
			} else if (name.equals("username")) {
				text = reader.nextString();
				messages.add(text);
			} else if (name.equals("height")) {
				text = reader.nextString();
				messages.add(text);
			} else if (name.equals("weight")) {
				text = reader.nextString();
				messages.add(text);
			} else if (name.equals("gender")) {
				text = reader.nextString();
				messages.add(text);
			} else if (name.equals("birthday")) {
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
		int usernameSize = usernameEditText.getText().length();
		int passwordSize = passwordEditText.getText().length();
		if (usernameSize == 0) {
			return getResources().getString(R.string.no_username_message);
		}
		if (passwordSize == 0) {
			return getResources().getString(R.string.no_password_message);
		}
		return "success";
	}

	@Override
	public void onBackPressed() {
		Intent gameActiviy = new Intent(LoginActivity.this, LoginSignupActivity.class);
		startActivityForResult(gameActiviy, 0);
		finish();
	}
}
