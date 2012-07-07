package com.DanGirshovich.T4B;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

public class Common {
	
	private static Toast hintToast;
	private static Toast percentToast;
	
	public static boolean checkShortcutIntent(Activity a, Intent i, String shortcutAction) {

        final String action = i.getAction();
        
        if (Intent.ACTION_CREATE_SHORTCUT.equals(action)) {
            Intent shortcutIntent = new Intent(shortcutAction);
            shortcutIntent.setClassName(a, a.getClass().getName());
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Intent ret = new Intent();
            ret.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            //TODO: fix icon label not showing up
            ret.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.touch_mode);
            ret.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
            		Intent.ShortcutIconResource.fromContext(a, R.drawable.widgeti));
            a.setResult(Activity.RESULT_OK, ret);    
            a.finish();
            return true;
        }
        
        return false;

	}
	
	public static int getBrightness(ContentResolver contentResolver) {	
		int b;
		try {
			b = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			b = 177;
		}
		return b;
	}
	
	public static int getScreenOrientation(Activity a) {
		switch(a.getWindowManager().getDefaultDisplay().getRotation()){
			case Surface.ROTATION_0: return Const.PORTRAIT; 
			case Surface.ROTATION_90: return Const.LANDSCAPE;
			case Surface.ROTATION_270: return Const.REVERSE_LANDSCAPE; 
			default: return Const.PORTRAIT;
		}
	}
	
	public static void lockOrientation(boolean doit, int orientation, Activity a) {
		if (doit) {
			int requested;
			switch(orientation){
				case Const.PORTRAIT: requested = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; break;
				case Const.LANDSCAPE: requested = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE; break;
				default: requested = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
			a.setRequestedOrientation(requested);
		}		
	}

	public static void onCreateOptionsMenu(Activity a, Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE,
				a.getString(R.string.settings_label)).setIcon(
				android.R.drawable.ic_menu_preferences);

		menu.add(Menu.NONE, 1, Menu.NONE, "Auto-brightness on").setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
	}
	
	public static void onOptionsItemSelected(Activity a, MenuItem item) {
		if (item.getItemId() == 0) {
			a.startActivity(new Intent(a, com.DanGirshovich.T4B.Settings.Settings.class));
		} else if (item.getItemId() == 1) {
			Common.setAutoBrightness(true, a.getContentResolver());
		}
	}

	public static void setAutoBrightness(boolean on, ContentResolver contentResolver) {
		if (on) {
			Settings.System.putInt(contentResolver,
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		} else {
			Settings.System.putInt(contentResolver,
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		}
	}

	public static float setBrightness(float brightness, Activity a) {
		if (brightness < 20.0)
			brightness = (float) 20.0;
		else if (brightness > 255.0)
			brightness = (float) 255.0;

		WindowManager.LayoutParams lp = a.getWindow().getAttributes();
		lp.screenBrightness = (float) (brightness / 255.0);
		a.getWindow().setAttributes(lp);	
		
		return brightness;
	}

	public static void showHintToast(boolean show, Activity a, SharedPreferences prefs) {
		
		if(hintToast == null) {
			hintToast = Toast.makeText(a, "", Toast.LENGTH_SHORT);
		}
		
		if (prefs.getBoolean("showHints", true)) {
			if(a instanceof MainTouch){
				hintToast.setText(prefs.getBoolean("volume", false) ? R.string.touch_hint2 : R.string.touch_hint1);
			}
			else if(a instanceof MainTilt){
				hintToast.setText(prefs.getBoolean("endOnTapTilt",true) ? R.string.tilt_hint1 : R.string.tilt_hint2);
			}
			
			if(show){
				hintToast.show();
			}
			else{
				hintToast.cancel();
			}
		}
		
		
	
	}

	public static void showPercentToast(boolean show, Activity a, SharedPreferences prefs, double brightness) {
		
		if(percentToast == null){
			percentToast = Toast.makeText(a, "", Toast.LENGTH_SHORT);
		}
		
		percentToast.setText(new Integer((int) (brightness / 2.55)).toString() + "%");
		
		if(show){
			if (prefs.getBoolean("showToast", true)) percentToast.show();
		}
		else{
			percentToast.cancel();
		}
	}

	public static void setSystemBrightness(int brightness, Activity a) {
		Settings.System.putInt(a.getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS,
				(int) brightness);		
	}
	
}
