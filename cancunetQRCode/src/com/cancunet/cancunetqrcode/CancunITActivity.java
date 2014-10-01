package com.cancunet.cancunetqrcode;

import java.io.IOException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.EPLAYBACK_STATUS;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.MovieTextureStatus;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.tools.io.AssetsManager;

public class CancunITActivity extends ARViewActivity 
{
	private IGeometry modelCancunIT;
	private IGeometry modelCancunITVideo;
	private IGeometry modelSpringBreakVideo;
	private int mSelectedModel;
	private CancunITActivity mThis;
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
		modelCancunIT = null;
		modelCancunITVideo = null;
		modelSpringBreakVideo = null;
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
		return R.layout.activity_cancun_it; 
	}
		
	public void onButtonClick(View v)
	{
		finish();
	}
	
	public void onModelButtonClick(View v)
	{
		setActiveModel(1);
	}
	
	public void onMovieButtonClick(View v)
	{
		setActiveModel(2);
	}
	
	public void onMovieSBButtonClick(View v)
	{
		setActiveModel(3);
	}
	
	public void onDotNetbuttonClick(View v)
	{
		Intent myIntent = new Intent(v.getContext(), DotNetActivity.class);
		startActivity(myIntent);
		//setActiveModel(3);
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
			trackingConfigFile = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/cancunitmarker.xml");
			
			//MetaioDebug.log(Log.ERROR, "data to load: " + trackingConfigFile);
			final boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile);
			
			MetaioDebug.log(Log.ERROR, "data loaded: " + result);
						
			//cargando video
			final String moviePath = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/dotnetvideo.3g2");
			//MetaioDebug.log(Log.ERROR, "movie loaded: " + moviePath);
			if (moviePath != null)
			{
				modelCancunITVideo = metaioSDK.createGeometryFromMovie(moviePath, false);
				
				if (modelCancunITVideo != null)
				{
					//MetaioDebug.log(Log.ERROR, "movie created");
					modelCancunITVideo.setScale(6.0f);
					//mMoviePlane.setRotation(new Rotation(0f, 0f, (float)-Math.PI/2));
					//MetaioDebug.log("Loaded geometry "+moviePath);
				}
				else {
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+moviePath);
				}
			}
			
			//cargando video
			final String moviePath2 = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/springbreak.3g2");
			if (moviePath2 != null) {
				modelSpringBreakVideo = metaioSDK.createGeometryFromMovie(moviePath2, false);
				
				if (modelSpringBreakVideo != null) {
					modelSpringBreakVideo.setScale(6.0f);
				}
				else {
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+moviePath2);
				}
			}
			
			//cargando el modelo de cancunIT
			final String cancunITModel = AssetsManager.getAssetPath(getApplicationContext(), "dotnet/cancunit3d.obj");
			if (cancunITModel != null)
			{
				
				modelCancunIT = metaioSDK.createGeometry(cancunITModel);
				MetaioDebug.log(Log.ERROR, "model loaded " + cancunITModel);
				try
				{
					MetaioDebug.log(Log.ERROR, "model name " + modelCancunIT.getName());
				}
				
				catch (Exception e){
					MetaioDebug.log(Log.ERROR, "error getting model name");
				}
					
				if (modelCancunIT != null)
				{
					modelCancunIT.setScale(150.f);
					//mMoviePlane.setRotation(new Rotation(0f, 0f, (float)-Math.PI/2));
				}
				else
				{
					MetaioDebug.log(Log.ERROR, "Error loading geometry: " + cancunITModel);
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
		if (geometry.equals(modelCancunITVideo))
		{
			final MovieTextureStatus status = modelCancunITVideo.getMovieTextureStatus();
			if (status.getPlaybackStatus() == EPLAYBACK_STATUS.EPLAYBACK_STATUS_PLAYING)
				modelCancunITVideo.pauseMovieTexture();
			else
				modelCancunITVideo.startMovieTexture(true);
		}
		
		if (geometry.equals(modelSpringBreakVideo))
		{
			final MovieTextureStatus status = modelSpringBreakVideo.getMovieTextureStatus();
			if (status.getPlaybackStatus() == EPLAYBACK_STATUS.EPLAYBACK_STATUS_PLAYING)
				modelSpringBreakVideo.pauseMovieTexture();
			else
				modelSpringBreakVideo.startMovieTexture(true);
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

		if (modelIndex == 1){
			modelCancunIT.setVisible(true);
			modelCancunITVideo.setVisible(false);
			modelCancunITVideo.stopMovieTexture();
			modelSpringBreakVideo.setVisible(false);
			modelSpringBreakVideo.stopMovieTexture();
		}
		
		if (modelIndex == 2)
		{
			modelCancunIT.setVisible(false);
			modelCancunITVideo.setVisible(true);
			modelSpringBreakVideo.setVisible(false);
			modelSpringBreakVideo.stopMovieTexture();
		}
		
		if (modelIndex == 3)
		{
			modelCancunIT.setVisible(false);
			modelCancunITVideo.setVisible(false);
			modelCancunITVideo.stopMovieTexture();
			modelSpringBreakVideo.setVisible(true);
			
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

			// We only have one COS, so there can only ever be one TrackingValues structure passed.
			// Play movie if the movie button was selected and we're currently tracking.
			if (trackingValues.isEmpty() || !trackingValues.get(0).isTrackingState())
			{
				if (modelCancunITVideo != null)
					modelCancunITVideo.stopMovieTexture();
				
				if (modelSpringBreakVideo != null)
					modelSpringBreakVideo.stopMovieTexture();
			}
			else
			{
				if (modelCancunITVideo != null && mSelectedModel == 2)
					modelCancunITVideo.startMovieTexture(true);
				
				if (modelSpringBreakVideo != null && mSelectedModel == 3)
					modelSpringBreakVideo.startMovieTexture(true);
			}
		}
	}
}
