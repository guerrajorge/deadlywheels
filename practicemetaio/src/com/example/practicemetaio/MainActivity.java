package com.example.practicemetaio;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.tools.io.AssetsManager;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ARViewActivity {

	@Override
	protected int getGUILayout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void loadContents() {
		// TODO Auto-generated method stub
		try
		{
			// Getting a file path for tracking configuration XML file
			String trackingConfigFile = AssetsManager.getAssetPath(getApplicationContext(), "TutorialHelloWorld/Assets/TrackingData_MarkerlessFast.xml");
			
			// Assigning tracking configuration
			boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile); 
			MetaioDebug.log("Tracking data loaded: " + result); 
	        
			// Getting a file path for a 3D geometry
			String metaioManModel = AssetsManager.getAssetPath(getApplicationContext(), "TutorialHelloWorld/Assets/metaioman.md2");			
			if (metaioManModel != null) 
			{
				// Loading 3D geometry
				IGeometry geometry = metaioSDK.createGeometry(metaioManModel);
				if (geometry != null) 
				{
					// Set geometry properties
					geometry.setScale(4f);
				}
				else
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+metaioManModel);
			}
		}
		catch (Exception e)
		{
			MetaioDebug.printStackTrace(Log.ERROR, e);
		}
	}

	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		// TODO Auto-generated method stub
		
	}

   
}
