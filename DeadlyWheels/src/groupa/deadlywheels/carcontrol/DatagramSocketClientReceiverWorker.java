package groupa.deadlywheels.carcontrol;

import groupa.deadlywheels.core.CarDroiDuinoCore;
import groupa.deadlywheels.utils.SystemProperties;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.util.Log;

/**
 * 
 * <p>
 * <b>Description:</b> <br>
 * 
 * <p>
 * 
 * Class that implements the Worker had received the frames of Video Server and
 * sent them to the screen , through the Core System , Into Frames to display
 * the User in Real Time
 * 
 * </p>
 */
public class DatagramSocketClientReceiverWorker implements Runnable {

	/**
	 * Core System - Prov Queues for Data Exchange between System Threads
	 */
	private CarDroiDuinoCore systemCore;

	/**
	 * Socket for connection via UPD with Server and receipt of Frames
	 */
	private DatagramSocket datagramSocket;

	/**
	 * Looping Control of Thread
	 */
	private boolean isOn = false;

	/**
	 * Booting the worker to receive the video data coming via TCP / IP
	 * 
	 * @Param systemCore Core System
	 * @Param port ClientPort Client for Receiving Frames
	 * @throws SocketExceptio
	 */
	public DatagramSocketClientReceiverWorker(CarDroiDuinoCore systemCore,
			int clientPort) throws SocketException {
		this.systemCore = systemCore;
		// **********************************************
		// Booting Datagram connection
		this.datagramSocket = new DatagramSocket(clientPort);
		// **********************************************
		this.isOn = true;
	}

	/**
	 * Starts Receiving the frames via UDP Socket and forwards them to the Core
	 * System to be Captured by the thread that draws the frame in View
	 */
	public void run() {
		while (this.isOn) {
			try {
				// ********************************************
				// Creates Datagram Package Receiving Frame

				DatagramPacket packetFrame = new DatagramPacket(
						new byte[SystemProperties.BUFFER_FRAME_RECEIVER],
						SystemProperties.BUFFER_FRAME_RECEIVER);
				// *********************************************
				// Receiving Frame Video
				this.datagramSocket.receive(packetFrame);
				// *********************************************
				// Creating the byte array to receive the Frame Buffer
				byte[] byteFrame = new byte[packetFrame.getLength()];
				// *********************************************
				// Paste the Frame Buffer for definitive Array
				System.arraycopy(packetFrame.getData(), 0, byteFrame, 0,
						packetFrame.getLength());
				// *********************************************
				// Sending frame for the the Entry Queue Core System
				this.systemCore.addDataToCameraQueue(byteFrame);
				// *********************************************
			} catch (IOException e) {
				Log.e("DatagramSocketClientReceiverWorker - IOException",
						e.getMessage());
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_SOCKET_RECEIVERS);
				} catch (InterruptedException e1) {
					Log.e("DatagramSocketClientReceiverWorker - InterruptedException",
							e1.getMessage());
				}
			} catch (Exception e) {
				Log.e("DatagramSocketClientReceiverWorker - Exception",
						e.getMessage());
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_SOCKET_RECEIVERS);
				} catch (InterruptedException e1) {
					Log.e("DatagramSocketClientReceiverWorker - InterruptedException",
							e1.getMessage());
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
			Log.e("DatagramSocketClientReceiverWorker - Exception",
					ex.getMessage());
		}
	}
}
