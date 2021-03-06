package groupa.deadlywheels.ui;

import groupa.deadlywheels.R;
import groupa.deadlywheels.db.CarDroiDuinoDbAdapter;
import groupa.deadlywheels.utils.SystemProperties;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * <p>
 * <b>Description:</b> <br>
 * <p>
 * Home screen of DeadlyWheel System - Start Menu to Choose the Purpose Device :
 * Car Remote Control Server or remote control
 * </p>
 * 
 */
public class CarDroidDuinoActivity extends Activity {

	/**
	 * Port for connection - So much for the Remote Control as to the Server Add
	 * to Car
	 */
	private String port = "5002";

	/**
	 * IP address - So much for Remote Control and for the Server on car
	 */
	private String ipAddress = "192.168.0.1";

	/**
	 * Helper to assist in connection to SQLLite
	 */
	private CarDroiDuinoDbAdapter carDroiDuinoDbAdapter;

	/**
	 * MAC Address of Bluetooth Modem Car
	 */
	private String modemBluetoothMACAddress;

	// ************************************************
	// Android device id
	String android_id = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// *************************************************
		// Performs first principal inherited class (Upper class - Extends )
		super.onCreate(savedInstanceState);

		// *************************************************
		// Makes the screen properties: fullscreen getting rib of the
		// status bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// *************************************************
		// Which by setting the Layout ( XML ) Activity associated with this - >
		// CarDroiDuino \ res \ layout
		setContentView(R.layout.principal_layout);

		// *************************************************
		// Starting the connection to the Database
		this.carDroiDuinoDbAdapter = new CarDroiDuinoDbAdapter(this);
		this.carDroiDuinoDbAdapter.open();

