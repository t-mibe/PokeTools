package com.mibe.pt_species;

import com.mibe.pt_library.ViewCsvActivity;

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
}
