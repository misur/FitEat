package me.fiteat.activities;

import me.fiteat.DAO.SettingsDAO;
import me.fiteat.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginSignupActivity extends Activity {
	private Button loginButton;
	private Button singupButton;
	private Button changeLanguageButton;

	private SettingsDAO settingsDAO;

	private boolean doubleBackToExitPressedOnce = false;

	private Typeface typeRegular;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_singup);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		settingsDAO = new SettingsDAO(this);

		try {
			String username = settingsDAO.getUsername();
			if (!username.equals("empty")) {
				Intent gameActivity = new Intent(LoginSignupActivity.this,
						MainActivity.class);
				startActivityForResult(gameActivity, 0);
				finish();
			}
		} catch (Exception e) {

		}

		typeRegular = Typeface.createFromAsset(getAssets(), "font-regular.ttf");

		initLoginButton();
		initSingUpButton();
		initChangeLanguageButton();
	}

	private void initLoginButton() {
		loginButton = (Button) findViewById(R.id.loginLoginSingupButton);
		loginButton.setTypeface(typeRegular);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent gameActiviy = new Intent(LoginSignupActivity.this,
						LoginActivity.class);
				startActivityForResult(gameActiviy, 0);
				finish();

			}
		});
	}

	private void initSingUpButton() {
		singupButton = (Button) findViewById(R.id.singnupLoginSingupButton);
		singupButton.setTypeface(typeRegular);
		singupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent gameActiviy = new Intent(LoginSignupActivity.this,
						SignUpActivity.class);
				startActivityForResult(gameActiviy, 0);
				finish();

			}
		});

	}

	private void initChangeLanguageButton() {
//		changeLanguageButton = (Button) findViewById(R.id.changeLanguageSinginSingupButton);
//		changeLanguageButton.setTypeface(typeRegular);
//		changeLanguageButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (settingsDAO.saveLanguage("empty")) {
//					Intent gameActiviy = new Intent(LoginSignupActivity.this,
//							LanguageChooserActivity.class);
//					startActivityForResult(gameActiviy, 0);
//					finish();
//				}
//			}
//		});
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			finish();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT)
				.show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}
}
