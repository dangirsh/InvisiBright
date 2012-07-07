package com.DanGirshovich.T4B.Settings;

import com.DanGirshovich.T4B.Const;

import android.content.Context;
import android.util.AttributeSet;

public class HeightPreference extends SeekBarPreference {
	
	
	public HeightPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setLabel(int val){
		val += Const.HEIGHT_PREF_OFFSET;
		super.setLabel(val);
	}
	
	
}
