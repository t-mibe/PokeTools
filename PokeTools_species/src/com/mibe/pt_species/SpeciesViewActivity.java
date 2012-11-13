package com.mibe.pt_species;

import java.io.Serializable;

import android.view.View;
import android.widget.TextView;

import com.mibe.lib_activity.ViewCsvActivity;

/**
 * 種族データ一覧のActivityと
 * 種族データ閲覧ダイアログの定義
 * @author mibe
 *
 */
public class SpeciesViewActivity extends ViewCsvActivity {

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

	// ダイアログのタイトルを作成する
	@Override
	public View getItemDialogTitle(Serializable record){
		
		SpeciesBean bean = (SpeciesBean) record;
		
		String name = bean.getName();
		String id = bean.getId();

		TextView textView = new TextView(this);
		textView.setText(name.concat("\n").concat(id));

		return textView;
	}
}
