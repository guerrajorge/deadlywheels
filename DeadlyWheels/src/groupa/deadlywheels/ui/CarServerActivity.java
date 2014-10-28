package groupa.deadlywheels.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
//import java.util.Vector;

import groupa.deadlywheels.R;
import groupa.deadlywheels.carserver.DatagramSocketServerGate;
import groupa.deadlywheels.carserver.InternalCommandServerGate;
import groupa.deadlywheels.core.CarDroiDuinoCore;
import groupa.deadlywheels.utils.SystemProperties;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.ScrollView;

/**
 * <p>
 * <b>Description:</b> <br>
 * <p>
 * Screen System Server module -Screen to be performed on the device coupled to
 * cart to be controlled It features : -Interface with camera Android Device
 * -Connected Socket For Receiving and Sending Data to the Client (Remote
 * Control) -Bluetooth Interface with Arduino Controlled Car -Resolver
 * initialization of Internal Controls
 * </p>
 * 
 */
public class CarServerActivity extends Activity implements
		SurfaceHolder.Callback {

	/**
	 * IP Address of Remote Control Car
	 */
	private String clientIPAddress;

	/**
	 * Delivered by Server Port for Receiving Command Even Port number provided
	 * by the Client for sending frames
	 */
	private String clientServerPort;

	/**
	 * Core for Sharing Data Between Threads
	 */
	private CarDroiDuinoCore systemCore;

	/**
	 * Gate for communication TCP / IP with the Remote Control
	 */
	private DatagramSocketServerGate socketServerGate;

	/**
	 * Gate processing Internal Commands Android Device
	 */
	private InternalCommandServerGate internalCommandServerGate;

	/**
	 * Camera of the mobile device
	 */
	private Camera mCamera;

	/**
	 * Prompt to display Status
	 */
	private EditText mTxtPrompt;

	/**
	 * Scroll to the Prompt
	 */
	private ScrollView mScrPrompt;

	/**
	 * Surface view to show the camera image ( Preview)
	 */
	private SurfaceView surfaceView;

	/**
	 * Surface container to contain the image of the camera
	 */
	private SurfaceHolder surfaceHolder;

	/**
	 * Check if the camera is getting the open frames
	 */
	private boolean isPreviewRunning = false;

	/**
	 * Check that the threads have already been initialized
	 */
	private boolean isThreadsInitialided = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// *************************************************
		// Performs first the method inherited class (Upper class -
		// Extends )
		super.onCreate(savedInstanceState);

		// *************************************************
		// configuration to capture the camera
		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		// *************************************************
		// Which by setting the Layout ( XML ) Activity associated with this - >
		// CarDroiDuino \ res \ layout
		setContentView(R.layout.car_layout);

		// *************************************************
		// Getting to the port available for connection and the MAC Address of
		// the
		// Module
		// Bluetooth Arduino - These data were sent by Class
		// CarDroiDuinoActivity
		this.clientServerPort = getIntent().getExtras().getString(
				SystemProperties.KEY_PORT_NUMBER);
		getIntent().getExtras().getString(SystemProperties.KEY_DEVICE_ADDRESS);
		this.clientIPAddress = getIntent().getExtras().getString(
				SystemProperties.KEY_IP_ADDRESS);
		// *************************************************

		// *************************************************
		// Booting Surface to show the Camera Preview
		this.surfaceView = (SurfaceView) findViewById(R.id.surface);
		this.surfaceHolder = this.surfaceView.getHolder();
		this.surfaceHolder.addCallback(this);
		// *************************************************

		// *************************************************
		// Capturing the text and scroll to use as Prompt Messaging
		this.mTxtPrompt = (EditText) findViewById(R.id.txtPrompt);
		this.mScrPrompt = (ScrollView) findViewById(R.id.scrPrompt);

		// *************************************************
		// Initialize of the server of the car
		this.setupServer();
	}

	/**
	 * Starts the Gate to go run and retain workers Threads Submission Frames of
	 * the Camera , Receive Data Command stand and Sending data via Bluetooth to
	 * command the Arduino
	 */
	private void setupServer() {
		// *************************************************
		// Initializes the Core System to prepare the queues to exchange
		// Data between Threads
		this.systemCore = new CarDroiDuinoCore();
		try {
			// *************************************************
			// Starts the gate to go Connect via Bluetooth module to Arduino
			// And send
			// Commands arriving Queue Commands for Controlling the Core
			// Cart
			// TODO : Comment / non comment here
			// This.bluetoothGate = new BluetoothGate ( this.systemCore ,
			// This.modemBluetoothMACAddress , my handler ) ;
			// ************************************************ *
			// Starts Gate that will create and manage threads Submission
			// Camera and Frames
			// Receiving Data Control Via TCP / IP
			this.socketServerGate = new DatagramSocketServerGate(
					this.systemCore, this.clientIPAddress,
					Integer.parseInt(this.clientServerPort), this.mHandler);
			// *************************************************
			// GATE OF INTERNAL CONTROLS S CREATED AFTER THE CAMERA BE
			// Initialized
			// *************************************************
		} catch (Exception e) {
			new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
		}

		// ************************************************
		// Of time for which the Client uninitialize their Threads
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// *************************************************
		// Stating that everything was initialized
		this.isThreadsInitialided = true;
	}

	/**
	 * Write handler for messages to Thread graphics System for Visualization by
	 * the user
	 */
	@SuppressLint("HandlerLeak") private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String txt = mTxtPrompt.getText().toString();

			// ****************************************
			// If you pass 100
			if (mTxtPrompt.getLineCount() > 100)
				txt = "";

			txt += "\n" + (String) msg.obj;
			/*
			 * Show in the post Received Prompt
			 */
			mTxtPrompt.setText(txt);

			/*
			 * Scroll down
			 */
			mScrPrompt.post(new Runnable() {
				public void run() {
					mScrPrompt.smoothScrollTo(0, mTxtPrompt.getBottom());
				}
			});
		}
	};

	/**
	 * Event triggered whenever a frame is captured by the camera Preview Upon
	 * reaching the frame he compressed JPG in order to be sent via TCP / IP
	 * Client to the car - Then he placed in Queue Core Sender Thread able to
	 * capture it and send it
	 */
	Camera.PreviewCallback mPreviewCallBack = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (systemCore != null && socketServerGate != null
					&& socketServerGate.isSocketGateInitialized()
					&& isThreadsInitialided) {
				// *************************************************
				// Compressing and Uploading the frame to the Core System
				systemCore.addDataToCameraQueue(convertImageToJPEG(data));
			}
		}
	};

	/**
	 * Converts the image format frame camera ( NV21 ) to JPEG
	 * 
	 * @Param date Image Captured on camera by Format
	 * @return Formtato JPEG Image
	 */
	private byte[] convertImageToJPEG(byte[] data) {

		// *************************************************
		// Capturing the Width and Height of the image to make the current
		// conversion
		Camera.Parameters parameters = mCamera.getParameters();
		int w = parameters.getPreviewSize().width;
		int h = parameters.getPreviewSize().height;

		// *************************************************
		// Transforms the image into an object of type YuvImage because this
		// object
		// Has a method for JPEG images to conveter
		YuvImage yuv_image = new YuvImage(data, parameters.getPreviewFormat(),
				w, h, null);

		// *************************************************
		// Creates the object from a rectangle to set the image that will be
		// converted
		Rect rect = new Rect(0, 0, w, h);

		// *************************************************
		// Creates able to contain the converted image an object and returns it
		// as
		// An Array of Bytes
		ByteArrayOutputStream output_stream = new ByteArrayOutputStream();

		// *************************************************
		// Compressing Image - The result ( in JPEG Frame ) be returned
		// within
		// The outputStream
		yuv_image.compressToJpeg(rect, 30, output_stream);

		// *************************************************
		// Returned the converted image
		return output_stream.toByteArray();
	}

	/**
	 * Event triggered when a change occurs in the Surface , such as The
	 * Rotating Screen - Makes the rest of the Camera Setup of this method
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		// *************************************************
		// If the camera is running , so to be able to Preview
		// Set it
		if (isPreviewRunning) {
			mCamera.stopPreview();
		}

		// *************************************************
		// Obtain few configuration parameters of the Camera
		Camera.Parameters p = mCamera.getParameters();
		// *************************************************
		// HERE WE WILL CAPTURE CONFIGURATION HOW MANY FRAMES PER SECOND THE
		// CAMERA
		// p.setPreviewFrameRate(5);
		// p.setPreviewFormat(PixelFormat.JPEG);
		p.setJpegQuality(1);
		// p.setPreviewSize(480, 320);
		p.setPreviewSize(240, 160);
		// *************************************************
		// Set configured parameters
		mCamera.setParameters(p);

		try {
			// *************************************************
			// Informa is Surface Camera Which she will use to show Its // Preview
			mCamera.setPreviewDisplay(holder);
			// *************************************************
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "surfaceChanged - Exception: "
					+ e.getMessage());
			e.printStackTrace();
		}
		// *************************************************
		// Resets the camera to her return to capture images of its Preview
		mCamera.startPreview();
		// *************************************************
		// Set Flag to indicate that the camera is running
		isPreviewRunning = true;
		// *************************************************
		// INFORMATION HEREIN WILL RECEIVE THE OBJECT THAT CAPTURED BY THE FRAMES
		// CAMERA
		// THIS OBJECT IS SET UP SOON
		mCamera.setPreviewCallback(mPreviewCallBack);

		// **************************************************
		// BOOTS THE GATE THAT MANAGES AND HANDLES the resolver COMMAND
		// INTERNAL
		// **************************************************
		this.internalCommandServerGate = new InternalCommandServerGate(
				this.systemCore, this.mHandler, this.mCamera);
		// **************************************************
	}

	/**
	 * Event fired when the Surface created . She gets the Camera Device To be
	 * able to handle it
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	/**
	 * Event triggered when destroyed Surface - For the execution of the Camera
	 * And the other releases for Software
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// *********************************************
		// Killing Threads
		this.killGates();
		// *********************************************
		// destroying objects
		mCamera.stopPreview();
		mCamera.setPreviewCallback(null);
		mCamera.release();
		mCamera = null;
		isPreviewRunning = false;
	}

	/**
	 * Kills Threads of Gates
	 */
	private void killGates() {
		// *********************************************
		// Killing of Server Threads
		this.internalCommandServerGate.turnOff();
		this.socketServerGate.turnOff();
	}
}