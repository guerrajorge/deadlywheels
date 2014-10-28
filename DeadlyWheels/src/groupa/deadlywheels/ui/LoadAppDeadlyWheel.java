package groupa.deadlywheels.ui;

import groupa.deadlywheels.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// *************************************************
		// Performs first principal inherited class (Upper class - Extends )
		super.onCreate(savedInstanceState);

		// getting rib off the status bar
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
		// *************************************************
		// Creating Object to send the intent to start the Screen
		// properties
		Intent i = new Intent(this, Login.class);
		// *************************************************
		// Sending the Operating System the intent to start Activity
		startActivity(i);
	}
}