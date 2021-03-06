package groupa.deadlywheels.ui;

import java.util.Set;

import groupa.deadlywheels.R;
import groupa.deadlywheels.utils.SystemProperties;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * <p>
 * <b>Description:</b> <br>
 * <p>
 * This Activity appears as a dialog. The paired devices list And detected after
 * the discovery . When a selected device By the user 's MAC address of the
 * device returned to the Activity They 're called , the result Intent
 * </p>
 */
public class DeviceBluetoothListActivity extends Activity {

	/**
	 * Constants for Debugging the Application
	 */
	private static final String TAG = "DeviceListActivity";
	private static final boolean D = true;

	/**
	 * Hold the object to Bluetooth Adapter
	 */
	private BluetoothAdapter mBtAdapter;

	/**
	 * Array Adapter that will contain the MAC addresses of the devices paired
	 */
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;

	/**
	 * Array Adapter that will contain the new MAC addresses of devices found by
	 * Search Bluetooth
	 */
	private ArrayAdapter<String> mNewDevicesArrayAdapter;

	/**
	 * First method to run after the constructor
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// *************************************************
		// Performs first omtodo inherited class (Upper class - extends )
		super.onCreate(savedInstanceState);

		// *************************************************
		// Performs the Setup Activity of determining the characteristic of the screen
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// *************************************************
		// Which by setting the Layout ( XML ) associated with this Activity -->
		// CarDroiDuino\res\layout
		setContentView(R.layout.bluedevicelist_layout);

		// *************************************************
		// Return CANCELED by setting as Default as to if the user exit
		// of
		// Activity without selecting a MAC Address
		setResult(Activity.RESULT_CANCELED);

		// *************************************************
		// Booting the button to go booting the discovery of new
		// devices
		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// *************************************************
				// Routine calls looking for new devices
				doDiscovery();
				// *************************************************
				// Some with the progress screen
				v.setVisibility(View.GONE);
			}
		});

		// *************************************************
		// Starting ArrayAdapters to contain the MAC Address of
		// Devices
		// A j to paired devices and other devices to
		// found
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);

		// *************************************************
		// Getting the ListView to go list paired devices and
		// associandor
		// The ArrayAdapter for him
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		// *************************************************
		// Associates the act of the user touching the line of the ListView to an event
		// specific 
		// To capture the selected MAC address
		pairedListView.setOnItemClickListener(mDeviceClickListener);

		// *************************************************
		// Getting the ListView to go and list the devices found
		// associandor
		// The ArrayAdapter for him
		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		// *************************************************
		// Associates the act of the user touching the line of the ListView to an event
		// specific 
		// To capture the selected MAC address
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		// *************************************************
		// Register to run the BroadCastReceiver when a new
		// device is
		// encontraado
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// *************************************************
		// Register to run the BroadCastReceiver when Bluetooth
		// end
		// search for devices
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		// *************************************************
		// Capturing the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		// *************************************************
		// Capturing the list of devices already paired with the adapter
		// bluetooth
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		// *************************************************
		// If you have paired devices picks up one by one and adds to
		// Of ArrayAdapter
		// Devices already paired. The contrary shoves a String saying
		// Has anyone paired in
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				mPairedDevicesArrayAdapter.add(device.getName() + "\n"
						+ device.getAddress());
			}
		} else {
			String noDevices = getResources().getText(R.string.none_paired)
					.toString();
			mPairedDevicesArrayAdapter.add(noDevices);
		}
	}

	/**
	 * Event fired when the Activity destroyed For the discovery of Bluetooth
	 * and unset the BroadCast who received messages System Operational
	 */
	@Override
	protected void onDestroy() {
		// *************************************************
		// Performs first omtodo upper class ( extends )
		super.onDestroy();

		// *************************************************
		// If you're still discovering devices then the whole process
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}

		// *************************************************
		// Unregister BroadcastReceiver that received the messages System
		// Operating 
		this.unregisterReceiver(mReceiver);
	}

	/**
	 * Starts the discovery of Bluetooth devices around
	 */
	private void doDiscovery() {
		if (D)
			Log.d(TAG, "doDiscovery()");

		// *************************************************
		// Enable progress bar to show that user is
		// Trying to discover devices
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);

		// *************************************************
		//Displaying the Title of New devices for new ListView
		// devices
		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		// *************************************************
		// If the discovery is already rolling then for him
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}

		// *************************************************
		// Asks the adapter to start the hunt devices around
		mBtAdapter.startDiscovery();
	}

	/**
	 * Event fired when the user selects any one of the devices The two lists
	 * devices - Get the MAC address of the device Selected and returns to
	 * CarServerActivity
	 */
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// *************************************************
			// Cancels the discovery
			mBtAdapter.cancelDiscovery();

			// *************************************************
			// Get the MAC Address String contained in the selected
			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);

			// *************************************************
			// Intent creates the result and adds the MAC address to selected
			Intent intent = new Intent();
			intent.putExtra(SystemProperties.KEY_DEVICE_ADDRESS, address);

			// *************************************************
			// Indicates that the result is valid to be sent - OK
			setResult(Activity.RESULT_OK, intent);

			// *************************************************
			// Terminate this Activity sending returns CarServerActivity
			finish();
		}
	};

	/**
	 * Event fired when a new device found or when the Discovering new devices
	 * finalized
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// *************************************************
			// Capture the action triggered by the identification system
			String action = intent.getAction();

			// *************************************************
			// If the event is the discovery of a new device ...
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// *************************************************
				// Handle the device informed by Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// *************************************************
				// If the device is still on the paired then he adds
				// The ArrayAdapter of discovered devices
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
				// *************************************************
				// If the event is to inform the order of discovery for new
				// Devices ...
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				// ************************************************ *
				// Some // with Progress Bar
				setProgressBarIndeterminateVisibility(false);
				// ************************************************ *
				// Change the title to ask the user to select one
				// device
				setTitle(R.string.select_device);
				// ************************************************ *
				// If the device was discovered some then puts a
				// Message informing ListView
				// That the search turned up nothing
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					String noDevices = getResources().getText(
							R.string.none_found).toString();
					mNewDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

}
