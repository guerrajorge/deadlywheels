package groupa.deadlywheels.ui;


import groupa.deadlywheels.R;
import groupa.deadlywheels.carcontrol.DatagramSocketClientGate;
import groupa.deadlywheels.core.CarDroiDuinoCore;
import groupa.deadlywheels.utils.SystemProperties;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility")
public class CarControlActivity extends Activity{

	// Camera's IP address
	private String serverIPAddress;

	private String clientServerPort;

	private FrameView surfaceView;

	private CarDroiDuinoCore systemCore;

	private DatagramSocketClientGate socketClientGate;

	public Boolean game_started;

	TextView xAxisValue;
	
	TextView servertextview;

	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.control_layout);

		this.serverIPAddress = getIntent().getExtras().getString(
				SystemProperties.KEY_IP_ADDRESS);
		this.clientServerPort = getIntent().getExtras().getString(
				SystemProperties.KEY_PORT_NUMBER);
	
		ImageButton btnFrente = (ImageButton) findViewById(R.id.btnFrente);
		ImageButton btnRe = (ImageButton) findViewById(R.id.btnRe);
		ImageButton btnLeft = (ImageButton) findViewById(R.id.btnLeft);
		ImageButton btnRight = (ImageButton) findViewById(R.id.btnRight);

		xAxisValue = (TextView) findViewById(R.id.xAxisValue);

		btnFrente.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (game_started) {
					if (event.getAction() == MotionEvent.ACTION_DOWN)
						commandArduino("http://192.168.100.108/?forward");

					else if (event.getAction() == MotionEvent.ACTION_UP)
						commandArduino("http://192.168.100.108/?stop");
				}
				return true;

			}
		});

		btnRe.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (game_started) {
					if (event.getAction() == MotionEvent.ACTION_DOWN)
						commandArduino("http://192.168.100.108/?reverse");

					else if (event.getAction() == MotionEvent.ACTION_UP)
						commandArduino("http://192.168.100.108/?stop");
				}
				return true;
			}
		});

		btnLeft.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					commandArduino("http://192.168.100.108/?left");

				else if (event.getAction() == MotionEvent.ACTION_UP)
					commandArduino("http://192.168.100.108/?servo");

				return true;

			}
		});

		btnRight.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					commandArduino("http://192.168.100.108/?right");

				else if (event.getAction() == MotionEvent.ACTION_UP)
					commandArduino("http://192.168.100.108/?servo");

				return true;

			}
		});

		this.surfaceView = (FrameView) findViewById(R.id.surfaceControl);

		this.setupClient();

		/**
		 * ImageView crackview = (ImageView) findViewById(R.id.crackview);
		 * crackview.setOnTouchListener(new View.OnTouchListener() { public
		 * boolean onTouch(View v, MotionEvent event) { MediaPlayer mPlayer =
		 * MediaPlayer.create(CarControlActivity.this, R.raw.glass);
		 * mPlayer.start();
		 * 
		 * return true;
		 * 
		 * } });
		 */

		start_counter();

	}

	private void start_counter() {
		final Handler handler = new Handler();

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int interval = 3;
				// Do something after 5s = 5000ms
				start_countdown(interval);
			}
		}, 1000);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int interval = 2;
				// Do something after 5s = 5000ms
				start_countdown(interval);
			}
		}, 2500);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int interval = 1;
				// Do something after 5s = 5000ms
				start_countdown(interval);
			}
		}, 4000);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int interval = 0;
				// Do something after 5s = 5000ms
				start_countdown(interval);
			}
		}, 5500);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int interval = 6;
				// Do something after 5s = 5000ms
				start_countdown(interval);
			}
		}, 6000);

	}

	private void start_countdown(int interval) {

		ImageView NumberOne = (ImageView) findViewById(R.id.numberone);
		ImageView NumberTwo = (ImageView) findViewById(R.id.numbertwo);
		ImageView NumberThree = (ImageView) findViewById(R.id.numberthree);
		ImageView NumberGO = (ImageView) findViewById(R.id.numbergo);

		if (interval == 3) {
			game_started = false;
			NumberOne.setVisibility(View.INVISIBLE);
			NumberTwo.setVisibility(View.INVISIBLE);
			NumberThree.setVisibility(View.VISIBLE);
			NumberGO.setVisibility(View.INVISIBLE);

			MediaPlayer mPlayer = MediaPlayer.create(CarControlActivity.this,
					R.raw.beep);
			mPlayer.start();
		}

		else if (interval == 2) {
			NumberOne.setVisibility(View.INVISIBLE);
			NumberTwo.setVisibility(View.VISIBLE);
			NumberThree.setVisibility(View.INVISIBLE);
			NumberGO.setVisibility(View.INVISIBLE);
			MediaPlayer mPlayer = MediaPlayer.create(CarControlActivity.this,
					R.raw.beep);
			mPlayer.start();
		}

		else if (interval == 1) {
			NumberOne.setVisibility(View.VISIBLE);
			NumberTwo.setVisibility(View.INVISIBLE);
			NumberThree.setVisibility(View.INVISIBLE);
			NumberGO.setVisibility(View.INVISIBLE);
			MediaPlayer mPlayer = MediaPlayer.create(CarControlActivity.this,
					R.raw.beep);
			mPlayer.start();
		}

		else if (interval == 0) {
			NumberOne.setVisibility(View.INVISIBLE);
			NumberTwo.setVisibility(View.INVISIBLE);
			NumberThree.setVisibility(View.INVISIBLE);
			NumberGO.setVisibility(View.VISIBLE);
		}

		else if (interval == 6) {
			NumberOne.setVisibility(View.INVISIBLE);
			NumberTwo.setVisibility(View.INVISIBLE);
			NumberThree.setVisibility(View.INVISIBLE);
			NumberGO.setVisibility(View.INVISIBLE);
			game_started = true;
		}

	}

	private void setupClient() {

		this.systemCore = new CarDroiDuinoCore();
		try {

			this.socketClientGate = new DatagramSocketClientGate(
					this.systemCore, this.serverIPAddress,
					Integer.parseInt(this.clientServerPort));
			
			this.surfaceView.startImageDrawer(this.systemCore);
		} catch (Exception e) {
			new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
		}
	}

	public void commandArduino(String url) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.execute(new HttpGet(url));
		} catch (Exception e) {
		}
	}

	private void enviarComandoCarrinho(String comando) {
		if (this.systemCore != null)
			this.systemCore.addDataToCarControlQueue(comando.getBytes());
	}

	private void ligarDesligarLanterna() {
		this.enviarComandoCarrinho(SystemProperties.COMANDO_LANTERNA_SERVER);
	}

	private void killGates() {
		this.socketClientGate.turnOff();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.killGates();
	}

	public void cracksound() {
		MediaPlayer mPlayer = MediaPlayer.create(CarControlActivity.this,
				R.raw.glass);
		mPlayer.start();
	}
}
