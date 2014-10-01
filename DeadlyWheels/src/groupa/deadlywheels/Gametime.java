package groupa.deadlywheels;

import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.tools.io.AssetsManager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class Gametime extends ARViewActivity implements SensorEventListener
{
	private IGeometry modelCancunIT;
	private MetaioSDKCallbackHandler mCallbackHandler;
	AssetsExtracter mTask;
	
	private SensorManager senSensorManager;
	private Sensor senAccelerometer;
	
	
	private long lastUpdate = 0;
	private float last_x, last_y, last_z;
	private static final int SHAKE_THRESHOLD = 200;

	/**
	 * This task extracts all the assets to an external or internal location
	 * to make them accessible to Metaio SDK
	 */
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean>
	{
		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			try 
			{
				// Extract all assets and overwrite existing files if debug build
				AssetsManager.extractAllAssets(getApplicationContext(), BuildConfig.DEBUG);
			} 
			catch (IOException e) 
			{
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}

			return true;
		}
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		
		senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
	    		
	    		
		//getting rib off the status bar
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		//MetaioDebug.log(Log.ERROR, "executing assets extractor");
		//extract all the assets
		mTask = new AssetsExtracter();
		mTask.execute(0);
				
		//MetaioDebug.log(Log.ERROR, "the application starts");
		super.onCreate(savedInstanceState);
		modelCancunIT = null;
		mCallbackHandler = new MetaioSDKCallbackHandler();
		
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		mCallbackHandler.delete();
		mCallbackHandler = null;
	}
	
	@Override
	protected int getGUILayout() 
	{
		return R.layout.activity_gametime; 
	}
	
	@Override
	protected void loadContents() 
	{
		try
		{
			// Load desired tracking data for planar marker tracking
			final String trackingConfigFile;
			trackingConfigFile = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/TrackingData_MarkerlessFast.xml");
			
			//MetaioDebug.log(Log.ERROR, "data to load: " + trackingConfigFile);
			final boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile);
			
			MetaioDebug.log(Log.ERROR, "data loaded: " + result);
			
			//cargando el modelo de cancunIT
			final String cancunITModel = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/fence.mfbx");
			modelCancunIT = metaioSDK.createGeometry(cancunITModel);
			modelCancunIT.setScale(150.f);
		
			//start displaying the model
			modelCancunIT.setVisible(true);		
			mCallbackHandler.onTrackingEvent(metaioSDK.getTrackingValues());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			MetaioDebug.log(Log.ERROR, "FAILED: ");
		}
	}
	
	@Override
	protected void onGeometryTouched(IGeometry geometry) 
	{
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() 
	{
		return mCallbackHandler;
	}
	
	final private class MetaioSDKCallbackHandler extends IMetaioSDKCallback 
	{
		@Override
		public void onSDKReady() 
		{
			// show GUI after SDK is ready
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					mGUIView.setVisibility(View.VISIBLE);
				}
			});
		}
	}
	
	protected void onPause() {
	    super.onPause();
	    senSensorManager.unregisterListener(this);
	}
	
	protected void onResume() {
	    super.onResume();
	    senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		// TODO Auto-generated method stub
		Sensor mySensor = sensorEvent.sensor;
		 
	    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	        float x = sensorEvent.values[0];
	        float y = sensorEvent.values[1];
	        float z = sensorEvent.values[2];
	 
	        long curTime = System.currentTimeMillis();
	 
	        if ((curTime - lastUpdate) > 100) {
	            lastUpdate = curTime;
	 
	            System.out.println("the x value is: " + x);
	            System.out.println("the y value is: " + y);
	            System.out.println("the z value is: " + z);
	            	
	 
	            last_x = x;
	            last_y = y;
	            last_z = z;
	        }
	    }
		
	}
}

