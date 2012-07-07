package com.DanGirshovich.T4B;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainTilt extends Activity {

	private SensorManager sensorManager;
	private Sensor tiltSensor;
	private double range;
	private Float initTiltP, initTiltL, initTiltR = null;
	private int orientation;
	private float brightness;
	private SharedPreferences prefs;
	private Activity thisActivity;
	
	//prefs
	private boolean lockLayout;
	private int tiltSensitivity;
	private boolean endOnTap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisActivity = this;
		if (!Common.checkShortcutIntent(thisActivity, getIntent(), Const.START_TILT)) {
			loadPrefs();
			Common.setAutoBrightness(false, getContentResolver());
			orientation = Common.getScreenOrientation(thisActivity);
			Common.lockOrientation(lockLayout, orientation, thisActivity);
			brightness = Common.getBrightness(getContentResolver());
			initSensor();
			range = (50.0 - (0.1 * tiltSensitivity + 10));
			Common.showHintToast(true, thisActivity, prefs);
			showPercentToast(true);
		}
	}
	
	private void loadPrefs(){
		prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
		lockLayout = prefs.getBoolean("lockLayoutTilt", false);
		tiltSensitivity = prefs.getInt("tiltSensitivity", 50);
		endOnTap = prefs.getBoolean("endOnTapTilt", true);		
	}

	private void initSensor() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sensors.size() > 0)	tiltSensor = sensors.get(0);
	}

	private final SensorEventListener mySensorListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
					
			int axis = (orientation==Const.PORTRAIT) ? 1:2;
			
			float tilt;
			
			if(initTiltP==null && orientation==Const.PORTRAIT){
				initTiltP = event.values[axis];
				initTiltL = null;
				initTiltR = null;
			}
			else if(initTiltL==null && orientation==Const.LANDSCAPE){
				initTiltL = event.values[axis];
				initTiltP = null;
				initTiltR = null;
			}
			else if(initTiltR==null && orientation==Const.REVERSE_LANDSCAPE){
				initTiltR = event.values[axis];
				initTiltP = null;
				initTiltL = null;
			}
			
			tilt = event.values[axis];
			float initTilt;
			switch(orientation){
				case Const.PORTRAIT:
					initTilt = initTiltP;
				break;
				case Const.LANDSCAPE:
					initTilt = initTiltL;
				break;
				case Const.REVERSE_LANDSCAPE:
					initTilt = initTiltR;
				break;
				default:
					initTilt = initTiltP;
			}
			float offset = (orientation!=Const.LANDSCAPE) ? (tilt-initTilt) : (initTilt - tilt);
			brightness = (float) (255.0 * (offset + range/2) / range);
			brightness = Common.setBrightness(brightness, thisActivity);
			showPercentToast(true);
			
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	};

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(mySensorListener, tiltSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		showPercentToast(false);
		try {
			sensorManager.unregisterListener(mySensorListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Common.setSystemBrightness((int) brightness, thisActivity);
		super.onStop();
		finish();
	}

	@Override
	protected void onPause() {
		onStop();
	}
	
	private void showPercentToast(boolean show){
		Common.showPercentToast(show, thisActivity, prefs, brightness);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		orientation = Common.getScreenOrientation(thisActivity);
		brightness = (float) 177.5;
		showPercentToast(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Common.onCreateOptionsMenu(thisActivity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Common.onOptionsItemSelected(thisActivity, item);
		onStop();
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (endOnTap) onStop();
		return true;
	}
}
