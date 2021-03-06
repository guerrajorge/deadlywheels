package groupa.deadlywheels.carcontrol;

import groupa.deadlywheels.core.CarDroiDuinoCore;
import groupa.deadlywheels.utils.SystemProperties;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.util.Log;

/**
 * 
 * <p>
 * <b>Description:</b> <br>
 * 
 * <p>
 * 
 * Class that implements the Worker Thread that send the Control Data through
 * UDP to Car
 * 
 * </p>
 */
public class DatagramSocketClientSenderWorker implements Runnable {

	/**
	 * Core System - Prov Queues for Data Exchange between the Threads System
	 */
	private CarDroiDuinoCore systemCore;

	/**
	 * IP address of the Server for sending commands to Car
	 */
	private String serverIPAddress;

	/**
	 * Server port for sending commands to Car
	 */
	private int serverPort;

	/**
	 * Socket for sending UDP frames
	 */
	private DatagramSocket datagramSocket;

	/**
	 * Looping Control of Thread
	 */
	private boolean isOn = false;

	/**
	 * Initializes the Worker for sending Control Data Cart
	 * 
	 * @Param systemCore Core System
	 * @Param ServerIPAddress IP Server for sending commands
	 * @Param serverPort Server port for sending commands throws SocketException
	 */
	public DatagramSocketClientSenderWorker(CarDroiDuinoCore systemCore,
			String serverIPAddress, int serverPort) throws SocketException {
		this.systemCore = systemCore;
		this.serverIPAddress = serverIPAddress;
		this.serverPort = serverPort;
		// *******************************************
		// Initializing the connection for sending frames
		this.datagramSocket = new DatagramSocket();
		// *******************************************
		this.isOn = true;
	}

	/**
	 * Starts the process of withdrawal Data Commands Core and sends through the
	 * Socket connection ( UDP ) Server to Car You're stuck on looping until the
	 * thread is finished
	 */
	public void run() {
		while (this.isOn) {
			// *********************************************
			// Removes the given Command Car
			byte[] commandData = this.systemCore.poolDataFromCarControlQueue();
			if (commandData != null) {
				try {
					// *******************************************
					// Riding the datagram packet for sending Command
					DatagramPacket datagramPacketCommand = new DatagramPacket(
							commandData, commandData.length,
							InetAddress.getByName(this.serverIPAddress),
							this.serverPort);
					// *******************************************
					// Sending Command via UDP
					this.datagramSocket.send(datagramPacketCommand);
					// *******************************************
				} catch (IOException e) {
					Log.e("DatagramSocketClientSenderWorker - send",
							e.getMessage());
				}
			} else {
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_SOCKET_SENDERS);
				} catch (InterruptedException e) {
					Log.e("DatagramSocketClientSenderWorker - InterruptedException",
							e.getMessage());
				}
			}
		}
	}

	/**
	 * Thread the ends of communication
	 */
	public void turnOff() {
		this.isOn = false;
		try {
			this.datagramSocket.disconnect();
			this.datagramSocket.close();
		} catch (Exception ex) {
			Log.e("DatagramSocketClientSenderWorker - Exception",
					ex.getMessage());
		}
	}
}
