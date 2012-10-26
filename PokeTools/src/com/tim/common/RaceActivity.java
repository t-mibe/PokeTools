package com.tim.common;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;

import com.tim.csv.CSVViewActivity;
import com.tim.data.Race;
import com.tim.dialog.RaceViewDialog;
import com.tim.other.MyParse;
import com.tim.poketools.R;

public class RaceActivity extends CSVViewActivity {
	
	private RaceViewDialog rDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// 種族ダイアログクラスを生成する
		rDialog = new RaceViewDialog(this);

		// タイトルを設定する
		setTitle("種族");
		
		// レコードの要素数を設定する
		record_length = Race.RECORD_SIZE;
		
		// 種族CSVファイルを展開し表示する
		init(R.string.path_race);
	}

	@Override
	public String toShortRecord(String longRecord) {
		
		// レコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(longRecord);
		
		// レコードの要素数が不正ならダミーデータを返す
		if(list_record.size() != Race.RECORD_SIZE)return "ダミー";
		
		return list_record.get(Race.RECORD_ID_NAME);
	}

	@Override
	public String getItemViewTitle(int position) {
		
		// レコード文を取得してダイアログのタイトル文を作成する
		return rDialog.getDialogTitleByRecord(list_long.get(position));
	}

	@Override
	public View getItemViewBody(int position) {

		// レコード文を取得してダイアログのViewを作成する
		return rDialog.getDialogViewByRecord(list_long.get(position));
	}

}
