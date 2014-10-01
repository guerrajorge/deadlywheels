package com.cancunet.cancunetqrcode;

import java.io.IOException;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.EPLAYBACK_STATUS;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.MovieTextureStatus;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.tools.io.AssetsManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class DotNetActivity extends ARViewActivity {

	private IGeometry modelDotNet;
	
	private int mSelectedModel;
	private DotNetActivity mThis;
	private MetaioSDKCallbackHandler mCallbackHandler;
	AssetsExtracter mTask;

	/**
	 * This task extracts all the assets to an external or internal location
	 * to make them accessible to metaio SDK
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
		//MetaioDebug.log(Log.ERROR, "executing assets extracter");
		// extract all the assets
		mTask = new AssetsExtracter();
		mTask.execute(0);
				
		//MetaioDebug.log(Log.ERROR, "the application starts");
		
		super.onCreate(savedInstanceState);
		mThis = this;
		modelDotNet = null;
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
		return R.layout.activity_dot_net; 
	}
		
	public void onButtonClick(View v)
	{
		finish();
	}
	
	public void oncancunITModelsbuttonClick(View v)
	{
		//regresar a cancunIT activity
		 Intent myIntent = new Intent(v.getContext(), CancunITActivity.class);
		 startActivity(myIntent);
	}
	
	/*
	@Override
	public void onDrawFrame() {
		// TODO Auto-generated method stub
		super.onDrawFrame();
		
		if (metaioSDK != null)
	    {
	        // get all detected poses/targets
	        TrackingValuesVector poses = metaioSDK.getTrackingValues();

	        if (mSelectedModel == 1){
		        if (poses.size() > 0) {
		            marker(poses.get(0).getCoordinateSystemID());
		        } 
	        }
        }
	}	
	
	private void marker(int cosId) {
    
		switch (cosId) {
        case 1:
            // Getting a file path for a 3D geometry
            String imageModel = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/cancunit3d.obj");         
            if (imageModel != null) 
            {
                // Loading 3D geometry
            	modelCancunIT = metaioSDK.createGeometryFromImage(imageModel);
                if (modelCancunIT != null) 
                {
                	modelCancunIT.setScale(150.f);
                	//modelCancunIT.setCoordinateSystemID(1);
                }
                else
                    MetaioDebug.log(Log.ERROR, "Error loading geometry: "+imageModel);
            }
            break;
        case 2:
            // Getting a file path for a 3D geometry
            String metaioManModel = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/dotnetlogo.obj");         
            if (metaioManModel != null) 
            {
                // Loading 3D geometry
            	modelCancunIT = metaioSDK.createGeometry(metaioManModel);
                if (modelCancunIT != null) 
                {
                	modelCancunIT.setScale(150.f);
                	//modelCancunIT.setCoordinateSystemID(2);
                }
                else
                    MetaioDebug.log(Log.ERROR, "Error loading geometry: "+metaioManModel);
            }
            break;
        }
    }
	*/
	
	@Override
	protected void loadContents() 
	{
		try
		{
			// Load desired tracking data for planar marker tracking
			//MetaioDebug.log(Log.ERROR, "configs: " + getApplicationContext() + "dotnet/TrackingData_MarkerlessFast.xml");
			final String trackingConfigFile;
			trackingConfigFile = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/dotnetmarker.xml");
			
			//MetaioDebug.log(Log.ERROR, "data to load: " + trackingConfigFile);
			final boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile);
			
			MetaioDebug.log(Log.ERROR, "data loaded: " + result);
						
			
			//cargando el modelo de dotnet
			final String sdotNetModel = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/dotnetlogo.obj");
			if (sdotNetModel != null)
			{
				
				modelDotNet = metaioSDK.createGeometry(sdotNetModel);
				MetaioDebug.log(Log.ERROR, "model loaded " + sdotNetModel);
				
				if (modelDotNet != null)
				{
					modelDotNet.setScale(80.0f);
					//mMoviePlane.setRotation(new Rotation(0f, 0f, (float)-Math.PI/2));
				}
				else
				{
					MetaioDebug.log(Log.ERROR, "Error loading geometry: " + sdotNetModel);
				}
			}
			
			//start displaying the model
			setActiveModel(1);			

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
	
	private void setActiveModel(int modelIndex)
	{
		mSelectedModel = modelIndex;

		if (modelIndex == 1){
			modelDotNet.setVisible(true);
		}
		
		// Start or pause movie according to tracking state
		mCallbackHandler.onTrackingEvent(metaioSDK.getTrackingValues());
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
		
		@Override
		public void onTrackingEvent(TrackingValuesVector trackingValues)
		{
			super.onTrackingEvent(trackingValues);
		}
	}
}
