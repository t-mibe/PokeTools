package com.mibe.pt_species;

import java.io.Serializable;

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
	
	/**
	 * アイテムが指定された時の処理（デフォルトの処理はスキップ）
	 * record: 指定されたアイテムのレコード
	 */
	@Override
	public void onListItemClick(Serializable record){
		
		SpeciesBean bean = (SpeciesBean) record;
		
		SpeciesDialog dialog = new SpeciesDialog(this, getHomeDir(), bean);
		
		dialog.create();
		dialog.show();
	}
}