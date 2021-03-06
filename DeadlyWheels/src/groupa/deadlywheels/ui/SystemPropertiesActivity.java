package groupa.deadlywheels.ui;

import groupa.deadlywheels.R;
import groupa.deadlywheels.db.CarDroiDuinoDbAdapter;
import groupa.deadlywheels.utils.SystemProperties;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * <p>
 * <b>Description:</b> <br>
 * <p>
 * Display the System Properties - Has the main properties for Communication
 * between the client and server elements
 * 
 * 
 */
public class SystemPropertiesActivity extends Activity {

	/**
	 * Server IP field ( Anbdroid Car)
	 */
	private EditText txtIPServer;

	/**
	 * Server Port field
	 */
	private EditText txtPortServer;

	/**
	 * Client IP field (Remote Control )
	 */
	private EditText txtIPClient;

	/**
	 * Helper to manage the connection SqlLite
	 */
	private CarDroiDuinoDbAdapter carDroiDuinoDbAdapter;

	/**
	 * Port field Car
	 */
	private EditText txtPortClient;

	/**
	 * Executed method to initialize the class
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// *************************************************
		// Which by setting the Layout ( XML ) Activity associated with this - >
		// CarDroiDuino \ res \ layout
		setContentView(R.layout.properties_layout);

		// *************************************************
		// Capturing and setting screen objects
		this.txtIPServer = (EditText) findViewById(R.id.txtIPServer);
		this.txtPortServer = (EditText) findViewById(R.id.txtPortServer);
		this.txtIPClient = (EditText) findViewById(R.id.txtIPClient);
		this.txtPortClient = (EditText) findViewById(R.id.txtPortClient);
		Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
		btnSalvar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// ******************************************
				// Listener button - Saves the data and closes the window to the
				// click
				// In Boto
				saveFields();
				finish();
			}
		});

		// *************************************************
		// Initializing connection to the Database
		this.carDroiDuinoDbAdapter = new CarDroiDuinoDbAdapter(this);
		this.carDroiDuinoDbAdapter.open();

		// *************************************************
		// populating fields
		this.populateFields();
	}

	/**
	 * Populates the fields to display the registered data
	 */
	private void populateFields() {

		Cursor cursor;
		// *************************************************
		// Seeking IP Server

		cursor = this.carDroiDuinoDbAdapter
				.fetchPropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER);
		this.txtIPServer.setText(cursor.getString(cursor
				.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)));

		cursor = null;
		// *************************************************
		// Seeking Port Server
		cursor = this.carDroiDuinoDbAdapter
				.fetchPropertie(SystemProperties.PROPERTIE_ID_PORT_SERVER);

		this.txtPortServer.setText(cursor.getString(cursor
				.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)));

		cursor = null;
		// *************************************************
		// Seeking IP Client
		cursor = this.carDroiDuinoDbAdapter
				.fetchPropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_CLIENT);

		this.txtIPClient.setText(cursor.getString(cursor
				.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)));

		cursor = null;
		// *************************************************
		// Seeking Client port
		cursor = this.carDroiDuinoDbAdapter
				.fetchPropertie(SystemProperties.PROPERTIE_ID_PORT_CLIENT);

		this.txtPortClient.setText(cursor.getString(cursor
				.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)));
	}

	/**
	 * Saves the edited fields
	 */
	private void saveFields() {
		// *************************************************
		// Saving the IP Server
		this.carDroiDuinoDbAdapter.updatePropertie(
				SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER,
				this.txtIPServer.getText().toString());

		// *************************************************
		// Saving Port Server
		this.carDroiDuinoDbAdapter.updatePropertie(
				SystemProperties.PROPERTIE_ID_PORT_SERVER, this.txtPortServer
						.getText().toString());

		// *************************************************
		// Saving the IP Client
		this.carDroiDuinoDbAdapter.updatePropertie(
				SystemProperties.PROPERTIE_ID_IPADDRESS_CLIENT,
				this.txtIPClient.getText().toString());

		// *************************************************
		// Saving the Client Port
		this.carDroiDuinoDbAdapter.updatePropertie(
				SystemProperties.PROPERTIE_ID_PORT_CLIENT, this.txtPortClient
						.getText().toString());

	}

}
