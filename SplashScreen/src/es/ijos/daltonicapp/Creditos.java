package es.ijos.daltonicapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class Creditos extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creditos);
		
		
		//Brillo m�ximo permanente
	    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
	    layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}
	
	
}
