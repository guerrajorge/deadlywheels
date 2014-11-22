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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

@SuppressLint("ClickableViewAccessibility")
public class CarControlActivity extends Activity implements SensorEventListener {

	// Camera's IP address
	private String serverIPAddress;

	private String clientServerPort;

	private FrameView surfaceView;

	private CarDroiDuinoCore systemCore;

	private DatagramSocketClientGate socketClientGate;


	

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
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
		

		btnFrente.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				
					if (event.getAction() == MotionEvent.ACTION_DOWN)
						commandArduino("http://192.168.100.107/?forward");

					else if (event.getAction() == MotionEvent.ACTION_UP)
						commandArduino("http://192.168.100.107/?stop");
				return true;

			}
		});

		btnRe.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
					if (event.getAction() == MotionEvent.ACTION_DOWN)
						commandArduino("http://192.168.100.107/?reverse");

					else if (event.getAction() == MotionEvent.ACTION_UP)
						commandArduino("http://192.168.100.107/?stop");
				
				return true;

			}
		});

		btnLeft.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					commandArduino("http://192.168.100.107/?left");

				else if (event.getAction() == MotionEvent.ACTION_UP)
					commandArduino("http://192.168.100.107/?servo");

				return true;

			}
		});

		btnRight.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					commandArduino("http://192.168.100.107/?right");

				else if (event.getAction() == MotionEvent.ACTION_UP)
					commandArduino("http://192.168.100.107/?servo");

				return true;

			}
		});

		ImageButton btnLanterna = (ImageButton) findViewById(R.id.btnFarol);

		btnLanterna.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					ligarDesligarLanterna();

				return false;
			}
		});

		this.surfaceView = (FrameView) findViewById(R.id.surfaceControl);

		this.setupClient();

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


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
