package com.mibe.lib_image;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

public class PokeImage {
	
	public static final int iconSize = 60;

	private Context context;
	private String homeDir;

	public PokeImage(Context context, String homeDir){

		this.context = context;
		this.homeDir = homeDir;
	}

	public Drawable getImage(String group, String id){

		if(homeDir == null || homeDir.equals(""))return null;

		String filePath = homeDir.concat("image/").concat(group).concat("/").concat(id).concat(".gif");

		if(!new File(filePath).isFile()) return null;

		ImageView imageView = new ImageView(context);
		imageView.setImageURI(Uri.parse("file://".concat(filePath)));
		
		return imageView.getDrawable();
	}
	
	public Drawable resizeIcon(Drawable src){

		Bitmap bitmap = ((BitmapDrawable) src).getBitmap();
		
		return new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true));
	}
}
