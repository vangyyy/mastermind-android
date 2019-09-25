package com.vangor.mastermind.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.vangor.mastermind.R;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		Handler handler = new Handler();

		final Runnable r = new Runnable() {
			public void run() {
				goToMainActivity();
			}
		};

		long splashTime = 500L; // 0.5 second

		handler.postDelayed(r, splashTime);
	}

	private void goToMainActivity() {
		Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(mainActivityIntent);
		finish();
	}
}
