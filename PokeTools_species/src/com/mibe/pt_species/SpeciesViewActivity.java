package com.mibe.pt_species;

import java.io.Serializable;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.mibe.lib_activity.ViewCsvActivity;
import com.mibe.lib_image.PokeImage;

/**
 * 種族データ一覧のActivityと
 * 種族データ閲覧ダイアログの定義
 * @author mibe
 *
 */
public class SpeciesViewActivity extends ViewCsvActivity {
	
	private static final float textSize = 0.7f;

	// 表示するファイルのローカルパスを取得する
	@Override
	public String getLocalPath(){
		return getString(R.string.species_localPath);
	}

	// レコードを対応したJavaBeanに変換する
	@Override
	public Serializable makeBean(String[] record){
		return new SpeciesBean().create(record);
	}
	
	/**
	 * アイテムが指定された時の処理
	 * record: 指定されたアイテムのレコード
	 */
	@Override
	public void onListItemClick(Serializable record){
		
		SpeciesBean bean = (SpeciesBean) record;
		
		String id = bean.getId();
		
		super.onListItemClick(record);
	}

	// ダイアログのタイトルを作成する
	@Override
	public View getItemDialogTitle(Serializable record){
		
		PokeImage pi = new PokeImage(this, getHomeDir());

		SpeciesBean bean = (SpeciesBean) record;

		String name = bean.getName();
		String id = bean.getId();

		Drawable icon = pi.getImage("pk", id);
		if(icon == null) icon = getDefaultIcon();
		
		icon = pi.resizeIcon(icon);

		TextView textView = new TextView(this);
		textView.setText(name);

		textView.setTextSize(icon.getIntrinsicHeight() * textSize);

		textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

		return textView;
	}
}
