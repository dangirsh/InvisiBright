package com.DanGirshovich.T4B;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainTouch extends Activity {
	
	private double brightness;
	private int lBound;
	private int uBound;
	private SharedPreferences prefs;
	private int orientation;
	private DisplayMetrics metrics;
	private Activity thisActivity;
	
	//prefs
	private boolean lockLayout;
	private boolean isLefty;
	private int sliderAlpha;
	private int sliderColor;
	private double heightPortraitPerc;
	private double heightLandscapePerc;
	private boolean isVolume;
	private int volSliderAlpha;
	private int volSliderColor;
	private boolean showVolLevel;
	private boolean borderAndSigns;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		thisActivity = this;
		if(!Common.checkShortcutIntent(thisActivity, getIntent(), Const.START_TOUCH)){	
			loadPrefs();
			setContentView(new TouchView(thisActivity));
			Common.setAutoBrightness(false, getContentResolver());
			orientation = Common.getScreenOrientation(thisActivity);
			Common.lockOrientation(lockLayout, orientation, thisActivity);
			calcBounds(orientation);
			brightness = Common.getBrightness(getContentResolver());
			Common.showHintToast(true, thisActivity, prefs);
			showPercentToast(true);
		}
	}
	
	private void loadPrefs(){
		
		prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
		lockLayout = prefs.getBoolean("lockLayoutTouch", false);
		isLefty = prefs.getBoolean("lefty", false);
		sliderAlpha = (int) (255 - (2.55 * prefs.getInt("sliderAlpha", 100)));
		
		try{
			sliderColor = Color.parseColor(prefs.getString("sliderColor", "#0000FF"));
		}
		catch(IllegalArgumentException e){
			sliderColor = Color.parseColor("#0000FF");
		}
		
		heightPortraitPerc = ((float) prefs.getInt("heightPortrait", 80)) / 100f;
		heightLandscapePerc = ((float) prefs.getInt("heightLandscape", 80)) / 100f;
		
		isVolume = prefs.getBoolean("volume", false);
		volSliderAlpha = (int) (255 - (2.55 * prefs.getInt("volSliderAlpha", 100)));
		
		try{
			volSliderColor = Color.parseColor(prefs.getString("volSliderColor", "#FF00FF"));
		}
		catch(IllegalArgumentException e){
			volSliderColor = Color.parseColor("#FF00FF");
		}
				
		showVolLevel = prefs.getBoolean("showVolLevel", true);
		borderAndSigns = prefs.getBoolean("borderAndSigns", true);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		orientation = Common.getScreenOrientation(thisActivity);
		calcBounds(orientation);
	}

	public int getHeight() {
		return metrics.heightPixels;
	}

	public int getWidth() {
		return metrics.widthPixels;
	}

	public int getMargin(){
		double percent = (orientation == Const.LANDSCAPE) ? heightLandscapePerc : heightPortraitPerc;
		return (int) ((getHeight() - percent * getHeight()) / 2);
	}
	
	public void calcBounds(int o) {
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		lBound = getMargin();
		uBound = getHeight() - getMargin();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		Common.showHintToast(false, thisActivity, prefs);
		
		float x = event.getX();
		float y = event.getY();
		
		boolean touchRight = x > getWidth() / 2;
		
		if(y > getMargin() &&  y < getHeight() - getMargin()){
		
			if(touchRight == isLefty) {
				if(isVolume){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						showPercentToast(false);
						updateVolume(y); 
					}
				}
				else {
					onStop();
				}
			}
			else if(touchRight != isLefty) {
				updateBrightness(y);
			}
		}
		return true;
	}

	private void updateVolume(float y) {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int dir = (y<getHeight()/2) ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER;
		int flags = showVolLevel ? AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI : AudioManager.FLAG_PLAY_SOUND;
		am.adjustSuggestedStreamVolume(dir, AudioManager.USE_DEFAULT_STREAM_TYPE, flags);
	}

	public void updateBrightness(float y) {
		float ratio = (y - lBound) / (uBound - lBound);
		float value = 255.0f - ratio * 255.0f;
		
		if (value < 20){
			value = 20;
		}
		else if (value > 255){
			value = 255;
		}

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = value / 255.0f;

		showPercentToast(true);

		getWindow().setAttributes(lp);
		brightness = value;
	}

	@Override
	protected void onStop() {
		showPercentToast(false);
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

	private class TouchView extends View {

		public TouchView(Context c) {
			super(c);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			canvas.drawColor(Color.TRANSPARENT);
			paint.setColor(sliderColor);
			paint.setAlpha(sliderAlpha);
			int x1 = isLefty ? 0 : getWidth() / 2;
			int x2 = isLefty ? getWidth() / 2 : getWidth();
			int y1 = getMargin();
			int y2 = getHeight() - getMargin();
			Rect r = new Rect(x1, y1, x2, y2);
			canvas.drawRect(r, paint);

			if(isVolume){
				Log.e("ib", String.valueOf(volSliderColor));

				//area
				Paint paintv = new Paint();
				paintv.setStyle(Paint.Style.FILL);
				paintv.setColor(volSliderColor);
				paintv.setAlpha(volSliderAlpha);
				int x1v = !isLefty ? 0 : getWidth() / 2;
				int x2v = !isLefty ? getWidth() / 2 : getWidth();
				int y1v = getMargin();
				int y2v = getHeight() - getMargin();
				Rect rv = new Rect(x1v, y1v, x2v, y2v);
				canvas.drawRect(rv, paintv);

				if(borderAndSigns){
				
					//border
					int mid = (y2v + y1v) / 2;
					int borderWidth = 4;
					Rect rb = new Rect(x1v, mid - borderWidth ,x2v, mid + borderWidth);
					Paint paintb = new Paint();
					paintb.setStyle(Paint.Style.FILL);
					paintb.setColor(0xFFFFFF - volSliderColor);
					paintb.setAlpha(Math.min(volSliderAlpha, 255));
					canvas.drawRect(rb, paintb);
					
					//signs
					int signRadius = 15;
					int signThickness = 3;
					int signCenterX = (x1v + x2v) / 2;
	
					int plusCenterY = (y1v + mid) / 2;
					Rect plusHorizontal = new Rect(signCenterX - signRadius, plusCenterY - signThickness, signCenterX + signRadius, plusCenterY + signThickness);
					Rect plusVertical =  new Rect(signCenterX - signThickness, plusCenterY - signRadius, signCenterX + signThickness, plusCenterY + signRadius);
					canvas.drawRect(plusHorizontal, paintb);
					canvas.drawRect(plusVertical, paintb);
					
					int minusCenterY = (y2v + mid) / 2;
					Rect minus = new Rect(signCenterX - signRadius, minusCenterY - signThickness, signCenterX + signRadius, minusCenterY + signThickness);
					canvas.drawRect(minus, paintb);	
				}
				
			}
		}

	}

}
