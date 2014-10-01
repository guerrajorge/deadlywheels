package com.cancunet.cancunetqrcode;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.EPLAYBACK_STATUS;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.MovieTextureStatus;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.tools.io.AssetsManager;

public class TrailerActivity extends ARViewActivity 
{
	private IGeometry mMetaioMan;
	private IGeometry mImagePlane;
	private IGeometry mMoviePlane;
	private IGeometry mTruck;
	private int mSelectedModel;
	private TrailerActivity mThis;
	private AlertDialog mAlert;
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
		mAlert = null;
		mMetaioMan = null;
		mImagePlane = null;
		mMoviePlane = null;
		mTruck = null;
		
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
		return R.layout.activity_main; 
	}
	
	public void onButtonClick(View v)
	{
		finish();
	}
	
	public void onModelButtonClick(View v)
	{
		setActiveModel(0);
	}
	
	public void onImageButtonClick(View v)
	{
		setActiveModel(1);
	}
	
	public void onMovieButtonClick(View v)
	{
		setActiveModel(3);
	}
	
	public void onTruckButtonClick(View v)
	{
		setActiveModel(2);
	}
	
	@Override
	protected void loadContents() 
	{
		try
		{
			// Load desired tracking data for planar marker tracking
			//MetaioDebug.log(Log.ERROR, "configs: " + getApplicationContext() + "dotnet/TrackingData_MarkerlessFast.xml");
			final String trackingConfigFile = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/TrackingData_MarkerlessFast.xml");
			
			//MetaioDebug.log(Log.ERROR, "data to load: " + trackingConfigFile);
			final boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile); 
			MetaioDebug.log(Log.ERROR, "data loaded: " + result);
			
			/*
			// Load all the geometries. First - Model
			final String modelPath = AssetsManager.getAssetPath(getApplicationContext(), "fueltruck/fueltrucka.md2");			
			if (modelPath != null) 
			{
				mMetaioMan = metaioSDK.createGeometry(modelPath);
				if (mMetaioMan != null) 
				{
					// Set geometry properties
					mMetaioMan.setScale(4.0f);
					MetaioDebug.log("Loaded geometry "+modelPath);
				}
				else
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+modelPath);
			}
			*/
			
			// Loading image geometry
			/*
			final String imagePath = AssetsManager.getAssetPath(getApplicationContext(), "TutorialContentTypes/Assets/frame.png");
			if (imagePath != null)
			{
				mImagePlane = metaioSDK.createGeometryFromImage(imagePath);
				if (mImagePlane != null)
				{
					mImagePlane.setScale(3.0f);
					MetaioDebug.log("Loaded geometry "+imagePath);
				}
				else {
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+imagePath);
				}
			}
			*/
			
			// Loading movie geometry
			/*
			final String moviePath = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/dotnetvideo.3g2");
			//MetaioDebug.log(Log.ERROR, "movie loaded: " + moviePath);
			if (moviePath != null)
			{
				mMoviePlane = metaioSDK.createGeometryFromMovie(moviePath, false);
				
				if (mMoviePlane != null)
				{
					//MetaioDebug.log(Log.ERROR, "movie created");
					mMoviePlane.setScale(6.0f);
					//mMoviePlane.setRotation(new Rotation(0f, 0f, (float)-Math.PI/2));
					//MetaioDebug.log("Loaded geometry "+moviePath);
				}
				else {
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+moviePath);
				}
			}
			
			 */
			// loading truck geometry
			
			final String cancunITModel = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/dotnetlogo.obj");
			
			if (cancunITModel != null)
			{
				
				mTruck = metaioSDK.createGeometry(cancunITModel);
				MetaioDebug.log(Log.ERROR, "model loaded " + cancunITModel);
				try
				{
					MetaioDebug.log(Log.ERROR, "model name " + mTruck.getName());
				}
				
				catch (Exception e){
					MetaioDebug.log(Log.ERROR, "error getting model name");
				}
				
					
				if (mTruck != null)
				{
					mTruck.setScale(150.f);
					//mMoviePlane.setRotation(new Rotation(0f, 0f, (float)-Math.PI/2));
					
				}
				else
				{
					MetaioDebug.log(Log.ERROR, "Error loading geometry: " + cancunITModel);
				}
			}
			
			//mMoviePlane.setVisible(true);
			mTruck.setVisible(true);
			//setActiveModel(3);
			
			//start displaying the model
			//setActiveModel(0);
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
		if (geometry.equals(mMoviePlane))
		{
			final MovieTextureStatus status = mMoviePlane.getMovieTextureStatus();
			if (status.getPlaybackStatus() == EPLAYBACK_STATUS.EPLAYBACK_STATUS_PLAYING)
				mMoviePlane.pauseMovieTexture();
			else
				mMoviePlane.startMovieTexture(true);
		}
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() 
	{
		return mCallbackHandler;
	}
	
	private void setActiveModel(int modelIndex)
	{
		mSelectedModel = modelIndex;

		mMetaioMan.setVisible(modelIndex == 0);
		mImagePlane.setVisible(modelIndex == 1);
		mTruck.setVisible(modelIndex == 2);
		mMoviePlane.setVisible(modelIndex == 3);

		if (modelIndex != 3)
			mMoviePlane.stopMovieTexture();

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

			// We only have one COS, so there can only ever be one TrackingValues structure passed.
			// Play movie if the movie button was selected and we're currently tracking.
			if (trackingValues.isEmpty() || !trackingValues.get(0).isTrackingState())
			{
				if (mMoviePlane != null)
					mMoviePlane.stopMovieTexture();
			}
			else
			{
				if (mMoviePlane != null && mSelectedModel == 3)
					mMoviePlane.startMovieTexture(true);
			}
		}
	}
}
