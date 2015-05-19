package me.fiteat.activities;

import me.fiteat.R;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.camera.BeepManager;

public class ScanActivity extends CaptureActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr_scan);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}

	@Override
	public void handleDecode(Result rawResult, Bitmap barcode, float a) {
		// Toast.makeText(this.getApplicationContext(),
		// "Scanned code " + rawResult.getText(), Toast.LENGTH_LONG)
		// .show();
		BeepManager bm = new BeepManager(this);
		bm.playBeepSoundAndVibrate();
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", rawResult.getText());
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	@Override
	public void onBackPressed() {

		finish();
	}
}
