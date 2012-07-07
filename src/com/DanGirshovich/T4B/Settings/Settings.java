package com.DanGirshovich.T4B.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.widget.TextView;

import com.DanGirshovich.T4B.Const;
import com.DanGirshovich.T4B.MainTilt;
import com.DanGirshovich.T4B.MainTouch;
import com.DanGirshovich.T4B.R;
import com.DanGirshovich.T4B.R.id;
import com.DanGirshovich.T4B.R.layout;
import com.DanGirshovich.T4B.R.string;
import com.DanGirshovich.T4B.R.xml;

public class Settings extends PreferenceActivity {

	private String modeStr;
	private Intent updateNotification;
	private boolean isTesting = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		updateNotification = new Intent();
		updateNotification.setAction(Const.UPDATE_NOTIFICATION);
		updateNotification();
		
		modeStr = (String) ((ListPreference) findPreference("mode")).getEntry();
		Preference mode = findPreference("mode");
		mode.setSummary(modeStr + " mode selected.");
		mode.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				modeStr = (String) newValue;
				preference.setSummary(modeStr + " mode selected.");
				updateNotification();
				return true;
			}
		});
		
		findPreference("shortcut").setOnPreferenceClickListener(new ChangeNotListener());
		findPreference("iconVisible").setOnPreferenceClickListener(new ChangeNotListener());
		findPreference("iconInverted").setOnPreferenceClickListener(new ChangeNotListener());
		findPreference("tiltTest").setOnPreferenceClickListener(new IntentListener(new Intent(getApplicationContext(), MainTilt.class)));
		findPreference("touchTest").setOnPreferenceClickListener(new IntentListener(new Intent(getApplicationContext(), MainTouch.class)));
		findPreference("about").setOnPreferenceClickListener(new DialogListener(0));
		findPreference("help").setOnPreferenceClickListener(new DialogListener(1));
	}

	protected void updateNotification() {
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().commit();
		getApplicationContext().sendBroadcast(updateNotification);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Builder builder = new AlertDialog.Builder(this);
		View view = getLayoutInflater().inflate(R.layout.about, null);
		TextView textView = ((TextView) view.findViewById(R.id.about));
		if (id == 0) {
			builder.setTitle("Info").setIcon(android.R.drawable.ic_dialog_info);
			textView.setText(R.string.about);
			builder.setView(view);
			builder.setNegativeButton("Close", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		} else if (id == 1) {
			builder.setTitle("Help").setIcon(android.R.drawable.ic_dialog_info);
			textView.setTextSize(18);
			textView.setText(R.string.help);
			builder.setView(view);
			builder.setNegativeButton("Close", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}

		return builder.create();

	}

	@Override
	public void onPause(){
		if(isTesting){
			isTesting = false;
			super.onPause();
		}
		else{
			onStop();
		}
	}
	

	@Override
	public void onStop() {
		super.onStop();
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private class ChangeNotListener implements OnPreferenceClickListener{

		public boolean onPreferenceClick(Preference preference) {
			updateNotification();
			return false;
		}		
	}
	
	private class IntentListener implements OnPreferenceClickListener{
		
		private Intent i;
		
		public IntentListener(Intent intent){
			super();
			i = intent;
		}

		public boolean onPreferenceClick(Preference p) {
			isTesting = true;
			startActivity(i);
			return false;
		}
		
	}
	
	private class DialogListener implements OnPreferenceClickListener{
		
		private int id;
		
		public DialogListener(int argId){
			super();
			id = argId;
		}

		public boolean onPreferenceClick(Preference preference) {
			showDialog(id);
			return false;
		}
		
	}
}