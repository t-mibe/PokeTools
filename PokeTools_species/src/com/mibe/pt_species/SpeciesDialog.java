package com.mibe.pt_species;

import java.io.Serializable;

import com.mibe.lib_image.PokeImage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

public class SpeciesDialog extends AlertDialog {
	
	private static final float textSize = 0.7f;
	
	private Context context = null;
	
	private SpeciesBean bean = null;
	private PokeImage pi = null;
	
	private Drawable icon_default = null;
	
	private TextView titleView = null;
	
	private String sName = null;

	protected SpeciesDialog(Context context, String homeDir) {
		super(context);		
		mSetup(context, homeDir, null, null);
	}
	
	protected SpeciesDialog(Context context, String homeDir, Serializable record) {
		super(context);
		mSetup(context, homeDir, null, record);
	}
	
	private void mSetup(Context context, String homeDir, String sName, Serializable record){
		
		this.context = context;
		pi = new PokeImage(context, homeDir);
		icon_default = context.getResources().getDrawable(R.drawable.ic_launcher);
		titleView = new TextView(context);
		
		if(sName != null)this.sName = sName;
		if(record != null)this.bean = (SpeciesBean) record;
	}
	
	// ダイアログの内容を生成する
	public void create(){
		
		if(bean == null){
			
			if(sName == null){
				
				// 名前もレコードもない事をエラーとして出力
			}
			
			// 種族名から種族データのBeanを取得する（失敗したらエラー）
		}
		
		// ダイアログのタイトルを設定するする
		mSetTitle();
		
		// ダイアログのビューを設定する
		mSetView();
	}
	
	public boolean readRecord(Serializable record){
		
		if(record == null)return false;
		
		this.bean = (SpeciesBean) record;
		
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
	
	/**
	 * ダイアログのビューを設定する
	 * 内容は図鑑番号，種族値，タイプ，特性，輝石，♂率，体重
	 * @return
	 */
	private boolean mSetView(){
		
		if(bean == null)return false;
		
		View view = createView();
		
		setView(view);
		
		return true;
	}
	
	private View createView(){
		
		Activity activity = (SpeciesViewActivity)context;
		
		return activity.getLayoutInflater().inflate(R.layout.dialog_view, null);
	}

}
