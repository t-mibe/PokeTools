package com.mibe.pt_species;

import java.io.Serializable;

import com.mibe.lib_image.PokeImage;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class SpeciesDialog extends AlertDialog {
	
	private static final float textSize = 0.7f;
	
	private SpeciesBean bean = null;
	private PokeImage pi = null;
	
	private Drawable icon_default = null;
	
	private TextView titleView = null;

	protected SpeciesDialog(Context context, String homeDir) {
		super(context);
		
		pi = new PokeImage(context, homeDir);
		icon_default = context.getResources().getDrawable(R.drawable.ic_launcher);
		titleView = new TextView(context);
	}
	
	public boolean readRecord(Serializable record){
		
		if(record == null)return false;
		
		bean = (SpeciesBean) record;
		
		mSetTitle();
		
		
		return true;
	}
	
	private boolean mSetTitle(){
		
		if(bean == null)return false;

		Drawable icon = pi.getImage("pk", bean.getId());
		if(icon == null) icon = icon_default;
		
		icon = pi.resizeIcon(icon);
		
		titleView.setText(bean.getName());
		
		titleView.setTextSize(icon.getIntrinsicHeight() * textSize);
		
		titleView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		
		setCustomTitle(titleView);
		
		return true;
		
	}

}
