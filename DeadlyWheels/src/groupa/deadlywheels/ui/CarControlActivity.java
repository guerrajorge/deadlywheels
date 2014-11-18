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
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility")
public class CarControlActivity extends Activity implements SensorEventListener {

	// Camera's IP address
	private String serverIPAddress;

	private String clientServerPort;

	private FrameView surfaceView;

	private CarDroiDuinoCore systemCore;

	private DatagramSocketClientGate socketClientGate;

	private SensorManager mSensorManager;

	private Sensor mAccelerometer;


	ImageButton btnForward;
	ImageButton bntBackward;
	ImageButton btnLeft;
	ImageButton btnRight;
	ImageButton bntLight;

	TextView xAxisValue;
	TextView yAxisValue;
	TextView zAxisValue;

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

		btnForward = (ImageButton) findViewById(R.id.btnForward);
		bntBackward = (ImageButton) findViewById(R.id.bntBackward);
		btnLeft = (ImageButton) findViewById(R.id.btnLeft);
		btnRight = (ImageButton) findViewById(R.id.btnRight);

		btnForward.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					commandArduino("http://192.168.100.102/?forward");

				else if (event.getAction() == MotionEvent.ACTION_UP)
					commandArduino("http://192.168.100.102/?stop");

				return true;

			}
		});

		bntBackward.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					commandArduino("http://192.168.100.102/?reverse");

				else if (event.getAction() == MotionEvent.ACTION_UP)
					commandArduino("http://192.168.100.102/?stop");

				return true;

			}
		});

		bntLight = (ImageButton) findViewById(R.id.btnLight);

		bntLight.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					ligarDesligarLanterna();

				return false;
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

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	private void start_counter() {
		final Handler handler = new Handler();

		btnForward.setEnabled(false);
		bntBackward.setEnabled(false);
		btnLeft.setEnabled(false);
		btnRight.setEnabled(false);
		bntLight.setEnabled(false);

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
		}, 3000);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int interval = 1;
				// Do something after 5s = 5000ms
				start_countdown(interval);
			}
		}, 5000);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int interval = 0;
				// Do something after 5s = 5000ms
				start_countdown(interval);
			}
		}, 7000);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int interval = 6;
				// Do something after 5s = 5000ms
				start_countdown(interval);
			}
		}, 9000);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int interval = 8;
				// Do something after 5s = 5000ms
				start_countdown(interval);
			}
		}, 9000);

		btnForward.setEnabled(true);
		bntBackward.setEnabled(true);
		btnLeft.setEnabled(true);
		btnRight.setEnabled(true);
		bntLight.setEnabled(true);

	}

	private void start_countdown(int interval) {

		ImageView NumberOne = (ImageView) findViewById(R.id.numberone);
		ImageView NumberTwo = (ImageView) findViewById(R.id.numbertwo);
		ImageView NumberThree = (ImageView) findViewById(R.id.numberthree);
		ImageView NumberGO = (ImageView) findViewById(R.id.numbergo);

		if (interval == 3) {
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
			NumberGO.setVisibility(View.VISIBLE);
		}

		else if (interval == 7) {
			NumberOne.setVisibility(View.INVISIBLE);
			NumberTwo.setVisibility(View.INVISIBLE);
			NumberThree.setVisibility(View.INVISIBLE);
			NumberGO.setVisibility(View.INVISIBLE);
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

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	public void onSensorChanged(SensorEvent event) {

		xAxisValue = (TextView) findViewById(R.id.xAxisValue);
		yAxisValue = (TextView) findViewById(R.id.yAxisValue);
		zAxisValue = (TextView) findViewById(R.id.zAxisValue);

		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		xAxisValue.setText(Float.toString(x));

		if (y > -2) {
			yAxisValue.setText("right");
			commandArduino("http://192.168.100.102/?right");
		} else if (y < 2) {
			yAxisValue.setText("left");
			commandArduino("http://192.168.100.102/?left");
		} else {
			yAxisValue.setText("servo");
			commandArduino("http://192.168.100.102/?servo");
		}
		zAxisValue.setText(Float.toString(z));

	}

}
