package groupa.deadlywheels.ui;

import groupa.deadlywheels.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * <p>
 * <b>Description:</b> <br>
 * <p>
 * Home screen of DeadlyWheel System - Start Menu to Choose the Purpose Device :
 * Car Remote Control Server or remote control
 * </p>
 * 
 */
public class LoadAppDeadlyWheel extends Activity {

	// ************************************************
	// Android device id
	String android_id = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// ************************************************
		// Android device id
		// first server (Jorge's phone) id: f2113d40eb7bd59a
		// first client (Jonathan's phone) id: 2cc0cff86966c395
		android_id = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);

		Log.d("android id read by cell", android_id);

		// *************************************************
		// Performs first principal inherited class (Upper class - Extends )
		super.onCreate(savedInstanceState);

		// getting rib of the status bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// *************************************************
		// Which by setting the Layout ( XML ) Activity associated with this - >
		// CarDroiDuino \ res \ layout
		setContentView(R.layout.appload_layout);

	}

	/**
	 * Called when the user clicks on the image (for now) later it will be
	 * called after a certain time interval
	 */
	public void gotoLogin(View view) {

		if (android_id.contains("f2113d40eb7bd59a")
				|| android_id.contains("f02b4307b728b94c")) {
			// *************************************************
			// Creating Object to send the intent to start the Screen
			// properties
			Intent intent = new Intent(this, Login.class);
			intent.putExtra("EXTRA_ANDROID_ID", android_id);
			// *************************************************
			// Sending the Operating System the intent to start Activity
			startActivity(intent);
		}

		else if (android_id.contains("2cc0cff86966c395")
				|| android_id.contains("a5fb5698b3bd699c")) {
			// *************************************************
			// Creating Object to send the intent right to the video
			// screen
			Intent intent = new Intent(this, CarDroidDuinoActivity.class);
			intent.putExtra("EXTRA_ANDROID_ID", android_id);
			startActivity(intent);

		}
	}
}