package groupa.deadlywheels.ui;

import groupa.deadlywheels.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Referee extends Activity {

	
	@SuppressLint("NewApi")
	@Override
	   protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_referee);
		
		
		Button btncar1 = (Button) findViewById(R.id.car1);
		
		Button btncar2 = (Button) findViewById(R.id.car2);
		
		
		
		
		
		btncar1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				commandArduino("http://192.168.100.107/?forward");
			}
		});
		
		btncar2.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				commandArduino("http://192.168.100.107/?reverse");
				
			}
		});
		
	}
	
	public void commandArduino(String url) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.execute(new HttpGet(url));
		} catch (Exception e) {
		}
	}

	
}
