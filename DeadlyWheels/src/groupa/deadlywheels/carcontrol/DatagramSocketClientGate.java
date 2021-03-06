package groupa.deadlywheels.carcontrol;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import groupa.deadlywheels.core.CarDroiDuinoCore;

/**
 * 
 * <p>
 * <b>Description:</b> <br>
 * 
 * <p>
 * Class that implements the Gate of communication via UPD ( Socket ) for Remote
 * Cart Starts Working Threads for sending commands and receiving of video
 * frames
 * </p>
 * 
 */
public class DatagramSocketClientGate {

	/**
	 * Core System for Sharing Data Between Threads
	 */
	private CarDroiDuinoCore systemCore;

	/**
	 * IP address of the server for connection Cart
	 */
	private String serverIPAddress;

	/**
	 * Port that will be used by the Client to receive the video frames Even
	 * port number used by the Server for receiving control data
	 */
	private int clientServerPort;

	/**
	 * Status of the Gate
	 */
	private boolean isInitialized = false;

	/**
	 * Worker responsible for, the receipt of frames and video routing Of the
	 * Core System
	 */
	private DatagramSocketClientReceiverWorker datagramSocketClientReceiverWorker;

	/**
	 * Thread that will trigger the Worker Receiving
	 */
	private Thread datagramSocketClientReceiverThread;

	/**
	 * Thread that will trigger the Worker Receiving
	 */
	private Thread datagramSocketArduinoThread;

	/**
	 * Worker responsible for sending the Data Control to Cart Server through
	 * the core
	 */
	private DatagramSocketClientSenderWorker datagramSocketClientSenderWorker;

	/**
	 * Thread that will trigger the Worker Shipping
	 */
	private Thread datagramSocketClientSenderThread;

	/**
	 * Creates a new instance the SocketClientGate
	 * 
	 * @Param systemCore Core System
	 * @Param ServerIPAddress IP Address Server
	 * @Param clientServerPort Port for sending data to the Receiving Server and
	 *        Frames
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public DatagramSocketClientGate(CarDroiDuinoCore systemCore,
			String serverIPAddress, int clientServerPort)
			throws UnknownHostException, IOException {
		// *************************************************
		// Setting the Gate
		this.systemCore = systemCore;
		this.serverIPAddress = serverIPAddress;
		this.clientServerPort = clientServerPort;
		// *************************************************
		// Making the initialization of Threads
		this.setupSocketGate();
	}

	/**
	 * Initializes the connection TCP / IP and the Workers ( Workers ) to E
	 * Receive data from the Server Cart
	 * 
	 * @throws SocketException
	 */
	private void setupSocketGate() throws SocketException {
		// *************************************************
		// If the connection was made , then creates the object of that Worker
		// Thread
		// Receive frames Video
		this.datagramSocketClientReceiverWorker = new DatagramSocketClientReceiverWorker(
				this.systemCore, this.clientServerPort);

		// *************************************************
		// Object creates the worker thread that sent the data Control the Car
		this.datagramSocketClientSenderWorker = new DatagramSocketClientSenderWorker(
				this.systemCore, this.serverIPAddress, this.clientServerPort);

		// *************************************************
		// Starts Threads for them to begin their work
		this.datagramSocketClientReceiverThread = new Thread(
				this.datagramSocketClientReceiverWorker);
		this.datagramSocketClientReceiverThread.start();

		this.datagramSocketClientSenderThread = new Thread(
				this.datagramSocketClientSenderWorker);
		this.datagramSocketClientSenderThread.start();

		// *************************************************
		// Flag Status for the Gate Initialized
		this.isInitialized = true;
	}

	/**
	 * Return the status of the Gate
	 * 
	 * @return Status Gate
	 */
	public boolean isSocketGateInitialized() {
		return this.isInitialized;
	}

	/**
	 * Killing off the Gate Threads Workers
	 */
	public void turnOff() {
		// **************************************************
		// Checks if the was finished or even been initialized
		if (!this.isInitialized)
			return;

		// **************************************************
		// Sets the flag to the boot to prevent null exception
		this.isInitialized = false;

		// **************************************************
		// Control to the Finish Threads
		boolean retry = true;
		// **************************************************
		// Ends of the loops Threads
		this.datagramSocketClientReceiverWorker.turnOff();
		this.datagramSocketClientSenderWorker.turnOff();
		// **************************************************
		// Try to kill the thread receiving the knife !!!
		while (retry) {
			try {
				this.datagramSocketClientReceiverThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
		// **************************************************
		// Reusing Flag-- This wrong ... but to do what ?
		retry = true;
		// **************************************************
		// Try to kill the Thread shipping in bullet !!!!
		while (retry) {
			try {
				this.datagramSocketClientSenderThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}

		// **************************************************
		// Reusing Flag-- This wrong ... but to do what ?
		retry = true;
		// **************************************************
		// Try to kill the Thread shipping in bullet !!!!
		while (retry) {
			try {
				this.datagramSocketArduinoThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
