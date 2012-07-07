package com.DanGirshovich.T4B;

import com.DanGirshovich.T4B.Settings.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainChooser extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		String action = getIntent().getAction();
		if(action.equals(Intent.ACTION_MAIN)){
			Intent i = new Intent(this, Settings.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
		
		else if(action.equals(Const.START_TILT)){
			Intent i = new Intent(this, MainTilt.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
		
		else if(action.equals(Const.START_TOUCH)){
			Intent i = new Intent(this, MainTouch.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
		
		else if(action.equals(Intent.ACTION_SEARCH_LONG_PRESS)){
			
			String mode = PreferenceManager.getDefaultSharedPreferences(this).getString("mode", "Touch");
			
			if(mode.equals("Touch")){
				Intent i = new Intent(this, MainTouch.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
			
			else if(mode.equals("Tilt")){
				Intent i = new Intent(this, MainTilt.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
			
		}
		else{
			startActivity(new Intent(this, Settings.class));
		}
		onStop();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		finish();
	}
	
	@Override
	public void onResume(){
		onStop();
	}
}