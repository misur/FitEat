package me.fiteat.statics;

import java.util.Calendar;

import me.fiteat.R;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogs {
	Context context;
	public Dialog dialog;
	DisplayMetrics displaymetrics;

	public CustomDialogs(Context context, DisplayMetrics displaymetrics) {
		super();
		this.context = context;
		this.displaymetrics = displaymetrics;
	}

	public void showOkDialog(String message) {
		dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.ok_view);
		dialog.setCancelable(false);
		Button okButton = (Button) dialog.findViewById(R.id.okButtonViewOk);
		TextView errorTextView = (TextView) dialog.findViewById(R.id.errorTextViewOkView);
		errorTextView.setText(message);
		errorTextView.setMovementMethod(new ScrollingMovementMethod());
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		int height = displaymetrics.heightPixels / 3;
		if (lp.height < height) {
			lp.height = height;
		}
		lp.width = (displaymetrics.widthPixels / 5) * 4;
		// lp.height = displaymetrics.heightPixels / 3;
		window.setAttributes(lp);
		dialog.show();
	}

	public void showYesNoDialog(String message, View.OnClickListener yesButtonListener, View.OnClickListener noButtonListener) {
		dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.yes_no_view);
		dialog.setCancelable(false);
		Button yesButton = (Button) dialog.findViewById(R.id.yesButtonYesNoView);
		Button noButton = (Button) dialog.findViewById(R.id.noButtonYesNoView);
		TextView errorTextView = (TextView) dialog.findViewById(R.id.errorTextViewYesNoView);
		errorTextView.setText(message);
		errorTextView.setMovementMethod(new ScrollingMovementMethod());
		yesButton.setOnClickListener(yesButtonListener);
		noButton.setOnClickListener(noButtonListener);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		int height = displaymetrics.heightPixels / 3;
		if (lp.height < height) {
			lp.height = height;
		}
		lp.width = (displaymetrics.widthPixels / 5) * 4;
		window.setAttributes(lp);
		dialog.show();
	}

	public void showCalorieDialog(String ingridiends, String calorie, String walking, String running, String biking) {
		dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.calorie_view);
		dialog.setCancelable(false);

		Button okButton = (Button) dialog.findViewById(R.id.caloreViewOkButton);

		TextView ingridientsTextView = (TextView) dialog.findViewById(R.id.calorieViewIngridientsTextView);
		ingridientsTextView.setText(ingridiends);

		TextView caloriesTextView = (TextView) dialog.findViewById(R.id.calorieViewCalorieTextView);
		caloriesTextView.setText(calorie + "kcal");

		TextView walkingTextView = (TextView) dialog.findViewById(R.id.walkigTextView);
		walkingTextView.setText(walking + " min");

		TextView runningTextView = (TextView) dialog.findViewById(R.id.runningTextView);
		runningTextView.setText(running + " min");

		TextView bikingTextView = (TextView) dialog.findViewById(R.id.bikingTextView);
		bikingTextView.setText(biking + " min");

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		int height = displaymetrics.heightPixels / 2;
		if (lp.height < height) {
			lp.height = height;
		}
		lp.width = (displaymetrics.widthPixels / 5) * 4;
		// lp.height = displaymetrics.heightPixels / 3;
		window.setAttributes(lp);
		dialog.show();
	}
}
