package groupa.deadlywheels.core;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <p>
 * <b>Description:</b> <br>
 * <p>
 * Core System CarDroiDuino - Has internal queues for sharing data between
 * threads of this module - Data Queue the camera - > Sharing of Data Captured
 * by the camera Video Server - Data Queue Control Car - > Sharing Data Control
 * Cart ( Client )
 * </p>
 * 
 */
public class CarDroiDuinoCore {

	/**
	 * Queue for Sharing of Data Captured Video of camera
	 */
	private ConcurrentLinkedQueue<byte[]> filaCameraVideo;

	/**
	 * Queue for Sharing Data Control Car
	 */
	private ConcurrentLinkedQueue<byte[]> filaControleCarro;

	/**
	 * Queue for Sharing Internal Commands (for control between Android devices
	 * )
	 */
	private ConcurrentLinkedQueue<byte[]> filaComandosInternos;

	/**
	 * Constructor to initialize the Queue
	 */
	public CarDroiDuinoCore() {
		this.filaCameraVideo = new ConcurrentLinkedQueue<byte[]>();
		this.filaControleCarro = new ConcurrentLinkedQueue<byte[]>();
		this.filaComandosInternos = new ConcurrentLinkedQueue<byte[]>();
	}

	/**
	 * Adds the Data Obtained from camera Queue Sharing
	 * 
	 * @Param data - data the device camera
	 * @return True if the given added Queue
	 */
	public synchronized boolean addDataToCameraQueue(byte[] data) {
		return this.filaCameraVideo.add(data);
	}

	/**
	 * Adds the Data Control Car Queue Sharing
	 * 
	 * @Param date - Data received by the Control Car
	 * @return True if the given added Queue
	 */
	public synchronized boolean addDataToCarControlQueue(byte[] data) {
		return this.filaControleCarro.add(data);
	}

	/**
	 * Adds Data Internal Controls Internal Queue Commands Android devices
	 * 
	 * @param data
	 *            - Data received by Datagram Receiver Worker
	 * @return True if the data added to Queue
	 */
	public synchronized boolean addDataToInternalCommandQueue(byte[] data) {
		return this.filaComandosInternos.add(data);
	}

	/**
	 * Removes the data from the camera Video Sharing Queue
	 * 
	 * @return Video stored
	 */
	public synchronized byte[] poolDataFromCameraQueue() {
		return this.filaCameraVideo.poll();
	}

	/**
	 * Removes the Control Data Car Queue Sharing Given to control return Car
	 */
	public synchronized byte[] poolDataFromCarControlQueue() {
		return this.filaControleCarro.poll();
	}

	/**
	 * Removes Data Internal Controls Internal Queue Commands Android devices
	 * Data return internal commands of Android Devices
	 */
	public synchronized byte[] poolDataFromInternalCommandQueue() {
		return this.filaComandosInternos.poll();
	}
}
