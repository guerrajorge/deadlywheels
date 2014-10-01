// Copyright 2007-2014 metaio GmbH. All rights reserved.
package com.metaio.Example;

import android.util.Log;
import android.view.View;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.ECAMERA_TYPE;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

public class TutorialStereoRendering extends ARViewActivity
{

	@Override
	protected int getGUILayout()
	{
		// Attaching layout to the activity
		return R.layout.tutorial_stereo_rendering;
	}

	public void onButtonClick(View v)
	{
		finish();
	}

	@Override
	protected void loadContents()
	{
		try
		{
			// Getting a file path for tracking configuration XML file
			final String trackingConfigFile = AssetsManager.getAssetPath(getApplicationContext(), "TutorialStereoRendering/Assets/TrackingData_MarkerlessFast.xml");

			// Assigning tracking configuration
			boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile);
			MetaioDebug.log("Tracking data loaded: " + result);

			// Getting a file path for a 3D geometry
			final String filepath = AssetsManager.getAssetPath(getApplicationContext(), "TutorialStereoRendering/Assets/metaioman.md2");
			if (filepath != null)
			{
				// Loading 3D geometry
				IGeometry geometry = metaioSDK.createGeometry(filepath);
				if (geometry != null)
				{
					// Set geometry properties
					geometry.setScale(2f);
				}
				else
					MetaioDebug.log(Log.ERROR, "Error loading the geometry: "+filepath);
			}

			// Adjust hand-eye calibration (i.e. difference in view of camera vs. left/right eye).
			// These are contrived example values. Real values should be gathered by an exact
			// calibration. Note that for typical scenarios, e.g. AR/VR glasses where the camera has
			// a translation to left/right eye, the camera image is still rendered as for the mono
			// case (it is not transformed by the hand-eye calibration to look correct). Therefore
			// on glasses, see-through mode should be enabled (see above).
			// Note that setStereoRendering automatically sets an initial hand-eye calibration for
			// known devices, so if you want to override it, you should instead call
			// setHandEyeCalibration *after* setStereoRendering(true)!
			metaioSDK.setHandEyeCalibration(
				new Vector3d(70f, 0f, 0f),
				new Rotation(0f, -18f * (float)Math.PI/180f, 0f),
				ECAMERA_TYPE.ECT_RENDERING_LEFT);
			metaioSDK.setHandEyeCalibration(
				new Vector3d(10f, 0f, 0f),
				new Rotation(0f, 7f * (float)Math.PI/180f, 0f),
				ECAMERA_TYPE.ECT_RENDERING_RIGHT);

			// Enable stereo rendering
			metaioSDK.setStereoRendering(true);

			// Enable see through mode (e.g. on glasses)
			metaioSDK.setSeeThrough(true);
			metaioSDK.setSeeThroughColor(0, 0, 0, 255);
		}
		catch (Exception e)
		{
			MetaioDebug.printStackTrace(Log.ERROR, e);
		}
	}

	@Override
	protected void onGeometryTouched(IGeometry geometry)
	{
		// Not used in this tutorial
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler()
	{
		// No callbacks needed in this tutorial
		return null;
	}
}