		// *************************************************
		// Checking if properties were created
		this.verifyBasicProperties();

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			android_id = extras.getString("EXTRA_ANDROID_ID");
		}

		Log.d("android id received by the intent", android_id);

		/*
		 * Button Opening Screen to perform in Car
		 */
		ImageButton btnOpenCarServer = (ImageButton) findViewById(R.id.btnCarro);
		btnOpenCarServer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openCarServerActivity();
			}
		});

		/*
		 * Button to Opening Screen Remote Control Cart
		 */
		ImageButton btnOpenCarControl = (ImageButton) findViewById(R.id.btnControle);
		btnOpenCarControl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { // openCarControlActivy();
				startCarControlActivity();
			}
		});

		/*
		 * Button to Opening Screen System Properties
		 */
		ImageButton btnOpenProperties = (ImageButton) findViewById(R.id.btnPropriedades);
		btnOpenProperties.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openPropertiesActivity();
			}
		});
	}

	/**
	 * Opens the Activity of the Car Server the Opening the event
	 * OnAtivityResult because it opens the Activity after getting the MAC
	 * address of the Modem Bluetooth car
	 */
	private void openCarServerActivity() {
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent,
					SystemProperties.REQUEST_CODE_ENABLE_BLUETOOTH);
		} else {
			// this.obtainClientIPAddressAndRequestPort();
			openDeviceBluetoothList();
		}
	}

	/**
	 * Opens the System Properties Activity
	 */
	private void openPropertiesActivity() {
		// *************************************************
		// Creating Object to send the intent to start the Screen
		// properties
		Intent i = new Intent(this, SystemPropertiesActivity.class);
		// *************************************************
		// Sending the Operating System the intent to start Activity
		startActivity(i);
	}

	/**
	 * Opens the list of Bluetooth devices to get the MAC address of the modem
	 * Shopping at The MAC address is returned in onActivityResult Event
	 */
	private void openDeviceBluetoothList() {
		// *************************************************
		// Creates the Intent object to show the intention to open the Activity

		Intent serverIntent = new Intent(this,
				DeviceBluetoothListActivity.class);
		// *************************************************
		// Sends the intent to open the screen for the Operating System
		startActivityForResult(serverIntent,
				SystemProperties.REQUEST_CODE_CONNECT_BLUETOOTH);
	}

	/**
	 * Event fired when the returned result If an Activity Returned the MAC
	 * address of the modem Bluetooth , initializes the Activity Servant Cart If
	 * Intent is returned from the available Bluetooth function again calls for
	 * opening the Server Car
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// *************************************************
		// Runs , first , the method of the Senior Class
		super.onActivityResult(requestCode, resultCode, data);
		// *************************************************
		// Activity verifies what was and what was the result returned by
		// Intent
		switch (requestCode) {
		// *************************************************
		// Return the Activity that lists the Bluetooth encounterer
		case SystemProperties.REQUEST_CODE_CONNECT_BLUETOOTH:
			if (resultCode == Activity.RESULT_OK) {
				// *************************************************
				// If it's an OK result then returned the MAC Address of the
				// To which the Device Server will connect
				this.modemBluetoothMACAddress = data.getExtras().getString(
						SystemProperties.KEY_DEVICE_ADDRESS);
				// *************************************************
				// Starts Activity Server
				startCarServerActivity();
			}
			break;
		// *************************************************
		// Return of Intent was inidicando Bluetooth Enabled
		case SystemProperties.REQUEST_CODE_ENABLE_BLUETOOTH:
			if (resultCode == Activity.RESULT_OK) {
				// *************************************************
				// If Bluetooth is activated then starts opening
				// Server Activity
				this.openCarServerActivity();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Opens the Screen Server Cart
	 * 
	 * @param port
	 */
	private void startCarServerActivity() {
		// *************************************************
		// Initializes fields of IP and Port
		this.loadIPAddressAndPort(
				SystemProperties.PROPERTIE_ID_IPADDRESS_CLIENT,
				SystemProperties.PROPERTIE_ID_PORT_CLIENT);
		// *************************************************
		// Creating Object to send the intent to start the Server
		// Car
		Intent i = new Intent(this, CarServerActivity.class);
		// *************************************************
		// Passing the Port Number of the server to send the frames via UDP
		// The Client
		i.putExtra(SystemProperties.KEY_PORT_NUMBER, this.port);
		// *************************************************
		// Passing the MAC Address of Bluetooth device for Activity
		i.putExtra(SystemProperties.KEY_DEVICE_ADDRESS,
				this.modemBluetoothMACAddress);
		// *************************************************
		// Passing the IP Address of the Client for sending frames via UDP
		i.putExtra(SystemProperties.KEY_IP_ADDRESS, this.ipAddress);

		// Sending the Operating System the intent to start Activity
		startActivity(i);
	}

	/**
	 * Opens the Screen Remote Control Cart
	 */
	private void startCarControlActivity() {
		// *************************************************
		// Initializes fields of IP and Port
		this.loadIPAddressAndPort(
				SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER,
				SystemProperties.PROPERTIE_ID_PORT_SERVER);
		// *************************************************
		// Creating Object to send the intent to start the Control ( Client )
		// Car
		Intent i = new Intent(this, CarControlActivity.class);
		// *************************************************
		// Passing the IP address for the client to send data to the command
		// Server
		i.putExtra(SystemProperties.KEY_IP_ADDRESS, this.ipAddress);
		// *************************************************
		// Passing the Door to Client send data to the Command Server
		i.putExtra(SystemProperties.KEY_PORT_NUMBER, this.port);
		// *************************************************
		// Sending the Operating System the intent to start Activity
		startActivity(i);
	}

	/**
	 * Carries the IP and port to be used - If opening activity server Then
	 * loads the Client IP and ports open if the customer activity IP and then
	 * loads the Server Ports
	 * 
	 * @param idIPaddress
	 * @param idPort
	 */
	private void loadIPAddressAndPort(int idIPaddress, int idPort) {
		Cursor cursor;
		// *************************************************
		// Carries the IP to be used
		cursor = this.carDroiDuinoDbAdapter.fetchPropertie(idIPaddress);
		this.ipAddress = cursor
				.getString(
						cursor.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP))
				.trim();

		cursor = null;
		// *************************************************
		// Loads the port to be used
		cursor = this.carDroiDuinoDbAdapter.fetchPropertie(idPort);
		this.port = cursor
				.getString(
						cursor.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP))
				.trim();
	}

	/**
	 * Checks if the properties were created in bsicas Bank Given If the Off -
	 * Then the Create with default values
	 */
	private void verifyBasicProperties() {

		// ************************************************
		// Viewing of the properties
		Cursor cursor = this.carDroiDuinoDbAdapter
				.fetchPropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER);

		// ************************************************
		// Check only one property
		if (cursor.getCount() == 0) {
			// ************************************************
			// Inserting properties IP Server
			this.carDroiDuinoDbAdapter.createPropertie(
					SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER,
					SystemProperties.PROPERTIE_DEFAUL_VAL_IPADDRESS_SERVER_1,
					" ");

			// ************************************************
			// Inserting properties IP Server
			this.carDroiDuinoDbAdapter.createPropertie(
					SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER,
					SystemProperties.PROPERTIE_DEFAUL_VAL_IPADDRESS_SERVER_1,
					" ");

			this.carDroiDuinoDbAdapter.createPropertie(
					SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER,
					SystemProperties.PROPERTIE_DEFAUL_VAL_IPADDRESS_SERVER_1,
					" ");

			// ************************************************
			// Inserting prop Server Port
			this.carDroiDuinoDbAdapter.createPropertie(
					SystemProperties.PROPERTIE_ID_PORT_SERVER,
					SystemProperties.PROPERTIE_DEFAUL_VAL_PORT_SERVER, " ");

			this.carDroiDuinoDbAdapter.createPropertie(
					SystemProperties.PROPERTIE_ID_IPADDRESS_CLIENT,
					SystemProperties.PROPERTIE_DEFAUL_VAL_IPADDRESS_CLIENT_1,
					" ");

			this.carDroiDuinoDbAdapter.createPropertie(
					SystemProperties.PROPERTIE_ID_IPADDRESS_CLIENT,
					SystemProperties.PROPERTIE_DEFAUL_VAL_IPADDRESS_CLIENT_1,
					" ");

			// ************************************************
			// Inserting prop Port Client
			this.carDroiDuinoDbAdapter.createPropertie(
					SystemProperties.PROPERTIE_ID_PORT_CLIENT,
					SystemProperties.PROPERTIE_DEFAUL_VAL_PORT_CLIENT, " ");
		}
	}
}
