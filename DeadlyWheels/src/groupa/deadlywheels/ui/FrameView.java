package groupa.deadlywheels.ui;

import groupa.deadlywheels.core.CarDroiDuinoCore;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * <p>
 * <b>Description:</b> <br>
 * Implements an object that inherits from FrameView SurfaceView to Generate
 * inside Own a Thread that will capture the frames and Core Show user pro
 * <p>
 * 
 */
public class FrameView extends SurfaceView implements SurfaceHolder.Callback {
	class FrameThread extends Thread {

		/**
		 * Holder to Capture Canvas
		 */
		private SurfaceHolder surfaceHolder;

		/**
		 * Core System
		 */
		private CarDroiDuinoCore systemCore;

		/**
		 * Control looping Thread
		 */
		private boolean isOn = false;

		/**
		 * 
		 * @param surfaceHolder
		 * @param systemCore
		 */
		public FrameThread(SurfaceHolder surfaceHolder) {
			this.surfaceHolder = surfaceHolder;
		}

		/**
		 * Informs the Core for Thread access to Queue frames that Arriving via
		 * TCP / IP
		 * 
		 * @param systemCore
		 */
		public void setSystemCore(CarDroiDuinoCore systemCore) {
			this.systemCore = systemCore;
		}

		/**
		 * Informs the Core System set to Thread
		 * 
		 * @return
		 */
		public CarDroiDuinoCore getSystemCore() {
			return this.systemCore;
		}

		/**
		 * enable / disable the looping thread
		 * 
		 * @param onOff
		 */
		public void setOnOff(boolean onOff) {
			this.isOn = onOff;
		}

		/**
		 * Here begins to rotate the thread that captures the Frame Queue
		 * Arrival and Draws on the Surface to Show the image to usurio
		 */
		@Override
		public void run() {
			while (this.isOn) {

				// *************************************************
				// Capture Frame Queue Core
				byte[] jpgFrame = this.systemCore.poolDataFromCameraQueue();

				if (jpgFrame != null) {
					Canvas c = null;
					try {
						// *****************************************
						// Attempts to Capture the Canvas to draw
						c = this.surfaceHolder.lockCanvas(null);
						synchronized (this.surfaceHolder) {
							doDraw(c, jpgFrame);
						}
					} finally {
						// *********************************************
						// Returns the Canvas to Surface - if you give exception
						// Leaves the surface in a state incosistente
						if (c != null) {
							this.surfaceHolder.unlockCanvasAndPost(c);
						}
					}
				}
			}
		}

		/**
		 * Put the frame captured in the Core Surface to show usurio Converts
		 * the bitmap to jpeg Frame Clears the Draw Frame on Canvas Canvas
		 * 
		 * @param canvas
		 */
		private void doDraw(Canvas canvas, byte[] jpgFrame) {
			// *************************************************
			// Decoding compressed image to a Bitmap object JPGE
			// (Bitmap )
			Bitmap bitMapImg = BitmapFactory.decodeByteArray(jpgFrame, 0,
					jpgFrame.length);

			// *************************************************
			// If you managed to catch the drawing area then clears the area
			// To Draw the image without sugeiras
			canvas.drawARGB(255, 0, 0, 0);

			// *************************************************
			// Drawing the image in area
			canvas.drawBitmap(bitMapImg, null, canvas.getClipBounds(), null);
		}

	}

	/**
	 * Flag for whether the Surface is ready to start the Capture Thread Image *
	 */
	private boolean surfaceRead;

	/**
	 * Implemented thread above - Draw the Captured frames in Core surface
	 */
	private FrameThread frameThred;

	/**
	 * Creates Instance of FrameView and initialize Thread for the design of
	 * frames captured
	 * 
	 * @param context
	 * @param attrs
	 */
	public FrameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		// ***********************************************
		// Instantiating the Thread Update Image
		this.frameThred = new FrameThread(getHolder());
	}

	/**
	 * Required to implement the Interface Event - empty for now
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	/**
	 * Event executed when the Surface has been created completely try to start
	 * Thread to j if there been informed of the Core System
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// ***********************************************
		// Can Initialize a Thread here if I had received
		// Core System in StartImageDrawer
		if (this.frameThred.getSystemCore() != null) {
			this.frameThred.setOnOff(true);
			// TODO: uncomment
			this.frameThred.start();
		}
		this.surfaceRead = true;
	}

	/**
	 * Killing a thread tries at all costs
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		this.frameThred.setOnOff(false);
		while (retry) {
			try {
				this.frameThred.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Informs the Surface est 100 % to start
	 * 
	 * @return
	 */
	public boolean isSurfaceRead() {
		return this.surfaceRead;
	}

	/**
	 * Informs the Core System to try to start a thread and that thread Mounts
	 * the image on the Surface
	 * 
	 * @param systemCore
	 */
	public void startImageDrawer(CarDroiDuinoCore systemCore) {
		// ****************************************************
		// Informs the Core System to Thread
		this.frameThred.setSystemCore(systemCore);
		// ****************************************************
		// You can start the thread here if j built surface
		if (this.isSurfaceRead()) {
			this.frameThred.setOnOff(true);
			// TODO: uncomment
			this.frameThred.start();
		}
	}

}
